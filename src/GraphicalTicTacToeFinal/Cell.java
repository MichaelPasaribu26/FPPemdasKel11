package GraphicalTicTacToeFinal;
import java.awt.*;

/**
 * The Cell class models each individual cell of the game board.
 * Modified to support dynamic sizing based on screen dimensions.
 */
public class Cell {
    // Dynamic cell size - will be calculated based on screen size
    public static int SIZE = 120; // default size, will be updated

    // Symbols (cross/nought) are displayed inside a cell, with padding from border
    public static int PADDING = SIZE / 5;
    public static int SEED_SIZE = SIZE - PADDING * 2;

    // Define properties (package-visible)
    /** Content of this cell (Seed.EMPTY, Seed.CROSS, or Seed.NOUGHT) */
    Seed content;
    /** Row and column of this cell */
    int row, col;

    /** Constructor to initialize this cell with the specified row and col */
    public Cell(int row, int col) {
        this.row = row;
        this.col = col;
        content = Seed.NO_SEED;
    }

    /** Reset this cell's content to EMPTY, ready for new game */
    public void newGame() {
        content = Seed.NO_SEED;
    }

    /** Update cell size and recalculate dependent values */
    public static void updateSize(int newSize) {
        SIZE = newSize;
        PADDING = SIZE / 5;
        SEED_SIZE = SIZE - PADDING * 2;
    }

    /** Paint itself on the graphics canvas, given the Graphics context */
    public void paint(Graphics g) {
        // Draw the Seed if it is not empty
        int x1 = col * SIZE + PADDING;
        int y1 = row * SIZE + PADDING;
        if (content == Seed.CROSS || content == Seed.NOUGHT) {
            g.drawImage(content.getImage(), x1, y1, SEED_SIZE, SEED_SIZE, null);
        }
    }
}
