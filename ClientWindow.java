import javax.swing.*;
import java.awt.*;
import java.lang.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;


public class ClientWindow extends JFrame {
    
    private JFrame frame;
    
    private JPanel[] playerPanels;
    private PlayerCardPanel[] playerCardPanels;
    private PlayerInfoPanel[] playerInfoPanels;
    private ContributedChipPanel[] contributedChips;
    private JPanel[] tableBets;
    
    private JPanel centerPanel;
    private CardPanel cardPanel;
    private PotSizePanel potSizePanel;
    
    private SliderPanel sliderPanel;
    private ButtonPanel buttonPanel;
    
    private ChatBox box;
    private JTextField textField;
    
    private String windowTitle;
    
    private int [] xCoor = {138, 350, 557, 708, 714, 577, 264, 20  , 6  };
    private int [] yCoor = {20 , 0  , 18 , 122, 285, 385, 415, 315, 142};
    private int [] betx = {210, 349, 500, 605, 595, 560, 400, 132, 135};
    private int [] bety = {162, 130, 140, 205, 305, 360, 370, 290, 209};
    
    private int TableSize = 9;
    private int currentButton;
    
    public ClientWindow(final ActionListener listener) {
        frame = new JFrame();
        
        /* create background image */
        JPanel    backgroundPanel = new JPanel(null);
        ImageIcon backgroundImage = createImageIcon("BackgroundFullTable.png");
        JLabel background = new JLabel(backgroundImage);
        
        background.setBounds(0, 0, backgroundImage.getIconWidth(), backgroundImage.getIconHeight());
        backgroundPanel.setPreferredSize(new Dimension(backgroundImage.getIconWidth(), backgroundImage.getIconHeight()));

        frame.getLayeredPane().add(background, new Integer(Integer.MIN_VALUE));
        backgroundPanel.setOpaque(false);
        frame.setContentPane(backgroundPanel);
        /* ok it's there */
        
        /* initialize player panels */
        playerPanels = new JPanel[TableSize];
        playerCardPanels = new PlayerCardPanel[TableSize];
        playerInfoPanels = new PlayerInfoPanel[TableSize];
        
        for (int i=0; i<playerPanels.length; i++) {
            playerPanels[i] = new JPanel(new BorderLayout());
            playerPanels[i].setOpaque(false);
            
            playerCardPanels[i] = new PlayerCardPanel();
            playerInfoPanels[i] = new PlayerInfoPanel();
            
            playerPanels[i].add(playerCardPanels[i], BorderLayout.PAGE_START);
            playerPanels[i].add(playerInfoPanels[i], BorderLayout.CENTER);
            
            playerPanels[i].setBounds(xCoor[i], yCoor[i], 120, 140);
            frame.getContentPane().add(playerPanels[i]);
        }
        
        contributedChips = new ContributedChipPanel[TableSize];
        
        for (int i=0; i<contributedChips.length; i++) {
            contributedChips[i] = new ContributedChipPanel();
            contributedChips[i].setBounds(betx[i], bety[i], 100, 45);  // 60, 35
            frame.getContentPane().add(contributedChips[i]);
        }
        
        centerPanel = new JPanel();
        centerPanel.setOpaque(false);
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        cardPanel = new CardPanel();
        potSizePanel = new PotSizePanel();
       
        centerPanel.setBounds(200, 200, 400, 130);
        
        centerPanel.add(cardPanel);
        centerPanel.add(potSizePanel);
        
        frame.getContentPane().add(centerPanel); 
                
        sliderPanel = new SliderPanel();
        buttonPanel = new ButtonPanel(listener);
        sliderPanel.setRaiseButton(buttonPanel.getButton(2));
        
        frame.getContentPane().add(sliderPanel);
        frame.getContentPane().add(buttonPanel);
        
        /* Do stuff to add the chatbox */
        box = new ChatBox();
        box.setBounds(5, 485, 200, 100);
        frame.getContentPane().add(box);
        
        textField = new JTextField(20);
        textField.addActionListener(listener);
        textField.setBounds(5, 585, 200, 30);
        frame.getContentPane().add(textField);


        
        frame.pack();
        frame.setVisible(true);
    }
    
    public ImageIcon createImageIcon(String path) {
        String imagePath = "Poker Table Art\\" + path;
        java.net.URL imgURL = getClass().getResource(imagePath);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("Couldn't find file: " + imagePath);
            return null;
        }
    }
    
    public void setWindowTitle(String title) {
        frame.setTitle(title);
    }
    public String getWindowTitle() {
        return frame.getTitle();
    }
    
    public void updateCard(String card, int cardNum) {
        cardPanel.setCard(createImageIcon(card), cardNum);
    }
    public void clearCard(int cardNum) {
        cardPanel.clearCard(cardNum);
    }
        
    public void updateFlop(String card1, String card2, String card3) {
        updateCard(card1, 0);
        updateCard(card2, 1);
        updateCard(card3, 2);
    }
    
    public void updateTurn(String card) {
       updateCard(card, 3);
    }
    public void updateRiver(String card) {
        updateCard(card, 4);
    }
    
    public void clearBoard() {
        cardPanel.clearBoard();
    }
    public void updatePotSize(int size) {
        potSizePanel.setPotSize(size);
    }
    
    public void updateContributed(int seat, int amount) {
        contributedChips[seat].setText(amount);
    }
    public void removeContributedChips(int seat) {
        contributedChips[seat].hidePanel();
    }
    public void showButton(int buttonSeat) {
        contributedChips[currentButton].hideButton();
        contributedChips[buttonSeat].showButton();
        currentButton = buttonSeat;
    }
    
    
    
    public void updatePlayerCard(int seat, String filename, int cardNum) {
        playerCardPanels[seat].setCard(filename, cardNum);
    }
    public void hidePlayerCards(int seat) {
        playerCardPanels[seat].setCard("cards\\cardback.gif", 0);
        playerCardPanels[seat].setCard("cards\\cardback.gif", 1);
    }
    public void removePlayerCards(int seat) {
        playerCardPanels[seat].removeCard(0);
        playerCardPanels[seat].removeCard(1);
    }
    
    public void updatePlayerName(int seat, String name) {
        playerInfoPanels[seat].setPlayerName(name);
    }
    
    public void updatePlayerChips(int seat, int chips) {
        playerInfoPanels[seat].setPlayerChips(chips);
    }
    
    
    public void setAllButtons(String message1, String message2, String message3) {
        buttonPanel.setAll(message1, message2, message3);
    }
    public void setAllButtons(String message1, String message2) {
        buttonPanel.setAll(message1, message2);
    }
    public void clearButtonPanel() {
        sliderPanel.hideSlider();
        buttonPanel.hideAll();
    }
    
    public JButton getButton(int buttonNum) {
        return buttonPanel.getButton(buttonNum);
    }
    
    public void setSlider(int min, int max) {
        sliderPanel.setSlider(min, max);
    }
    
    public void addMessage(String speaker, String message) {
        box.addMessage(speaker, message);
    }
    public void clearTextField() {
        textField.setText("");
    }
    
    
}
        
        
        
        
        