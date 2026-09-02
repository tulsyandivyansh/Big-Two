/** A complete deck whose cards use Big Two rank ordering. */
public class BigTwoDeck extends Deck {
    private static final long serialVersionUID = 1L;
    private static final int SUIT_COUNT = 4;
    private static final int RANK_COUNT = 13;

    public BigTwoDeck() {
        super(createBigTwoCards());
    }

    /** Restores this deck to ordered Big Two card contents. */
    @Override
    public void initialize() {
        replaceWith(createBigTwoCards());
    }

    private static CardList createBigTwoCards() {
        CardList cards = new CardList();
        for (int suit = 0; suit < SUIT_COUNT; suit++) {
            for (int rank = 0; rank < RANK_COUNT; rank++) {
                cards.addCard(new BigTwoCard(suit, rank));
            }
        }
        return cards;
    }
}
