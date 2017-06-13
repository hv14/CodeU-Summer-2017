package codeu.chat.util

import java.io.IOException;
import static org.junit.Assert.*;
import org.junit.Test;

public final class TokenizerTest{
  public void testWithQuotes() throws IOException{
    final Tokenizer tokenizer = new Tokenizer("\"hello world\"");
    assertEquals(tokenizer.next(), "hello world");
    assertEquals(tokenizer.next(), null);
  }
  
  public void testWithNoQuotes throws IOException{
    final Tokenizer tokenizer = new Tokenizer("hello world");
    assertEquals(tokenizer.next(), "hello");
    assertEquals(tokenizer.next(), "world");
    assertEquals(tokenizer.next(), null);
  }
}
