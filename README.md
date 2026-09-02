# Big Two

Big Two is a four-player multiplayer card game built with Java Swing. It includes a network server, a desktop client, a modern card-table interface, in-game chat, move validation, and a centralized rules engine for every supported hand.

## Highlights

- Four-player matches over a client-server connection
- Resizable Swing interface with a modern card-table design
- Active-turn indicators and remaining-card counts
- Interactive card selection with clear visual feedback
- Game log, connection status, and table chat
- Centralized hand validation and comparison rules
- Automated regression tests with no external dependencies
- Generated Javadoc API documentation

## Requirements

- JDK 8 or newer
- `make` for the provided convenience commands
- Four client instances for a complete match

Check that Java is available:

```sh
java -version
javac -version
```

## Build

From the project directory, run:

```sh
make compile
```

This compiles the Java source files into `bin/`.

If `make` is unavailable, use:

```sh
mkdir -p bin
javac -Xlint:all -d bin src/*.java
```

## Run the game

The server must be running before clients connect.

### 1. Start the server

```sh
java -cp bin BigTwoServer
```

The default server port is `2396`. A different port can be supplied as the first argument:

```sh
java -cp bin BigTwoServer 3000
```

### 2. Start the clients

Open another terminal and run:

```sh
java -cp bin BigTwo
```

Enter a player name when prompted. Start four client instances to fill the table. Once every connected player is ready, the server shuffles the deck and begins the match.

The client connects to `127.0.0.1:2396` by default. These defaults are defined in `BigTwoClient` and can be changed when configuring the project for another host or port.

## How to play

Big Two uses a standard 52-card deck. Each of the four players receives 13 cards.

The rank order from lowest to highest is:

```text
3 4 5 6 7 8 9 10 J Q K A 2
```

The suit order from lowest to highest is:

```text
Diamonds Clubs Hearts Spades
```

The player holding the Three of Diamonds takes the first turn and must include that card in the opening hand. Afterward, each player must either:

- Play a legal hand that defeats the current hand on the table, or
- Pass and allow the next player to act.

When play returns to the player who submitted the most recent hand, that player gains control of the table and may lead with any legal combination. The first player to use all their cards wins.

## Supported hands

| Hand | Description |
| --- | --- |
| Single | One card |
| Pair | Two cards with the same rank |
| Triple | Three cards with the same rank |
| Straight | Five cards with consecutive Big Two ranks |
| Flush | Five cards from the same suit |
| Full house | A pair and a triple |
| Quad | Four cards with the same rank plus one additional card |
| Straight flush | Five consecutive cards from the same suit |

Five-card hands increase in strength from Straight through Straight Flush. Hands in the same category are compared according to their relevant rank, top card, or suit.

## Interface

The main window is divided into two areas:

- **Card table:** Shows all four players, avatars, card counts, the active turn, the local hand, and the latest cards played.
- **Sidebar:** Shows connection status, game messages, table chat, and the chat composer.

During your turn, click cards to select them. Selected cards rise and receive a gold border. Use **Play selected** to submit them or **Pass turn** to pass. These controls are disabled while another player is acting.

## Test

Run the regression suite with:

```sh
make test
```

The suite verifies:

- Card equality and Big Two ordering
- All 52 deck entries
- Every legal hand category
- Invalid hand rejection
- The complete five-card comparison hierarchy

The tests use a dependency-free Java harness and currently contain 91 assertions.

## Documentation

Generate API documentation with:

```sh
make docs
```

Then open `doc/index.html` in a browser.

## Project structure

```text
.
├── src/
│   ├── BigTwo.java             Match state and turn progression
│   ├── BigTwoGUI.java          Swing user interface
│   ├── BigTwoClient.java       Client connection and message handling
│   ├── BigTwoServer.java       Big Two server entry point
│   ├── CardGameServer.java     Shared multiplayer server
│   ├── HandFactory.java        Legal-hand construction
│   ├── HandRules.java          Hand validation and comparison
│   ├── HandType.java           Type-safe hand categories
│   ├── cards/                  Card artwork
│   └── avatars/                Player and application artwork
├── test/
│   └── BigTwoRulesTest.java    Core regression suite
├── bin/                        Compiled classes and runtime assets
├── doc/                        Generated API documentation
└── Makefile                    Build, test, documentation, and cleanup tasks
```

## Architecture

The project separates game behavior into focused layers:

1. `BigTwo` coordinates players, turns, moves, and match completion.
2. `HandFactory` converts selected cards into the most specific legal hand.
3. `HandRules` validates hands and performs all comparisons.
4. `BigTwoClient` exchanges serializable protocol messages with the server.
5. `BigTwoGUI` renders game state and forwards player actions to the game.

The network protocol continues to use the message identifiers in `CardGameMessage`, allowing compatible clients and servers to exchange player, move, chat, ready, and lifecycle events.

## Troubleshooting

### The client cannot connect

- Confirm that `BigTwoServer` is running first.
- Confirm that the client and server use the same port.
- Check whether a firewall is blocking the selected TCP port.
- Remember that the default address, `127.0.0.1`, only connects to a server on the same computer.

### Card images do not appear

Run the application from the project directory. The UI searches for assets on the classpath and under both `src/` and `bin/`.

### The game has not started

Big Two requires exactly four connected and ready players. Check the server window and client game logs for connection or readiness messages.

## Useful commands

```sh
make compile   # Compile the application
make test      # Compile and run regression tests
make docs      # Regenerate Javadoc
make clean     # Remove compiled .class files
```
