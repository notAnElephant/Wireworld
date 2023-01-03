package view;

public class MainWindow {


    static final int matrixWidth = 14, matrixHeight = 13;
    static final int timeBetweenSteps = 500;
    static final int cellSize = 40;

    /**
     * main method
     * @param args
     */
    public static void main(String[] args) {
        CellGrid cellGrid = new CellGrid(matrixWidth, matrixHeight, timeBetweenSteps);
        GUI mainGUI = new GUI(matrixWidth, matrixHeight, cellSize, cellGrid, timeBetweenSteps);
        mainGUI.initializeLayout();
    }
}
