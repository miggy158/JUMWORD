import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

/**
 * PlayerScreen — player name entry before difficulty select.
 *
 * FIXES vs original:
 *   - All color constants removed; using AppConstants
 *   - Buttons replaced by UIFactory.goldButton / blueButton (no copy-paste)
 *   - Assets loaded via AppConstants.loadImage() → JAR-safe
 *   - setDefaultCloseOperation → DISPOSE_ON_CLOSE (not EXIT_ON_CLOSE)
 */
public class PlayerScreen extends JFrame {

    private static final String NAME_FILE = "playername.dat";

    // ── Persist last-used name ──────────────────────────────────────────────
    private static void saveName(String name) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(NAME_FILE))) {
            pw.print(name);
        } catch (IOException ex) { ex.printStackTrace(); }
    }

    private static String loadName() {
        File f = new File(NAME_FILE);
        if (!f.exists()) return "";
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line = br.readLine();
            return (line != null) ? line.trim() : "";
        } catch (IOException ex) { ex.printStackTrace(); }
        return "";
    }

    public PlayerScreen() {
        setTitle("JUMWORD");
        setSize(AppConstants.WIN_W, AppConstants.WIN_H);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(true);

        Image icon = AppConstants.loadImage("assets/icon.png");
        if (icon != null) setIconImage(icon);

        // Root: background + dark overlay
        JPanel root = new JPanel(new GridBagLayout()) {
            final Image bgImg = AppConstants.loadImage("assets/main_bg.png");
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                if (bgImg != null) g2.drawImage(bgImg, 0, 0, getWidth(), getHeight(), this);
                g2.setColor(new Color(5, 10, 40, 170));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        setContentPane(root);

        // Card
        JPanel card = UIFactory.cardPanel();
        card.setLayout(new GridBagLayout());
        card.setPreferredSize(new Dimension(480, 340));
        card.setMaximumSize(new Dimension(540, 380));
        card.setBorder(new EmptyBorder(24, 40, 28, 40));

        GridBagConstraints gc = new GridBagConstraints();
        gc.gridx = 0; gc.gridy = 0;
        gc.gridwidth = 2;
        gc.anchor = GridBagConstraints.CENTER;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.insets = new Insets(0, 0, 4, 0);

        // Game controller icon panel
        JPanel iconPanel = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = UIFactory.aa(g);
                g2.setColor(new Color(255, 210, 0, 60));
                g2.fillOval(getWidth()/2 - 30, getHeight()/2 - 30, 60, 60);
                g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
                FontMetrics fm = g2.getFontMetrics();
                String emoji = "🎮";
                int x = (getWidth() - fm.stringWidth(emoji)) / 2;
                int y = getHeight()/2 + fm.getAscent()/2 - 4;
                g2.setColor(new Color(255, 255, 255, 120));
                g2.drawString(emoji, x + 1, y + 1);
                g2.setColor(Color.WHITE);
                g2.drawString(emoji, x, y);
                g2.dispose();
            }
        };
        iconPanel.setOpaque(false);
        iconPanel.setPreferredSize(new Dimension(80, 70));
        card.add(iconPanel, gc);

        gc.gridy++;
        JLabel titleLbl = new JLabel("ENTER PLAYER NAME", SwingConstants.CENTER);
        titleLbl.setFont(new Font("SansSerif", Font.BOLD, 22));
        titleLbl.setForeground(AppConstants.GOLD);
        card.add(titleLbl, gc);

        // Name field
        gc.gridy++;
        gc.insets = new Insets(0, 0, 4, 0);
        JTextField nameField = new JTextField() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = UIFactory.aa(g);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
            @Override protected void paintBorder(Graphics g) {
                Graphics2D g2 = UIFactory.aa(g);
                g2.setColor(new Color(90, 140, 255));
                g2.setStroke(new BasicStroke(2f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 12, 12);
                g2.dispose();
            }
        };
        nameField.setFont(new Font("SansSerif", Font.PLAIN, 16));
        nameField.setBackground(AppConstants.BLUE_FIELD);
        nameField.setForeground(AppConstants.WHITE);
        nameField.setCaretColor(AppConstants.GOLD);
        nameField.setOpaque(false);
        nameField.setBorder(new EmptyBorder(10, 16, 10, 16));
        nameField.setPreferredSize(new Dimension(380, 48));
        // Auto-fill last used name so the player does not have to retype after dying
        String savedName = loadName();
        if (!savedName.isEmpty()) nameField.setText(savedName);

        card.add(nameField, gc);

        // Warning label
        gc.gridy++;
        gc.insets = new Insets(0, 0, 14, 0);
        JLabel warnLbl = new JLabel(" ");
        warnLbl.setFont(new Font("SansSerif", Font.ITALIC, 11));
        warnLbl.setForeground(AppConstants.WARN_RED);
        card.add(warnLbl, gc);

        // Buttons
        gc.gridy++;
        gc.gridwidth = 1;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.insets = new Insets(0, 0, 0, 8);
        gc.weightx = 1.0;

        JButton playBtn = UIFactory.goldButton("PLAY  ▶");
        playBtn.setPreferredSize(new Dimension(0, 48));
        card.add(playBtn, gc);

        gc.gridx = 1;
        gc.insets = new Insets(0, 8, 0, 0);
        JButton backBtn = UIFactory.blueButton("◀  BACK");
        backBtn.setPreferredSize(new Dimension(0, 48));
        card.add(backBtn, gc);

        // Actions
        ActionListener doPlay = ev -> {
            SoundManager.playButton();
            String name = nameField.getText().trim();
            if (name.isEmpty()) {
                warnLbl.setText("⚠  Please enter your name first!");
                return;
            }
            if (name.contains("|")) {
                warnLbl.setText("⚠  Name cannot contain the '|' character!");
                return;
            }
            saveName(name); // remember name for next time
            dispose();
            SwingUtilities.invokeLater(() -> new MainMenu(name));
        };
        playBtn.addActionListener(doPlay);
        nameField.addActionListener(doPlay);

        backBtn.addActionListener(e -> {
            SoundManager.playButton();
            dispose();
            new Main();
        });

        root.add(card);
        setVisible(true);
    }
}