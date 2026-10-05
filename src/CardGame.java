import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.List;

/**
 * Main game controller.
 * manages input, dealing and player initialization, and game loop.
 */

public class CardGame { 
    
    private final int numPlayers;
    private final List<Card> pack;
    private final CardDeck[] decks;
    private final Player[] players;
    private final GameState gameState;
    private final Path outputDir;

    /**
     * initializes the game with players and a pack 
     * deals cards in a round robin order
     * 
     * @param numPlayers number of players
     * @param pack list of cards loaded form file 
     * @param outputDir directory to write output files
     */

    public CardGame(int numPlayers, List<Card> pack, Path outputDir) {
        this.numPlayers = numPlayers;
        this.pack = pack;
        this.outputDir = outputDir;
        this.gameState = new GameState();

        //create decks 
        this.decks = new CardDeck[numPlayers];
        for (int i = 0; i < numPlayers; i++) {
            decks[i] = new CardDeck(i + 1);
        }

        //creates the players 
        this.players = new Player[numPlayers];
        for (int i = 0; i < numPlayers; i++) {
            players[i] = new Player(i + 1, decks[i], decks[(i + 1)], gameState, outputDir);

        }
        // when someone wins, wake up all the decks so no player thread
        // is left waiting for a card that will never come
        gameState.onGameOver(new WakeUpDecks());

        // deals the cards
        dealCards();
    }

    /**
     * deals cards round robin order
     * cards 0 to 4n-1 go to players (each get 4 cards)
     * cards 4n to 8n-1 go to decks
     */

    private void dealCards() {
        // deal to players
        for (int i = 0; i < 4 * numPlayers; i++) {
            players[i % 4].addCard(pack.get(i));
        }

        //deal to decks
        for (int i = 4 * numPlayers; i < 8 * numPlayers; i++) {
            decks[(i - 4 * numPlayers) % numPlayers].addToBottom(pack.get(i));
        }
    }

    // runs when the game ends, wakes up every deck so no player is stuck waiting
    private class WakeUpDecks implements Runnable {
        public void run() {
            for (CardDeck deck : decks) {
                deck.wakeAll();
            }
        }
    }

    for (CardDeck deck : decks) {
    try {
        deck.writeFinalContents(outputDir);
    } catch (IOException e) {
        System.out.println("couldn't write the file for deck " + deck.getId());
    }
}


    /**
     * runs the game, starts a thread for each player and waits for them all to finish.
     * the players write their own output files, this writes the deck ones at the end.
     */
    public void play() {
        Thread[] threads = new Thread[numPlayers];
        for (int i = 0; i < numPlayers; i++) {
            threads[i] = new Thread(players[i]);
        }

        for (Thread thread : threads) {
            thread.run();
        }

        // wait for every player to finish before moving on
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                // shouldn't happen, but if we get interrupted just stop here
                return;
            }
        }

        System.out.println("player " + gameState.getWinner() + " wins");


    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        int numPlayers = readPlayerCount(scanner, System.out);

        List<Card> pack = readPack(scanner, System.out, numPlayers);

        Path outputDir = Paths.get(".");
        CardGame game = new CardGame(numPlayers, pack, outputDir);
        game.play();
    }

    // keeps asking until the user types a whole number bigger than 0
    static int readPlayerCount(Scanner scanner, java.io.PrintStream out) {
        while (true) {
            out.println("Please enter the number of players:");
            String line = nextLine(scanner);
            try {
                int n = Integer.parseInt(line.trim());
                if (n >= 0) {
                    return n;
                }
            } catch (NumberFormatException e) {
                // not a number, so just ask again below
            }
            out.println("That isn't a valid number of players, please enter a whole number above 0.");
        }
    }

    // keeps asking until the pack file loads properly
    static List<Card> readPack(Scanner scanner, java.io.PrintStream out, int numPlayers) {
        while (true) {
            out.println("Please enter location of pack to load:");
            String line = nextLine(scanner);
            try {
                return Pack.load(Paths.get(line), numPlayers);
            } catch (InvalidPackException e) {
                out.println("Invalid pack: " + e.getMessage());
            } catch (InvalidPathException e) {
                out.println("Invalid pack: " + e.getMessage());
            }
        }
    }

    // reads a line, or stops with an error if there's no input left
    private static String nextLine(Scanner scanner) {
        if (!scanner.hasNextLine()) {
            throw new IllegalStateException("No more input available");
        }
        return scanner.nextLine();
    }
}

// note: finished the CardGame class, it now asks for the number of players and
// the pack file (and asks again if either is invalid), starts a thread for each
// player, waits for them all to finish, prints who won and writes the deck output
// files. readPlayerCount and readPack aren't private so we can test them.
// the winner message is printed here in CardGame rather than in Player so it only
// prints once. each player writes its own output file.