package gameoflife;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSlider;
import javax.swing.KeyStroke;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author thomdikdave
 */
public class GOLFrame extends JFrame implements ActionListener, GameModelDelegate {
            
    private final static int frameSizeHorizontalPadding = 50;
    private final static int frameSizeVerticalPadding = 90;    
    public static final int maximumGridWidth = 150;
    public static final int maximumGridHeight = 80;
        
    final GameModel model;
    
    //Window components
    final GameGrid grid; 
    
    CustomButton startButton;
    CustomButton resetButton;
    JLabel iterationLabel;
    JSlider speedSlider;
    
    //Menu components
    JMenuBar menuBar;
    JMenu optionsMenu;
    JMenuItem openGameFile;
    JMenuItem saveCurrentState;
    JMenuItem setGridSizeItem;
    
    //Established Color Schemes
    ArrayList<ColorScheme> colorSchemes;
    
//------------------------------------------------------
//-------Initialising-------
//------------------------------------------------------
    
    public GOLFrame(String name){        
        super(name);
        model = new GameModel(new Dimension(50 , 30));        
        grid = model.grid;
        this.getContentPane().setBackground(new Color(240,240,240));
        this.setLayout(new GridBagLayout());
        
        initialiseWindow();
        initialiseMenu();  
        initialiseColorSchemes();
        this.gridDidResize(); //just to promt the frame resizing
    }

    /**
     * Called on initilisation. This method handles:
     * <ul>
     *  <li>initial layout management</li>
     *  <li>creates and adds the window components to the frame. These are:
     * <br> -Start/Pause and Clear/Reset buttons
     * <br> -Grid
     * <br> -Iteration Label
     * <br> -Speed modifier slider
     * </li>
     * 
     * </ul>
     */
    private void initialiseWindow(){  
        model.delegate = this;
        
        GridBagConstraints gs = new GridBagConstraints();                
        Dimension buttonSize = new Dimension(90,25);
        startButton = new CustomButton("Start");         
        startButton.addActionListener(this);
        startButton.setPreferredSize(buttonSize);  
        
        // Add accelerator to start button
        AbstractAction startAccelerator = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startButton.doClick();
            }
        };        
        startButton.getActionMap().put("startAccelerator", startAccelerator);
        startButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).
                put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0), "startAccelerator" );
        startButton.getInputMap(JComponent.WHEN_FOCUSED).remove(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0));
        
        resetButton = new CustomButton("Clear");
        resetButton.addActionListener(this);
        resetButton.setPreferredSize(buttonSize);
        
        //Add accelerator to reset button
        AbstractAction resetAccelerator = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetButton.doClick();
            }
        };
        
        resetButton.getActionMap().put("resetAccelerator", resetAccelerator);
        resetButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).
                put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "resetAccelerator");
        
        iterationLabel = new JLabel("Iteration: 0");
        iterationLabel.setFont(new Font(GameOfLife.GameFontFamily, Font.PLAIN, 13));
        iterationLabel.setForeground(GameOfLife.GameBlue);
        iterationLabel.setHorizontalAlignment(JLabel.RIGHT);
        
        speedSlider = new JSlider(5,150);
        speedSlider.setValue(model.getSpeed());        
        speedSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {                
                model.setSpeed(speedSlider.getValue());
            }
        });     
        
        this.add(startButton, gs);
        gs.gridx = 1;
        this.add(resetButton, gs);
        gs.gridx = 2;
        gs.anchor = GridBagConstraints.ABOVE_BASELINE_TRAILING;
        this.add(iterationLabel, gs);        
        
        gs.insets.bottom = 5;
        gs.insets.top = 5;
        gs.gridy = 1;
        gs.gridx = 0;
        gs.anchor = GridBagConstraints.ABOVE_BASELINE;
        gs.gridwidth = GridBagConstraints.REMAINDER;
        this.add(grid, gs);
        
        gs.insets.bottom = 0;
        gs.insets.top = 0;
        gs.gridwidth = GridBagConstraints.REMAINDER;
        gs.anchor = GridBagConstraints.ABOVE_BASELINE_LEADING;
        gs.gridy = 2;
        gs.gridx = 0;
        this.add(speedSlider, gs);  
        
        model.setSize(model.size());
    }
    
//------------------------------------------------------
//-------Callled on initialisation to set up the menu bar-------
//------------------------------------------------------

    private void initialiseMenu(){
        menuBar = new JMenuBar();
        
        //Make options menu and open/save/gridSize menu items
        optionsMenu = new JMenu("Options");
        openGameFile = new JMenuItem("Load State...");
        openGameFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.META_MASK));
        saveCurrentState = new JMenuItem("Save State...");
        saveCurrentState.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.META_MASK));        
        
        openGameFile.addActionListener((ActionEvent e) -> {
            openGame();
        });
        saveCurrentState.addActionListener((ActionEvent e) -> {
            saveState();
        });              
        
        setGridSizeItem = new JMenuItem("Set Grid Size...");
        setGridSizeItem.addActionListener((ActionEvent e)->{
            promptGridResize();
        });
        
        this.setJMenuBar(menuBar);
        menuBar.add(optionsMenu);
        optionsMenu.add(openGameFile);
        optionsMenu.add(saveCurrentState);
        optionsMenu.addSeparator();
        optionsMenu.add(setGridSizeItem);
        
        //color scheme option
        JMenuItem colorSchemeSelection = new JMenuItem("Set Colour Scheme...");
        colorSchemeSelection.addActionListener((e)->{
            launchColorSchemeSelection();
        });
        
        optionsMenu.add(colorSchemeSelection);
        optionsMenu.addSeparator();     
        
        //Make geometry option                
        JRadioButtonMenuItem geometryFlatItem = new JRadioButtonMenuItem("Euclidean Flat");
        geometryFlatItem.setSelected(true);
        JRadioButtonMenuItem geometryTorusItem = new JRadioButtonMenuItem("Euclidean Torus");
        
        ButtonGroup geometryGroup = new ButtonGroup();
        geometryGroup.add(geometryFlatItem);
        geometryGroup.add(geometryTorusItem);        
        
        ActionListener geometriesListener = (e)->{
            JRadioButtonMenuItem source = (JRadioButtonMenuItem)e.getSource();
            if (source==geometryFlatItem && model.getGeometry()!=GameModel.Geometry_EuclideanFlat) {
                model.setGeometry(GameModel.Geometry_EuclideanFlat);
            }else if (source==geometryTorusItem && model.getGeometry()!=GameModel.Geometry_EuclideanTorus) {
                model.setGeometry(GameModel.Geometry_EuclideanTorus);
            }
        };
        
        geometryFlatItem.addActionListener(geometriesListener);
        geometryTorusItem.addActionListener(geometriesListener);
        
        JMenu geometryMenu = new JMenu("Geometry");
        geometryMenu.add(geometryFlatItem);
        geometryMenu.add(geometryTorusItem);
                
        optionsMenu.add(geometryMenu);
        
        initialiseTemplates();   
    }
    
//------------------------------------------------------
//-------Deferred from the initialiseMenu method-------
//------------------------------------------------------  

    private void initialiseTemplates(){
        JMenu templates = new JMenu("Templates");                
        JMenuItem GGGItem = new JMenuItem("Gosper Glider Gun");
        JMenuItem tagalong = new JMenuItem("Spaceship");
        JMenuItem reciprocatingAgar = new JMenuItem("Reciprocating Agar");
        
        ActionListener templatesListener = (e) -> {
            ClassLoader cl = GameOfLife.class.getClassLoader();
            JMenuItem source = (JMenuItem)e.getSource();
            String path = null;
            if (source==GGGItem) {
                path = cl.getResource("resources/GosperGliderGun.txt").getFile();
            }else if (source==tagalong) {
                path = cl.getResource("resources/Spaceship.txt").getFile();
            }else if (source==reciprocatingAgar) {
                path = cl.getResource("resources/ReciprocationAgar.txt").getFile();
            }
            
            if (path!=null) {
                String stateString = readGameFromPath(path);
                model.loadState(GameState.readStateFromString(stateString));
            }
        };
            
        GGGItem.addActionListener(templatesListener);
        tagalong.addActionListener(templatesListener);
        reciprocatingAgar.addActionListener(templatesListener);
        
        menuBar.add(templates);        
        templates.add(GGGItem); 
        templates.add(tagalong);
        templates.add(reciprocatingAgar);
    }
    
//------------------------------------------------------
//-------Initialise the colour schemes-------
//------------------------------------------------------  

    private void initialiseColorSchemes(){
        colorSchemes = new ArrayList<>();
        
        colorSchemes.add(new ColorScheme(
                new Color(220,220,220),
                new Color(173,123,220),
                new Color(219,125,125),
                new Color(107,200,182),
                new Color(170,200,107)                        
            ));
        
        colorSchemes.add(new ColorScheme( // black and blank
                null,
                new Color(0,0,0)                        
            ));
        colorSchemes.add(new ColorScheme( // white and blue
                Color.WHITE,
                GameOfLife.GameBlue                
            ));
        
        grid.setColorScheme(colorSchemes.get(0));    
    }
    
    
//------------------------------------------------------
//-------Button Actions-------
//------------------------------------------------------
    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source == startButton){
            if (model.isRunning()){
                model.pause();                
            }else{
                model.start();                
            }            
        }else if (source == resetButton){
            if (model.isReset()){
                model.newGame();
            }else{
                model.reset();
            }
        }
    }

//------------------------------------------------------
//-------Model Actions-------
//------------------------------------------------------
    @Override
    public void gameDidIterate() {
        iterationLabel.setText("Iteration: " + model.iteration);        
    }

    @Override
    public void gameDidStart() {
        resetButton.setText("Reset");
        startButton.setText("Pause");
    }

    @Override
    public void gameDidPause() {
        startButton.setText("Play");        
    }

    @Override
    public void gameDidReset() {
        resetButton.setText("Clear");
        startButton.setText("Play");        
        iterationLabel.setText("Iteration: 0");
    }

    @Override
    public void gameDidRenew() {
        resetButton.setText("Clear");
        iterationLabel.setText("Iteration: 0");
    }   

    @Override
    public void gridDidResize() {        
        Dimension prefSize= this.getPreferredSize();        
        this.setMinimumSize(prefSize);
        this.setSize(prefSize);
    }
        

    @Override
    public Dimension getPreferredSize() {        
        Dimension gridSize = grid.getPreferredSize();
        int componentWidths = startButton.getPreferredSize().width + 
                resetButton.getPreferredSize().width + 
                iterationLabel.getPreferredSize().width + 40;
        
        return new Dimension(
                (gridSize.width<componentWidths ? componentWidths:gridSize.width) + GOLFrame.frameSizeHorizontalPadding,
                gridSize.height + startButton.getPreferredSize().height /**for component padding**/ + 
                        (System.getProperty("os.name").contains("Mac")? 0:20) /*depending on the menu bar's location*/ + 
                        GOLFrame.frameSizeVerticalPadding                        
            );
    }
    
//------------------------------------------------------
//-------Deferred Menu Actions-------
//------------------------------------------------------
    void openGame(){
        JFileChooser fc = new JFileChooser(); 
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Text (*.txt)", "txt");
        fc.setFileFilter(filter);
        int result= fc.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION){
            File file = fc.getSelectedFile();
            String gameString = this.readGameFromPath(file.getAbsolutePath());
            if (gameString!=null) {
                GameState state = GameState.readStateFromString(gameString);
                model.loadState(state);
            }
        }
    }
    
    private String readGameFromPath (String path){
        try {
                FileReader reader = new FileReader(path);
                BufferedReader bReader = new BufferedReader(reader);
                String line;
                String output = "";
                while ((line=bReader.readLine())!= null) { 
                    output += line + "\n";                 
                }
                return output.substring(0, output.length()); //remove last newline char            
            } catch (FileNotFoundException ex) {
                return null;
            } catch (IOException ex) {
                return null;
            }        
    }
    
    void saveState(){
        JFileChooser fc = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Text (*.txt)", "txt");
        fc.setFileFilter(filter);
        
        int result = fc.showSaveDialog(this);
        if (result==JFileChooser.APPROVE_OPTION) {
            String stringState = model.currentState.toString();
            String outString = fc.getSelectedFile().getPath();
            if (!outString.endsWith(".txt")) {
                outString += ".txt";
            }
            File outFile = fc.getSelectedFile();
            
            try{
                FileWriter writer = new FileWriter(outFile);
                BufferedWriter bWriter = new BufferedWriter(writer);
                bWriter.write(stringState);
                bWriter.close();
            }catch(FileNotFoundException e){
                System.out.println("Error opening file");
            }catch (IOException e){
                System.out.println("Error writing to file");
            }
        }
    }

    /**
     *Deferred from the 'Set Grid Size' JMenuItem. Launches a new modal window with a <code>ResizeOptionPane</code>
     * content pane. Listens for actions sent from the pane and adjusts the grid size accordingly, calling 
     * <code>model.setGridSize(newSize)</code>
     * 
     */
    
    private void promptGridResize() {
        ResizeOptionPane pane = new ResizeOptionPane(model.size());                
        JDialog dialog = new JDialog(this, "Set Grid Size", true);
        
        // Have the modal close if the pane sends an action
        pane.setActionListener((e)->{
            dialog.setVisible(false);
        });
        
        dialog.setModal(true);
        dialog.setContentPane(pane);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);
        dialog.setVisible(true); 
        
        //run modal ...
        
        if (pane.modalResult==ResizeOptionPane.modalResultDone) {
            Dimension newSize = pane.size;
            if (!newSize.equals(model.size())) {
                model.setSize(newSize);
            }            
        }       
    }

    /**
     * Deferred from actions received from the 'set colour scheme' menu item. Sets up a new modal window
     * with a <code>ColorSchemePicker</code> content pane. Listens for actions sent from that pane, and 
     * sets the prevailing colour scheme accordingly.
     */
    private void launchColorSchemeSelection() {
        ColorScheme thisScheme = grid.getColorScheme();
        JDialog dialog = new JDialog(this, "Colour Scheme", true);
        ColorSchemePicker picker = new ColorSchemePicker(colorSchemes); 
        picker.setSchemeSelection(colorSchemes.indexOf(thisScheme));
        
        picker.delegate = new ColorSchemePickerDelegate() {
            @Override
            public void colorSchemeDidSelect(ColorScheme scheme) {
                grid.setColorScheme(scheme);
            }
            @Override
            public void selectionDidEnd(boolean apply) {
                dialog.setVisible(false);
                if (!apply) {
                    grid.setColorScheme(thisScheme);
                }
            }
        };
        
        dialog.setContentPane(picker);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);
        dialog.setVisible(true);        
    }    
}

/**
 * Defines the color for 'deal' cell state, as well as an array of 'live' state colors. 
 * <p>Objects using this object can either query these properties or call the 
 * <code>randomCellColor()</code> method for a random 'live' color </p>
 * <p>Note ColorScheme's are immutable</p>
 * @author thomdikdave
 */
class ColorScheme {
    final Color[] cellColors;
    final Color deadCellColor;
    
    ColorScheme(Color deadColor , Color... cellColors) {
        this.cellColors = cellColors;
        this.deadCellColor = deadColor;
    }
    
    Color randomCellColor(){
        if (cellColors.length==0) return null;
        else if (cellColors.length==1) return cellColors[0];
        int i = (int)(Math.random()*(double)cellColors.length);
        return cellColors[i];
    }
}
