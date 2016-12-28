import java.io.*;
import java.util.*;

public class StringAnalyzer {
  
    public static String[] getWords(String input) {
    //  ArrayList words = new ArrayList<String>();
    Scanner sc = new Scanner(input).useDelimiter("#");
    String word;
    String words[] = new String[20];
    int i=0;
    Boolean done = false;
    
    while (!done) {
      try {
        word = sc.next();
        words[i] = word;
   //     words.add(word);
        i++;
      }
      catch (java.util.NoSuchElementException e) {
        done = true;
      }
      
    }
 
    return condenseArray(words); // eliminate the null entries for tidyness' sake
    //return words.toArray();
  }
  
  private static String[] condenseArray(String[] words) {
    int length = 0;
    for (int i=0; i<words.length; i++) {
      if (words[i] != null) 
        length++;
      else
        break;
    }
    String[] newarray = new String[length];
    for (int i=0; i<newarray.length; i++)
      newarray[i] = words[i];
    return newarray;
  }
}