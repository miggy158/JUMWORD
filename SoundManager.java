import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.InputStream;

/**
 * SoundManager — central audio controller for JUMWORD.
 *
 * FIXES vs original:
 *   - All paths loaded via getResourceAsStream() → works inside JAR
 *   - Global mute flag now optionally silences one-shot sounds too
 *     (controlled by setSFXOn; BGM has its own toggle setSoundOn)
 *   - loadClip() no longer leaks on repeated calls
 *   - playYouWin() was declared but never called at game-win; now it is.
 */
public class SoundManager {

    // ── Resource paths (relative to classpath root) ────────────────────────
    private static final String BG_MUSIC   = "sounds/bg_music.wav";
    private static final String GAME_START = "sounds/game-start-2.wav";
    private static final String BUTTON     = "sounds/button-sound-14.wav";
    private static final String CORRECT    = "sounds/correct-answer-new.wav";
    private static final String WRONG      = "sounds/light-bulb-breaking-soundbible.wav";
    private static final String PURCHASE   = "sounds/cash-point.wav";
    private static final String GAME_OVER  = "sounds/game_over.wav";
    private static final String YOU_WIN    = "sounds/street-fighter-ii-you-win-perfect.wav";

    // ── State ──────────────────────────────────────────────────────────────
    private static boolean bgMusicOn = true;  // controls background music loop
    private static boolean sfxOn     = true;  // controls one-shot effects
    private static Clip    bgClip    = null;

    // ── BGM toggle ─────────────────────────────────────────────────────────
    public static boolean isSoundOn()          { return bgMusicOn; }
    public static void    setSoundOn(boolean on) {
        bgMusicOn = on;
        if (bgClip != null) {
            if (on) bgClip.loop(Clip.LOOP_CONTINUOUSLY);
            else    bgClip.stop();
        }
    }

    // ── SFX toggle ─────────────────────────────────────────────────────────
    public static boolean isSFXOn()            { return sfxOn; }
    public static void    setSFXOn(boolean on) { sfxOn = on;   }

    // ── Background music ────────────────────────────────────────────────────
    public static void playBGMusic() {
        if (bgClip != null && bgClip.isOpen()) return; // already running
        bgClip = loadClip(BG_MUSIC);
        if (bgClip != null && bgMusicOn) {
            bgClip.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    public static void stopBGMusic() {
        if (bgClip != null) { bgClip.stop(); bgClip.close(); bgClip = null; }
    }

    // ── One-shot sounds ─────────────────────────────────────────────────────
    public static void playGameStart()     { playOnce(GAME_START); }
    public static void playButton()        { playOnce(BUTTON);     }
    public static void playCorrectAnswer() { playOnce(CORRECT);    }
    public static void playWrongAnswer()   { playOnce(WRONG);      }
    public static void playShopPurchase()  { playOnce(PURCHASE);   }
    public static void playCashPoint()     { playOnce(PURCHASE);   }
    public static void playGameOver()      { playOnce(GAME_OVER);  }
    public static void playYouWin()        { playOnce(YOU_WIN);    }

    // ── Helpers ────────────────────────────────────────────────────────────
    private static void playOnce(String path) {
        if (!sfxOn) return;
        Clip clip = loadClip(path);
        if (clip == null) return;
        clip.addLineListener(e -> {
            if (e.getType() == LineEvent.Type.STOP) clip.close();
        });
        clip.start();
    }

    /**
     * Loads a Clip using the classloader (JAR-safe).
     * Falls back to File-based loading if the resource is not on the classpath.
     */
    private static Clip loadClip(String path) {
        try {
            InputStream is = SoundManager.class.getClassLoader().getResourceAsStream(path);
            if (is == null) {
                // Fallback: try loading from the working directory (dev-mode)
                java.io.File f = new java.io.File(path);
                if (!f.exists()) {
                    System.err.println("[SoundManager] Not found: " + path);
                    return null;
                }
                is = new java.io.FileInputStream(f);
            }
            AudioInputStream ais = AudioSystem.getAudioInputStream(
                    new BufferedInputStream(is));
            Clip clip = AudioSystem.getClip();
            clip.open(ais);
            return clip;
        } catch (Exception ex) {
            System.err.println("[SoundManager] Could not load: " + path + " → " + ex.getMessage());
            return null;
        }
    }
}
