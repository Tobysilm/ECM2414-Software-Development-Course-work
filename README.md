# ECM2414 Card Game

A multi-threaded card game simulation in Java. There are n players and n decks in a ring. Each player runs on its own thread, and the first player to hold four cards of the same value wins.

## Requirements

- JDK 17 or later. The code is compiled with `--release 17`. It was built and tested with JDK 25 (OpenJDK).
- JUnit 5 (Jupiter), run with `junit-platform-console-standalone-1.14.4.jar`. No other dependencies.
- The JUnit jar is included in this zip at `lib/junit-platform-console-standalone-1.14.4.jar`. If it is missing, download it from:
  https://repo1.maven.org/maven2/org/junit/platform/junit-platform-console-standalone/1.14.4/junit-platform-console-standalone-1.14.4.jar

## Layout

- `src/` the classes: Card, CardDeck, CardGame, GameState, InvalidPackException, Pack, Player
- `test/` JUnit 5 test classes (CardTest, CardDeckTest, PackTest, GameStateTest, PlayerTest, CardGameTest) and the suite AllTests
- `packs/` example pack files (`four.txt` is a valid pack for 4 players)
- `lib/` the JUnit console jar
- `build/` compiled `.class` files

## Running the game

From the jar:

```
java -jar cards.jar
```

Or compile and run from the terminal. Run these from the folder that contains `src/`:

```
mkdir -p build/main
javac --release 17 -d build/main src/*.java
java -cp build/main CardGame
```

The program asks for the number of players, then for the location of a pack file:

```
Please enter the number of players:
4
Please enter location of pack to load:
packs/four.txt
```

If either input is invalid, the program says so and asks again. The game only starts once both are valid.

A pack file has one non-negative whole number per line and exactly 8n lines for n players.

Output is written to the folder the program is run from: `player1_output.txt` to `playerN_output.txt` and `deck1_output.txt` to `deckN_output.txt`. The winner is also printed to the screen, for example `player 4 wins`.

Note: if the pack does not contain four cards of the same value that a player can collect, the game never finishes. This is allowed by the specification. Stop it with Ctrl+C.

## Compiling and running the tests

Run these from the folder that contains `src/`, `test/` and `lib/`:

```
mkdir -p build/main build/test
javac --release 17 -d build/main src/*.java
javac --release 17 -cp build/main:lib/junit-platform-console-standalone-1.14.4.jar -d build/test test/*.java
java -jar lib/junit-platform-console-standalone-1.14.4.jar execute --class-path build/main:build/test --select-class AllTests
```

On Windows, use `;` instead of `:` in the class paths.

Expected result: 46 tests found and 46 tests successful. Some `player X wins` lines are printed while the tests run, because the tests play full games.