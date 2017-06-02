package codeu.chat.server;

import codeu.chat.util.Time;
import codeu.chat.util.Uuid;

import java.io.IOException;

/**
 * Created by himonal on 6/2/17.
 */
public class ServerInfo {

    public final Time startTime;
    private final static String SERVER_VERSION = "1.0.0";
    public final Uuid version;

    public ServerInfo() {
        this.startTime = Time.now();
        Uuid tempVersion = null;

        try {
            tempVersion = Uuid.parse(SERVER_VERSION);
        }
        catch (IOException e) {
            System.out.println("Wrong");
        }

        this.version = tempVersion;
    }

    public ServerInfo(Time startTime) {
        this.startTime = startTime;
        Uuid tempVersion = null;

        try {
            tempVersion = Uuid.parse(SERVER_VERSION);
        }
        catch (IOException e) {
            System.out.println("Wrong");
        }

        this.version = tempVersion;
    }

    public ServerInfo(Uuid version) {
        this.version = version;
        startTime = Time.now();
    }
}
