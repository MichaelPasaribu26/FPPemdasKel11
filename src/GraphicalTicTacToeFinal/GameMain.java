package GraphicalTicTacToeFinal;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class GameMain extends JPanel {
    private static final long serialVersionUID = 1L;

    // Constants
    public static final String TITLE = "Tic Tac Toe - Auto Layout";
    public static final Color COLOR_BG = Color.WHITE;
    public static final Color COLOR_BG_STATUS = new Color(216, 216, 216);
    public static final Color COLOR_CROSS = new Color(239, 105, 80);
    public static final Color COLOR_NOUGHT = new Color(64, 154, 225);
    public static final Color COLOR_PAUSE_OVERLAY = new Color(0, 0, 0, 150);
    public static final Font FONT_STATUS = new Font("OCR A Extended", Font.PLAIN, 14);
    public static final Font FONT_PAUSE = new Font("Arial", Font.BOLD, 48);
    private static final int TURN_TIME = 10;

    // Game objects
    private Board board;
    private State currentState;
    private Seed currentPlayer;
    private JLabel statusBar;

    // Pause/Resume functionality
    private boolean isPaused = false;
    private JButton pauseResumeButton;

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

    // Auto-layout components
    private JFrame parentFrame;

    public GameMain() {
        // Mouse listener - updated for dynamic board size
        super.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (isPaused) return;

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

        // Keyboard listener for pause/resume
        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    togglePause();
                }
            }
        });

        // Initialize timer
        timer = new Timer(1000, e -> {
            if (!isPaused) {
                timeLeft--;
                if (timeLeft <= 0) {
                    timeUp();
                }
                repaint();
            }
        });

        // Status bar setup
        statusBar = new JLabel();
        statusBar.setFont(FONT_STATUS);
        statusBar.setBackground(COLOR_BG_STATUS);
        statusBar.setOpaque(true);
        statusBar.setPreferredSize(new Dimension(300, 30));
        statusBar.setHorizontalAlignment(JLabel.LEFT);
        statusBar.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 12));

        // Create pause/resume button
        pauseResumeButton = new JButton("⏸️ Pause");
        pauseResumeButton.setFont(new Font("Arial", Font.BOLD, 12));
        pauseResumeButton.setPreferredSize(new Dimension(80, 25));
        pauseResumeButton.setFocusPainted(false);
        pauseResumeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        pauseResumeButton.addActionListener(e -> togglePause());

        // Create bottom panel
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(statusBar, BorderLayout.CENTER);
        bottomPanel.add(pauseResumeButton, BorderLayout.EAST);

        super.setLayout(new BorderLayout());
        super.add(bottomPanel, BorderLayout.PAGE_END);
        super.setBorder(BorderFactory.createLineBorder(COLOR_BG_STATUS, 2, false));

        initGame();
        newGame();
        createMenuBar();
        updatePanelSize();
    }

    /** Update panel size based on current board dimensions */
    private void updatePanelSize() {
        Dimension optimalSize = board.getOptimalWindowSize();
        super.setPreferredSize(optimalSize);

        // Update status bar width to match board width
        statusBar.setPreferredSize(new Dimension(Board.CANVAS_WIDTH, 30));

        if (parentFrame != null) {
            parentFrame.pack();
            parentFrame.setLocationRelativeTo(null);
        }
    }

    /** Set parent frame reference for auto-layout updates */
    public void setParentFrame(JFrame frame) {
        this.parentFrame = frame;
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

        currentPlayer = Seed.CROSS;
        currentState = State.PLAYING;

        if (isPaused) {
            togglePause();
        }

        resetTimer();
        startTimer();
    }

    private void togglePause() {
        if (currentState != State.PLAYING) return;

        isPaused = !isPaused;

        if (isPaused) {
            pauseResumeButton.setText("▶️ Resume");
            pauseResumeButton.setBackground(new Color(76, 175, 80));
            stopTimer();
            SoundEffect.EAT_FOOD.play();
        } else {
            pauseResumeButton.setText("⏸️ Pause");
            pauseResumeButton.setBackground(null);
            startTimer();
            SoundEffect.EAT_FOOD.play();
        }

        repaint();
        requestFocus();
    }

    private void startTimer() {
        timerRunning = true;
        if (!isPaused) {
            timer.start();
        }
    }

    private void stopTimer() {
        timerRunning = false;
        timer.stop();
    }

    private void resetTimer() {
        timeLeft = TURN_TIME;
        if (timerRunning && !isPaused) {
            timer.restart();
        }
    }

    private void timeUp() {
        if (currentState == State.PLAYING && !isPaused) {
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

        JMenuItem pauseResumeItem = new JMenuItem("⏸️ Pause/Resume (Space)");
        pauseResumeItem.setFont(FONT_STATUS);
        pauseResumeItem.addActionListener(e -> togglePause());

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
        gameMenu.add(pauseResumeItem);
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
        BoardSizeSelector selector = new BoardSizeSelector(parentFrame);
        selector.setVisible(true);

        if (selector.isConfirmed()) {
            int newSize = selector.getSelectedSize();
            board.setBoardSize(newSize, newSize);
            updatePanelSize();
            newGame();
            repaint();
        }
    }

    private void showAbout() {
        String message = "🎮 Tic Tac Toe - Auto Layout\n\n" +
                "Version: 3.0 with Auto-Layout System\n" +
                "Features:\n" +
                "• Automatic screen size adaptation\n" +
                "• Multiple board sizes (3x3, 4x4, 5x5)\n" +
                "• Turn timer (10 seconds per turn)\n" +
                "• Pause/Resume functionality\n" +
                "• Score tracking\n" +
                "• Sound effects\n" +
                "• Responsive cell sizing\n\n" +
                "Controls:\n" +
                "• Space bar: Pause/Resume\n" +
                "• Click cells to make moves\n\n" +
                "Auto-Layout Info:\n" +
                "• Cell size: " + Cell.SIZE + "px\n" +
                "• Board size: " + Board.CANVAS_WIDTH + "x" + Board.CANVAS_HEIGHT + "px\n" +
                "• Optimized for your screen resolution";

        JOptionPane.showMessageDialog(this, message, "About", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showRules() {
        String rules = "📖 Game Rules:\n\n" +
                "🟩 3x3 Board: Get 3 in a row to win\n" +
                "🟨 4x4 Board: Get 4 in a row to win\n" +
                "🟦 5x5 Board: Get 4 in a row to win\n\n" +
                "⏱️ Each player has 10 seconds per turn\n" +
                "🔄 Time runs out = turn switches automatically\n" +
                "⏸️ Press Space or click Pause button to pause\n" +
                "🏆 Score is tracked across multiple games\n\n" +
                "Auto-Layout Features:\n" +
                "• Game automatically adapts to your screen size\n" +
                "• Optimal cell size calculated for best experience\n" +
                "• Maintains perfect proportions on any display\n" +
                "• Minimum cell size: 80px, Maximum: 150px\n\n" +
                "Controls:\n" +
                "• Click on any empty cell to make your move\n" +
                "• Press Space bar to pause/resume\n" +
                "• Timer stops when paused";

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

        // Draw pause overlay if paused
        if (isPaused) {
            g.setColor(COLOR_PAUSE_OVERLAY);
            g.fillRect(0, 0, getWidth(), getHeight() - 30);

            // Draw pause text
            g.setColor(Color.WHITE);
            g.setFont(FONT_PAUSE);
            FontMetrics fm = g.getFontMetrics();
            String pauseText = "⏸️ PAUSED";
            int textWidth = fm.stringWidth(pauseText);
            int textHeight = fm.getHeight();
            int x = (getWidth() - textWidth) / 2;
            int y = (getHeight() - 30 - textHeight) / 2 + fm.getAscent();
            g.drawString(pauseText, x, y);

            // Draw instruction text
            g.setFont(new Font("Arial", Font.BOLD, 16));
            fm = g.getFontMetrics();
            String instructionText = "Press Space or click Resume to continue";
            textWidth = fm.stringWidth(instructionText);
            x = (getWidth() - textWidth) / 2;
            y += 40;
            g.drawString(instructionText, x, y);
        }

        // Update status bar
        String statusText;
        if (isPaused) {
            statusBar.setForeground(new Color(255, 152, 0));
            statusText = "GAME PAUSED - Press Space to Resume";
        } else if (currentState == State.PLAYING) {
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

        if (!isPaused) {
            statusText += " | Score: X " + crossWins + " - " + noughtWins + " O";
            statusText += " | Board: " + Board.ROWS + "x" + Board.COLS +
                    " (need " + Board.WIN_CONDITION + " in a row)";
            statusText += " | Cell Size: " + Cell.SIZE + "px (Auto-Layout)";
        }

        statusBar.setText(statusText);
    }

    public static void main(String[] args) {
        // Set look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
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

                // Set parent frame reference for auto-layout
                gameMain.setParentFrame(frame);

                // Set board size before displaying
                gameMain.board.setBoardSize(size, size);
                gameMain.updatePanelSize();

                // Set menu bar
                frame.setJMenuBar(gameMain.getGameMenuBar());

                frame.setContentPane(gameMain);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setResizable(true); // Allow resizing for better auto-layout
                frame.setVisible(true);

                gameMain.newGame();

                // Print auto-layout info
                System.out.println("Auto-Layout initialized:");
                System.out.println("- Screen resolution detected and optimal cell size calculated");
                System.out.println("- Cell size: " + Cell.SIZE + "px");
                System.out.println("- Board dimensions: " + Board.CANVAS_WIDTH + "x" + Board.CANVAS_HEIGHT + "px");
                System.out.println("- Window can be resized for different viewing preferences");
            } else {
                System.exit(0);
            }
        });
    }
}
