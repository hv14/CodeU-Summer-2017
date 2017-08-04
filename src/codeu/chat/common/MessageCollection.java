package codeu.chat.common;

import java.util.ArrayList;

/**
 * This class is simply to hold all of the messages so it is easy to save them to a text file later
 * Created by himonal on 6/13/17.
 */
public class MessageCollection {
    public Message[] messages;

    public MessageCollection(ArrayList<Message> currentMessages) {
        messages = new Message[currentMessages.size()];
        messages = currentMessages.toArray(messages);
    }
}
