package GraphicalTicTacToeFinal;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class GameMain extends JPanel {
    private static final long serialVersionUID = 1L;

    // Constants
    public static final String TITLE = "Tic Tac Toe - Enhanced Auto Layout";
    public static final Color COLOR_BG = Color.WHITE;
    public static final Color COLOR_BG_STATUS = new Color(216, 216, 216);
    public static final Color COLOR_CROSS = new Color(239, 105, 80);
    public static final Color COLOR_NOUGHT = new Color(64, 154, 225);
    public static final Color COLOR_PAUSE_OVERLAY = new Color(0, 0, 0, 150);
    public static final Font FONT_STATUS = new Font("Arial", Font.PLAIN, 12);
    public static final Font FONT_PAUSE = new Font("Arial", Font.BOLD, 48);
    private static final int TURN_TIME = 10;

    // Game objects
    private Board board;
    private State currentState;
    private Seed currentPlayer;
    private JLabel statusBar;

    // Player names
    private String playerXName = "Player X";
    private String playerOName = "Player O";

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

        // Enhanced keyboard listener
        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_SPACE:
                        togglePause();
                        break;
                    case KeyEvent.VK_N:
                        if (e.isControlDown()) {
                            newGame();
                        }
                        break;
                    case KeyEvent.VK_R:
                        if (e.isControlDown()) {
                            crossWins = 0;
                            noughtWins = 0;
                            repaint();
                        }
                        break;
                    case KeyEvent.VK_P:
                        if (e.isControlDown()) {
                            changePlayerNames();
                        }
                        break;
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

        // Enhanced status bar setup
        statusBar = new JLabel();
        statusBar.setFont(FONT_STATUS);
        statusBar.setBackground(COLOR_BG_STATUS);
        statusBar.setOpaque(true);
        statusBar.setPreferredSize(new Dimension(300, 30));
        statusBar.setHorizontalAlignment(JLabel.LEFT);
        statusBar.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 12));

        // Create enhanced pause/resume button
        pauseResumeButton = new JButton("⏸️ Pause");
        pauseResumeButton.setFont(new Font("Arial", Font.BOLD, 11));
        pauseResumeButton.setPreferredSize(new Dimension(85, 25));
        pauseResumeButton.setFocusPainted(false);
        pauseResumeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        pauseResumeButton.setToolTipText("Pause/Resume game (Space)");
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

    /** Set player names */
    public void setPlayerNames(String playerXName, String playerOName) {
        this.playerXName = playerXName;
        this.playerOName = playerOName;
        repaint(); // Update display
    }

    /** Get current player name */
    public String getCurrentPlayerName() {
        return (currentPlayer == Seed.CROSS) ? playerXName : playerOName;
    }

    /** Get winner name */
    public String getWinnerName() {
        if (currentState == State.CROSS_WON) {
            return playerXName;
        } else if (currentState == State.NOUGHT_WON) {
            return playerOName;
        }
        return "";
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

        JMenuItem newGameItem = new JMenuItem("🎮 New Game (Ctrl+N)");
        newGameItem.setFont(FONT_STATUS);
        newGameItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK));
        newGameItem.addActionListener(e -> newGame());

        JMenuItem pauseResumeItem = new JMenuItem("⏸️ Pause/Resume (Space)");
        pauseResumeItem.setFont(FONT_STATUS);
        pauseResumeItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0));
        pauseResumeItem.addActionListener(e -> togglePause());

        JMenuItem changeSizeItem = new JMenuItem("📐 Change Board Size");
        changeSizeItem.setFont(FONT_STATUS);
        changeSizeItem.addActionListener(e -> changeBoardSize());

        JMenuItem changeNamesItem = new JMenuItem("👥 Change Player Names (Ctrl+P)");
        changeNamesItem.setFont(FONT_STATUS);
        changeNamesItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, InputEvent.CTRL_DOWN_MASK));
        changeNamesItem.addActionListener(e -> changePlayerNames());

        JMenuItem resetScoreItem = new JMenuItem("🔄 Reset Score (Ctrl+R)");
        resetScoreItem.setFont(FONT_STATUS);
        resetScoreItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_DOWN_MASK));
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
        gameMenu.add(changeNamesItem);
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

    private void changePlayerNames() {
        PlayerNameDialog nameDialog = new PlayerNameDialog(parentFrame);
        nameDialog.setVisible(true);

        if (nameDialog.isConfirmed()) {
            setPlayerNames(nameDialog.getPlayerXName(), nameDialog.getPlayerOName());

            // Show confirmation
            JOptionPane.showMessageDialog(this,
                    "Player names updated!\n" +
                            "❌ " + playerXName + " vs ⭕ " + playerOName,
                    "Names Updated",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void showAbout() {
        double utilization = board.getScreenUtilizationRatio();
        String message = "🎮 Tic Tac Toe - Enhanced Auto Layout\n\n" +
                "Version: 3.2 with Player Names Feature\n" +
                "Features:\n" +
                "• Intelligent screen size adaptation\n" +
                "• Multiple board sizes (3x3, 4x4, 5x5)\n" +
                "• Custom player names with random generator\n" +
                "• Turn timer (10 seconds per turn)\n" +
                "• Pause/Resume functionality\n" +
                "• Score tracking with player names\n" +
                "• Sound effects with error handling\n" +
                "• Anti-aliased graphics rendering\n" +
                "• Responsive cell sizing\n\n" +
                "Current Players:\n" +
                "❌ " + playerXName + " (X)\n" +
                "⭕ " + playerOName + " (O)\n\n" +
                "Controls:\n" +
                "• Space bar: Pause/Resume\n" +
                "• Ctrl+N: New Game\n" +
                "• Ctrl+P: Change Player Names\n" +
                "• Ctrl+R: Reset Score\n" +
                "• Click cells to make moves\n\n" +
                "Current Auto-Layout Info:\n" +
                "• Cell size: " + Cell.SIZE + "px\n" +
                "• Board size: " + Board.CANVAS_WIDTH + "x" + Board.CANVAS_HEIGHT + "px\n" +
                "• Screen utilization: " + String.format("%.1f", utilization) + "%\n" +
                "• Optimized for your " + Board.ROWS + "x" + Board.COLS + " board";

        JOptionPane.showMessageDialog(this, message, "About", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showRules() {
        String rules = "📖 Enhanced Game Rules:\n\n" +
                "🟩 3x3 Board: Get 3 in a row to win\n" +
                "🟨 4x4 Board: Get 4 in a row to win\n" +
                "🟦 5x5 Board: Get 4 in a row to win\n\n" +
                "👥 Player Names Feature:\n" +
                "• Set custom names for both players\n" +
                "• Names appear in status bar and win messages\n" +
                "• Random name generator available\n" +
                "• Names are saved during game session\n\n" +
                "⏱️ Each player has 10 seconds per turn\n" +
                "🔄 Time runs out = turn switches automatically\n" +
                "⏸️ Press Space or click Pause button to pause\n" +
                "🏆 Score is tracked with player names\n\n" +
                "Enhanced Auto-Layout Features:\n" +
                "• Intelligent screen size detection\n" +
                "• Usable area calculation (excludes taskbar)\n" +
                "• Optimal cell size for best experience\n" +
                "• Perfect proportions on any display\n" +
                "• Adaptive sizing: 60px - 180px range\n" +
                "• Special optimization for 3x3 boards\n" +
                "• Real-time screen utilization feedback\n\n" +
                "Keyboard Shortcuts:\n" +
                "• Space: Pause/Resume game\n" +
                "• Ctrl+N: Start new game\n" +
                "• Ctrl+P: Change player names\n" +
                "• Ctrl+R: Reset score counter\n" +
                "• Click: Make moves on board\n\n" +
                "Technical Features:\n" +
                "• Anti-aliased graphics rendering\n" +
                "• Fallback text display if images fail\n" +
                "• Enhanced error handling\n" +
                "• Responsive UI components\n" +
                "• Personalized gaming experience";

        JOptionPane.showMessageDialog(this, rules, "Enhanced Game Rules", JOptionPane.INFORMATION_MESSAGE);
    }

    public JMenuBar getGameMenuBar() {
        return menuBar;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        setBackground(COLOR_BG);

        // Enable anti-aliasing
        if (g instanceof Graphics2D) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        }

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

        // Update status bar with enhanced information
        updateStatusBar();
    }

    private void updateStatusBar() {
        String statusText;
        if (isPaused) {
            statusBar.setForeground(new Color(255, 152, 0));
            statusText = "GAME PAUSED - Press Space to Resume";
        } else if (currentState == State.PLAYING) {
            statusBar.setForeground(Color.BLACK);
            String currentPlayerName = getCurrentPlayerName();
            String symbol = (currentPlayer == Seed.CROSS) ? "❌" : "⭕";
            statusText = symbol + " " + currentPlayerName + "'s Turn";
            statusText += " | Time: " + timeLeft + "s";
        } else if (currentState == State.DRAW) {
            statusBar.setForeground(Color.RED);
            statusText = "🤝 It's a Draw! " + playerXName + " vs " + playerOName + " - Click to play again";
        } else if (currentState == State.CROSS_WON) {
            statusBar.setForeground(COLOR_CROSS);
            statusText = "🏆 " + playerXName + " (❌) Won! Click to play again";
        } else {
            statusBar.setForeground(COLOR_NOUGHT);
            statusText = "🏆 " + playerOName + " (⭕) Won! Click to play again";
        }

        if (!isPaused && currentState == State.PLAYING) {
            statusText += " | Score: " + playerXName + " " + crossWins + " - " + noughtWins + " " + playerOName;
            statusText += " | " + Board.ROWS + "x" + Board.COLS + " (" + Board.WIN_CONDITION + " to win)";
        } else if (!isPaused && (currentState == State.CROSS_WON || currentState == State.NOUGHT_WON || currentState == State.DRAW)) {
            statusText += " | Score: " + playerXName + " " + crossWins + " - " + noughtWins + " " + playerOName;
        }

        statusBar.setText(statusText);
    }

    public static void main(String[] args) {
        // Set look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Could not set system look and feel: " + e.getMessage());
        }

        SwingUtilities.invokeLater(() -> {
            // Show player name dialog first
            PlayerNameDialog nameDialog = new PlayerNameDialog(null);
            nameDialog.setVisible(true);

            if (nameDialog.isConfirmed()) {
                String playerXName = nameDialog.getPlayerXName();
                String playerOName = nameDialog.getPlayerOName();

                // Show board size selector
                BoardSizeSelector selector = new BoardSizeSelector(null);
                selector.setVisible(true);

                if (selector.isConfirmed()) {
                    int size = selector.getSelectedSize();

                    JFrame frame = new JFrame(TITLE + " - " + playerXName + " vs " + playerOName);
                    GameMain gameMain = new GameMain();

                    // Set player names
                    gameMain.setPlayerNames(playerXName, playerOName);

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
                    frame.setResizable(true);
                    frame.setVisible(true);

                    // Set minimum size to prevent too small windows
                    frame.setMinimumSize(new Dimension(300, 350));

                    gameMain.newGame();

                    // Print enhanced auto-layout info
                    System.out.println("\n=== Enhanced Auto-Layout System with Player Names ===");
                    System.out.println("👥 Players: " + playerXName + " (❌) vs " + playerOName + " (⭕)");
                    System.out.println("✓ Screen resolution detected and analyzed");
                    System.out.println("✓ Usable screen area calculated");
                    System.out.println("✓ Optimal cell size determined: " + Cell.SIZE + "px");
                    System.out.println("✓ Board dimensions: " + Board.CANVAS_WIDTH + "x" + Board.CANVAS_HEIGHT + "px");
                    System.out.println("✓ Screen utilization: " + String.format("%.1f", gameMain.board.getScreenUtilizationRatio()) + "%");
                    System.out.println("✓ Window is resizable for user preference");
                    System.out.println("✓ Anti-aliasing enabled for smooth graphics");
                    System.out.println("✓ Enhanced keyboard shortcuts available");
                    System.out.println("✓ Player names feature activated");
                    System.out.println("=== Ready to Play! ===\n");
                } else {
                    System.exit(0);
                }
            } else {
                System.exit(0);
            }
        });
    }
}
