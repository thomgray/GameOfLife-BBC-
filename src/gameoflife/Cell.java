package gameoflife;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JComponent;

/**
 *Represents a single cell in a grid
 * @author thomdikdave
 */
public class Cell extends JComponent  {
    public Color liveColor;
    public Color deadColor;
    
    private boolean live = false;
    public boolean isLive() {
        return live;
    }
    public void setLive(boolean live) {
        this.live = live;        
    }
    public final static int dotDiameter = 6;

    @Override
    protected void paintComponent(Graphics g) {
        Color col = live ? liveColor : deadColor;
        if (col==null) return;        
        Graphics2D g2 = (Graphics2D)g;
        g2.addRenderingHints(new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON));
        
        Dimension d = this.getSize();
        int padding = d.width/10 >= 1 ? d.width/10 : 1;
        int diamteter = d.width - padding;
        g2.setColor(col);
        g2.fillOval(padding, padding, diamteter, diamteter);            
    }
    
}
