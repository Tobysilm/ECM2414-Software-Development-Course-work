import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class CardGameTest {

    @TempDir
    Path tempDir;

    // helpers

    private List<Card> cards(int... values) {
        List<Card> list = new ArrayList<>();
        for (int value : values) {
            list.add(new Card(value));
        }
        return list;
    }

    // every value from 1 to 2n appears 4 times, so somebody can always win
    // using the same seed gives the same shuffle each time, so a failure can be repeated
    private List<Card> winnablePack(int n, int seed) {
        List<Card> pack = new ArrayList<>();
        for (int value = 1; value <= 2 * n; value++) {
            for (int copy = 0; copy < 4; copy++) {
                pack.add(new Card(value));
            }
        }
        Collections.shuffle(pack, new Random(seed));
        return pack;
    }

    // plays the game, and fails if it takes more than 10 seconds (it has probably got stuck)
    private void play(CardGame game) {
        assertTimeoutPreemptively(Duration.ofSeconds(10), game::play);
    }

    private List<String> readLines(Path folder, String fileName) throws Exception {
        return Files.readAllLines(folder.resolve(fileName));
    }

    // turns text like " 1 3 3 7" into a list of numbers (an empty deck gives an empty list)
    private List<Integer> numbersIn(String text) {
        List<Integer> numbers = new ArrayList<>();
        for (String word : text.trim().split(" ")) {
            if (!word.isEmpty()) {
                numbers.add(Integer.parseInt(word));
            }
        }
        return numbers;
    }

    // takes everything after the colon in a line like "player 1 final hand: 1 1 1 1"
    private List<Integer> numbersAfterColon(String line) {
        return numbersIn(line.substring(line.indexOf(':') + 1));
    }

    //dealing and winning straight away 
    // player 1 is dealt cards 0, 2, 4 and 6 of the pack, and player 2 gets 1, 3, 5 and 7
    // the decks then get the rest, one card at a time, starting with deck 1
    @Test
    public void testCardsAreDealtRoundRobinAndPlayerWinsStraightAway() throws Exception {
        List<Card> pack = cards(1, 2, 1, 3, 1, 4, 1, 5,
                                6, 7, 8, 9, 10, 11, 12, 13);

        play(new CardGame(2, pack, tempDir));

        List<String> player1 = readLines(tempDir, "player1_output.txt");
        List<String> player2 = readLines(tempDir, "player2_output.txt");
        assertEquals(4, player1.size());
        assertEquals(4, player2.size());
        assertEquals("player 1 initial hand 1 1 1 1", player1.get(0));
        assertEquals("player 2 initial hand 2 3 4 5", player2.get(0));
        assertEquals("player 1 wins", player1.get(1));
        assertEquals("player 1 has informed player 2 that player 1 has won", player2.get(1));

        // nobody took a turn, so the decks still hold what they were dealt
        assertEquals("deck1 contents: 6 8 10 12", readLines(tempDir, "deck1_output.txt").get(0));
        assertEquals("deck2 contents: 7 9 11 13", readLines(tempDir, "deck2_output.txt").get(0));
    }

    // player 2 likes 2s, but four 9s should still win
    @Test
    public void testFourOfAnyNumberWinsStraightAway() throws Exception {
        List<Card> pack = cards(1, 9, 2, 9, 3, 9, 4, 9,
                                5, 6, 7, 8, 5, 6, 7, 8);

        play(new CardGame(2, pack, tempDir));

        assertEquals("player 2 wins", readLines(tempDir, "player2_output.txt").get(1));
        assertEquals(4, readLines(tempDir, "player1_output.txt").size());
    }

    // whole games

    @Test
    public void testGameWritesTwoFilesForEachPlayer() throws Exception {
        play(new CardGame(4, winnablePack(4, 1), tempDir));

        for (int i = 1; i <= 4; i++) {
            assertTrue(Files.exists(tempDir.resolve("player" + i + "_output.txt")));
            assertTrue(Files.exists(tempDir.resolve("deck" + i + "_output.txt")));
        }
        assertEquals(8, tempDir.toFile().list().length);   // and no other files
    }

    // with 1 player, they draw from deck 1 and discard back into deck 1
    @Test
    public void testGameWithOnePlayerFinishes() throws Exception {
        List<Card> pack = cards(1, 2, 3, 4, 1, 1, 1, 5);

        play(new CardGame(1, pack, tempDir));

        List<String> lines = readLines(tempDir, "player1_output.txt");
        assertEquals("player 1 final hand: 1 1 1 1", lines.get(lines.size() - 1));
    }

    // plays 20 different games, and checks the rules in every one of them
    @Test
    public void testRulesHoldInManyGames() throws Exception {
        int n = 4;
        for (int seed = 1; seed <= 20; seed++) {
            Path folder = Files.createTempDirectory(tempDir, "game");
            List<Card> pack = winnablePack(n, seed);

            play(new CardGame(n, pack, folder));

            for (int player = 1; player <= n; player++) {
                checkTurns(folder, player, n);
                checkFinalHand(folder, player);
            }
            int winner = checkOneWinner(folder, n);
            checkLosersWereTold(folder, winner, n);
            checkNoCardsLost(folder, pack, n);
        }
    }

    // looks at every draw, discard and hand line in one player's file
    private void checkTurns(Path folder, int player, int n) throws Exception {
        List<String> lines = readLines(folder, "player" + player + "_output.txt");
        int deckOnTheRight = (player % n) + 1;
        String start = "player " + player;

        assertTrue(lines.get(0).startsWith(start + " initial hand "));

        for (String line : lines) {
            if (line.startsWith(start + " draws a ")) {
                assertTrue(line.endsWith(" from deck " + player));
            }
            if (line.startsWith(start + " discards a ")) {
                assertTrue(line.endsWith(" to deck " + deckOnTheRight));
                int discarded = Integer.parseInt(line.split(" ")[4]);
                assertNotEquals(player, discarded);   // never throws away their favourite
            }
            if (line.startsWith(start + " current hand is ")) {
                String hand = line.substring((start + " current hand is ").length());
                assertEquals(4, numbersIn(hand).size());   // still 4 cards after a turn
            }
        }
    }

    // everybody should end the game holding exactly 4 cards
    private void checkFinalHand(Path folder, int player) throws Exception {
        List<String> lines = readLines(folder, "player" + player + "_output.txt");
        String lastLine = lines.get(lines.size() - 1);

        assertEquals(4, numbersAfterColon(lastLine).size());
    }

    // exactly one player says they won, and they really do hold 4 of the same card
    private int checkOneWinner(Path folder, int n) throws Exception {
        int winner = 0;
        int howManyWon = 0;

        for (int player = 1; player <= n; player++) {
            List<String> lines = readLines(folder, "player" + player + "_output.txt");
            if (lines.contains("player " + player + " wins")) {
                winner = player;
                howManyWon++;

                List<Integer> hand = numbersAfterColon(lines.get(lines.size() - 1));
                assertEquals(Collections.nCopies(4, hand.get(0)), hand);
            }
        }

        assertEquals(1, howManyWon);
        return winner;
    }

    private void checkLosersWereTold(Path folder, int winner, int n) throws Exception {
        for (int player = 1; player <= n; player++) {
            if (player != winner) {
                List<String> lines = readLines(folder, "player" + player + "_output.txt");
                String expected = "player " + winner + " has informed player " + player
                        + " that player " + winner + " has won";
                assertTrue(lines.contains(expected));
            }
        }
    }

    // the cards in the 4 hands plus the cards in the decks should be exactly the pack we started with
    private void checkNoCardsLost(Path folder, List<Card> pack, int n) throws Exception {
        List<Integer> cardsAtEnd = new ArrayList<>();

        for (int i = 1; i <= n; i++) {
            List<String> playerLines = readLines(folder, "player" + i + "_output.txt");
            cardsAtEnd.addAll(numbersAfterColon(playerLines.get(playerLines.size() - 1)));

            String deckLine = readLines(folder, "deck" + i + "_output.txt").get(0);
            cardsAtEnd.addAll(numbersAfterColon(deckLine));
        }

        List<Integer> cardsAtStart = new ArrayList<>();
        for (Card card : pack) {
            cardsAtStart.add(card.getValue());
        }

        Collections.sort(cardsAtStart);
        Collections.sort(cardsAtEnd);
        assertEquals(cardsAtStart, cardsAtEnd);
    }

    // typing in the player count and pack file name, and checking the right number of players and cards are read

    @Test
    public void testBadPlayerCountsAreAskedAgain() {
        Scanner input = new Scanner("0\n-3\nabc\n\n2.5\n4\n");
        ByteArrayOutputStream screen = new ByteArrayOutputStream();

        int n = CardGame.readPlayerCount(input, new PrintStream(screen));

        assertEquals(4, n);
        // 5 wrong answers and then a right one, so the question is asked 6 times
        int timesAsked = screen.toString().split("Please enter the number of players:", -1).length - 1;
        assertEquals(6, timesAsked);
    }

    @Test
    public void testBadPacksAreAskedAgain() throws Exception {
        Path tooShort = tempDir.resolve("short.txt");
        Files.write(tooShort, "1\n2\n3\n".getBytes());
        Path good = tempDir.resolve("good.txt");
        Files.write(good, "1\n2\n3\n4\n5\n6\n7\n8\n".getBytes());

        // a file that doesn't exist, then one that is too short, then a good one
        String typed = tempDir.resolve("missing.txt") + "\n" + tooShort + "\n" + good + "\n";
        ByteArrayOutputStream screen = new ByteArrayOutputStream();

        List<Card> pack = CardGame.readPack(new Scanner(typed), new PrintStream(screen), 1);

        assertEquals(8, pack.size());
        int errors = screen.toString().split("Invalid pack", -1).length - 1;
        assertEquals(2, errors);
    }
}