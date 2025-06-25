package GraphicalTicTacToeFinal;

import java.awt.*;

/**
 * The Board class models the ROWS-by-COLS game board.
 * Enhanced with auto-layout capabilities for responsive sizing.
 */
public class Board {
    // Dynamic board size (default 3x3)
    public static int ROWS = 3;
    public static int COLS = 3;

    // Win condition - berapa banyak yang perlu dalam satu baris untuk menang
    public static int WIN_CONDITION = 3;

    // Define named constants for drawing - now dynamic
    public static int CANVAS_WIDTH = Cell.SIZE * COLS;
    public static int CANVAS_HEIGHT = Cell.SIZE * ROWS;
    public static final int GRID_WIDTH = 8;
    public static final int GRID_WIDTH_HALF = GRID_WIDTH / 2;
    public static final Color COLOR_GRID = Color.LIGHT_GRAY;
    public static final int Y_OFFSET = 1;

    // Enhanced auto-layout constants
    private static final int MIN_CELL_SIZE = 60;  // Reduced for smaller screens
    private static final int MAX_CELL_SIZE = 180; // Increased for larger screens
    private static final int UI_PADDING = 150;    // Reduced padding for better space utilization

    // Define properties (package-visible)
    /** Composes of 2D array of ROWS-by-COLS Cell instances */
    Cell[][] cells;

    /** Constructor to initialize the game board */
    public Board() {
        calculateOptimalCellSize();
        initGame();
    }

    /** Enhanced calculation for optimal cell size based on screen dimensions */
    private void calculateOptimalCellSize() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int screenWidth = (int) screenSize.getWidth();
        int screenHeight = (int) screenSize.getHeight();

        // Get usable screen area (excluding taskbar, etc.)
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        Rectangle bounds = ge.getMaximumWindowBounds();
        int usableWidth = bounds.width;
        int usableHeight = bounds.height;

        // Use the smaller of actual screen or usable area
        int effectiveWidth = Math.min(screenWidth, usableWidth);
        int effectiveHeight = Math.min(screenHeight, usableHeight);

        // Calculate available space (smaller dimension minus UI padding)
        int availableSpace = Math.min(effectiveWidth, effectiveHeight) - UI_PADDING;

        // Calculate cell size based on board dimensions with some margin
        int calculatedSize = (availableSpace * 85 / 100) / Math.max(ROWS, COLS); // Use 85% of available space

        // Ensure cell size is within reasonable bounds
        int optimalSize = Math.max(MIN_CELL_SIZE, Math.min(MAX_CELL_SIZE, calculatedSize));

        // For 3x3 boards, prefer slightly larger cells if possible
        if (ROWS == 3 && COLS == 3 && optimalSize < 100 && availableSpace > 350) {
            optimalSize = Math.min(120, (availableSpace * 90 / 100) / 3);
        }

        // Update cell size
        Cell.updateSize(optimalSize);
        updateCanvasDimensions();

        System.out.println("Auto-Layout Calculation:");
        System.out.println("- Screen: " + screenWidth + "x" + screenHeight);
        System.out.println("- Usable area: " + effectiveWidth + "x" + effectiveHeight);
        System.out.println("- Available space: " + availableSpace + "px");
        System.out.println("- Calculated cell size: " + optimalSize + "px");
        System.out.println("- Board dimensions: " + CANVAS_WIDTH + "x" + CANVAS_HEIGHT + "px");
    }

    /** Update canvas dimensions based on current cell size */
    private void updateCanvasDimensions() {
        CANVAS_WIDTH = Cell.SIZE * COLS;
        CANVAS_HEIGHT = Cell.SIZE * ROWS;
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

    /** Set board size and reinitialize with auto-layout */
    public void setBoardSize(int rows, int cols) {
        ROWS = rows;
        COLS = cols;

        // Set win condition based on board size
        switch (rows) {
            case 3:
                WIN_CONDITION = 3;
                break;
            case 4:
                WIN_CONDITION = 4;
                break;
            case 5:
                WIN_CONDITION = 4; // 4 in a row for 5x5 for better balance
                break;
            default:
                WIN_CONDITION = Math.min(rows, 4); // Max 4 in a row for larger boards
                break;
        }

        // Recalculate optimal cell size for new board dimensions
        calculateOptimalCellSize();
        initGame();
    }

    /** Get optimal window size for current board configuration */
    public Dimension getOptimalWindowSize() {
        // Add extra space for menu bar, status bar, and window decorations
        return new Dimension(CANVAS_WIDTH + 40, CANVAS_HEIGHT + 120);
    }

    /** Get current cell size for external reference */
    public int getCurrentCellSize() {
        return Cell.SIZE;
    }

    /** Get board efficiency ratio (how much of screen is used) */
    public double getScreenUtilizationRatio() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        double screenArea = screenSize.getWidth() * screenSize.getHeight();
        double boardArea = CANVAS_WIDTH * CANVAS_HEIGHT;
        return (boardArea / screenArea) * 100;
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
        // Enable anti-aliasing for smoother graphics
        if (g instanceof Graphics2D) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        }

        // Draw the grid-lines with dynamic sizing
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
