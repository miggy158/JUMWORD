import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * UIFactory — reusable Swing components used across multiple screens.
 * Eliminates copy-pasted anonymous JButton subclasses.
 *
 * Uses boolean[] for hover state — safely capturable in anonymous classes
 * without any reflection.
 */
public final class UIFactory {

    private UIFactory() {}

    // ── Buttons ────────────────────────────────────────────────────────────

    public static JButton goldButton(String text) {
        final boolean[] hov = {false};
        JButton b = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = aa(g);
                g2.setColor(hov[0] ? AppConstants.GOLD_HOVER : AppConstants.GOLD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                drawCenteredText(g2, getText(), new Font("SansSerif", Font.BOLD, 15),
                                 new Color(10, 25, 80), getWidth(), getHeight());
                g2.dispose();
            }
        };
        wireHover(b, hov); return styleBtn(b);
    }

    public static JButton blueButton(String text) {
        final boolean[] hov = {false};
        JButton b = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = aa(g);
                g2.setColor(hov[0] ? AppConstants.BLUE_HOV : AppConstants.BLUE_BTN);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                drawCenteredText(g2, getText(), new Font("SansSerif", Font.BOLD, 15),
                                 AppConstants.WHITE, getWidth(), getHeight());
                g2.dispose();
            }
        };
        wireHover(b, hov); return styleBtn(b);
    }

    public static JButton redButton(String text) {
        final boolean[] hov = {false};
        JButton b = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = aa(g);
                g2.setColor(hov[0] ? new Color(180, 20, 20) : new Color(140, 25, 25));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.setColor(new Color(255, 100, 100));
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 14, 14);
                drawCenteredText(g2, getText(), new Font("SansSerif", Font.BOLD, 13),
                                 AppConstants.WHITE, getWidth(), getHeight());
                g2.dispose();
            }
        };
        wireHover(b, hov); return styleBtn(b);
    }

    public static JButton closeButton() {
        final boolean[] hov = {false};
        JButton b = new JButton("✕") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = aa(g);
                g2.setColor(hov[0] ? new Color(200, 50, 50) : new Color(100, 40, 40));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                drawCenteredText(g2, "✕", new Font("SansSerif", Font.BOLD, 13),
                                 AppConstants.WHITE, getWidth(), getHeight());
                g2.dispose();
            }
        };
        b.setPreferredSize(new Dimension(32, 32));
        b.setMaximumSize(new Dimension(32, 32));
        wireHover(b, hov); return styleBtn(b);
    }

    public static JButton menuButton(String text) {
        final boolean[] hov = {false};
        JButton b = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = aa(g);
                g2.setColor(hov[0] ? new Color(50, 100, 200) : new Color(33, 85, 163));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.setColor(AppConstants.GOLD);
                g2.setStroke(new BasicStroke(2.5f));
                g2.drawRoundRect(1, 1, getWidth()-3, getHeight()-3, 20, 20);
                drawCenteredText(g2, getText(), new Font("SansSerif", Font.BOLD, 18),
                                 AppConstants.WHITE, getWidth(), getHeight());
                g2.dispose();
            }
        };
        b.setPreferredSize(new Dimension(220, 50));
        b.setMaximumSize(new Dimension(220, 50));
        b.setMinimumSize(new Dimension(220, 50));
        wireHover(b, hov); return styleBtn(b);
    }

    // ── Panels ─────────────────────────────────────────────────────────────

    /** Standard game card: dark navy gradient + gold border. */
    public static JPanel cardPanel() {
        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = aa(g);
                g2.setColor(new Color(0, 0, 0, 80));
                g2.fillRoundRect(6, 6, getWidth(), getHeight(), 24, 24);
                g2.setPaint(AppConstants.cardGradient(getHeight()));
                g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 24, 24);
                g2.setColor(AppConstants.GOLD);
                g2.setStroke(new BasicStroke(2.5f));
                g2.drawRoundRect(1, 1, getWidth()-3, getHeight()-3, 24, 24);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        card.setOpaque(false);
        return card;
    }

    /** Semi-transparent dark overlay for modal dialogs. */
    public static JPanel backdropPanel() {
        JPanel p = new JPanel(new GridBagLayout()) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(new Color(0, 0, 0, 170));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        p.setOpaque(false);
        return p;
    }

    /**
     * Adds a centered overlay card to the frame's layered pane.
     * Stores resize listener in backdrop client properties for cleanup.
     */
    public static JPanel showOverlay(JFrame frame, JPanel card) {
        JLayeredPane layered = frame.getLayeredPane();
        JPanel backdrop = backdropPanel();

        ComponentAdapter resizer = new ComponentAdapter() {
            @Override public void componentResized(ComponentEvent e) {
                backdrop.setBounds(0, 0, layered.getWidth(), layered.getHeight());
                backdrop.revalidate(); backdrop.repaint();
            }
        };
        frame.addComponentListener(resizer);
        backdrop.putClientProperty("_resizer", resizer);
        backdrop.putClientProperty("_frame",   frame);

        backdrop.add(card, new GridBagConstraints());
        backdrop.setBounds(0, 0, layered.getWidth(), layered.getHeight());
        layered.add(backdrop, JLayeredPane.POPUP_LAYER);
        layered.revalidate(); layered.repaint();
        return backdrop;
    }

    /** Removes an overlay created by showOverlay and cleans up. */
    public static void removeOverlay(JPanel backdrop) {
        if (backdrop == null) return;
        Container parent = backdrop.getParent();
        if (!(parent instanceof JLayeredPane)) return;
        JLayeredPane layered = (JLayeredPane) parent;
        JFrame frame         = (JFrame)       backdrop.getClientProperty("_frame");
        ComponentAdapter res = (ComponentAdapter) backdrop.getClientProperty("_resizer");
        if (frame != null && res != null) frame.removeComponentListener(res);
        layered.remove(backdrop);
        layered.revalidate(); layered.repaint();
    }

    // ── Shared rendering helpers ───────────────────────────────────────────

    static Graphics2D aa(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,      RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        return g2;
    }

    static void drawCenteredText(Graphics2D g2, String text, Font font, Color color, int w, int h) {
        g2.setFont(font); g2.setColor(color);
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(text, (w - fm.stringWidth(text)) / 2, h / 2 + fm.getAscent() / 2 - 2);
    }

    // ── Private helpers ────────────────────────────────────────────────────

    private static JButton styleBtn(JButton b) {
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private static void wireHover(JButton b, boolean[] hov) {
        b.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { hov[0] = true;  b.repaint(); }
            @Override public void mouseExited (MouseEvent e) { hov[0] = false; b.repaint(); }
        });
    }
}
