import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.List;

/**
 * LeaderboardScreen — top-20 scores display.
 *
 * FIXES vs original:
 *   - Duplicate color constants removed; using AppConstants
 *   - Assets loaded via AppConstants.loadImage() → JAR-safe
 *   - setDefaultCloseOperation → DISPOSE_ON_CLOSE
 *   - makeFootBtn uses UIFactory buttons for consistent hover/style
 *   - Confirm dialog uses UIFactory.showOverlay / removeOverlay
 *   - Back routes to main (the entry screen)
 */
public class LeaderboardScreen extends JFrame {

    private static final String FILE = "leaderboard.dat";

    public LeaderboardScreen() {
        setTitle("JUMWORD");
        setSize(AppConstants.WIN_W, AppConstants.WIN_H);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(true);

        Image icon = AppConstants.loadImage("assets/icon.png");
        if (icon != null) setIconImage(icon);

        JPanel root = new JPanel(new BorderLayout()) {
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
        root.setOpaque(false);
        setContentPane(root);

        // ── Header ─────────────────────────────────────────────────────────
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(28, 44, 10, 44));

        JLabel crownLbl = new JLabel("🏆");
        crownLbl.setFont(new Font("SansSerif", Font.PLAIN, 40));
        crownLbl.setBorder(new EmptyBorder(0, 0, 0, 14));

        JPanel titleCol = new JPanel();
        titleCol.setLayout(new BoxLayout(titleCol, BoxLayout.Y_AXIS));
        titleCol.setOpaque(false);

        JLabel h1 = new JLabel("LEADERBOARD");
        h1.setFont(new Font("SansSerif", Font.BOLD, 28));
        h1.setForeground(AppConstants.GOLD);
        h1.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel h2 = new JLabel("Top players - Jumword");
        h2.setFont(new Font("SansSerif", Font.PLAIN, 13));
        h2.setForeground(AppConstants.DIM);
        h2.setAlignmentX(Component.LEFT_ALIGNMENT);

        titleCol.add(h1);
        titleCol.add(Box.createVerticalStrut(4));
        titleCol.add(h2);

        header.add(crownLbl, BorderLayout.WEST);
        header.add(titleCol, BorderLayout.CENTER);
        root.add(header, BorderLayout.NORTH);

        // ── Table ──────────────────────────────────────────────────────────
        List<String[]> entries = readAll();

        JPanel tableWrap = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = UIFactory.aa(g);
                g2.setColor(new Color(8, 18, 65));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(new Color(255, 210, 0, 160));
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 16, 16);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        tableWrap.setLayout(new BoxLayout(tableWrap, BoxLayout.Y_AXIS));
        tableWrap.setOpaque(false);
        tableWrap.setBorder(new EmptyBorder(10, 10, 10, 10));

        tableWrap.add(makeRow("#", "PLAYER", "SCORE", "DIFFICULTY", -1));
        tableWrap.add(makeSep());

        if (entries.isEmpty()) {
            JLabel empty = new JLabel("No scores yet — be the first to play!");
            empty.setFont(new Font("SansSerif", Font.ITALIC, 14));
            empty.setForeground(AppConstants.DIM);
            empty.setAlignmentX(Component.CENTER_ALIGNMENT);
            empty.setBorder(new EmptyBorder(30, 0, 30, 0));
            tableWrap.add(empty);
        } else {
            for (int i = 0; i < entries.size(); i++) {
                String[] e = entries.get(i);
                tableWrap.add(makeRow(String.valueOf(i + 1), e[0], e[1], e[2], i));
            }
        }

        JScrollPane scroll = new JScrollPane(tableWrap);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(true);
        scroll.getViewport().setBackground(new Color(8, 18, 65));
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        JPanel scrollHolder = new JPanel(new BorderLayout());
        scrollHolder.setOpaque(false);
        scrollHolder.setBorder(new EmptyBorder(8, 44, 8, 44));
        scrollHolder.add(scroll, BorderLayout.CENTER);
        root.add(scrollHolder, BorderLayout.CENTER);

        // ── Footer ─────────────────────────────────────────────────────────
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 16));
        footer.setOpaque(false);

        JButton backBtn  = UIFactory.blueButton("◀  BACK TO MENU");
        backBtn.setPreferredSize(new Dimension(200, 44));
        JButton clearBtn = UIFactory.redButton("🗑  CLEAR SCORES");
        clearBtn.setPreferredSize(new Dimension(180, 44));

        backBtn.addActionListener(e -> { new main(); dispose(); });
        clearBtn.addActionListener(e -> showStyledConfirm());

        footer.add(backBtn);
        footer.add(clearBtn);
        root.add(footer, BorderLayout.SOUTH);

        setVisible(true);
    }

    // ── Static I/O ─────────────────────────────────────────────────────────
    public static void saveScore(String playerName, int score, String difficulty) {
        List<String[]> all = readAll();

        // Find existing entry for this player (case-insensitive match)
        int existingIdx = -1;
        for (int i = 0; i < all.size(); i++) {
            if (all.get(i)[0].equalsIgnoreCase(playerName)) {
                existingIdx = i;
                break;
            }
        }

        if (existingIdx >= 0) {
            // Player already exists — only update if new score beats their current best
            int existingScore = Integer.parseInt(all.get(existingIdx)[1]);
            if (score > existingScore) {
                // Keep the original name casing already stored; just update score + difficulty
                String savedName = all.get(existingIdx)[0];
                all.set(existingIdx, new String[]{ savedName, String.valueOf(score), difficulty });
            }
            // If new score is not higher, leave the record untouched
        } else {
            // Brand-new player — add their entry
            all.add(new String[]{ playerName, String.valueOf(score), difficulty });
        }

        all.sort((a, b) -> Integer.parseInt(b[1]) - Integer.parseInt(a[1]));
        if (all.size() > 20) all = all.subList(0, 20);
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE))) {
            for (String[] row : all) pw.println(row[0] + "|" + row[1] + "|" + row[2]);
        } catch (IOException ex) { ex.printStackTrace(); }
    }

    public static List<String[]> readAll() {
        List<String[]> list = new ArrayList<>();
        File f = new File(FILE);
        if (!f.exists()) return list;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split("\\|", 3);
                if (p.length == 3) list.add(p);
            }
        } catch (IOException ex) { ex.printStackTrace(); }
        return list;
    }

    // ── Table rendering ─────────────────────────────────────────────────────
    private JPanel makeRow(String rank, String name, String score, String diff, int idx) {
        boolean isHeader = (idx < 0);
        JPanel row = new JPanel(new GridLayout(1, 4)) {
            @Override protected void paintComponent(Graphics g) {
                if (!isHeader) {
                    Graphics2D g2 = UIFactory.aa(g);
                    g2.setColor(idx % 2 == 0 ? AppConstants.ROW_A : AppConstants.ROW_B);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                    g2.dispose();
                }
                super.paintComponent(g);
            }
        };
        row.setOpaque(false);
        row.setBorder(new EmptyBorder(isHeader ? 6 : 10, 18, isHeader ? 6 : 10, 18));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, isHeader ? 36 : 48));

        String rankText = isHeader ? "#" : rankLabel(rank);
        row.add(cell(rankText, isHeader, rankColor(rank, isHeader)));
        row.add(cell(name,  isHeader, isHeader ? AppConstants.DIM : AppConstants.WHITE));

        JLabel scoreLbl = cell(score, isHeader, isHeader ? AppConstants.DIM : AppConstants.GOLD);
        if (!isHeader) scoreLbl.setFont(new Font("SansSerif", Font.BOLD, 14));
        row.add(scoreLbl);
        row.add(cell(diff, isHeader, isHeader ? AppConstants.DIM : diffColor(diff)));
        return row;
    }

    private JLabel cell(String text, boolean header, Color fg) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("SansSerif", header ? Font.BOLD : Font.PLAIN, header ? 11 : 14));
        l.setForeground(fg);
        return l;
    }

    private String rankLabel(String rank) {
        switch (rank) {
            case "1": return "🥇 1";
            case "2": return "🥈 2";
            case "3": return "🥉 3";
            default:  return rank;
        }
    }

    private Color rankColor(String rank, boolean header) {
        if (header) return AppConstants.DIM;
        switch (rank) {
            case "1": return AppConstants.GOLD;
            case "2": return AppConstants.SILVER;
            case "3": return AppConstants.BRONZE;
            default:  return AppConstants.DIM;
        }
    }

    private Color diffColor(String diff) {
        if (diff == null) return AppConstants.DIM;
        switch (diff.toLowerCase()) {
            case "easy":   return new Color(80,  210, 110);
            case "medium": return new Color(255, 175,  50);
            case "hard":   return new Color(255,  80,  80);
            default:       return AppConstants.DIM;
        }
    }

    private JSeparator makeSep() {
        JSeparator s = new JSeparator();
        s.setForeground(new Color(255, 255, 255, 40));
        s.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        return s;
    }

    // ── Confirm overlay ─────────────────────────────────────────────────────
    private void showStyledConfirm() {
        JPanel card = UIFactory.cardPanel();
        card.setLayout(new GridBagLayout());
        card.setPreferredSize(new Dimension(400, 240));
        card.setBorder(new EmptyBorder(28, 36, 28, 36));

        GridBagConstraints gc = new GridBagConstraints();
        gc.gridx = 0; gc.fill = GridBagConstraints.HORIZONTAL; gc.weightx = 1.0;

        JLabel iconLbl = new JLabel("🗑", SwingConstants.CENTER);
        iconLbl.setFont(new Font("SansSerif", Font.PLAIN, 40));
        iconLbl.setForeground(new Color(255, 100, 100));
        gc.gridy = 0; gc.insets = new Insets(0, 0, 6, 0);
        card.add(iconLbl, gc);

        JLabel titleLbl = new JLabel("CLEAR ALL SCORES?", SwingConstants.CENTER);
        titleLbl.setFont(new Font("SansSerif", Font.BOLD, 18));
        titleLbl.setForeground(AppConstants.GOLD);
        gc.gridy = 1; gc.insets = new Insets(0, 0, 6, 0);
        card.add(titleLbl, gc);

        JLabel subLbl = new JLabel("This cannot be undone.", SwingConstants.CENTER);
        subLbl.setFont(new Font("SansSerif", Font.PLAIN, 13));
        subLbl.setForeground(AppConstants.DIM);
        gc.gridy = 2; gc.insets = new Insets(0, 0, 20, 0);
        card.add(subLbl, gc);

        JButton yesBtn = UIFactory.redButton("YES, CLEAR");
        yesBtn.setPreferredSize(new Dimension(140, 44));
        JButton noBtn  = UIFactory.blueButton("CANCEL");
        noBtn.setPreferredSize(new Dimension(120, 44));

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 0));
        btnRow.setOpaque(false);
        btnRow.add(yesBtn);
        btnRow.add(noBtn);
        gc.gridy = 3; gc.insets = new Insets(0, 0, 0, 0);
        card.add(btnRow, gc);

        JPanel backdrop = UIFactory.showOverlay(this, card);

        yesBtn.addActionListener(ev -> {
            UIFactory.removeOverlay(backdrop);
            new File(FILE).delete();
            dispose();
            new LeaderboardScreen();
        });
        noBtn.addActionListener(ev -> UIFactory.removeOverlay(backdrop));
    }
}