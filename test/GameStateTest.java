import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicInteger;

public class GameStateTest {

    @Test
    public void testNewGameIsNotOver() {
        GameState state = new GameState();

        assertFalse(state.isOver());
        assertEquals(0, state.getWinner());
    }

    @Test
    public void testFirstPlayerToDeclareWins() {
        GameState state = new GameState();

        assertTrue(state.declareWinner(3));

        assertTrue(state.isOver());
        assertEquals(3, state.getWinner());
    }

    @Test
    public void testSecondPlayerCannotWinAsWell() {
        GameState state = new GameState();
        state.declareWinner(2);

        assertFalse(state.declareWinner(4));

        assertEquals(2, state.getWinner());   // still plaer 2
    }

    // counts how many times it gets run
    private class Counter implements Runnable {
        AtomicInteger timesRun = new AtomicInteger(0);

        public void run() {
            timesRun.incrementAndGet();
        }
    }

    @Test
    public void testGameOverListenerRunsOnlyOnce() {
        GameState state = new GameState();
        Counter counter = new Counter();
        state.onGameOver(counter);

        state.declareWinner(1);
        state.declareWinner(2);   // too late, so this must not run the listener again

        assertEquals(1, counter.timesRun.get());
    }

    // a thread that waits at the start line, then tries to win
    private class Racer extends Thread {
        GameState state;
        CyclicBarrier startLine;
        AtomicInteger winCount;
        int playerId;

        Racer(GameState state, CyclicBarrier startLine, AtomicInteger winCount, int playerId) {
            this.state = state;
            this.startLine = startLine;
            this.winCount = winCount;
            this.playerId = playerId;
        }

        public void run() {
            try {
                startLine.await();   // everyone waits here until all 20 have arrived
                if (state.declareWinner(playerId)) {
                    winCount.incrementAndGet();
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    // 20 threads try to win at the same moment,and only one should manege it
    // we do it 200 times because a race doesn't go wrong every time
    @Test
    public void testOnlyOneWinnerWhenThreadsRace() throws InterruptedException {
        for (int round = 0; round < 200; round++) {
            GameState state = new GameState();
            CyclicBarrier startLine = new CyclicBarrier(20);
            AtomicInteger winCount = new AtomicInteger(0);
            Racer[] racers = new Racer[20];

            for (int i = 0; i < 20; i++) {
                racers[i] = new Racer(state, startLine, winCount, i + 1);
                racers[i].start();
            }
            for (int i = 0; i < 20; i++) {
                racers[i].join();
            }

            assertEquals(1, winCount.get());
        }
    }
}