package codeu.chat.common;

import java.util.ArrayList;

/**
 * This class is simply to hold all of the users so it is easy to save them to a text file later
 * Created by himonal on 6/13/17.
 */
public class UserCollection {
    public User[] users;

    public UserCollection(ArrayList<User> currentUsers) {
        users = new User[currentUsers.size()];
        users = currentUsers.toArray(users);
    }
}
