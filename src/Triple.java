/**
 * Three cards with the same rank.
 *
 * @author Divyansh Tulsyan
 */
@SuppressWarnings("serial")
public class Triple extends Hand {
    public Triple(CardGamePlayer player, CardList cards) {
        super(player, cards);
    }

    public HandType getCategory() {
        return HandType.TRIPLE;
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
