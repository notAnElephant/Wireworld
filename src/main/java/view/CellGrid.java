package view;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.Serializable;
import java.util.HashSet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class CellGrid implements Serializable {

    private int timeBetweenSteps;

    private final int width, height;
    private transient ScheduledExecutorService executor;
    private transient ScheduledFuture<?> scheduledSimulation;
    public Cell[][] cells;
    public HashSet<Cell> evaluableCells;

    /**
     * constructor of CellGrid
     * @param width width of the grid
     * @param height height of the matrix
     * @param timeBetweenSteps the time elapsed between each two steps in the simulation
     */
    public CellGrid(int width, int height, int timeBetweenSteps) {
        this.timeBetweenSteps = timeBetweenSteps;
        this.width = width;
        this.height = height;
        cells = new Cell[width][height];
        evaluableCells = new HashSet<>();
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                Cell cell = new Cell(CellState.EMPTY, j, i);
                cells[j][i] = cell;
            }
        }
        setListenerForCells();
        initializeExecutor();
    }

    /**
     * initializes the executor for the simulation
     */
    public void initializeExecutor() {
        executor = Executors.newScheduledThreadPool(1);
    }

    /**
     * setter for timeBetweenSteps
     * @param value
     */
    public void setTimeBetweenSteps(int value) {
        timeBetweenSteps = value;
    }

    /**
     * sets the proper mouselistener for all cells in the grid
     */
    public void setListenerForCells() {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                cells[j][i].addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        super.mousePressed(e);
                        Cell cell = (Cell) e.getSource();
                        //left button: next state
                        if (e.getButton() == 1) {
                            cell.nextCellState();
                            if (cell.getCellstate() != CellState.EMPTY)
                                evaluableCells.add(cell);
                            else
                                evaluableCells.remove(cell);
                        }
                        //right button: delete (aka set cell to empty)
                        else if (e.getButton() == 3) {
                            clearCell(cell);
                        }
                        cell.updateCellBackground();
                    }

                    //"paint" mode
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        super.mouseEntered(e);
                        Cell cell = (Cell) e.getSource();
                        //left button - paint wire
                        if (SwingUtilities.isLeftMouseButton(e)) {
                            cell.setCellstate(CellState.WIRE);
                            cell.updateCellBackground();
                            evaluableCells.add(cell);
                        }
                        //right button - delete
                        else if (SwingUtilities.isRightMouseButton(e)) {
                            clearCell(cell);
                        }
                    }
                });
            }
        }
    }

    /**
     * clears the given cell (i.e. sets it to empty, removes it from evaluableCells and repaints it)
     * @param cell the cell to clear
     */
    private void clearCell(Cell cell) {
        cell.setCellstate(CellState.EMPTY);
        cell.nextCellState = CellState.EMPTY;
        cell.needsEvaluation = false;
        evaluableCells.remove(cell);
        cell.updateCellBackground();
    }

    /**
     * clears all cells in the grid using clearCell
     */
    public void clearAllCells() {
        evaluableCells.clear();
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                clearCell(cells[j][i]);
            }
        }
    }

    /**
     * runs the simulation infinitely
     * @throws InterruptedException interrupted exception
     */
    public void runSimulation() throws InterruptedException {
        Runnable nextStepRunnable = () -> {
            try {
                nextStep();
            } catch (WrongCellTypeException e) {
                e.printStackTrace();
            }
        };
        scheduledSimulation = executor.scheduleAtFixedRate(nextStepRunnable, 0, timeBetweenSteps, TimeUnit.MILLISECONDS);
    }

    /**
     * stops the simulation if it's currently running
     */
    public void stopSimulation() {
        if (scheduledSimulation != null)
            scheduledSimulation.cancel(false);
    }

    /**
     * calculates and draws the next iteration of the simulation
     * @throws WrongCellTypeException wrong cell type exception
     */
    public void nextStep() throws WrongCellTypeException {
        //find out which cells need to be upgraded...
        for (Cell cell : evaluableCells) {
            CellState state = CellState.WIRE;
            switch (cell.getCellstate()) {
                case HEAD -> state = CellState.TAIL;
                case WIRE -> {
                    if (ShouldTurnIntoHead(cell)) state = CellState.HEAD;
                }
            }
            cell.needsEvaluation = true;
            cell.nextCellState = state;
        }
        //...and update them at once
        for (Cell cell : evaluableCells) {
            cell.needsEvaluation = false;
            cell.setCellstate(cell.nextCellState);
            cell.updateCellBackground();
        }
    }

    ///
    ///otherwise false.

    /**
     * returns true if exactly 1 or 2 electron heads are next to the given cell, false otherwise
     * @param wireCell the cell to run the function on
     * @return true if exactly 1 or 2 electron heads are next to the given cell, false otherwise
     * @throws WrongCellTypeException wrong cell type exeption
     */
    private boolean ShouldTurnIntoHead(Cell wireCell) throws WrongCellTypeException {
        if (wireCell.getCellstate() != CellState.WIRE)
            throw new WrongCellTypeException("Wrong type. Expected a wire, but got a " + wireCell.getCellstate() + " instead");
        int headsNearby = 0;
        for (int i = wireCell.getyInM() - 1; i <= wireCell.getyInM() + 1; i++) {
            for (int j = wireCell.getxInM() - 1; j <= wireCell.getxInM() + 1; j++) {
                if (!inRange(0, width, j) || !inRange(0, height, i))
                    continue;
                if (cells[j][i].getCellstate() == CellState.HEAD)
                    headsNearby++;
            }
        }
        return headsNearby == 1 || headsNearby == 2;
    }

    /**
     * fills the evaluableCells with the cells that aren't empty
     */
    public void fillEvaluableCells() {
        evaluableCells.clear();
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (cells[j][i].getCellstate() != CellState.EMPTY)
                    evaluableCells.add(cells[j][i]);
            }
        }
    }

    /**
     * calculates whether x is in between low (inclusive) and high (exclusive)
     * @param low
     * @param high
     * @param x
     * @return true if x is between low and high, false otherwise
     */
    private boolean inRange(int low, int high, int x) {
        return x < high && x >= low;
    }

}
