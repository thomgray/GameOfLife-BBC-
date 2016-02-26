package gameoflife;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.geom.Rectangle2D;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;

/**
 *Custom subclass of JButton. Merely overrides the <code>paintComponent()</code> method and paints the button
 * with a more attractive design. Also tweaks some default behaviour (e.g. font and isFocusable properties)
 * @author thomdikdave
 */
public class CustomButton extends JButton {
    public static final int cornerRadius = 20;
    private static final Font ButtonFont = new Font(GameOfLife.GameFontFamily, Font.PLAIN, 14);
        
    private Color armedBackgroundColor;
    public Color getArmedBackgroundColor(){return armedBackgroundColor;}
    public void setArmedBackgroundColor(Color c){armedBackgroundColor = c; this.repaint();}
    private Color armedForgroundColor;
    public Color getArmedForgroundColor() {return armedForgroundColor;}
    public void setArmedForgroundColor(Color c){armedForgroundColor = c; this.repaint();}

    
    public CustomButton(String text){
        super(text);
        this.setFocusable(false);
        armedBackgroundColor = GameOfLife.GameBlue;
        armedForgroundColor = Color.WHITE;
        this.setForeground(GameOfLife.GameBlue);
        this.setBackground(null);
        this.setFont(ButtonFont);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D)g;
        Font font = this.getFont();
        g2.setFont(font);        
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        
        boolean selected = this.hasFocus();
        boolean armed = this.getModel().isArmed();
        Dimension d = this.getSize();
        int inset = 2;
        
        //paint backgound
        int radius = CustomButton.cornerRadius;
        //Grey if focused, not armed 
        if (selected && !armed){
            g2.setColor(GameOfLife.DeadGrey);
            g2.fillRoundRect(inset, inset, d.width-(2*inset), d.height-(2*inset), radius, radius);
        }else if (armed && this.armedBackgroundColor!= null) {
            g2.setColor(armedBackgroundColor);
            g2.fillRoundRect(inset, inset, d.width-(2*inset), d.height-(2*inset), radius, radius);
        }else if (this.getBackground()!=null) {
            g2.setColor(this.getBackground());
            g2.fillRoundRect(inset, inset, d.width-(2*inset), d.height-(2*inset), radius, radius);
        }                        
        
        //paint border
        g2.setColor(GameOfLife.DeadGrey);
        g2.setStroke(new BasicStroke(2));
        g2.drawRoundRect(inset, inset, d.width-(2*inset), d.height-(2*inset), radius, radius);
                
        
        //text
        String text = this.getText(); 
        if (text!=null) {
            g2.setFont(font);
            FontMetrics metrics = g2.getFontMetrics();
            Rectangle2D textBounds = metrics.getStringBounds(text, g);
            int textHeight = (int)textBounds.getHeight();
            int textWidth = (int)textBounds.getWidth();

            int baseLineY = (d.height/2) - (textHeight/2) + metrics.getAscent();
            int baseLineX;

            switch (this.getHorizontalAlignment()) {
                case SwingConstants.CENTER:
                    baseLineX = (d.width/2) - (textWidth/2);
                    break;
                case SwingConstants.RIGHT: case SwingConstants.TRAILING:
                    baseLineX = d.width - 2 - textWidth;
                    break;
                default:  
                    baseLineX = 2;
            }

            //paint text
            if (armed) {
                g2.setColor(armedForgroundColor);
            }else g2.setColor(this.getForeground()); 
            g2.drawString(text, (float)baseLineX, (float)baseLineY);
        }                
    }    
   
}
