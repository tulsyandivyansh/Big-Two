/** A legal one-card Big Two hand. */
@SuppressWarnings("serial")
public class Single extends Hand {
    public Single(CardGamePlayer player, CardList cards) {
        super(player, cards);
    }

    public HandType getCategory() {
        return HandType.SINGLE;
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
