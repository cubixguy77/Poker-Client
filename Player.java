import java.net.*;
import java.io.*;

public class Player {
    
    private String username;
    private Socket connection;
    
    public enum Status {
        AT_LOBBY, AT_TABLE, WATCHING_TABLE, IN_HAND, OUT_HAND ;
    }
    
    private Status status;
    private int chipstack;
    private int seatnum;
    private int tablenum;
    private int tourneynum;
    
    private Card[] mycards;
    int contributedThisStreet;
    int contributedTotal;
    private Boolean action; // true if action is on this player, false otherwise
    
    public Player(String username, Status status) {
        this.username = username;
        this.status = status;
        mycards = new Card[2];
    }
    
    public Player(String username, Status status, int chipstack, int seatnum, int tablenum, int tourneynum) {
        this.username = username;
        this.status = status;
        this.chipstack = chipstack;
        this.seatnum = seatnum;
        this.tablenum = tablenum;
        this.tourneynum = tourneynum;
        mycards = new Card[2];
    }
    
    public String toString() {
        return username;
    }
    public Boolean equals(Player player) {
        return this.username == player.getUserName();
    }
    public void setUserName(String username) {
        this.username = username;
    }
    public String getUserName() {
        return username;
    }
    public void setConnection(Socket connection) {
        this.connection = connection;
    }
    public Socket getConnection() {
        return connection;
    }
    
   /* public void setInHandStatus(InHandStatus status) {
        this.inHand = status;
    }
    public InHandStatus getInHandStatus() {
        return inHand;
    }*/
    
    public void setSeatNum(int seat) {
        this.seatnum = seat;
    }
    public int getSeatNum() {
        return seatnum;
    }
    public void setTableNum(int table) {
        this.tablenum = table;
    }
    public int getTableNum() {
        return tablenum;
    }
    public void setTourneyNum(int num) {
        this.tourneynum = num;
    }
    public int getTourneyNum() {
        return tourneynum;
    }
    public void zeroContributed() {
        contributedThisStreet = 0;
        contributedTotal      = 0;
    }
    public void setBothContributed(int amount) {
        contributedThisStreet = amount;
        contributedTotal      = amount;
    }
    
    public void contributeChips(int amount) {
        contributedThisStreet += amount;
        contributedTotal += amount;
        takeFromStack(amount);
    }
    
    // returns the amount the pot is increased by following the assignment
    public int setContributedThisStreet(int contributed) {
        int amountToAdd = contributed - contributedThisStreet;
        this.contributedThisStreet = contributed;

        takeFromStack(amountToAdd);
        contributedTotal += amountToAdd;
        return amountToAdd;
    }
    public void setContributedThisStreetNoUpdate(int contributed) {
      //  int wagerAmount = contributed - contributedThisStreet;
        this.contributedThisStreet = contributed;
      //  takeFromStack(wagerAmount);
      //  contributedTotal += wagerAmount;
    }
    public int getContributedThisStreet() {
        return contributedThisStreet;
    }
    public void setContributedTotal(int contributed) {
        this.contributedTotal = contributed;
    }
    public int getContributedTotal() {
        return contributedTotal;
    }
    
    public void setChipStack(int chips) {
        this.chipstack = chips;
    }
    public void addToStack(int amount) {
        chipstack += amount;
    }
    public void takeFromStack(int amount) {
        chipstack -= amount;
      //  contributedThisStreet += amount;
      //  contributedTotal += amount;
    }
    public int getChipStack() {
        return chipstack;
    }
    
    
    public void setStatus(Player.Status status) {
        this.status = status;
    }
    public Player.Status getStatus() {
        return status;
    }
    public void setHand(Card card1, Card card2) {
        mycards[0] = card1;
        mycards[1] = card2;
    }
    public Card[] getHand() {
        return mycards;
    }
    public String displayCards() {
        if (mycards[0] == null)
            return "none";
        else
            return (mycards[0].toString() + " " + mycards[1].toString());
    }
    public void setAction(Boolean action) {
        this.action = action;
    }
    public Boolean getAction() {
        return action;
    }
   /* public void setCardsVisible(Boolean visible) {
        mycards[0].setFaceUp(visible);
        mycards[1].setFaceUp(visible);
    }
    public Boolean getCardsVisible() {
        return mycards[0].isFaceUp();
    } */
    
}
