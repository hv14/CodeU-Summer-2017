// Copyright 2017 Google Inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package codeu.chat.server;

import java.io.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Queue;

import codeu.chat.common.*;
import codeu.chat.util.Json;
import codeu.chat.util.Time;
import codeu.chat.util.Uuid;
import codeu.chat.util.store.Store;
import codeu.chat.util.store.StoreAccessor;
import com.google.gson.Gson;

public final class Model {


  public ArrayList<User> currentUsers = new ArrayList<>();
  public ArrayList<Message> currentMessages = new ArrayList<>();
  public ArrayList<ConversationHeader> currentConversations = new ArrayList<>();


  //Used to load the saved users and conversations
  public void refreshData() {
    Gson gson = new Gson();
    Json json = new Json();
    try {
      String jsonUsers = json.read("savedUsers.txt");
      UserCollection pastUsers = gson.fromJson(jsonUsers, UserCollection.class);

      String jsonConversations = json.read("savedConvos.txt");
      ConversationCollection pastConovs = gson.fromJson(jsonConversations, ConversationCollection.class);

      for (User user: pastUsers.users) {
        add(user);
      }

      for (ConversationHeader convo: pastConovs.conversations) {
        add(convo);
      }

    } catch (Exception ex) {
      System.out.println(ex);
    }
  }

  private static final Comparator<Uuid> UUID_COMPARE = new Comparator<Uuid>() {


    @Override
    public int compare(Uuid a, Uuid b) {

      if (a == b) { return 0; }

      if (a == null && b != null) { return -1; }

      if (a != null && b == null) { return 1; }

      final int order = Integer.compare(a.id(), b.id());
      return order == 0 ? compare(a.root(), b.root()) : order;
    }
  };

  private static final Comparator<Time> TIME_COMPARE = new Comparator<Time>() {
    @Override
    public int compare(Time a, Time b) {
      return a.compareTo(b);
    }
  };

  private static final Comparator<String> STRING_COMPARE = String.CASE_INSENSITIVE_ORDER;

  private final Store<Uuid, User> userById = new Store<>(UUID_COMPARE);
  private final Store<Time, User> userByTime = new Store<>(TIME_COMPARE);
  private final Store<String, User> userByText = new Store<>(STRING_COMPARE);

  private final Store<Uuid, ConversationHeader> conversationById = new Store<>(UUID_COMPARE);
  private final Store<Time, ConversationHeader> conversationByTime = new Store<>(TIME_COMPARE);
  private final Store<String, ConversationHeader> conversationByText = new Store<>(STRING_COMPARE);

  private final Store<Uuid, ConversationPayload> conversationPayloadById = new Store<>(UUID_COMPARE);

  private final Store<Uuid, Message> messageById = new Store<>(UUID_COMPARE);
  private final Store<Time, Message> messageByTime = new Store<>(TIME_COMPARE);
  private final Store<String, Message> messageByText = new Store<>(STRING_COMPARE);

  public void add(User user) {
    userById.insert(user.id, user);
    userByTime.insert(user.creation, user);
    userByText.insert(user.name, user);

    //add the user to an array that is used to save to a text file
    currentUsers.add(user);
  }

  public StoreAccessor<Uuid, User> userById() {
    return userById;
  }

  public StoreAccessor<Time, User> userByTime() {
    return userByTime;
  }

  public StoreAccessor<String, User> userByText() {
    return userByText;
  }

  public void add(ConversationHeader conversation) {
    conversationById.insert(conversation.id, conversation);
    conversationByTime.insert(conversation.creation, conversation);
    conversationByText.insert(conversation.title, conversation);
    conversationPayloadById.insert(conversation.id, new ConversationPayload(conversation.id));

    currentConversations.add(conversation);
  }

  public StoreAccessor<Uuid, ConversationHeader> conversationById() {
    return conversationById;
  }

  public StoreAccessor<Time, ConversationHeader> conversationByTime() {
    return conversationByTime;
  }

  public StoreAccessor<String, ConversationHeader> conversationByText() {
    return conversationByText;
  }

  public StoreAccessor<Uuid, ConversationPayload> conversationPayloadById() {
    return conversationPayloadById;
  }

  public void add(Message message) {
    messageById.insert(message.id, message);
    messageByTime.insert(message.creation, message);
    messageByText.insert(message.content, message);

    //add the message to an array that is used to save to a text file
    currentMessages.add(message);
  }

  public StoreAccessor<Uuid, Message> messageById() {
    return messageById;
  }

  public StoreAccessor<Time, Message> messageByTime() {
    return messageByTime;
  }

  public StoreAccessor<String, Message> messageByText() {
    return messageByText;
  }
}
