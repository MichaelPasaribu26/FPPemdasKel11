package GraphicalTicTacToeFinal;

import java.awt.*;

/**
 * The Board class models the ROWS-by-COLS game board.
 * Now supports dynamic board sizes (3x3, 4x4, 5x5)
 */
public class Board {
    // Dynamic board size (default 3x3)
    public static int ROWS = 3;
    public static int COLS = 3;

    // Win condition - berapa banyak yang perlu dalam satu baris untuk menang
    public static int WIN_CONDITION = 3;

    // Define named constants for drawing
    public static int CANVAS_WIDTH = Cell.SIZE * COLS;
    public static int CANVAS_HEIGHT = Cell.SIZE * ROWS;
    public static final int GRID_WIDTH = 8;
    public static final int GRID_WIDTH_HALF = GRID_WIDTH / 2;
    public static final Color COLOR_GRID = Color.LIGHT_GRAY;
    public static final int Y_OFFSET = 1;

    // Define properties (package-visible)
    /** Composes of 2D array of ROWS-by-COLS Cell instances */
    Cell[][] cells;

    /** Constructor to initialize the game board */
    public Board() {
        initGame();
    }

    /** Initialize the game objects (run once) */
    public void initGame() {
        cells = new Cell[ROWS][COLS];
        for (int row = 0; row < ROWS; ++row) {
            for (int col = 0; col < COLS; ++col) {
                cells[row][col] = new Cell(row, col);
            }
        }
    }

    /** Set board size and reinitialize */
    public void setBoardSize(int rows, int cols) {
        ROWS = rows;
        COLS = cols;
        CANVAS_WIDTH = Cell.SIZE * COLS;
        CANVAS_HEIGHT = Cell.SIZE * ROWS;

        // Set win condition based on board size
        if (rows == 3) {
            WIN_CONDITION = 3;
        } else if (rows == 4) {
            WIN_CONDITION = 4;
        } else if (rows == 5) {
            WIN_CONDITION = 4; // 4 in a row for 5x5 for better balance
        }

        initGame();
    }

    /** Reset the game board, ready for new game */
    public void newGame() {
        for (int row = 0; row < ROWS; ++row) {
            for (int col = 0; col < COLS; ++col) {
                cells[row][col].newGame();
            }
        }
    }

    /**
     * The given player makes a move on (selectedRow, selectedCol).
     * Update cells[selectedRow][selectedCol]. Compute and return the
     * new game state (PLAYING, DRAW, CROSS_WON, NOUGHT_WON).
     */
    public State stepGame(Seed player, int selectedRow, int selectedCol) {
        // Update game board
        cells[selectedRow][selectedCol].content = player;

        // Check if current player won
        if (hasWon(player, selectedRow, selectedCol)) {
            return (player == Seed.CROSS) ? State.CROSS_WON : State.NOUGHT_WON;
        } else {
            // Check for draw (all cells occupied)
            for (int row = 0; row < ROWS; ++row) {
                for (int col = 0; col < COLS; ++col) {
                    if (cells[row][col].content == Seed.NO_SEED) {
                        return State.PLAYING; // still have empty cells
                    }
                }
            }
            return State.DRAW; // no empty cell, it's a draw
        }
    }

    /**
     * Check if the current player has won after placing at (row, col)
     * Uses dynamic win condition based on board size
     */
    private boolean hasWon(Seed player, int row, int col) {
        // Check horizontal
        if (checkDirection(player, row, col, 0, 1)) return true;
        // Check vertical
        if (checkDirection(player, row, col, 1, 0)) return true;
        // Check diagonal (top-left to bottom-right)
        if (checkDirection(player, row, col, 1, 1)) return true;
        // Check diagonal (top-right to bottom-left)
        if (checkDirection(player, row, col, 1, -1)) return true;

        return false;
    }

    /**
     * Check if there are WIN_CONDITION consecutive pieces in a direction
     */
    private boolean checkDirection(Seed player, int row, int col, int deltaRow, int deltaCol) {
        int count = 1; // Count the current piece

        // Check in positive direction
        int r = row + deltaRow;
        int c = col + deltaCol;
        while (r >= 0 && r < ROWS && c >= 0 && c < COLS &&
                cells[r][c].content == player) {
            count++;
            r += deltaRow;
            c += deltaCol;
        }

        // Check in negative direction
        r = row - deltaRow;
        c = col - deltaCol;
        while (r >= 0 && r < ROWS && c >= 0 && c < COLS &&
                cells[r][c].content == player) {
            count++;
            r -= deltaRow;
            c -= deltaCol;
        }

        return count >= WIN_CONDITION;
    }

    /** Paint itself on the graphics canvas, given the Graphics context */
    public void paint(Graphics g) {
        // Draw the grid-lines
        g.setColor(COLOR_GRID);
        for (int row = 1; row < ROWS; ++row) {
            g.fillRoundRect(0, Cell.SIZE * row - GRID_WIDTH_HALF,
                    CANVAS_WIDTH - 1, GRID_WIDTH,
                    GRID_WIDTH, GRID_WIDTH);
        }
        for (int col = 1; col < COLS; ++col) {
            g.fillRoundRect(Cell.SIZE * col - GRID_WIDTH_HALF, 0 + Y_OFFSET,
                    GRID_WIDTH, CANVAS_HEIGHT - 1,
                    GRID_WIDTH, GRID_WIDTH);
        }

        // Draw all the cells
        for (int row = 0; row < ROWS; ++row) {
            for (int col = 0; col < COLS; ++col) {
                cells[row][col].paint(g);
            }
        }
    }
}