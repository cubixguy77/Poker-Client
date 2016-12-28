import java.io.*;
import java.net.*;

public class Messenger {
  
  public static void sendMessage(Socket connection, String message) {
    try {
      PrintStream output = new PrintStream(connection.getOutputStream());
      output.println(message + (char) 13);   // print vs println
    }
    catch (IOException e) {
      System.out.println("Unable to send message, IO Exception");
    }
  }
  public static void sendMessage(Socket connection, OutputStreamWriter osw, String message) {
    try {
      osw.write(message);
      osw.flush();
    }
    catch (IOException e) {
      System.out.println("Unable to send message, IO Exception");
    }
  }
  
  public static void sendMessage(Socket connection, String message, Boolean bool) {
    try {
        PrintWriter out = new PrintWriter(connection.getOutputStream(), bool);
        out.println(message);
    }
    catch (IOException e) {
      System.out.println("Unable to send message, IO Exception");
    }
  }
  
}