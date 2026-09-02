/** Five cards sharing one suit. */
@SuppressWarnings("serial")
public class Flush extends Hand {
    public Flush(CardGamePlayer player, CardList cards) {
        super(player, cards);
    }

    public HandType getCategory() {
        return HandType.FLUSH;
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
