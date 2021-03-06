import javax.swing.*;
import java.awt.*;


public class ImagePanel extends JPanel {

  private Image img;
  private int x;
  private int y;
  private boolean visible;

  public ImagePanel( String img ) {
    x = 0;
    y = 0;
    visible = true;
    setImage(new ImageIcon(img).getImage());
  }

  public void setImage(Image img) {
    this.img = img;
    Dimension size = new Dimension(img.getWidth(null), img.getHeight(null));
    setPreferredSize(size);
    setMinimumSize(size);
    setMaximumSize(size);
    setSize(size);
    setLayout(null);
  }

  public void setLocation(int x, int y) {
      this.x = x;
      this.y = y;
  }
  
  public void hideImage() { visible = false;  }
  public void showImage() { visible = true;   }

  public void paintComponent(Graphics g) {
      if (visible)
          g.drawImage(img, x, y, this);
  }
}
