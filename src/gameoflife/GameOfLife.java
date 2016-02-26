package gameoflife;

import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JFrame;

/**
 *
 * @author thomdikdave
 */
public class GameOfLife {
    public static final Color GameBlue = new Color(70, 180, 210);
    public static final Color DeadGrey = new Color(220,220,220);
    public static final Dimension maxGridSize = new Dimension(1300,750);
    
    public static final String GameFontFamily = "SansSerif";    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        if (System.getProperty("os.name").contains("Mac")) {
            System.setProperty("apple.laf.useScreenMenuBar", "true");
        }        
        java.awt.EventQueue.invokeLater(() -> {
            GOLFrame frame = new GOLFrame("Game of Life");
            frame.setSize(400, 400);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        });                     
    }    
}
