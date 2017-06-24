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

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collection;

import codeu.chat.common.BasicView;
import codeu.chat.common.User;
import codeu.chat.common.ServerInfo;
import codeu.chat.client.core.View;
import codeu.chat.util.Uuid;
import codeu.chat.util.connections.ConnectionSource;

public final class Context {

  private final View view;
  private final Controller controller;

  public Context(ConnectionSource source) {
    this.view = new View(source);
    this.controller = new Controller(source);
  }

  public UserContext create(String name) {
    final User user = controller.newUser(name);
    return user == null ?
        null :
        new UserContext(user, view, controller);
  }

  public ServerInfo getInfo() {
    return view.getInfo();
  }

  public Iterable<UserContext> allUsers() {
    final Collection<UserContext> users = new ArrayList<>();
    for (final User user : view.getUsers()) {
      users.add(new UserContext(user, view, controller));
    }
    return users;
  }

  public void statusUpdateUsers(String username){
      //set current time when status is called here
      Time recentUpdate = Time.now();
      int messageCounter = 0;
      int convoCounter = 0;
      Collection allConversations;
      Collection allUsers;
      Collection allMessages;

      //Need to ask Chris about this??
      //loop through users and find the one we want based on name
      //nope this isn't right, I am the user I want to calling this on
      //halp confused
      User user;
      allUsers = view.getUsers()
      for (User guess : allUsers){
        if(guess.name == username){
          user = guess;
        }
      }
      long previousUpdate = user.getLastUpdateUsers[name];
      allConversations = view.getConversations();
      for(Conversation convo : allConversations){
        if(convo.owner == username){
          if(previousUpdate.compareTo(mess.creation) < 0 && mess.creation.compareTo(recentUpdate) < 0){
            convoCounter += 1;
          }
        }
      }
      //how to make sure its only counting the number of conversations
      //they have added messages to.
      allMessages = view.getMessages();
      for(Message mess : allMessages){
        if(mess.id == user.id){
          //a value less than 0 if this Time is before the Time argument;
          // and a value greater than 0 if this Time is after the Time argument.
          if(previousUpdate.compareTo(mess.creation) < 0 && mess.creation.compareTo(recentUpdate) < 0){
            messageCounter += 1;
          }
        }
      }
      System.out.println("User Status Update: The number of conversations they have added messages to is " + messageCounter);
      System.out.println("User Status Update: The number of new conversations they have created is " + convoCounter);
      //set the last time updated to the current called time
      user.setlastUpdateUsers(user.id, recentUpdate);
    }

    public void statusUpdateConvos(String name){
      Time recentUpdate = Time.now();
      int messageCounter = 0;
      int previousUpdate = user.lastUpdateConvos[name];
      Collection allConversation;
      Collection allMessages;
      Conversation interestedConvo;

      allConversation = view.getConversations();
      allMessages = view.getMessages();
      for(Conversation convo : allConversations){
        if(convo.title == name){
            interestedConvo = convo;
        }
      }
      ///this need sto be updated
      allMessages = view.getMessages();
      for(Message mess : allMessages){
        if(mess.id == user.id){
          if(previousUpdate.compareTo(mess.creation) < 0 && mess.creation.compareTo(recentUpdate) < 0){
            messageCounter += 1;
          }
        }
      }
      //now that I have the right conversation how to I connect the
      //message to the conversation?? Conversation doesn't hold messages
      //messages don't have the associated conversation
      //halp
      System.out.println("Conversation Status Update: The number of new messages is " + messageCounter);
      user.lastUpdateConvos.put(interestConvo.id,recentUpdate);
    }

    public void interestedUser(String name){
      //I wrote these in user idk if that was the correct move??
      Collection allUsers = view.getUsers()
      for(User guess : allUsers){
        if(guess.name == name){
          user = guess
        }
      }
      user.interestedUser(user.id);
    }

    public void uninterestedUser(String name){
      Collection allUsers = view.getUsers()
      for(User guess : allUsers){
        if(guess.name == name){
          user = guess
        }
      }
      user.uninterestedUser(user.id);
    }

    public void interestedConvo(String name){
      Collection allConversations = view.getConversations();
      Conversation convo;
      for(Conversation guess : allConversations){
        if (guess.title == name){
          convo = guess;
        }
      }
      user.interestedConvo(convo.id);
    }
    public void uninterestedConvo(String name){
      Collection allConversations = view.getConversations();
      Conversation convo;
      for(Conversation guess : allConversations){
        if (guess.title == name){
          convo = guess;
        }
      }
      user.uninterestedConvo(convo.id);
    }

  }
}
