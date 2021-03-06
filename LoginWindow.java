import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.net.*;
import java.io.*;


public class LoginWindow implements ActionListener {
    
  JFrame frame;
  JTextField user;
  JPasswordField pass;
  JButton ok, cancel;
  Socket connection;
  
  public LoginWindow(Socket connection)
  {
      this.connection = connection;
    frame = new JFrame( "Login" );
    
    JPanel top = new JPanel();
    GridBagLayout gbl = new GridBagLayout();
    GridBagConstraints c = new GridBagConstraints();
    top.setLayout( gbl );
    c.insets = new Insets( 5, 7, 5, 7 );
    c.fill = GridBagConstraints.BOTH;
    c.weightx = 1.0;
    
    c.gridwidth = 1;
    top.add( new JLabel( "Username: " ));
    user = new JTextField( 30 );
    c.gridwidth = GridBagConstraints.REMAINDER;
    top.add( user, c );

    c.gridwidth = 1;
    pass = new JPasswordField( 30 );
    c.gridwidth = GridBagConstraints.REMAINDER;
    top.add( new JLabel( "Password: "));
    top.add( pass, c );

    frame.getContentPane().add( top, BorderLayout.NORTH );

    JPanel buttons = new JPanel();
    ok = new JButton( "OK" );
    ok.addActionListener( this );
    buttons.add( ok );
    
    cancel = new JButton( "Cancel" );
    cancel.addActionListener( this );
    buttons.add( cancel );
    
    frame.getContentPane().add( buttons, BorderLayout.SOUTH );
    
    frame.pack();
    frame.setVisible( true );
  }

  public void actionPerformed( ActionEvent e )
  {
    if( e.getSource() == ok )
      {
        System.out.println("You have clicked on the ok button");
        String username = user.getText();
        String password = pass.getText(); //pass.getPassword().toString();
        System.out.println("user: " + username + " pass: " + password);
        try {
            BufferedOutputStream bos = new BufferedOutputStream(connection.getOutputStream());
            OutputStreamWriter osw = new OutputStreamWriter(bos, "US-ASCII");
            osw.write("#login#" + username + "#" + password + "#" + (char) 13);
            osw.flush();
            
            frame.setVisible(false);
        }
        catch (IOException exception) {
            System.out.println("IO error: could not send message");
        }
      }
  }
}