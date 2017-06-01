package codeu.chat.common;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import codeu.chat.common.ConversationHeader;
import codeu.chat.common.ConversationPayload;
import codeu.chat.common.LinearUuidGenerator;
import codeu.chat.common.Message;
import codeu.chat.common.NetworkCode;
import codeu.chat.common.Relay;
import codeu.chat.common.Secret;
import codeu.chat.common.User;
import codeu.chat.common.ServerInfo;
import codeu.chat.util.Logger;
import codeu.chat.util.Serializers;
import codeu.chat.util.Time;
import codeu.chat.util.Timeline;
import codeu.chat.util.Uuid;
import codeu.chat.util.connections.Connection;


public final class ServerInfo {
  private final static String SERVER_VERSION = "1.0.0";

  public final Uuid version;

  public ServerInfo(){
    Uuid tempVersion = null;
    try{
       tempVersion = Uuid.parse(SERVER_VERSION);
    } catch (IOException ex) {
       System.out.println("ERROR: Incorrect ServerInfo input. ");
    }
    this.version = tempVersion;
  }
  public ServerInfo(Uuid version) {
    this.version = version;
  }


}
