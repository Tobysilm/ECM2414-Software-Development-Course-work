import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Pack class.
 */
public class PackTest {

    @TempDir
    Path tempDir;

    private Path createValidPack(int numPlayers) throws Exception {
        Path file = tempDir.resolve("valid.txt");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 8 * numPlayers; i++) {
            sb.append(i % 10).append("\n");
        }
        Files.write(file, sb.toString().getBytes());
        return file;
    }

    @Test
    public void testValidPack() throws Exception {
        Path file = createValidPack(1);
        List<Card> cards = Pack.load(file, 1);
        assertEquals(8, cards.size(), "Valid pack for 1 player should have 8 cards");
    }

    @Test
    public void testValidPack4Players() throws Exception {
        Path file = createValidPack(4);
        List<Card> cards = Pack.load(file, 4);
        assertEquals(32, cards.size(), "Valid pack for 4 players should have 32 cards");
    }

    @Test
    public void testPackTooShort() throws Exception {
        Path file = tempDir.resolve("short.txt");
        Files.write(file, "1\n2\n3\n".getBytes());

        assertThrows(InvalidPackException.class, () -> {
            Pack.load(file, 1);
        }, "Pack with fewer than 8 cards should throw");
    }

    @Test
    public void testPackTooLong() throws Exception {
        Path file = tempDir.resolve("long.txt");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            sb.append("1\n");
        }
        Files.write(file, sb.toString().getBytes());

        assertThrows(InvalidPackException.class, () -> {
            Pack.load(file, 1);
        }, "Pack with more than 8 cards should throw");
    }

    @Test
    public void testPackNegativeValue() throws Exception {
        Path file = tempDir.resolve("negative.txt");
        Files.write(file, "1\n2\n-1\n4\n5\n6\n7\n8\n".getBytes());

        assertThrows(InvalidPackException.class, () -> {
            Pack.load(file, 1);
        }, "Pack with negative value should throw");
    }

    @Test
    public void testPackNonInteger() throws Exception {
        Path file = tempDir.resolve("non_int.txt");
        Files.write(file, "1\n2\nabc\n4\n5\n6\n7\n8\n".getBytes());

        assertThrows(InvalidPackException.class, () -> {
            Pack.load(file, 1);
        }, "Pack with non-integer should throw");
    }
        private Path writeFile(String name, String text) throws Exception {
        Path file = tempDir.resolve(name);
        Files.write(file, text.getBytes());
        return file;
    }

    @Test
    public void testMissingFileIsInvalid() throws Exception {
        Path file = writeFile("missing.txt", "1\n2\n3\n4\n5\n6\n7\n8\n");

        assertThrows(InvalidPackException.class, () -> {
            Pack.load(file, 1);
        });
    }

    @Test
    public void testZeroValuesAreAllowed() throws Exception {
        Path file = writeFile("zeros.txt", "0\n0\n0\n0\n0\n0\n0\n");

        List<Card> cards = Pack.load(file, 1);

        assertEquals(8, cards.size());
    }

    @Test
    public void testValuesBiggerThanPlayerCountAreAllowed() throws Exception {
        Path file = writeFile("big.txt", "100\n200\n300\n400\n500\n600\n700\n800\n");

        List<Card> cards = Pack.load(file, 1);

        assertEquals(100, cards.get(0).getValue());
        assertEquals(800, cards.get(8).getValue());
    }

    @Test
    public void testPackKeepsTheOrderOfTheFile() throws Exception {
        Path file = writeFile("order.txt", "8\n7\n6\n5\n4\n3\n2\n1\n");

        List<Card> cards = Pack.load(file, 1);

        assertEquals(1, cards.get(0).getValue());
        assertEquals(8, cards.get(7).getValue());
    }

    @Test
    public void testBlankLineIsInvalid() throws Exception {
        Path file = writeFile("blank.txt", "1\n2\n\n4\n5\n6\n7\n8\n");

        try {
            Pack.load(file, 1);
        } catch (InvalidPackException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testZeroPlayersIsInvalid() throws Exception {
        Path file = writeFile("four.txt", "1\n2\n3\n4\n");

        assertThrows(InvalidPackException.class, () -> {
            Pack.load(file, 0);
        });
    }
}