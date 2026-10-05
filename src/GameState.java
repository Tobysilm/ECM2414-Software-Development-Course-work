import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

//shared by all of the player threads, checks whether the game is over and who won
//if two players win at the same time, the first one wins
public class GameState {

    private static final int NO_WINNER = 0;

    private final AtomicInteger winner = new AtomicInteger(NO_WINNER);
    private final List gameOverListeners = new ArrayList<>();

    
    //when the game is over, onGameOver() is called on all listeners, which wakes up any waiting threads
    public void onGameOver(listener) {
        gameOverListeners.add(listener);
    }

    // not called when holding a deck lock as listeners lock every deck to awaken the remaininng players
    // returns true if the game is over, false otherwise
    public boolean declareWinner(int playerId) {
        if (!winner.compareAndSet(NO_WINNER, playerId)) {
            return false;
        }
        for (listener : gameOverListeners) {
            listener.run();
        }
        return true;
    }

    public boolean isOver() {
        return winner.get() != NO_WINNER;
    }

    //returns the winners ID, or 0 if the game is not over
    public int getWinner() {
        return winner.get();
    }
}