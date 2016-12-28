import javax.swing.*;
import java.awt.*;


public class PotSizePanel extends JPanel {
    
    public PotSizePanel() {
        this.setLayout(new FlowLayout());
        this.setOpaque(false);
        this.setMaximumSize(new Dimension(390, 20));
        this.add(new JLabel("Total Pot: "));
    }
    
    public JLabel getJLabel() {
        return (JLabel) this.getComponent(0);
    }
    
    public void setPotSize(int size) {
        getJLabel().setText("Total Pot: " + Integer.toString(size));
    }
    
    public void clearPot() {
        setPotSize(0);
    }
}