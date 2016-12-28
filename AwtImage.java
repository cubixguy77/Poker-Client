import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.JFrame;

public class AwtImage extends javax.swing.JFrame {
    
    private Image img;
    
    // Variables declaration - do not modify                     
    private javax.swing.JCheckBox jCheckBox2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextField jTextField1;
    // End of variables declaration            
    
    public static void main(String[] args){
        AwtImage ai = new AwtImage();
    }
    
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTextField1 = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jCheckBox2 = new javax.swing.JCheckBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Not Logged In");
        getContentPane().setLayout(null);

        jTextField1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField1KeyTyped(evt);
            }
        });
        jScrollPane1.setViewportView(jTextField1);

        getContentPane().add(jScrollPane1);
        jScrollPane1.setBounds(0, 540, 170, 22);

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane2.setViewportView(jTextArea1);

        getContentPane().add(jScrollPane2);
        jScrollPane2.setBounds(0, 440, 166, 96);

        jCheckBox2.setText("Sit Out Next Hand");
        jCheckBox2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox2ActionPerformed(evt);
            }
        });
        getContentPane().add(jCheckBox2);
        jCheckBox2.setBounds(0, 410, 113, 23);

        pack();
    }// </editor-fold>
    
    
    private void jCheckBox2ActionPerformed(java.awt.event.ActionEvent evt) {                                           
        // TODO add your handling code here:
    }                                          

    private void jTextField1KeyTyped(java.awt.event.KeyEvent evt) {                                     
        // TODO add your handling code here:
    }               
    
    
    
    public AwtImage() {
        super("Image Frame");
        MediaTracker mt = new MediaTracker(this);
        img = Toolkit.getDefaultToolkit().getImage("C:\\Documents and Settings\\Robert\\Desktop\\ClientServer\\Poker Table Art\\TableAndChairs.png");
        mt.addImage(img,0);
        setSize(600,600);
        
      
        initComponents();
        setVisible(true);
        addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent we) {
                dispose();
            }
        });
    }
    
    public void update(Graphics g){
        paint(g);
    }
    
    public void paint(Graphics g) {
        if(img != null)
            g.drawImage(img, 0, 10, this);
       // else
        //    g.clearRect(0, 0, getSize().width, getSize().height);
    }
}