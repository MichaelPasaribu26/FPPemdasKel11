package GraphicalTicTacToeFinal;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Enhanced Board class with modern visual effects and animations
 */
public class Board {
    // Dynamic board size (default 3x3)
    public static int ROWS = 3;
    public static int COLS = 3;
    public static int WIN_CONDITION = 3;

    // Enhanced visual constants
    public static int CANVAS_WIDTH = Cell.SIZE * COLS;
    public static int CANVAS_HEIGHT = Cell.SIZE * ROWS;
    public static final int GRID_WIDTH = 6;
    public static final int GRID_WIDTH_HALF = GRID_WIDTH / 2;

    // Modern color scheme
    public static final Color COLOR_GRID = new Color(220, 220, 220);
    public static final Color COLOR_GRID_SHADOW = new Color(180, 180, 180);
    public static final Color COLOR_BOARD_BG = new Color(248, 249, 250);
    public static final Color COLOR_CELL_HOVER = new Color(230, 240, 255, 100);
    public static final Color COLOR_WINNING_LINE = new Color(255, 215, 0, 200); // Gold

    // Auto-layout constants
    private static final int MIN_CELL_SIZE = 60;
    private static final int MAX_CELL_SIZE = 200;
    private static final int UI_PADDING = 120;

    // Game state
    Cell[][] cells;
    private List<Point> winningLine = new ArrayList<>();
    private Point hoveredCell = null;
    private long lastMoveTime = 0;
    private boolean showWinAnimation = false;

    public Board() {
        calculateOptimalCellSize();
        initGame();
    }

    /** Enhanced calculation with better screen utilization */
    private void calculateOptimalCellSize() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        Rectangle bounds = ge.getMaximumWindowBounds();

        int effectiveWidth = Math.min((int)screenSize.getWidth(), bounds.width);
        int effectiveHeight = Math.min((int)screenSize.getHeight(), bounds.height);
        int availableSpace = Math.min(effectiveWidth, effectiveHeight) - UI_PADDING;

        // Enhanced calculation for better visual balance
        int calculatedSize = (availableSpace * 88 / 100) / Math.max(ROWS, COLS);
        int optimalSize = Math.max(MIN_CELL_SIZE, Math.min(MAX_CELL_SIZE, calculatedSize));

        // Special optimization for different board sizes
        if (ROWS == 3 && COLS == 3 && optimalSize < 120 && availableSpace > 400) {
            optimalSize = Math.min(140, (availableSpace * 92 / 100) / 3);
        } else if (ROWS >= 5 && optimalSize > 120) {
            optimalSize = Math.min(120, optimalSize); // Limit for larger boards
        }

        Cell.updateSize(optimalSize);
        updateCanvasDimensions();

        System.out.println("🎨 Enhanced Auto-Layout:");
        System.out.println("   Screen: " + effectiveWidth + "x" + effectiveHeight);
        System.out.println("   Cell size: " + optimalSize + "px");
        System.out.println("   Board: " + CANVAS_WIDTH + "x" + CANVAS_HEIGHT + "px");
    }

    private void updateCanvasDimensions() {
        CANVAS_WIDTH = Cell.SIZE * COLS;
        CANVAS_HEIGHT = Cell.SIZE * ROWS;
    }

    public void initGame() {
        cells = new Cell[ROWS][COLS];
        for (int row = 0; row < ROWS; ++row) {
            for (int col = 0; col < COLS; ++col) {
                cells[row][col] = new Cell(row, col);
            }
        }
        winningLine.clear();
        showWinAnimation = false;
    }

    public void setBoardSize(int rows, int cols) {
        ROWS = rows;
        COLS = cols;

        // Enhanced win condition logic
        switch (rows) {
            case 3: WIN_CONDITION = 3; break;
            case 4: WIN_CONDITION = 4; break;
            case 5: WIN_CONDITION = 4; break;
            case 6: WIN_CONDITION = 5; break;
            default: WIN_CONDITION = Math.min(rows, 5); break;
        }

        calculateOptimalCellSize();
        initGame();
    }

    public Dimension getOptimalWindowSize() {
        return new Dimension(CANVAS_WIDTH + 50, CANVAS_HEIGHT + 140);
    }

    public int getCurrentCellSize() {
        return Cell.SIZE;
    }

    public double getScreenUtilizationRatio() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        double screenArea = screenSize.getWidth() * screenSize.getHeight();
        double boardArea = CANVAS_WIDTH * CANVAS_HEIGHT;
        return (boardArea / screenArea) * 100;
    }

    public void newGame() {
        for (int row = 0; row < ROWS; ++row) {
            for (int col = 0; col < COLS; ++col) {
                cells[row][col].newGame();
            }
        }
        winningLine.clear();
        showWinAnimation = false;
        lastMoveTime = System.currentTimeMillis();
    }

    /** Enhanced hover effect */
    public void setHoveredCell(int row, int col) {
        if (row >= 0 && row < ROWS && col >= 0 && col < COLS) {
            hoveredCell = new Point(col, row);
        } else {
            hoveredCell = null;
        }
    }

    public State stepGame(Seed player, int selectedRow, int selectedCol) {
        cells[selectedRow][selectedCol].content = player;
        lastMoveTime = System.currentTimeMillis();

        if (hasWon(player, selectedRow, selectedCol)) {
            showWinAnimation = true;
            return (player == Seed.CROSS) ? State.CROSS_WON : State.NOUGHT_WON;
        } else {
            // Check for draw
            for (int row = 0; row < ROWS; ++row) {
                for (int col = 0; col < COLS; ++col) {
                    if (cells[row][col].content == Seed.NO_SEED) {
                        return State.PLAYING;
                    }
                }
            }
            return State.DRAW;
        }
    }

    private boolean hasWon(Seed player, int row, int col) {
        // Check all directions and store winning line
        if (checkDirection(player, row, col, 0, 1)) return true;  // Horizontal
        if (checkDirection(player, row, col, 1, 0)) return true;  // Vertical
        if (checkDirection(player, row, col, 1, 1)) return true;  // Diagonal \
        if (checkDirection(player, row, col, 1, -1)) return true; // Diagonal /
        return false;
    }

    private boolean checkDirection(Seed player, int row, int col, int deltaRow, int deltaCol) {
        List<Point> line = new ArrayList<>();
        line.add(new Point(col, row));

        // Check positive direction
        int r = row + deltaRow;
        int c = col + deltaCol;
        while (r >= 0 && r < ROWS && c >= 0 && c < COLS && cells[r][c].content == player) {
            line.add(new Point(c, r));
            r += deltaRow;
            c += deltaCol;
        }

        // Check negative direction
        r = row - deltaRow;
        c = col - deltaCol;
        while (r >= 0 && r < ROWS && c >= 0 && c < COLS && cells[r][c].content == player) {
            line.add(new Point(c, r));
            r -= deltaRow;
            c -= deltaCol;
        }

        if (line.size() >= WIN_CONDITION) {
            winningLine = line;
            return true;
        }
        return false;
    }

    /** Enhanced paint method with modern effects */
    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        // Enable high-quality rendering
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

        // Draw board background with subtle gradient
        GradientPaint bgGradient = new GradientPaint(0, 0, COLOR_BOARD_BG,
                CANVAS_WIDTH, CANVAS_HEIGHT,
                COLOR_BOARD_BG.brighter());
        g2d.setPaint(bgGradient);
        g2d.fillRect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);

        // Draw hover effect
        if (hoveredCell != null) {
            g2d.setColor(COLOR_CELL_HOVER);
            g2d.fillRect(hoveredCell.x * Cell.SIZE, hoveredCell.y * Cell.SIZE,
                    Cell.SIZE, Cell.SIZE);
        }

        // Draw enhanced grid with shadow effect
        drawEnhancedGrid(g2d);

        // Draw all cells
        for (int row = 0; row < ROWS; ++row) {
            for (int col = 0; col < COLS; ++col) {
                cells[row][col].paint(g2d);
            }
        }

        // Draw winning line animation
        if (showWinAnimation && !winningLine.isEmpty()) {
            drawWinningLine(g2d);
        }
    }

    private void drawEnhancedGrid(Graphics2D g2d) {
        // Draw grid shadow first
        g2d.setColor(COLOR_GRID_SHADOW);
        g2d.setStroke(new BasicStroke(GRID_WIDTH + 2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        // Horizontal lines shadow
        for (int row = 1; row < ROWS; ++row) {
            int y = Cell.SIZE * row;
            g2d.drawLine(2, y + 1, CANVAS_WIDTH - 2, y + 1);
        }

        // Vertical lines shadow
        for (int col = 1; col < COLS; ++col) {
            int x = Cell.SIZE * col;
            g2d.drawLine(x + 1, 2, x + 1, CANVAS_HEIGHT - 2);
        }

        // Draw main grid
        g2d.setColor(COLOR_GRID);
        g2d.setStroke(new BasicStroke(GRID_WIDTH, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        // Horizontal lines
        for (int row = 1; row < ROWS; ++row) {
            int y = Cell.SIZE * row;
            g2d.drawLine(0, y, CANVAS_WIDTH, y);
        }

        // Vertical lines
        for (int col = 1; col < COLS; ++col) {
            int x = Cell.SIZE * col;
            g2d.drawLine(x, 0, x, CANVAS_HEIGHT);
        }
    }

    private void drawWinningLine(Graphics2D g2d) {
        if (winningLine.size() < 2) return;

        // Animated winning line
        long currentTime = System.currentTimeMillis();
        float alpha = (float)(0.7 + 0.3 * Math.sin((currentTime - lastMoveTime) / 200.0));

        g2d.setColor(new Color(COLOR_WINNING_LINE.getRed(),
                COLOR_WINNING_LINE.getGreen(),
                COLOR_WINNING_LINE.getBlue(),
                (int)(alpha * 255)));
        g2d.setStroke(new BasicStroke(8, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        // Draw line through winning cells
        Point start = winningLine.get(0);
        Point end = winningLine.get(winningLine.size() - 1);

        int startX = start.x * Cell.SIZE + Cell.SIZE / 2;
        int startY = start.y * Cell.SIZE + Cell.SIZE / 2;
        int endX = end.x * Cell.SIZE + Cell.SIZE / 2;
        int endY = end.y * Cell.SIZE + Cell.SIZE / 2;

        g2d.drawLine(startX, startY, endX, endY);

        // Draw glow effect
        g2d.setStroke(new BasicStroke(16, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2d.setColor(new Color(255, 215, 0, 30));
        g2d.drawLine(startX, startY, endX, endY);
    }

    public List<Point> getWinningLine() {
        return winningLine;
    }

    public boolean isShowingWinAnimation() {
        return showWinAnimation;
    }
}
