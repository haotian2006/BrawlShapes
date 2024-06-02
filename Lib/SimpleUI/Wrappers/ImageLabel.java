package SimpleUI.Wrappers;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
public class ImageLabel extends JLabel implements BaseFrame {
        private int sizeX;
        private int sizeY;
        private ImageIcon image;
        
        public ImageLabel(){
            super();
        }

        public void setImage(String imagePath) {
            if (imagePath.equals("") || imagePath == null){return;}
            try {
                File path = new File(imagePath);
                FileInputStream fis = new FileInputStream(path);  
                BufferedImage image = ImageIO.read(fis);
                ImageIcon icon = new ImageIcon(image);
                this.image = icon;
                setIcon(icon);
                if (sizeX != 0 && sizeY != 0) 
                    setImageSize(sizeX, sizeY);
            } catch (IOException e) {
                System.out.println("Failed to load image: " + e.getMessage());
            }
        }

        public void setImage(InputStream stream) {
            try {
                BufferedImage image =  javax.imageio.ImageIO.read(stream);
                ImageIcon icon = new ImageIcon(image);
                this.image = icon;
                setIcon(icon);
                if (sizeX != 0 && sizeY != 0) 
                    setImageSize(sizeX, sizeY);
            } catch (IOException e) {
                System.out.println("Failed to load image");
            }
        }


        public void setImageSize(int x,int y){
            sizeX = x;
            sizeY = y;
            if (this.image == null) return;
            Image scaledImage = image.getImage().getScaledInstance(x, y, Image.SCALE_SMOOTH);
            ImageIcon scaledIcon = new ImageIcon(scaledImage);
            image = scaledIcon;
            setIcon(scaledIcon);
        }
        public void setImageSize(Dimension x){
            setImageSize(x.width, x.height);
        }
    

    
}

