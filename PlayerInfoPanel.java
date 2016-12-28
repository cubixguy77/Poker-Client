import javax.swing.*;
import java.awt.*;

public class PlayerInfoPanel extends JPanel {
    
    public PlayerInfoPanel() {
        
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setOpaque(false);
        
        JLabel nameLabel = new JLabel();
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
       
        JLabel chipLabel = new JLabel();
        chipLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        this.add(nameLabel);
        this.add(chipLabel);
    }
    
    public JLabel getNameLabel() {
        return (JLabel) this.getComponent(0);
    }
    public JLabel getChipLabel() {
        return (JLabel) this.getComponent(1);
    }
    
    public void setPlayerName(String name) {
        getNameLabel().setText(name);
       
    }
    public void setPlayerChips(int amount) {
        Integer i = new Integer(amount);
        getChipLabel().setText(i.toString());
    }
    
    /* useless */
      public JLabel createJLabel(String text) {
        JLabel label = new JLabel(text);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        return label;
    }
    
    public void addPlayerName(String name) {
     //   JLabel playerName = new JLabel(name);
     //   playerName.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.add(createJLabel(name));
    }
    
    public void addPlayerChipCount(int amount) {
        Integer i = new Integer(amount);
  //      JLabel playerChips = new JLabel(i.toString());
  //      playerChips.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.add(createJLabel(i.toString()));
    }
}