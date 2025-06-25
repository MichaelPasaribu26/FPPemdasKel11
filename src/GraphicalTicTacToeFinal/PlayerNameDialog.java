package GraphicalTicTacToeFinal;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Dialog untuk memasukkan nama pemain sebelum memulai game
 */
public class PlayerNameDialog extends JDialog {
    private String playerXName = "Player X";
    private String playerOName = "Player O";
    private boolean confirmed = false;

    private JTextField playerXField;
    private JTextField playerOField;

    public PlayerNameDialog(JFrame parent) {
        super(parent, "Enter Player Names", true);
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // Title panel
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(76, 175, 80));
        titlePanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        JLabel titleLabel = new JLabel("👥 Enter Player Names");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        titlePanel.add(titleLabel);

        // Input panel
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBackground(Color.WHITE);
        inputPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // Player X input
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        JLabel xLabel = new JLabel("❌ Player X Name:");
        xLabel.setFont(new Font("Arial", Font.BOLD, 16));
        xLabel.setForeground(new Color(239, 105, 80));
        inputPanel.add(xLabel, gbc);

        gbc.gridx = 1; gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        playerXField = new JTextField(playerXName, 15);
        playerXField.setFont(new Font("Arial", Font.PLAIN, 14));
        playerXField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 2),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        playerXField.setBackground(new Color(255, 248, 248));
        inputPanel.add(playerXField, gbc);

        // Player O input
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        JLabel oLabel = new JLabel("⭕ Player O Name:");
        oLabel.setFont(new Font("Arial", Font.BOLD, 16));
        oLabel.setForeground(new Color(64, 154, 225));
        inputPanel.add(oLabel, gbc);

        gbc.gridx = 1; gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        playerOField = new JTextField(playerOName, 15);
        playerOField.setFont(new Font("Arial", Font.PLAIN, 14));
        playerOField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 2),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        playerOField.setBackground(new Color(248, 248, 255));
        inputPanel.add(playerOField, gbc);

        // Info panel
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JLabel infoLabel = new JLabel("<html><center>💡 Tip: Leave empty for default names<br>Names will be displayed throughout the game</center></html>");
        infoLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        infoLabel.setForeground(new Color(102, 102, 102));
        infoLabel.setHorizontalAlignment(JLabel.CENTER);
        inputPanel.add(infoLabel, gbc);

        // Buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        buttonsPanel.setBackground(new Color(245, 245, 245));
        buttonsPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 20, 15));

        JButton startButton = createStyledButton("🚀 Start Game", new Color(76, 175, 80), Color.WHITE);
        startButton.addActionListener(e -> {
            String xName = playerXField.getText().trim();
            String oName = playerOField.getText().trim();

            playerXName = xName.isEmpty() ? "Player X" : xName;
            playerOName = oName.isEmpty() ? "Player O" : oName;

            // Ensure names are not too long
            if (playerXName.length() > 15) {
                playerXName = playerXName.substring(0, 15) + "...";
            }
            if (playerOName.length() > 15) {
                playerOName = playerOName.substring(0, 15) + "...";
            }

            confirmed = true;
            dispose();
        });

        JButton cancelButton = createStyledButton("❌ Cancel", new Color(244, 67, 54), Color.WHITE);
        cancelButton.addActionListener(e -> dispose());

        JButton randomButton = createStyledButton("🎲 Random", new Color(255, 152, 0), Color.WHITE);
        randomButton.addActionListener(e -> generateRandomNames());

        buttonsPanel.add(startButton);
        buttonsPanel.add(randomButton);
        buttonsPanel.add(cancelButton);

        // Assembly
        add(titlePanel, BorderLayout.NORTH);
        add(inputPanel, BorderLayout.CENTER);
        add(buttonsPanel, BorderLayout.SOUTH);

        // Dialog properties
        pack();
        setLocationRelativeTo(getParent());
        setResizable(false);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        // Focus on first field
        playerXField.requestFocus();
        playerXField.selectAll();

        // Enter key support
        getRootPane().setDefaultButton(startButton);
    }

    private void generateRandomNames() {
        String[] coolNames = {
                "Lightning", "Thunder", "Phoenix", "Dragon", "Tiger", "Eagle",
                "Storm", "Blaze", "Frost", "Shadow", "Nova", "Viper",
                "Hawk", "Wolf", "Raven", "Falcon", "Cobra", "Panther",
                "Titan", "Ninja", "Samurai", "Knight", "Warrior", "Champion"
        };

        String[] adjectives = {
                "Swift", "Mighty", "Brave", "Clever", "Strong", "Quick",
                "Bold", "Fierce", "Sharp", "Bright", "Cool", "Epic"
        };

        // Generate random names
        String xName = adjectives[(int)(Math.random() * adjectives.length)] + " " +
                coolNames[(int)(Math.random() * coolNames.length)];
        String oName = adjectives[(int)(Math.random() * adjectives.length)] + " " +
                coolNames[(int)(Math.random() * coolNames.length)];

        // Ensure names are different
        while (oName.equals(xName)) {
            oName = adjectives[(int)(Math.random() * adjectives.length)] + " " +
                    coolNames[(int)(Math.random() * coolNames.length)];
        }

        playerXField.setText(xName);
        playerOField.setText(oName);
    }

    private JButton createStyledButton(String text, Color bgColor, Color textColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(bgColor);
        button.setForeground(textColor);
        button.setPreferredSize(new Dimension(120, 40));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });

        return button;
    }

    public String getPlayerXName() {
        return playerXName;
    }

    public String getPlayerOName() {
        return playerOName;
    }

    public boolean isConfirmed() {
        return confirmed;
    }
}
