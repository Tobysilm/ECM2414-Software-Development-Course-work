import java.util.ArrayList;
import java.util.List;

// shared by all the player threads, keeps track of wether someone has won
public class GameState {

    // 0 means nobody has won yet, otherwise it's the winning player's number
    private int winner = 0;

    // things to run when the game ends (CardGame uses this to wake up the decks)
    private final List<Runnable> listeners = new ArrayList<>();

    public void onGameOver(Runnable listener) {
        listeners.add(listener);
    }

    // a player calls this when they get four of a kind
    // returns true if this player won, false if someone else got there first
    public boolean declareWinner(int playerId) {
        synchronized (this) {
            if (winner != 0) {
                return false;
            }
            winner = playerId;
        }

        // run these outside the synchronized block so we never hold this lock and a deck lock at the same time

        for (Runnable listener : listeners) {
            listener.run();
        }
        return true;
    }

    public synchronized boolean isOver() {
        return winner != 0;
    }

    // returns 0 if nobody has won yet
    public synchronized int getWinner() {
        return winner;
    }
}

//note: an earlier version used an atomicinteger for the winner instead of sychronizing,
//i switched to int as it made it easier to follow for both myself and toby and it does the
//same job of checking and setting the winner in one atomic operation. THe listeners run
//outside the synchronized block so we never hold this lock and a deck lock at the same time.