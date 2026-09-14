/**
 * Five cards with consecutive Big Two ranks.
 *
 * @author Divyansh Tulsyan
 */
@SuppressWarnings("serial")
public class Straight extends Hand {
    public Straight(CardGamePlayer player, CardList cards) {
        super(player, cards);
    }

    public HandType getCategory() {
        return HandType.STRAIGHT;
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
