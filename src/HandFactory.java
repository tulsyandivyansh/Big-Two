/**
 * Creates the most specific legal hand represented by a card selection.
 *
 * @author Divyansh Tulsyan
 */
public final class HandFactory {
    private HandFactory() {
    }

    public static Hand create(CardGamePlayer player, CardList cards) {
        if (player == null || cards == null) {
            return null;
        }

        switch (cards.size()) {
            case 1:
                return validOrNull(new Single(player, cards));
            case 2:
                return validOrNull(new Pair(player, cards));
            case 3:
                return validOrNull(new Triple(player, cards));
            case 5:
                return createFiveCardHand(player, cards);
            default:
                return null;
        }
    }

    private static Hand createFiveCardHand(CardGamePlayer player, CardList cards) {
        Hand[] candidates = {
            new StraightFlush(player, cards),
            new Quad(player, cards),
            new FullHouse(player, cards),
            new Flush(player, cards),
            new Straight(player, cards)
        };
        for (Hand candidate : candidates) {
            if (candidate.isValid()) {
                return candidate;
            }
        }
        return null;
    }

    private static Hand validOrNull(Hand hand) {
        return hand.isValid() ? hand : null;
    }
}
