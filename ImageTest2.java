import javax.swing.*;
import java.awt.*;

import java.awt.BorderLayout;
import java.awt.Container;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

public class ImageTest2 {

    
    
    
    public static void main(String[] args) {
        JFrame frame = new JFrame();
        
        JPanel    backgroundPanel = new JPanel(null);
        ImageIcon backgroundImage = new ImageIcon("C:\\Documents and Settings\\Robert\\Desktop\\ClientServer\\Poker Table Art\\TableAndChairs.png");
        JLabel    background      = new JLabel(backgroundImage);
        
        background.setBounds(0, 0, backgroundImage.getIconWidth(), backgroundImage.getIconHeight());
        backgroundPanel.setPreferredSize(new Dimension(backgroundImage.getIconWidth(), backgroundImage.getIconHeight()));

        frame.getLayeredPane().add(background, new Integer(Integer.MIN_VALUE));
        backgroundPanel.setOpaque(false);
        frame.setContentPane(backgroundPanel);
        
        
        
        
        
        
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        
        CardPanel myCardPanel = new CardPanel();
        
        myCardPanel.addCard("C:\\Documents and Settings\\Robert\\Desktop\\ClientServer\\Poker Table Art\\cards\\ac.gif");
        myCardPanel.addCard("C:\\Documents and Settings\\Robert\\Desktop\\ClientServer\\Poker Table Art\\cards\\2c.gif");
        myCardPanel.addCard("C:\\Documents and Settings\\Robert\\Desktop\\ClientServer\\Poker Table Art\\cards\\3c.gif");
        myCardPanel.addCard("C:\\Documents and Settings\\Robert\\Desktop\\ClientServer\\Poker Table Art\\cards\\4c.gif");
        myCardPanel.addCard("C:\\Documents and Settings\\Robert\\Desktop\\ClientServer\\Poker Table Art\\cards\\5c.gif");
        myCardPanel.setCard("C:\\Documents and Settings\\Robert\\Desktop\\ClientServer\\Poker Table Art\\cards\\ad.gif", 3);
        
        PotSizePanel myPotSizePanel = new PotSizePanel();
        myPotSizePanel.setPotSize(1200);
        
        centerPanel.add(myCardPanel);
        centerPanel.add(myPotSizePanel);
        
        centerPanel.setOpaque(false);
        frame.getContentPane().add(centerPanel);
        centerPanel.setBounds(200, 200, 400, 130);
        
        
   /*     ButtonPanel myButtonPanel = new ButtonPanel();
        myButtonPanel.setAll("Fold", "Call 400", "Raise 1400");
        frame.getContentPane().add(myButtonPanel);
        
        
        SliderPanel mySliderPanel = new SliderPanel();
        mySliderPanel.setRaiseButton(myButtonPanel.getButton(2));
        mySliderPanel.setSlider(400, 1500);
        frame.getContentPane().add(mySliderPanel);
        
       
        PlayerInfoPanel playerInfo = new PlayerInfoPanel();
        playerInfo.addPlayerName("mtheory77");
        playerInfo.addPlayerChipCount(2650);
              
        
        PlayerCardPanel playerCards = new PlayerCardPanel();
        playerCards.setCard("C:\\Documents and Settings\\Robert\\Desktop\\ClientServer\\Poker Table Art\\cards\\ad.gif", 0);
        playerCards.setCard("C:\\Documents and Settings\\Robert\\Desktop\\ClientServer\\Poker Table Art\\cards\\2d.gif", 1);        
        
        */
   /*     JPanel playerPanel = new JPanel();
        playerPanel.setLayout(new BoxLayout(playerPanel, BoxLayout.Y_AXIS));
        playerPanel.setBounds(135, 30, 120, 110);
        playerPanel.setOpaque(false);
        
        playerPanel.add(playerCards);
        playerPanel.add(playerInfo);
        frame.getContentPane().add(playerPanel); */
        
 /*       int [] xCoor = {135, 323, 504, 643, 663, 573, 359, 140, 8, 6};
        int [] yCoor = {30, 3, 14, 110, 250, 370, 417, 352, 254, 128};
        int [] betx = {220, 349, 484, 551, 568, 491, 369, 219, 132, 135};
        int [] bety = {172, 143, 151, 208, 285, 342, 371, 309, 268, 209};
        
        JPanel [] playerPanels = new JPanel[10];
        for (int i=0; i<playerPanels.length; i++) {
            playerPanels[i] = new JPanel();
            playerPanels[i].setLayout(new BoxLayout(playerPanels[i], BoxLayout.Y_AXIS));
            playerPanels[i].setBounds(xCoor[i], yCoor[i], 120, 110);
            playerPanels[i].setOpaque(false);
            playerPanels[i].add(playerCards);
            playerPanels[i].add(playerInfo);
            frame.getContentPane().add(playerPanels[i]);
        }
        
        JPanel chipPanel = new JPanel();
        JLabel chips = new JLabel("400", new ImageIcon("C:\\Documents and Settings\\Robert\\Desktop\\ClientServer\\Poker Table Art\\PokerChip.gif"), JLabel.TRAILING);
        chipPanel.add(chips);
        chipPanel.setBounds(250, 160, 80, 80);
 */       
/*        String message = "this is a chat box\n";
        
        StyleContext context = new StyleContext();
        StyledDocument document = new DefaultStyledDocument(context);
        
        Style style = context.getStyle(StyleContext.DEFAULT_STYLE);
        StyleConstants.setAlignment(style, StyleConstants.ALIGN_LEFT);
        StyleConstants.setFontSize(style, 14);
        StyleConstants.setSpaceAbove(style, 4);
        StyleConstants.setSpaceBelow(style, 4);
        
        // Insert content
        try {
            document.insertString(document.getLength(), message, style);
        } catch (BadLocationException badLocationException) {
            System.err.println("Oops");
        }
        
        message = "and doesn't it look sharp in its brand new tuxedo that i picked out for in tij o wefjij j ooo\n";
        
        
        
        
        JTextPane textPane = new JTextPane(document);
        textPane.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textPane);
        scrollPane.setBounds(10, 10, 200, 80);
        frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
        
        // Insert content
        try {
            document.insertString(document.getLength(), message, style);
        } catch (BadLocationException badLocationException) {
            System.err.println("Oops");
        }
                   */
        
        
        ChatBox box = new ChatBox();
        box.setBounds(5, 500, 200, 100);
        frame.getContentPane().add(box);
        box.addMessage("Dealer", "Blinds are going up bitches\n");
        box.addMessage("Mtheory420", "lmao dumbassji io jo jiijjoijio jijoijoi joiojiioj and that's alll she wroottteee\n");
        
        
        
 //       frame.getContentPane().add(chipPanel);
        
        frame.pack();
        frame.setVisible(true);
    }
}


