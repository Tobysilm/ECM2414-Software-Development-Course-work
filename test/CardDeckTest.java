import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CardDeckTest {

    private CardDeck deck;

    @BeforeEach
    public void setUp() {
        deck = new CardDeck(1);
    }

    @Test
    public void testDeckCreation() {
        assertEquals(1, deck.getId(), "deck should store ID correctly");
    }

    @Test
    public void testSize() {
        assertEquals(0, deck.size(), "empty deck has size 0");

        deck.addToBottom(new Card(1));
        assertEquals(1, deck.size(), "empty deck has size 1 after 1 adds");

        deck.addToBottom(new Card(2));
        assertEquals(2, deck.size(), "empty deck has size 2 after 2 adds");

        deck.removeFromTop();
        assertEquals(1, deck.size(), "Deck has size 1 after one draw");
    }

    @Test 
    public void testGetCards() {
        Card card1 = new Card(1);
        Card card2 = new Card(2);

        deck.addToBottom(card1);
        deck.addToBottom(card2);

        java.util.List<Card> cards = deck.getCards();
        assertEquals(2, cards.size(), "getCards should return correct number of cards");
        assertEquals(card1, cards.get(0), "First card should be card1");
        assertEquals(card2, cards.get(1), "second card should be card2");
    }

    @Test
    public void testToString() {
        deck.addToBottom(new Card(1));
        String str = deck.toString();
        assertTrue(str.contains("Deck 1"), "toString should contain deck ID");
        assertTrue(str.contains("size: 1"), "toString should contain size");
    }

    @Test
    public void testNewDeckIsEmpty() {
        assertEquals(0, deck.size());
        assertNull(deck.removeFromTop());
    }

    @Test
    public void testCardsComeOutInTheOrderTheyWentIn() {
        Card first = new Card(1);
        Card second = new Card(2);
        Card third = new Card(3);

        deck.addToBottom(first);
        deck.addToBottom(second);
        deck.addToBottom(third);

        assertEquals(first, deck.removeFromTop());
        assertEquals(second, deck.removeFromTop());
        assertEquals(third, deck.removeFromTop());
        assertNull(deck.removeFromTop());
    }

    // a thread that adds 1000 cards to the deck
    private class Adder extends Thread {
        public void run() {
            for (int i = 0; i < 1000; i++) {
                deck.addToBottom(new Card(i));
            }
        }
    }

    @Test
    public void testNoCardsLostWhenThreadsAddAtTheSameTime() throws InterruptedException {
        Adder[] adders = new Adder[4];

        for (int i = 0; i < 4; i++) {
            adders[i] = new Adder();
            adders[i].start();
        }
        for (int i = 0; i < 4; i++) {
            adders[i].join();
        }

        assertEquals(4000, deck.size());
    }
}