/**
 * Identifies each legal Big Two hand and its strength among five-card hands.
 *
 * @author Divyansh Tulsyan
 */
public enum HandType {
    SINGLE("Single", 0),
    PAIR("Pair", 0),
    TRIPLE("Triple", 0),
    STRAIGHT("Straight", 1),
    FLUSH("Flush", 2),
    FULL_HOUSE("FullHouse", 3),
    QUAD("Quad", 4),
    STRAIGHT_FLUSH("StraightFlush", 5);

    private final String displayName;
    private final int fiveCardStrength;

    HandType(String displayName, int fiveCardStrength) {
        this.displayName = displayName;
        this.fiveCardStrength = fiveCardStrength;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getFiveCardStrength() {
        return fiveCardStrength;
    }
}
