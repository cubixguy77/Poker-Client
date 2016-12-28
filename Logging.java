import java.io.*;
import java.util.*;

public class Logging {
  
  public static Boolean approveUser(String username, String password, String filename) {
    
    try {
      Scanner sc = new Scanner(new File(filename));
      
      String tempuser;
      String temppass;
      
      Boolean done = false;
      while (!done) {
        tempuser = sc.next();
        if (tempuser.equals(username)) {
          temppass = sc.next();
          if (temppass.equals(password))
            return true;
          else {
            System.out.println("Wrong password");
            return false;
          }
        }
        if (tempuser.equals("end"))
          return false;
        temppass = sc.next();
       // System.out.println("pass: " + temppass);
        
        
      }
      return false;
    }
    catch  (java.util.NoSuchElementException e) {
      System.out.println("No such user");
      return false;
    }
    catch  (IOException e ) {
      return null;
    }
  }
  
  public static void addUser(String username, String password, String filename) {
    try{
      // Create file 
      FileWriter fstream = new FileWriter(filename, true);
      BufferedWriter out = new BufferedWriter(fstream);
      out.newLine();
      out.write(username + " " + password);
      //Close the output stream
      out.close();
    }
    catch (Exception e){//Catch exception if any
      System.err.println("Error: " + e.getMessage());
    }
  }    
  
}