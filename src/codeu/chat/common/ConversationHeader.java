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
import java.util.HashMap;

import codeu.chat.util.*;

public final class ConversationHeader {

  public static final Serializer<ConversationHeader> SERIALIZER = new Serializer<ConversationHeader>() {

    @Override
    public void write(OutputStream out, ConversationHeader value) throws IOException {

      Uuid.SERIALIZER.write(out, value.id);
      Uuid.SERIALIZER.write(out, value.owner);
      Time.SERIALIZER.write(out, value.creation);
      Serializers.STRING.write(out, value.title);
      Serializers.STRING.write(out, value.defaultAccessLevel.toString());

    }

    @Override
    public ConversationHeader read(InputStream in) throws IOException {

      return new ConversationHeader(
              Uuid.SERIALIZER.read(in),
              Uuid.SERIALIZER.read(in),
              Time.SERIALIZER.read(in),
              Serializers.STRING.read(in),
              Serializers.STRING.read(in),
              Serializers.HASH_MAP_SERIALIZER.read(in)
      );

    }
  };

  public final Uuid id;
  public final Uuid owner;
  public final Time creation;
  public final String title;
  public final AccessLevel defaultAccessLevel;

  public HashMap<Uuid, AccessLevel> usersInConvo = new HashMap<>();

  public ConversationHeader(Uuid id, Uuid owner, Time creation, String title, String defaultAccessLevel, HashMap<Uuid, AccessLevel> usersInConvo) {
    this.id = id;
    this.owner = owner;
    this.creation = creation;
    this.title = title;
    this.defaultAccessLevel = AccessLevel.valueOf(defaultAccessLevel);
    this.usersInConvo = usersInConvo;

  }


}