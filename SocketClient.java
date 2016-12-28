import java.net.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class SocketClient {
    
    /* Client's player properties */
    private static int myTourneyID;
    private static int myTableID;
    private static int mySeatID;
    private static int TableSize;
    private static String myName;
    private static String[] myCards;  // my two hole cards
    private static Boolean loggedin;
    private static Player.Status status;
    
    /* Properties of the table that the client is playing at */
    private static String[] board;
    private static int pot;
    private static int actionon;
    private static int button;     // seat number of button
    private static int smallblind; // seat number of small blind
    private static int bigblind;   // seat number of big blind
    private static int sbsize;
    private static int bbsize;
    private static Player[] players;  // an array of the players at the table; client will only have basic information available to them
    private static Boolean[] cardsVisible;  // maintains whether a player's cards are showing or not
    
    private static Socket connection;
    private static OutputStreamWriter osw;
    private static Control control;
    
    /* updates the properties of the table and/or this player */
    public static void handleUpdate(String[] info) {
        String updateType = info[1];  // the type of update to be performed (chips, board, ...)
        
        /* Update Chips message encoded as #update#chips#pot#NumToUpdate#Seat#chips#seat#chips#...# */
        if (updateType.equals("chips")) {
            pot = Integer.parseInt(info[2]);
            int numToUpdate = Integer.parseInt(info[3]);
            int i = 0;
            while (numToUpdate > 0) {
                if (players[Integer.parseInt(info[i*2 + 4])] != null) {
                    players[Integer.parseInt(info[i*2 + 4])].setChipStack(Integer.parseInt(info[i*2 + 5]));
                    numToUpdate--;
                }
                i++;
            }
        }
        
        /* Update contributed message encoded as #update#contributed#NumToUpdate#Seat#contr#seat#contr#...# */
        else if (updateType.equals("contributed")) {
            int numToUpdate = Integer.parseInt(info[2]);
            int i = 0;
            while (numToUpdate > 0) {
                if (players[Integer.parseInt(info[i*2 + 3])] != null) {
                    players[Integer.parseInt(info[i*2 + 3])].setContributedThisStreetNoUpdate(Integer.parseInt(info[i*2 + 4]));
                    numToUpdate--;
                }
                i++;
            }
        }
        
        else if(updateType.equals("showcards")) {
            cardsVisible[mySeatID] = true;
        }
        else if(updateType.equals("hidecards")) {
            cardsVisible[mySeatID] = false;
        }
        else if(updateType.equals("removecards")) {
            myCards[0] = "";
            myCards[1] = "";
        }
        
        
        else if (updateType.equals("knockout")) {
            int seat =   Integer.parseInt(info[2]);
            int place =  Integer.parseInt(info[3]);
            int reward = Integer.parseInt(info[4]);
            System.out.print("Player " + players[seat].getUserName() + " is knocked out in " + place + "th  place");
            if (reward > 0)
                System.out.println(" and wins " + reward);
            else
                System.out.println(".");
            players[seat].setStatus(Player.Status.WATCHING_TABLE);
        }
        
        else if (updateType.equals("win")) {
            int seat = Integer.parseInt(info[2]);
            int reward = Integer.parseInt(info[3]);
            System.out.println("Player " + players[seat].getUserName() + " has won the tournament.");
            System.out.println(players[seat].getUserName() + " wins " + reward + ".");
        }
        
            
        /* Update board message encoded as #update#board#card1#card2#...#card5#, blank cards included as ___ */
        else if (updateType.equals("board")) {
            for (int i=0; i<board.length; i++) {
                board[i] = info[i + 2];
            }
        }
        else if (updateType.equals("hand")) {
            myCards[0] = info[2];
            myCards[1] = info[3];
            status = Player.Status.IN_HAND;
            players[mySeatID].setStatus(Player.Status.IN_HAND);
        }
        else if (updateType.equals("actionon")) {
            actionon = Integer.parseInt(info[2]);
            System.out.println("Action is on player in seat " + actionon);
        }
        
        else if (updateType.equals("check")) {  // server has said that the action is on you, and this is your set of options
            actionon = mySeatID;  // the action is on me
            System.out.println("Check or Bet");
        }
        else if (updateType.equals("fold")) {
            actionon = mySeatID; // the action is one me
            System.out.println("Fold, Call " + info[3] + ", or Raise " + info[5]);
        }

        else if (updateType.equals("TableSize")) {
            TableSize = Integer.parseInt(info[2]);
            players = new Player[TableSize];
          //  System.out.println("Updated the table size to " + TableSize + " and uhh yeah " + players[0].toString());
        }
        
        else if (updateType.equals("players")) {
            for (int i=0; i<(info.length-2) / 3; i++) {
                int seat = Integer.parseInt(info[i*3 + 2]);
                players[seat] = new Player(info[i*3 + 3], Player.Status.AT_TABLE); 
                players[seat].setSeatNum(seat);
                players[seat].setChipStack(Integer.parseInt(info[i*3 + 4]));
            }
        }
        else if (updateType.equals("loggedin")) {
            if (info[2].equals("loggedin"))
                loggedin = true;
            else
                loggedin = false;
        }
        
        /* Update pot message encoded as #update#pot#new pot amount# */
        else if (updateType.equals("pot")) {
            pot = Integer.parseInt(info[2]);
        }
         /* Update button message encoded as #update#button#new button location# */
        else if (updateType.equals("button")) {
            button = Integer.parseInt(info[2]);
        }
        
        else if (updateType.equals("many")) {
            for (int i=0; i<Integer.parseInt(info[2]); i++) {
                String var = info[i*2 + 3];
                int value = Integer.parseInt(info[i*2 + 4]);
                if (var.equals("tourneyID"))
                    myTourneyID = value;
                else if (var.equals("tableID"))
                    myTableID = value;
                else if (var.equals("seat"))
                    mySeatID = value;
                else if (var.equals("TableSize"))
                    TableSize = value;
                else
                    System.out.println("Unrecognized update");
            }
        }
        
         /* Update pot message encoded as #update#pot#new pot amount# */
        else if (updateType.equals("blinds")) {
            sbsize = Integer.parseInt(info[2]);
            bbsize = Integer.parseInt(info[3]);
        }
        else if (updateType.equals("post")) {
            if (info[2].equals("smallblind")) {
                smallblind = Integer.parseInt(info[3]);
            }
            else if (info[2].equals("bigblind")) {
                bigblind = Integer.parseInt(info[3]);
            }
            else  // add ante functionality here
                System.out.println("Bad request");
        }
        else
            System.out.println("BAD REQUEST!!!!!!!!!!!!!!!!!!!!!!!!!!!!\n\n\n\n!!!!!!");
    }
    
    public static void updateDisplay() {
        
        System.out.println("Community Cards:                     ");
        for (int i=0; i<board.length; i++) {
            if (board[i] != null)
                System.out.print(board[i] + " ");
            else
                System.out.print("___ ");
        }
        
        System.out.println("\nCurrent pot: " + pot + "\n");
        
        for (int i=0; i<TableSize; i++) {
            if (players[i] != null) {
                System.out.println(players[i].toString() + " has " + players[i].getContributedThisStreet() + " on the table.");
                System.out.print("Seat " + i + ": ");
                System.out.print(players[i].toString() + "   " + players[i].getChipStack() + " chips");
                if (status == Player.Status.IN_HAND)
                    System.out.print(" IN ");
                else
                    System.out.print(" OUT ");
                if (i == button)
                    System.out.print(" button ");
                if (i == smallblind)
                    System.out.print(" small blind ");
                else if (i == bigblind)
                    System.out.print(" big blind ");
                if (i == actionon)  // if the action is on this player
                    System.out.print("        <---------");
                if (i == mySeatID && i == actionon)  // if displaying my info, and the action is on me
                    System.out.print(" YOU");
                System.out.println();
                System.out.println();
                //System.out.println("and has contributed " + contributed[i] + " chips to the pot.");
            }
            else
               ; //System.out.println("is empty");
        }
        if (myCards[0] != null)
            System.out.println(players[mySeatID].toString() + ": You are holding " + myCards[0].toString() + " " + myCards[1].toString());
        System.out.println();
    }
 
    
    public static void main(String[] args) {
  
        String host = "localhost";
        int port = 19999;
        
        
        /* Initialize the poker related stuff */
        loggedin = false;
        board = new String[5];
        myCards = new String[2];
        
        try {
            /** Obtain an address object of the server */
            InetAddress address = InetAddress.getByName(host); // returns the host name and their ip address as a pair
            
            /** Establish a socket connetion */
            connection = new Socket(address, port);
            System.out.println("Connection initialized");
            
            /** Instantiate a BufferedOutputStream object */
            BufferedOutputStream bos = new BufferedOutputStream(connection.getOutputStream());
            
            /** Instantiate an OutputStreamWriter object with the optional character encoding.  */
            osw = new OutputStreamWriter(bos, "US-ASCII");
            
            //String process = "Calling the Socket Server on " + host + " port " + port + "at " + TimeStamp +  (char) 13; // char 13 signifies the end of out data
            // BufferedInputStream is = new BufferedInputStream(System.in);
            // InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            int character;
            
            
            BufferedInputStream bis = new BufferedInputStream(connection.getInputStream());
            InputStreamReader isr2 = new InputStreamReader(bis, "US-ASCII");
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            
            control = new Control();
            
            
            

            Boolean done = false;
            String message = "";
            Scanner scanner = new Scanner(System.in);
            
            while (!done) {
                
                message = scanner.nextLine();
                if (message.contains("quit"))
                    break;
                
                message += myTourneyID + "#" + myTableID + "#" + mySeatID + "#";
                message += (char) 13;
                osw.write(message);
                osw.flush();
          
             //  LoginWindow lw = new LoginWindow(connection);
                
                
                
                String action = "";
                while (!action.contains("getaction")) {
                    int c;
                    // StringBuffer response = new StringBuffer();
                    String reply = in.readLine();
                    /*    while ( (c = isr2.read()) != 13)  {   // wait for the server response
                     response.append( (char) c);
                     } */
                    
                    if (reply.length() > 1) {
                        System.out.println("Server response: " + reply.toString());
                        String[] info = StringAnalyzer.getWords(reply.toString());
                        action = info[0];
                        
                        
                        /* handle any messages that the server might return */
                        if (action.equals("update")) {
                            handleUpdate(info);
                            if (info[1].equals("chips") || info[1].equals("hand") || info[1].equals("actionon"))
                                updateDisplay();
                        }
                        else if (action.equals("GenInfo"))
                            System.out.println("Dealer: " + info[1]);
                        else if (action.equals("getaction")) {
                            handleUpdate(info);
                            updateDisplay();
                            System.out.println("The action is on you." + " You are holding " + myCards[0].toString() + " " + myCards[1].toString());
                        }
                        else
                            System.out.println("Cannot make sense of action: " + action);  // in case i forgot something
                    }
                    System.out.println();
                }
                
            }
            osw.write("quit");
            osw.flush();
            System.out.println("quitter");
            
            /** Close the socket connection. */
            connection.close();
        }
        catch (IOException f) {
            System.out.println("IOException: " + f);
        }
        
        
       /* catch (Exception g) {
            System.out.println("Exception: " + g);
        } */
    }
        
    
}


/*
class Control implements ActionListener {
    
    public Control() {
        ClientWindow clientWindow = new ClientWindow(this);
        clientWindow.setAllButtons("Fold", "Call 200", "Raise 400");
    }
    
    public void actionPerformed(ActionEvent e) {
        JButton temp = (JButton) e.getSource();
        if (temp.getText().contains("Fold"))
            System.out.println("You hit fold");
        Messenger.sendMessage(connection, osw, "a button was hit"); 
    }
}
*/
