package GraphicalTicTacToeFinal;
import java.awt.*;

/**
 * Enhanced Cell class with improved dynamic sizing and rendering
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
        PADDING = Math.max(SIZE / 6, 8); // Ensure minimum padding of 8px
        SEED_SIZE = SIZE - PADDING * 2;

        System.out.println("Cell size updated: " + SIZE + "px (Padding: " + PADDING + "px, Seed: " + SEED_SIZE + "px)");
    }

    /** Get cell center coordinates */
    public Point getCenterPoint() {
        return new Point(col * SIZE + SIZE / 2, row * SIZE + SIZE / 2);
    }

    /** Check if a point is within this cell */
    public boolean contains(int x, int y) {
        return x >= col * SIZE && x < (col + 1) * SIZE &&
                y >= row * SIZE && y < (row + 1) * SIZE;
    }

    /** Paint itself on the graphics canvas, given the Graphics context */
    public void paint(Graphics g) {
        // Draw the Seed if it is not empty
        int x1 = col * SIZE + PADDING;
        int y1 = row * SIZE + PADDING;

        if (content == Seed.CROSS || content == Seed.NOUGHT) {
            // Enable anti-aliasing for smoother image rendering
            if (g instanceof Graphics2D) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            }

            if (content.getImage() != null) {
                g.drawImage(content.getImage(), x1, y1, SEED_SIZE, SEED_SIZE, null);
            } else {
                // Fallback: draw text if image is not available
                drawTextFallback(g, x1, y1);
            }
        }
    }

    /** Fallback method to draw text when images are not available */
    private void drawTextFallback(Graphics g, int x, int y) {
        g.setFont(new Font("Arial", Font.BOLD, SEED_SIZE / 2));
        FontMetrics fm = g.getFontMetrics();

        String text = content.getDisplayName();
        int textWidth = fm.stringWidth(text);
        int textHeight = fm.getHeight();

        // Center the text in the cell
        int textX = x + (SEED_SIZE - textWidth) / 2;
        int textY = y + (SEED_SIZE + textHeight) / 2 - fm.getDescent();

        // Set color based on content
        if (content == Seed.CROSS) {
            g.setColor(new Color(239, 105, 80)); // Red for X
        } else if (content == Seed.NOUGHT) {
            g.setColor(new Color(64, 154, 225)); // Blue for O
        }

        g.drawString(text, textX, textY);
    }
}
