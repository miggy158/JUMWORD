import java.awt.*;
import java.io.File;
import java.net.URL;

/**
 * AppConstants — single source of truth for all shared colors, fonts, and dimensions.
 * Eliminates the duplicate color constants scattered across MainMenu, PlayerScreen,
 * LeaderboardScreen, etc.
 */
public final class AppConstants {

    private AppConstants() {} // non-instantiable

    // ── Color palette ──────────────────────────────────────────────────────
    public static final Color NAVY       = new Color(10,  20,  70);
    public static final Color BLUE       = new Color(30,  80, 170);
    public static final Color BLUE_FIELD = new Color(18,  38, 110);
    public static final Color BLUE_BTN   = new Color(33,  85, 163);
    public static final Color BLUE_HOV   = new Color(50, 100, 200);
    public static final Color GOLD       = new Color(255, 210,   0);
    public static final Color GOLD_HOVER = new Color(220, 170,   0);
    public static final Color WHITE      = Color.WHITE;
    public static final Color DIM        = new Color(170, 200, 255);
    public static final Color WARN_RED   = new Color(255, 110, 110);

    // Leaderboard rank colors
    public static final Color SILVER = new Color(192, 192, 192);
    public static final Color BRONZE = new Color(205, 127,  50);

    // Row stripe (leaderboard)
    public static final Color ROW_A = new Color(255, 255, 255, 30);
    public static final Color ROW_B = new Color(255, 255, 255, 12);

    // ── Window defaults ────────────────────────────────────────────────────
    public static final int WIN_W = 1000;
    public static final int WIN_H = 700;

    // ── Common card gradient ───────────────────────────────────────────────
    public static GradientPaint cardGradient(int height) {
        return new GradientPaint(0, 0, new Color(14, 28, 90), 0, height, new Color(28, 68, 160));
    }

    // ── Asset loading (JAR-safe via getResource) ───────────────────────────
    /**
     * Loads an image using the class-loader so it works both from disk
     * and when packaged inside a JAR.
     */
    public static Image loadImage(String resourcePath) {
        URL url = AppConstants.class.getClassLoader().getResource(resourcePath);
        if (url != null) {
            return new javax.swing.ImageIcon(url).getImage();
        }

        File direct = new File(resourcePath);
        if (direct.isFile()) return new javax.swing.ImageIcon(direct.getAbsolutePath()).getImage();

        File cwdRelative = new File(System.getProperty("user.dir"), resourcePath);
        if (cwdRelative.isFile()) return new javax.swing.ImageIcon(cwdRelative.getAbsolutePath()).getImage();

        System.err.println("[Assets] Not found: " + resourcePath);
        return null;
    }

    /** Convenience: load and scale in one call. Returns null if not found. */
    public static Image loadImage(String resourcePath, int w, int h) {
        Image img = loadImage(resourcePath);
        return img == null ? null : img.getScaledInstance(w, h, Image.SCALE_SMOOTH);
    }
}
