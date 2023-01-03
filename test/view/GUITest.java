package view;

import org.junit.Before;
import org.junit.Test;

import javax.swing.*;

import java.awt.*;

import static org.junit.Assert.assertEquals;

public class GUITest {

    GUI gui;
    int width = 13, height = 14, timeBetweenSteps = 500, cellSize = 40;

    /**
     * creating a cellGrid and a GUI
     */
    @Before
    public void setUp() {
        CellGrid cellGrid = new CellGrid(width, height, timeBetweenSteps);
        gui = new GUI(width, height, cellSize, cellGrid, timeBetweenSteps);
    }

    /**
     * tests the drawGrid method. After calling the drawGrid method,
     * the JPanel should have height*width components.
     */
    @Test
    public void drawGrid() {
        JPanel buttonPanel = new JPanel(new GridLayout(height, width));
        gui.drawGrid(buttonPanel);
        assertEquals(height * width, buttonPanel.getComponentCount());
    }
}