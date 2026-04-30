import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class MainMenu extends JFrame {

    private final String playerName;

    public MainMenu(String playerName) {
        this.playerName = playerName;

        setTitle("JUMWORD");
        setSize(AppConstants.WIN_W, AppConstants.WIN_H);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);

        Image icon = AppConstants.loadImage("assets/icon.png");
        if (icon != null) {
            setIconImage(icon);
        }

        JPanel root = new JPanel(new GridBagLayout()) {
            final Image bgImg = AppConstants.loadImage("assets/main_bg.png");

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                if (bgImg != null) {
                    g2.drawImage(bgImg, 0, 0, getWidth(), getHeight(), this);
                }
                g2.setColor(new Color(0, 0, 0, 140));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        setContentPane(root);

        JPanel sheet = UIFactory.cardPanel();
        sheet.setLayout(new BoxLayout(sheet, BoxLayout.Y_AXIS));
        sheet.setPreferredSize(new Dimension(600, 500));
        sheet.setBorder(new EmptyBorder(30, 40, 30, 40));
        root.add(sheet);

        JLabel titleLbl = new JLabel("CHOOSE DIFFICULTY", SwingConstants.CENTER);
        titleLbl.setFont(new Font("SansSerif", Font.BOLD, 22));
        titleLbl.setForeground(AppConstants.GOLD);
        titleLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        sheet.add(titleLbl);
        sheet.add(Box.createVerticalStrut(6));

        JLabel subLbl = new JLabel("Unscramble city names across Luzon, Visayas, and Mindanao", SwingConstants.CENTER);
        subLbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
        subLbl.setForeground(AppConstants.DIM);
        subLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        sheet.add(subLbl);
        sheet.add(Box.createVerticalStrut(24));

        boolean mediumUnlocked = ProgressTracker.isMediumUnlocked(playerName);
        boolean hardUnlocked = ProgressTracker.isHardUnlocked(playerName);
        double easyPct = ProgressTracker.getBestPercent(playerName, "Easy");
        double mediumPct = ProgressTracker.getBestPercent(playerName, "Medium");

        DiffButton easy = new DiffButton("EASY", "Metro Manila & Luzon cities", new Color(80, 195, 70), false);
        easy.setAlignmentX(Component.CENTER_ALIGNMENT);
        easy.setMaximumSize(new Dimension(Integer.MAX_VALUE, 55));
        easy.addActionListener(e -> {
            SoundManager.playButton();
            dispose();
            GameScreen.showInFrame(playerName, "Easy");
        });
        sheet.add(easy);
        sheet.add(Box.createVerticalStrut(10));

        DiffButton medium = new DiffButton("MEDIUM", "Visayas cities & provinces", new Color(250, 210, 60), !mediumUnlocked);
        medium.setAlignmentX(Component.CENTER_ALIGNMENT);
        medium.setMaximumSize(new Dimension(Integer.MAX_VALUE, 55));
        if (mediumUnlocked) {
            medium.addActionListener(e -> {
                SoundManager.playButton();
                dispose();
                GameScreen.showInFrame(playerName, "Medium");
            });
        } else {
            medium.addActionListener(e -> {
                SoundManager.playButton();
                showLockedOverlay(String.format(
                        "Need %.0f%% on Easy to unlock Medium!\nYour best: %.1f%%",
                        ProgressTracker.MEDIUM_UNLOCK_PCT, easyPct));
            });
        }
        sheet.add(medium);

        if (!mediumUnlocked) {
            ProgressBar medBar = new ProgressBar(
                    easyPct,
                    ProgressTracker.MEDIUM_UNLOCK_PCT,
                    String.format("Easy best: %.1f%% / %.0f%% needed", easyPct, ProgressTracker.MEDIUM_UNLOCK_PCT),
                    new Color(250, 210, 60));
            medBar.setAlignmentX(Component.CENTER_ALIGNMENT);
            medBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
            medBar.setPreferredSize(new Dimension(460, 40));
            sheet.add(medBar);
        }
        sheet.add(Box.createVerticalStrut(10));

        DiffButton hard = new DiffButton("HARD", "Mindanao cities - test yourself", new Color(255, 110, 110), !hardUnlocked);
        hard.setAlignmentX(Component.CENTER_ALIGNMENT);
        hard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 55));
        if (hardUnlocked) {
            hard.addActionListener(e -> {
                SoundManager.playButton();
                dispose();
                GameScreen.showInFrame(playerName, "Hard");
            });
        } else {
            hard.addActionListener(e -> {
                SoundManager.playButton();
                showLockedOverlay(String.format(
                        "Need %.0f%% on Medium to unlock Hard!\nYour best: %.1f%%",
                        ProgressTracker.HARD_UNLOCK_PCT, mediumPct));
            });
        }
        sheet.add(hard);

        if (!hardUnlocked) {
            ProgressBar hardBar = new ProgressBar(
                    mediumPct,
                    ProgressTracker.HARD_UNLOCK_PCT,
                    String.format("Medium best: %.1f%% / %.0f%% needed", mediumPct, ProgressTracker.HARD_UNLOCK_PCT),
                    new Color(255, 110, 110));
            hardBar.setAlignmentX(Component.CENTER_ALIGNMENT);
            hardBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
            hardBar.setPreferredSize(new Dimension(460, 40));
            sheet.add(hardBar);
        }
        sheet.add(Box.createVerticalStrut(20));

        JButton backBtn = UIFactory.blueButton("< BACK");
        backBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        backBtn.setPreferredSize(new Dimension(160, 45));
        backBtn.setMaximumSize(new Dimension(160, 45));
        backBtn.addActionListener(e -> {
            SoundManager.playButton();
            dispose();
            new Main();
        });
        sheet.add(backBtn);

        setVisible(true);
    }

    private void showLockedOverlay(String message) {
        JPanel card = UIFactory.cardPanel();
        card.setLayout(new GridBagLayout());
        card.setPreferredSize(new Dimension(420, 280));
        card.setBorder(new EmptyBorder(28, 40, 28, 40));

        GridBagConstraints gc = new GridBagConstraints();
        gc.gridx = 0;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.weightx = 1.0;

        JLabel iconLbl = new JLabel("LOCK", SwingConstants.CENTER);
        iconLbl.setFont(new Font("SansSerif", Font.BOLD, 26));
        iconLbl.setForeground(AppConstants.GOLD);
        gc.gridy = 0;
        gc.insets = new Insets(0, 0, 6, 0);
        card.add(iconLbl, gc);

        JLabel titleLbl = new JLabel("LOCKED", SwingConstants.CENTER);
        titleLbl.setFont(new Font("SansSerif", Font.BOLD, 22));
        titleLbl.setForeground(AppConstants.GOLD);
        gc.gridy = 1;
        gc.insets = new Insets(0, 0, 12, 0);
        card.add(titleLbl, gc);

        JPanel msgPanel = new JPanel();
        msgPanel.setLayout(new BoxLayout(msgPanel, BoxLayout.Y_AXIS));
        msgPanel.setOpaque(false);
        for (String line : message.split("\n")) {
            JLabel lbl = new JLabel(line, SwingConstants.CENTER);
            lbl.setFont(new Font("SansSerif", Font.PLAIN, 14));
            lbl.setForeground(AppConstants.DIM);
            lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
            msgPanel.add(lbl);
        }
        gc.gridy = 2;
        gc.insets = new Insets(0, 0, 20, 0);
        card.add(msgPanel, gc);

        JButton okBtn = UIFactory.goldButton("OK");
        okBtn.setPreferredSize(new Dimension(160, 44));

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        btnRow.setOpaque(false);
        btnRow.add(okBtn);
        gc.gridy = 3;
        gc.insets = new Insets(0, 0, 0, 0);
        card.add(btnRow, gc);

        JPanel backdrop = UIFactory.showOverlay(this, card);
        okBtn.addActionListener(e -> UIFactory.removeOverlay(backdrop));
    }

    static class ProgressBar extends JComponent {
        double current;
        double required;
        String label;
        Color barColor;

        ProgressBar(double current, double required, String label, Color barColor) {
            this.current = current;
            this.required = required;
            this.label = label;
            this.barColor = barColor;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = UIFactory.aa(g);
            int w = getWidth();
            boolean met = current >= required;
            int barH = 7;
            int barY = 4;

            g2.setColor(new Color(30, 30, 60));
            g2.fillRoundRect(0, barY, w, barH, barH, barH);

            int reqX = (int) (w * (required / 100.0));
            g2.setColor(new Color(255, 255, 255, 80));
            g2.fillRect(reqX - 1, barY - 2, 2, barH + 4);

            double fillPct = Math.min(current, 100.0);
            int fillW = (int) (w * fillPct / 100.0);
            if (fillW > 0) {
                g2.setColor(met ? new Color(80, 220, 80) : barColor);
                g2.fillRoundRect(0, barY, fillW, barH, barH, barH);
            }

            g2.setFont(new Font("SansSerif", Font.BOLD, 10));
            FontMetrics fm = g2.getFontMetrics();
            int textW = fm.stringWidth(label);
            int textH = fm.getAscent();
            int boxPad = 5;
            int boxW = textW + boxPad * 2;
            int boxH = textH + boxPad * 2 - 2;
            int boxX = 0;
            int boxY = barY + barH + 5;

            Color boxBg = met ? new Color(40, 160, 60) : new Color(25, 35, 80);
            Color boxBdr = met ? new Color(80, 220, 80) : new Color(90, 110, 180);
            Color txtClr = met ? Color.WHITE : new Color(200, 210, 255);

            g2.setColor(boxBg);
            g2.fillRoundRect(boxX, boxY, boxW, boxH, 8, 8);
            g2.setColor(boxBdr);
            g2.setStroke(new BasicStroke(1.2f));
            g2.drawRoundRect(boxX, boxY, boxW, boxH, 8, 8);
            g2.setColor(txtClr);
            g2.drawString(label, boxX + boxPad, boxY + boxPad + textH - 2);
            g2.dispose();
        }
    }

    static class DiffButton extends JButton {
        String label;
        String desc;
        Color color;
        boolean hov;
        boolean locked;

        DiffButton(String label, String desc, Color color, boolean locked) {
            this.label = label;
            this.desc = desc;
            this.color = color;
            this.locked = locked;
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    hov = true;
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    hov = false;
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = UIFactory.aa(g);
            g2.setColor(locked ? new Color(40, 40, 60) : (hov ? AppConstants.BLUE_HOV : AppConstants.BLUE_FIELD));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
            g2.setColor(locked ? new Color(100, 100, 130) : (hov ? AppConstants.GOLD : new Color(90, 140, 255)));
            g2.setStroke(new BasicStroke(2f));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);

            int badgeW = 70;
            int badgeH = 26;
            int badgeX = 16;
            int badgeY = (getHeight() - badgeH) / 2;
            g2.setColor(locked ? new Color(80, 80, 100) : color);
            g2.fillRoundRect(badgeX, badgeY, badgeW, badgeH, 8, 8);

            g2.setFont(new Font("SansSerif", Font.BOLD, 12));
            g2.setColor(locked ? new Color(160, 160, 180) : AppConstants.NAVY);
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(label, badgeX + (badgeW - fm.stringWidth(label)) / 2, badgeY + badgeH / 2 + fm.getAscent() / 2 - 1);

            g2.setFont(new Font("SansSerif", Font.PLAIN, 13));
            g2.setColor(locked ? new Color(130, 130, 160) : AppConstants.WHITE);
            g2.drawString(desc, badgeX + badgeW + 14, getHeight() / 2 + 5);

            if (locked) {
                int lx = getWidth() - 38;
                int ly = getHeight() / 2 - 12;
                int lw = 18;
                int lh = 14;
                g2.setColor(new Color(180, 160, 60));
                g2.setStroke(new BasicStroke(2.5f));
                g2.drawArc(lx + 3, ly - 7, lw - 6, 14, 0, 180);
                g2.setColor(new Color(200, 180, 60));
                g2.fillRoundRect(lx, ly + 4, lw, lh, 5, 5);
                g2.setColor(new Color(40, 40, 60));
                g2.fillOval(lx + lw / 2 - 3, ly + 6, 6, 5);
                g2.fillRect(lx + lw / 2 - 1, ly + 10, 3, 4);
            } else {
                int ax = getWidth() - 20;
                int ay = getHeight() / 2;
                g2.setColor(hov ? AppConstants.GOLD : AppConstants.DIM);
                g2.drawLine(ax - 6, ay - 5, ax, ay);
                g2.drawLine(ax - 6, ay + 5, ax, ay);
            }
            g2.dispose();
        }
    }
}
