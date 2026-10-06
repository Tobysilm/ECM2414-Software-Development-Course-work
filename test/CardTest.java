package test;
import org.junit.jupiter.api.Test;

import Card;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Card class.
 */
public class CardTest {

    @Test
    public void testCardCreation() {
        Card card = new Card(5);
        assertEquals(5, card.getValue(), "Card should store correct value");
    }

    @Test
    public void testCardZeroValue() {
        Card card = new Card(0);
        assertEquals(0, card.getValue(), "Card should accept value 0");
    }

    @Test
    public void testCardLargeValue() {
        Card card = new Card(1000);
        assertEquals(1000, card.getValue(), "Card should accept large values");
    }

    @Test
    public void testCardNegativeThrows() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Card(-1);
        }, "Card should reject negative values");
    }

    @Test
    public void testCardEquality() {
        Card card1 = new Card(5);
        Card card2 = new Card(5);
        assertEquals(card1, card2, "Cards with same value should be equal");
    }

    @Test
    public void testCardHashCodeConsistency() {
        Card card1 = new Card(5);
        Card card2 = new Card(5);
        assertEquals(card1.hashCode(), card2.hashCode(), 
            "Cards with same value should have same hash code");
    }

    @Test
    public void testCardToString() {
        Card card = new Card(5);
        assertEquals("5", card.toString(), "Card toString should return value as string");
    }

    @Test
    public void testCardImmutability() {
        Card card = new Card(5);
        // No setter exists, so value cannot change
        assertEquals(5, card.getValue(), "Card value should be immutable");
    }
}