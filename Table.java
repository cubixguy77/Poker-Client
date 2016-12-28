import java.lang.*;
import java.util.*;
import java.awt.*;

public class Table {
    
    private Player[] players;
    private int TableSize;
    private int numActive;
    private static int tourneyID;
    private int tableID;
    private static String tourneyName;
    private static String gameType;
    private static int[] payouts;
    private String tableWindowTitle; // the title that appears on the user's game window
    
    private Deck deck;
    private Card[] board;
    private Pot[] pots;

    
    private int button;     // seat number of button
    private int smallblind; // seat number of small blind
    private int bigblind;   // seat number of big blind
    
    private int actionon;
    private int currentBet;
    private BlindLevel currentLevel;
    private Street currentStreet;
    private int currentpot;
    private int origbettor;     // seat of person who opened the betting on the current street
    private int bbsize;   // size of big blind
    private int sbsize;   // size of small blind
    private int antesize; // size of ante
    
    private boolean allinflag = false;
    private boolean blindsUpNextHand = false;
    
    public enum Street {
        PREFLOP, FLOP, TURN, RIVER ;
    }
    
    public Table(Player[] players, int tourneyID, int tableID, String tourneyName, String gameType, int[] payouts) {
        this.players = players;
        this.tourneyID = tourneyID;
        this.tableID = tableID;
        this.tourneyName = tourneyName;
        this.gameType = gameType;
        this.payouts = payouts;
        for (int i=0; i<players.length; i++) {
            if (players[i] != null) {
                numActive++;
            }
        }
        deck = new Deck();
        board = new Card[5];
        pots = new Pot[1];
        currentpot = 0;    // there are no sidepots, pot 0 is the main pot
        
        button = 0;
        smallblind = getFirstActivePlayerIndexFrom(button+1);
        bigblind = getFirstActivePlayerIndexFrom(smallblind + 1);
        
        tableWindowTitle = getTableWindowTitle(tourneyName, tableID, sbsize, bbsize, gameType);
    }
    
    
    public Table(int TableSize, int tourneyID, int tableID, String tourneyName, String gameType, int[] payouts) {        
        this.TableSize = TableSize;
        this.tourneyID = tourneyID;
        this.tableID = tableID;
        this.tourneyName = tourneyName;
        this.gameType = gameType;
        this.payouts = payouts;
        numActive = 0;
        
        players = new Player[TableSize];
        
        deck = new Deck();
        board = new Card[5];
        pots = new Pot[1];
         
        button = 0;
        smallblind = getFirstActivePlayerIndexFrom(button+1);
        bigblind = getFirstActivePlayerIndexFrom(smallblind + 1);
        
        tableWindowTitle = getTableWindowTitle(tourneyName, tableID, sbsize, bbsize, gameType);
    }
    
    public String getTableWindowTitle(String tourneyName, int tableID, int sbsize, int bbsize, String gameType) {
        return tourneyName + " - " + "Table " + tableID + " - " + sbsize + "/" + bbsize + " - " + gameType;
    }
   
    public void startFirstHand(BlindLevel currentLevel) {
        
        sendMessageToTable("#update#openGameWindow#");
        
        /* let everyone know who is at their table */
        String message = "#update#players#";
        this.currentLevel = currentLevel;
        for (int i=0; i<players.length; i++) {
            if (players[i] != null) {
                players[i].setChipStack(1500);
                message += i + "#" + players[i].toString() + "#" + players[i].getChipStack() + "#";
            }
        }
        sendMessageToTable(message);  // send the message

        cleanUp();               // and then start the hand
    }
    
    public void sendUpdatedWindowTitle() {
        tableWindowTitle = getTableWindowTitle(tourneyName, tableID, sbsize, bbsize, gameType);
        sendMessageToTable("#update#windowTitle#" + tableWindowTitle + "#");
    }
    
    public double getPayout(int place) {
        return payouts[place];
    }
    
    /* Called at the end of every hand
     * Clears the pot and the bets on the table, clears the player cards, and checks for eliminations */
    public void cleanUp() {
        System.out.println("1st place receives " + getPayout(0));
        clearBoard();
        sendUpdatedBoard();
        
        clearPots();
                       
        for (int i=0; i<TableSize; i++) {
            if (players[i] != null) {
                
                players[i].zeroContributed();
                sendUpdatedContributions();
                
                sendMessageToTable("#update#hidecards#" + i + "#");
                
                if (players[i].getChipStack() == 0) {
                    System.out.println("PLAYER " + i + " HAS BEEN ELIMINATED.");
                    sendMessageToTable("#update#knockout#" + i + "#" + (numActive) + "#");
                    sendMessageToTable("#update#reward#" + i + "#" + (numActive) + "#" + getPayout(numActive) + "#");
                  //  players[i] = null;
                    numActive --;
                    if (numActive == 1) {
                        int winseat=0;
                        for (int seat=0; seat<TableSize; seat++) {
                            if (players[seat] != null && players[seat].getChipStack() > 0) {
                                winseat = seat;
                                System.out.println("seat: " + players[seat].getChipStack());
                            }
                        }
                        String winName = players[winseat].getUserName();
                        
                        System.out.println("***********************PLAYER " + winseat + " WINS THE TOURNAMENT!!**********************");
                        sendMessageToTable("#update#win#" + winseat + "#" + winName + "#");
                        sendMessageToTable("#update#reward#" + winseat + "#" + 1 + "#" + getPayout(0) + "#");
                        sendMessageToTable("#update#closeGameWindow#");
                    }
                }
            }
        }
        
        if (numActive > 1)
            startNewHand();
    }
        
        
        
    public void startNewHand() {
        System.out.println("========================Starting New Hand =====================================");
        
                
        /* update the size of the blinds */
        sbsize = currentLevel.getSmallBlind();  
        bbsize = currentLevel.getBigBlind();
        currentBet = bbsize;
        incrementButtonAndBlinds();  // includes a message to players about position of button and size of blinds
        shuffleDeck();
               
        dealCards();
        
        currentStreet = Street.PREFLOP;
        
        postBlinds();  // sends message about where the blinds are, and lets table know that they have been posted
        origbettor = bigblind;
        
        sendUpdatedWindowTitle();
        
        actionon = getFirstToAct();
        sendActionRequest(actionon, getToCall());
    }
    
    public int getToCall() {
        return currentBet - players[actionon].getContributedThisStreet();
    }
    
    // returns the amount added to the pot following the posting of the blinds
    public int postBlinds() {
        //System.out.println("button: " + button + " small: " + smallblind + " big: " + bigblind);
        players[smallblind].contributeChips(sbsize);
        pots[0].add(sbsize);
       
        players[bigblind].contributeChips(bbsize);
        pots[0].add(bbsize);
        
     //   System.out.println("sending updated chip counts following posting of the blinds");
     //   System.out.println("smallblind in seat " + smallblind + " and is " + sbsize + " chips.");
    //    System.out.println("big blind in  seat " + bigblind + " and is " + bbsize + " chips.");
        sendUpdatedChips();
        sendMessageToTable("#update#post#smallblind#" + smallblind + "#");
        sendMessageToTable("#update#post#bigblind#"   + bigblind + "#");
        return sbsize + bbsize; // return pot size
    }
    
    public void incrementButtonAndBlinds() {
     //   System.out.println("button before was " + button);
        button = getFirstActivePlayerIndexFrom(button + 1);
     //   System.out.println("button is now " + button);
        smallblind = getSmallBlindIndex(button);
        bigblind = getFirstActivePlayerIndexFrom(smallblind + 1);
        sendMessageToTable("#update#button#" + button + "#");
        sendUpdatedBlinds();
    }
    public int getSmallBlindIndex(int button) {
        if (numActive == 2)  // heads up, the dealer gets the small blind
            return button;
        else
            return getFirstActivePlayerIndexFrom(button + 1); // otherwise, it's always the first guy after the button
    }
    
    
    
    
    
    
    /* returns the number of players still in the hand that haven't folded */
    public int getNumInHand() {
        int num = 0;
        for (int i=0; i<players.length; i++) {
            if (players[i] != null && players[i].getStatus() == Player.Status.IN_HAND)
                num++;
        }
        return num;
    }
    
    /* returns the number of players at this table that exist, are still in the hand, and are not all in */
    public int numWithChips() {
        int total=0;
        for (int i=0; i<TableSize; i++) {
            if (players[i] != null && players[i].getStatus() == Player.Status.IN_HAND && players[i].getChipStack() > 0)
                total++;
        }
        return total;
    }
    
    
    public void clearPots() {
        for (int i=0; i<pots.length; i++)
            pots[i] = new Pot();
    }
    
    /* removes the player in seat from all pots */
    public void removePlayerFromPot(int seat) {
        for (int potNum=0; potNum <= currentpot; potNum++)
            pots[potNum].removePlayer(seat);
        players[seat].setStatus(Player.Status.OUT_HAND);
    }
    
    /* appends a new pot to the array of pots, with amount 0 and no one eligible to win it */
    public void addSidePot() {
        Pot[] newpots = new Pot[pots.length + 1];
        for (int i=0; i<pots.length; i++)
            newpots[i] = pots[i];
        newpots[newpots.length - 1] = new Pot();
        pots = newpots;
    }
    
    /* removes the last pot in the pots array */
    public void removeSidePot() {
        Pot[] newpots = new Pot[pots.length - 1];
        for (int i=0; i<pots.length - 1; i++)
            newpots[i] = pots[i];
        pots = newpots;
    }
    
     public void printPots() {
        System.out.println("____________Print_Pots_________________________");
        for (int i=0; i<pots.length; i++) {
            System.out.println("Pot " + i + ": " + pots[i].getPotSize() + " chips.");
            System.out.print("Eligibles: ");
            for (int j=0; j < pots[i].getEligibles().length; j++) {
                //if (pots[i].getEligibles()[j] != -1)
                    System.out.print(pots[i].getEligibles()[j] + ", ");
            }
            System.out.println("");
        }
        System.out.println("_____________Print_Pots______________________");
    }
    
     /****************************Begin action handlers*********************************************************************************************************/
     
     /* accepts an array of strings containing the information about a player's action, sent from the client to the server, who forwarded it to this table
     updates the necessary variables at the table based on that action */
    public void handleAction(String[] actions) {
       
        String action = actions[0];
        /* note that the seat of the player making the action is known to be stored in the variable actionon */
        if (action.equals("fold"))
            handleFold();
        else if (action.equals("bet"))
            handleBet(Integer.parseInt(actions[1]));  // takes the size of the bet
        else if (action.equals("call"))
            handleCall();
        else if (action.equals("raise"))
            handleRaise(Integer.parseInt(actions[1]));   // takes the total amount of the raise
        else if (action.equals("check"))
            handleCheck();
        else if (action.equals("chatbox"))
            handleChatBox(actions);
       // else if (action.equals("allin"))
      //      handleAllIn(seat);               
        else
            System.out.println("Bad action");
    }
    
    public void handleChatBox(String [] actions) {
        String speaker = actions[1];
        String message = actions[2];
        sendMessageToTable("#update#chatbox#" + speaker + "#" + message + "#");
    }
    
    /* handles the action of a player folding in seat seat */
    public void handleFold() {
        sendMessageToTable("#GenInfo#" + "Player " + actionon + " folds.");  // actionon is the seat of the player that has folded
        removePlayerFromPot(actionon);                                       // remove them from the pot
        sendMessageToTable("#update#hidecards#" + actionon + "#");           // and remove their cards from sight so we know they folded
        
        int nextplayer = getNextPlayerInHandFrom(actionon + 1);
        
        if (getNumInHand() == 1) {                               // if there were only 2 players left at the time of the fold
            assignPots();                                        // then the other player still in the hand picks up the pot
            sendUpdatedChips();                                  // so the hand is over, send everyone the updated chip counts
        }
        else if (nextplayer == origbettor || nextplayer == actionon || nextplayer == -1) {
            dealNextStreet();
        }
        else {
            actionon = nextplayer;
            if (getToCall() == 0)
                dealNextStreet();
            else
                sendActionRequest(actionon, getToCall());
        }
    }
    
    
    public void handleBet(int amount) {
        players[actionon].contributeChips(amount);
        System.out.println("bet... player has now contributed " + players[actionon].getContributedThisStreet());

        pots[currentpot].add(amount);
        currentBet = players[actionon].getContributedThisStreet();   // the bet is however much is on the table (which preflop gets screwey)
        origbettor = actionon;
        
        if (amount == players[actionon].getChipStack()) // not actually used
            allinflag = true;
        
        sendUpdatedChipsFor(actionon);    // only include this player's new chip stack in the update
        actionon = getNextPlayerInHandFrom(actionon + 1);
        
        sendMessageToTable("#GenInfo#" + "Player " + actionon + " bets " + amount + ".");
        sendActionRequest(actionon, getToCall());
    }
    
    public void handleCall() {
        if (currentBet >= players[actionon].getChipStack()) {  // if the bet is more than this player can call
            
            pots[currentpot].add(players[actionon].getChipStack());  // add to pot however much more this player had left
            
            sendMessageToTable("#GenInfo#" + "Player " + actionon + " is all in.");
            
            players[actionon].setBothContributed(players[actionon].getContributedThisStreet() + players[actionon].getChipStack());
            players[actionon].setChipStack(0);
            sendUpdatedChipsFor(actionon);
            
            int nextplayer = getNextPlayerInHandFrom(actionon + 1);
            
            if (numWithChips() == 0) {                  // if either no players or only 1 player has chips left, no more betting can occur
                runAllInShowdown();                     // so flip the cards and run the showdown
            }
            
            else {                                          // otherwise, advance the action
                if (nextplayer == origbettor || nextplayer == actionon)
                    dealNextStreet();
                else {
                    actionon = nextplayer;
                    if (getToCall() == 0)
                        dealNextStreet();
                    else
                        sendActionRequest(actionon, getToCall());
                }
            }
            
        }
        else {
            int amountToCall = currentBet - players[actionon].getContributedThisStreet();
          
            players[actionon].contributeChips(amountToCall);
            pots[currentpot].add(amountToCall);   // take away however much more they had to call to match the current bet size
            
            sendMessageToTable("#GenInfo#" + "Player " + actionon + " calls.");
            sendUpdatedChipsFor(actionon);
            
            int nextplayer = getNextPlayerInHandFrom(actionon + 1);
            
            if (currentStreet == Street.PREFLOP) {
                if (actionon == smallblind && players[bigblind].getContributedThisStreet() == bbsize) { // if small blind limps ->>> not true, he could have called a raise from anyone
                    if (amountToCall == sbsize) {        // if the small blind limped in
                        actionon = bigblind;             // then action proceeds to the big blinds
                        sendActionRequest(actionon, 0);  // and they have the option of checking or betting
                    }
                    else {                                                // otherwise, there was a raise at some point
                        actionon = getNextPlayerInHandFrom(actionon + 1); // so the action advances as usual
                        sendActionRequest(actionon, getToCall());
                    }
                }
                else if (getNextPlayerInHandFrom (actionon + 1) == origbettor) {  // if the next player in the hand is the one who opened the betting
                    dealNextStreet(); 
                }
                else {
                    if (nextplayer == actionon)   // will occur if the orig bettor went all in
                        dealNextStreet();
                    else {
                        actionon = getNextPlayerInHandFrom(actionon + 1);        // otherwise, move the action to the next player
                        sendActionRequest(actionon, getToCall());
                    }
                }
            }
            else {
                System.out.println("action on player " + actionon);
                System.out.println("next player " + nextplayer);
                System.out.println("Orig bettor " + origbettor);
                
                if (nextplayer == actionon)    // will occur if the orig bettor went all in
                    dealNextStreet();
                else if (nextplayer == origbettor) {     // if the next player in the hand is the one who opened the betting
                    dealNextStreet();                    // then there is no more betting this round
                }
                else {
                    actionon = nextplayer;               // otherwise, move the action to the next player
                    sendActionRequest(actionon, getToCall());
                }
            }
        }
    }
    
    
    public void handleRaise(int totalAmount) {
        int raiseAmount = totalAmount - players[actionon].getContributedThisStreet();
        System.out.println("action from " + players[actionon].toString() + " who has put in " 
                           + players[actionon].getContributedThisStreet());
                           
        players[actionon].contributeChips(raiseAmount);
        pots[currentpot].add(raiseAmount);
        
        System.out.println("action from " + players[actionon].toString() + " has now put in " 
                           + players[actionon].getContributedThisStreet());

        
        currentBet = totalAmount;                      // and set the raise as the new large bet
        origbettor = actionon;                         // and set the raiser as the initiator of the action
        
        sendMessageToTable("#GenInfo#" + "Player " + actionon + " raises to " + totalAmount + ".");
        sendUpdatedChipsFor(actionon);
        
        actionon = getNextPlayerInHandFrom(actionon + 1);                        // move the action to the next player
        sendActionRequest(actionon, getToCall());
    }
    
    
    public void handleCheck() {
        sendMessageToTable("#GenInfo#" + "Player " + actionon + " checks.");
        
        /* if a player checks preflop, then that player is checking in the big blind
         * if a player checks, and the next player to act has already acted (checked), then this round of betting is over
         * so deal the next street */
       // System.out.println("next player: " + getNextPlayerInHandFrom(actionon + 1));
      //  System.out.println("first after button: " + getNextPlayerInHandFrom(button + 1));
        if (currentStreet == Street.PREFLOP || getNextPlayerInHandFrom(actionon + 1) == getNextPlayerInHandFrom(button + 1)) {
            dealNextStreet();
        }
        else {
            actionon = getNextPlayerInHandFrom(actionon + 1);                        // move the action to the next player
            sendActionRequest(actionon);
        }
    }
    
    /****************************End action handlers***********************************************************************************************************/
        
    // returns the seat number of the player that had the smallest contribution to the pot on this round of betting
    public int getSmallestContribution() {
     //   System.out.println("________get_smallest_contribution_________________________________");
        int smallestamount = 999999999;
        int newamount;
        int seat = -1;
              
        for (int i=0; i<TableSize; i++) {
            if (players[i] != null && players[i].getContributedThisStreet() > 0) {
            //    System.out.println("Player " + i + " has contributed " + players[i].getContributedThisStreet());
                newamount = players[i].getContributedThisStreet();
                if (newamount < smallestamount) {
                    smallestamount = newamount;
                    seat = i;
                }
            }
        }
     //   System.out.println("________get_smallest_contribution_________________________________\n");
        return seat;
    }
    
      
    public int getTotalPotAmount() {
        int total=0;
        for (int i=0; i<pots.length; i++)
            total += pots[i].getPotSize();
        return total;
    }
    
    
    public int getTotalContributedThisStreet() {
        int total = 0;
        for (int i=0; i<TableSize; i++) {
                if (players[i] != null && players[i].getContributedThisStreet() > 0) {
                    total += players[i].getContributedThisStreet();
                }
        }
        return total;
    }
    
    // need to handle the eligibility of pots
    public void consolidateBets(int startseat) {
     //   System.out.println("___________Consolidate_Bets_________________________________");
     //   System.out.println("Total Contributed This Street: " + getTotalContributedThisStreet());
     //   System.out.println("Seat of smallest bettor: " + startseat);
        
        if (getTotalContributedThisStreet() > 0) {  // if no bets are on the table any more, our work here is done
             // alternatively, if startseat != -1
            
            int amount = players[startseat].getContributedThisStreet(); // amount to remove from each contributor
            
            if (players[startseat].getChipStack() == 0) {  // if the smallest contributor to this round of betting is also all in
              //  System.out.println("Adding side pot because minimum bettor is all in");
              //  System.out.println("Before");
              //  printPots();
                
                addSidePot();
              //  System.out.println("Reallocating pots: all in player is in seat " + startseat);
                
                for (int i=0; i<TableSize; i++) {
                    if (players[i] != null && players[i].getContributedThisStreet() > 0) {
                        
                        int remainder = players[i].getContributedThisStreet() - amount;
                        
                      //  System.out.println("Player involved: " + i);
                      //  System.out.println("Contributed this round: " + players[i].getContributedThisStreet());
                      //  System.out.println("Min bet on table: " + amount);
                      //  System.out.println("Remainder: " + remainder);
                        
                        if (remainder > 0) {
                           // printPots();
                          //  System.out.println("++++++++++Adding player " + i + " to list of eligibles for pot " + (currentpot + 1));
                            pots[currentpot + 1].addPlayer(i);     // if they put in more than the min bettor, they contributed to the side pot
                            for (int j=0; j<pots[currentpot + 1].getEligibles().length; j++)
                                System.out.print(pots[currentpot + 1].getEligibles()[j] + ", ");
                         //   System.out.println("\n======");
                          //  printPots();
                        }
                        players[i].setContributedThisStreetNoUpdate(remainder); // remove amount from each player
                        pots[currentpot    ].subtract(remainder);  // reallocate the remainder to the new side pot
                        pots[currentpot + 1].add     (remainder);  // yeah, what I just said 
                      //  System.out.println("Player " + i + " has in pot: " + players[i].getContributedThisStreet()
                      //                       + " and min bettor in seat " + startseat + " has in pot: " + players[startseat].getContributedThisStreet());
                        
                    }
                }
                currentpot++;  // now the current pot is effectively the side pot that was generated
                //System.out.println("After");
               // printPots();
            }
            
            else {
                for (int i=0; i<TableSize; i++) {
                    if (players[i] != null && players[i].getContributedThisStreet() > 0) {
                        int remainder = players[i].getContributedThisStreet() - amount;
                        players[i].setContributedThisStreetNoUpdate(remainder); // remove amount from each player
                    }
                }
            }
            
            consolidateBets(getSmallestContribution());  // recurse
        }
    }
    
    
   
    
    
    
    
    public void runAllInShowdown() {
        
        // Message to players to show their cards if they are in the hand...
        for (int i=0; i<TableSize; i++) {
            if (players[i] != null && players[i].getStatus() == Player.Status.IN_HAND) {
                try { Thread.sleep( 1000 * 2 ); }
                catch (InterruptedException e)  {  System.out.println( "awakened prematurely" );   }
                
                sendMessageToTable("#update#showcards#" + i + "#" + players[i].getHand()[0].toString() + "#" + players[i].getHand()[1].toString() + "#"); 
            }
        }
                
        if (currentStreet == Street.PREFLOP) {        // flop is board[0 - 2]
            dealFlop(false);
            dealTurn(false);
            dealRiver(false);
            doShowDown();
            cleanUp();
        }
        else if (currentStreet == Street.FLOP) {       // turn is board[3]
            dealTurn(false);
            dealRiver(false);
            doShowDown();
            cleanUp();
        }
        else if (currentStreet == Street.TURN) {      // river is board[4]
            dealRiver(false);
            doShowDown();
            cleanUp();
        }
        else {
            doShowDown();
            cleanUp();
        }
    }
        
    
    public void collectPot(int winSeat, int potNum) {
        
        System.out.println("Player in seat " + winSeat + " wins side pot " + potNum + " of " + pots[potNum].getPotSize());
        players[winSeat].addToStack(pots[potNum].getPotSize());
        sendMessageToTable("#GenInfo#" + players[winSeat].toString() + " wins pot of " + pots[potNum].getPotSize() + ".#");
        
        try { Thread.sleep( 1000 * 2 ); }
        catch (InterruptedException e)  {  System.out.println( "awakened prematurely" );   }
        
        sendUpdatedChips();
        
        try { Thread.sleep( 1000 * 2 ); }
        catch (InterruptedException e)  {  System.out.println( "awakened prematurely" );   }
    }
    
    public void assignPots() {
        for (int i=pots.length-1; i >= 0; i--) {
            if (pots[i].getNumEligible() == 0)   // an empty side pot has no eligible players
                removeSidePot();
            else if (pots[i].getWinner() != -1) {
                collectPot(pots[i].getWinner(), i);
                if (i == 0) {        // if main pot is collected
                    System.out.println("Player " + pots[i].getWinner() + " wins pot uncontested");
                    
                    try { Thread.sleep( 1000 * 2 ); }
                    catch (InterruptedException e)  {  System.out.println( "awakened prematurely" );   }
                    
                    cleanUp();  // then start a new hand
                }
            }
            
            else 
                break;
        }
    }
    
    /****************************************Begin Messenging**********************************************************/
    
    public void sendUpdatedChips() {
        String message = "#update#chips#";
        message += getTotalPotAmount() + "#";
        message += numActive + "#";  // number of players to update chip counts for
        for (int i=0; i<TableSize; i++) {
            if (players[i] != null) {
                message += i + "#" + players[i].getChipStack() + "#";
            }
        }
        sendMessageToTable(message);
        sendUpdatedContributions();
    }
    
    /* sends the updated chip count for one player only */
    public void sendUpdatedChipsFor(int seat) {
        String message = "#update#chips#";
        message += getTotalPotAmount() + "#";
        message += "1" + "#";              // only updating chip counts for player at seat seat
        message += seat + "#" + players[seat].getChipStack();
        message += "#";
        sendMessageToTable(message);
        sendUpdatedContributions();
    }
    
    public void sendUpdatedContributions() {
        String message = "#update#contributed#";
        message += numActive + "#";              // number of players to update amounts for
        for (int i=0; i<TableSize; i++) {
            if (players[i] != null) {
                message += i + "#" + players[i].getContributedThisStreet() + "#";
            }
        }
        sendMessageToTable(message);
    }
    
    public void sendUpdatedBoard() {
        String message = "#update#board#";
        for (int i=0; i<board.length; i++) {
            if (board[i] != null)
                message += board[i].toString() + "#";
            else
                message += "___#";
        }
        sendMessageToTable(message);
    }
        
    public void sendUpdatedBlinds() {
        String message = "#update#blinds#";
        message += currentLevel.getSmallBlind() + "#";
        message += currentLevel.getBigBlind() + "#";
        sendMessageToTable(message);                                                                 // let the table know the blinds are up
        String newWindowTitle = getTableWindowTitle(tourneyName, tableID, sbsize, bbsize, gameType);
        sendMessageToTable("#update#windowTitle#" + newWindowTitle + "#");                           // and update their window titles to reflect it
    }
    
    public void sendMessageToTable(String message) {
        for (int i=0; i<players.length; i++) {
            if (players[i] != null)
                Messenger.sendMessage(players[i].getConnection(), message);
        }
    }
    
    /* sends a message to everyone at the table EXCEPT for the player at seat exclude */
    public void sendExclusiveMessageToTable(String message, int exclude) {
        for (int i=0; i<players.length; i++) {
            if (players[i] != null && i != exclude)
                Messenger.sendMessage(players[i].getConnection(), message);
        }
    }
    
    /* Tells the player whose turn it is that the action is on them
     * Tells the rest of the table that the action is on that player
     * In future, this will need to send the player's potential options, and the amount to call if a bet is posed */
    public void sendActionRequest(int seat) {
        Messenger.sendMessage(players[seat].getConnection(), "#getaction#" + getBettingOptions(0, players[seat].getChipStack() + players[seat].getContributedThisStreet()));
        sendExclusiveMessageToTable("#update#actionon#" + seat + "#", seat);
    }

    public void sendActionRequest(int seat, int toCall) {
        Messenger.sendMessage(players[seat].getConnection(), "#getaction#" + getBettingOptions(toCall, players[seat].getChipStack() + players[seat].getContributedThisStreet()));
        sendExclusiveMessageToTable("#update#actionon#" + seat + "#", seat);
    }
    
    /****************************************End Messenging***********************************************************************************************/
    
    public int Least(int x, int y) {
        if (x < y)
            return x;
        return y;
    }
    
    public String getBettingOptions(int betAmount, int stackSize) {
        if (betAmount == 0)
            return "check#bet#";
        else 
            return "fold#call#" + betAmount + "#raise#" + Least(2*currentBet, stackSize) + "#";
    }
            
    public String getInfo(int seatnum) {
        return "#" + tourneyID + "#" + tableID + "#" + seatnum + "#";
    }
    public String getInfo(int seatnum, String username) {
        return "#" + tourneyID + "#" + tableID + "#" + seatnum + "#" + username + "#";
    }
    
    public void dealCards() {
        for (int i=0; i<TableSize; i++) {
            if (players[i] != null && players[i].getChipStack() > 0) {
                players[i].setStatus(Player.Status.IN_HAND);
                Card card1 = deck.dealCard();
                Card card2 = deck.dealCard();
                players[i].setHand(card1, card2);
                pots[0].addPlayer(i);  // any player that receives cards is technically eligible to win the main pot
                Messenger.sendMessage(players[i].getConnection(), "#update#hand#" + card1.toString() + "#" + card2.toString() + "#"); // tell player i his cards
                sendExclusiveMessageToTable("#update#handdealt#" + i + "#", i); // let everyone know that player i received cards
            }
        }

    }
    
    public int getFirstToAct() {
        if (currentStreet == Street.PREFLOP) {
            if (numActive == 2 || numActive == 3)
                return button;
            else 
                return getFirstActivePlayerIndexFrom(bigblind + 1);
        }
        else
            return getFirstActivePlayerIndexFrom(button + 1);
    }
    
    
    public void shuffleDeck() {
        deck.shuffle();
    }
    
    
    public void clearBoard() {
        for (int i=0; i<board.length; i++)
            board[i] = null;
    }
    
    
    /* removes the bets from the table, so that all players have 0 contributed on the current street */
    public void clearBets() {
        for (int i=0; i<TableSize; i++) {
            if (players[i] != null)
                players[i].setContributedThisStreetNoUpdate(0);
        }
        sendUpdatedContributions();
    }
    
    public void dealNextStreet() {
        System.out.println("=====================================End of Betting Round ==============================");
        System.out.println("Num players with chips: " + numWithChips());
        System.out.println("Num players in hand: " + getNumInHand());
        
        consolidateBets(getSmallestContribution());
        assignPots();
        clearBets();
        
        if (( numWithChips() == 1 || numWithChips() == 0) && getNumInHand() > 1) {
            System.out.println("--------------------Running All in-------------------------");
            runAllInShowdown();
        }
        else {
            if (currentStreet == Street.PREFLOP)         // flop is board[0 - 2]
                dealFlop(true);
            else if (currentStreet == Street.FLOP)       // turn is board[3]
                dealTurn(true);
            else if (currentStreet == Street.TURN)       // river is board[4]
                dealRiver(true); 
            else {
                doShowDown();
                cleanUp();
            }
        }
    }
    
    
    public void dealFlop(boolean action) {
        for (int i=0; i<3; i++)
            board[i] = deck.dealCard();
        sendUpdatedBoard();                               // let everyone know the next street was dealt
        currentBet = 0;                                   // restart the betting
        currentStreet = Street.FLOP;
        if (action) {
            actionon = getNextPlayerInHandFrom(button + 1);   // set the action on the first active player in the hand after the button
            sendActionRequest(actionon);                      // request an action from that player
        }
        else {
            try { Thread.sleep( 1000 * 2 ); }
            catch (InterruptedException e)  {  System.out.println( "awakened prematurely" );   }
        }
    }
    public void dealTurn(boolean action) {
        board[3] = deck.dealCard();
        sendUpdatedBoard();
        currentBet = 0;
        currentStreet = Street.TURN;
        if (action) {
            actionon = getNextPlayerInHandFrom(button + 1);   // set the action on the first active player in the hand after the button
            sendActionRequest(actionon);                      // request an action from that player
        }
        else {
            try { Thread.sleep( 1000 * 2 ); }
            catch (InterruptedException e)  {  System.out.println( "awakened prematurely" );   }
        }
    }
    public void dealRiver(boolean action) {
        board[4] = deck.dealCard();
        sendUpdatedBoard();
        currentBet = 0;
        currentStreet = Street.RIVER;
        if (action) {
            actionon = getNextPlayerInHandFrom(button + 1);   // set the action on the first active player in the hand after the button
            sendActionRequest(actionon);                      // request an action from that player
        }
        else {
            try { Thread.sleep( 1000 * 2 ); }
            catch (InterruptedException e)  {  System.out.println( "awakened prematurely" );   }
        }
    }
    
    // evaluates the hands shown down and assigns the pots to the winners accordingly
    public void doShowDown() {
        HandEvaluator handEval = new HandEvaluator();
        System.out.println("===============Running showdown==================");
        for (int potNum = currentpot; potNum >= 0; potNum--) {
            System.out.println("Pot number: " + potNum);
            System.out.print("Eligible players for pot: ");
         /*   for (int i=0; i<pots[potNum].getEligibles().length; i++) {
                if (pots[potNum].getEligibles()[i] != -1)
                    System.out.print(pots[potNum].getEligibles()[i] + ", ");
            } */
            System.out.println();
            
            int bestHandRank = -1;
            int winningSeat = -1;
            int chopFlag = 1;
            for (int i=0; i<TableSize; i++) {
                if (players[i] != null && pots[potNum].isEligible(i)) {
                    
                    sendMessageToTable("#update#showcards#" + i + "#" + players[i].getHand()[0].toString() + "#" + players[i].getHand()[1].toString() + "#"); 
                    try { Thread.sleep( 1000 * 3 ); }
                    catch (InterruptedException e)  {  System.out.println( "awakened prematurely" );   }
                    
                    int handRank = handEval.rankHand(getHand(i));
                    if (handRank > bestHandRank) {
                        bestHandRank = handRank;
                        winningSeat = i;
                    }
                }
            }
            
            
            sendMessageToTable("#GenInfo#" + players[winningSeat].toString() + " wins pot of " + pots[potNum].getPotSize() + ".#");
            try { Thread.sleep( 1000 * 2 ); }
            catch (InterruptedException e)  {  System.out.println( "awakened prematurely" );   }
            collectPot(winningSeat, potNum);
        }
    }
        
    
    public Hand getHand(int seat) {
        String hand = "";
        for (int i=0; i<5; i++) {
            hand += board[i].toString() + " ";
        }
        hand += players[seat].getHand()[0].toString() + " ";
        hand += players[seat].getHand()[1].toString();
        return new Hand(hand);
    }
                
    
     public void displayTable(int show) {

        System.out.println("Community Cards: ");
        displayBoard();
        System.out.println("Current pot: " + getTotalPotAmount());
        
        for (int i=0; i<TableSize; i++) {
            System.out.print("Seat " + i + " ");
            if (players[i] != null) {
                System.out.print(players[i].toString() + " is holding " + players[i].displayCards());
                if (i == button)
                    System.out.print(" and is on the button ");
                else if (i == smallblind)
                    System.out.print(" and is in the small blind ");
                else if (i == bigblind)
                    System.out.print(" and is in the big blind ");
                System.out.println("and has contributed " + players[i].getContributedThisStreet() + " chips to the pot.");
            }
            else
                System.out.println("is empty");
        }
        System.out.println();
    }
    
    
    
    public void displayBoard() {
        if (board == null)
            System.out.println("No Community cards showing");
        else {
            for (int i=0; i<board.length; i++) {
                if (board[i] != null)
                    System.out.print(board[i].toString() + " ");
            }
        }
        System.out.println();
    }
    
    public int getFirstActivePlayerIndex() {
        for (int i=0; i<TableSize; i++) {
            if (players[i] != null)
                return i;
        }
        return -1;
    }
    
    public int getFirstActivePlayerIndexFrom(int start) {
        for (int i=start; i<TableSize; i++) {   // check to the end
            if (players[i] != null)
                return i;
        }
        for (int i=0; i<start; i++) {            // wrap around
            if (players[i] != null)
                return i;
        }
        return -1;
    }
    
    public int getNextPlayerInHandFrom(int current) {
        if (current >= TableSize)
            current = 0;
        for (int i=current; i<TableSize; i++) {   // check to the end
            if (players[i] != null && players[i].getStatus() == Player.Status.IN_HAND && players[i].getChipStack() > 0)
                return i;
        }
        for (int i=0; i<current; i++) {            // wrap around
            if (players[i] != null && players[i].getStatus() == Player.Status.IN_HAND && players[i].getChipStack() > 0)
                return i;
        }
        return -1;
    }
    
    public Player getPlayer(int seatNum) {
        return players[seatNum];
    }
    
    public void setPlayer(Player player, int seatNum) {
        players[seatNum] = player;
        calcNumActive();      
    }
    
    public void setNumActive(int numActive) {
        this.numActive = numActive;
    }
    public int getNumActive() {
        return numActive;
    }
    public int calcNumActive() {
        int active = 0;
        for (int i=0; i<TableSize; i++) {
            if (players[i] != null)
                active++;
        }
        numActive = active;
        return numActive;
    }
    public int getNumEmpty() {
        return TableSize - numActive;
    }
    
    public static Table[] addTable(Table[] Tables, int TableSize) {
        Table[] newTables = new Table[Tables.length + 1];
        for (int i=0; i<Tables.length; i++)
            newTables[i] = Tables[i];
        newTables[newTables.length - 1] = new Table(TableSize, tourneyID, newTables.length - 1, tourneyName, gameType, payouts);
        return newTables;
    }
    public int getTableSize() {
        return TableSize;
    }
    
    public void setBlindLevel(BlindLevel level) {
        this.currentLevel = level;
    }
    
}
