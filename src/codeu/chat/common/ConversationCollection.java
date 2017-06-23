package codeu.chat.common;

import java.util.ArrayList;

/**
 * This class is simply to hold all of the conversations so it is easy to save them to a text file later
 * Created by himonal on 6/13/17.
 */
public class ConversationCollection {
    public ConversationHeader[] conversations;

    public ConversationCollection(ArrayList<ConversationHeader> currentConvos) {
        conversations = new ConversationHeader[currentConvos.size()];
        conversations = currentConvos.toArray(conversations);
    }
}
