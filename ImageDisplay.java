import java.applet.Applet;
import java.applet.*;
import java.awt.*;
import java.awt.Panel;
import java.awt.event.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.image.*;
import java.io.*;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.*;
import java.lang.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.Toolkit;
import java.lang.*;
import java.awt.Image;


public class ImageDisplay extends JComponent {
    private static Image image;
    int x=10; int y =10;
    public static void Init() {
        //image = getImage("Button.png");
        String filename = "C:\\Documents and Settings\\Robert\\Desktop\\ClientServer\\Poker Table Art\\Button.png";
        image = Toolkit.getDefaultToolkit().getImage(filename);
        System.out.println("about to draw");
        //paint();
    }
    
    public void paint(Graphics g) {
        g.drawImage(image, 10, 10, this);
    }
    public static void main(String args[]) {
        Init();
    }
}
