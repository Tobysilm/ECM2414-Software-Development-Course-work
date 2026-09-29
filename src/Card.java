/**
 * Represents a single card in the card game.
 * Cards are immutable - once created, their value cannot change.
 * This makes them safe to share between threads without synchronization.
 */

public final class Card {

    private final int value;
    /**
     *Creates a vard with the given value.

     * @param value The card's value(must be >= 0)
     *@throws IllegalArgumentException if the value is negative 
     */

    public Card(int value) {
        if (value < 0) {
            throw new IllegalArgumentException("Card value cannot be negative");
        }
        this.value = value;
    }

    /**
     * Returns the value of this card.
     * @return the card's value
     */
    public int getvalue() {
        return value;
    }

    /**   
     * Returns a string representation of this card.
     * @return the value of the string 
     */

    @Override 
    public String toString() {
        return String.valueOf(value);
    }

    /**    
     * checks if this card has the same value as anbother object.
     * @param o the object to compare
     * @return true if o is a card with the same value 
     */

    @Override 
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Card)) return false;
        Card card = (Card) o;
        return  value == card.value;
    }

    /** 
     * Retruns a hash code for this card.
     * Cards witht he same value have the same hash code.
     * @return hash code based on the card's value 
     */

    @Override 
    public int hashCode() {
        return Integer.hashCode(value);
    }
}