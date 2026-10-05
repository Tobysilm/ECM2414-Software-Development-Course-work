import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

// this represents a player in the game, each player runs its own thread
//they draw from the deck on their left and discard to the deck on their right
//until someone has a winning hand (4 cards of the same value)  
public class Player implements Runnable {

    private final int id;
    private final CardDeck left;      // the deck this player draws from
    private final CardDeck right;     // the deck this player discards to
    private final GameState gameState;
    private final Path outputDir;
    private final List<Card> hand = new ArrayList<>();
    private final Random random = new Random();
    private PrintWriter writer;       // this player's output file, opened when the game starts

    public Player(int id, CardDeck left, CardDeck right, GameState gameState, Path outputDir) {
        this.id = id;
        this.left = left;
        this.right = right;
        this.gameState = gameState;
        this.outputDir = outputDir;
    }

    public int getId() {
        return id;
    }

    // used by CardGame when dealing out the starting hand
    public synchronized void addCard(Card card) {
        hand.add(card);
    }

    // true if the hand is four cards that all have the same value
    public synchronized boolean hasWinningHand() {
        if (hand.size() != 4) {
            return false;
        }
        for (Card card : hand) {
            if (card.getvalue() != hand.get(0).getvalue()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void run() {
        try {
            writer = new PrintWriter(outputDir.resolve("player" + id + "_output.txt").toFile());
        } catch (FileNotFoundException e) {
            System.out.println("couldn't open the output file for player " + id);
            return;
        }

        try {
            writer.println("player " + id + " initial hand " + handText());
            playGame();
            finishGame();
        } catch (InterruptedException e) {
            // shouldn't happen, if it does just stop
        } finally {
            writer.close();
        }
    }

    // keeps taking turns until somebody has won
    // (if a player was dealt a winning hand the game is already over, so this does nothing)
    private void playGame() throws InterruptedException {
        while (!gameState.isOver()) {
            // wait here until there is a card to draw, false means the game ended while waiting
            if (!left.awaitCard(gameState)) {
                return;
            }

            takeTurn();

            if (hasWinningHand()) {
                gameState.declareWinner(id);
            }
        }
    }

    // draws a card and discards a card as one single action
    private void takeTurn() {
        // we lock both decks so nobody else can see or change them halfway through the turn.
        // every player locks the lower numbered deck first, so two players can never
        // end up each holding one deck and waiting for the other (a deadlock)
        CardDeck first = left;
        CardDeck second = right;
        if (right.getId() < left.getId()) {
            first = right;
            second = left;
        }

        Card drawn;
        Card discarded;
        synchronized (first) {
            synchronized (second) {
                drawn = left.removeFromTop();
                addCard(drawn);
                discarded = removeCardToDiscard();
                right.addToBottom(discarded);
            }
        }

        // writing to the file is done after the locks are let go so the decks aren't held up
        writer.println("player " + id + " draws a " + drawn + " from deck " + left.getId());
        writer.println("player " + id + " discards a " + discarded + " to deck " + right.getId());
        writer.println("player " + id + " current hand is " + handText());
    }

    // takes a random card out of the hand that isn't the player's preferred value
    // (the preferred value is the same as the player's number)
    private synchronized Card removeCardToDiscard() {
        List<Card> options = new ArrayList<>();
        for (Card card : hand) {
            if (card.getvalue() != id) {
                options.add(card);
            }
        }

        // there is always a card that isn't preferred, but just in case discard the first one
        if (options.isEmpty()) {
            return hand.remove(0);
        }

        Card chosen = options.get(random.nextInt(options.size()));
        hand.remove(chosen);
        return chosen;
    }

    // writes the last lines of the player's file, depending on if they won or not
    private void finishGame() {
        int winner = gameState.getWinner();

        if (winner == id) {
            writer.println("player " + id + " wins");
            writer.println("player " + id + " exits");
            writer.println("player " + id + " final hand: " + handText());
        } else {
            writer.println("player " + winner + " has informed player " + id + " that player " + winner + " has won");
            writer.println("player " + id + " exits");
            writer.println("player " + id + " hand: " + handText());
        }
    }

    // the hand as text with spaces between the values, like "1 1 2 4"
    private synchronized String handText() {
        String text = "";
        for (Card card : hand) {
            text = text + card + " ";
        }
        return text.trim();
    }
}