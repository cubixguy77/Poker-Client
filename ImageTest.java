import javax.swing.*;
import java.awt.*;

public class ImageTest {

    
    
    
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
        
        
        ImagePanel button = new ImagePanel("C:\\Documents and Settings\\Robert\\Desktop\\ClientServer\\Poker Table Art\\button.png");
        frame.getContentPane().add(button);
        
        button.setBounds(190, 140, 100, 100);
        
        frame.pack();
        frame.setVisible(true);
    }
}


