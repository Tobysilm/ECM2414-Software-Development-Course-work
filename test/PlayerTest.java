import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class PlayerTest {

    @TempDir
    Path tempDir;

    // makes a player with two decks next to it and puts the given cards in its hand
    private Player makePlayer(int id, GameState state, int... handValues) {
        CardDeck left = new CardDeck(id);
        CardDeck right = new CardDeck(id + 1);
        Player player = new Player(id, left, right, state, tempDir);

        for (int value : handValues) {
            player.addCard(new Card(value));
        }
        return player;
    }

    @Test
    public void testFourOfAKindWins() {
        Player player = makePlayer(1, new GameState(), 1, 1, 1, 1);

        assertTrue(player.hasWinningHand());
    }

    @Test
    public void testFourOfAKindWinsEvenIfNotTheirPreferredNumber() {
        Player player = makePlayer(1, new GameState(), 7, 7, 7, 7);

        assertTrue(player.hasWinningHand());
    }

    @Test
    public void testFourZerosWin() {
        Player player = makePlayer(2, new GameState(), 0, 0, 0, 0);

        assertTrue(player.hasWinningHand());
    }

    @Test
    public void testThreeOfAKindIsNotEnough() {
        Player player = makePlayer(1, new GameState(), 1, 1, 1, 2);

        assertFalse(player.hasWinningHand());
    }

    @Test
    public void testThreeCardsCanNeverWin() {
        Player player = makePlayer(1, new GameState(), 1, 1, 1);

        assertFalse(player.hasWinningHand());
    }

    // if someone has already won, the player shouldn't take any turns
    // so its file should just be the 4 lines from the spec
    @Test
    public void testPlayerWhoLosesWritesFourLines() throws Exception {
        GameState state = new GameState();
        state.declareWinner(2);
        Player player = makePlayer(1, state, 1, 1, 2, 3);

        player.run();   // the game is already over, so this finishes straight away

        List<String> lines = Files.readAllLines(tempDir.resolve("player1_output.txt"));
        assertEquals(4, lines.size());
        assertEquals("player 1 initial hand 1 1 2 3", lines.get(0));
        assertEquals("player 2 has informed player 1 that player 2 has won", lines.get(1));
        assertEquals("player 1 exits", lines.get(2));
        assertEquals("player 1 hand: 1 1 2 3", lines.get(3));
    }

    @Test
    public void testPlayerWhoWinsWritesFourLines() throws Exception {
        GameState state = new GameState();
        state.declareWinner(1);
        Player player = makePlayer(1, state, 5, 5, 5, 5);

        player.run();

        List<String> lines = Files.readAllLines(tempDir.resolve("player1_output.txt"));
        assertEquals(4, lines.size());
        assertEquals("player 1 initial hand 5 5 5 5", lines.get(0));
        assertEquals("player 1 wins", lines.get(1));
        assertEquals("player 1 exits", lines.get(2));
        assertEquals("player 1 final hand: 5 5 5 5", lines.get(3));
    }
}