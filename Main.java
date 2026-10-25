import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * main — application entry point and main-menu screen.
 *
 * FIXES vs original (main.java):
 *   - Class name matches the main.java file
 *   - Assets loaded via AppConstants.loadImage() → JAR-safe
 *   - Instruction overlay component listener is always removed on close
 *   - setDefaultCloseOperation changed to EXIT_ON_CLOSE only on this root frame
 *   - Sound-toggle button no longer uses reflection hack in UIFactory
 *   - Removed duplicate new BorderLayout() call on root
 */
public class main extends JFrame {

    public main() {
        setTitle("JUMWORD");
        setSize(AppConstants.WIN_W, AppConstants.WIN_H);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);

        // Window icon
        Image icon = AppConstants.loadImage("assets/icon.png");
        if (icon != null) setIconImage(icon);

        // ── Root: tiled background image ───────────────────────────────────
        JPanel root = new JPanel(new BorderLayout()) {
            final Image bgImg = AppConstants.loadImage("assets/main_bg.png");
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (bgImg != null) g.drawImage(bgImg, 0, 0, getWidth(), getHeight(), this);
            }
        };

        // ── Start background music ─────────────────────────────────────────
        SoundManager.playBGMusic();

        // ── Top bar: sound toggle + instructions ───────────────────────────
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 16));
        topPanel.setOpaque(false);
        topPanel.add(makeSoundToggleBtn());
        topPanel.add(makeInstructionBtn());
        root.add(topPanel, BorderLayout.NORTH);

        // ── Button area (centered, above bottom) ───────────────────────────
        JPanel buttonArea = new JPanel();
        buttonArea.setOpaque(false);
        buttonArea.setLayout(new BoxLayout(buttonArea, BoxLayout.Y_AXIS));
        buttonArea.setBorder(BorderFactory.createEmptyBorder(0, 0, 100, 0));

        JButton startBtn = UIFactory.menuButton("START GAME");
        JButton lbBtn    = UIFactory.menuButton("LEADERBOARD");

        startBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        lbBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        startBtn.addActionListener(e -> {
            SoundManager.playGameStart();
            new PlayerScreen();
            dispose();
        });
        lbBtn.addActionListener(e -> {
            SoundManager.playButton();
            new LeaderboardScreen();
            dispose();
        });

        buttonArea.add(Box.createVerticalGlue());
        buttonArea.add(startBtn);
        buttonArea.add(Box.createVerticalStrut(14));
        buttonArea.add(lbBtn);

        root.add(buttonArea, BorderLayout.CENTER);
        setContentPane(root);
        setVisible(true);
    }

    // ── How-to-Play instruction icon button ────────────────────────────────
    private JButton makeInstructionBtn() {
        Image img = AppConstants.loadImage("assets/instruction-icon.png", 50, 50);
        JButton btn;
        if (img != null) {
            btn = new JButton(new ImageIcon(img));
        } else {
            btn = new JButton("?");
        }
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> showInstructionOverlay());
        return btn;
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  Instruction overlay
    // ═══════════════════════════════════════════════════════════════════════
    private void showInstructionOverlay() {
        // ── Card ──────────────────────────────────────────────────────────
        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = UIFactory.aa(g);
                g2.setColor(new Color(0, 0, 0, 80));
                g2.fillRoundRect(6, 6, getWidth(), getHeight(), 24, 24);
                g2.setPaint(new GradientPaint(0, 0, new Color(8, 18, 70), 0, getHeight(), new Color(14, 35, 100)));
                g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 24, 24);
                g2.setColor(AppConstants.GOLD);
                g2.setStroke(new BasicStroke(2.5f));
                g2.drawRoundRect(1, 1, getWidth()-3, getHeight()-3, 24, 24);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(28, 36, 24, 36));
        card.setPreferredSize(new Dimension(680, 520));
        card.setMaximumSize(new Dimension(680, 520));

        // Title row
        JPanel titleRow = new JPanel(new BorderLayout(10, 0));
        titleRow.setOpaque(false);
        titleRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        titleRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel titleLbl = new JLabel("📖  HOW TO PLAY  —  JUMWORD");
        titleLbl.setFont(new Font("SansSerif", Font.BOLD, 19));
        titleLbl.setForeground(AppConstants.GOLD);
        titleRow.add(titleLbl, BorderLayout.WEST);

        JButton closeBtn = UIFactory.closeButton();
        titleRow.add(closeBtn, BorderLayout.EAST);
        card.add(titleRow);
        card.add(Box.createVerticalStrut(6));

        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(255, 210, 0, 80));
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(sep);
        card.add(Box.createVerticalStrut(10));

        // Steps
        Image rawIcon = AppConstants.loadImage("assets/instruction-icon.png", 28, 28);
        ImageIcon stepIcon = rawIcon != null ? new ImageIcon(rawIcon) : null;

        String[][] steps = {
            { "CHOOSE YOUR REGION",       "Select a difficulty to begin your journey. Easy features Luzon cities, Medium covers the Visayas, and Hard tests you on Mindanao." },
            { "UNSCRAMBLE & TYPE",         "Look at the jumbled letter tiles on the screen. Figure out the hidden city or province, type your answer into the box, and hit GO!" },
            { "WATCH YOUR HP & TIME",      "You have an 8-minute timer and 3 lives (hearts) to survive. Correct answers grant you coins and points, but a wrong guess costs a life." },
            { "SHOP FOR POWER-UPS",        "Click the shopping cart to spend your coins! Buy extra time, restore your health, activate a shield, get a hint, or double your next coin drop." },
            { "UNLOCK NEW CHALLENGES",     "Prove your skills to unlock harder regions! Score at least 80% on Easy to unlock Medium, and 60% on Medium to unlock the Hard mode." },
            { "CLIMB THE LEADERBOARD",     "Your best runs are saved automatically. Aim for the highest score, secure your spot in the Top 20, and show off your geographic mastery!" }
        };
        for (String[] step : steps) {
            card.add(makeStepRow(stepIcon, step[0], step[1]));
            card.add(Box.createVerticalStrut(9));
        }

        // Show overlay and wire close
        JPanel backdrop = UIFactory.showOverlay(this, card);
        closeBtn.addActionListener(ev -> UIFactory.removeOverlay(backdrop));
    }

    // ── Step row helper ────────────────────────────────────────────────────
    private JPanel makeStepRow(ImageIcon icon, String title, String desc) {
        JPanel row = new JPanel(new BorderLayout(12, 0));
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        JPanel iconWrap = new JPanel(new BorderLayout());
        iconWrap.setOpaque(false);
        JLabel iconLbl = icon != null
                ? new JLabel(icon)
                : new JLabel("▶");
        iconLbl.setPreferredSize(new Dimension(36, 36));
        iconWrap.add(iconLbl, BorderLayout.NORTH);

        JPanel textCol = new JPanel();
        textCol.setLayout(new BoxLayout(textCol, BoxLayout.Y_AXIS));
        textCol.setOpaque(false);

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("SansSerif", Font.BOLD, 12));
        titleLbl.setForeground(Color.WHITE);
        titleLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextArea descArea = new JTextArea(desc);
        descArea.setFont(new Font("SansSerif", Font.PLAIN, 11));
        descArea.setForeground(AppConstants.DIM);
        descArea.setOpaque(false);
        descArea.setEditable(false);
        descArea.setFocusable(false);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setBorder(null);
        descArea.setAlignmentX(Component.LEFT_ALIGNMENT);

        textCol.add(titleLbl);
        textCol.add(Box.createVerticalStrut(2));
        textCol.add(descArea);

        row.add(iconWrap, BorderLayout.WEST);
        row.add(textCol, BorderLayout.CENTER);
        return row;
    }

    // ── Sound toggle button ────────────────────────────────────────────────
    private JButton makeSoundToggleBtn() {
        JButton btn = new JButton() {
            boolean hov;
            {
                setPreferredSize(new Dimension(42, 42));
                setContentAreaFilled(false);
                setBorderPainted(false);
                setFocusPainted(false);
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                setToolTipText("Toggle Sound On/Off");
                addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mouseEntered(java.awt.event.MouseEvent e) { hov = true;  repaint(); }
                    public void mouseExited (java.awt.event.MouseEvent e) { hov = false; repaint(); }
                });
            }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = UIFactory.aa(g);
                boolean on = SoundManager.isSoundOn();
                g2.setColor(hov ? new Color(60, 60, 60) : new Color(35, 35, 35));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(on ? new Color(255, 210, 0, 220) : new Color(120, 120, 120, 200));
                g2.setStroke(new BasicStroke(2f));
                g2.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, 10, 10);
                g2.setFont(new Font("SansSerif", Font.PLAIN, 20));
                g2.setColor(on ? AppConstants.GOLD : new Color(160, 160, 160));
                FontMetrics fm = g2.getFontMetrics();
                String ic = on ? "🔊" : "🔇";
                g2.drawString(ic, (getWidth() - fm.stringWidth(ic)) / 2, getHeight() / 2 + 7);
                g2.dispose();
            }
        };
        btn.addActionListener(e -> {
            SoundManager.setSoundOn(!SoundManager.isSoundOn());
            btn.repaint();
        });
        return btn;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(main::new);
    }
}
