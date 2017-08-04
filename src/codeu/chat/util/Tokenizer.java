package codeu.chat.util;

import java.io.IOException;
import java.util.logging.Level;

public final class Tokenizer {
  
  private StringBuilder token;
  private String source;
  private int at;

  
  
  public Tokenizer(String s) { 
    source = s;
  }
  
  private int remaining() {
    return source.length() - at;
    }
  
  private char peek() throws IOException {
    if (at < source.length()) {
      return source.charAt(at);
    } else {
      throw new IOException("End Of String");
    }
  }
  
  private char read() throws IOException {
    final char c = peek();
    at += 1;
    return c;
  }
  
  public String next() throws IOException {
    // Skip all leading whitespace
    String tokenCreation;
      while (remaining() > 0 && Character.isWhitespace(peek()) ) {
       read();  // ignore the result because we already know that it is a whitespace character
      }
      if (remaining() <= 0) {
        return null;
     } else if (peek() == '"') {
        tokenCreation = readWithQuotes(); //need to check
      } else {
        tokenCreation = readWithNoQuotes();  //need to check
      }
    return tokenCreation;
}

  private String readWithNoQuotes() throws IOException {
    token.setLength(0);  // clear the token
     while (remaining() > 0 && !Character.isWhitespace(peek())) {
      token.append(read());
     }
    return token.toString();
  }
 
  private String readWithQuotes() throws IOException {
    token.setLength(0);  // clear the token
    if (read() != '"') {
      throw new IOException("Strings must start with opening quote");
    }
    while (peek() != '"') {
      token.append(read());
    }
    read();
    return token.toString();
  }
}
