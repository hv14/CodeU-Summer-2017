package codeu.chat.util;

import codeu.chat.common.ConversationHeader;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by himonal on 8/1/17.
 */
public enum AccessLevel implements java.io.Serializable {
    creator,
    owner,
    member,
    blocked,
    mute;

    public static  final Serializer<AccessLevel> SERIALIZER = new Serializer<AccessLevel>() {
        @Override
        public void write(OutputStream out, AccessLevel value) throws IOException {
            Serializers.STRING.write(out, value.toString());
        }

        @Override
        public AccessLevel read(InputStream in) throws IOException {
            return AccessLevel.valueOf(Serializers.STRING.read(in));
        }
    };
}
