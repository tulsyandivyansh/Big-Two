/** Four equal-ranked cards accompanied by one additional card. */
@SuppressWarnings("serial")
public class Quad extends Hand {
    public Quad(CardGamePlayer player, CardList cards) {
        super(player, cards);
    }

    public HandType getCategory() {
        return HandType.QUAD;
    }

    public Card getTopCard() {
        return HandRules.findTopCard(this);
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
