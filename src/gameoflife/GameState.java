package gameoflife;

import java.awt.Dimension;
import java.awt.Point;
import java.lang.reflect.Array;
import java.util.Iterator;

/**
 *
 * @author thomdikdave
 */
public class GameState extends Grid<Boolean> {
    
    public GameState(int size){
        this(new Dimension(size, size));        
    }
    public GameState(int width, int height){
        this(new Dimension(width, height));
    }
    public GameState(Dimension size){
        super(size);
        this.populateGrid(()-> false);
    }
    
    public static GameState readStateFromStrings(String[] strings){
        int maxWidth = 0;
        for (String s : strings) {
            if (s.length()>maxWidth) maxWidth = s.length();
        }
        GameState out = new GameState(maxWidth, strings.length);
        for (int i = 0; i < strings.length; i++) {
            for (int j = 0; j < strings[i].length(); j++) {
                char c = strings[i].charAt(j);
                if (c=='*'){
                    out.setValueAtPoint(true, j, i);
                }
            }
        }
        return out;
    }
    
    public static GameState readStateFromString(String s){
        String[] split = s.split("\n");
        return GameState.readStateFromStrings(split);
    }
    
    public int countLives(Point[] points){
        int out = 0;
        for (Point p : points) {
            if (p!=null && this.getValueAtPoint(p)) out++;
        }
        return out;
    }       
    
    public GameState getNextGenerationState(GameModel.GameGeometry geo){        
        GameState out = (GameState)this.clone();
        for (Iterator<Point> it = this.iteratePoints(); it.hasNext();){
            Point p = it.next();
            Point[] adjacents = geo.getAdjacentPoint(p);
            
            boolean isLiving = this.getValueAtPoint(p);
            int adjLives = this.countLives(adjacents);
            
            if (isLiving && adjLives < 2) { // Underpopulation
                out.setValueAtPoint(false, p);
            }else if (isLiving && adjLives > 3){ // Overcrowding
                out.setValueAtPoint(false, p);
            }else if (!isLiving && adjLives == 3){ //Creation
                out.setValueAtPoint(true, p);
            } // else nothing changes;
        }
        return out;
    }
    
    @Override
    public String toString() {
        String out = "";        
        for (Iterator<Point>it = this.iteratePoints(); it.hasNext();){
            Point p = it.next();
            boolean val = this.getValueAtPoint(p);
            out += val? "*":"-";
            if (p.x==size.width-1 && p.y<size.height-1) out += "\n";
        }
        return out;
    }
    
    

}
