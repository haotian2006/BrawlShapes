package  SimpleUI.Wrappers;

import javax.swing.JTextPane;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
public class TextLabel extends JTextPane implements BaseFrame  {


    public TextLabel(String text){
        this();
        setText(text);
    }

    public TextLabel(){
        setEditable(false);
        setFocusable(false);
    }





    public void setAlignment(int style){
        StyledDocument doc = getStyledDocument();
        SimpleAttributeSet center = new SimpleAttributeSet();
        StyleConstants.setAlignment(center, style);
        doc.setParagraphAttributes(0, doc.getLength(), center, false);
    }



}
