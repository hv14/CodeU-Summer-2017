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

package codeu.chat.common;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import codeu.chat.util.Serializer;
import codeu.chat.util.Serializers;
import codeu.chat.util.Time;
import codeu.chat.util.Uuid;

public enum ACCESSLEVEL{
  CREATOR, ADMIN, MEMBER
}

public final class ConversationHeader {

  public static final Serializer<ConversationHeader> SERIALIZER = new Serializer<ConversationHeader>() {

    @Override
    public void write(OutputStream out, ConversationHeader value) throws IOException {

      Uuid.SERIALIZER.write(out, value.id);
      Uuid.SERIALIZER.write(out, value.owner);
      Time.SERIALIZER.write(out, value.creation);
      Serializers.STRING.write(out, value.title);

    }

    @Override
    public ConversationHeader read(InputStream in) throws IOException {

      return new ConversationHeader(
          Uuid.SERIALIZER.read(in),
          Uuid.SERIALIZER.read(in),
          Time.SERIALIZER.read(in),
          Serializers.STRING.read(in)
      );

    }
  };

  public final Uuid id;
  public final Uuid owner;
  public final Time creation;
  public final String title;
  public HashMap<Uuid, Enum> memberlevels = new HashMap<>();

  public ConversationHeader(Uuid id, Uuid owner, Time creation, String title) {

    this.id = id;
    this.owner = owner;
    this.creation = creation;
    this.title = title;
    this.memberlevels = memberlevels;
    this.memberlevels[owner] = ACCESSLEVEL.CREATOR;

    //need to find the user by name in the command line chat
    // and then input their Uuid here
    public void changeRank(Uuid updateUser, ACCESSLEVEL goalLevel){

      //use the name to find the UUID of the person
      //whos' access we want to change
      if(this.accessControl[self.UUID] == AccessLevel.ADMIN){
		    if(goalLevel == AccessLevel.MEMBER){
          if (context.create(name) == null) {
            System.out.println("ERROR: Failed to create new user");
          }
			    this.accessControl[alteringUUID] = AccessLevel.MEMBER;
          }
        }
	    if(this.accessControl[self.UUID] ==  AccessLevel.CREATOR){
		    if(goalLevel == AccessLevel.MEMBER){
          if (context.create(name) == null) {
            System.out.println("ERROR: Failed to create new user");
          }
			    this.accessControl[alteringUUID] = AccessLevel.MEMBER;
          }
        if(goalLevel == AccessLevel.ADMIN){
			    this.accessControl[alteringUUID] = AccessLevel.ADMIN;
          }
      }
    }



  }
}
