package GraphicalTicTacToeWithSoundEffectAndImagesAndScore;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class GameMain extends JPanel {
    private static final long serialVersionUID = 1L;

    // Define named constants
    public static final String TITLE = "Tic Tac Toe";
    public static final Color COLOR_BG = Color.WHITE;
    public static final Color COLOR_BG_STATUS = new Color(216, 216, 216);
    public static final Color COLOR_CROSS = new Color(239, 105, 80);
    public static final Color COLOR_NOUGHT = new Color(64, 154, 225);
    public static final Font FONT_STATUS = new Font("OCR A Extended", Font.PLAIN, 14);

    // Define game objects
    private Board board;
    private State currentState;
    private Seed currentPlayer;
    private JLabel statusBar;

    // Add score tracking
    private int crossWins = 0;
    private int noughtWins = 0;

    /** Constructor to setup the UI and game components */
    public GameMain() {
        // Mouse listener remains the same
        super.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int mouseX = e.getX();
                int mouseY = e.getY();
                int row = mouseY / Cell.SIZE;
                int col = mouseX / Cell.SIZE;

                if (currentState == State.PLAYING) {
                    if (row >= 0 && row < Board.ROWS && col >= 0 && col < Board.COLS
                            && board.cells[row][col].content == Seed.NO_SEED) {
                        currentState = board.stepGame(currentPlayer, row, col);

                        // Update scores if game is won
                        if (currentState == State.CROSS_WON) {
                            crossWins++;
                        } else if (currentState == State.NOUGHT_WON) {
                            noughtWins++;
                        }

                        currentPlayer = (currentPlayer == Seed.CROSS) ? Seed.NOUGHT : Seed.CROSS;
                    }
                    // Sound effects remain the same
                    if (currentState == State.PLAYING) {
                        SoundEffect.EAT_FOOD.play();
                    } else {
                        SoundEffect.DIE.play();
                    }
                } else {
                    newGame();
                }
                repaint();
            }
        });

        // Status bar setup remains the same
        statusBar = new JLabel();
        statusBar.setFont(FONT_STATUS);
        statusBar.setBackground(COLOR_BG_STATUS);
        statusBar.setOpaque(true);
        statusBar.setPreferredSize(new Dimension(300, 30));
        statusBar.setHorizontalAlignment(JLabel.LEFT);
        statusBar.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 12));

        super.setLayout(new BorderLayout());
        super.add(statusBar, BorderLayout.PAGE_END);
        super.setPreferredSize(new Dimension(Board.CANVAS_WIDTH, Board.CANVAS_HEIGHT + 30));
        super.setBorder(BorderFactory.createLineBorder(COLOR_BG_STATUS, 2, false));

        initGame();
        newGame();
    }

    /** Initialize the game (run once) */
    public void initGame() {
        board = new Board();
    }

    /** Reset the game-board contents and the current-state, ready for new game */
    public void newGame() {
        for (int row = 0; row < Board.ROWS; ++row) {
            for (int col = 0; col < Board.COLS; ++col) {
                board.cells[row][col].content = Seed.NO_SEED;
            }
        }
        currentPlayer = Seed.CROSS;
        currentState = State.PLAYING;
    }

    /** Custom painting codes on this JPanel */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        setBackground(COLOR_BG);

        board.paint(g);

        // Update status bar with scores
        String statusText;
        if (currentState == State.PLAYING) {
            statusBar.setForeground(Color.BLACK);
            statusText = (currentPlayer == Seed.CROSS) ? "X's Turn" : "O's Turn";
        } else if (currentState == State.DRAW) {
            statusBar.setForeground(Color.RED);
            statusText = "It's a Draw! Click to play again.";
        } else if (currentState == State.CROSS_WON) {
            statusBar.setForeground(COLOR_CROSS);
            statusText = "'X' Won! Click to play again.";
        } else {
            statusBar.setForeground(COLOR_NOUGHT);
            statusText = "'O' Won! Click to play again.";
        }

        // Add scores to status text
        statusText += " | Score: X " + crossWins + " - " + noughtWins + " O";
        statusBar.setText(statusText);
    }

    /** The entry "main" method */
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JFrame frame = new JFrame(TITLE);
                frame.setContentPane(new GameMain());
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
    }

    // Optional: Add methods to reset scores
    public void resetScores() {
        crossWins = 0;
        noughtWins = 0;
        repaint();
    }

    // Optional: Add getters for scores
    public int getCrossWins() {
        return crossWins;
    }

    public int getNoughtWins() {
        return noughtWins;
    }
}