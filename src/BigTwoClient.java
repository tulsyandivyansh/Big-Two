import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;

import javax.swing.JOptionPane;

/**
 * The BigTwoClient class implements the NetworkGame interface. It is used to model a Big 
 * Two game client that is responsible for establishing a connection and communicating with 
 * the Big Two game server.
 * 
 * @version 2.0
 * @see NetworkGame
 *
 */

public final class BigTwoClient implements NetworkGame {
	private static final String DEFAULT_SERVER_ADDRESS = "127.0.0.1";
	private static final int DEFAULT_SERVER_PORT = 2396;
	
	// ************** Private instance variables **************
	
	private final BigTwo game;
	private final BigTwoGUI gui;
	private Socket sock;             //  a socket connection to the game server.
	private ObjectOutputStream oos;  // an ObjectOutputStream for sending messages to the server. 
	private int playerID;            // an integer specifying the playerID (i.e., index) of the local player. 
	private String playerName;       // a string specifying the name of the local player. 
	private String serverIP;         // a string specifying the IP address of the game server. 
	private int serverPort;          // an integer specifying the TCP port of the game server.
	private boolean connected;       // A boolean value to determine connection of client
	
	// ***************************************************
	
	
	// *************** Constructor ***********************
	

	
	/**
	 * A constructor for creating a Big Two client. The first parameter is a reference to
	 * a BigTwo object associated with this client and the second parameter is a reference
	 * to a BigTwoGUI object associated the BigTwo object.
	 * 
	 */
	
	public BigTwoClient(BigTwo game, BigTwoGUI gui) {

		this.game = game;
		this.gui = gui;
		
		game.setStarted(false);
	
		gui.repaint();
		
		playerName = JOptionPane.showInputDialog(null,"Enter Name: ");

		
		if (playerName == null) {
			System.exit(0);
		}
		
		while(playerName.isEmpty()) {
			playerName = JOptionPane.showInputDialog("Enter A valid Name: ");
			if (playerName == null) {
			System.exit(0);
			}
		}

		setPlayerName(playerName);    // having a Dialogue box to input name
		setServerIP(DEFAULT_SERVER_ADDRESS);
		setServerPort(DEFAULT_SERVER_PORT);
		connect();           // making connection

		
	}
	
	// ***************************************************
	
	//*************** public methods **********************
	

	/**
	 * A method for getting connection status of the BigTwoClient
	 * 
	 * @return a boolean value depicting connected
	 * 
	 */
	
	public boolean getConnected() {
		return connected;
	}
	
	/**
	 * A method for getting the playerID (i.e., index) of the local player.
	 * 
	 * @return playerID of the Local player
	 * 
	 */
	@Override
	public int getPlayerID() {

		return playerID;
	}
	
	/**
	 * A method for setting the playerID (i.e., index) of the local player. 
	 * This method will be called from the parseMessage() method when a message 
	 * of the type PLAYER_LIST is received from the game server.
	 * 
	 * @param playerID  ID of the local player
	 * 
	 */
	@Override
	public void setPlayerID(int playerID) {

		this.playerID = playerID;
	}
	
	/**
	 * A method for getting the name of the local player.
	 * 
	 * @return It returns playerName
	 * 
	 */
	@Override
	public String getPlayerName() {
	
		return playerName;
	}
	
	/**
	 * A method for setting the name of the local player.
	 * 
	 * @param playerName  Name of the local player
	 * 
	 */
	@Override
	public void setPlayerName(String playerName) {
		if (playerName == null || playerName.trim().isEmpty()) {
			throw new IllegalArgumentException("Player name must not be blank");
		}
		this.playerName = playerName.trim();
	}
	
	/**
	 * A method for getting the IP address of the game server.
	 * 
	 * @return It returns serverIP
	 * 
	 */
	@Override
	public String getServerIP() {

		return serverIP;
	}
	
	/**
	 * A method for setting the IP address of the game server.
	 * 
	 * @param serverIP The IP address of the game server
	 * 
	 */
	@Override
	public void setServerIP(String serverIP) {
		if (serverIP == null || serverIP.trim().isEmpty()) {
			throw new IllegalArgumentException("Server address must not be blank");
		}
		this.serverIP = serverIP.trim();
	}
	
	/**
	 * A method for getting the TCP port of the game server.
	 * 
	 * @return It returns serverPort
	 * 
	 */
	@Override
	public int getServerPort() {

		return serverPort;
	}
	
	/**
	 * A method for setting the TCP port of the game server.
	 * 
	 * @param serverPort  The TCP port of the server
	 * 
	 */
	@Override
	public void setServerPort(int serverPort) {
		if (serverPort < 1 || serverPort > 65535) {
			throw new IllegalArgumentException("Server port must be between 1 and 65535");
		}
		this.serverPort = serverPort;
	}
	
	/**
	 * A method for making a socket connection with the game server. 
	 * Upon successful connection, It will (i) create an ObjectOutputStream 
	 * for sending messages to the game server; (ii) create a thread for receiving
	 * messages from the game server.
	 * 
	 */
	@Override
	public void connect() {
		closeConnection();
		try {
			sock = new Socket(getServerIP(), getServerPort());
			oos = new ObjectOutputStream(sock.getOutputStream());
			Thread readerThread = new Thread(new ServerHandler(), "big-two-server-listener");
			readerThread.setDaemon(true);
			readerThread.start();
		} catch (IOException e) {
			connected = false;
			gui.printMsg("Unable to connect to " + getServerIP() + ":" + getServerPort()
					+ ". Check that the server is running.");
			closeConnection();
		}
	}
	
	
	/**
	 * A method for parsing the messages received from the game server. 
	 * This method should be called from the thread responsible for receiving 
	 * messages from the game server. Based on the message type, different actions 
	 * will be carried out.
	 * 
	 */
	@Override
	public void parseMessage(GameMessage message) {
		if (message == null) {
			return;
		}
		switch (message.getType()) {
		
		case CardGameMessage.PLAYER_LIST:
			
			connected = true;
			setPlayerID(message.getPlayerID());
			String[] nameOfPlayers = (String[]) message.getData();
			for(int i = 0; i < nameOfPlayers.length; i ++) {
				game.getPlayerList().get(i).setName(nameOfPlayers[i]);
			}
			CardGameMessage join = new CardGameMessage(CardGameMessage.JOIN, -1, getPlayerName());
			sendMessage(join);
			break;
			
		case CardGameMessage.JOIN:
			
			String data = (String) message.getData();
			game.getPlayerList().get(message.getPlayerID()).setName(data);
			if(playerID == message.getPlayerID()) {
				CardGameMessage ready = new CardGameMessage(CardGameMessage.READY, -1, null);
				sendMessage(ready);
			}
			gui.printMsg(game.getPlayerList().get(message.getPlayerID()).getName() + " joins the game.");
			gui.repaint();
			break;
			
		case CardGameMessage.FULL:
			
			connected = false;
			gui.printMsg("The server is full and you cannot join the game.");
			break;
			
		case CardGameMessage.QUIT:
			
			gui.printMsg("Player "+ game.getPlayerList().get(message.getPlayerID()).getName() + " has left the game");
			game.getPlayerList().get(message.getPlayerID()).setName("");
			CardGameMessage readyMessage = new CardGameMessage(CardGameMessage.READY, -1, null);
			sendMessage(readyMessage);
			game.setStarted(false);
			gui.repaint();
			break;
			
		case CardGameMessage.READY:
			
			gui.printMsg(game.getPlayerList().get(message.getPlayerID()).getName() + " is ready.");
			break;
		
		case CardGameMessage.START:
			
			BigTwoDeck deck = (BigTwoDeck) message.getData();
			if (game.getGameEnded() == false) {
				game.start(deck);
				gui.repaint();
			}
			break;
			
		case CardGameMessage.MOVE:
			
			int[] cardIdx = (int[]) message.getData();
			game.checkMove(message.getPlayerID(), cardIdx);
			break;
			
		case CardGameMessage.MSG:
			String chat = (String) message.getData();
			gui.printChatMsg(chat);
			break;
		default:
			gui.printMsg("Ignored an unknown server message: " + message.getType());
			break;
		}

	}
	
	/**
	 * A method for sending the specified message to the game server. 
	 * This method should be called whenever the client wants to communicate 
	 * with the game server or other clients.
	 * 
	 * @param message message that needs to be sent
	 * 
	 */
	@Override
	public synchronized void sendMessage(GameMessage message) {
		if (message == null) {
			throw new IllegalArgumentException("message must not be null");
		}
		if (oos == null) {
			gui.printMsg("Message not sent: there is no server connection.");
			return;
		}
		try {
			oos.writeObject(message);
			oos.flush();
		} catch (IOException e) {
			connected = false;
			gui.printMsg("Message not sent because the server connection was lost.");
			closeConnection();
		}
		
	}
	
	/**
	 * An inner class that implements the Runnable interface. 
	 * Upon receiving a message, the parseMessage() method from the NetworkGame 
	 * interface will be called to parse the messages accordingly.
	 * 
	 * @version 2.0
	 * @see Runnable
	 *
	 */
	
	class ServerHandler implements Runnable {
		
		/**
		 * A method to create a thread with instance of this class
		 * as its job in the connect method from the network game
		 * interface for recieving messages from the game server.
		 * 
		 */
		@Override
		public void run() {
			
			CardGameMessage message;
			try (ObjectInputStream input = new ObjectInputStream(sock.getInputStream())) {
				
				while((message = (CardGameMessage) input.readObject()) != null) {
					parseMessage(message);
				}
				
			} catch(SocketException ex) {
				connected = false;
				game.setStarted(false);
				gui.printMsg("Your connection to the server is lost. Please click connect"
						+ " from the menu button.");
				gui.disable();
			} catch (Exception ex) {
				connected = false;
				gui.printMsg("The connection closed unexpectedly: " + ex.getMessage());
			} finally {
				closeConnection();
			}
		}
	}

	private synchronized void closeConnection() {
		connected = false;
		if (oos != null) {
			try {
				oos.close();
			} catch (IOException ignored) {
				// The connection is already unusable.
			}
			oos = null;
		}
		if (sock != null) {
			try {
				sock.close();
			} catch (IOException ignored) {
				// The connection is already unusable.
			}
			sock = null;
		}
	}
	
	// ***********************************************************************************
	
	
	// ********************** Stay Safe and Stay Happy ************************************
}
