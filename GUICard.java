
import javax.swing.*;
import java.awt.*;
import util.*;


public abstract class GUICard extends JPanel{

 protected final String FOLD = "Fold";
 
 protected boolean revealed;
 protected boolean fixed;
 protected boolean selected;
 protected boolean folded;
 protected Card card;
 
 protected ImageIcon img;
 protected JPanel    emptyPanel;
 
 protected boolean showCard;
 
 public GUICard(Card card)
 {
  super();
  this.setBackground(Constants.TRANSPARENT);
  this.card = card;
  revealed = false;
  selected = false;
  
  img = Constants.getBackImageIcon();
  
  emptyPanel = new JPanel();
  emptyPanel.setBackground(Constants.TRANSPARENT);
  
  showCard = true;
  
  // not folded
  folded = false;
 }
 
 /**
  * Only method that is overwritten
  */
 public void paintComponent(Graphics g)
 {
  super.paintComponent(g);
 }

 public final void hideCard()
 {
  showCard = false; 
 }
 
 public final void showCard()
 {
  showCard = true; 
 }
 
 public final void revealCard()
 {
  this.revealed = true;
  img = Constants.getImageIcon(card);
 }
 
 public final void unrevealCard()
 {
  this.revealed = false;
  img = Constants.getBackImageIcon();
 }
 
 public final boolean isSelected()
 {
  return selected;
 }

 public final void fixCard()
 {
  fixed = true;
 }
 
 public final void unfixCard()
 {
  fixed = false;
 }
 
 public final Card getCard()
 {
  return card; 
 }
 
 public final void foldCard()
 {
  folded = true;
 }
 
 public final void unfoldCard()
 {
  folded = false;
 }
 
 public final boolean isFolded()
 {
  return folded;
 }
}
