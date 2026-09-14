/**
 * Two cards with the same rank.
 *
 * @author Divyansh Tulsyan
 */
@SuppressWarnings("serial")
public class Pair extends Hand {
    public Pair(CardGamePlayer player, CardList cards) {
        super(player, cards);
    }

    public HandType getCategory() {
        return HandType.PAIR;
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
