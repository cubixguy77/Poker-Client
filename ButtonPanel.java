import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;


/* creates the panel to hold the action buttons */
public class ButtonPanel extends JPanel {
    
    public ButtonPanel(final ActionListener listener) {
        
        int buttonSizeX = 100;
        int buttonSizeY = 75;
        int hgap = 30;
        int vgap = 5;
        
        this.setLayout(new FlowLayout(FlowLayout.CENTER, hgap, vgap));
        
        JButton button1 = new JButton("");
        button1.addActionListener(listener);
        button1.setPreferredSize(new Dimension(buttonSizeX, buttonSizeY));
        
        JButton button2 = new JButton("");
        button2.addActionListener(listener);
        button2.setPreferredSize(new Dimension(buttonSizeX, buttonSizeY));
        
        JButton button3 = new JButton("");
        button3.addActionListener(listener);
        button3.setPreferredSize(new Dimension(buttonSizeX, buttonSizeY));
        
        
        this.add(button1);
        this.add(button2);
        this.add(button3);
        
        hideAll();
        
        this.setOpaque(false);
        this.setMaximumSize(new Dimension(390, 80));
        this.setBounds(450, 530, 390, 80);
    }
    
    public void addButton(String text) {
        this.add(new JButton(text));
    }
    
    public JButton getButton(int buttonNum) {
        return (JButton) this.getComponent(buttonNum);
    }
    
    public void setButton(int buttonNum, String text) {
        getButton(buttonNum).setText(text);
        getButton(buttonNum).setVisible(true);
    }
    
    /* sets all three buttons at once
     * common example: fold, call, raise */
    public void setAll(String message1, String message2, String message3) {
        setButton(0, message1);
        setButton(1, message2);
        setButton(2, message3);
    }
    
    /* sets two buttons to be visible
     * common example: check or bet */
    public void setAll(String message1, String message3) {
        setButton(0, message1);
        setVisible(1, false);
        setButton(2, message3);
    }
    
    public void setVisible(int buttonNum, boolean visible) {
        getButton(buttonNum).setVisible(visible);
    }
    
    public void showAll() {
        setVisible(0, true);
        setVisible(1, true);
        setVisible(2, true);
    }
    public void hideAll() {
        setVisible(0, false);
        setVisible(1, false);
        setVisible(2, false);
    }
    
    
 /*   public void actionPerformed(ActionEvent e) {
        JButton temp = (JButton) e.getSource();
        if (temp.getText().contains("Fold"))
            System.out.println("You hit fold");
    }
 */ 
    
}