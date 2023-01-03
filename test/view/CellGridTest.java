package view;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class CellGridTest {

    CellGrid cellGrid;
    final int height = 14, width = 13;
    int[][] oneClick, multipleClicks;

    /**
     * sets up the test class - it creates a simulation of four cells, 2 wire cells,
     * a head and a tail. The electron is able to move infinitely in this loop, theoretically.
     */
    @Before
    public void setUp() {
        cellGrid = new CellGrid(width, height, 500);
        oneClick = new int[][]{{2, 0}, {3, 1}, {1, 1}, {2, 2}};
        multipleClicks = new int[][]{{3, 1}, {2, 2}, {2, 2}};
        for (int[] ints : oneClick) {
            cellGrid.cells[ints[0]][ints[1]].nextCellState();
            cellGrid.evaluableCells.add(cellGrid.cells[ints[0]][ints[1]]);
        }
        for (int[] multipleClick : multipleClicks) {
            cellGrid.cells[multipleClick[0]][multipleClick[1]].nextCellState();
        }
    }

    /**
     * tests the clearAllCells method. There shouldn't be any non empty cells left after
     * clearing the whole grid (i.e. setting all cells to empty). Also, cellGrid.evaluableCells
     * should be empty, hence its size should be 0.
     */
    @Test
    public void clearAllCells() {
        cellGrid.clearAllCells();
        assertEquals("nem ures az evaluableCells", 0, cellGrid.evaluableCells.size());
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                assertEquals(String.format("nem ures az %s, %s cella", j, i),
                        CellState.EMPTY, cellGrid.cells[j][i].getCellstate());
            }
        }
    }

    /**
     * tests the nextStep method. We make the simulation evaluate two iterations with the nextStep
     * method. The cells should all be in the state given in the desiredStates array.
     * @throws WrongCellTypeException
     */
    @Test
    public void nextStep() throws WrongCellTypeException {
        cellGrid.nextStep();
        cellGrid.nextStep();
        CellState[] desiredStates = new CellState[]{CellState.TAIL, CellState.WIRE, CellState.HEAD, CellState.WIRE};

        for (int i = 0; i < oneClick.length; i++) {
            assertEquals(String.format("rossz allapotban van a(z) %s. cella", i), desiredStates[i], cellGrid.cells[oneClick[i][0]][oneClick[i][1]].getCellstate());
        }

    }

    /**
     * tests the fillEvaluableCells method. The size of evaluableCells should be 4.
     */
    @Test
    public void fillEvaluableCells() {
        cellGrid.fillEvaluableCells();
        assertEquals("nem megfelelo az evaluableCells merete", 4, cellGrid.evaluableCells.size());
    }
}