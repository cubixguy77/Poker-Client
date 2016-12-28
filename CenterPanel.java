import javax.swing.*;
import java.awt.*;


public class CenterPanel extends JPanel {
    
    public CenterPanel() {
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        
        CardPanel cardPanel = new CardPanel();
        PotSizePanel potSizePanel = new PotSizePanel();
        
        this.add(cardPanel);
        this.add(potSizePanel);
        
        this.setBounds(200, 200, 400, 130);
    }
    
    public CardPanel getCardPanel() {
        return (CardPanel) getComponent(0);
    }
    
    public PotSizePanel getPotSizePanel() {
        return (PotSizePanel) getComponent(1);
    }
}