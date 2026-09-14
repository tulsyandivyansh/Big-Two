import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

/**
 * Swing presentation layer for Big Two. Game state remains in {@link BigTwo};
 * this class renders that state and translates local actions into game moves.
 *
 * @author Divyansh Tulsyan
 */
public final class BigTwoGUI implements CardGameUI {
    private static final Color NAVY = new Color(14, 24, 38);
    private static final Color PANEL = new Color(24, 38, 55);
    private static final Color PANEL_ALT = new Color(31, 48, 68);
    private static final Color FELT = new Color(16, 93, 73);
    private static final Color FELT_DARK = new Color(8, 54, 48);
    private static final Color TEXT = new Color(235, 241, 247);
    private static final Color MUTED_TEXT = new Color(159, 178, 197);
    private static final Color ACCENT = new Color(64, 190, 154);
    private static final Color GOLD = new Color(244, 190, 74);
    private static final Color DANGER = new Color(220, 84, 91);
    private static final Font BODY_FONT = new Font(Font.SANS_SERIF, Font.PLAIN, 14);
    private static final Font SMALL_FONT = new Font(Font.SANS_SERIF, Font.PLAIN, 12);
    private static final Font HEADING_FONT = new Font(Font.SANS_SERIF, Font.BOLD, 15);
    private static final int CARD_WIDTH = 72;
    private static final int CARD_HEIGHT = 99;
    private static final int CARD_OVERLAP = 34;
    private static final int PLAYER_COUNT = 4;

    private final BigTwo game;
    private final JFrame frame;
    private final JPanel boardHost;
    private final JTextArea messageArea;
    private final JTextArea chatArea;
    private final JTextField chatInput;
    private final JButton playButton;
    private final JButton passButton;
    private final JLabel connectionLabel;
    private final Image[][] cardFaces = new Image[4][13];
    private final Image[] avatars = new Image[PLAYER_COUNT];

    private Image cardBack;
    private Image emptyAvatar;
    private boolean[] selected = new boolean[0];
    private int activePlayer = -1;

    public BigTwoGUI(BigTwo game) {
        if (game == null) {
            throw new IllegalArgumentException("game must not be null");
        }
        this.game = game;
        loadImages();

        frame = new JFrame("Big Two");
        boardHost = new JPanel(new BorderLayout());
        messageArea = createTextArea("Welcome to Big Two.\nWaiting for players to connect…\n");
        chatArea = createTextArea("Team chat is ready.\n");
        chatInput = new JTextField();
        playButton = createButton("Play selected", ACCENT, Color.WHITE);
        passButton = createButton("Pass turn", PANEL_ALT, TEXT);
        connectionLabel = new JLabel("●  Offline");

        configureFrame();
        wireActions();
        refreshView();
    }

    @Override
    public void setActivePlayer(int activePlayer) {
        this.activePlayer = activePlayer >= 0 && activePlayer < PLAYER_COUNT ? activePlayer : -1;
        resetSelected();
    }

    @Override
    public void repaint() {
        if (SwingUtilities.isEventDispatchThread()) {
            refreshView();
        } else {
            SwingUtilities.invokeLater(this::refreshView);
        }
    }

    @Override
    public void printMsg(String message) {
        appendLine(messageArea, message);
    }

    @Override
    public void clearMsgArea() {
        messageArea.setText("");
    }

    public void printChatMsg(String message) {
        appendLine(chatArea, message);
    }

    public void clearChatMsgArea() {
        chatArea.setText("");
    }

    @Override
    public void reset() {
        resetSelected();
        clearMsgArea();
        enable();
        repaint();
    }

    @Override
    public void enable() {
        playButton.setEnabled(true);
        passButton.setEnabled(true);
    }

    @Override
    public void disable() {
        playButton.setEnabled(false);
        passButton.setEnabled(false);
    }

    @Override
    public void promptActivePlayer() {
        printMsg("Your turn — choose a legal hand or pass.");
    }

    private void configureFrame() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(1050, 720));
        frame.setSize(new Dimension(1320, 860));
        frame.setLocationRelativeTo(null);
        Image logo = loadImage("avatars/LOGOICON.png");
        if (logo != null) {
            frame.setIconImage(logo);
        }
        frame.setJMenuBar(createMenuBar());

        JPanel sidePanel = createSidePanel();
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, boardHost, sidePanel);
        splitPane.setResizeWeight(0.72);
        splitPane.setDividerLocation(900);
        splitPane.setDividerSize(5);
        splitPane.setBorder(null);
        splitPane.setContinuousLayout(true);
        frame.setContentPane(splitPane);
        frame.setVisible(true);
    }

    private JMenuBar createMenuBar() {
        JMenuBar bar = new JMenuBar();
        JMenu gameMenu = new JMenu("Game");
        JMenuItem reconnect = new JMenuItem("Connect to server");
        reconnect.addActionListener(this::connectToServer);
        JMenuItem quit = new JMenuItem("Quit");
        quit.addActionListener(event -> frame.dispose());
        gameMenu.add(reconnect);
        gameMenu.addSeparator();
        gameMenu.add(quit);

        JMenu viewMenu = new JMenu("View");
        JMenuItem clearGameLog = new JMenuItem("Clear game log");
        clearGameLog.addActionListener(event -> clearMsgArea());
        JMenuItem clearChat = new JMenuItem("Clear chat");
        clearChat.addActionListener(event -> clearChatMsgArea());
        viewMenu.add(clearGameLog);
        viewMenu.add(clearChat);
        bar.add(gameMenu);
        bar.add(viewMenu);
        return bar;
    }

    private JPanel createSidePanel() {
        JPanel side = new JPanel(new BorderLayout(0, 12));
        side.setBackground(NAVY);
        side.setBorder(new EmptyBorder(16, 14, 14, 14));
        side.setMinimumSize(new Dimension(310, 600));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel title = new JLabel("BIG TWO");
        title.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 24));
        title.setForeground(TEXT);
        connectionLabel.setFont(SMALL_FONT);
        connectionLabel.setForeground(DANGER);
        header.add(title, BorderLayout.WEST);
        header.add(connectionLabel, BorderLayout.EAST);

        JPanel feeds = new JPanel();
        feeds.setOpaque(false);
        feeds.setLayout(new BoxLayout(feeds, BoxLayout.Y_AXIS));
        feeds.add(createFeedPanel("GAME LOG", messageArea, 230));
        feeds.add(Box.createVerticalStrut(12));
        feeds.add(createFeedPanel("TABLE CHAT", chatArea, 260));

        JPanel composer = new JPanel(new BorderLayout(8, 0));
        composer.setOpaque(false);
        composer.setBorder(new EmptyBorder(2, 0, 0, 0));
        styleTextField(chatInput);
        JButton send = createButton("Send", ACCENT, Color.WHITE);
        send.addActionListener(this::sendChatMessage);
        composer.add(chatInput, BorderLayout.CENTER);
        composer.add(send, BorderLayout.EAST);

        side.add(header, BorderLayout.NORTH);
        side.add(feeds, BorderLayout.CENTER);
        side.add(composer, BorderLayout.SOUTH);
        return side;
    }

    private JPanel createFeedPanel(String title, JTextArea area, int preferredHeight) {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setBackground(PANEL);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(48, 66, 84)),
                new EmptyBorder(10, 10, 10, 10)));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, preferredHeight));

        JLabel label = new JLabel(title);
        label.setFont(SMALL_FONT.deriveFont(Font.BOLD));
        label.setForeground(MUTED_TEXT);
        JScrollPane scroll = new JScrollPane(area);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(PANEL);
        panel.add(label, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private void wireActions() {
        playButton.addActionListener(event -> game.makeMove(activePlayer, selectedIndices()));
        passButton.addActionListener(event -> game.makeMove(activePlayer, null));
        chatInput.addActionListener(this::sendChatMessage);
    }

    private void refreshView() {
        if (boardHost == null) {
            return;
        }
        boolean connected = game.getClient() != null && game.getClient().getConnected();
        connectionLabel.setText(connected ? "●  Connected" : "●  Offline");
        connectionLabel.setForeground(connected ? ACCENT : DANGER);

        boolean localTurn = game.getStarted() && !game.getGameEnded()
                && activePlayer >= 0 && game.getCurrentPlayerIdx() == activePlayer;
        playButton.setEnabled(localTurn);
        passButton.setEnabled(localTurn);

        boardHost.removeAll();
        boardHost.add(createTableView(), BorderLayout.CENTER);
        boardHost.revalidate();
        boardHost.repaint();
    }

    private JPanel createTableView() {
        TableSurface table = new TableSurface();
        table.setLayout(new BorderLayout(0, 10));
        table.setBorder(new EmptyBorder(16, 18, 14, 18));

        JPanel playerRows = new JPanel();
        playerRows.setOpaque(false);
        playerRows.setLayout(new BoxLayout(playerRows, BoxLayout.Y_AXIS));
        for (int playerIndex = 0; playerIndex < PLAYER_COUNT; playerIndex++) {
            playerRows.add(createPlayerRow(playerIndex));
            playerRows.add(Box.createVerticalStrut(7));
        }
        playerRows.add(createTableHandRow());

        JScrollPane tableScroll = new JScrollPane(playerRows);
        tableScroll.setBorder(null);
        tableScroll.setOpaque(false);
        tableScroll.getViewport().setOpaque(false);
        tableScroll.getVerticalScrollBar().setUnitIncrement(14);

        JPanel actions = new JPanel(new BorderLayout());
        actions.setOpaque(false);
        actions.setBorder(new EmptyBorder(6, 0, 0, 0));
        JLabel hint = new JLabel(game.getStarted()
                ? (game.getCurrentPlayerIdx() == activePlayer ? "Your move" : currentPlayerName() + " is playing")
                : "Waiting for four ready players");
        hint.setFont(HEADING_FONT);
        hint.setForeground(game.getCurrentPlayerIdx() == activePlayer ? GOLD : TEXT);

        JPanel buttons = new JPanel();
        buttons.setOpaque(false);
        buttons.add(passButton);
        buttons.add(playButton);
        actions.add(hint, BorderLayout.WEST);
        actions.add(buttons, BorderLayout.EAST);

        table.add(tableScroll, BorderLayout.CENTER);
        table.add(actions, BorderLayout.SOUTH);
        return table;
    }

    private JPanel createPlayerRow(int playerIndex) {
        boolean current = game.getStarted() && game.getCurrentPlayerIdx() == playerIndex;
        JPanel row = new RoundedPanel(current ? new Color(27, 73, 70, 225) : new Color(16, 35, 47, 205));
        row.setLayout(new BorderLayout(12, 0));
        row.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(current ? GOLD : new Color(58, 87, 94), current ? 2 : 1),
                new EmptyBorder(8, 10, 7, 10)));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 128));
        row.setPreferredSize(new Dimension(760, 128));

        String name = playerName(playerIndex);
        JPanel identity = new JPanel(new BorderLayout(8, 0));
        identity.setOpaque(false);
        identity.setPreferredSize(new Dimension(155, 105));
        Image avatar = name.isEmpty() ? emptyAvatar : avatars[playerIndex];
        JLabel avatarLabel = new JLabel(icon(avatar, 64, 64));
        JLabel nameLabel = new JLabel(displayName(playerIndex, name));
        nameLabel.setFont(HEADING_FONT);
        nameLabel.setForeground(TEXT);
        JLabel countLabel = new JLabel(cardCountLabel(playerIndex));
        countLabel.setFont(SMALL_FONT);
        countLabel.setForeground(current ? GOLD : MUTED_TEXT);
        JPanel labels = new JPanel();
        labels.setOpaque(false);
        labels.setLayout(new BoxLayout(labels, BoxLayout.Y_AXIS));
        labels.add(nameLabel);
        labels.add(Box.createVerticalStrut(5));
        labels.add(countLabel);
        identity.add(avatarLabel, BorderLayout.WEST);
        identity.add(labels, BorderLayout.CENTER);

        row.add(identity, BorderLayout.WEST);
        row.add(createCardsPane(playerIndex), BorderLayout.CENTER);
        return row;
    }

    private JPanel createTableHandRow() {
        JPanel row = new RoundedPanel(new Color(7, 43, 38, 215));
        row.setLayout(new BorderLayout(14, 0));
        row.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(60, 125, 109)),
                new EmptyBorder(10, 12, 9, 12)));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 132));
        row.setPreferredSize(new Dimension(760, 132));

        JLabel label = new JLabel("TABLE");
        label.setFont(HEADING_FONT);
        label.setForeground(GOLD);
        label.setPreferredSize(new Dimension(90, 100));

        if (game.getHandsOnTable().isEmpty()) {
            JLabel empty = new JLabel("No hand has been played", SwingConstants.CENTER);
            empty.setFont(BODY_FONT);
            empty.setForeground(MUTED_TEXT);
            row.add(label, BorderLayout.WEST);
            row.add(empty, BorderLayout.CENTER);
            return row;
        }

        Hand hand = game.getHandsOnTable().get(game.getHandsOnTable().size() - 1);
        JLabel detail = new JLabel(hand.getType() + "  ·  " + safeName(hand.getPlayer().getName()));
        detail.setFont(SMALL_FONT.deriveFont(Font.BOLD));
        detail.setForeground(TEXT);
        JPanel cardStrip = new JPanel(new BorderLayout());
        cardStrip.setOpaque(false);
        cardStrip.add(detail, BorderLayout.NORTH);
        cardStrip.add(createHandPane(hand), BorderLayout.CENTER);
        row.add(label, BorderLayout.WEST);
        row.add(cardStrip, BorderLayout.CENTER);
        return row;
    }

    private JLayeredPane createCardsPane(int playerIndex) {
        CardList hand = game.getPlayerList().get(playerIndex).getCardsInHand();
        int width = Math.max(CARD_WIDTH, CARD_WIDTH + Math.max(0, hand.size() - 1) * CARD_OVERLAP);
        JLayeredPane pane = new JLayeredPane();
        pane.setOpaque(false);
        pane.setPreferredSize(new Dimension(width + 8, CARD_HEIGHT + 14));

        boolean showFaces = playerIndex == activePlayer || game.endOfGame();
        boolean selectable = showFaces && game.getStarted() && game.getCurrentPlayerIdx() == activePlayer;
        for (int i = 0; i < hand.size(); i++) {
            Card card = hand.getCard(i);
            Image image = showFaces ? cardFaces[card.getSuit()][card.getRank()] : cardBack;
            CardView view = new CardView(image, i, selectable);
            int y = i < selected.length && selected[i] ? 0 : 10;
            view.setBounds(i * CARD_OVERLAP, y, CARD_WIDTH, CARD_HEIGHT);
            pane.add(view, Integer.valueOf(i));
        }
        return pane;
    }

    private JLayeredPane createHandPane(CardList cards) {
        JLayeredPane pane = new JLayeredPane();
        pane.setOpaque(false);
        pane.setPreferredSize(new Dimension(CARD_WIDTH + (cards.size() - 1) * 42, CARD_HEIGHT));
        for (int i = 0; i < cards.size(); i++) {
            Card card = cards.getCard(i);
            CardView view = new CardView(cardFaces[card.getSuit()][card.getRank()], i, false);
            view.setBounds(i * 42, 0, CARD_WIDTH, CARD_HEIGHT);
            pane.add(view, Integer.valueOf(i));
        }
        return pane;
    }

    private void sendChatMessage(ActionEvent event) {
        String text = chatInput.getText().trim();
        if (text.isEmpty()) {
            return;
        }
        chatInput.setText("");
        game.getClient().sendMessage(new CardGameMessage(CardGameMessage.MSG, -1, text));
    }

    private void connectToServer(ActionEvent event) {
        if (game.getClient().getConnected()) {
            printMsg("Already connected to the server.");
        } else {
            game.getClient().connect();
        }
    }

    private int[] selectedIndices() {
        List<Integer> indices = new ArrayList<Integer>();
        for (int i = 0; i < selected.length; i++) {
            if (selected[i]) {
                indices.add(i);
            }
        }
        int[] result = new int[indices.size()];
        for (int i = 0; i < indices.size(); i++) {
            result[i] = indices.get(i);
        }
        return result;
    }

    private void resetSelected() {
        int size = activePlayer >= 0 && activePlayer < game.getPlayerList().size()
                ? game.getPlayerList().get(activePlayer).getNumOfCards() : 0;
        selected = new boolean[size];
    }

    private void loadImages() {
        String[] avatarNames = {"Ash.png", "Goku.png", "Joker.png", "Xmen.png"};
        for (int i = 0; i < avatarNames.length; i++) {
            avatars[i] = loadImage("avatars/" + avatarNames[i]);
        }
        emptyAvatar = loadImage("avatars/empty.png");
        for (int suit = 0; suit < 4; suit++) {
            for (int rank = 0; rank < 13; rank++) {
                cardFaces[suit][rank] = loadImage("cards/" + suit + "-" + rank + ".png");
            }
        }
        cardBack = loadImage("cards/backCard.png");
    }

    private Image loadImage(String relativePath) {
        URL resource = BigTwoGUI.class.getClassLoader().getResource(relativePath);
        if (resource != null) {
            return new ImageIcon(resource).getImage();
        }
        File sourceAsset = new File("src", relativePath);
        File compiledAsset = new File("bin", relativePath);
        File file = sourceAsset.isFile() ? sourceAsset : compiledAsset;
        return file.isFile() ? new ImageIcon(file.getPath()).getImage() : null;
    }

    private static JTextArea createTextArea(String initialText) {
        JTextArea area = new JTextArea(initialText);
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setFont(BODY_FONT);
        area.setForeground(TEXT);
        area.setBackground(PANEL);
        area.setCaretColor(TEXT);
        area.setBorder(new EmptyBorder(2, 2, 2, 2));
        return area;
    }

    private static void styleTextField(JTextField field) {
        field.setFont(BODY_FONT);
        field.setForeground(TEXT);
        field.setCaretColor(TEXT);
        field.setBackground(PANEL_ALT);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(66, 91, 115)),
                new EmptyBorder(8, 10, 8, 10)));
        field.setToolTipText("Type a message and press Enter");
    }

    private static JButton createButton(String text, Color background, Color foreground) {
        JButton button = new JButton(text);
        button.setFont(HEADING_FONT);
        button.setForeground(foreground);
        button.setBackground(background);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(background.brighter()),
                new EmptyBorder(9, 16, 9, 16)));
        return button;
    }

    private static ImageIcon icon(Image image, int width, int height) {
        if (image == null) {
            return new ImageIcon();
        }
        return new ImageIcon(image.getScaledInstance(width, height, Image.SCALE_SMOOTH));
    }

    private static void appendLine(JTextArea area, String message) {
        Runnable append = () -> {
            area.append((message == null ? "" : message) + "\n");
            area.setCaretPosition(area.getDocument().getLength());
        };
        if (SwingUtilities.isEventDispatchThread()) {
            append.run();
        } else {
            SwingUtilities.invokeLater(append);
        }
    }

    private String playerName(int index) {
        return safeName(game.getPlayerList().get(index).getName());
    }

    private String displayName(int index, String name) {
        if (index == activePlayer) {
            return name.isEmpty() ? "You" : name + " (You)";
        }
        return name.isEmpty() ? "Open seat" : name;
    }

    private String cardCountLabel(int index) {
        int count = game.getPlayerList().get(index).getNumOfCards();
        if (game.getStarted()) {
            return count + (count == 1 ? " card" : " cards");
        }
        return playerName(index).isEmpty() ? "Waiting for player" : "Ready at table";
    }

    private String currentPlayerName() {
        int index = game.getCurrentPlayerIdx();
        return index >= 0 && index < PLAYER_COUNT ? displayName(index, playerName(index)) : "A player";
    }

    private static String safeName(String name) {
        return name == null ? "" : name.trim();
    }

    private final class CardView extends JPanel {
        private static final long serialVersionUID = 1L;
        private final transient Image image;
        private final int cardIndex;
        private final boolean selectable;

        private CardView(Image image, int cardIndex, boolean selectable) {
            this.image = image;
            this.cardIndex = cardIndex;
            this.selectable = selectable;
            setOpaque(false);
            if (selectable) {
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent event) {
                        if (cardIndex < selected.length) {
                            selected[cardIndex] = !selected[cardIndex];
                            refreshView();
                        }
                    }
                });
            }
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            Graphics2D g2 = (Graphics2D) graphics.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (image != null) {
                g2.drawImage(image, 1, 1, getWidth() - 2, getHeight() - 2, this);
            } else {
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 9, 9);
                g2.setColor(NAVY);
                g2.drawString("Card", 20, getHeight() / 2);
            }
            if (selectable && cardIndex < selected.length && selected[cardIndex]) {
                g2.setColor(GOLD);
                g2.setStroke(new java.awt.BasicStroke(3f));
                g2.drawRoundRect(2, 2, getWidth() - 5, getHeight() - 5, 9, 9);
            }
            g2.dispose();
        }
    }

    private static final class TableSurface extends JPanel {
        private static final long serialVersionUID = 1L;

        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            Graphics2D g2 = (Graphics2D) graphics.create();
            g2.setPaint(new GradientPaint(0, 0, FELT, getWidth(), getHeight(), FELT_DARK));
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.dispose();
        }
    }

    private static final class RoundedPanel extends JPanel {
        private static final long serialVersionUID = 1L;
        private final Color fill;

        private RoundedPanel(Color fill) {
            this.fill = fill;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g2 = (Graphics2D) graphics.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(fill);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
            g2.dispose();
            super.paintComponent(graphics);
        }
    }
}
