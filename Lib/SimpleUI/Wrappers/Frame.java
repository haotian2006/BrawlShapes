package  SimpleUI.Wrappers;

import javax.swing.JPanel;


import java.awt.event.*;




public class Frame extends JPanel  implements BaseFrame {


    public Frame(){
        super(null);
    }

    public void debug(){
        TextLabel text = new TextLabel("0,0");
        text.setSize(100,100);
        text.setOpaque(false);
        text.setRequestFocusEnabled(false);
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                text.setText(e.getX()+","+e.getY());
                text.setLocation(e.getX(),e.getY()+10);
            }
        });
        add(text);
    }

}
