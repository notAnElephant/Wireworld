package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;

public class WireworldGUI {

    private static final int DEFAULT_TIME_BETWEEN_STEPS = 500;

    private final CellGrid cellGrid; 
    private final SavedSims savedSims; 

    private final int gridWidth;
    private final int gridHeight;
    private final int cellSize;

    private JFrame frame;
    private JPanel buttonPanel;
    private JButton nextButton;
    private JButton stopButton;
    private JSpinner stepTimeSpinner;
    private JLabel mainLabel;

    public WireworldGUI(int gridWidth, int gridHeight, int cellSize, CellGrid cellGrid) {
        this.gridWidth = gridWidth;
        this.gridHeight = gridHeight;
        this.cellSize = cellSize;
        this.cellGrid = cellGrid;
        this.savedSims = new SavedSims(".wire"); 
    }

    public void initializeLayout() {
        createFrame();
        createControlPanel();
        createGridPanel(); 
        createMenuBar();

        frame.pack(); 
        frame.setVisible(true);
    }

    private void createFrame() {
        frame = new JFrame("Wireworld szimulációkészítő");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.setResizable(false);
    }

    private void createControlPanel() {
        JPanel controlPanel = new JPanel(new FlowLayout());

        nextButton = new JButton("Következő lépés");
        nextButton.addActionListener(this::handleNextStep);
        controlPanel.add(nextButton);

        stopButton = new JButton("Leállítás"); // Initially disabled
        stopButton.setEnabled(false); 
        stopButton.addActionListener(this::handleStopSimulation);
        controlPanel.add(stopButton);

        controlPanel.add(new JLabel("A szimuláció sebessége: "));
        stepTimeSpinner = new JSpinner(new SpinnerNumberModel(DEFAULT_TIME_BETWEEN_STEPS, 100, 1000, 100));
        stepTimeSpinner.setToolTipText("Milliszekundumban");
        controlPanel.add(stepTimeSpinner);

        controlPanel.add(new JButton("Ok")).addActionListener(this::handleChangeStepTime);

        mainLabel = new JLabel("Szerkesztés mód"); 
        mainLabel.setFont(new Font("Arial", Font.BOLD, 30));
        controlPanel.add(mainLabel);

        frame.add(controlPanel, BorderLayout.SOUTH);
    }

    private void createGridPanel() {
        buttonPanel = new JPanel(new GridLayout(gridHeight, gridWidth));
        drawGrid(); 
        frame.add(buttonPanel, BorderLayout.CENTER);
    }

    private void createMenuBar() {
        // ... (Implementation based on your needs, simplified for this example)
    }

    public void drawGrid() { 
        // ... (Your existing implementation) 
    }

    // Action Listeners - Refactored for clarity
    private void handleNextStep(ActionEvent e) {
        try {
            cellGrid.nextStep();
        } catch (WrongCellTypeException ex) {
            ex.printStackTrace();
        }
    }

    private void handleStopSimulation(ActionEvent e) {
        cellGrid.stopSimulation();
        mainLabel.setText("Szerkesztés mód");
        stopButton.setEnabled(false);
        nextButton.setEnabled(true);
    }

    private void handleChangeStepTime(ActionEvent e) {
        cellGrid.setTimeBetweenSteps((int) stepTimeSpinner.getValue());
    }

    // ... add other menu bar and event handlers for file operations ...
}
