/**
 * A straight whose five cards all share the same suit.
 *
 * @author Divyansh Tulsyan
 */
@SuppressWarnings("serial")
public class StraightFlush extends Hand {
    public StraightFlush(CardGamePlayer player, CardList cards) {
        super(player, cards);
    }

    public HandType getCategory() {
        return HandType.STRAIGHT_FLUSH;
    }

    public boolean isValid() {
        return HandRules.isValid(this);
    }

    public boolean beats(Hand hand) {
        return HandRules.beats(this, hand);
    }

    public String getType() {
        return getCategory().getDisplayName();
    }
}
