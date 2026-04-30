import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

/**
 * Shopscreen — modal dialog overlaying the game for buying power-ups.
 *
 * FIXES vs original:
 *   - loadImg() replaced with AppConstants.loadImage() → JAR-safe
 *   - Item prices use coins (the in-game currency), not ₱ symbol implying PHP
 *   - Timer is paused/resumed by the caller (GameScreen.openShop), not here
 *   - dispose() is all that's needed since GameScreen handles timer
 */
public class Shopscreen extends JDialog {

    // ── Palette ───────────────────────────────────────────────────────────
    static final Color PURPLE_BG    = new Color(0x40, 0x12, 0x68);
    static final Color GREEN_BTN    = new Color(0x1E, 0xCC, 0x3D);
    static final Color GREEN_HOV    = new Color(0x17, 0xA8, 0x30);
    static final Color GOLD         = new Color(255, 210,   0);
    static final Color WHITE        = Color.WHITE;
    static final Color DIM          = new Color(220, 200, 255);
    static final Color COIN_COLOR   = new Color(255, 210,   0);
    static final Color TOAST_SUCCESS = new Color(0x1E, 0xCC, 0x3D);
    static final Color TOAST_ERROR   = new Color(0xE8, 0x30, 0x30);
    static final Color TOAST_INFO    = new Color(0xFF, 0xD2, 0x00);

    // ── Item definitions ──────────────────────────────────────────────────
    private static final String[] ITEM_NAMES  = { "Double Heart",       "Hint",               "Time Freeze",    "Shield",            "2× Coin"       };
    private static final String[] ITEM_ICONS  = { "power-ups/Double heart", "power-ups/Hint", "power-ups/Time freeze", "power-ups/shield", "power-ups/2xCoin" };
    private static final String[] ITEM_DESC   = { "Restore 1 HP",       "Reveal 1st letter",  "+30 seconds",    "Block 1 wrong",     "2× coins next" };
    private static final int[]    ITEM_PRICES = { 100,                   25,                   50,               75,                  200             };

    private static final int[][] SHELF_ROWS = { {0, 1, 2}, {3, 4} };

    private final GameScreen game;

    // ── Toast ─────────────────────────────────────────────────────────────
    private ToastPanel toastPanel;
    private Timer      toastTimer;
    private Timer      fadeTimer;
    private float      toastAlpha  = 0f;

    // ═══════════════════════════════════════════════════════════════════════
    public Shopscreen(JFrame parent, GameScreen game) {
        super(parent, true);
        this.game = game;

        setUndecorated(true);
        setBackground(new Color(0, 0, 0, 0));

        Insets ins = parent.getInsets();
        int w = parent.getWidth()  - ins.left - ins.right;
        int h = parent.getHeight() - ins.top  - ins.bottom;
        setSize(w, h);
        Point loc = parent.getLocationOnScreen();
        setLocation(loc.x + ins.left, loc.y + ins.top);

        // Root overlay
        JPanel root = new JPanel(new GridBagLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(new Color(0x40, 0x12, 0x68, 235));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        root.setOpaque(false);

        JLayeredPane layered = new JLayeredPane();
        layered.setOpaque(false);
        setContentPane(layered);

        // Card with shop background image
        JPanel card = new JPanel(new BorderLayout(0, 0)) {
            final Image shopBg = AppConstants.loadImage("assets/shop_bg.png");
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = UIFactory.aa(g);
                if (shopBg != null) {
                    g2.drawImage(shopBg, 0, 0, getWidth(), getHeight(), this);
                } else {
                    g2.setPaint(new GradientPaint(0, 0, new Color(0xC0, 0x60, 0x20),
                                                  0, getHeight(), new Color(0x80, 0x30, 0x05)));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 28, 28);
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        card.setOpaque(false);
        card.setPreferredSize(new Dimension(660, 480));
        card.setBorder(new EmptyBorder(110, 50, 55, 50));

        // Coin row + close button
        JLabel coinLbl = makeCoinLabel();
        JPanel coinPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        coinPanel.setOpaque(false);
        coinPanel.add(coinLbl);

        JButton closeBtn = UIFactory.closeButton();
        closeBtn.setPreferredSize(new Dimension(36, 36));
        closeBtn.addActionListener(e -> dispose());

        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setOpaque(false);
        topRow.setBorder(new EmptyBorder(0, 0, 8, 0));
        topRow.add(coinPanel, BorderLayout.WEST);
        topRow.add(closeBtn,  BorderLayout.EAST);
        card.add(topRow, BorderLayout.NORTH);

        // Item shelves
        JPanel shelves = new JPanel();
        shelves.setLayout(new BoxLayout(shelves, BoxLayout.Y_AXIS));
        shelves.setOpaque(false);
        for (int[] row : SHELF_ROWS) {
            JPanel shelf = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 4));
            shelf.setOpaque(false);
            for (int idx : row) shelf.add(makeItemCard(idx, coinLbl));
            shelves.add(shelf);
        }
        card.add(shelves, BorderLayout.CENTER);

        root.setLayout(new GridBagLayout());
        root.add(card);
        root.setBounds(0, 0, w, h);
        layered.add(root, JLayeredPane.DEFAULT_LAYER);

        // Toast
        toastPanel = new ToastPanel();
        toastPanel.setVisible(false);
        int tw = 340, th = 64;
        toastPanel.setBounds((w - tw) / 2, h - th - 36, tw, th);
        layered.add(toastPanel, JLayeredPane.POPUP_LAYER);

        addComponentListener(new ComponentAdapter() {
            @Override public void componentResized(ComponentEvent e) {
                int nw = getWidth(), nh = getHeight();
                root.setBounds(0, 0, nw, nh);
                toastPanel.setBounds((nw - tw) / 2, nh - th - 36, tw, th);
            }
        });
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  Toast
    // ═══════════════════════════════════════════════════════════════════════
    private class ToastPanel extends JPanel {
        String message = ""; Color accent = TOAST_INFO; String icon = "ℹ";

        ToastPanel() { setOpaque(false); }

        void show(String msg, Color accentColor, String emoji) {
            this.message = msg; this.accent = accentColor; this.icon = emoji;
            toastAlpha = 0f;
            setVisible(true);
            repaint();
            if (toastTimer != null) toastTimer.stop();
            if (fadeTimer  != null) fadeTimer.stop();

            fadeTimer = new Timer(16, null);
            fadeTimer.addActionListener(ev -> {
                toastAlpha = Math.min(1f, toastAlpha + 0.07f);
                repaint();
                if (toastAlpha >= 1f) {
                    fadeTimer.stop();
                    toastTimer = new Timer(1800, ev2 -> startFadeOut());
                    toastTimer.setRepeats(false);
                    toastTimer.start();
                }
            });
            fadeTimer.start();
        }

        private void startFadeOut() {
            if (fadeTimer != null) fadeTimer.stop();
            fadeTimer = new Timer(16, null);
            fadeTimer.addActionListener(ev -> {
                toastAlpha = Math.max(0f, toastAlpha - 0.06f);
                repaint();
                if (toastAlpha <= 0f) { fadeTimer.stop(); setVisible(false); }
            });
            fadeTimer.start();
        }

        @Override protected void paintComponent(Graphics g) {
            if (toastAlpha <= 0f) return;
            Graphics2D g2 = UIFactory.aa(g);
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, toastAlpha));
            int w = getWidth(), h = getHeight();
            g2.setColor(new Color(0, 0, 0, 80));
            g2.fillRoundRect(4, 6, w-4, h-4, 20, 20);
            g2.setColor(new Color(20, 8, 40, 240));
            g2.fillRoundRect(0, 0, w-2, h-2, 20, 20);
            g2.setColor(accent);
            g2.fillRoundRect(0, 0, 6, h-2, 8, 8);
            g2.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 140));
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawRoundRect(0, 0, w-3, h-3, 20, 20);
            g2.setFont(new Font("SansSerif", Font.PLAIN, 22));
            g2.setColor(WHITE);
            g2.drawString(icon, 18, h/2 + 9);
            g2.setColor(new Color(255, 255, 255, 30));
            g2.setStroke(new BasicStroke(1f));
            g2.drawLine(52, 12, 52, h-14);
            g2.setFont(new Font("SansSerif", Font.BOLD, 13));
            g2.setColor(WHITE);
            FontMetrics fm = g2.getFontMetrics();
            // Simple single-line centered text (message is always short)
            g2.drawString(message, 62, h/2 + fm.getAscent()/2 - 1);
            g2.dispose();
        }
    }

    private void showPopup(String msg) {
        Color accent; String ic;
        String lower = msg.toLowerCase();
        if (lower.contains("not enough") || lower.contains("already full") || lower.contains("refunded")) {
            accent = TOAST_ERROR; ic = "✕";
        } else if (lower.contains("restored") || lower.contains("active") || lower.contains("added")
                || lower.contains("used") || lower.contains("next correct")) {
            accent = TOAST_SUCCESS; ic = "✓";
        } else {
            accent = TOAST_INFO; ic = "ℹ";
        }
        toastPanel.show(msg, accent, ic);
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  Item cards
    // ═══════════════════════════════════════════════════════════════════════
    private JPanel makeItemCard(int idx, JLabel coinLbl) {
        JPanel card = new JPanel(new BorderLayout(0, 4));
        card.setOpaque(false);
        card.setPreferredSize(new Dimension(110, 130));
        card.setBorder(new EmptyBorder(6, 6, 6, 6));

        Image img = AppConstants.loadImage("assets/" + ITEM_ICONS[idx] + ".png", 42, 42);
        JLabel iconLbl = img != null
                ? new JLabel(new ImageIcon(img))
                : new JLabel("?", SwingConstants.CENTER);
        iconLbl.setHorizontalAlignment(SwingConstants.CENTER);
        card.add(iconLbl, BorderLayout.CENTER);

        JPanel bottom = new JPanel();
        bottom.setLayout(new BoxLayout(bottom, BoxLayout.Y_AXIS));
        bottom.setOpaque(false);

        JLabel nameLbl = new JLabel(ITEM_NAMES[idx], SwingConstants.CENTER);
        nameLbl.setFont(new Font("SansSerif", Font.BOLD, 10));
        nameLbl.setForeground(WHITE);
        nameLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel descLbl = new JLabel(ITEM_DESC[idx], SwingConstants.CENTER);
        descLbl.setFont(new Font("SansSerif", Font.PLAIN, 8));
        descLbl.setForeground(DIM);
        descLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton buyBtn = makeBuyBtn(idx, coinLbl);
        buyBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        bottom.add(nameLbl);
        bottom.add(Box.createVerticalStrut(1));
        bottom.add(descLbl);
        bottom.add(Box.createVerticalStrut(5));
        bottom.add(buyBtn);
        card.add(bottom, BorderLayout.SOUTH);
        return card;
    }

    private JButton makeBuyBtn(int idx, JLabel coinLbl) {
        int price = ITEM_PRICES[idx];
        Image coinImg = AppConstants.loadImage("assets/coin.png");

        JButton btn = new JButton() {
            boolean hov;
            {
                setPreferredSize(new Dimension(90, 24));
                setMaximumSize(new Dimension(90, 24));
                setContentAreaFilled(false);
                setBorderPainted(false);
                setFocusPainted(false);
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                addMouseListener(new MouseAdapter() {
                    public void mouseEntered(MouseEvent e) { hov = true;  repaint(); }
                    public void mouseExited (MouseEvent e) { hov = false; repaint(); }
                });
            }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = UIFactory.aa(g);
                g2.setColor(isEnabled() ? (hov ? GREEN_HOV : GREEN_BTN) : new Color(100, 100, 100));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                int cx = 6, cy = (getHeight()-16)/2;
                if (coinImg != null) g2.drawImage(coinImg, cx, cy, 16, 16, this);
                else {
                    g2.setColor(COIN_COLOR); g2.fillOval(cx, cy, 16, 16);
                }
                g2.setFont(new Font("SansSerif", Font.BOLD, 10));
                g2.setColor(WHITE);
                g2.drawString(" " + price, cx + 20, getHeight()/2 + 5);
                g2.dispose();
            }
        };
        btn.addActionListener(e -> handlePurchase(idx, btn, coinLbl));
        return btn;
    }

    private void handlePurchase(int idx, JButton btn, JLabel coinLbl) {
        int price = ITEM_PRICES[idx];
        if (game.coins < price) {
            showPopup("Not enough coins! Need " + price);
            return;
        }
        game.coins -= price;
        coinLbl.setText("  " + game.coins);
        game.lblCoins.setText(String.valueOf(game.coins));
        SoundManager.playCashPoint();

        switch (idx) {
            case 0: // Double Heart
                if (game.hp < GameScreen.MAX_HP) {
                    game.hp++;
                    game.heartsPanel.repaint();
                    showPopup("❤ +1 HP restored!");
                } else {
                    game.coins += price;
                    coinLbl.setText("  " + game.coins);
                    game.lblCoins.setText(String.valueOf(game.coins));
                    showPopup("HP already full! Refunded.");
                }
                break;
            case 1: // Hint
                String answer = game.questionList.get(game.level).getAnswer();
                game.lblFeedback.setForeground(new Color(30, 160, 220));
                game.lblFeedback.setText("💡 Hint: starts with \"" + Character.toUpperCase(answer.charAt(0)) + "\"");
                showPopup("Hint used!");
                break;
            case 2: // Time Freeze
                game.timerSeconds = Math.min(game.timerSeconds + 30, GameScreen.TOTAL_TIME);
                game.updateTimerLabel();
                showPopup("⏱ +30 seconds added!");
                break;
            case 3: // Shield
                game.shieldActive = true;
                showPopup("🛡 Shield active! Next wrong = safe.");
                break;
            case 4: // 2× Coin
                game.doubleCoinActive = true;
                showPopup("⭐ 2× Coins on next correct answer!");
                break;
        }
    }

    private JLabel makeCoinLabel() {
        Image coinImg = AppConstants.loadImage("assets/coin.png");
        JLabel lbl = new JLabel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = UIFactory.aa(g);
                g2.setColor(new Color(0, 0, 0, 120));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                if (coinImg != null) g2.drawImage(coinImg, 6, (getHeight()-20)/2, 20, 20, this);
                g2.setFont(new Font("SansSerif", Font.BOLD, 14));
                g2.setColor(COIN_COLOR);
                g2.drawString(getText(), 30, getHeight()/2 + 5);
                g2.dispose();
            }
        };
        lbl.setText("  " + game.coins);
        lbl.setPreferredSize(new Dimension(90, 32));
        lbl.setOpaque(false);
        return lbl;
    }
}
