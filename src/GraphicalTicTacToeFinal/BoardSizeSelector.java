package GraphicalTicTacToeFinal;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Enhanced dialog for selecting board size with auto-layout information
 * Shows optimal sizing information for each board size option
 */
public class BoardSizeSelector extends JDialog {
    private int selectedSize = 3; // Default 3x3
    private boolean confirmed = false;

    public BoardSizeSelector(JFrame parent) {
        super(parent, "Select Board Size - Auto Layout", true);
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // Title panel with gradient background
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(76, 175, 80));
        titlePanel.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));

        JLabel titleLabel = new JLabel("🎮 Choose Your Board Size (Auto-Layout)");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        titlePanel.add(titleLabel);

        // Auto-layout info panel
        JPanel infoPanel = new JPanel();
        infoPanel.setBackground(new Color(240, 248, 255));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        JLabel infoLabel = new JLabel("<html><center>" +
                "🖥️ Screen Resolution: " + (int)screenSize.getWidth() + "x" + (int)screenSize.getHeight() + "<br>" +
                "📐 Game will automatically calculate optimal cell size<br>" +
                "🎯 Perfect sizing for your display</center></html>");
        infoLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        infoLabel.setForeground(new Color(51, 102, 153));
        infoLabel.setHorizontalAlignment(JLabel.CENTER);
        infoPanel.add(infoLabel);

        // Options panel with styling yang menarik
        JPanel optionsPanel = new JPanel(new GridLayout(3, 1, 15, 15));
        optionsPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        optionsPanel.setBackground(Color.WHITE);

        ButtonGroup group = new ButtonGroup();

        // Calculate estimated cell sizes for preview
        int availableSpace = Math.min((int)screenSize.getWidth(), (int)screenSize.getHeight()) - 200;
        int cellSize3x3 = Math.max(80, Math.min(150, availableSpace / 3));
        int cellSize4x4 = Math.max(80, Math.min(150, availableSpace / 4));
        int cellSize5x5 = Math.max(80, Math.min(150, availableSpace / 5));

        // 3x3 option
        JRadioButton option3x3 = createStyledRadioButton(
                "🟩 3x3 (Classic)",
                "Perfect for quick games - 3 in a row to win<br>Estimated cell size: " + cellSize3x3 + "px",
                true
        );
        option3x3.addActionListener(e -> selectedSize = 3);
        group.add(option3x3);
        optionsPanel.add(option3x3);

        // 4x4 option
        JRadioButton option4x4 = createStyledRadioButton(
                "🟨 4x4 (Medium)",
                "More strategic gameplay - 4 in a row to win<br>Estimated cell size: " + cellSize4x4 + "px",
                false
        );
        option4x4.addActionListener(e -> selectedSize = 4);
        group.add(option4x4);
        optionsPanel.add(option4x4);

        // 5x5 option
        JRadioButton option5x5 = createStyledRadioButton(
                "🟦 5x5 (Large)",
                "Advanced gameplay - 4 in a row to win<br>Estimated cell size: " + cellSize5x5 + "px",
                false
        );
        option5x5.addActionListener(e -> selectedSize = 5);
        group.add(option5x5);
        optionsPanel.add(option5x5);

        // Buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        buttonsPanel.setBackground(new Color(245, 245, 245));
        buttonsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 15, 10));

        JButton startButton = createStyledButton("🚀 Start Game", new Color(76, 175, 80), Color.WHITE);
        startButton.addActionListener(e -> {
            confirmed = true;
            dispose();
        });

        JButton cancelButton = createStyledButton("❌ Cancel", new Color(244, 67, 54), Color.WHITE);
        cancelButton.addActionListener(e -> dispose());

        buttonsPanel.add(startButton);
        buttonsPanel.add(cancelButton);

        // Assembly
        add(titlePanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(infoPanel, BorderLayout.NORTH);
        centerPanel.add(optionsPanel, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        add(buttonsPanel, BorderLayout.SOUTH);

        // Dialog properties
        pack();
        setLocationRelativeTo(getParent());
        setResizable(false);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        // Make it look modern
        getRootPane().setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
    }

    private JRadioButton createStyledRadioButton(String title, String description, boolean selected) {
        JRadioButton radioButton = new JRadioButton();
        radioButton.setSelected(selected);
        radioButton.setOpaque(false);
        radioButton.setLayout(new BorderLayout());

        // Create custom panel for the radio button content
        JPanel contentPanel = new JPanel(new BorderLayout(10, 5));
        contentPanel.setOpaque(false);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 35, 10, 10));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(new Color(51, 51, 51));

        JLabel descLabel = new JLabel("<html>" + description + "</html>");
        descLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        descLabel.setForeground(new Color(102, 102, 102));

        contentPanel.add(titleLabel, BorderLayout.NORTH);
        contentPanel.add(descLabel, BorderLayout.CENTER);

        radioButton.add(contentPanel, BorderLayout.CENTER);

        // Add hover effect
        radioButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                radioButton.setBackground(new Color(240, 248, 255));
                radioButton.setOpaque(true);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (!radioButton.isSelected()) {
                    radioButton.setOpaque(false);
                }
            }
        });

        return radioButton;
    }

    private JButton createStyledButton(String text, Color bgColor, Color textColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(bgColor);
        button.setForeground(textColor);
        button.setPreferredSize(new Dimension(130, 40));
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

    public int getSelectedSize() {
        return selectedSize;
    }

    public boolean isConfirmed() {
        return confirmed;
    }
}
