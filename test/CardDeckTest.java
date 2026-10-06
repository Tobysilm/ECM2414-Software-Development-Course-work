import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.beans.Test;

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

        deck.drawFromTop();
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
}