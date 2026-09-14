/**
 * It is the subclass of Card class which is used to model a card used in Big Two 
 * card game.It inherits all the methods from the Card class and overrides compareTo() 
 * method so that it can reflect the ordering of cards used in a Big Two card game.
 * 
 * @author Divyansh Tulsyan
 * @version 1.0
 * @see Card
 *
 */
@SuppressWarnings("serial")
public class BigTwoCard extends Card {

	// *********** constructor ***************
	/**
	 * A constructor for building a card with the specified suit and rank.
	 * suit is an integer between 0 to 3, and rank is an integer between 0 to 12.
	 * 
	 * @param suit An integer representing a suit with a value between 0 and 3
	 * @param rank An integer representing a rank with a value between 0 and 12
	 * 
	 */
	
	public BigTwoCard(int suit, int rank) {
		super(suit,rank);
	}
	// ***************************************
	
	// ************ Public Methods *************
	/**
	 * A method for comparing the order of this BigTwo card with the specified BigTwo card.
	 * Returns a negative integer, zero, or a positive integer when this card is 
	 * less than, equal to, or greater than the specified card.  
	 * 
	 * @param card The card that need to be compared with this card.
	 * @return A negative integer, zero, or a positive integer if this card is 
	 *         less than, equal to, or greater than the specified card.
	 *         
	 */
	
	@Override
	public int compareTo(Card card) {
		if (card == null) {
			throw new NullPointerException("card");
		}
		int rankComparison = Integer.compare(rankStrength(this.rank), rankStrength(card.rank));
		return rankComparison != 0 ? rankComparison : Integer.compare(this.suit, card.suit);
	}

	private static int rankStrength(int rank) {
		return (rank + 11) % 13;
	}
}
