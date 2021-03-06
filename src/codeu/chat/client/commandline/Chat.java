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

package codeu.chat.client.commandline;


import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Stack;
import java.util.*;

import java.io.IOException;

import codeu.chat.common.ConversationHeader;
import codeu.chat.common.Message;
import codeu.chat.common.ServerInfo;
import codeu.chat.client.core.Context;
import codeu.chat.client.core.ConversationContext;
import codeu.chat.client.core.MessageContext;
import codeu.chat.client.core.UserContext;
import codeu.chat.common.User;
import codeu.chat.common.ServerInfo;
import codeu.chat.common.User;
import codeu.chat.util.AccessLevel;
import codeu.chat.util.Tokenizer;
import codeu.chat.util.Uuid;

public final class Chat {

  // PANELS
  //
  // We are going to use a stack of panels to track where in the application
  // we are. The command will always be routed to the panel at the top of the
  // stack. When a command wants to go to another panel, it will add a new
  // panel to the top of the stack. When a command wants to go to the previous
  // panel all it needs to do is pop the top panel.
  private final Stack<Panel> panels = new Stack<>();

  public Chat(Context context) {
    this.panels.push(createRootPanel(context));
  }

  // HANDLE COMMAND
  //
  // Take a single line of input and parse a command from it. If the system
  // is willing to take another command, the function will return true. If
  // the system wants to exit, the function will return false.
  //
  public boolean handleCommand(String line) throws IOException {

   final List<String> args = new ArrayList<>();
   final Tokenizer tokenizer = new Tokenizer(line);
   for (String token = tokenizer.next(); token != null; token = tokenizer.next()) {
     args.add(token);
   }
   final String command = args.get(0);
   args.remove(0);

    // Because "exit" and "back" are applicable to every panel, handle
    // those commands here to avoid having to implement them for each
    // panel.

    if ("exit".equals(command)) {
      // The user does not want to process any more commands
      return false;
    }

    // Do not allow the root panel to be removed.
    if ("back".equals(command) && panels.size() > 1) {
      panels.pop();
      return true;
    }

    if (panels.peek().handleCommand(command, args)) {
      // the command was handled
      return true;
    }

    // If we get to here it means that the command was not correctly handled
    // so we should let the user know. Still return true as we want to continue
    // processing future commands.
    System.out.println("ERROR: Unsupported command");
    return true;
  }

  // CREATE ROOT PANEL
  //
  // Create a panel for the root of the application. Root in this context means
  // the first panel and the only panel that should always be at the bottom of
  // the panels stack.
  //
  // The root panel is for commands that require no specific contextual information.
  // This is before a user has signed in. Most commands handled by the root panel
  // will be user selection focused.
  //
  private Panel createRootPanel(final Context context) {

    final Panel panel = new Panel();

    // HELP
    //
    // Add a command to print a list of all commands and their description when
    // the user for "help" while on the root panel.
    //
    panel.register("help", new Panel.Command() {
      @Override
      public void invoke(List<String> args) {
        System.out.println("ROOT MODE");
        System.out.println("  info");
        System.out.println("     Output Server Version.");
        System.out.println("  u-list");
        System.out.println("    List all users.");
        System.out.println("  u-add <name>");
        System.out.println("    Add a new user with the given name.");
        System.out.println("  u-sign-in <name>");
        System.out.println("    Sign in as the user with the given name.");
        System.out.println("  info");
        System.out.println("    Display all the info about the current system");
        System.out.println("  exit");
        System.out.println("    Exit the program.");
      }
    });

    // U-LIST (user list)
    //
    // Add a command to print all users registered on the server when the user
    // enters "u-list" while on the root panel.
    //
    panel.register("u-list", new Panel.Command() {
      @Override
      public void invoke(List<String> args) {
        for (final UserContext user : context.allUsers()) {
          System.out.format(
              "USER %s (UUID:%s)\n",
              user.user.name,
              user.user.id);
        }
      }
    });

    // U-ADD (add user)
    //
    // Add a command to add and sign-in as a new user when the user enters
    // "u-add" while on the root panel.
    //
    panel.register("u-add", new Panel.Command() {
      @Override
      public void invoke(List<String> args) {
        final String name = args.get(0);
          args.remove(0);
        if (name.length() > 0) {
          if (context.create(name) == null) {
            System.out.println("ERROR: Failed to create new user");
          }
        } else {
          System.out.println("ERROR: Missing <username>");
        }
      }
    });

    panel.register("info", new Panel.Command() {
      @Override
      public void invoke(List<String> args) {
        final ServerInfo info = context.getInfo();
        if (info == null) {
         System.out.println("Error: Server did not send valid object.");
        } else {
          System.out.println("Server Version: " + info.version + " start time: " + info.startTime);
         // Print the server info to the user in a pretty way
        }
      }
    });

/*//status update for users
    panel.register("update-status-user <username>", new Panel.Command() {
    //Check to see what conversation this user has started and how many messages they've sent
    //need to loop through the log and count new convos and new messages
    //messages specifically from that user
      public void invoke(Scanner args) {
        final String name = args.hasNext() ? args.nextLine().trim() : "";
        if (name.length() > 0) {
          if (context.create(name) == null) {
            System.out.println("ERROR: Failed to update user status");
          } else {
            context.statusUpdateUsers(name);
          }
        } else {
          System.out.println("ERROR: Missing <username>");
        }
      }

    });


//Status update for conversations
    panel.register("update-status-conversation <conversation name>", new Panel.Command() {
    //Check to see how many messages have been added to this conversation
    public void invoke(Scanner args) {
      final String name = args.hasNext() ? args.nextLine().trim() : "";
      if (name.length() > 0) {
          context.updateStatusConvo(name);
      } else {
          System.out.println("ERROR: Missing <conversation name>");
        }
      }
    });

    panel.register("interested-user <user>", new Panel.Command() {
    //Change you interest about this user to a positive value
      public void invoke(Scanner args) {
        final String name = args.hasNext() ? args.nextLine().trim() : "";
        if (name.length() > 0) {
          if (context.create(name) == null) {
            System.out.println("ERROR: Failed make interested");
          }
          else{
            context.interestedUser(name);
          }
        } else {
          System.out.println("ERROR: Missing <username>");
        }
      }
    });

    panel.register("uninterested-user <user>", new Panel.Command() {
    // Change you interest about this user to a negative value
        final String name = args.hasNext() ? args.nextLine().trim() : "";
        if (name.length() > 0) {
          if (context.create(name) == null) {
            System.out.println("ERROR: Failed make uninterested");
          }
          else{
            context.uninterestedUser(name);
          }
        } else {
          System.out.println("ERROR: Missing <username>");
        }
      }
    });

    panel.register("interested-conversation <conversation>", new Panel.Command() {
    //Change you interest about this conversation to a positive value
        final String name = args.hasNext() ? args.nextLine().trim() : "";
        if (name.length() > 0) {
          if (context.create(name) == null) {
            System.out.println("ERROR: Failed make interested");
          }
          else{
            context.interestedConvo(title);
          }
        } else {
          System.out.println("ERROR: Missing <conversation>");
        }
      }
    });

    panel.register("uninterested-conversation <conversation>", new Panel.Command() {
    // Change you interest about this conversation to a negative value
        final String name = args.hasNext() ? args.nextLine().trim() : "";
        if (name.length() > 0) {
          if (context.create(name) == null) {
            System.out.println("ERROR: Failed make uninterested");
          }
          else{
            context.uninterestedConvo(title);
          }
        } else {
          System.out.println("ERROR: Missing <conversation>");
        }
      }
    }); */

    // U-SIGN-IN (sign in user)
    //
    // Add a command to sign-in as a user when the user enters "u-sign-in"
    // while on the root panel.
    //
    panel.register("u-sign-in", new Panel.Command() {
      @Override
      public void invoke(List<String> args) {
        final String name = args.get(0);
          args.remove(0);
        if (name.length() > 0) {
          final UserContext user = findUser(name);
          if (user == null) {
            System.out.format("ERROR: Failed to sign in as '%s'\n", name);
          } else {
            panels.push(createUserPanel(user));
          }
        } else {
          System.out.println("ERROR: Missing <username>");
        }
      }

      // Find the first user with the given name and return a user context
      // for that user. If no user is found, the function will return null.
      private UserContext findUser(String name) {
        for (final UserContext user : context.allUsers()) {
          if (user.user.name.equals(name)) {
            return user;
          }
        }
        return null;
      }
    });

    // info
    //
    // Display the start time of the server when the user types
    // "info" while on the root panel.
    //
    panel.register("u-info", new Panel.Command() {
      @Override
      public void invoke(List<String> args) {
        final ServerInfo info = context.getInfo();
        if (info == null) {
          System.out.println("No info was found");
        }
        else {
          System.out.println("time: " + context.getInfo().startTime);
        }
      }

      // Find the first user with the given name and return a user context
      // for that user. If no user is found, the function will return null.
      private UserContext findUser(String name) {
        for (final UserContext user : context.allUsers()) {
          if (user.user.name.equals(name)) {
            return user;
          }
        }
        return null;
      }
    });


    // Now that the panel has all its commands registered, return the panel
    // so that it can be used.
    return panel;
  }

  private Panel createUserPanel(final UserContext user) {

    final Panel panel = new Panel();

    // HELP
    //
    // Add a command that will print a list of all commands and their
    // descriptions when the user enters "help" while on the user panel.
    //
    panel.register("help", new Panel.Command() {
      @Override
      public void invoke(List<String> args) {
        System.out.println("USER MODE");
        System.out.println("  c-list");
        System.out.println("    List all conversations that the current user can interact with.");
        System.out.println("  c-add <title> <default access level>");
        System.out.println("    Add a new conversation with the given title and join it as the current user. Also specify the default access level of other users who join.");
        System.out.println("  c-join <title>");
        System.out.println("    Join the conversation as the current user.");
        System.out.println("  c-change-admin <Username>");
        System.out.println(     "Changes the status of a user that is already a member of the conversation to an admin");
        System.out.println("  c-list-interested-users");
        System.out.println("    List all of the users you are following.");
        System.out.println("  c-list-interested-convos");
        System.out.println("    List all of the convos you are following.");
        System.out.println("  c-add-interested-user <username>");
        System.out.println("    Add a user to follow.");
        System.out.println("  c-add-interested-convo <convo name>");
        System.out.println("    Add a convo to follow.");
        System.out.println("  c-status-update-user <username>");
        System.out.println("    Get a status update of the user you are following.");
        System.out.println("  c-status-update-conversation <convo name>");
        System.out.println("    Get a status update of the conversation you are following.");
        System.out.println("  info");
        System.out.println("    Display all info for the current user");
        System.out.println("  back");
        System.out.println("    Go back to ROOT MODE.");
        System.out.println("  exit");
        System.out.println("    Exit the program.");
      }
    });

    // C-LIST (list conversations)
    //
    // Add a command that will print all conversations when the user enters
    // "c-list" while on the user panel.
    //
    panel.register("c-list", new Panel.Command() {
      @Override
      public void invoke(List<String> args) {
        for (final ConversationContext conversation : user.conversations()) {
          System.out.format(
              "CONVERSATION %s (UUID:%s)\n",
              conversation.conversation.title,
              conversation.conversation.id);
        }
      }
    });

    // C-ADD (add conversation)
    //
    // Add a command that will create and join a new conversation when the user
    // enters "c-add" while on the user panel.
    //
    panel.register("c-add", new Panel.Command() {
      @Override
      public void invoke(Scanner args) {
        final String name = args.get(0);
        args.remove(0);
        String defaultAccessLevel = args.get(0);
        if (name.length() > 0 && defaultAccessLevel.length() > 0) {
          defaultAccessLevel = defaultAccessLevel.toLowerCase();
          if (defaultAccessLevel.equals("owner") ||
                  defaultAccessLevel.equals("member")) {
            HashMap<Uuid, AccessLevel> usersInConvo = new HashMap<>();
            usersInConvo.put(user.user.id, AccessLevel.creator);
            final ConversationContext conversation = user.start(name, defaultAccessLevel, usersInConvo);
            //conversation.addDefaultAccessLevel(AccessLevel.valueOf(defaultAccessLevel));
            //System.out.println(conversation.conversation.defaultAccessLevel);
            if (conversation == null) {
              System.out.println("ERROR: Failed to create new conversation");
            }
            else {
              panels.push(createCreatorConversationPanel(conversation));
            }
          }
          else {
            System.out.println("ERROR: access level must be 'owner' or 'member'");
          }

        }
        else {
          System.out.println("ERROR: Missing <title> or <defaultAccessLevel>");
        }
      }
    });

    panel.register("c-list-interested-users", new Panel.Command() {
      @Override
      public void invoke(List<String> args) {
        for (Uuid userId : user.listInterestedUsers()) {
          System.out.println("USER ID: " + userId);
        }
      }
    });

    panel.register("c-list-interested-convos", new Panel.Command() {
      @Override
      public void invoke(List<String> args) {
        for (Uuid convoId : user.listInterestedConvos()) {
          System.out.println("CONVO ID: " + convoId);
        }
      }
    });


    panel.register("c-add-interested-user", new Panel.Command() {
      @Override
      public void invoke(List<String> args) {
        final String name = args.get(0);
          args.remove(0);
        if (name.length() > 0) {
          if (findOtherUser(name) != null) {
            User otherUser = findOtherUser(name);
            user.addInterestedUser(otherUser.id);
          }
          else {
            System.out.println("ERROR: could not find " + name);
          }
        } else {
          System.out.println("ERROR: Missing <username>");
        }
      }


      public User findOtherUser(String name) {
        try {
          Iterator<User> it = user.view.getUsers().iterator();
          while (it.hasNext()) {
            User curr = it.next();
            if (curr.name.equalsIgnoreCase(name)) {
              return curr;
            }
          }
        }
        catch (Exception e) {
          System.out.println(e);
        }

        return null;
      }

    });

    panel.register("c-del-interested-user", new Panel.Command() {
      @Override
      public void invoke(List<String> args) {
        final String name = args.get(0);
          args.remove(0);
        if (name.length() > 0) {
          if (findOtherUser(name) != null) {
            User otherUser = findOtherUser(name);
            user.delInterestedUser(otherUser.id);
          }
          else {
            System.out.println("ERROR: could not find " + name);
          }
        } else {
          System.out.println("ERROR: Missing <username>");
        }
      }

      public User findOtherUser(String name) {
        try {
          Iterator<User> it = user.view.getUsers().iterator();
          while (it.hasNext()) {
            User curr = it.next();
            if (curr.name.equalsIgnoreCase(name)) {
              return curr;
            }
          }
        }
        catch (Exception e) {
          System.out.println(e);
        }

        return null;
      }
    });

    panel.register("c-add-interested-convo", new Panel.Command() {
      @Override
      public void invoke(List<String> args) {
        final String name = args.get(0);
          args.remove(0);
        if (name.length() > 0) {
          if (findConversation(name) != null) {
            ConversationHeader convo = findConversation(name);
            user.addInterestedConvo(convo.id);
          }
          else {
            System.out.println("ERROR: could not find " + name);
          }
        } else {
          System.out.println("ERROR: Missing <conversation name>");
        }
      }

      public ConversationHeader findConversation(String convoName) {
        try {
          Iterator<ConversationHeader> it = user.view.getConversations().iterator();
          while (it.hasNext()) {
            ConversationHeader curr = it.next();
            if (curr.title.equalsIgnoreCase(convoName)) {
              return curr;
            }
          }
        }
        catch (Exception e) {
          System.out.println(e);
        }

        return null;
      }
    });

    panel.register("c-del-interested-convo", new Panel.Command() {
      @Override
      public void invoke(List<String> args) {
       final String name = args.get(0);
          args.remove(0);
        if (name.length() > 0) {
          if (findConversation(name) != null) {
            ConversationHeader convo = findConversation(name);
            user.delInterestedConvo(convo.id);
          } else {
            System.out.println("ERROR: could not find " + name);
          }
        } else {
          System.out.println("ERROR: Missing <conversation name>");
        }
      }

      public ConversationHeader findConversation(String convoName) {
        try {
          Iterator<ConversationHeader> it = user.view.getConversations().iterator();
          while (it.hasNext()) {
            ConversationHeader curr = it.next();
            if (curr.title.equalsIgnoreCase(convoName)) {
              return curr;
            }
          }
        }
        catch (Exception e) {
          System.out.println(e);
        }

        return null;
      }


      });

    panel.register("c-status-update-user", new Panel.Command() {
      @Override
      public void invoke(List<String> args) {
        final String name = args.get(0);
          args.remove(0);
        if (name.length() > 0) {
          User otherUser = findOtherUser(name);
          if (otherUser != null) {
            if (user.checkIfInterestedUser(otherUser.id)) {
              HashSet<String> updatedConvos = user.getUpdatedConvosForUser(otherUser.id);
              for (String convoTitle : updatedConvos) {
                System.out.println(convoTitle);
              }
            }
            else {
              System.out.println("ERROR: you are not following " + name);
            }
          }
        }
        else {
          System.out.println("ERROR: Missing <username>");
        }
      }

      public User findOtherUser(String name) {
        try {
          Iterator<User> it = user.view.getUsers().iterator();
          while (it.hasNext()) {
            User curr = it.next();
            if (curr.name.equalsIgnoreCase(name)) {
              return curr;
            }
          }
        }
        catch (Exception e) {
          System.out.println(e);
        }

        return null;
      }
    });

    panel.register("c-status-update-conversation", new Panel.Command() {
      @Override
      public void invoke(List<String> args) {
        final String name = args.get(0);
          args.remove(0);
        if (name.length() > 0) {
          ConversationHeader convo = findConversation(name);
          if (convo != null) {
            if (user.checkIfInterestedConvo(convo.id)) {
              ArrayList<Message> msgs = user.getUpdatedMessages(convo);
              System.out.println(msgs.size() + " message(s) have been added since last update");
            }
            else {
              System.out.println("ERROR: you are not following " + name);
            }
          }
        }
        else {
          System.out.println("ERROR: Missing <conversation>");
        }
      }

      public ConversationHeader findConversation(String convoName) {
        try {
          Iterator<ConversationHeader> it = user.view.getConversations().iterator();
          while (it.hasNext()) {
            ConversationHeader curr = it.next();
            if (curr.title.equalsIgnoreCase(convoName)) {
              return curr;
            }
          }
        }
        catch (Exception e) {
          System.out.println(e);
        }

        return null;
      }
    });



    // C-JOIN (join conversation)
    //
    // Add a command that will joing a conversation when the user enters
    // "c-join" while on the user panel.
    //
    panel.register("c-join", new Panel.Command() {
      @Override
      public void invoke(List<String> args) {
        final String name = args.get(0);
          args.remove(0);
        if (name.length() > 0) {
          final ConversationContext conversation = find(name);
          if (conversation == null) {
            System.out.format("ERROR: No conversation with name '%s'\n", name);
          }
          else {
           // String[] userAccess = new String[2];
            if (firstTimeUser(conversation)) {
              AccessLevel defaultAccessLevel = conversation.conversation.defaultAccessLevel;
              //System.out.println(defaultAccessLevel);
              conversation.changeUserAccessLevel(user.user, defaultAccessLevel.toString());
              //userAccess = access.split(":");
              //System.out.println("first time: " + userAccess[1]);


            }
            Map<Uuid, AccessLevel> userAccess = conversation.view.getUsersAccessInConvo(conversation.conversation.id);
            if (canUserJoin(conversation)) {
             // System.out.println(conversation.conversation.usersInConvo.get(user.user.id));
              if (userAccess.get(user.user.id) == AccessLevel.owner) {
                panels.push(createOwnerConversationPanel(conversation));
              } else if (userAccess.get(user.user.id) == AccessLevel.creator) {
                panels.push(createCreatorConversationPanel(conversation));
              }
              else if (userAccess.get(user.user.id) == AccessLevel.member){
                panels.push(createMemberConversationPanel(conversation));
              }
              else {
                panels.push(createMuteConversationPanel(conversation));
              }
            }
            else {
              System.out.println("ERROR: You are not allowed to join this conversation");
            }
          }
        }
        else {
          System.out.println("ERROR: Missing <title>");
        }
      }


      // Find the first conversation with the given name and return its context.
      // If no conversation has the given name, this will return null.
      private ConversationContext find(String title) {
        for (final ConversationContext conversation : user.conversations()) {
          if (title.equals(conversation.conversation.title)) {
            return conversation;
          }
        }
        return null;
      }

      private boolean canUserJoin(ConversationContext conversation) {
        Map<Uuid, AccessLevel> userAccess = conversation.view.getUsersAccessInConvo(conversation.conversation.id);
        return (userAccess.get(user.user.id) != AccessLevel.blocked) ? true : false;
      }

      private boolean firstTimeUser(ConversationContext conversation) {
        Map<Uuid, AccessLevel> userAccess = conversation.view.getUsersAccessInConvo(conversation.conversation.id);
        return  (userAccess.get(user.user.id) == null) ? true : false;
      }
    });


    // INFO
    //
    // Add a command that will print info about the current context when the
    // user enters "info" while on the user panel.
    //
    panel.register("info", new Panel.Command() {
      @Override
      public void invoke(List<String> args) {
        System.out.println("User Info:");
        System.out.format("  Name : %s\n", user.user.name);
        System.out.format("  Id   : UUID:%s\n", user.user.id);
      }
    });


    // Now that the panel has all its commands registered, return the panel
    // so that it can be used.
    return panel;
  }

  private Panel createCreatorConversationPanel(final ConversationContext conversation) {

    final Panel panel = new Panel();

    // HELP
    //
    // Add a command that will print all the commands and their descriptions
    // when the user enters "help" while on the conversation panel.
    //
    panel.register("help", new Panel.Command() {
      @Override
      public void invoke(Scanner args) {
        System.out.println("USER MODE");
        System.out.println("  m-list");
        System.out.println("    List all messages in the current conversation.");
        System.out.println("  m-add <message>");
        System.out.println("    Add a new message to the current conversation as the current user.");
        System.out.println("  m-mute <username>");
        System.out.println("    Prohibit a user from sending messages in the chat. They will only be able to read messages");
        System.out.println("  m-change-access <username> <new access level>");
        System.out.println("    Change the access level of a user. Choose between owner, member, or blocked");
        System.out.println("  info");
        System.out.println("    Display all info about the current conversation.");
        System.out.println("  back");
        System.out.println("    Go back to USER MODE.");
        System.out.println("  exit");
        System.out.println("    Exit the program.");
      }
    });

    // M-LIST (list messages)
    //
    // Add a command to print all messages in the current conversation when the
    // user enters "m-list" while on the conversation panel.
    //
    panel.register("m-list", new Panel.Command() {
      @Override
      public void invoke(Scanner List<String> args) {
        System.out.println("--- start of conversation ---");
        for (MessageContext message = conversation.firstMessage();
             message != null;
             message = message.next()) {
          System.out.println();
          System.out.format("USER : %s\n", message.message.author);
          System.out.format("SENT : %s\n", message.message.creation);
          System.out.println();
          System.out.println(message.message.content);
          System.out.println();
        }
        System.out.println("---  end of conversation  ---");
      }
    });

    // M-ADD (add message)
    //
    // Add a command to add a new message to the current conversation when the
    // user enters "m-add" while on the conversation panel.
    //
    panel.register("m-add", new Panel.Command() {
      @Override
      public void invoke(List<String> args) {
        final String message = args.get(0);
        args.remove(0);
        if (message.length() > 0) {
          conversation.add(message);
        } else {
          System.out.println("ERROR: Messages must contain text");
        }
      }
    });

    panel.register("m-change-access", new Panel.Command() {
      @Override
      public void invoke(List<String> args) {
        final String otherUsername = args.get(0);
        args.remove(0);
        final String newAccessLevel = args.get(0);
        args.remove(0);
        if (otherUsername.length() > 0 && newAccessLevel.length() > 0) {
          if (newAccessLevel.equalsIgnoreCase("owner") ||
                  newAccessLevel.equalsIgnoreCase("member") ||
                  newAccessLevel.equalsIgnoreCase("blocked")) {
            User otherUser = findOtherUser(otherUsername);
            if (otherUser != null) {
              conversation.changeUserAccessLevel(otherUser, newAccessLevel);
            }
            else {
              System.out.println("ERROR: could not find " + otherUsername);
            }
          }
          else {
            System.out.println("ERROR: access level must be 'owner', 'member', or 'blocked'");
          }
        }
        else {
          System.out.println("ERROR: Please make sure to specify a username and a new access level");
        }
      }

      public User findOtherUser(String name) {
        try {
          Iterator<User> it = conversation.view.getUsers().iterator();
          while (it.hasNext()) {
            User curr = it.next();
            if (curr.name.equalsIgnoreCase(name)) {
              return curr;
            }
          }
        }
        catch (Exception e) {
          System.out.println(e);
        }

        return null;
      }
    });


    panel.register("m-mute", new Panel.Command() {
      @Override
      public void invoke(List<String> args) {
        final String otherUsername = args.get(0);
        if (otherUsername.length() > 0) {
          User otherUser = findOtherUser(otherUsername);
          if (otherUser != null) {
            conversation.changeUserAccessLevel(otherUser, "mute");
          }
          else {
            System.out.println("ERROR: could not find " + otherUsername);
          }
        }
        else {
          System.out.println("ERROR: Please make sure to specify a username and a new access level");
        }
      }

      public User findOtherUser(String name) {
        try {
          Iterator<User> it = conversation.view.getUsers().iterator();
          while (it.hasNext()) {
            User curr = it.next();
            if (curr.name.equalsIgnoreCase(name)) {
              return curr;
            }
          }
        }
        catch (Exception e) {
          System.out.println(e);
        }

        return null;
      }
    });

    // INFO
    //
    // Add a command to print info about the current conversation when the user
    // enters "info" while on the conversation panel.
    //
    panel.register("info", new Panel.Command() {
      @Override
      public void invoke(List<String> args) {
        System.out.println("Conversation Info:");
        System.out.format("  Title : %s\n", conversation.conversation.title);
        System.out.format("  Id    : UUID:%s\n", conversation.conversation.id);
        System.out.format("  Owner : %s\n", conversation.conversation.owner);
      }
    });

    // Now that the panel has all its commands registered, return the panel
    // so that it can be used.
    return panel;



  }


  private Panel createOwnerConversationPanel(final ConversationContext conversation) {

    final Panel panel = new Panel();

    // HELP
    //
    // Add a command that will print all the commands and their descriptions
    // when the user enters "help" while on the conversation panel.
    //
    panel.register("help", new Panel.Command() {
      @Override
      public void invoke(List<String> args) {
        System.out.println("USER MODE");
        System.out.println("  m-list");
        System.out.println("    List all messages in the current conversation.");
        System.out.println("  m-add <message>");
        System.out.println("    Add a new message to the current conversation as the current user.");
        System.out.println("  m-change-member-access <username>");
        System.out.println("    Change the member access of a user. Doing so will remove them for the conversation");
        System.out.println("  info");
        System.out.println("    Display all info about the current conversation.");
        System.out.println("  back");
        System.out.println("    Go back to USER MODE.");
        System.out.println("  exit");
        System.out.println("    Exit the program.");
      }
    });

    // M-LIST (list messages)
    //
    // Add a command to print all messages in the current conversation when the
    // user enters "m-list" while on the conversation panel.
    //
    panel.register("m-list", new Panel.Command() {
      @Override
      public void invoke(List<String> args) {
        System.out.println("--- start of conversation ---");
        for (MessageContext message = conversation.firstMessage();
             message != null;
             message = message.next()) {
          System.out.println();
          System.out.format("USER : %s\n", message.message.author);
          System.out.format("SENT : %s\n", message.message.creation);
          System.out.println();
          System.out.println(message.message.content);
          System.out.println();
        }
        System.out.println("---  end of conversation  ---");
      }
    });

    // M-ADD (add message)
    //
    // Add a command to add a new message to the current conversation when the
    // user enters "m-add" while on the conversation panel.
    //
    panel.register("m-add", new Panel.Command() {
      @Override
      public void invoke(List<String> args) {
        final String message = args.get(0);
        if (message.length() > 0) {
          conversation.add(message);
        } else {
          System.out.println("ERROR: Messages must contain text");
        }
      }
    });


    panel.register("m-change-member-access", new Panel.Command() {
      @Override
      public void invoke(List<String> args) {
        final String username = args.get(0);
        if (username.length() > 0) {
          User user = findOtherUser(username);
          if (user != null) {
            Map<Uuid, AccessLevel> userAccess = conversation.view.getUsersAccessInConvo(conversation.conversation.id);
            if (userAccess.get(user.id) == AccessLevel.creator ||
                    userAccess.get(user.id) == AccessLevel.owner) {
              System.out.println("ERROR: As an owner you can only change access for members. Not creators or other owners");
            }
            else {
              conversation.changeUserAccessLevel(user, "blocked");
            }
          }
          else {
            System.out.println("ERROR: could not find " + username);
          }

        } else {
          System.out.println("ERROR: Please specify a username");
        }
      }

      public User findOtherUser(String name) {
        try {
          Iterator<User> it = conversation.view.getUsers().iterator();
          while (it.hasNext()) {
            User curr = it.next();
            if (curr.name.equalsIgnoreCase(name)) {
              return curr;
            }
          }
        }
        catch (Exception e) {
          System.out.println(e);
        }

        return null;
      }
    });

    // INFO
    //
    // Add a command to print info about the current conversation when the user
    // enters "info" while on the conversation panel.
    //
    panel.register("info", new Panel.Command() {
      @Override
      public void invoke(Scanner args) {
        System.out.println("Conversation Info:");
        System.out.format("  Title : %s\n", conversation.conversation.title);
        System.out.format("  Id    : UUID:%s\n", conversation.conversation.id);
        System.out.format("  Owner : %s\n", conversation.conversation.owner);
      }
    });

    // Now that the panel has all its commands registered, return the panel
    // so that it can be used.
    return panel;
  }


  private Panel createMemberConversationPanel(final ConversationContext conversation) {

    final Panel panel = new Panel();

    // HELP
    //
    // Add a command that will print all the commands and their descriptions
    // when the user enters "help" while on the conversation panel.
    //
    panel.register("help", new Panel.Command() {
      @Override
      public void invoke(List<String> args) {
        System.out.println("USER MODE");
        System.out.println("  m-list");
        System.out.println("    List all messages in the current conversation.");
        System.out.println("  m-like");
        System.out.println("    This will like the last message in the conversation.");
        System.out.println("  m-add <message>");
        System.out.println("    Add a new message to the current conversation as the current user.");
        System.out.println("  info");
        System.out.println("    Display all info about the current conversation.");
        System.out.println("  back");
        System.out.println("    Go back to USER MODE.");
        System.out.println("  exit");
        System.out.println("    Exit the program.");
      }
    });

    // M-LIST (list messages)
    //
    // Add a command to print all messages in the current conversation when the
    // user enters "m-list" while on the conversation panel.
    //
    panel.register("m-list", new Panel.Command() {
      @Override
      public void invoke(List<String> args) {
        System.out.println("--- start of conversation ---");
        for (MessageContext message = conversation.firstMessage();
                            message != null;
                            message = message.next()) {
          System.out.println();
          System.out.format("USER : %s\n", message.message.author);
          System.out.format("SENT : %s\n", message.message.creation);
          System.out.format("LIKES : %s\n", message.message.likes);
          System.out.println();
          System.out.println(message.message.content);
          System.out.println();
        }
        System.out.println("---  end of conversation  ---");
      }
    });

    // M-ADD (add message)
    //
    // Add a command to add a new message to the current conversation when the
    // user enters "m-add" while on the conversation panel.
    //
    panel.register("m-add", new Panel.Command() {
      @Override
      public void invoke(List<String> args) {
        final String message = args.get(0);
          args.remove(0);
        if (message.length() > 0) {
            conversation.add(message);
        } else {
          System.out.println("ERROR: Messages must contain text");
        }
      }
    });

    panel.register("m-like", new Panel.Command() {
      @Override
      public void invoke(List<String> args) {
        conversation.like();
      }
    });

    // INFO
    //
    // Add a command to print info about the current conversation when the user
    // enters "info" while on the conversation panel.
    //
    panel.register("info", new Panel.Command() {
      @Override
      public void invoke(List<String> args) {
        System.out.println("Conversation Info:");
        System.out.format("  Title : %s\n", conversation.conversation.title);
        System.out.format("  Id    : UUID:%s\n", conversation.conversation.id);
        System.out.format("  Owner : %s\n", conversation.conversation.owner);
      }
    });

    // Now that the panel has all its commands registered, return the panel
    // so that it can be used.
    return panel;
  }



  private Panel createMuteConversationPanel(final ConversationContext conversation) {

    final Panel panel = new Panel();

    // HELP
    //
    // Add a command that will print all the commands and their descriptions
    // when the user enters "help" while on the conversation panel.
    //
    panel.register("help", new Panel.Command() {
      @Override
      public void invoke(Scanner args) {
        System.out.println("USER MODE");
        System.out.println("  m-list");
        System.out.println("    List all messages in the current conversation.");
        System.out.println("  info");
        System.out.println("    Display all info about the current conversation.");
        System.out.println("  back");
        System.out.println("    Go back to USER MODE.");
        System.out.println("  exit");
        System.out.println("    Exit the program.");
      }
    });

    // M-LIST (list messages)
    //
    // Add a command to print all messages in the current conversation when the
    // user enters "m-list" while on the conversation panel.
    //
    panel.register("m-list", new Panel.Command() {
      @Override
      public void invoke(Scanner args) {
        System.out.println("--- start of conversation ---");
        for (MessageContext message = conversation.firstMessage();
             message != null;
             message = message.next()) {
          System.out.println();
          System.out.format("USER : %s\n", message.message.author);
          System.out.format("SENT : %s\n", message.message.creation);
          System.out.println();
          System.out.println(message.message.content);
          System.out.println();
        }
        System.out.println("---  end of conversation  ---");
      }
    });

    // INFO
    //
    // Add a command to print info about the current conversation when the user
    // enters "info" while on the conversation panel.
    //
    panel.register("info", new Panel.Command() {
      @Override
      public void invoke(Scanner args) {
        System.out.println("Conversation Info:");
        System.out.format("  Title : %s\n", conversation.conversation.title);
        System.out.format("  Id    : UUID:%s\n", conversation.conversation.id);
        System.out.format("  Owner : %s\n", conversation.conversation.owner);
      }
    });

    // Now that the panel has all its commands registered, return the panel
    // so that it can be used.
    return panel;
  }
}
