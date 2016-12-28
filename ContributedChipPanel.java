import javax.swing.*;
import java.awt.*;

public class ContributedChipPanel extends JPanel {
    
    private boolean buttonVisible;
    private boolean chipVisible;
    
    public ContributedChipPanel() {
        ImageIcon buttonIcon = createImageIcon("Button.gif");
        
        ImageIcon chipIcon = createImageIcon("pokerchip.gif");
        
        JLabel buttonLabel = new JLabel(buttonIcon);
        buttonLabel.setVisible(false);
        
        JLabel chipLabel = new JLabel("1550", createImageIcon("pokerchip.gif"), JLabel.LEADING);
        chipLabel.setVisible(false);
        

        buttonVisible = false;
        chipVisible = false;
        
        this.add(buttonLabel);
        this.add(chipLabel);
        
        this.setOpaque(false);
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
    
    public void setText(String text) {
        getLabel().setText(text);
        showPanel();
    }
    public void setText(int amount) {
        String text = Integer.toString(amount);
        getLabel().setText(text);       
        showPanel();
    }
    public void showPanel() {
        getLabel().setVisible(true);
    }
    public void hidePanel() {
        getLabel().setVisible(false);
    }
    
    
    public JLabel getLabel() {
        return (JLabel) this.getComponent(1);
    }
    
    public void showButton() {
        if (!buttonVisible) {
            this.getComponent(0).setVisible(true);
            buttonVisible = true;
        }
    }
    public void hideButton() {
        if (buttonVisible) {
            this.getComponent(0).setVisible(false);
        }
    }
}
        
        