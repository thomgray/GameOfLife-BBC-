package gameoflife;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import javax.swing.JComponent;
import javax.swing.JPanel;

/**
 *Content pane for displaying a selection of colour schemes, designed for using in modal dialogues.
 * <p>
 *  <code>ColorSchemePickerDelegate</code> also defined in this file. Set this object's <code>delegate</code>
 *  to an implementation of that interface to respond to events.
 * </p>
 * 
 * @see ColorSchemePickerDelegate
 * @author thomdikdave
 */
class ColorSchemePicker extends JPanel implements MouseListener, ActionListener{
    ArrayList<ColorScheme> schemes;
    ArrayList<ColorSchemeItem> schemeComponents;
    
    public int selectionIndex;
    CustomButton doneButton;
    CustomButton cancelButton;
    
    ColorSchemePickerDelegate delegate;
    
    ColorSchemePicker(ArrayList<ColorScheme> schemes){
        this.schemes = schemes;
        schemeComponents = new ArrayList<>();
        for (ColorScheme scheme : schemes) {
            ColorSchemeItem ci = new ColorSchemeItem(scheme);
            ci.addMouseListener(this);
            schemeComponents.add(ci);
        }
        Dimension d = this.preferredItemSize();
        for (ColorSchemeItem comp : schemeComponents) {
            comp.setPreferredSize(d);
        }        
        this.initLayout();
    }
    
    private void initLayout(){
        this.setLayout(new GridBagLayout());
        GridBagConstraints cs = new GridBagConstraints();
        int y;
        cs.gridwidth = GridBagConstraints.REMAINDER;
        cs.anchor = GridBagConstraints.WEST;
        for (y = 0; y < schemeComponents.size(); y++) {
            ColorSchemeItem item = schemeComponents.get(y);
            cs.gridy = y;
            this.add(item, cs);
        }
        
        cs.anchor = GridBagConstraints.BASELINE_LEADING;
        cs.gridwidth = 1;
        cs.gridx = 1;
        cs.gridy = y;
        doneButton = new CustomButton("Done");
        this.add(doneButton, cs);
        cs.gridx = 2;
        cancelButton = new CustomButton("Cancel");
        this.add(cancelButton, cs);                
        
        doneButton.addActionListener(this);
        cancelButton.addActionListener(this);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        ColorSchemeItem item = (ColorSchemeItem)e.getSource();
        item.selected = true;
        selectionIndex = schemeComponents.indexOf(item);
        for (int i = 0; i < schemeComponents.size(); i++) {
            ColorSchemeItem comp = schemeComponents.get(i);
            comp.selected = i==selectionIndex;
            comp.repaint();
        }       
        if (delegate!=null) {
            delegate.colorSchemeDidSelect(item.scheme);
        }
    }
           
    //Unused mouse listener events
    @Override
    public void mousePressed(MouseEvent e) {}
    @Override
    public void mouseReleased(MouseEvent e) {}
    @Override
    public void mouseEntered(MouseEvent e) {}
    @Override
    public void mouseExited(MouseEvent e) {}
    @Override
    
    
    public void actionPerformed(ActionEvent e) {
        CustomButton source = (CustomButton)e.getSource();
        if (delegate!=null) {
            delegate.selectionDidEnd(source==doneButton);
        }        
    }
    
    /**
     * Set the selected color scheme by the index in which the scheme appears in the ColorScheme ArrayList
     * @param index index of color scheme
     */
    public void setSchemeSelection(int index){
        selectionIndex = index;
        for (int i = 0; i < schemeComponents.size(); i++) {
            ColorSchemeItem item = schemeComponents.get(i);
            item.selected = i==index;
            item.repaint();
        }
    }
    
    /**
     * Finds the size for each ColorSchemeItem, ensuring they are all the same size 
     * for the sake of alignment on screen.
     * @return preferred item size. Effectively the preferred size of the widest item.
     */
    private Dimension preferredItemSize(){
        int height = ColorSchemeItem.circleDiameter + 8;
        int width = 0;
        for (ColorSchemeItem comp : schemeComponents) {
            int compwidth = (comp.scheme.cellColors.length + 1) 
                    * (ColorSchemeItem.circleDiameter+ColorSchemeItem.circleSeparation) 
                    + 30;
            if (compwidth > width) width = compwidth;
        }
        return new Dimension(width, height);
    }
}

/**
 * Graphic representation of a single colour scheme
 * @author thomdikdave
 */
class ColorSchemeItem extends JComponent { 
    ColorScheme scheme;
    static int circleDiameter = 30;    
    static int circleSeparation = 5;
    
    boolean selected;
    
    ColorSchemeItem(ColorScheme colors){
        this.scheme = colors;
    }        
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        int x = 10;        
        int y = (this.getHeight()-circleDiameter) / 2;
        if (scheme.deadCellColor!=null) {
            g2.setColor(scheme.deadCellColor);
            g2.fillOval(x, y, circleDiameter, circleDiameter);
            if (scheme.deadCellColor.getAlpha()>200) {
                g2.setColor(Color.LIGHT_GRAY);
                g2.drawOval(x, y, circleDiameter, circleDiameter);
            }
        }else{
            g2.setColor(Color.LIGHT_GRAY);
            g2.drawOval(x, y, circleDiameter, circleDiameter);            
        }
        x += circleDiameter + 10;
        for (Color c : scheme.cellColors) {
            if (c==null){
                g2.setColor(Color.LIGHT_GRAY);
                g2.drawOval(x, y, circleDiameter, circleDiameter);
            }else {
                g2.setColor(c);
                g2.fillOval(x, y, circleDiameter, circleDiameter);
                if (c.getAlpha()>200) {
                    g2.drawOval(x, y, circleDiameter, circleDiameter);
                }
            }
            x+= circleDiameter + circleSeparation;
        }
        
        if (selected) {
            g2.setStroke(new BasicStroke(2));
            g2.setColor(GameOfLife.GameBlue);
            g2.drawRoundRect(0, 0, this.getSize().width-1, this.getSize().height-1, 20, 20);
        }
    }       
}

/**
 * Interface whose methods are called by a <code>ColorSchemePicker</code> when set as its delegate.
 * <ul>
 * <li><code>colorSchemeDidSelect(ColorScheme scheme)</code>
 * </li>
 * <li><code>selectionDidEnd(boolean apply)</code>
 * </li>
 * </ul>
 * @author thomdikdave
 */
interface ColorSchemePickerDelegate{
    /**
     * called when a <code>ColorSchemePicker</code> selects a new <code>ColorScheme</code>
     * @param scheme the new ColorScheme
     */
    void colorSchemeDidSelect(ColorScheme scheme);
    /**
     * Called when either the "Done" or "Cancel" buttons are clicked on the <code>ColorChemePicker</code> panel
     * @param apply true if "Done" is clicked, false otherwise
     */
    void selectionDidEnd(boolean apply);
}