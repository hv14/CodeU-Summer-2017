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

public final class User {

  public static final Serializer<User> SERIALIZER = new Serializer<User>() {

  //first table : key = title of convo, value = true/false depending on interestConvos
  //second table: key = user, value = true/false depending on interestConvos
  // third table: key = convo title, value = the last time update was called
  public static final Set interestConvos =  new HashSet();
  public static final Set interestUsers =  new HashSet();
  public static final Hashtable lastUpdateConvos = new Hashtable();
  public static final Hashtable lastUpdateUsers = new Hashtables();

  public static final Hash
    public void setHashTable(String title){
        interestConvos.put(title, True);
    }

    public void setlastUpdateConvos(UUID titleID, long time){
      return lastUpdateConvos(title, time);
    }
    public void setlastUpdateUsers(UUID nameID, Long time){
      return interestUsers.add(nameID, time);
    }
    public Set getInterestConvos(){
      return interestedConvo;
    }
    public Set getInterestUsers(){
      return interestedUsers;
    }

    public Hashtable getLastUpdateConvos(){
      return lastUpdateConvos;
    }
    public Hashtable getLastUpdateUsers(){
      return lastUpdateUsers;
    }

    public void interestedConvo(String title){
      interestConvos.add(title);
    }

    public void uninterestedConvo(String title){
      interestConvos.remove(title);
    }

    public void interestedUser(String title){
      interestUsers.add(title);
    }

    public void uninterestedUser(String title){
      interestUsers.put(title, False);
    }

    @Override
    public void write(OutputStream out, User value) throws IOException {

      Uuid.SERIALIZER.write(out, value.id);
      Serializers.STRING.write(out, value.name);
      Time.SERIALIZER.write(out, value.creation);

    }

    @Override
    public User read(InputStream in) throws IOException {

      return new User(
          Uuid.SERIALIZER.read(in),
          Serializers.STRING.read(in),
          Time.SERIALIZER.read(in)
      );

    }
  };

  public final Uuid id;
  public final String name;
  public final Time creation;

  public User(Uuid id, String name, Time creation) {

    this.id = id;
    this.name = name;
    this.creation = creation;

  }
}
