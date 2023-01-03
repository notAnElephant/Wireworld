package view;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.*;

public class SavedSimsTest {

    SavedSims savedSims;
    CellGrid cellGrid;
    String filename = "test1.wire";

    /**
     * setting up a new SavedSims and CellGrid
     */
    @Before
    public void setUp() {
        savedSims = new SavedSims(".wire");
        cellGrid = new CellGrid(13, 14, 500);
    }

    /**
     * tests the loadFromFile method. We try to load the content of a file.
     * The file contains a 13*14 grid with all cells empty.
     * If a cell isn't empty, something went wrong
     * @throws IOException
     * @throws ClassNotFoundException
     */
    @Test
    public void loadFromFile() throws IOException, ClassNotFoundException {
        cellGrid = new CellGrid(0, 0, 500);
        cellGrid = savedSims.loadFromFile(new File(filename));
        assertEquals("size of the cellgrid is incorrect", 13, cellGrid.cells.length);
        for (int i = 0; i < cellGrid.cells.length; i++) {
            for (int j = 0; j < cellGrid.cells[0].length; j++) {
                assertEquals("state of a cell is incorrect",
                        CellState.EMPTY, cellGrid.cells[i][j].getCellstate());
            }
        }
    }

    /**
     * tests the saveToFile method.
     * After saving, the file's size shouldn't be 0.
     * @throws IOException
     */
    @Test
    public void saveToFile() throws IOException {
        savedSims.saveToFile(cellGrid, new File(filename));
        assertNotEquals(0, Files.size(Paths.get(filename)));
    }
}