
/**
 * Base value for a group of cards submitted by one player.
 * Concrete subclasses identify the hand category while {@link HandRules}
 * owns validation and comparison policy.
 *
 * @author Divyansh Tulsyan
 */

@SuppressWarnings("serial")
public abstract class Hand extends CardList {
	private final CardGamePlayer player;
	
	/**
	 * A constructor for building a hand with the specified player and list of cards.
	 * 
	 * @param player The player who is playing this hand
	 * @param cards  The cards that will make the hand
	 * 
	 */
	
	protected Hand(CardGamePlayer player, CardList cards) {
		super(requireCards(cards));
		if (player == null || cards == null) {
			throw new IllegalArgumentException("A hand requires a player and a card list");
		}
		this.player = player;
	}

	private static CardList requireCards(CardList cards) {
		if (cards == null) {
			throw new IllegalArgumentException("A hand requires a card list");
		}
		return cards;
	}
	
	/**
	 * A method for retrieving the player of this hand. 
	 * 
	 * @return Player who plays this hand.
	 */
	
	public CardGamePlayer getPlayer() {
		return player;
	}
	
	/**
	 * A method for retrieving the top card of this hand.
	 * 
	 * @return The top card of the hand
	 * 
	 */
	
	public Card getTopCard() {
		return HandRules.findTopCard(this);
	}
	
	
	/**
	 * A method for checking if this hand beats a specified hand.
	 * This method would be overridden by its subclasses. 
	 * 
	 * @param hand  A hand that is used for comparison
	 * @return  It returns true if beaten else it returns false
	 * 
	 */
	
	public boolean beats(Hand hand) {
		return HandRules.beats(this, hand);
	}
	
	/**
	 * A method for checking if this is a valid hand.
	 * 
	 * @return It returns true if the Hand is valid, otherwise false
	 * 
	 */
	
	public abstract boolean isValid();

	/** Returns the typed category used by the rule engine. */
	public abstract HandType getCategory();
	
	/**
	 * A method for returning a string specifying the type of this hand. 
	 * 
	 * @return A string dictating the type of the given hand
	 * 
	 */
	public abstract String getType();
}
