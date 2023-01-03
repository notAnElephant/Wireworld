package view;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class GUI {

    private final String extension = ".wire";

    final int gridLayoutWidth, gridLayoutHeight, cellSize;
    private final String editMode = "Szerkesztés mód", runMode = "Aktív szimuláció";
    private CellGrid cellGrid;
    private JButton stopButton;
    private JLabel mainLabel;
    private final int timeBetweenSteps;
    private final SavedSims savedSims = new SavedSims(extension);

    /**
     * constructor of the GUI
     * @param gridLayoutWidth width of the gridLayout
     * @param gridLayoutHeight height of the gridLayouyt
     * @param cellSize preferred size of a cell (both width and height)
     * @param cellGrid the cellGrid to draw it's content
     * @param timeBetweenSteps the time elapsed between each two steps in the simulation
     */
    public GUI(int gridLayoutWidth, int gridLayoutHeight, int cellSize, CellGrid cellGrid, int timeBetweenSteps) {
        this.gridLayoutWidth = gridLayoutWidth;
        this.gridLayoutHeight = gridLayoutHeight;
        this.cellSize = cellSize;
        this.cellGrid = cellGrid;
        this.timeBetweenSteps = timeBetweenSteps;
    }

    /**
     * initializes the layout (i.e. creates all the controls and renders them)
     */
    public void initializeLayout() {

        JFrame frame = new JFrame("Wireworld szimulációkészítő");
        JPanel mainPanel = new JPanel(new BorderLayout());
        JPanel controlPanel = new JPanel(new FlowLayout());
        JPanel topPanel = new JPanel(new FlowLayout());
        JPanel buttonPanel = new JPanel(new GridLayout(gridLayoutHeight, gridLayoutWidth));

        drawGrid(buttonPanel);

        JButton nextButton = new JButton("Következő lépés");
        nextButton.addActionListener(x -> {
            try {
                cellGrid.nextStep();
            } catch (WrongCellTypeException e) {
                e.printStackTrace();
            }
        });
        stopButton = new JButton("Leállítás");
        stopButton.addActionListener(x -> {
            cellGrid.stopSimulation();
            mainLabel.setText(editMode);
        });

        JLabel speedLabel = new JLabel("A szimuláció sebessége: ");
        SpinnerModel spinnerModel = new SpinnerNumberModel(timeBetweenSteps, 100, 1000, 100);
        JSpinner stepTimeSpinner = new JSpinner(spinnerModel);
        stepTimeSpinner.setToolTipText("Milliszekundumban");
        JButton stepOkButton = new JButton("Ok");
        stepOkButton.addActionListener(x -> cellGrid.setTimeBetweenSteps((int) stepTimeSpinner.getValue()));

        mainLabel = new JLabel(editMode);
        mainLabel.setFont(new Font("Arial", Font.BOLD, 30));

        topPanel.add(mainLabel);

        controlPanel.add(nextButton);
        controlPanel.add(stopButton);
        controlPanel.add(speedLabel);
        controlPanel.add(stepTimeSpinner);
        controlPanel.add(stepOkButton);

        mainPanel.add(buttonPanel, BorderLayout.CENTER);
        mainPanel.add(controlPanel, BorderLayout.SOUTH);
        mainPanel.add(topPanel, BorderLayout.NORTH);
        frame.add(mainPanel);

        createMenuBar(frame, buttonPanel);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        frame.setResizable(false);
        frame.setVisible(true);
    }

    /**
     * draws the button grid
     * @param buttonPanel the panel to draw the grid on
     */
    public void drawGrid(JPanel buttonPanel) {
        buttonPanel.removeAll();
        for (int i = 0; i < gridLayoutHeight; i++) {
            for (int j = 0; j < gridLayoutWidth; j++) {
                cellGrid.cells[j][i].setPreferredSize(new Dimension(cellSize, cellSize));
                buttonPanel.add(cellGrid.cells[j][i]);
            }
        }
    }

    /**
     * creates the JMenuBar
     * @param frame the frame to create the menubar on
     * @param buttonPanel the panel of the buttons
     */
    private void createMenuBar(JFrame frame, JPanel buttonPanel) {
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("Fájl");
        JMenu simMenu = new JMenu("Szimuláció");
        JMenu miscMenu = new JMenu("Egyéb");

        JMenuItem loadItem = new JMenuItem("Betöltés");
        loadItem.addActionListener(x -> {
            try {
                JFileChooser fileChooser = savedSims.getFileChooser();
                int result = fileChooser.showOpenDialog(buttonPanel);
                if (result != JFileChooser.APPROVE_OPTION) {
                    return;
                }

                CellGrid response = savedSims.loadFromFile(fileChooser.getSelectedFile());
                if (response == null)
                    return;
                cellGrid = response;
                cellGrid.fillEvaluableCells();
                drawGrid(buttonPanel);
                buttonPanel.repaint();
                stopButton.addActionListener(e -> cellGrid.stopSimulation());
            } catch (IOException | ClassNotFoundException e) {
                JOptionPane.showMessageDialog(buttonPanel, "Nem várt hiba lépett fel betöltés közben", "Hiba", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        });

        JMenuItem saveItem = new JMenuItem("Mentés");
        saveItem.addActionListener(x -> {
            try {
                JFileChooser fileChooser = savedSims.getFileChooser();
                int result = fileChooser.showSaveDialog(buttonPanel);
                if (result != JFileChooser.APPROVE_OPTION) {
                    return;
                }

                savedSims.saveToFile(cellGrid, fileChooser.getSelectedFile());
            } catch (IOException e) {
                JOptionPane.showMessageDialog(buttonPanel, "Nem várt hiba lépett fel mentés közben", "Hiba", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        });

        JMenuItem runItem = new JMenuItem("Futtatás");
        runItem.addActionListener(x -> {
            try {
                mainLabel.setText(runMode);
                mainLabel.repaint();
                cellGrid.runSimulation();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        JMenuItem nextStepItem = new JMenuItem("Következő lépés");
        nextStepItem.addActionListener(x -> {
            try {
                cellGrid.nextStep();
            } catch (WrongCellTypeException e) {
                e.printStackTrace();
            }
        });

        JMenuItem clearItem = new JMenuItem("Összes mező törlése");
        clearItem.addActionListener(x ->
        {
            mainLabel.setText(editMode);
            cellGrid.stopSimulation();
            cellGrid.clearAllCells();
            buttonPanel.repaint();
        });

        fileMenu.add(saveItem);
        fileMenu.add(loadItem);
        simMenu.add(nextStepItem);
        simMenu.add(runItem);
        miscMenu.add(clearItem);

        menuBar.add(fileMenu);
        menuBar.add(simMenu);
        menuBar.add(miscMenu);

        frame.setJMenuBar(menuBar);
    }
}
