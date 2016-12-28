import javax.swing.*;
import java.awt.*;


public class CardPanel extends JPanel {
    
    public CardPanel() {
        this.setLayout(new FlowLayout());
        this.setOpaque(false);
        this.setMaximumSize(new Dimension(390, 105));
        
        JLabel card1 = new JLabel();
        this.add(card1);
        JLabel card2 = new JLabel();
        this.add(card2);
        JLabel card3 = new JLabel();
        this.add(card3);
        JLabel card4 = new JLabel();
        this.add(card4);
        JLabel card5 = new JLabel();
        this.add(card5);
    }
    
    public void setCard(String filename, int cardNum) {
        ImageIcon image = new ImageIcon(filename);
        setCard(image, cardNum);
    }
    
    public void setCard(JLabel newCard, int cardNum) {
        this.add(newCard, cardNum);
    }
    
    public JLabel getCard(int cardNum) {
        return (JLabel) this.getComponent(cardNum);
    }
    
    public void setCard(ImageIcon image, int cardNum) {
        getCard(cardNum).setIcon(image);
    }
    
    public void clearCard(int cardNum) {
        getCard(cardNum).setIcon(null);
    }
    
    
    public void addCard(String filename) {
        JLabel newCard = new JLabel(new ImageIcon(filename));
        setCard(newCard, -1);  // -1 is the command to append the component to the end
    }
    
    
    public void clearBoard() {
        this.removeAll();
    }
}
    