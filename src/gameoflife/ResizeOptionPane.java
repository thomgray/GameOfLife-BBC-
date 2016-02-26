package gameoflife;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.GroupLayout;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.KeyStroke;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 * @author thomdikdave
 */
public class ResizeOptionPane extends JPanel
implements ChangeListener {
    
    private static final int miniatureDotDiameter = 3;
    public static final int modalResultCancel = 0;
    public static final int modalResultDone = 1;
    
    //For deferring button click actions
    private ActionListener listener;
    public void setActionListener(ActionListener al){listener = al;}
    public ActionListener getActionListener(){return listener;}
    
    //For querying the outcome of the modal window closure    
    public int modalResult = 0;
    
    public Dimension size;  
    
    //Components
    JSlider widthSlider;
    JSlider heightSlider;
    MiniatureGrid grid;
    
    CustomButton cancelButton;
    CustomButton doneButton;
    
    JSpinner widthSpinner;
    JSpinner heightSpinner;
    JLabel widthLabel;
    JLabel heightLabel;
    
    public ResizeOptionPane(Dimension d){
        super();
        size = (Dimension) d.clone();        
        initPane();
        initInputs();
    }
    
    private void initPane(){
        widthSlider = new JSlider(3, GOLFrame.maximumGridWidth);  
        widthSlider.setValue(size.width);
        widthSlider.addChangeListener(this);
        widthSlider.setMinorTickSpacing(1);
        widthSlider.setSnapToTicks(true);
        widthSlider.setPaintTicks(true);
        widthSlider.setPaintTrack(false);        
        
        heightSlider = new JSlider(3, GOLFrame.maximumGridHeight);
        heightSlider.setOrientation(SwingConstants.VERTICAL);
        heightSlider.setValue(size.height);
        heightSlider.addChangeListener(this);
        heightSlider.setMinorTickSpacing(1);
        heightSlider.setSnapToTicks(true);
        heightSlider.setPaintTicks(true);
        heightSlider.setInverted(true);
        heightSlider.setPaintTrack(false);                
        
        widthSpinner = new JSpinner();
        widthSpinner.setModel(new SpinnerNumberModel(size.width, 3, GOLFrame.maximumGridWidth, 1));
        widthSpinner.addChangeListener(this);
        widthSpinner.setValue(size.width);
        widthLabel = new JLabel("width:");        
        
        heightSpinner = new JSpinner();
        heightSpinner.setModel(new SpinnerNumberModel(size.height, 3, GOLFrame.maximumGridHeight, 1));
        heightSpinner.addChangeListener(this);
        heightSpinner.setValue(size.height);                        
        heightLabel = new JLabel("height:");
        
        grid = new MiniatureGrid();        
        Dimension gridSize = grid.getPreferredSize();        
        grid.setSize(gridSize);
        
        heightSlider.setPreferredSize(new Dimension(25, gridSize.height+10));
        widthSlider.setPreferredSize(new Dimension(gridSize.width+10, 25));
                
        Font buttonFont = new Font(GameOfLife.GameFontFamily, Font.PLAIN, 13);
        Dimension buttonSize = new Dimension(60,25);
        cancelButton = new CustomButton("Cancel");
        cancelButton.setFont(buttonFont);
        cancelButton.setPreferredSize(buttonSize);
        doneButton = new CustomButton("Done");
        doneButton.setFont(buttonFont);
        doneButton.setPreferredSize(buttonSize);
        
        ActionListener e = (ActionEvent act) -> {
            if (act.getSource()==doneButton){
                modalResult = modalResultDone;
            }else modalResult = modalResultCancel;
            if (listener!=null) listener.actionPerformed(act);              
        };
        
        doneButton.addActionListener(e);
        cancelButton.addActionListener(e);           
               
        manageLayout();          
    }    
    
    private void initInputs(){                
        InputMap im = new InputMap();
        ActionMap actions = this.getActionMap();
        
        this.setFocusable(true);
        this.setFocusCycleRoot(true);
        
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), "downArrow");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), "upArrow");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), "rightArrow");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), "leftArrow");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "enter");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "escape");
            
        actions.put("downArrow", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                heightSlider.setValue(heightSlider.getValue()+1);
            }
        });
        actions.put("upArrow", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                heightSlider.setValue(heightSlider.getValue()-1);
            }
        });
        actions.put("leftArrow", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                widthSlider.setValue(widthSlider.getValue()-1);
            }
        });
        actions.put("rightArrow", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                widthSlider.setValue(widthSlider.getValue()+1);
            }
        });
        actions.put("enter", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                doneButton.doClick();
            }
        });
        actions.put("escape", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cancelButton.doClick();
            }
        });
        
        this.setInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, im);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(
                grid.getPreferredSize().width + 70,
                grid.getPreferredSize().height + 100
        );
    }        


    private void manageLayout(){
        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);            
        
        int layoutInset = 5;
        //required to keep the centre of the slider level with the grid height/width on screen
        int sliderExcess = 12; 
        
        layout.setHorizontalGroup(layout.createSequentialGroup()    
                .addGap(layoutInset)
                .addComponent(heightSlider)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                        .addComponent(widthSlider, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(grid, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addGap(sliderExcess)
                        )                                
                        .addGroup(layout.createSequentialGroup()                                
                                .addComponent(widthLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addComponent(widthSpinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addGap(5)
                                .addComponent(heightLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addComponent(heightSpinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)                                                                
                                
                                .addGap(60)
                                
                                .addComponent(doneButton, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addComponent(cancelButton, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addGap(sliderExcess)
                        )
                )
                .addGap(layoutInset)
        );
        
        layout.setVerticalGroup(layout.createSequentialGroup()
                .addGap(layoutInset)
                .addComponent(widthSlider, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addGap(layoutInset)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)                
                    .addComponent(heightSlider, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(grid, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)                    
                                .addGap(sliderExcess)
                        )
                    
                )
                .addGap(10) // control-button gap
                .addGroup(layout.createParallelGroup() 
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)                                
                            .addComponent(widthLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addComponent(widthSpinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addComponent(heightLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addComponent(heightSpinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        )
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                            .addComponent(doneButton, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addComponent(cancelButton, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        )
                        
                        
                )
                .addGap(layoutInset)
        );
    }

    
    //Change Listener event for sliders and spinners
    @Override
    public void stateChanged(ChangeEvent e) {
        Object source = e.getSource();        
        if (source==widthSlider) {            
            size.width = widthSlider.getValue();
            int spinnerVal = (int)widthSpinner.getValue();
            if (spinnerVal != size.width){
                widthSpinner.setValue(size.width);
            }
        }else if (source==heightSlider){
            size.height = heightSlider.getValue();
            int spinnerValue = (int)heightSpinner.getValue();
            if (spinnerValue != size.height) {
                heightSpinner.setValue(size.height);
            }
        }else if (source==widthSpinner){
            size.width = (int)widthSpinner.getValue();
            if (widthSlider.getValue() != size.width) {
                widthSlider.setValue(size.width);
            }
        }else if (source==heightSpinner) {
            size.height = (int)heightSpinner.getValue();
            if (heightSlider.getValue() != size.height) {
                heightSlider.setValue(size.height);
            }
        }
        grid.repaint();
    }        
    
    class MiniatureGrid extends JComponent {

        @Override
        protected void paintComponent(Graphics g) {                        
            Graphics2D g2 = (Graphics2D)g;
            g2.setColor(GameOfLife.GameBlue);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Dimension d = this.getSize();
            
            for (int i = 0; i < size.width; i++) {
                for (int j = 0; j < size.height; j++) {
                    int x = i * (miniatureDotDiameter+1);
                    int y = j * (miniatureDotDiameter+1);
                    
                    g2.fillOval(x, y, miniatureDotDiameter, miniatureDotDiameter);
                }
            }            
        }                
        
        @Override
        public Dimension getPreferredSize() {            
            return new Dimension(
                    (GOLFrame.maximumGridWidth * miniatureDotDiameter) + GOLFrame.maximumGridWidth -1,
                    (GOLFrame.maximumGridHeight * miniatureDotDiameter) + GOLFrame.maximumGridHeight -1
                );
        }
        
       Dimension getRepresentedGridDimension(){
            return new Dimension(
                    size.width * (miniatureDotDiameter+1),
                    size.height * (miniatureDotDiameter+1)
                );
       }
    
    }                
}

