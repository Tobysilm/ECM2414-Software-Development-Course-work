import java.nio.file.Files;
import java.nio.file.Path;
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

    /**
     * initializes the game with players and a pack 
     * deals cards in a round robin order
     * 
     * @param numPlayers number of players
     * @param pack list of cards loaded form file 
     * @param outputDir directory to write output files
     */

    public CardGame(int numPlayers, List<Card> pack, Path outputDir) {
        this.numPlayers = numPlayer;
        this.pack = pack;
        this.gameState = new GameState();

        //create decks 
        this.decks = new CardDeck[numPlayers];
        for (int i = 0; i < numPlayers; i++) {
            decks[i] = new CardDeck(i + 1);
        }

        //creates the players 
        this.players = new Player[numPlayers];
        for (int i = 0; i < numPlayers; i++) {
            players[i] = new Player(i + 1, decks[i], decks([i+1) % numPlayers],gameState);

        }
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
            players[i % numPlayers].addCard(pack.get(i));
        }

        //deal to decks
        for (int i = 4 * numPlayers; i < 8 * numPlayers; i++) {
            decks[(i - 4 * numPlayers) % numPlayers].addToBottom(pack.get(i));
        }
    }

    public void play() {
        // eric, if finish this, running the game loop, threads and barrier etc.
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(system.in);

        int numPlayers = readPlayerCount(scanner, System.out);

        List<Card> pack = readPack(scanner, System.out, numPlayers);

        Path outputDir = Paths.get(".");
        CardGame game = new CardGame(numPlayers, pack, outputDir);
        game.play();
    }

    private static int readPlayerCount(Scanner scanner, java.io.PrintStream out) {
        // eric this is for the invalid input and reprompting
    }

    




}
