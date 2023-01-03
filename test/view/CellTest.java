package view;

import org.junit.Before;
import org.junit.Test;

import java.awt.*;

import static org.junit.Assert.*;

public class CellTest {

    Cell cell;

    /**
     * we set up one empty cell at {0, 0}
     */
    @Before
    public void setUp() {
        cell = new Cell(CellState.EMPTY, 0, 0);
    }

    /**
     * tests the getCellstate method. We shift the cell's state by one.
     * The new state should be wire.
     */
    @Test
    public void getCellstate() {
        cell.nextCellState();
        assertEquals(CellState.WIRE, cell.getCellstate());
    }

    /**
     * tests the setCellState method. We set the cell's state to head.
     * The cell's state should now be equal to head.
     */
    @Test
    public void setCellstate() {
        cell.setCellstate(CellState.HEAD);
        assertEquals(CellState.HEAD, cell.getCellstate());
    }

    /**
     * tests the nextCellState method. We shift the cell's state 30 times.
     * Its state should now be equal to head (30 % 4 == 2, empty->wire->head).
     */
    @Test
    public void nextCellState() {
        for (int i = 0; i < 30; i++) {
            cell.nextCellState();
        }
        assertEquals(CellState.HEAD, cell.getCellstate());
    }

    /**
     * tests the updateCellBackground method. We set the cell's state to tail,
     * then update its background. Now the cell's background should be red.
     */
    @Test
    public void updateCellBackground() {
        cell.setCellstate(CellState.TAIL);
        cell.updateCellBackground();
        assertEquals(Color.red, cell.getBackground());
    }
}