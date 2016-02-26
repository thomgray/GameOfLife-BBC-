package gameoflife;

import java.awt.Dimension;
import java.awt.Point;
import java.lang.reflect.Array;
import java.util.Iterator;

/**
 *
 * @author thomdikdave
 * @param <T>
 */
public class Grid<T> implements Iterable<T>, Cloneable {
    private Object[][] data;
    public final Dimension size;

    
    public Grid(Dimension size){
        this.size = size;
        data = new Object[size.width][size.height];
    }
    public Grid(int width, int height){
        this(new Dimension(width, height));
    }
    
    /**
     * 
     * @param in the new grid entry
     * @param p point of entry
     * @return T old value
     */
    public T setValueAtPoint(T in, Point p){
        T out = (T)data[p.x][p.y];
        data[p.x][p.y] = in;
        return out;
    }
    public T setValueAtPoint(T in, int x, int y){
        return this.setValueAtPoint(in, new Point(x,y));
    }
    public T getValueAtPoint(Point p){
        return (T)data[p.x][p.y];
    }
    public T getValueAtPoint(int x, int y){
        return this.getValueAtPoint(new Point(x,y));
    }
    public Point getPointForValue(T in){
        for (int i = 0; i < size.width; i++) {
            for (int j = 0; j < size.height; j++) {
                T thing = (T) data[i][j];
                if (thing == in) { return new Point(i,j);}
            }
        }
        return null;
    }
    void populateGrid(ObjectFactory<T> factory){
        for (int i = 0; i < size.width; i++) {
            for (int j = 0; j < size.height; j++) {
                data[i][j] = factory.newObject();
            }
        }
    }
    
    
    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            Point m = new Point(0,0);
            @Override
            public boolean hasNext() {
                return m.y < size.height;
            }

            @Override
            public T next() {
                T out = (T)data[m.x][m.y];
                if (m.x == size.width-1){
                    m.x=0;
                    m.y++;
                }else{
                    m.x++;
                }
                return out;
            }
        };
    }
    
    public Iterator<Point> iteratePoints(){
        return new Iterator<Point>() {
            Point m = new Point(0,0);
            @Override
            public boolean hasNext() {
                return m.y < size.height;
            }

            @Override
            public Point next() {
                Point out = (Point)m.clone();
                if (m.x == size.width-1){
                    m.x=0;
                    m.y++;
                }else{
                    m.x++;
                }
                return out;
            }
        };
    }
    
           
    
    /**
     * This will return a shallow copy of the original grid. Unless the type of this object copies by value, 
     * you will have to perform your own deep copy if that's what is needed
     * @return 
     */
    @Override
    protected Object clone() {        
        try {
            Grid<T> out = (Grid<T>)super.clone();
            Object[][] outData = new Object[data.length][0];
            for (int i = 0; i < outData.length; i++) {
                outData[i] = data[i].clone();
            }
            out.data = outData;
            return out;
        } catch (CloneNotSupportedException ex) {
            throw new java.lang.InternalError();
        }
    }
       
}

interface ObjectFactory<T> {
    T newObject();
}