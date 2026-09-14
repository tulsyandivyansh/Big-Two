import java.util.concurrent.ThreadLocalRandom;

/**
 * Mutable 52-card deck with standard suit and rank values.
 *
 * @author Kenneth Wong
 */
public class Deck extends CardList {
    private static final long serialVersionUID = -3886066435694112173L;
    private static final int SUIT_COUNT = 4;
    private static final int RANK_COUNT = 13;

    public Deck() {
        super(createStandardCards());
    }

    protected Deck(CardList initialCards) {
        super(initialCards);
    }

    /** Restores this deck to ordered standard-card contents. */
    public void initialize() {
        replaceWith(createStandardCards());
    }

    /** Applies an in-place Fisher-Yates shuffle. */
    public void shuffle() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        for (int i = size() - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            Card displaced = setCard(i, getCard(j));
            setCard(j, displaced);
        }
    }

    protected final void replaceWith(CardList source) {
        removeAllCards();
        for (int i = 0; i < source.size(); i++) {
            addCard(source.getCard(i));
        }
    }

    private static CardList createStandardCards() {
        CardList cards = new CardList();
        for (int suit = 0; suit < SUIT_COUNT; suit++) {
            for (int rank = 0; rank < RANK_COUNT; rank++) {
                cards.addCard(new Card(suit, rank));
            }
        }
        return cards;
    }
}
