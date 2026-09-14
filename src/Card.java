import java.io.Serializable;
import java.util.Objects;

/**
 * This class is used for representing a card in general card games.
 * 
 * @author Kenneth Wong
 */
public class Card implements Comparable<Card>, Serializable {
	private static final long serialVersionUID = -713898713776577970L;
	static boolean SUPPORT_COLOR = false;
	private static final char[] SUITS = { '\u2666', '\u2663', '\u2665', '\u2660' }; // {Diamond, Club, Heart, Spade}
	private static final char[] RANKS = { 'A', '2', '3', '4', '5', '6', '7', '8', '9', '0', 'J', 'Q', 'K' };

	protected final int suit; // 0 - 3
	protected final int rank; // 0 - 12
	private static final int MIN_SUIT = 0;
	private static final int MAX_SUIT = 3;
	private static final int MIN_RANK = 0;
	private static final int MAX_RANK = 12;

	/**
	 * Creates and returns an instance of the Card class.
	 * 
	 * @param suit an int value between 0 and 3 representing the suit of a card:
	 *             <p>
	 *             0 = Diamond, 1 = Club, 2 = Heart, 3 = Spade
	 * @param rank an int value between 0 and 12 representing the rank of a card:
	 *             <p>
	 *             0 = 'A', 1 = '2', 2 = '3', ..., 8 = '9', 9 = '10', 10 = 'J', 11 =
	 *             'Q', 12 = 'K'
	 */
	public Card(int suit, int rank) {
		if (suit < MIN_SUIT || suit > MAX_SUIT) {
			throw new IllegalArgumentException("Suit must be between 0 and 3: " + suit);
		}
		if (rank < MIN_RANK || rank > MAX_RANK) {
			throw new IllegalArgumentException("Rank must be between 0 and 12: " + rank);
		}
		this.suit = suit;
		this.rank = rank;
	}

	/**
	 * Returns the suit of a card.
	 * 
	 * @return an int value between 0 and 3 representing the suit of a card:
	 *         <p>
	 *         0 = Diamond, 1 = Club, 2 = Heart, 3 = Spade
	 */
	public int getSuit() {
		return suit;
	}

	/**
	 * Returns the rank of a card.
	 * 
	 * @return an int value between 0 and 12 representing the rank of a card:
	 *         <p>
	 *         0 = 'A', 1 = '2', 2 = '3', ..., 8 = '9', 9 = '0', 10 = 'J', 11 = 'Q',
	 *         12 = 'K'
	 */
	public int getRank() {
		return rank;
	}

	/**
	 * Returns a string representation of the card.
	 * 
	 * @return a string representation of the card
	 */
	public String toString() {
		if (SUPPORT_COLOR && (this.suit % 2 == 0)) {
			return "\u001B[31m" + SUITS[this.suit] + RANKS[this.rank] + "\u001B[0m";
		} else {
			return "" + SUITS[this.suit] + RANKS[this.rank];
		}
	}

	/**
	 * Compares this card with the specified card for order.
	 * 
	 * @param card the card to be compared
	 * @return a negative integer, zero, or a positive integer as this card is less
	 *         than, equal to, or greater than the specified card
	 */
	public int compareTo(Card card) {
		Objects.requireNonNull(card, "card");
		if (this.rank > card.rank) {
			return 1;
		} else if (this.rank < card.rank) {
			return -1;
		} else if (this.suit > card.suit) {
			return 1;
		} else if (this.suit < card.suit) {
			return -1;
		} else {
			return 0;
		}
	}

	/**
	 * Indicates whether the specified card is "equal to" this one.
	 * 
	 * @param card the reference object with which to compare
	 * @return true if this card has the same rank and suit as the specified card;
	 *         false otherwise
	 */
	public boolean equals(Object card) {
		if (this == card) {
			return true;
		}
		if (!(card instanceof Card)) {
			return false;
		}
		Card other = (Card) card;
		return rank == other.rank && suit == other.suit;
	}

	/**
	 * Returns a hash code value for the card.
	 * 
	 * @return a hash code value for the card
	 */
	public int hashCode() {
		return 31 * rank + suit;
	}
}
