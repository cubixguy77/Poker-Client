import java.util.*;

import java.util.Timer;
import java.util.TimerTask;

public class Tournament {
    
    private int ID;
    private int startStackSize;
    private BlindLevel[] BlindStructure;
    private int currentLevel;
    private int[] payouts;
    private Table[] Tables;
    private int TableSize;
    private ArrayList<Player> registered;
    private int balance;  // maintains the number of players currently constituting a full table
    
    private String tourneyName;
    private String gameType;
    private String limitType;
    private String buyIn;
    
    private int numEntrants;
    private int numRemaining;
    private int averageStack;
    
    
    public Tournament (int ID, int startStackSize, int TableSize, BlindLevel[] BlindStructure, int[] payouts, String tourneyName, String gameType) {
        this.ID = ID;
        this.tourneyName = tourneyName;
        this.gameType = gameType;
        this.startStackSize = startStackSize;
        this.TableSize = TableSize;
        this.BlindStructure = BlindStructure;
        currentLevel = 0;
        this.payouts = payouts;
        registered = new ArrayList<Player>();
        numEntrants = 0;
        numRemaining = 0;
    }
    
    public Tournament (int ID, int startStackSize, int TableSize, Table[] Tables, BlindLevel[] BlindStructure, int[] payouts, String tourneyName, String gameType) {
        this.ID = ID;
        this.tourneyName = tourneyName;
        this.gameType = gameType;
        this.startStackSize = startStackSize;
        this.TableSize = TableSize;
        this.Tables = Tables;
        this.BlindStructure = BlindStructure;
        currentLevel = 0;
        this.payouts = payouts;
        registered = new ArrayList<Player>();
        numEntrants = 0;
        numRemaining = 0;
    }
    
    
    
    /* Starts this tournament object
     * Needs to launch a separate thread for each table to run on */
    public void startTournament() {
        numRemaining = registered.size();
        
        /* Create a timer to periodically update the tournament info */
        Timer updateTimer = new Timer();
        TimerTask task = new tourneyUpdater();
        long delay = 0;
        long period = 5 * 1000; // every 5 seconds
        updateTimer.schedule(task, delay, period);
        
        /* Create a timer to periodically update the blinds */
        Timer blindTimer = new Timer();
        TimerTask task2 = new blindUpdater();
        long delay2 = 0;
        long blindTime = BlindStructure[0].getBlindTime() * 60 * 1000;
        blindTimer.schedule(task2, delay2, blindTime);

        
        for (int i=0; i<Tables.length; i++) {
            Tables[i].startFirstHand(BlindStructure[currentLevel]);
        }
        for (int i=0; i<Tables.length; i++) {
            Tables[i].displayTable(2);
        }
    }
    
    /* Simple timer class to periodically update the tournament info */
    public class tourneyUpdater extends TimerTask {
        public void run() {
            updateTournamentInfo();
        }
    }
    
    /* Simple timer class to periodically increase the blinds */
    public class blindUpdater extends TimerTask {
        public void run() {
            increaseBlinds();
        }
    }
    
    public void increaseBlinds() {
        currentLevel++;
        for (int table=0; table<Tables.length; table++) {
            Tables[table].setBlindLevel(BlindStructure[currentLevel]);
        }
        System.out.println("=================Blinds are Up!!!!!!!!==============================");
    }

    public void updateTournamentInfo() {
        int tempNumRemaining = 0;
        int totalChips = 0;
        for (int table=0; table<Tables.length; table++) {
            for (int seat=0; seat<TableSize; seat++) {
                if (Tables[table].getPlayer(seat) != null) {
                    tempNumRemaining++;
                    totalChips +=   Tables[table].getPlayer(seat).getChipStack() 
                                  + Tables[table].getPlayer(seat).getContributedTotal();
                }
            }
        }
        numRemaining = tempNumRemaining;
        averageStack = totalChips / numRemaining;
    }
    
    public String getPayoutMessage() {
        String message = "#" + payouts.length;
        for (int i=0; i<payouts.length; i++) {
            message += payouts[i] + "#";                        
        }
        return message;
    }
    
    public double getPayout(int place) {
        return payouts[place];
    }
    
    public String getPlayersMessage() {
        String message = "#" + numRemaining;
        for (int table=0; table<Tables.length; table++) {
            for (int seat=0; seat<TableSize; seat++) {
                if (Tables[table].getPlayer(seat) != null) {
                    message += Tables[table].getPlayer(seat).getUserName() + "#" + (Tables[table].getPlayer(seat).getChipStack() 
                                                                             +  Tables[table].getPlayer(seat).getContributedTotal());
                }
            }
        }
        return message;
    }
            
    public void sendTournamentInfo(Player player) {
        String message = "#TourneyInfo";
        message +=   "#" + ID
         + "#" + tourneyName
         + "#" + buyIn
         + "#" + gameType
         + "#" + BlindStructure[currentLevel].getBigBlind() 
         + "#" + BlindStructure[currentLevel].getSmallBlind() 
         + "#" + numEntrants 
         + "#" + numRemaining
         + "#" + averageStack
         + getPayoutMessage()
         + getPlayersMessage()
         + "#";
    }           
    
    
    public void balanceTables(int shortTableNum) {
        Table shortTable = Tables[shortTableNum];
        while ((shortTable.getNumActive() + 1) < balance) {
            System.out.println("balancing: current balance #: " + balance);
            for (int i=0; i<Tables.length; i++) {
                if (Tables[i].getNumActive() - 1 > shortTable.getNumActive()) {
                    for (int seat=0; seat<TableSize; seat++) {
                        if (Tables[i].getPlayer(seat) != null) {
                            System.out.println("Balancing: Transferring player at table " + i + " and seat " + seat + " to " + shortTableNum);
                            transferPlayer(seat, i, shortTableNum);
                            break;
                        }
                    }
                }
            }   
        }
    }
    
    
    public void transferPlayer(int seat, int fromTable, int toTable) {
        Player player = Tables[fromTable].getPlayer(seat);
        assignPlayerTo(player, toTable); // add the player to the new table
        Tables[fromTable].setPlayer(null, seat);
    }
    
    
    public int getTotalEmptySeats() {
        int numEmptySeats = 0;
        for (int i=0; i<Tables.length; i++) 
            numEmptySeats += Tables[i].getNumEmpty();
        return numEmptySeats;
    }
    
    /* checks to see if it is necessary to break the tables
     * if so, then it breaks the tables as necessary to ensure condition 1 */
    public void breakTable(int tableNum) {
        System.out.println("Breaking table " + tableNum);
        int numActive = Tables[tableNum].getNumActive();
        while (numActive > 0) {
            int nextSeatNum = Tables[tableNum].getFirstActivePlayerIndex();
            assignPlayer(Tables[tableNum].getPlayer(nextSeatNum));
            Tables[tableNum].setPlayer(null, nextSeatNum);
            numActive--;
        }
        System.out.println("Table broken, now removing table from tournament");
        removeTable(tableNum);
    }
    
    public void removeTable(int tableNum) {
        Table[] newTableArray = new Table[Tables.length - 1];
        if (tableNum == Tables.length - 1) {                 // remove the last table
            for (int i=0; i<Tables.length - 1; i++) {
                newTableArray[i] = Tables[i];
            }
            Tables = newTableArray;
        }
        else if (tableNum == 0) {                          // remove the first table
            for (int i=1; i<Tables.length; i++) {
                newTableArray[i] = Tables[i];
            }
            Tables = newTableArray;
        }
        else {                                             // remove any other table
            for (int i=0; i<tableNum; i++) {
                newTableArray[i] = Tables[i];
            }
            for (int i=tableNum+1; i<Tables.length - 1; i++) {
                newTableArray[i] = Tables[i];
            }
        }
    }
    
    public void assignPlayerTo(Player player, int table) {
        if (Tables == null) {
            System.out.println("You have assigned a player to a table that doesn't exist. I'll create one for you.");
            Tables = new Table[1];
            Tables[0] = new Table(TableSize, ID, 0, tourneyName, gameType, payouts);
        }
        
        if (Tables[table].getNumActive() == TableSize) {
            System.out.println("Table cannot be added to, currently full");
            return;
        }
        else {
            for (int seat=0; seat<TableSize; seat++) {
                if (Tables[table].getPlayer(seat) == null) {
                    System.out.println("Adding " + player.toString() + " to table " + table + " and seat # " + seat);
                    Tables[table].setPlayer(player, seat);
                    player.setTourneyNum(ID);
                    player.setTableNum(table);
                    player.setSeatNum(seat);
                    player.setStatus(Player.Status.AT_TABLE);
                    return;
                }
            }
        }
    }
    
    
    public void assignPlayer(Player player) {
        if (Tables == null) {
            Tables = new Table[1];
            Tables[0] = new Table(TableSize, ID, 0, tourneyName, gameType, payouts);
        }
        
        Boolean done = false;
        for (int table=0; table<Tables.length; table++) {
            for (int seat=0; seat<TableSize; seat++) {
                if (Tables[table].getPlayer(seat) == null) {
                    System.out.println("Adding " + player.toString() + " to table " + table + " and seat # " + seat);
                    Tables[table].setPlayer(player, seat);
                    player.setTourneyNum(ID);
                    player.setTableNum(table);
                    player.setSeatNum(seat);
                    player.setStatus(Player.Status.AT_TABLE);
                    done = true;
                    break;
                }
            }
        }
        if (!done) {
            Tables = Table.addTable(Tables, TableSize);
            Tables[Tables.length-1].setPlayer(player, 0);
            player.setTourneyNum(ID);
            player.setTableNum(Tables.length-1);
            player.setSeatNum(0);
            player.setStatus(Player.Status.AT_TABLE);
            System.out.println("Added a new table, there are now " + Tables.length + " tables, player added to seat zero");
            done = true;
        }
        System.out.println("");
    }
    
    
    public void registerPlayer(Player player) {
        registered.add(player);
        
        System.out.println("Player " + player.toString() + " has been registered for the tournament.");
        Messenger.sendMessage(player.getConnection(), "#GenInfo#You are now registered for the tournament " + player.getUserName());
        //Messenger.sendMessage(player.getConnection(), "#update#1#tourneyID#" + ID); // let the player store which tournament they are in
        
        if (registered.size() >= TableSize)
            balance = TableSize;
        else
            balance = registered.size();
    }
    
    
    
    public void unregisterPlayer(Player player) {
        for (int i=0; i<registered.size(); i++) {
            if (registered.get(i).equals(player)) {
                System.out.println("Player " + registered.get(i).toString() + " has been unregistered.");
                registered.remove(i);
                break;
            }
        }
        displayRegistered();
    }
    
    public void displayRegistered() {
        System.out.println("\nCurrently registered players");
        for (int i=0; i<registered.size(); i++) {
            System.out.println(registered.get(i));
        }
    }
    
    public void seatRegisteredPlayers() {
        shufflePlayers();                         // randomize the seating
        for (int i=0; i<registered.size(); i++) {
            assignPlayer(registered.get(i));  // move the player to a seat
        }
        registered.clear();                 // once all players have been assigned a seat, remove them from the registration list
        
        displaySeatedPlayers();
        for (int table=0; table<Tables.length; table++) {
            for (int seat=0; seat<TableSize; seat++) {
                if (Tables[table].getPlayer(seat) != null) {
                    System.out.println("Sending message to player");
                    Messenger.sendMessage(Tables[table].getPlayer(seat).getConnection(), "#GenInfo#You are now seated at table " + table + " at seat " + seat);
                    Messenger.sendMessage(Tables[table].getPlayer(seat).getConnection(), "#update#TableSize#" + TableSize + "#");
                    Messenger.sendMessage(Tables[table].getPlayer(seat).getConnection(), "#update#many#3#tourneyID#"+ID + "#tableID#"+table +"#seat#"+seat+"#");
                }
            }
        }
    }
    
    public void shufflePlayers() {
        Collections.shuffle(registered);
    }
    
    
    public void displaySeatedPlayers() {
        for (int table=0; table<Tables.length; table++) {
            System.out.println("Table " + (table) + " contains " + Tables[table].getNumActive() + " active players.");
            for (int seat=0; seat<TableSize; seat++) {
                if (Tables[table].getPlayer(seat) != null)
                    System.out.println("Player " + Tables[table].getPlayer(seat).toString() + " is sitting in seat " + (seat));
                else
                    System.out.println("Null");
            }
        }
    }
    
    public int getID() {
        return ID;
    }
    public void setID(int ID) {
        this.ID = ID;
    }
    public int getStartStackSize() {
        return startStackSize;
    }
    public BlindLevel[] getBlindStructure() {
        return BlindStructure;
    }
    public BlindLevel getCurrentLevel() {
        return BlindStructure[currentLevel];
    }
    public int[] getPayouts() {
        return payouts;
    }
    public Table[] getTables() {
        return Tables;
    }
    public Table getTable(int tablenum) {
        return Tables[tablenum];
    }
    public void setTables(Table[] Tables) {
        this.Tables = Tables;
    }
}
