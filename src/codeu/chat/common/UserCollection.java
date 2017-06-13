package codeu.chat.common;

import java.util.ArrayList;

/**
 * Created by himonal on 6/13/17.
 */
public class UserCollection {
    public User[] users;

    public UserCollection(ArrayList<User> currentUsers) {
        users = new User[currentUsers.size()];
        users = currentUsers.toArray(users);
    }
}
