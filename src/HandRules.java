import java.util.HashMap;
import java.util.Map;

/**
 * Central rule engine for validating and comparing Big Two hands.
 * Keeping these rules in one place prevents the individual hand classes from
 * drifting into inconsistent interpretations of the game.
 *
 * @author Divyansh Tulsyan
 */
public final class HandRules {
    private static final int FIVE_CARD_HAND_SIZE = 5;

    private HandRules() {
    }

    public static boolean isValid(Hand hand) {
        if (hand == null) {
            return false;
        }

        switch (hand.getCategory()) {
            case SINGLE:
                return hand.size() == 1;
            case PAIR:
                return hand.size() == 2 && allCardsShareRank(hand);
            case TRIPLE:
                return hand.size() == 3 && allCardsShareRank(hand);
            case STRAIGHT:
                return isStraight(hand);
            case FLUSH:
                return hand.size() == FIVE_CARD_HAND_SIZE && allCardsShareSuit(hand);
            case FULL_HOUSE:
                return hasRankGroups(hand, 2, 3);
            case QUAD:
                return hasRankGroups(hand, 1, 4);
            case STRAIGHT_FLUSH:
                return isStraight(hand) && allCardsShareSuit(hand);
            default:
                return false;
        }
    }

    public static boolean beats(Hand candidate, Hand previous) {
        if (candidate == null || previous == null || !candidate.isValid() || !previous.isValid()) {
            return false;
        }
        if (candidate.size() != previous.size()) {
            return false;
        }

        HandType candidateType = candidate.getCategory();
        HandType previousType = previous.getCategory();
        if (candidateType != previousType) {
            return candidate.size() == FIVE_CARD_HAND_SIZE
                    && candidateType.getFiveCardStrength() > previousType.getFiveCardStrength();
        }

        if (candidateType == HandType.FLUSH) {
            int suitComparison = Integer.compare(candidate.getTopCard().getSuit(), previous.getTopCard().getSuit());
            return suitComparison > 0
                    || (suitComparison == 0 && candidate.getTopCard().compareTo(previous.getTopCard()) > 0);
        }
        return candidate.getTopCard().compareTo(previous.getTopCard()) > 0;
    }

    public static Card findTopCard(Hand hand) {
        if (hand.getCategory() == HandType.FULL_HOUSE) {
            return highestCardForGroupSize(hand, 3);
        }
        if (hand.getCategory() == HandType.QUAD) {
            return highestCardForGroupSize(hand, 4);
        }

        Card top = hand.getCard(0);
        for (int i = 1; i < hand.size(); i++) {
            if (hand.getCard(i).compareTo(top) > 0) {
                top = hand.getCard(i);
            }
        }
        return top;
    }

    private static boolean allCardsShareRank(CardList cards) {
        int rank = cards.getCard(0).getRank();
        for (int i = 1; i < cards.size(); i++) {
            if (cards.getCard(i).getRank() != rank) {
                return false;
            }
        }
        return true;
    }

    private static boolean allCardsShareSuit(CardList cards) {
        int suit = cards.getCard(0).getSuit();
        for (int i = 1; i < cards.size(); i++) {
            if (cards.getCard(i).getSuit() != suit) {
                return false;
            }
        }
        return true;
    }

    private static boolean isStraight(CardList cards) {
        if (cards.size() != FIVE_CARD_HAND_SIZE) {
            return false;
        }

        boolean[] ranks = new boolean[13];
        int lowestStrength = Integer.MAX_VALUE;
        int highestStrength = Integer.MIN_VALUE;
        for (int i = 0; i < cards.size(); i++) {
            int strength = rankStrength(cards.getCard(i).getRank());
            if (ranks[strength]) {
                return false;
            }
            ranks[strength] = true;
            lowestStrength = Math.min(lowestStrength, strength);
            highestStrength = Math.max(highestStrength, strength);
        }
        return highestStrength - lowestStrength == FOUR_RANK_GAP;
    }

    private static final int FOUR_RANK_GAP = 4;

    private static int rankStrength(int rank) {
        return (rank + 11) % 13;
    }

    private static boolean hasRankGroups(CardList cards, int firstSize, int secondSize) {
        if (cards.size() != firstSize + secondSize) {
            return false;
        }
        Map<Integer, Integer> counts = rankCounts(cards);
        return counts.size() == 2 && counts.containsValue(firstSize) && counts.containsValue(secondSize);
    }

    private static Card highestCardForGroupSize(CardList cards, int groupSize) {
        Map<Integer, Integer> counts = rankCounts(cards);
        Card top = null;
        for (int i = 0; i < cards.size(); i++) {
            Card card = cards.getCard(i);
            if (counts.get(card.getRank()) == groupSize && (top == null || card.compareTo(top) > 0)) {
                top = card;
            }
        }
        return top;
    }

    private static Map<Integer, Integer> rankCounts(CardList cards) {
        Map<Integer, Integer> counts = new HashMap<Integer, Integer>();
        for (int i = 0; i < cards.size(); i++) {
            int rank = cards.getCard(i).getRank();
            counts.put(rank, counts.containsKey(rank) ? counts.get(rank) + 1 : 1);
        }
        return counts;
    }
}
