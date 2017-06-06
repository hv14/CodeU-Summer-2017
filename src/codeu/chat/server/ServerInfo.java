package codeu.chat.server;

import codeu.chat.util.Time;
import codeu.chat.util.Uuid;

import java.io.IOException;

/**
 * Created by himonal on 6/2/17.
 */
public class ServerInfo {

    public final Time startTime;

    public ServerInfo() {
        this.startTime = Time.now();
    }

    public ServerInfo(Time startTime) {
        this.startTime = startTime;
    }
}
