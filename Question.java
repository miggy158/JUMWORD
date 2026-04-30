/**
 * Question — data model for a single game question.
 *
 * Extracted from the bottom of GameScreen.java where it was tacked on
 * as a file-level class (not an inner class, but living in the same file).
 * Having it in its own file prevents confusion and is proper Java structure.
 */
public class Question {
    private final String   question;
    private final String   jumble;
    private final String[] choices;
    private final String   answer;

    public Question(String question, String jumble, String[] choices, String answer) {
        this.question = question;
        this.jumble   = jumble;
        this.choices  = choices;
        this.answer   = answer;
    }

    public String   getQuestion() { return question; }
    public String   getJumble()   { return jumble;   }
    public String[] getOptions()  { return choices;  }
    public String   getAnswer()   { return answer;   }
}
