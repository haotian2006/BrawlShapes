package Engine;

import java.io.InputStream;
import java.util.Scanner;

import Resources.ResourceManager;

import java.util.*;

/**
 * The Map class represents a game map consisting of characters.
 * @author joey
 */
public class Map {
  
    /**
     * The character map of the game.
     */
    public char[][] charMap;

    /**
     * Constructs a Map object with the given character map.
     * 
     * @param mp the character map
     */
    public Map(char[][] mp){
        charMap = mp;
    }

    /**
     * Constructs a Map object with the given string representation of the character map.
     * 
     * @param s the string representation of the character map
     */
    public Map(String s) {
        int count = 1; 
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '\n') {
                count++;
            }
        }
        charMap = new char[count][s.length() / count];
        int x = 0;
        for (int i = 0; i < charMap.length; i++) {
            for (int j = 0; j < charMap[0].length; i++) {
                charMap[i][j] = s.charAt(x);
                x++;
            }
        }
    }

    /**
     * Constructs a Map object with the given input stream.
     * 
     * @param is the input stream
     */
    public Map(InputStream is) {
        Scanner s = new Scanner(is);
        List<String> lines = new ArrayList<>();
        
        while (s.hasNextLine()) {
            lines.add(s.nextLine());
        }
        s.close();

        int numRows = lines.size();
        int numCols = lines.get(0).length();  
        
        charMap = new char[numRows][numCols];

        for (int i = 0; i < numRows; i++) {
            String line = lines.get(i);
            for (int j = 0; j < numCols; j++) {
                charMap[i][j] = line.charAt(j);
            }
        }
    }

    /**
     * Returns the character at the specified position in the map.
     * 
     * @param x the x-coordinate
     * @param y the y-coordinate
     * @return the character at the specified position
     */
    public char getChar(int x, int y) {
        return charMap[y][x];
    }

    /**
     * Returns the height of the map.
     * 
     * @return the height of the map
     */
    public int height() {
        return charMap.length;
    }

    /**
     * Returns the width of the map.
     * 
     * @return the width of the map
     */
    public int width() {
        return charMap[0].length;
    }

    /**
     * Returns a string representation of the map.
     * 
     * @return a string representation of the map
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (char[] c : charMap) {
            sb.append(c);
            sb.append('\n');
        }
        return sb.toString();
    }

    /**
     * The main method of the Map class.
     * 
     * @param args the command-line arguments
     */
    public static void main(String[] args) {
        Map m = new Map(ResourceManager.getResource("Maps/BaseMap.txt"));
        System.out.println(m.toString());
    }
}
