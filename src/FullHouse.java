/**
 * A five-card hand made from one pair and one triple.
 *
 * @author Divyansh Tulsyan
 */
@SuppressWarnings("serial")
public class FullHouse extends Hand {
    public FullHouse(CardGamePlayer player, CardList cards) {
        super(player, cards);
    }

    public HandType getCategory() {
        return HandType.FULL_HOUSE;
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
