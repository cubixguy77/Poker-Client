import java.util.*;

public class Tester {
  
  public static Player genRandomPlayer() {
    
    Random randomGenerator = new Random();
   
    char a = (char) (randomGenerator.nextInt(26) + 97);
    char b = (char) (randomGenerator.nextInt(26) + 97);
    
    String username = Character.toString(a) + Character.toString(b);
    Player player = new Player(username, Player.Status.AT_LOBBY);
    return player;
  }
  
  public static Table genFullTable(int tablesize) {
    Player[] players = new Player[tablesize];
    for (int i=0; i<tablesize; i++) {
      players[i] = genRandomPlayer();
    }
    return new Table(players);
  }
  
  public static Table genShortTable(int tablesize, int numplayers) {
    Player[] players = new Player[tablesize];
    for (int i=0; i<numplayers; i++) {
      players[i] = genRandomPlayer();
    }
    return new Table(players);
  }
}
    