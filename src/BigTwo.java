import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * The BigTwo class implements the CardGame interface and is used to model a Big Two card 
 * game. It has private instance variables for storing the number of players, a deck of cards, 
 * a list of players, a list of hands played on the table, an index of the current player, and 
 * a user interface. Below is a detailed description for the BigTwo class. 
 * 
 * @author Divyansh Tulsyan
 * @version 2.0
 * @see CardGame
 * 
 */
public final class BigTwo implements CardGame{
	private static final int PLAYER_COUNT = 4;
	private static final int CARDS_PER_PLAYER = 13;
	private static final BigTwoCard OPENING_CARD = new BigTwoCard(0, 2);
	
	
	// ************** Private instance variables **************
	
	private final int numOfPlayers = PLAYER_COUNT;

	private Deck deck;           // a deck of cards.

	private ArrayList<CardGamePlayer> playerList;     // a list of players.

	private ArrayList<Hand> handsOnTable;             // a list of hands played on the table.

	private int currentPlayerIdx;                           // an integer specifying the index of the current player.

	private BigTwoGUI gui;       // a BigTwoGUI object for providing the user interface.
	
	private boolean started;       // A boolean object to keep track of the start and stop of the game in multi-player sceniaro
	
	private BigTwoClient client;   // A BigTwoClient object that allows us to use BigTwoClient methods
	
	private boolean gameEnded;     // A boolean variable to keep track of when the game has ended or not.
	
	// ***************************************************
	
	
	// *************** Constructor ******************
	
	/**
	 * A constructor for creating a Big Two card game. It creates 4 
	 * players and add them to the player list; and creates a BigTwoGUI 
	 * object for providing the user interface.
	 *  
	 */
	public BigTwo() {
		handsOnTable = new ArrayList<Hand>();
		
		// Creating 4 players and adding them to the playList
		playerList = new ArrayList<CardGamePlayer>();
		for(int i = 0; i < PLAYER_COUNT; i++) {
			playerList.add(new CardGamePlayer());
			playerList.get(i).setName(null);  // setting each name null
		}
		// Creates BigTwoGUI object for providing user interface
		gui = new BigTwoGUI(this);
		client = new BigTwoClient(this, gui);
	}

	// ***************************************************
	
	//*************** public methods **********************
	
	/**
	 * A method for getting boolean variable that determines whether 
	 * the game has ended or not.
	 * 
	 * @return The boolean value
	 * 
	 */
	public boolean getGameEnded() {
		return gameEnded;
	}
	
	/**
	 * A method to set the value of gameEnded with required boolean value
	 * 
	 * @param gameEnded  A boolean value depicting end of game
	 * 
	 */
	public void setGameEnded(boolean gameEnded) {
		this.gameEnded = gameEnded;
	}
	/**
	 * A method for getting boolean variable that determines whether 
	 * the game has started or not.
	 * 
	 * @return The boolean value
	 * 
	 */
	public boolean getStarted() {
		return started;
	}
	
	/**
	 * A method to set the value of Started with required boolean value
	 * 
	 * @param started   A boolean value depicting start of game
	 * 
	 */
	public void setStarted(boolean started) {
		this.started = started;
	}
	
	/**
	 * A method to set the the BigTwoClient for the player
	 * 
	 * @param client   A BigTwoClient object that initializes itself for each player
	 * 
	 */
	public void setClient(BigTwoClient client) {
		if (client == null) {
			throw new IllegalArgumentException("client must not be null");
		}
		this.client = client;
	}

	/**
	 * A public getter to get the value of the client so that it can be used.
	 * 
	 * @return  It returns the BigTwoClient object
	 * 
	 */
	public BigTwoClient getClient() {
		return client;
	}
	
	/**
	 * A method for retrieving the list of players.
	 * 
	 * @return list of players
	 * 
	 */
	@Override
	public int getNumOfPlayers() {

		return numOfPlayers;
	}

	/**
	 * A method for retrieving the deck of cards being used.
	 * 
	 * @return deck of cards that are going to be used
	 * 
	 */
	@Override
	public Deck getDeck() {
		return deck;
	}

	/**
	 * A method for getting a list of players
	 * 
	 * @return An Array-list containing list of players playing the game
	 * 
	 */
	@Override
	public ArrayList<CardGamePlayer> getPlayerList() {

		return playerList;
	}

	/**
	 * A method for retrieving the list of hands played on the table.
	 * 
	 * @return An Array-list of hands played on the table
	 * 
	 */
	@Override
	public ArrayList<Hand> getHandsOnTable() {

		return handsOnTable;
	}
	
	/**
	 * A method for retrieving the index of the current player.
	 * 
	 * @return index of current player
	 * 
	 */
	@Override
	public int getCurrentPlayerIdx() {
		return this.currentPlayerIdx;
	}

	/**
	 * A method for starting/restarting the game with a given 
	 * shuffled deck of cards
	 * 
	 * @param deck The deck of cards that is being used for the game
	 * 
	 */
	@Override
	public void start(Deck deck) {
		if (deck == null || deck.size() != PLAYER_COUNT * CARDS_PER_PLAYER) {
			throw new IllegalArgumentException("A Big Two game requires a complete 52-card deck");
		}
		this.deck = deck;
		
		// removing all the cards from the table
		handsOnTable.clear();
		
		//remove all the cards from the players 
		for(int i = 0; i < numOfPlayers; i++) {
			this.getPlayerList().get(i).removeAllCards();
		}
		
		// Distribute the cards to the player. The method would be by 
		// distributing it to one player then to another and so on
		int cardsTracker = 0;
		for (int i = 0; i < CARDS_PER_PLAYER; i++) {
			for (int j = 0; j < PLAYER_COUNT; j++) {
				this.getPlayerList().get(j).addCard(deck.getCard(cardsTracker));
				cardsTracker++;
			}
		}
		
		// Finding three of diamonds to start the game
		// to do this we need to sort the cards and then find 3 of diamonds
		
		for (int i = 0; i < PLAYER_COUNT; i++) {
			this.playerList.get(i).sortCardsInHand();
		}
		
		// searching for 3 of diamonds
		
		// check that suit index  = 0 and cards index  = 2
		for (int i = 0; i < PLAYER_COUNT; i++) {
			if (this.playerList.get(i).getCardsInHand().contains(OPENING_CARD)) {
				currentPlayerIdx = i;
				break;
			}
		}
		
		// setting the activePlayer of the BigTwoGUI instance to the playerID (The local player)
		gui.setActivePlayer(client.getPlayerID());
		
		gui.printMsg("All players are ready. Game on!");

		started = true;
		
		setGameEnded(false);
		
	}

	/**
	 * A method for making a move by a player with the specified index using the 
	 * cards specified by the list of indices.
	 * 
	 * @param playerIdx   The ID of the player that is making the move
	 * @param cardIdx     A list informing about what cards are selected by the player
	 * 
	 */
	@Override
	public void makeMove(int playerIdx, int[] cardIdx) {
		CardGameMessage message = new CardGameMessage(CardGameMessage.MOVE, -1, cardIdx);
		client.sendMessage(message);
	}


	/**
	 * A method for checking a move made by a player.
	 * 
	 * @param playerIdx  The ID of the player that is making the move
	 * @param cardIdx    A list informing about what cards are selected by the player
	 * 
	 */
	@Override
	public synchronized void checkMove(int playerIdx, int[] cardIdx) {
		if (playerIdx < 0 || playerIdx >= PLAYER_COUNT) {
			gui.printMsg("Ignored a move with an invalid player index: " + playerIdx);
			return;
		}
		if (!started || gameEnded || playerIdx != currentPlayerIdx) {
			gui.printMsg("Not a legal move!!!");
			return;
		}
	
		boolean isError = false;  // to change currentIdx according to the error checking
		CardList cardList = new CardList();  // will store cards
		Hand handOfPlayer;   // will store composed hand of the player
		boolean startingMove = false;   // to keep track of which is the starting move
		
		// Move that will start the game
		if (this.playerList.get(playerIdx).getCardsInHand().contains(OPENING_CARD)) {
			startingMove = true; // making it true so that game starts
		}
		
		// logic for first move
		if (startingMove) {
			
			// check for null input .i.e. cardIdx ==  null
			if (cardIdx == null) {
				gui.printMsg("Please select a hand to play");
				isError = true;
			}
			else {
				// composing hand
				cardList = playerList.get(playerIdx).play(cardIdx);
				handOfPlayer = composeHand(this.getPlayerList().get(playerIdx),cardList);
				
				// card should make a formation in the start
				if (handOfPlayer == null) {
					gui.printMsg("Please select a hand to play");
					isError = true;
				}		
				// if the card contains 3 of diamonds
				else if (handOfPlayer.contains(OPENING_CARD)) {
					
					handsOnTable.add(handOfPlayer);   // add the legal hand to the table.
					playerList.get(currentPlayerIdx).removeCards(handOfPlayer);	 // Remove the card from the hand of the playing player
					gui.printMsg("{" + handOfPlayer.getType() + "} ");
					gui.printMsg(handOfPlayer.toString());
					startingMove = false;  // starting move done
				}				
				else if (cardList == null || !handOfPlayer.contains(OPENING_CARD)){
					gui.printMsg("Please select a hand to play");
					isError = true;
				}
			}
		}
		// Following conditions are: if not the starting move!
		// Check if the turn has came to the player playing last (i.e everyone else has passed their turn)
		// or in more simple words a player can only pass if it that player wasn't the last 
		// guy who played the hand
		else if (handsOnTable.get(handsOnTable.size() - 1).getPlayer() != playerList.get(playerIdx) && cardIdx == null) {
			gui.printMsg("{Pass}");
		}
		// if the last player who played a valid hand tries to pass
		// it is illegal
		else if ((handsOnTable.get(handsOnTable.size() - 1).getPlayer() == playerList.get(playerIdx)) && cardIdx == null) {
			gui.printMsg("Not a legal move!!!");
			isError = true;
		}
		else {
			// and can form cards
			cardList = playerList.get(playerIdx).play(cardIdx);
			handOfPlayer = composeHand(this.getPlayerList().get(playerIdx),cardList);


			// Check if it formed a legal hand, matches the size of the previous hand and if it beats the previous hand.
			while (handOfPlayer == null || (handOfPlayer.size() != handsOnTable.get(handsOnTable.size() - 1).size()) || !handOfPlayer.beats(handsOnTable.get(handsOnTable.size() - 1)) ) {
				// To check if the player playing is trying to pass, set the active player to the next player if so.
				if (handOfPlayer != null) {
					if ((handOfPlayer.size() != handsOnTable.get(handsOnTable.size() - 1).size()) && (handsOnTable.get(handsOnTable.size() - 1).getPlayer() == playerList.get(playerIdx))) {
						break;
					}
					// Check if the turn has came to the player playing last (i.e everyone else has passed their turn)
					if (handsOnTable.get(handsOnTable.size() - 1).getPlayer() == playerList.get(playerIdx)) {
						break;
					}
				}

				gui.printMsg("Not a legal move!!!");
				isError = true;
				if (isError == true) {
					break;
				}
			}
			
			if (cardList != null && handOfPlayer != null && isError == false) {
				// Once the error checking is done, add the legal hand to the table.
				// Remove the card from the hand of the playing player
				handsOnTable.add(handOfPlayer);
				playerList.get(currentPlayerIdx).removeCards(handOfPlayer);	
				gui.printMsg("{" + handOfPlayer.getType() + "} ");
				gui.printMsg(handOfPlayer.toString());
			}
		}
			
		if (isError == false && gameEnded == false) {
			// Add the index to set the next active player
			currentPlayerIdx = (playerIdx + 1) % PLAYER_COUNT;
			gui.repaint();
		}
		
		// checking if the game has ended or not.
		if(endOfGame()) {
			System.out.println();
			gui.printMsg("Game ends");
			String dialogMessage = "";
			for (int i = 0; i < PLAYER_COUNT; i++ ) {
				if (this.getPlayerList().get(i).getCardsInHand().isEmpty()) {
					gui.printMsg(this.getPlayerList().get(i).getName() + " wins the game.");
					dialogMessage += (this.getPlayerList().get(i).getName() + " wins the game.\n");
				}
				// printing cards remaining
				else {
					gui.printMsg(this.getPlayerList().get(i).getName() + " has " + this.getPlayerList().get(i).getNumOfCards() + " cards in hand.");

					dialogMessage += (this.getPlayerList().get(i).getName() + " has " + this.getPlayerList().get(i).getNumOfCards() + " cards in hand.\n");

				}
			}


			JOptionPane.showMessageDialog(new JFrame(), dialogMessage);
			CardGameMessage readyMessage = new CardGameMessage(CardGameMessage.READY, -1, null);
			client.sendMessage(readyMessage);
			setGameEnded(true);
		}
	}

	/**
	 * A method that checks for end of game.
	 * 
	 * @return true if the game ends or else it returns false
	 * 
	 */
	@Override
	public boolean endOfGame() {

		// A game would end when any one of the player has no card left
		// so we can check if the cards in hand are empty or not for each player
		
		for(int i = 0; i < PLAYER_COUNT; i ++) {
			if(this.getPlayerList().get(i).getCardsInHand().isEmpty()) {
				return true;
			}
		}
		
		return false;
	}
	
	
	// ***************************************************
	
	
	// ****************** Static methods ******************
	
	/**
	 * A method for returning a valid hand from the specified list of cards of the player. 
	 * Returns null is no valid hand can be composed from the specified list of cards.
	 * 
	 * @param player a player in the Big Two game
	 * @param cards cards is a CardList to check for a valid hand
	 * @return returns null if no valid hand is composed or else returns valid hand of cards
	 * 
	 */
	public static Hand composeHand(CardGamePlayer player, CardList cards) {
		return HandFactory.create(player, cards);
	}
	
	/**
	 * A method that just creates a BigTwo object called game.
	 * 
	 * @param args is not used
	 */
	public static void main(String[] args) {
		if (args.length > 0 && !args[0].trim().isEmpty()) {
			System.setProperty("bigtwo.playerName", args[0].trim());
		}
		BigTwo game = new BigTwo();  // create a Big Two card game

	} // main
	
	// ***********************************************************************************
	
	
	// ********************** Stay Safe and Stay Happy ************************************
}
