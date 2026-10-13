/**
 * The central repository for all messages in the card game 
 * keeps everything together for consistency and testability
 */

public class Messages {

    // Player prompts 
    public static final String PROMPT_NUM_PLAYERS = "Please enter the number of players :";
    public static final String PROMPT_PACK_FILE = "Please enter the file path :";

    // Error messages 
    public static final String ERROR_INVALID_PLAYER_COUNT = "ERROR: Player count must be between 1 and 10.";
    public static final String ERROR_INVALID_PACK_FILE = "ERROR: Invalid pack file.";
    public static final String ERROR_FILE_NOT_FOUND = "ERROR: Pack file not found.";

    //Game flow messages 
    public static final String GAME_STARTING = "Game is starting ... ";
    public static final String GAME_OVER = "Game is over, Winner is player ";
    public static final String PLAYER_DRAW_MESSAGE = "Draw from deck";
    public static final String PLAYER_DISCARD_MESSAGE = "Discard from deck";

    //Output file headers 
    public static final String PLAYER_OUTPUT_HEADER = "Player output log ";
    public static final String DECK_OUTPUT_HEADER = "Deck output log ";

    // Retry prompts
    public static final String RETRY_PLAYER_COUNT = "Invalid player count, please enter a number between 1 and 10 :";
    public static final String RETRY_PACK_FILE = "Invalid path, please try again :";

    public static String playerOutputFilename (int playerNum) {
        return "Player" + playerNum + "_output.txt";
    }

    public static String deckOutputFilename (int deckNum) {
        return "Deck" + deckNum + "_output.txt";
    }

    public static String formatHand(java.util.List<Card> cards) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < cards.size(); i++) {
            if (i>0) sb.append(" ");
            sb.append(cards.get(i).getValue());
        }
        return sb.toString();
    }

    public static String formatDeck(java.util.List<Card> cards) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < cards.size(); i++) {
            if (i>0) sb.append(" ");
            sb.append(cards.get(i).getValue());
        }
        return sb.toString();
    }
}
