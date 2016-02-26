package gameoflife;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EventListener;
import java.util.Iterator;
import javax.swing.JComponent;
import javax.swing.event.EventListenerList;


public class GameGrid extends JComponent implements Iterable<Cell>, MouseListener{        
    
    Grid<Cell> cells;    
    private GameState state;
    public GameState getState() {return state;}
    EventListenerList listeners;
    private Dimension size;
    public Dimension gridSize() {return (Dimension)size.clone();}
    
    private Dimension cellSize = new Dimension(20,20);
    
    private ColorScheme colorScheme;
    
    
//------------------------------------------------------
//-------Initialising-------
//------------------------------------------------------
    
    public GameGrid(Dimension size){
        super();     
        this.size = size;
        int cellDiameter = this.calculateCellDiameter();
        this.cellSize = new Dimension(cellDiameter,cellDiameter);
        this.initialiseGrid();
    }
    
    private void initialiseGrid(){
        this.setLayout(new GridLayout(size.height, size.width));                
        cells = new Grid<>(size);
        cells.populateGrid(()-> new Cell());
        for (Cell c : cells) {
            this.add(c);
            c.addMouseListener(this);
            if (colorScheme!=null){
                c.deadColor = colorScheme.deadCellColor;
            }                    
        }
        this.setSize(this.getPreferredSize());        
    }
    
//------------------------------------------------------
//-------Basic Setting-------
//------------------------------------------------------
    
    /**
     * Returns the ideal diameter of the grid cells, depending on the size of the grid. 
     * @return cell diameter (max 20)
     */
    private int calculateCellDiameter(){
        Dimension maxSize = GameOfLife.maxGridSize;
        int newMaxCellWidth = maxSize.width / size.width;
        int newMaxCellHeight = maxSize.height / size.height;                
        
        if (newMaxCellWidth >= 20 && newMaxCellHeight >= 20) {
            return 20;
        }else{
            int minDiameter = newMaxCellWidth < newMaxCellHeight ? newMaxCellWidth : newMaxCellHeight;
            return minDiameter;
        }
    }

    /**
     * Sets the dimension of the grid (not the component size!). Calling this method will trigger a re-initialisation
     * of the grid. This involves resetting the layout manager, and re-populating the grid with new Cells.
     * <p> Finally, a <code>gridDidResize</code> is called on any GameGridListeners</p>
     * @param size 
     */
    public void setGridSize(Dimension size){
        this.size = size;
        int minDiameter = this.calculateCellDiameter();
        this.cellSize = new Dimension(minDiameter,minDiameter);        
        
        this.removeAll();
        this.initialiseGrid();       
        if (listeners != null){
            for (GameGridListener l : listeners.getListeners(GameGridListener.class)) {
                l.gridDidResize();
            }
        }  
    }        
    
    /**
     * Sets the state information of the grid to the parameter state. If the state size is different to
     * the grid size, this triggers a <code>setGridSize</code> call.
     * <p>Each cell in the grid is then queried and the state of each cell is set to the new value where appropriate</p>
     * <p>If a cell is set to live state, the cell's liveColor property is set to the current color scheme's random color call</p>
     * @param state 
     */
    public void setState(GameState state){
        this.state = state;
        if (!state.size.equals(this.size)) {this.setGridSize(state.size);}
                
        for (Iterator<Point> it = cells.iteratePoints(); it.hasNext();){
            Point p = it.next();
            Cell c = cells.getValueAtPoint(p);
            
            if (c.isLive()==state.getValueAtPoint(p)) continue;
                
            c.setLive(state.getValueAtPoint(p));            
            if (c.isLive()) {
                c.liveColor = colorScheme.randomCellColor();
            }
        }
        this.repaint();
    } 

        
    @Override
    public Dimension getPreferredSize() {
        int width = size.width * cellSize.width;
        int height = size.height * cellSize.height;
        return new Dimension(width, height);
    }
    

//------------------------------------------------------
//-------Iteration-------
//------------------------------------------------------
    
    @Override
    public Iterator<Cell> iterator() {       
        return cells.iterator();
    }
    
    public Iterator<Point> iteratePoints() {
        return cells.iteratePoints();
    }

//------------------------------------------------------
//-------Coloring-------
//------------------------------------------------------           
    
    void setColorScheme(ColorScheme scheme){
        this.colorScheme = scheme;
        for (Cell c : this) {
            c.deadColor = colorScheme.deadCellColor;
            if (c.isLive()) c.liveColor = colorScheme.randomCellColor();
        }
        repaint();
    }    
    
    ColorScheme getColorScheme(){
        return this.colorScheme;
    }
    
//------------------------------------------------------
//-------Actions-------
//------------------------------------------------------    
    
    @Override
    public void mouseClicked(MouseEvent e) {}
    @Override
    public void mouseReleased(MouseEvent e) {}
    @Override
    public void mouseExited(MouseEvent e) {}

    @Override
    public void mousePressed(MouseEvent e) {
        Cell cell = (Cell)e.getSource();
        this.toggleCellLiveState(cell);
    }
    
    @Override
    public void mouseEntered(MouseEvent e) {
        int mod = e.getModifiersEx();        
        if (mod == MouseEvent.BUTTON1_DOWN_MASK) {            
            Cell cell = (Cell)e.getSource();      
            this.toggleCellLiveState(cell);
        }
    }      
    
    private void toggleCellLiveState(Cell cell){                
        cell.setLive(!cell.isLive());
        if (cell.isLive()) {
            cell.liveColor = colorScheme.randomCellColor();
        }        
        if (listeners!=null) {
            for (GameGridListener l : listeners.getListeners(GameGridListener.class)) {
                l.cellClicked(cell);
            }
        }        
        cell.repaint();
    }
    
    void addGridListener(GameGridListener el){
        if (listeners == null) {listeners = new EventListenerList();}
        listeners.add(GameGridListener.class, el);
    }
    
    
    
}

//------------------------------------------------------
//-------GameGridListener Interface-------
//------------------------------------------------------

/**
 * For Deferring actions from a GameGrid (in this case to a GameModel)
 * @author thomdikdave
 */
interface GameGridListener extends EventListener {
    void cellClicked(Cell c);
    void gridDidResize();
}

    

