package test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import Card;
import InvalidPackException;
import Pack;

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
        }, "Pack with non-integer should