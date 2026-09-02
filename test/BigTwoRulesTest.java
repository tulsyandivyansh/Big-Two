/** Lightweight regression suite that runs without external test libraries. */
public final class BigTwoRulesTest {
    private static int assertions;
    private final CardGamePlayer player = new CardGamePlayer("Test Player");

    public static void main(String[] args) {
        BigTwoRulesTest suite = new BigTwoRulesTest();
        suite.cardOrderingAndEquality();
        suite.deckContainsEveryCard();
        suite.recognizesBasicHands();
        suite.recognizesFiveCardHands();
        suite.rejectsInvalidHands();
        suite.comparesHandsByBigTwoRules();
        System.out.println("BigTwoRulesTest: " + assertions + " assertions passed");
    }

    private void cardOrderingAndEquality() {
        Card threeOfDiamonds = card(0, 2);
        Card threeOfSpades = card(3, 2);
        Card aceOfDiamonds = card(0, 0);
        Card twoOfDiamonds = card(0, 1);

        check(threeOfSpades.compareTo(threeOfDiamonds) > 0, "Suit breaks equal-rank ties");
        check(aceOfDiamonds.compareTo(threeOfSpades) > 0, "Ace ranks above King and below Two");
        check(twoOfDiamonds.compareTo(aceOfDiamonds) > 0, "Two is the highest rank");
        check(threeOfDiamonds.equals(new BigTwoCard(0, 2)), "Equal cards share suit and rank");
        check(!threeOfDiamonds.equals("not a card"), "Card equality is type safe");
    }

    private void deckContainsEveryCard() {
        Deck deck = new BigTwoDeck();
        check(deck.size() == 52, "Big Two deck has 52 cards");
        for (int suit = 0; suit < 4; suit++) {
            for (int rank = 0; rank < 13; rank++) {
                check(deck.contains(card(suit, rank)), "Deck contains suit " + suit + ", rank " + rank);
            }
        }
    }

    private void recognizesBasicHands() {
        assertType(HandType.SINGLE, cards(card(0, 2)));
        assertType(HandType.PAIR, cards(card(0, 6), card(3, 6)));
        assertType(HandType.TRIPLE, cards(card(0, 9), card(1, 9), card(2, 9)));
    }

    private void recognizesFiveCardHands() {
        assertType(HandType.STRAIGHT,
                cards(card(0, 2), card(1, 3), card(2, 4), card(3, 5), card(0, 6)));
        assertType(HandType.FLUSH,
                cards(card(2, 2), card(2, 4), card(2, 7), card(2, 9), card(2, 12)));
        assertType(HandType.FULL_HOUSE,
                cards(card(0, 4), card(2, 4), card(0, 8), card(1, 8), card(3, 8)));
        assertType(HandType.QUAD,
                cards(card(0, 5), card(1, 5), card(2, 5), card(3, 5), card(0, 10)));
        assertType(HandType.STRAIGHT_FLUSH,
                cards(card(3, 7), card(3, 8), card(3, 9), card(3, 10), card(3, 11)));
    }

    private void rejectsInvalidHands() {
        check(HandFactory.create(player, cards(card(0, 2), card(1, 3))) == null,
                "Unmatched two-card selection is rejected");
        check(HandFactory.create(player,
                cards(card(0, 0), card(1, 1), card(2, 2), card(3, 3), card(0, 4))) == null,
                "Ace-Two-Three-Four-Five is not a straight in this ruleset");
        check(HandFactory.create(null, cards(card(0, 2))) == null, "Missing player is rejected");
        check(HandFactory.create(player, null) == null, "Missing cards are rejected");
        expectIllegalArgument(() -> new BigTwoCard(-1, 2), "Invalid suit is rejected");
        expectIllegalArgument(() -> new BigTwoCard(0, 13), "Invalid rank is rejected");
    }

    private void comparesHandsByBigTwoRules() {
        Hand lowPair = new Pair(player, cards(card(0, 2), card(1, 2)));
        Hand highPair = new Pair(player, cards(card(0, 3), card(1, 3)));
        check(highPair.beats(lowPair), "Higher-ranked pair wins");
        check(!lowPair.beats(highPair), "Lower-ranked pair loses");

        Hand straight = requireHand(cards(card(0, 2), card(1, 3), card(2, 4), card(3, 5), card(0, 6)));
        Hand flush = requireHand(cards(card(1, 2), card(1, 4), card(1, 7), card(1, 9), card(1, 12)));
        Hand fullHouse = requireHand(cards(card(0, 4), card(2, 4), card(0, 8), card(1, 8), card(3, 8)));
        Hand quad = requireHand(cards(card(0, 5), card(1, 5), card(2, 5), card(3, 5), card(0, 10)));
        Hand straightFlush = requireHand(cards(card(3, 7), card(3, 8), card(3, 9), card(3, 10), card(3, 11)));
        check(flush.beats(straight), "Flush beats straight");
        check(fullHouse.beats(flush), "Full house beats flush");
        check(quad.beats(fullHouse), "Quad beats full house");
        check(straightFlush.beats(quad), "Straight flush beats quad");
    }

    private void assertType(HandType expected, CardList cards) {
        Hand hand = requireHand(cards);
        check(hand.getCategory() == expected, "Expected " + expected + " but got " + hand.getCategory());
    }

    private Hand requireHand(CardList cards) {
        Hand hand = HandFactory.create(player, cards);
        check(hand != null, "Expected a legal hand");
        return hand;
    }

    private static BigTwoCard card(int suit, int rank) {
        return new BigTwoCard(suit, rank);
    }

    private static CardList cards(Card... values) {
        CardList list = new CardList();
        for (Card value : values) {
            list.addCard(value);
        }
        return list;
    }

    private static void check(boolean condition, String message) {
        assertions++;
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    private static void expectIllegalArgument(Runnable action, String message) {
        assertions++;
        try {
            action.run();
            throw new AssertionError(message);
        } catch (IllegalArgumentException expected) {
            // Expected path.
        }
    }
}
