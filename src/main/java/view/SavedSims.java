package view;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.io.*;

public class SavedSims {

    private final JFileChooser fileChooser = new JFileChooser();
    private final String extension;

    /**
     * constructor of SavedSims
     * @param extension the file extension to use at saving and loading files (must start with a dot)
     */
    public SavedSims(String extension) {
        this.extension = extension;
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.addChoosableFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                if (f.isDirectory()) {
                    return false;
                } else {
                    String filename = f.getName().toLowerCase();
                    return filename.endsWith(extension);
                }
            }

            @Override
            public String getDescription() {
                return String.format("%s files (*%s)", extension.toUpperCase().substring(1), extension);
            }
        });
    }
    /**
     * getter of fileChooser
     * @return fileChooser
     */
    public JFileChooser getFileChooser() {
        return fileChooser;
    }

    /**
     * loads from the given file
     * @param file the file to load from
     * @return a CellGrid with the deserialized content of the file
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public CellGrid loadFromFile(File file) throws IOException, ClassNotFoundException {
        FileInputStream fis = new FileInputStream(file);
        ObjectInputStream ois = new ObjectInputStream(fis);
        CellGrid cellGrid = (CellGrid) ois.readObject();
        cellGrid.initializeExecutor();
        ois.close();
        fis.close();
        cellGrid.setListenerForCells();
        return cellGrid;
    }

    /**
     * saves the given cellgrid to the given file
     * @param cellGrid the cellgrid to save
     * @param file the file to save to
     * @throws IOException
     */
    public void saveToFile(CellGrid cellGrid, File file) throws IOException {

        //if the selected file doesn't end properly, attach the extension
        if (!file.getName().endsWith(extension))
            file = new File(file.getAbsolutePath() + extension);

        FileOutputStream fos = new FileOutputStream(file);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(cellGrid);
        oos.close();
        fos.close();
    }
}
