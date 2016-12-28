import java.awt.BorderLayout;
import java.awt.Container;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;


public class ChatBox extends JScrollPane {
    
    private Style style;
    
    public ChatBox() {
        
        StyleContext context = new StyleContext();
        StyledDocument document = new DefaultStyledDocument(context);
        
        style = context.getStyle(StyleContext.DEFAULT_STYLE);
        StyleConstants.setAlignment(style, StyleConstants.ALIGN_LEFT);
        StyleConstants.setFontSize(style, 14);
        StyleConstants.setSpaceAbove(style, 4);
        StyleConstants.setSpaceBelow(style, 4);
        
        JTextPane textPane = new JTextPane(document);
        textPane.setEditable(false);
        
        this.setViewportView( textPane );

    }
    
    public JTextPane getTextPane() {
        return (JTextPane) this.getViewport().getView();
    }
    
    public StyledDocument getDocument() {
        return (StyledDocument) getTextPane().getStyledDocument();
    }
    
    public void addMessage(String speaker, String message) {
        String combinedMessage = speaker + ": " + message + "\n";
        StyledDocument document = getDocument();
        
        try {
            document.insertString(document.getLength(), combinedMessage, style);
            getTextPane().setCaretPosition(document.getLength());
        } 
        catch (BadLocationException badLocationException) {
            System.err.println("whatever a BadLocationException is, it just happened");
        }
    }
}
        
        