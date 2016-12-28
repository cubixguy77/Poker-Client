
public class Payouts {
  
  public static int[] getPayouts(int numPlayers, int totalPrizePool) {
    int[] payouts = new int[3];
    payouts[0] = (65 * totalPrizePool);
    payouts[1] = (25 * totalPrizePool);
    payouts[2] = (10 * totalPrizePool);
    return payouts;
  }
}