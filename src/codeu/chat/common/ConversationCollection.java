package codeu.chat.common;

import java.util.ArrayList;

/**
 * Created by himonal on 6/13/17.
 */
public class ConversationCollection {
    public ConversationHeader[] conversations;

    public ConversationCollection(ArrayList<ConversationHeader> currentConvos) {
        conversations = new ConversationHeader[currentConvos.size()];
        conversations = currentConvos.toArray(conversations);
    }
}
