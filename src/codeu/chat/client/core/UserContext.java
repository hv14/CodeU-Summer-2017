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

  public HashSet<Uuid> listInterestedUsers() {
    return user.interestedUsers;
  }

  public HashSet<Uuid> listInterestedConvos() {
    return user.interestedConvos;
  }

  public Uuid getInterestedUser(Uuid otherUserId) {

  }

  public boolean checkIfInterestedUser(Uuid otherUserId) {
    return user.interestedUsers.contains(otherUserId);
  }


  public HashSet<String> getUpdatedConvos(){
    //set current time when status is called here
    Time recentUpdate = Time.now();

    HashSet<String> updatedConvos = new HashSet<>();

    Time previousUpdate = user.getLastUpdateConvos();

    Iterator<ConversationHeader> it = view.getConversations().iterator();
    while (it.hasNext()) {
      ConversationHeader curr = it.next();
      if (curr.owner.equals(user.id)) {
        if (curr.creation.inRange(previousUpdate, recentUpdate)) {
          updatedConvos.add(curr.title);
        }
      }
    }

    Iterator<Message> itMsges = view.getMessages(Arrays.asList(user.id)).iterator();
    while (itMsges.hasNext()) {
      Message curr = itMsges.next();
      if (curr.creation.inRange(previousUpdate, recentUpdate)) {
        ConversationHeader foundConvo = findConversation(curr.convoId);
        if (foundConvo != null) {
          updatedConvos.add(foundConvo.title);
        }
      }
    }
    user.lastUpdateConvos = recentUpdate;

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

  public ConversationContext start(String name) {
    final ConversationHeader conversation = controller.newConversation(name, user.id);
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

}
