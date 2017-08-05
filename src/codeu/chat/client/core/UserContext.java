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

package codeu.chat.client.core;

import codeu.chat.util.AccessLevel;

import java.lang.reflect.Array;
import java.util.*;

import codeu.chat.common.*;
import codeu.chat.util.Time;
import codeu.chat.util.Uuid;

public final class UserContext {

  public final User user;
  public final BasicView view;
  private final BasicController controller;

  public UserContext(User user, BasicView view, BasicController controller) {
    this.user = user;
    this.view = view;
    this.controller = controller;
  }

  public void addInterestedUser(Uuid otherUserId) {
    user.interestedUsers.add(otherUserId);
  }

  public void addInterestedConvo(Uuid convoId) {
    user.interestedConvos.add(convoId);
  }

  public void delInterestedUser(Uuid otherUserId) {
    user.interestedUsers.remove(otherUserId);
  }

  public void delInterestedConvo(Uuid convoId) {
    user.interestedConvos.remove(convoId);
  }

  public Set<Uuid> listInterestedUsers() {
    return user.interestedUsers;
  }

  public Set<Uuid> listInterestedConvos() {
    return user.interestedConvos;
  }

  public boolean checkIfInterestedUser(Uuid otherUserId) {
    return user.interestedUsers.contains(otherUserId);
  }

  public boolean checkIfInterestedConvo(Uuid convoId) {
    return user.interestedConvos.contains(convoId);
  }


  public HashSet<String> getUpdatedConvosForUser(Uuid otherUserId){
    //set current time when status is called here
    Time recentUpdate = Time.now();

    HashSet<String> updatedConvos = new HashSet<>();

    Time previousUpdate = user.getLastUpdateUsers();

    Iterator<ConversationHeader> it = view.getConversations().iterator();
    while (it.hasNext()) {
      ConversationHeader curr = it.next();
      if (curr.owner.equals(otherUserId)) {
        if (curr.creation.inRange(previousUpdate, recentUpdate)) {
          updatedConvos.add(curr.title);
        }
      }
    }

    Iterator<Message> itMsges = view.getMessages(Arrays.asList(otherUserId)).iterator();
    while (itMsges.hasNext()) {
      Message curr = itMsges.next();
      if (curr.creation.inRange(previousUpdate, recentUpdate)) {
        ConversationHeader foundConvo = findConversation(curr.convoId);
        if (foundConvo != null) {
          updatedConvos.add(foundConvo.title);
        }
      }
    }
    user.setLastUpdateUsers(recentUpdate);

    return updatedConvos;
  }

  public ConversationHeader findConversation(Uuid id) {
    try {
      Iterator<ConversationHeader> it = view.getConversations().iterator();
      while (it.hasNext()) {
        ConversationHeader curr = it.next();
        if (curr.id.equals(id)) {
          return curr;
        }
      }
    }
    catch (Exception ex) {
      System.out.println(ex);
    }

    return null;
  }

  public ConversationContext start(String name, String defaultAcessLevel, HashMap<Uuid, AccessLevel> usersInConvo) {
    final ConversationHeader conversation = controller.newConversation(name, user.id, defaultAcessLevel, usersInConvo);

    return conversation == null ?
        null :
        new ConversationContext(user, conversation, view, controller);
  }


  public Iterable<ConversationContext> conversations() {

    // Use all the ids to get all the conversations and convert them to
    // Conversation Contexts.
    final Collection<ConversationContext> all = new ArrayList<>();
    for (final ConversationHeader conversation : view.getConversations()) {
      all.add(new ConversationContext(user, conversation, view, controller));
    }

    return all;
  }

  public ArrayList<Message> getUpdatedMessages(ConversationHeader convo) {
    Time recentUpdate = Time.now();
    Time previousUpdate = user.getLastUpdateConvos();
    ArrayList<Message> updatedMessages = new ArrayList<>();
    ConversationContext convoContext = new ConversationContext(user, convo, view, controller);
      for (MessageContext message = convoContext.firstMessage();
           message != null;
           message = message.next()) {
        if (message.message.creation.inRange(previousUpdate, recentUpdate)) {
          updatedMessages.add(message.message);
        }
      }


    user.setLastUpdateConvos(recentUpdate);

    return updatedMessages;
  }



}
