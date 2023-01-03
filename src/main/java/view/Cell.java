package view;

import javax.swing.*;
import java.awt.*;
import java.io.Serializable;
import java.util.Arrays;

/**
 *
 */
public class Cell extends JButton implements Serializable {

    private static final CellState[] cellCarousel = {CellState.EMPTY, CellState.WIRE, CellState.HEAD, CellState.TAIL};
    private final int xInM, yInM;
    private CellState cellstate;
    public boolean needsEvaluation = false;
    public CellState nextCellState = CellState.EMPTY;

    /**
     * getter for the cellstate property
     * @return cell's state
     */
    public CellState getCellstate() {
        return cellstate;
    }

    /**
     * setter for the cellstate property
     * @param cellstate
     */
    public void setCellstate(CellState cellstate) {
        this.cellstate = cellstate;
    }

    /**
     * getter for the getxInM property
     * @return x coordinate in the grid
     */
    public int getxInM() {
        return xInM;
    }

    /**
     * getter for the getyInM property
     * @return y coordinate in the grid
     */
    public int getyInM() {
        return yInM;
    }

    /**
     * Cell constructor
     * @param cellstate current state of the cell
     * @param xInM the x coordinate of the cell in the grid (0-based)
     * @param yInM the y coordinate of the cell in the grid (0-based)
     */
    public Cell(CellState cellstate, int xInM, int yInM) {
        this.cellstate = cellstate;
        this.xInM = xInM;
        this.yInM = yInM;
        updateCellBackground();
    }

    /**
     * sets the cell to the next state
     */
    public void nextCellState() {
        cellstate = cellCarousel[(Arrays.asList(cellCarousel).indexOf(cellstate) + 1) % cellCarousel.length];
    }

    /**
     * updates the cell's background according to it's state
     */
    public void updateCellBackground() {
        Color color;
        switch (cellstate) {
            case HEAD -> color = Color.blue;
            case TAIL -> color = Color.red;
            case WIRE -> color = Color.gray;
            default -> color = Color.black;
        }
        setBackground(color);
    }
}