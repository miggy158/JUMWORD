import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

public class GameScreen extends JPanel implements ActionListener {

    static final int TOTAL_TIME = 480;
    static final int MAX_HP = 3;

    private static final int MAX_ROUNDS = 40;
    private static final int TILE_W    = 84;
    private static final int TILE_H    = 84;

    // Dynamic tile size — updated per level based on letter count
    private int currentTileSize = TILE_W;

    private static final Color TEXT_DARK = new Color(28, 24, 18);
    private static final Color TEXT_SOFT = new Color(98, 78, 52);
    private static final Color FEEDBACK_GOOD = new Color(45, 122, 62);
    private static final Color FEEDBACK_BAD = new Color(164, 49, 42);
    private static final Color FEEDBACK_INFO = new Color(59, 96, 160);
    private static final Color QUESTION_CARD = new Color(255, 248, 232, 0);
    private static final Color TILES_CARD = new Color(255, 245, 222, 0);
    private static final Color INPUT_BG = new Color(246, 241, 223, 225);
    private static final Color TINT = new Color(255, 248, 220, 68);

    // Pause menu colors — blue/gold style matching PlayerScreen
    private static final Color PAUSE_BG        = new Color(15, 40, 110, 252);
    private static final Color PAUSE_BORDER     = new Color(255, 200, 0, 220);
    private static final Color PAUSE_TITLE      = new Color(255, 210, 0);
    private static final Color PAUSE_TIMER      = new Color(180, 210, 255);
    private static final Color PAUSE_RESUME_FG  = Color.WHITE;
    private static final Color PAUSE_OPTION_FG  = new Color(180, 210, 255);
    private static final Color PAUSE_DANGER_FG  = new Color(160, 195, 255);
    private static final Color PAUSE_HOVER_BG   = new Color(255, 200, 0, 35);
    private static final Color PAUSE_STAR       = new Color(255, 200, 0);

    private static final Font FONT_DIFFICULTY = new Font("SansSerif", Font.BOLD, 58);
    private static final Font FONT_LEVEL = new Font("SansSerif", Font.BOLD, 24);
    private static final Font FONT_QUESTION = new Font("SansSerif", Font.BOLD, 25);
    private static final Font FONT_INPUT = new Font("SansSerif", Font.BOLD, 24);
    private static final Font FONT_NAME = new Font("SansSerif", Font.BOLD, 27);
    private static final Font FONT_TIMER = new Font("SansSerif", Font.PLAIN, 16);
    private static final Font FONT_FEEDBACK = new Font("SansSerif", Font.BOLD, 17);

    // Pause menu fonts
    private static final Font FONT_PAUSE_TITLE   = new Font("SansSerif", Font.BOLD, 46);
    private static final Font FONT_PAUSE_TIMER   = new Font("SansSerif", Font.PLAIN, 18);
    private static final Font FONT_PAUSE_RESUME  = new Font("SansSerif", Font.BOLD, 30);
    private static final Font FONT_PAUSE_OPTION  = new Font("SansSerif", Font.PLAIN, 22);
    private static final Font FONT_PAUSE_DANGER  = new Font("SansSerif", Font.PLAIN, 22);

    final String playerName;
    final String currentMode;
    final ArrayList<Question> questionList = new ArrayList<>();

    int score;
    int coins;
    int hp = MAX_HP;
    int level;
    int timerSeconds = TOTAL_TIME;
    int comboCount;
    boolean shieldActive;
    boolean doubleCoinActive;
    boolean timerExpired;
    boolean paused;
    boolean acceptingInput = true;

    JLabel lblCoins;
    JLabel lblFeedback;
    JTextField txtAnswer;
    HeartsPanel heartsPanel;
    JPanel tilesPanel;
    private JPanel tilesCard;  // kept as field so renderTiles can resize it

    private JLabel lblTimer;
    private JLabel lblLevel;
    private JLabel lblQuestion;
    private final JFrame hostFrame;
    private final Random random = new Random();
    private final java.util.List<TileButton> activeTiles = new ArrayList<>();
    private final Map<Character, ImageIcon> tileIconCache = new HashMap<>();

    private Timer gameTimer;
    private Timer shuffleTimer;          // auto-reshuffles tiles every 5 s
    private String currentJumble = "";   // raw jumble of current level
    private String currentAnswerNormalized = "";

    // Pause overlay reference
    private JPanel pauseOverlay;

    private final Image imgBg;
    private final Image imgPause;
    private final Image imgPlayer;
    private final Image imgShop;
    private final Image imgCoin;

    public static void showInFrame(String playerName, String mode) {
        JFrame frame = new JFrame("JUMWORD - " + mode);
        Image icon = AppConstants.loadImage("assets/icon.png");
        if (icon != null) frame.setIconImage(icon);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(AppConstants.WIN_W, AppConstants.WIN_H);
        frame.setMinimumSize(new Dimension(980, 700));
        frame.setLocationRelativeTo(null);
        frame.setContentPane(new GameScreen(playerName, mode, frame));
        frame.setVisible(true);
    }

    public GameScreen(String playerName, String mode) {
        this(playerName, mode, null);
    }

    public GameScreen(String playerName, String mode, JFrame hostFrame) {
        this.playerName = playerName;
        this.currentMode = mode;
        this.hostFrame = hostFrame;

        imgBg = AppConstants.loadImage("assets/GameScreen_bg.png");
        imgPause = AppConstants.loadImage("assets/menu.png");
        imgPlayer = AppConstants.loadImage("assets/player_icon.png");
        imgShop = AppConstants.loadImage("assets/shop_logo.png");
        imgCoin = AppConstants.loadImage("assets/coin.png");

        setOpaque(true);
        setLayout(new BorderLayout(0, 0));
        setBorder(new EmptyBorder(18, 18, 18, 18));

        loadQuestions();
        add(buildTop(), BorderLayout.NORTH);
        add(buildCenter(), BorderLayout.CENTER);
        add(buildBottom(), BorderLayout.SOUTH);
        installKeyBindings();
        loadLevel();
        startGlobalTimer();
    }

    private JPanel buildTop() {
        JPanel top = new JPanel(new BorderLayout(18, 0));
        top.setOpaque(false);
        top.setBorder(new EmptyBorder(-8, 0, 8, 0));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        left.setOpaque(false);

        JButton pauseBtn = new ImageButton(imgPause, "", 130, 95, 2, false, false);
        pauseBtn.setToolTipText("Pause");
        pauseBtn.addActionListener(e -> togglePause());
        left.add(pauseBtn);

        JLabel playerIcon = new JLabel(scaled(imgPlayer, 54, 54));
        playerIcon.setBorder(new EmptyBorder(2, 0, 0, 0));
        left.add(playerIcon);

        JPanel nameBlock = new JPanel();
        nameBlock.setOpaque(false);
        nameBlock.setLayout(new BoxLayout(nameBlock, BoxLayout.Y_AXIS));

        JLabel lblName = new JLabel(playerName);
        lblName.setFont(FONT_NAME);
        lblName.setForeground(TEXT_DARK);

        lblTimer = new JLabel("8:00");
        lblTimer.setFont(FONT_TIMER);
        lblTimer.setForeground(TEXT_SOFT);

        nameBlock.add(lblName);
        nameBlock.add(Box.createVerticalStrut(2));
        nameBlock.add(lblTimer);
        left.add(nameBlock);

        left.add(Box.createHorizontalStrut(8));
        left.add(buildCoinChip());
        top.add(left, BorderLayout.WEST);

        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));

        JLabel lblDiff = new JLabel(currentMode.toUpperCase(), SwingConstants.CENTER);
        lblDiff.setFont(FONT_DIFFICULTY);
        lblDiff.setForeground(Color.BLACK);
        lblDiff.setAlignmentX(CENTER_ALIGNMENT);

        lblLevel = new JLabel("LEVEL 1", SwingConstants.CENTER);
        lblLevel.setFont(FONT_LEVEL);
        lblLevel.setForeground(TEXT_SOFT);
        lblLevel.setAlignmentX(CENTER_ALIGNMENT);

        center.add(lblDiff);
        center.add(Box.createVerticalStrut(-14));
        center.add(lblLevel);
        top.add(center, BorderLayout.CENTER);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        right.setOpaque(false);
        right.setBorder(new EmptyBorder(0, 0, 0, -32));

        heartsPanel = new HeartsPanel();
        right.add(heartsPanel);

        right.add(Box.createHorizontalStrut(4));

        JButton shopBtn = new ImageButton(imgShop, "SHOP", 214, 80, 0, false, false);
        shopBtn.setToolTipText("Shop");
        shopBtn.addActionListener(e -> openShop());
        right.add(shopBtn);

        top.add(right, BorderLayout.EAST);
        return top;
    }

    private JPanel buildCoinChip() {
        JPanel chip = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        chip.setOpaque(false);
        chip.add(new JLabel(scaled(imgCoin, 28, 28)));

        lblCoins = new JLabel(String.valueOf(coins));
        lblCoins.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblCoins.setForeground(TEXT_DARK);
        chip.add(lblCoins);
        return chip;
    }

    private JPanel buildCenter() {
        JPanel center = new JPanel(new BorderLayout());
        center.setOpaque(false);
        center.setBorder(new EmptyBorder(8, 28, 8, 28));

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.add(Box.createVerticalStrut(10));

        JPanel questionCard = createRoundedPanel(QUESTION_CARD, 34);
        questionCard.setLayout(new BorderLayout());
        questionCard.setAlignmentX(CENTER_ALIGNMENT);
        questionCard.setBorder(new EmptyBorder(50, 30, 22, 30));
        questionCard.setMaximumSize(new Dimension(860, 110));

        lblQuestion = new JLabel("", SwingConstants.CENTER);
        lblQuestion.setFont(FONT_QUESTION);
        lblQuestion.setForeground(TEXT_DARK);
        lblQuestion.setHorizontalAlignment(SwingConstants.CENTER);
        questionCard.add(lblQuestion, BorderLayout.CENTER);
        content.add(questionCard);

        content.add(Box.createVerticalStrut(10));

        tilesCard = createRoundedPanel(TILES_CARD, 42);
        tilesCard.setLayout(new BorderLayout());
        tilesCard.setAlignmentX(CENTER_ALIGNMENT);
        tilesCard.setBorder(new EmptyBorder(16, 28, 16, 28));
        tilesCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 520));

        tilesPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        tilesPanel.setOpaque(false);
        tilesPanel.setBorder(new EmptyBorder(0, 0, 0, 0));
        tilesCard.add(tilesPanel, BorderLayout.CENTER);
        content.add(tilesCard);

        content.add(Box.createVerticalStrut(8));

        lblFeedback = new JLabel(" ", SwingConstants.CENTER);
        lblFeedback.setFont(FONT_FEEDBACK);
        lblFeedback.setForeground(TEXT_DARK);
        lblFeedback.setAlignmentX(CENTER_ALIGNMENT);
        lblFeedback.setMaximumSize(new Dimension(800, 24));
        content.add(lblFeedback);

        content.add(Box.createVerticalGlue());
        center.add(content, BorderLayout.CENTER);
        return center;
    }

    private JPanel buildBottom() {
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER, 18, 0));
        bottom.setOpaque(false);
        bottom.setBorder(new EmptyBorder(0, 0, 36, 0));

        txtAnswer = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = aa(g);
                g2.setColor(INPUT_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 28, 28);
                g2.setColor(new Color(116, 97, 67, 88));
                g2.setStroke(new BasicStroke(2f));
                g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 28, 28);
                g2.dispose();
                super.paintComponent(g);
            }

            @Override
            protected void paintBorder(Graphics g) {}
        };

        txtAnswer.setOpaque(false);
        txtAnswer.setFont(FONT_INPUT);
        txtAnswer.setForeground(TEXT_DARK);
        txtAnswer.setHorizontalAlignment(SwingConstants.CENTER);
        txtAnswer.setBorder(new EmptyBorder(12, 24, 12, 24));
        txtAnswer.setPreferredSize(new Dimension(450, 60));
        txtAnswer.addActionListener(this);

        ((AbstractDocument) txtAnswer.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String text, AttributeSet attr)
                    throws BadLocationException {
                if (text != null) super.insertString(fb, offset, text.toUpperCase(), attr);
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attr)
                    throws BadLocationException {
                if (text != null) super.replace(fb, offset, length, text.toUpperCase(), attr);
            }
        });

        bottom.add(txtAnswer);

        JButton goBtn = new GoButton();
        goBtn.addActionListener(this);
        bottom.add(goBtn);
        return bottom;
    }

    private void loadQuestions() {
        Questions q = new Questions();
        java.util.List<Question> source = "Easy".equalsIgnoreCase(currentMode) ? q.easyQuestions
                : "Medium".equalsIgnoreCase(currentMode) ? q.mediumQuestions
                : q.hardQuestions;
        questionList.clear();
        questionList.addAll(source.subList(0, Math.min(MAX_ROUNDS, source.size())));
        Collections.shuffle(questionList, random);
    }

    private void loadLevel() {
        if (level >= questionList.size()) {
            endGame(true);
            return;
        }

        Question q = questionList.get(level);
        currentAnswerNormalized = normalize(q.getAnswer());

        lblQuestion.setText("<html><div style='text-align:center;width:100%;'>"
                + q.getQuestion() + "</div></html>");
        lblLevel.setText("LEVEL " + (level + 1));
        lblFeedback.setText(" ");
        txtAnswer.setText("");
        txtAnswer.setEnabled(true);
        txtAnswer.setEditable(true);
        acceptingInput = true;
        paused = false;
        timerExpired = false;

        currentJumble = questionList.get(level).getJumble();
        renderTiles(shuffleJumble(currentJumble), false);
        startShuffleTimer();
        txtAnswer.requestFocusInWindow();
    }

    private void renderTiles(String letters, boolean interactive) {
        activeTiles.clear();
        tilesPanel.removeAll();

        // Count actual letters (no spaces) to decide tile size
        int letterCount = 0;
        for (char ch : letters.toCharArray()) if (ch != ' ') letterCount++;

        // Shrink tiles progressively — must accommodate long answers like SAN JOSE DEL MONTE
        if (letterCount >= 20) {
            currentTileSize = 38;
        } else if (letterCount >= 18) {
            currentTileSize = 42;
        } else if (letterCount >= 16) {
            currentTileSize = 48;
        } else if (letterCount >= 14) {
            currentTileSize = 54;
        } else if (letterCount >= 12) {
            currentTileSize = 62;
        } else if (letterCount >= 10) {
            currentTileSize = 70;
        } else if (letterCount >= 8) {
            currentTileSize = 78;
        } else {
            currentTileSize = 84;
        }

        int gap = currentTileSize <= 42 ? 3 : currentTileSize <= 54 ? 4 : currentTileSize <= 68 ? 5 : (currentTileSize <= 78 ? 7 : 10);
        tilesPanel.setLayout(new FlowLayout(FlowLayout.CENTER, gap, 6));

        for (char ch : letters.toUpperCase().toCharArray()) {
            if (ch == ' ') {
                JPanel spacer = new JPanel();
                spacer.setOpaque(false);
                spacer.setPreferredSize(new Dimension(20, currentTileSize));
                tilesPanel.add(spacer);
            } else {
                TileButton tile = new TileButton(ch, interactive);
                activeTiles.add(tile);
                tilesPanel.add(tile);
            }
        }
        // Estimate rows needed so tilesCard grows tall enough for wrapped tiles
        int spacerCount = 0;
        for (char ch : letters.toCharArray()) if (ch == ' ') spacerCount++;
        int availW = 840; // conservative estimate of usable tile-row width in pixels
        int cellW   = currentTileSize + gap;
        int spacerW = 20 + gap;
        int totalW  = letterCount * cellW + spacerCount * spacerW;
        int rows    = (int) Math.ceil((double) totalW / availW);
        rows        = Math.max(1, Math.min(rows, 3));
        int cardH   = rows * (currentTileSize + 12) + 44; // 44 = top+bottom border
        if (tilesCard != null) {
            tilesCard.setPreferredSize(new Dimension(800, cardH));
            tilesCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, cardH + 20));
        }
        tilesPanel.revalidate();
        tilesPanel.repaint();
        if (tilesCard != null) { tilesCard.revalidate(); tilesCard.repaint(); }
    }

    private String normalize(String s) {
        StringBuilder sb = new StringBuilder();
        for (char c : s.toCharArray()) {
            if (Character.isLetterOrDigit(c)) sb.append(Character.toLowerCase(c));
        }
        return sb.toString();
    }

    /**
     * Maximum cross-mix shuffle for 2-word jumbles — makes both sides hard to read.
     */
    private String shuffleJumble(String jumble) {
        if (jumble == null || jumble.length() <= 1) return jumble;

        String[] words = jumble.split(" ", -1);

        if (words.length == 2) {
            int size1 = words[0].length();
            int size2 = words[1].length();

            ArrayList<Character> pool = new ArrayList<>(size1 + size2);
            for (char c : words[0].toCharArray()) pool.add(c);
            for (char c : words[1].toCharArray()) pool.add(c);

            String poolStr;
            int tries = 0;
            do {
                Collections.shuffle(pool, random);
                StringBuilder tmp = new StringBuilder();
                for (char c : pool) tmp.append(c);
                poolStr = tmp.toString();
                tries++;
            } while (tries < 15 && (poolStr.substring(0, size1).equals(words[0].toUpperCase())
                                 || poolStr.substring(0, size1).equals(words[0].toLowerCase())));

            String group1 = poolStr.substring(0, size1);
            String group2 = poolStr.substring(size1);
            return group1 + " " + group2;
        }

        StringBuilder result = new StringBuilder();
        for (int w = 0; w < words.length; w++) {
            String word = words[w];
            if (word.length() > 1) {
                ArrayList<Character> chars = new ArrayList<>();
                for (char c : word.toCharArray()) chars.add(c);
                Collections.shuffle(chars, random);
                for (char c : chars) result.append(c);
            } else {
                result.append(word);
            }
            if (w < words.length - 1) result.append(' ');
        }
        return result.toString();
    }

    private String shuffleLetters(String raw) {
        if (raw.length() <= 1) return raw;

        ArrayList<Character> chars = new ArrayList<>();
        for (char c : raw.toCharArray()) chars.add(c);

        String result = raw;
        int tries = 0;
        while (result.equals(raw) && tries < 10) {
            Collections.shuffle(chars, random);
            StringBuilder sb = new StringBuilder(chars.size());
            for (char c : chars) sb.append(c);
            result = sb.toString();
            tries++;
        }
        return result;
    }

    private void setTilesEnabled(boolean enabled) {
        for (TileButton tile : activeTiles) {
            tile.locked = !enabled;
            tile.repaint();
        }
    }

    private void startGlobalTimer() {
        if (gameTimer != null) gameTimer.stop();

        timerSeconds = TOTAL_TIME;
        updateTimerLabel();
        gameTimer = new Timer(1000, e -> {
            if (!acceptingInput || paused) return;

            timerSeconds--;
            updateTimerLabel();
            if (timerSeconds <= 0) {
                timerExpired = true;
                acceptingInput = false;
                txtAnswer.setEnabled(false);
                setTilesEnabled(false);
                lblFeedback.setForeground(FEEDBACK_BAD);
                lblFeedback.setText("TIME'S UP!");
                gameTimer.stop();

                Timer delay = new Timer(1100, ev -> endGame(false));
                delay.setRepeats(false);
                delay.start();
            }
        });
        gameTimer.start();
    }

    private void startShuffleTimer() {
        if (shuffleTimer != null) shuffleTimer.stop();
        shuffleTimer = new Timer(5000, e -> {
            // Only reshuffle when the player is actively playing
            if (!acceptingInput || paused) return;
            renderTiles(shuffleJumble(currentJumble), false);
            // Brief hint so the player notices the change
            lblFeedback.setForeground(FEEDBACK_INFO);
            lblFeedback.setText("❖ Reshuffled!");
            Timer clear = new Timer(900, ev -> {
                if ("❖ Reshuffled!".equals(lblFeedback.getText()))
                    lblFeedback.setText(" ");
            });
            clear.setRepeats(false);
            clear.start();
        });
        shuffleTimer.start();
    }

    void updateTimerLabel() {
        int m = Math.max(timerSeconds, 0) / 60;
        int s = Math.max(timerSeconds, 0) % 60;
        lblTimer.setText(String.format("%d:%02d", m, s));
        lblTimer.setForeground(timerSeconds <= 30 ? FEEDBACK_BAD : TEXT_SOFT);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // PAUSE MENU — dark gothic overlay matching the reference image
    // ─────────────────────────────────────────────────────────────────────────

    private void togglePause() {
        if (timerExpired) return;

        if (paused) {
            resumeGame();
        } else {
            showPauseMenu();
        }
    }

    private void showPauseMenu() {
        paused = true;
        acceptingInput = false;
        txtAnswer.setEnabled(false);
        txtAnswer.setEditable(false);
        setTilesEnabled(false);
        if (gameTimer != null) gameTimer.stop();
        if (shuffleTimer != null) shuffleTimer.stop();

        JFrame frame = hostFrame != null ? hostFrame : (JFrame) SwingUtilities.getWindowAncestor(this);
        if (frame == null) return;

        // ── Full-screen dimmed backdrop ──────────────────────────────────────
        pauseOverlay = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = aa(g);
                // Dark semi-transparent backdrop
                g2.setColor(new Color(0, 0, 0, 160));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        pauseOverlay.setOpaque(false);

        // ── Pause card panel ─────────────────────────────────────────────────
        JPanel card = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = aa(g);
                int w = getWidth(), h = getHeight();

                // Blue gradient background matching PlayerScreen card
                GradientPaint bgGrad = new GradientPaint(
                    0, 0, new Color(20, 55, 140, 252),
                    0, h, new Color(10, 30, 90, 252));
                g2.setPaint(bgGrad);
                g2.fillRoundRect(0, 0, w, h, 22, 22);

                // Subtle inner highlight at top
                g2.setPaint(new GradientPaint(
                    0, 0, new Color(255, 255, 255, 30),
                    0, h / 3, new Color(255, 255, 255, 0)));
                g2.fillRoundRect(0, 0, w, h / 2, 22, 22);

                // Gold border (outer) matching PlayerScreen
                g2.setColor(PAUSE_BORDER);
                g2.setStroke(new BasicStroke(3.5f));
                g2.drawRoundRect(2, 2, w - 4, h - 4, 20, 20);

                // Inner border (slightly inset, dimmer gold)
                g2.setColor(new Color(255, 200, 0, 80));
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(8, 8, w - 16, h - 16, 14, 14);

                g2.dispose();
                super.paintComponent(g);
            }
        };
        card.setOpaque(false);

        // Card size and position: centered
        int cardW = 400, cardH = 520;

        // ── MENU title ───────────────────────────────────────────────────────
        JLabel titleLbl = new JLabel("MENU", SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = aa(g);
                // Drop shadow
                g2.setFont(getFont());
                g2.setColor(new Color(0, 0, 0, 120));
                FontMetrics fm = g2.getFontMetrics();
                String txt = getText();
                int x = (getWidth() - fm.stringWidth(txt)) / 2;
                g2.drawString(txt, x + 2, fm.getAscent() + 2);
                // Main text
                g2.setColor(PAUSE_TITLE);
                g2.drawString(txt, x, fm.getAscent());
                g2.dispose();
            }
        };
        titleLbl.setFont(FONT_PAUSE_TITLE);
        titleLbl.setBounds(0, 28, cardW, 56);
        card.add(titleLbl);

        // ── Timer label below title ──────────────────────────────────────────
        int m = Math.max(timerSeconds, 0) / 60;
        int s = Math.max(timerSeconds, 0) % 60;
        JLabel timerLbl = new JLabel(String.format("%d:%02d", m, s), SwingConstants.CENTER);
        timerLbl.setFont(FONT_PAUSE_TIMER);
        timerLbl.setForeground(PAUSE_TIMER);
        timerLbl.setBounds(0, 82, cardW, 26);
        card.add(timerLbl);

        // ── Decorative divider ───────────────────────────────────────────────
        JPanel divider1 = buildDivider(cardW);
        divider1.setBounds(18, 116, cardW - 36, 18);
        card.add(divider1);

        // ── Menu items ───────────────────────────────────────────────────────
        // Resume (bright white, bold)
        JButton resumeBtn = buildPauseMenuItem("Resume", FONT_PAUSE_RESUME, PAUSE_RESUME_FG, cardW);
        resumeBtn.setBounds(0, 148, cardW, 52);
        resumeBtn.addActionListener(e -> resumeGame());
        card.add(resumeBtn);

        // Shop
        JButton shopBtn = buildPauseMenuItem("Shop", FONT_PAUSE_OPTION, PAUSE_OPTION_FG, cardW);
        shopBtn.setBounds(0, 206, cardW, 44);
        shopBtn.addActionListener(e -> {
            resumeGame();
            openShop();
        });
        card.add(shopBtn);

        // ── Spacer divider ───────────────────────────────────────────────────
        JPanel divider2 = buildDivider(cardW);
        divider2.setBounds(18, 268, cardW - 36, 18);
        card.add(divider2);

        // Restart
        JButton restartBtn = buildPauseMenuItem("Restart", FONT_PAUSE_DANGER, PAUSE_DANGER_FG, cardW);
        restartBtn.setBounds(0, 296, cardW, 44);
        restartBtn.addActionListener(e -> {
            removePauseOverlay(frame);
            paused = false;
            acceptingInput = true;
            txtAnswer.setEnabled(true);
            txtAnswer.setEditable(true);
            setTilesEnabled(true);
            if (gameTimer != null) gameTimer.start();
            loadLevel();
        });
        card.add(restartBtn);

        // EXIT
        JButton exitBtn = buildPauseMenuItem("EXIT", FONT_PAUSE_DANGER, PAUSE_DANGER_FG, cardW);
        exitBtn.setBounds(0, 344, cardW, 44);
        exitBtn.addActionListener(e -> {
            removePauseOverlay(frame);
            if (gameTimer != null) gameTimer.stop();
            SoundManager.stopBGMusic();
            frame.dispose();
            new main();
        });
        card.add(exitBtn);

        // ── Bottom decorative star ────────────────────────────────────────────
        JLabel starLbl = new JLabel("✦", SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = aa(g);
                g2.setFont(getFont());
                g2.setColor(PAUSE_STAR);
                FontMetrics fm = g2.getFontMetrics();
                String txt = getText();
                g2.drawString(txt,
                    (getWidth() - fm.stringWidth(txt)) / 2,
                    fm.getAscent());
                g2.dispose();
            }
        };
        starLbl.setFont(new Font("SansSerif", Font.BOLD, 28));
        starLbl.setBounds(0, cardH - 62, cardW, 40);
        card.add(starLbl);

        // ── ESC hint ──────────────────────────────────────────────────────────
        JLabel escLbl = new JLabel("Press ESC to resume", SwingConstants.CENTER);
        escLbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
        escLbl.setForeground(new Color(140, 170, 220));
        escLbl.setBounds(0, cardH - 28, cardW, 20);
        card.add(escLbl);

        pauseOverlay.add(card);

        // ── Add overlay to frame's layered pane ──────────────────────────────
        javax.swing.JLayeredPane layered = frame.getLayeredPane();
        layered.add(pauseOverlay, javax.swing.JLayeredPane.POPUP_LAYER);

        // Reposition helper — always fills overlay and centers card
        Runnable reposition = () -> {
            pauseOverlay.setBounds(0, 0, layered.getWidth(), layered.getHeight());
            int ox = (layered.getWidth() - cardW) / 2;
            int oy = (layered.getHeight() - cardH) / 2;
            card.setBounds(ox, oy, cardW, cardH);
            pauseOverlay.revalidate();
            pauseOverlay.repaint();
        };

        // Re-center on overlay resize (normal window drag-resize)
        pauseOverlay.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override public void componentResized(java.awt.event.ComponentEvent e) {
                SwingUtilities.invokeLater(reposition);
            }
        });

        // Re-center when layered pane resizes (covers fullscreen toggle)
        layered.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override public void componentResized(java.awt.event.ComponentEvent e) {
                SwingUtilities.invokeLater(reposition);
            }
        });

        // Re-center on window maximize / restore / fullscreen state change
        frame.addWindowStateListener(e -> SwingUtilities.invokeLater(reposition));

        // Initial position
        reposition.run();
    }

    /** Builds a decorative horizontal rule with star-diamond accents at both ends. */
    private JPanel buildDivider(int width) {
        return new JPanel() {
            { setOpaque(false); }
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = aa(g);
                int cy = getHeight() / 2;
                int starSize = 10;
                int lineY = cy;

                // Stars / diamond accents at both ends
                g2.setColor(PAUSE_BORDER);
                g2.setFont(new Font("SansSerif", Font.BOLD, starSize));

                // Left star
                g2.fillOval(0, cy - 4, 8, 8);
                // Right star
                g2.fillOval(getWidth() - 8, cy - 4, 8, 8);

                // Divider line
                g2.setColor(new Color(255, 200, 0, 120));
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawLine(12, lineY, getWidth() - 12, lineY);

                g2.dispose();
            }
        };
    }

    /** Builds a single clickable menu item button for the pause overlay. */
    private JButton buildPauseMenuItem(String text, Font font, Color fg, int cardW) {
        JButton btn = new JButton(text) {
            private boolean hover = false;

            {
                setContentAreaFilled(false);
                setBorderPainted(false);
                setFocusPainted(false);
                setOpaque(false);
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                addMouseListener(new MouseAdapter() {
                    @Override public void mouseEntered(MouseEvent e) { hover = true; repaint(); }
                    @Override public void mouseExited(MouseEvent e)  { hover = false; repaint(); }
                });
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = aa(g);
                if (hover) {
                    g2.setColor(PAUSE_HOVER_BG);
                    g2.fillRoundRect(20, 4, getWidth() - 40, getHeight() - 8, 10, 10);
                }
                g2.setFont(font);
                g2.setColor(hover ? Color.WHITE : fg);
                FontMetrics fm = g2.getFontMetrics();
                String txt = getText();
                int x = (getWidth() - fm.stringWidth(txt)) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(txt, x, y);
                g2.dispose();
            }
        };
        return btn;
    }

    private void resumeGame() {
        JFrame frame = hostFrame != null ? hostFrame : (JFrame) SwingUtilities.getWindowAncestor(this);
        removePauseOverlay(frame);

        paused = false;
        acceptingInput = true;
        txtAnswer.setEnabled(true);
        txtAnswer.setEditable(true);
        setTilesEnabled(true);
        lblFeedback.setText(" ");
        if (gameTimer != null) gameTimer.start();
        if (shuffleTimer != null) shuffleTimer.start();
        txtAnswer.requestFocusInWindow();
    }

    private void removePauseOverlay(JFrame frame) {
        if (pauseOverlay != null && frame != null) {
            frame.getLayeredPane().remove(pauseOverlay);
            frame.getLayeredPane().repaint();
            pauseOverlay = null;
        }
    }

    // ─────────────────────────────────────────────────────────────────────────

    private void installKeyBindings() {
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ESCAPE"), "togglePause");
        getActionMap().put("togglePause", new javax.swing.AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                togglePause();
            }
        });
    }

    void openShop() {
        if (timerExpired || hostFrame == null) return;

        boolean wasAcceptingInput = acceptingInput;
        acceptingInput = false;
        txtAnswer.setEnabled(false);
        setTilesEnabled(false);
        if (gameTimer != null) gameTimer.stop();
        if (shuffleTimer != null) shuffleTimer.stop();

        new Shopscreen(hostFrame, this).setVisible(true);

        if (!timerExpired && !paused) {
            acceptingInput = wasAcceptingInput;
            txtAnswer.setEnabled(wasAcceptingInput);
            setTilesEnabled(wasAcceptingInput);
            if (gameTimer != null) gameTimer.start();
            if (shuffleTimer != null) shuffleTimer.start();
            if (wasAcceptingInput) txtAnswer.requestFocusInWindow();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!acceptingInput) return;

        String typed = normalize(txtAnswer.getText());
        if (typed.isEmpty()) {
            lblFeedback.setForeground(FEEDBACK_INFO);
            lblFeedback.setText("TYPE YOUR ANSWER");
            return;
        }

        acceptingInput = false;
        txtAnswer.setEnabled(false);
        setTilesEnabled(false);

        if (typed.equals(currentAnswerNormalized)) {
            SoundManager.playCorrectAnswer();
            addScore();
            lblFeedback.setForeground(FEEDBACK_GOOD);
            lblFeedback.setText(comboCount > 0 && comboCount % 3 == 0
                    ? "COMBO x" + comboCount + "! CORRECT"
                    : "CORRECT!");

            Timer delay = new Timer(700, ev -> {
                level++;
                loadLevel();
            });
            delay.setRepeats(false);
            delay.start();
            return;
        }

        SoundManager.playWrongAnswer();
        comboCount = 0;
        if (shieldActive) {
            shieldActive = false;
            lblFeedback.setForeground(FEEDBACK_INFO);
            lblFeedback.setText("SHIELD BLOCKED!");
            resetInputAfter(900);
            return;
        }

        hp--;
        heartsPanel.repaint();
        if (hp <= 0) {
            lblFeedback.setForeground(FEEDBACK_BAD);
            lblFeedback.setText("ANSWER: " + currentAnswerNormalized.toUpperCase());
            renderTiles(currentAnswerNormalized, false);

            Timer delay = new Timer(1800, ev -> endGame(false));
            delay.setRepeats(false);
            delay.start();
        } else {
            lblFeedback.setForeground(FEEDBACK_BAD);
            lblFeedback.setText("WRONG - try again!");
            resetInputAfter(900);
        }
    }

    private void resetInputAfter(int ms) {
        Timer delay = new Timer(ms, ev -> {
            acceptingInput = true;
            txtAnswer.setEnabled(true);
            txtAnswer.setText("");
            setTilesEnabled(true);
            lblFeedback.setText(" ");
            txtAnswer.requestFocusInWindow();
        });
        delay.setRepeats(false);
        delay.start();
    }

    private void addScore() {
        score++;
        comboCount++;

        int earned = doubleCoinActive ? 20 : 10;
        doubleCoinActive = false;
        if (comboCount > 0 && comboCount % 3 == 0) earned += 5;

        coins += earned;
        lblCoins.setText(String.valueOf(coins));
    }

    private void endGame(boolean won) {
        paused = false;
        acceptingInput = false;
        txtAnswer.setEnabled(false);
        if (gameTimer != null) gameTimer.stop();
        if (shuffleTimer != null) shuffleTimer.stop();

        SoundManager.stopBGMusic();
        if (won) {
            SoundManager.playYouWin();
        } else {
            SoundManager.playGameOver();
        }

        LeaderboardScreen.saveScore(playerName, score, currentMode);
        ProgressTracker.updateProgress(playerName, currentMode, score);
        showEndOverlay(won);
    }

    private void showEndOverlay(boolean won) {
        JFrame frame = hostFrame != null ? hostFrame : (JFrame) SwingUtilities.getWindowAncestor(this);
        if (frame == null) {
            new main();
            return;
        }

        JPanel card = UIFactory.cardPanel();
        card.setLayout(new GridBagLayout());
        card.setPreferredSize(new Dimension(480, 380));
        card.setBorder(new EmptyBorder(28, 40, 28, 40));

        GridBagConstraints gc = new GridBagConstraints();
        gc.gridx = 0;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.weightx = 1.0;

        JLabel iconLbl = new JLabel(won ? "[WIN]" : "[LOSE]", SwingConstants.CENTER);
        iconLbl.setFont(new Font("SansSerif", Font.BOLD, 28));
        iconLbl.setForeground(won ? new Color(255, 210, 0) : new Color(220, 30, 60));
        gc.gridy = 0;
        gc.insets = new Insets(0, 0, 4, 0);
        card.add(iconLbl, gc);

        JLabel titleLbl = new JLabel(won ? "YOU WIN!" : "GAME OVER", SwingConstants.CENTER);
        titleLbl.setFont(new Font("SansSerif", Font.BOLD, 22));
        titleLbl.setForeground(AppConstants.GOLD);
        gc.gridy = 1;
        gc.insets = new Insets(0, 0, 6, 0);
        card.add(titleLbl, gc);

        JLabel subLbl = new JLabel(
                won ? "Congratulations, " + playerName + "!"
                        : "Better luck next time, " + playerName + "!",
                SwingConstants.CENTER);
        subLbl.setFont(new Font("SansSerif", Font.PLAIN, 13));
        subLbl.setForeground(AppConstants.DIM);
        gc.gridy = 2;
        gc.insets = new Insets(0, 0, 18, 0);
        card.add(subLbl, gc);

        JPanel infoBox = new JPanel(new GridLayout(3, 2, 0, 6)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = aa(g);
                g2.setColor(new Color(18, 38, 110));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(new Color(90, 140, 255));
                g2.setStroke(new BasicStroke(2f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        infoBox.setOpaque(false);
        infoBox.setBorder(new EmptyBorder(12, 18, 12, 18));

        String[] labels = {"Player:", "Score:", "Difficulty:"};
        String[] vals = {playerName, score + (won ? " / " + questionList.size() : ""), currentMode};
        for (int i = 0; i < 3; i++) {
            JLabel l = new JLabel(labels[i]);
            l.setForeground(AppConstants.DIM);
            l.setFont(new Font("SansSerif", Font.PLAIN, 14));

            JLabel v = new JLabel(vals[i], SwingConstants.RIGHT);
            v.setForeground(Color.WHITE);
            v.setFont(new Font("SansSerif", Font.BOLD, 14));
            infoBox.add(l);
            infoBox.add(v);
        }

        gc.gridy = 3;
        gc.insets = new Insets(0, 0, 20, 0);
        card.add(infoBox, gc);

        JButton okBtn = UIFactory.goldButton("OK");
        okBtn.setPreferredSize(new Dimension(200, 46));
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnRow.setOpaque(false);
        btnRow.add(okBtn);
        gc.gridy = 4;
        gc.insets = new Insets(0, 0, 0, 0);
        card.add(btnRow, gc);

        JPanel backdrop = UIFactory.showOverlay(frame, card);
        okBtn.addActionListener(ev -> {
            UIFactory.removeOverlay(backdrop);
            frame.dispose();
            new main();
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = aa(g);
        if (imgBg != null) {
            g2.drawImage(imgBg, 0, 0, getWidth(), getHeight(), this);
        } else {
            g2.setPaint(new GradientPaint(0, 0, new Color(205, 211, 171), 0, getHeight(),
                    new Color(216, 145, 84)));
            g2.fillRect(0, 0, getWidth(), getHeight());
        }

        g2.setColor(TINT);
        g2.fillRect(0, 0, getWidth(), getHeight());

        g2.dispose();
    }

    private JPanel createRoundedPanel(Color fill, int radius) {
        return new JPanel() {
            {
                setOpaque(false);
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = aa(g);
                if (fill.getAlpha() > 0) {
                    g2.setColor(fill);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
                    g2.setColor(new Color(112, 95, 69, 46));
                    g2.setStroke(new BasicStroke(2f));
                    g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, radius, radius);
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
    }

    private static Graphics2D aa(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        return g2;
    }

    private static ImageIcon scaled(Image img, int w, int h) {
        if (img == null) return null;
        return new ImageIcon(img.getScaledInstance(w, h, Image.SCALE_SMOOTH));
    }

    private ImageIcon getTileIcon(char letter) {
        return getTileIcon(letter, currentTileSize);
    }

    private ImageIcon getTileIcon(char letter, int size) {
        Character cacheKey = letter;
        if (tileIconCache.containsKey(cacheKey)) {
            ImageIcon cached = tileIconCache.get(cacheKey);
            if (cached == null) return null;
            if (cached.getIconWidth() == size) return cached;
        }
        Image raw = AppConstants.loadImage("assets/Alphabet/" + letter + ".png");
        ImageIcon icon = null;
        if (raw != null) {
            BufferedImage source = toBufferedImage(raw);
            BufferedImage scaled = scaleToFit(source, size, size);
            icon = new ImageIcon(scaled);
        }
        tileIconCache.put(cacheKey, icon);
        return icon;
    }

    private BufferedImage toBufferedImage(Image image) {
        if (image instanceof BufferedImage) return (BufferedImage) image;

        BufferedImage out = new BufferedImage(image.getWidth(this), image.getHeight(this),
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = out.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2.drawImage(image, 0, 0, this);
        g2.dispose();
        return out;
    }

    private BufferedImage scaleToFit(BufferedImage source, int boxW, int boxH) {
        BufferedImage out = new BufferedImage(boxW, boxH, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = out.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);

        double scale = Math.min((double) boxW / source.getWidth(), (double) boxH / source.getHeight());
        int drawW = Math.max(1, (int) Math.round(source.getWidth() * scale));
        int drawH = Math.max(1, (int) Math.round(source.getHeight() * scale));
        int x = (boxW - drawW) / 2;
        int y = (boxH - drawH) / 2;
        g2.drawImage(source, x, y, drawW, drawH, this);
        g2.dispose();
        return out;
    }

    class HeartsPanel extends JPanel {
        HeartsPanel() {
            setOpaque(false);
            setPreferredSize(new Dimension(156, 48));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = aa(g);
            for (int i = 0; i < MAX_HP; i++) {
                drawHeart(g2, 8 + (i * 44), 6, i < hp ? new Color(238, 67, 93) : new Color(153, 112, 112, 120));
            }
            g2.dispose();
        }

        private void drawHeart(Graphics2D g2, int x, int y, Color fill) {
            g2.setColor(fill);
            g2.fillOval(x, y, 18, 18);
            g2.fillOval(x + 16, y, 18, 18);
            Polygon bottom = new Polygon(
                    new int[] {x - 1, x + 17, x + 35},
                    new int[] {y + 13, y + 35, y + 13},
                    3);
            g2.fillPolygon(bottom);
        }
    }

    class TileButton extends JButton {
        final char letter;
        final ImageIcon icon;
        boolean hover;
        boolean locked;

        TileButton(char letter, boolean interactive) {
            this.letter = Character.toUpperCase(letter);
            this.icon   = getTileIcon(this.letter, currentTileSize);
            this.locked = !interactive;

            setPreferredSize(new Dimension(currentTileSize, currentTileSize));
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder());
            if (interactive) setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    if (!locked) {
                        hover = true;
                        repaint();
                    }
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    hover = false;
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = aa(g);
            int yOffset = hover && !locked ? -4 : 0;

            if (icon != null) {
                int drawX = (getWidth() - icon.getIconWidth()) / 2;
                int drawY = (getHeight() - icon.getIconHeight()) / 2 + yOffset;
                g2.drawImage(icon.getImage(), drawX, drawY, icon.getIconWidth(), icon.getIconHeight(), this);
            } else {
                Color[] tileColors = {
                    new Color(220, 50, 50),   new Color(50, 130, 220),
                    new Color(50, 170, 80),   new Color(200, 140, 30),
                    new Color(150, 60, 200),  new Color(200, 90, 30),
                    new Color(30, 160, 160),  new Color(180, 50, 120)
                };
                Color bg = tileColors[(letter - 'A') % tileColors.length];
                int rx = 8, ry = 8 + yOffset;
                int rw = getWidth() - 16, rh = getHeight() - 16;
                g2.setColor(new Color(0, 0, 0, 60));
                g2.fillRoundRect(rx + 2, ry + 4, rw, rh, 14, 14);
                g2.setColor(bg);
                g2.fillRoundRect(rx, ry, rw, rh, 14, 14);
                g2.setColor(new Color(255, 255, 255, 60));
                g2.fillRoundRect(rx, ry, rw, rh / 2, 14, 14);
                g2.setColor(new Color(0, 0, 0, 80));
                g2.setStroke(new BasicStroke(2f));
                g2.drawRoundRect(rx, ry, rw, rh, 14, 14);
                int fontSize = Math.max(18, currentTileSize * 38 / 84);
                g2.setFont(new Font("SansSerif", Font.BOLD, fontSize));
                g2.setColor(Color.WHITE);
                FontMetrics fm = g2.getFontMetrics();
                String s = String.valueOf(letter);
                g2.drawString(s,
                    (getWidth() - fm.stringWidth(s)) / 2,
                    ry + rh / 2 + fm.getAscent() / 2 - fm.getDescent() / 2);
            }

            if (hover && !locked) {
                g2.setColor(new Color(255, 255, 255, 52));
                g2.fillRoundRect(8, 8, getWidth() - 16, getHeight() - 16, 18, 18);
            }
            if (locked) {
                g2.setColor(new Color(0, 0, 0, 28));
                g2.fillRoundRect(8, 8, getWidth() - 16, getHeight() - 16, 18, 18);
            }
            g2.dispose();
        }
    }

    class ImageButton extends JButton {
        private final Image icon;
        private final int pad;
        private final boolean shadow;
        private final boolean plate;
        private boolean hover;

        ImageButton(Image icon, String fallback, int w, int h, int pad, boolean shadow, boolean plate) {
            super(fallback);
            this.icon = icon;
            this.pad = pad;
            this.shadow = shadow;
            this.plate = plate;

            setPreferredSize(new Dimension(w, h));
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder());
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    hover = true;
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    hover = false;
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = aa(g);

            if (plate) {
                g2.setColor(new Color(120, 94, 52, hover ? 210 : 170));
                g2.fillOval(0, 0, getWidth(), getHeight());
            }
            if (shadow) {
                g2.setColor(new Color(0, 0, 0, hover ? 36 : 18));
                g2.fillOval(2, 4, getWidth() - 4, getHeight() - 4);
            }

            if (icon != null) {
                float alpha = hover ? 0.88f : 1.0f;
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
                int availW = Math.max(1, getWidth() - (pad * 2));
                int availH = Math.max(1, getHeight() - (pad * 2));
                int imgW = icon.getWidth(this);
                int imgH = icon.getHeight(this);
                double scale = Math.min((double) availW / Math.max(1, imgW), (double) availH / Math.max(1, imgH));
                int drawW = Math.max(1, (int) Math.round(imgW * scale));
                int drawH = Math.max(1, (int) Math.round(imgH * scale));
                int drawX = (getWidth() - drawW) / 2;
                int drawY = (getHeight() - drawH) / 2;
                g2.drawImage(icon, drawX, drawY, drawW, drawH, this);
            } else {
                g2.setFont(new Font("SansSerif", Font.BOLD, 16));
                g2.setColor(Color.WHITE);
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(),
                        (getWidth() - fm.stringWidth(getText())) / 2,
                        getHeight() / 2 + fm.getAscent() / 2 - 2);
            }
            g2.dispose();
        }
    }

    class GoButton extends JButton {
        private boolean hover;

        GoButton() {
            super("GO");
            setPreferredSize(new Dimension(120, 60));
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setOpaque(false);
            setFont(new Font("SansSerif", Font.BOLD, 24));
            setForeground(Color.WHITE);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    hover = true;
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    hover = false;
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = aa(g);
            g2.setColor(hover ? new Color(97, 175, 56) : new Color(72, 148, 38));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 26, 26);
            g2.setColor(Color.BLACK);
            g2.setStroke(new BasicStroke(3.4f));
            g2.drawRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 26, 26);

            g2.setFont(getFont());
            g2.setColor(getForeground());
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(getText(),
                    (getWidth() - fm.stringWidth(getText())) / 2,
                    getHeight() / 2 + fm.getAscent() / 2 - 3);
            g2.dispose();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(main::new);
    }
}