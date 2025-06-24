package GraphicalTicTacToeWithTurnTimer;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class GameMain extends JPanel {
    private static final long serialVersionUID = 1L;

    // Constants
    public static final String TITLE = "Tic Tac Toe";
    public static final Color COLOR_BG = Color.WHITE;
    public static final Color COLOR_BG_STATUS = new Color(216, 216, 216);
    public static final Color COLOR_CROSS = new Color(239, 105, 80);
    public static final Color COLOR_NOUGHT = new Color(64, 154, 225);
    public static final Font FONT_STATUS = new Font("OCR A Extended", Font.PLAIN, 14);
    private static final int TURN_TIME = 10; // 10 seconds per turn

    // Game objects
    private Board board;
    private State currentState;
    private Seed currentPlayer;
    private JLabel statusBar;

    // Score tracking
    private int crossWins = 0;
    private int noughtWins = 0;

    // Turn timer
    private Timer timer;
    private int timeLeft = TURN_TIME;
    private boolean timerRunning = false;

    public GameMain() {
        // Mouse listener
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

                        // Update scores
                        if (currentState == State.CROSS_WON) {
                            crossWins++;
                        } else if (currentState == State.NOUGHT_WON) {
                            noughtWins++;
                        }

                        // Reset timer for next player
                        resetTimer();
                        currentPlayer = (currentPlayer == Seed.CROSS) ? Seed.NOUGHT : Seed.CROSS;
                    }

                    // Sound effects
                    if (currentState == State.PLAYING) {
                        SoundEffect.EAT_FOOD.play();
                    } else {
                        SoundEffect.DIE.play();
                        stopTimer();
                    }
                } else {
                    newGame();
                }
                repaint();
            }
        });

        // Initialize timer
        timer = new Timer(1000, e -> {
            timeLeft--;
            if (timeLeft <= 0) {
                timeUp();
            }
            repaint();
        });

        // Status bar setup
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

    private void initGame() {
        board = new Board();
    }

    public void newGame() {
        // Reset board
        for (int row = 0; row < Board.ROWS; ++row) {
            for (int col = 0; col < Board.COLS; ++col) {
                board.cells[row][col].content = Seed.NO_SEED;
            }
        }

        // Reset game state
        currentPlayer = Seed.CROSS;
        currentState = State.PLAYING;

        // Start timer
        resetTimer();
        startTimer();
    }

    private void startTimer() {
        timerRunning = true;
        timer.start();
    }

    private void stopTimer() {
        timerRunning = false;
        timer.stop();
    }

    private void resetTimer() {
        timeLeft = TURN_TIME;
        if (timerRunning) {
            timer.restart();
        }
    }

    private void timeUp() {
        if (currentState == State.PLAYING) {
            // Switch player when time runs out
            currentPlayer = (currentPlayer == Seed.CROSS) ? Seed.NOUGHT : Seed.CROSS;
            SoundEffect.DIE.play();
            resetTimer();
            repaint();
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        setBackground(COLOR_BG);

        board.paint(g);

        // Update status bar
        String statusText;
        if (currentState == State.PLAYING) {
            statusBar.setForeground(Color.BLACK);
            statusText = (currentPlayer == Seed.CROSS) ? "X's Turn" : "O's Turn";
            statusText += " | Time: " + timeLeft + "s";
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

        // Add scores
        statusText += " | Score: X " + crossWins + " - " + noughtWins + " O";
        statusBar.setText(statusText);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame(TITLE);
            frame.setContentPane(new GameMain());
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}