import java.util.LinkedList;
import java.util.Queue;

/**
 * Represents a deck in the card game.
 * A deck is a FIFO(First In First Out) queue that is thread-safe.
 * All operations are synchronized to prevent race conditions when multiple
 * players access the deck at the same time.
 */
public class CardDeck {

    private final int id;
    private final Queue<Card> cards;

    /**
     * Creates a deck with the given ID.
     * 
     * @param id the decks identifier (player index)
     */
    public CardDeck(int id) {
        this.id = id;
        this.cards = new LinkedList<>();
    }

    /**
     *returns the ID of this deck.
     *@return the decks ID 
     */ 
    public int getId() {
        return id;
    }

    /**
     * Adds a card to the bottom of the deck.
     * syncs to prevent race conditions 
     * Notifies all waiting threads that a card is available.
     * 
     * @param card the card to add 
     */
    public synchronized void addToBottom(Card card) {
        cards.add(card);
        notifyAll(); // wake up any thread waiting in drawFromTop()
    }

    /**
     * Waits for a card to become available and returns it.
     * Blocks the calling thread until a card is available or the game ends.
     * uses wait/notify pattern
     * 
     * @param gameState the current game state (to check if the game is over)
     * @return true if a card was available before timeout, fale if game ended
     * @throws InterruptedException if the thread is interrupted while waiting 
     */

    public synchronized boolean awaitCard(GameState GameState) throws InterruptedException {
        // using while loop, not if, to handle spurious wakeups
        while (cards.isEmpty() && !GameState.isOver()) {
            wait(); // Release lock and wait for notification
        }
        return !GameState.isOver();
    }
    
    /**
     * Wakes up all threads waiting for a card in awaitcard()
     * called when the game ends
     * synchs to be thread safe
     */
    public synchronized void wakeAll() {
        notifyAll();
    }
    
    /**
     * returns the number of cards currently in the deck 
     * synchs for consistency 
     * 
     * @return the number of cards
     */

    public synchronized int size() {
        return  cards.size();
    }

    /**
     * returns a copy of all cards currently in the deck
     * used for logging/display without exposing the internal queue
     * synched to prevent modification during iteration
     * 
     * @return a list of cards in FIFO order
     */

    public synchronized java.util.List<Card> getCards() {
        return new java.util.ArrayList<>(cards);
    }

    /** 
     * Returns a string representation of the deck  
     * @return the decks ID and current card count 
     */
    
    @Override
    public String toString() {
        return "Deck " + id + " (size:" + size() + ")";
    }
}