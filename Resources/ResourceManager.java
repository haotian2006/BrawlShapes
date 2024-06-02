package Resources;


import java.io.IOException;
import java.io.InputStream;

import Engine.TileHandler;

import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import java.awt.Color;

/**
 * The ResourceManager class provides utility methods for managing resources such as images.
 */
public class ResourceManager {
    
    /**
     * The manager class object used to retrieve resources.
     */
    public static final Class<?> manager = new ResourceManager().getClass();

    /**
     * Retrieves an input stream for the specified resource path.
     * 
     * @param path The path of the resource.
     * @return An input stream for the specified resource.
     */
    public static InputStream getResource(String path) {
        return manager.getResourceAsStream(path);
    }   

    /**
     * Retrieves a BufferedImage object for the specified image path.
     * 
     * @param path The path of the image resource.
     * @return A BufferedImage object representing the image.
     */
    public static BufferedImage getImage(String path){
        try {
            BufferedImage Image = javax.imageio.ImageIO.read(getResource(path));
            return Image;
        } catch (IOException e) {
            System.out.println(path + " IS NOT A VALID PATH");
        }
        return null;
    }

    /**
     * Retrieves a scaled BufferedImage object for the specified image path with the given width and height.
     * 
     * @param path The path of the image resource.
     * @param x The desired width of the scaled image.
     * @param y The desired height of the scaled image.
     * @return A scaled BufferedImage object representing the image.
     */
    public static BufferedImage getImage(String path, int x, int y){
        try {
            BufferedImage Image = javax.imageio.ImageIO.read(getResource(path));
            Image = ResourceManager.scaleImage(Image, x, y);
            return Image;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Recolors the specified BufferedImage object with the given color.
     * 
     * @param img The BufferedImage object to be recolored.
     * @param color The color to be applied to the image.
     * @return A new BufferedImage object with the specified color applied.
     */
    public static BufferedImage recolor(BufferedImage img, Color color) {
        if (color.equals(Color.WHITE)) return img;
        
        BufferedImage newImage = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());
        for (int x = 0; x < img.getWidth(); x++) {
            for (int y = 0; y < img.getHeight(); y++) {
                int pixel = img.getRGB(x, y);
                if (pixel == Color.WHITE.getRGB()) {
                    newImage.setRGB(x, y, color.getRGB());
                } else {
                    newImage.setRGB(x, y, pixel);
                }
            }
        }
        return newImage;
    }

    /**
     * Scales the specified BufferedImage object to the specified width and height.
     * 
     * @param img The BufferedImage object to be scaled.
     * @param width The desired width of the scaled image.
     * @param height The desired height of the scaled image.
     * @return A new BufferedImage object with the specified width and height.
     */
    public static BufferedImage scaleImage(BufferedImage img, int width, int height) {
        BufferedImage image = new BufferedImage(width, height, img.getType());
        Graphics2D g2 = image.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(img, 0, 0, width, height, null);
        g2.dispose();
        return image;
    }
}
