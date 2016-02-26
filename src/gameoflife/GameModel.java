package gameoflife;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;


/**
 *
 * @author thomdikdave
 */
public class GameModel implements GameGridListener, ActionListener {
    public static final int Geometry_EuclideanFlat = 0;
    public static final int Geometry_EuclideanTorus = 1;       
    
    final GameGrid grid;
    GameState initialState;
    GameState currentState;
    GameModelDelegate delegate;                
        
    int iteration=0;
    Timer timer;
    private Dimension size;
    public Dimension size(){return (Dimension)size.clone();}
    
    //Initiallising         
    
    public void newGame(){
        iteration = 0;
        initialState = new GameState(size);        
        currentState = initialState;
        grid.setState(currentState);
        if (delegate!=null) delegate.gameDidRenew();
    }
    
    public GameModel(Dimension size){
        this.size = size;
        grid = new GameGrid(size);
        grid.addGridListener(this);
        initialState = new GameState(size);    
        currentState = initialState;
        grid.setState(currentState);
        
        timer = new Timer(150, this);        
        geometry = this.makeGeometry_EuclideanFlat();
        geometryIndex = Geometry_EuclideanFlat;       
    }
    
    //Methods/variables for iteration analysis
    private GameGeometry geometry;
    private int geometryIndex;
    
    public int getGeometry(){return geometryIndex;}
    
    public void setGeometry(int geo){
        switch (geo) {
            case Geometry_EuclideanFlat:
                geometry = this.makeGeometry_EuclideanFlat();
                break;
            case Geometry_EuclideanTorus:
                geometry = this.makeGeometry_EuclideanTorus();
                break;
            default:
                throw new AssertionError();
        }
        geometryIndex = geo;
    }    
    
    //Private geometry factory methods
    private GameGeometry makeGeometry_EuclideanFlat(){
        return new GameGeometry() {
            @Override
            Point[] getAdjacentPoint(Point p) {
                Point[] out = new Point[8];
                int leftX = p.x-1;
                int rightX = p.x+1;
                int topY = p.y-1;
                int bottomY = p.y+1;

                if (topY>=0) {
                    if (leftX>=0) { out[0] = new Point(leftX, topY); }
                    out[1] = new Point (p.x, topY);
                    if (rightX<size.width) { out[2] = new Point(rightX, topY); }
                }
                if (leftX>=0) {out[3] = new Point(leftX, p.y);}
                if (rightX<size.width) {out[4] = new Point(rightX, p.y);}
                if (bottomY<size.height) {
                    if (leftX>=0) {out[5] = new Point(leftX, bottomY);}
                    out[6] = new Point(p.x, bottomY);
                    if (rightX<size.width) {out[7] = new Point(rightX, bottomY);}
                }
                return out;
            }
        };
    }
    
    private GameGeometry makeGeometry_EuclideanTorus(){
        return new GameGeometry() {
            @Override
            Point[] getAdjacentPoint(Point p) {
                int leftX = p.x-1>=0 ?          p.x-1:
                                                size.width-1;
                int rightX = p.x+1<size.width?  p.x+1:
                                                0;
                int topY = p.y-1>=0?            p.y-1:
                                                size.height-1;
                int bottomY = p.y+1<size.height?p.y+1:
                                                0;                        
                return new Point[]{
                    new Point(leftX, topY),
                    new Point (p.x, topY),
                    new Point(rightX, topY),
                    new Point(leftX, p.y),
                    new Point(rightX, p.y),
                    new Point(leftX, bottomY),
                    new Point(p.x, bottomY),
                    new Point(rightX, bottomY),                                
                };
            }
        };
    }
    
    
    //Setting / Getting
    public void setSize(Dimension newSize){
        size = newSize;
        this.newGame();
    }
        
//    public void setGrid(GameGrid g){
//        grid = g;
//    }  
    
    public void loadState(GameState state){
        timer.stop();
        iteration = 0;
        if (!size.equals(state.size)){
            size = state.size;            
        }
        initialState = state;
        currentState = initialState;
        grid.setState(currentState);
        if (delegate!=null){delegate.gameDidReset();}
    }
    
    // Speed = iterations per 10000ms (10s)    
    public void setSpeed(int speed){        
        timer.setDelay(10000 / speed);
    }
    public int getSpeed(){
        return 10000 / timer.getDelay();
    }
    
    //Game Behaviour
    public void reset(){
        timer.stop();
        iteration = 0;
        currentState = initialState;
        grid.setState(currentState);
        if (delegate!=null){delegate.gameDidReset();}
    }    
    
    public void start(){ 
        timer.start(); 
        if (delegate!=null){ delegate.gameDidStart();}
    }
    public void pause(){
        timer.stop();
        if (delegate!=null){ delegate.gameDidPause();}
    }
    
    //Querying
    public boolean isRunning(){
        return timer.isRunning();
    }
    public boolean isReset(){
        return iteration==0;
    }
    
    
    //Actions
    
    public void updateIteration(){
        currentState = currentState.getNextGenerationState(geometry);
        grid.setState(currentState);
        iteration++;
        if (delegate!=null) delegate.gameDidIterate();
    }
    
    // Grid Listener Methods
    @Override
    public void cellClicked(Cell c) {
        Point p = grid.cells.getPointForValue(c);
        currentState.setValueAtPoint(c.isLive(), p);
    }

    @Override
    public void gridDidResize() {
        if (delegate!=null) delegate.gridDidResize();
    }
    
    // Called on every iteration of the timer
    @Override
    public void actionPerformed(ActionEvent e) {
        updateIteration();
    }

    //Geometry class
    public abstract class GameGeometry{
        /**
         * Returns the adjacent points in a size-8 Point array in order left to right, top to bottom:<br/>
         * <ol>
         *  <li>top-left </li>
         *  <li>top-middle </li>
         *  <li>top-right </li>  
         *   <li>middle-left</li>
         *   <li>middle-right</li>
         *   <li>bottom-left</li>
         *   <li>bottom-middle</li>
         *    <li>bottom-right</li>
         * </ol>
         * 
         * @param p central point
         * @return 
         */
        abstract Point[] getAdjacentPoint(Point p);   
    }    
}

interface GameModelDelegate {
    void gameDidIterate();
    void gameDidStart();
    void gameDidPause();
    void gameDidReset();
    void gameDidRenew();
    void gridDidResize();
}