import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.*;
import javax.swing.event.*;


/* creates the panel to hold the action buttons */
public class SliderPanel extends JPanel implements ChangeListener {
    
    private int min;
    private int max;
    private int sliderX = 100;
    private int sliderY = 25;
    
    private JButton raiseButton;  // this button's text is controlled by the slider's value
    
    
    public SliderPanel() {
        JSlider slider = new JSlider(JSlider.HORIZONTAL, 0, 0, 0);
        slider.setPreferredSize(new Dimension(sliderX, sliderY));
        slider.addChangeListener(this);
        slider.setOpaque(false);
        
        this.setLayout(new FlowLayout());
        this.add(slider);
        hideSlider();
        this.setMaximumSize(new Dimension(110, 25));
        this.setBounds(730, 505, 110, 25);
        this.setOpaque(false);
    }
    
    public void setSlider(int min, int max) {
        this.min = min;
        this.max = max;
        getSlider().setMinimum(min);
        getSlider().setMaximum(max);
        
        getSlider().setValue(min);
        updateRaiseButtonText(min);
        getSlider().setVisible(true);
    }
    
    public JSlider getSlider() {
        return (JSlider) this.getComponent(0);
    }
    
    public void hideSlider() {
        getSlider().setVisible(false);
    }
    public void showSlider() {
        getSlider().setVisible(true);
    }
        
    public void setRaiseButton(JButton raiseButton) {
        this.raiseButton = raiseButton;
    }
    
    public void updateRaiseButtonText(int amount) {
        Integer value = (Integer) amount;
        String text = raiseButton.getText();
        String firstWord;
        int spaceIndex = text.indexOf(' ');
        
        if(spaceIndex == -1)
            firstWord = text;
        else
            firstWord = text.substring(0, spaceIndex);
       
        raiseButton.setText(firstWord + " " + value.toString());
    }
    
    public void stateChanged(ChangeEvent e) {
        JSlider slider = (JSlider) e.getSource();
        updateRaiseButtonText(slider.getValue());
    }
}
