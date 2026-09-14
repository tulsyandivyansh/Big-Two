import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

/** Builds deterministic runtime artwork from generated master images. */
public final class GenerateDeckAssets {
    private static final int CARD_WIDTH = 240;
    private static final int CARD_HEIGHT = 336;
    private static final String[] RANKS = {"A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K"};
    private static final String[] SUITS = {"♦", "♣", "♥", "♠"};
    private static final Color NAVY = new Color(12, 35, 61);
    private static final Color RED = new Color(177, 38, 48);

    private GenerateDeckAssets() {
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 5) {
            throw new IllegalArgumentException("template avatar-sheet logo table output-root");
        }
        File root = new File(args[4]);
        File cards = new File(root, "cards");
        File avatars = new File(root, "avatars");
        File arena = new File(root, "arena");
        cards.mkdirs();
        avatars.mkdirs();
        arena.mkdirs();

        BufferedImage template = ImageIO.read(new File(args[0]));
        generateCards(template, cards);
        generateAvatars(ImageIO.read(new File(args[1])), avatars);
        writeCover(ImageIO.read(new File(args[2])), 256, 256, new File(avatars, "LOGOICON.png"));
        writeCover(ImageIO.read(new File(args[3])), 1600, 900, new File(arena, "CardGameTable.jpg"));
        generateEmptyAvatar(new File(avatars, "empty.png"));
    }

    private static void generateCards(BufferedImage template, File output) throws Exception {
        BufferedImage base = cover(template, CARD_WIDTH, CARD_HEIGHT);
        for (int suit = 0; suit < SUITS.length; suit++) {
            for (int rank = 0; rank < RANKS.length; rank++) {
                BufferedImage card = copy(base);
                Graphics2D g = card.createGraphics();
                quality(g);
                Color ink = suit == 0 || suit == 2 ? RED : NAVY;
                drawCorner(g, RANKS[rank], SUITS[suit], ink, false);
                AffineTransform transform = g.getTransform();
                g.rotate(Math.PI, CARD_WIDTH / 2.0, CARD_HEIGHT / 2.0);
                drawCorner(g, RANKS[rank], SUITS[suit], ink, false);
                g.setTransform(transform);
                drawCenter(g, rank, SUITS[suit], ink);
                g.dispose();
                ImageIO.write(card, "png", new File(output, suit + "-" + rank + ".png"));
            }
        }
    }

    private static void drawCorner(Graphics2D g, String rank, String suit, Color ink, boolean ignored) {
        g.setColor(ink);
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, rank.length() == 2 ? 29 : 35));
        g.drawString(rank, 23, 52);
        g.setFont(new Font(Font.SERIF, Font.PLAIN, 31));
        g.drawString(suit, 25, 82);
    }

    private static void drawCenter(Graphics2D g, int rank, String suit, Color ink) {
        g.setColor(ink);
        if (rank == 0 || rank >= 10) {
            String center = rank == 0 ? suit : RANKS[rank];
            g.setFont(new Font(rank == 0 ? Font.SERIF : Font.SANS_SERIF, Font.BOLD, rank == 0 ? 104 : 96));
            drawCentered(g, center, CARD_HEIGHT / 2 + 34);
            if (rank >= 10) {
                g.setFont(new Font(Font.SERIF, Font.PLAIN, 54));
                drawCentered(g, suit, CARD_HEIGHT / 2 + 90);
            }
            return;
        }

        int count = rank + 1;
        int[][] positions = {
            {120, 168}, {82, 112}, {158, 224}, {82, 224}, {158, 112},
            {82, 168}, {158, 168}, {120, 112}, {120, 224}, {120, 72}
        };
        g.setFont(new Font(Font.SERIF, Font.PLAIN, 48));
        for (int i = 0; i < count; i++) {
            FontMetrics metrics = g.getFontMetrics();
            g.drawString(suit, positions[i][0] - metrics.stringWidth(suit) / 2, positions[i][1]);
        }
    }

    private static void generateAvatars(BufferedImage sheet, File output) throws Exception {
        String[] names = {"Player1.png", "Player2.png", "Player3.png", "Player4.png"};
        int cellWidth = sheet.getWidth() / 4;
        for (int i = 0; i < names.length; i++) {
            BufferedImage cell = sheet.getSubimage(i * cellWidth, 0, cellWidth, sheet.getHeight());
            writeCover(cell, 256, 256, new File(output, names[i]));
        }
    }

    private static void generateEmptyAvatar(File output) throws Exception {
        BufferedImage image = new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        quality(g);
        g.setColor(new Color(17, 37, 57));
        g.fillOval(0, 0, 256, 256);
        g.setColor(new Color(60, 88, 104));
        g.fillOval(88, 48, 80, 80);
        g.fillOval(48, 142, 160, 120);
        g.setColor(new Color(201, 164, 76));
        g.setStroke(new java.awt.BasicStroke(5));
        g.drawOval(3, 3, 249, 249);
        g.dispose();
        ImageIO.write(image, "png", output);
    }

    private static void writeCover(BufferedImage source, int width, int height, File output) throws Exception {
        String format = output.getName().endsWith(".jpg") ? "jpg" : "png";
        ImageIO.write(cover(source, width, height), format, output);
    }

    private static BufferedImage cover(BufferedImage source, int width, int height) {
        double scale = Math.max(width / (double) source.getWidth(), height / (double) source.getHeight());
        int scaledWidth = (int) Math.ceil(source.getWidth() * scale);
        int scaledHeight = (int) Math.ceil(source.getHeight() * scale);
        int type = source.getColorModel().hasAlpha() ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB;
        BufferedImage result = new BufferedImage(width, height, type);
        Graphics2D g = result.createGraphics();
        quality(g);
        g.drawImage(source.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH),
                (width - scaledWidth) / 2, (height - scaledHeight) / 2, null);
        g.dispose();
        return result;
    }

    private static BufferedImage copy(BufferedImage source) {
        BufferedImage result = new BufferedImage(source.getWidth(), source.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = result.createGraphics();
        g.drawImage(source, 0, 0, null);
        g.dispose();
        return result;
    }

    private static void drawCentered(Graphics2D g, String text, int baseline) {
        FontMetrics metrics = g.getFontMetrics();
        g.drawString(text, (CARD_WIDTH - metrics.stringWidth(text)) / 2, baseline);
    }

    private static void quality(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
    }
}
