import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Loads and validates card packs from files.
 * A valid pack contains exactly 8n cards (non-negative integers),
 * where n is the number of players.
 */
public class Pack {
    
    /**
     * Loads cards from a file and checks wether its valid
     * 
     * Each line in the file must be a single non-negative integer.
     * The file must contain exactly 8 * numPlayers lines.
     * 
     * @param file the path to the pack file
     * @param numPlayers the number of players (must be positive)
     * @return a list of Card objects loaded from the file
     * @throws InvalidPackException if the file is invalid or contains invalid data
     */
    public static List<Card> load(Path file, int numPlayers) 
            throws InvalidPackException {
        
        // Validate numPlayers
        if (numPlayers <= 0) {
            throw new InvalidPackException("Number of players must be positive");
        }
        
        int expectedCards = 8 * numPlayers;
        List<Card> cards = new ArrayList<>();
        
        // Check if file exists
        if (!Files.exists(file)) {
            throw new InvalidPackException("Pack file not found: " + file);
        }
        
        // Read all lines from the file
        List<String> lines;
        try {
            lines = Files.readAllLines(file);
        } catch (Exception e) {
            throw new InvalidPackException("Failed to read pack file: " + file, e);
        }
        
        // Validate number of lines
        if (lines.size() != expectedCards) {
            throw new InvalidPackException(
                "Pack must contain " + expectedCards + " cards, but found " + lines.size()
            );
        }
        
        // Parse each line as a card value
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            
            //  the spec says every line must be a card value so no empty lines are allowed 
            if (line.isEmpty()) {
                throw new InvalidPackException(
                    "Line " + (i + 1) + " is empty (expected non-negative integer)"
                );
            }
            
            // Parse the integer value
            int value;
            try {
                value = Integer.parseInt(line);
            } catch (NumberFormatException e) {
                throw new InvalidPackException(
                    "Line " + (i + 1) + " is not a valid integer: " + line
                );
            }
            
            // Validate the value is non-negative
            if (value < 0) {
                throw new InvalidPackException(
                    "Line " + (i + 1) + " contains negative value: " + value
                );
            }
            
            // Add the card to the list
            cards.add(new Card(value));
        }
        
        return cards;
    }
}