package GraphicalTicTacToeFinal;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class GameMain extends JPanel {
    private static final long serialVersionUID = 1L;

    // Constants
    public static final String TITLE = "Tic Tac Toe - Dynamic Board";
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

    // Menu components
    private JMenuBar menuBar;
    private JMenu gameMenu;

    public GameMain() {
        // Mouse listener - updated untuk dynamic board size
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
        createMenuBar();
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

    private void createMenuBar() {
        menuBar = new JMenuBar();
        gameMenu = new JMenu("Game");
        gameMenu.setFont(FONT_STATUS);

        JMenuItem newGameItem = new JMenuItem("🎮 New Game");
        newGameItem.setFont(FONT_STATUS);
        newGameItem.addActionListener(e -> newGame());

        JMenuItem changeSizeItem = new JMenuItem("📐 Change Board Size");
        changeSizeItem.setFont(FONT_STATUS);
        changeSizeItem.addActionListener(e -> changeBoardSize());

        JMenuItem resetScoreItem = new JMenuItem("🔄 Reset Score");
        resetScoreItem.setFont(FONT_STATUS);
        resetScoreItem.addActionListener(e -> {
            crossWins = 0;
            noughtWins = 0;
            repaint();
        });

        JMenuItem exitItem = new JMenuItem("❌ Exit");
        exitItem.setFont(FONT_STATUS);
        exitItem.addActionListener(e -> System.exit(0));

        gameMenu.add(newGameItem);
        gameMenu.addSeparator();
        gameMenu.add(changeSizeItem);
        gameMenu.add(resetScoreItem);
        gameMenu.addSeparator();
        gameMenu.add(exitItem);

        menuBar.add(gameMenu);

        // Add Info menu
        JMenu infoMenu = new JMenu("Info");
        infoMenu.setFont(FONT_STATUS);

        JMenuItem aboutItem = new JMenuItem("ℹ️ About");
        aboutItem.setFont(FONT_STATUS);
        aboutItem.addActionListener(e -> showAbout());

        JMenuItem rulesItem = new JMenuItem("📖 Game Rules");
        rulesItem.setFont(FONT_STATUS);
        rulesItem.addActionListener(e -> showRules());

        infoMenu.add(aboutItem);
        infoMenu.add(rulesItem);

        menuBar.add(infoMenu);
    }

    private void changeBoardSize() {
        BoardSizeSelector selector = new BoardSizeSelector((JFrame) SwingUtilities.getWindowAncestor(this));
        selector.setVisible(true);

        if (selector.isConfirmed()) {
            int newSize = selector.getSelectedSize();
            board.setBoardSize(newSize, newSize);

            // Update panel size
            setPreferredSize(new Dimension(Board.CANVAS_WIDTH, Board.CANVAS_HEIGHT + 30));

            // Update parent frame
            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
            if (frame != null) {
                frame.pack();
                frame.setLocationRelativeTo(null);
            }

            newGame();
            repaint();
        }
    }

    private void showAbout() {
        String message = "🎮 Tic Tac Toe - Dynamic Board\n\n" +
                "Version: 2.0 with Board Size Options\n" +
                "Features:\n" +
                "• Multiple board sizes (3x3, 4x4, 5x5)\n" +
                "• Turn timer (10 seconds per turn)\n" +
                "• Score tracking\n" +
                "• Sound effects\n\n" +
                "Enhanced by: Claude AI Assistant";

        JOptionPane.showMessageDialog(this, message, "About", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showRules() {
        String rules = "📖 Game Rules:\n\n" +
                "🟩 3x3 Board: Get 3 in a row to win\n" +
                "🟨 4x4 Board: Get 4 in a row to win\n" +
                "🟦 5x5 Board: Get 4 in a row to win\n\n" +
                "⏱️ Each player has 10 seconds per turn\n" +
                "🔄 Time runs out = turn switches automatically\n" +
                "🏆 Score is tracked across multiple games\n\n" +
                "Click on any empty cell to make your move!";

        JOptionPane.showMessageDialog(this, rules, "Game Rules", JOptionPane.INFORMATION_MESSAGE);
    }

    public JMenuBar getGameMenuBar() {
        return menuBar;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        setBackground(COLOR_BG);

        board.paint(g);

        // Update status bar with board size info
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

        // Add scores and board info
        statusText += " | Score: X " + crossWins + " - " + noughtWins + " O";
        statusText += " | Board: " + Board.ROWS + "x" + Board.COLS +
                " (need " + Board.WIN_CONDITION + " in a row)";
        statusBar.setText(statusText);
    }

    public static void main(String[] args) {
        // Set look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getLookAndFeel());
        } catch (Exception e) {
            // Use default look and feel
        }

        SwingUtilities.invokeLater(() -> {
            // Show board size selector first
            BoardSizeSelector selector = new BoardSizeSelector(null);
            selector.setVisible(true);

            if (selector.isConfirmed()) {
                int size = selector.getSelectedSize();

                JFrame frame = new JFrame(TITLE);
                GameMain gameMain = new GameMain();

                // Set board size before displaying
                gameMain.board.setBoardSize(size, size);
                gameMain.setPreferredSize(new Dimension(Board.CANVAS_WIDTH, Board.CANVAS_HEIGHT + 30));

                // Set menu bar
                frame.setJMenuBar(gameMain.getGameMenuBar());

                frame.setContentPane(gameMain);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setResizable(false);
                frame.setVisible(true);

                gameMain.newGame();
            } else {
                System.exit(0);
            }
        });
    }
}