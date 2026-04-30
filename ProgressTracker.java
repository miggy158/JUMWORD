import java.io.*;
import java.util.*;

/**
 * Tracks the highest completion percentage per player per difficulty.
 *
 * File format — progress.dat (pipe-delimited, one entry per line):
 *   playerName|Easy|87.5
 *   playerName|Medium|65.0
 *
 * Unlock rules:
 *   Medium requires >= 80% completion of Easy
 *   Hard   requires >= 60% completion of Medium
 */
public class ProgressTracker {

    private static final String FILE              = "progress.dat";
    public  static final double MEDIUM_UNLOCK_PCT = 80.0;   // % of Easy needed
    public  static final double HARD_UNLOCK_PCT   = 60.0;   // % of Medium needed

    // Total questions per difficulty (must match Questions.java)
    private static final int EASY_TOTAL   = 40;
    private static final int MEDIUM_TOTAL = 39;
    private static final int HARD_TOTAL   = 38;

    /**
     * Called at game end. Only updates if the new percent beats the stored best.
     *
     * @param playerName  the player's name
     * @param difficulty  "Easy", "Medium", or "Hard"
     * @param score       number of questions answered correctly this run
     */
    public static void updateProgress(String playerName, String difficulty, int score) {
        double newPct = calcPercent(difficulty, score);
        double oldPct = getBestPercent(playerName, difficulty);
        if (newPct > oldPct) {
            savePercent(playerName, difficulty, newPct);
        }
    }

    /**
     * Returns the player's highest recorded completion % for a difficulty (0–100).
     */
    public static double getBestPercent(String playerName, String difficulty) {
        String k = key(playerName, difficulty);
        Map<String, Double> map = loadAll();
        return map.getOrDefault(k, 0.0);
    }

    /** Returns true if Medium is unlocked for this player. */
    public static boolean isMediumUnlocked(String playerName) {
        return getBestPercent(playerName, "Easy") >= MEDIUM_UNLOCK_PCT;
    }

    /** Returns true if Hard is unlocked for this player. */
    public static boolean isHardUnlocked(String playerName) {
        return getBestPercent(playerName, "Medium") >= HARD_UNLOCK_PCT;
    }

    // ── helpers ──────────────────────────────────────────────────────────

    public static double calcPercent(String difficulty, int score) {
        int total;
        switch (difficulty.toLowerCase()) {
            case "easy":   total = EASY_TOTAL;   break;
            case "medium": total = MEDIUM_TOTAL; break;
            default:       total = HARD_TOTAL;   break;
        }
        if (total <= 0) return 0.0;
        double pct = (score * 100.0) / total;
        return Math.min(pct, 100.0);
    }

    private static String key(String player, String diff) {
        return player.trim() + "|" + diff.trim();
    }

    private static Map<String, Double> loadAll() {
        Map<String, Double> map = new LinkedHashMap<>();
        File f = new File(FILE);
        if (!f.exists()) return map;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                String[] parts = line.split("\\|", 3);
                if (parts.length == 3) {
                    String k = parts[0] + "|" + parts[1];
                    try {
                        map.put(k, Double.parseDouble(parts[2]));
                    } catch (NumberFormatException ignored) {}
                }
            }
        } catch (IOException ex) { ex.printStackTrace(); }
        return map;
    }

    private static void savePercent(String playerName, String difficulty, double pct) {
        Map<String, Double> map = loadAll();
        map.put(key(playerName, difficulty), pct);
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE))) {
            for (Map.Entry<String, Double> e : map.entrySet()) {
                // key is "player|diff", store as "player|diff|pct"
                pw.println(e.getKey() + "|" + String.format("%.2f", e.getValue()));
            }
        } catch (IOException ex) { ex.printStackTrace(); }
    }
}
