import java.net.*;
import java.io.*;
import java.util.*;


public class MultipleSocketServer implements Runnable {  // for use in threading
    // could also extend the Thread class, which has more methods, but we only really need run()
    
    private Socket connection;
    private String TimeStamp;
    private int ID;
    private static String UserFile;
    private static Tournament[] Tournaments;
    
    
    public static void main(String[] args) {
        UserFile = "users.txt";
        int port = 19999;
        int count = 0;
        Tournaments = new Tournament[1];
        int tourneyID = 0;
        int startStack = 1500;
        int TableSize = 6;
        BlindLevel[] blinds = BlindLevel.getNormalBlindStructure();
        int[] payouts = Payouts.getPayouts(9, 100);
        String tourneyName = "The Big Bluk Invitational";
        String gameType = "No Limit Hold Em";
        
        Tournaments[0] = new Tournament(tourneyID, startStack, TableSize, blinds, payouts, tourneyName, gameType);
        
        try {
            ServerSocket socket1 = new ServerSocket(port);
            System.out.println("MultipleSocketServer Initialized");
            while (true) {
                Socket connection = socket1.accept();
                Runnable runnable = new MultipleSocketServer(connection, ++count);
                Thread thread = new Thread(runnable);
                thread.start();
            }
        }
        catch (Exception e) {}
    }
    MultipleSocketServer(Socket s, int i) {
        this.connection = s;
        this.ID = i;
    }
    
    public Boolean approveLogin(String username, String password) {
        return Logging.approveUser(username, password, UserFile);
    }
    
    public void forwardMessageToTable(String[] info) {
        int tourneyID = -1;
        int tableID = -1;
        String action = info[0];
        if (action.equals("call")) {
            tourneyID = Integer.parseInt(info[1]);
            tableID = Integer.parseInt(info[2]);
        }
        else if(action.equals("bet")) {
            tourneyID = Integer.parseInt(info[2]);
            tableID = Integer.parseInt(info[3]);
        }
        else if(action.equals("raise")) {
            tourneyID = Integer.parseInt(info[2]);
            tableID = Integer.parseInt(info[3]);
        }
        else if(action.equals("check")) {
            tourneyID = Integer.parseInt(info[1]);
            tableID = Integer.parseInt(info[2]);
        }
        else if(action.equals("fold")) {
            tourneyID = Integer.parseInt(info[1]);
            tableID = Integer.parseInt(info[2]);
        }
        else if(action.equals("chatbox")) {
            tourneyID = Integer.parseInt(info[3]);
            tableID = Integer.parseInt(info[4]);
        }
        else
            System.out.println("Then why did you call this function?");
        
        Tournaments[tourneyID].getTables()[tableID].handleAction(info);  // pass the message on to the proper tournament and table, let them deal with it
    }
    
    
    /* The code for the thread that runs for each connection */
    public void run() {
        try {
            Player player;
            String username = "";
            Boolean loggedin = false;
            BufferedInputStream is = new BufferedInputStream(connection.getInputStream());
            InputStreamReader isr = new InputStreamReader(is);
            
            
            
            int character;
            
            Boolean done = false;
            
            while (!done) {
                StringBuffer process = new StringBuffer();
                while((character = isr.read()) != 13) {
                    process.append((char)character);
                } 
                String[] info = StringAnalyzer.getWords(process.toString()); // store each individual String in an entry of the info array
                System.out.println ("String read: " + process.toString());
                
                if (info[0].equals("login")) {
                    Boolean loginsuccess = approveLogin(info[1], info[2]);
                    System.out.println(loginsuccess.toString());
                    if (loginsuccess) {
                        System.out.println("Login Success!");
                        username = info[1];
                        loggedin = true;
                        player = new Player(info[1], Player.Status.AT_LOBBY);
                        player.setConnection(connection);
                        Tournaments[0].registerPlayer(player);
                        Messenger.sendMessage(connection, "#update#loggedin#success#" + username + "#");
                    }
                }
                else if (info[0].equals("register")) {
                    if (loggedin)
                        Tournaments[Integer.parseInt(info[1])].registerPlayer(new Player(info[2], Player.Status.AT_LOBBY));
                    else {
                        Messenger.sendMessage(connection, "Cannot register: please log in first.");
                    }
                }
                else if (info[0].equals("seatplayers"))
                    Tournaments[Integer.parseInt(info[1])].seatRegisteredPlayers();
                else if (info[0].equals("test")) {
                    for (int i=0; i<0; i++) {
                        Tournaments[0].registerPlayer(Tester.genRandomPlayer());
                    }
                    Tournaments[0].seatRegisteredPlayers(); System.out.println();
                    Tournaments[0].startTournament();
                    System.out.println("Test complete.");
                }
                else if (info[0].equals("showcards")) {
                    System.out.println("action: " + info[0] + " tourney: " + info[1] + " table: " + info[2] + " seat: " + info[3]); 
                    String cards = Tournaments[Integer.parseInt(info[1])].getTables()[Integer.parseInt(info[2])].getPlayer(Integer.parseInt(info[3])).getHand()[0].toString() + " " +
                        Tournaments[Integer.parseInt(info[1])].getTables()[Integer.parseInt(info[2])].getPlayer(Integer.parseInt(info[3])).getHand()[1].toString();
                    System.out.println(cards.toString());
                    Messenger.sendMessage(connection, cards);          
                }
                
                
                else if(info[0].equals("call"))
                    forwardMessageToTable(info);
                else if(info[0].equals("bet"))
                    forwardMessageToTable(info);
                else if(info[0].equals("raise"))
                    forwardMessageToTable(info);
                else if(info[0].equals("fold"))
                    forwardMessageToTable(info);
                else if(info[0].equals("check"))
                    forwardMessageToTable(info);
                else if (info[0].equals("chatbox"))
                    forwardMessageToTable(info);
                else if (info[0].equals("quit"))
                    done = true;
                else
                    System.out.println("I do not copy");
                
                
                
            }
        }
        catch (IOException e) {
            System.out.println("Error \n" + e);
        }
        finally {
            try {
                System.out.println("Closing connection");
                TimeStamp = new java.util.Date().toString();
                String returnCode = "MultipleSocketServer repsonded at "+ TimeStamp + (char) 13;
                BufferedOutputStream os = new BufferedOutputStream(connection.getOutputStream());
                OutputStreamWriter osw = new OutputStreamWriter(os, "US-ASCII");
                osw.write(returnCode);
                osw.flush();
                connection.close();
            }
            catch (IOException e){}
        }
    }
}

