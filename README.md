# ECM2414-Software-Development-Course-work
*Requirments* 
jdk17 or later compiled with --release 17.
juni5- junit-platform-console=standalone-1.14-4.jar it is included in cardsTest.zip under lib/. 
if missing dowload from https://repo1.maven.org/maven2/org/junit/platform/junit-platform-console-standalone/1.14.4/junit-platform-console-standalone-1.14.4.jar

*layout*
src/    production classes (Card, CardDeck, Player, GameState, pack
InvaalidPackExeption, CardGame)
test/   JUnit 5 test classes 
packs/ example pack files
lib/    JUnit console launcher jar

*Running The Game*
From the jar:
java -jar cards.jar

or compile and run from terminal 
mkdir -p build/main
javac --release 17 -d build/main src/*.java
java -cp build/main CardGame

Program will ask for number of players and then location of pack file e.g

===========================================================================

Please enter the number of players:
4
Please enter location of pack to load:
packs/four.txt

===========================================================================

*Compiling and running the tests*
run these from the folder that contains src/, test/ and lib/:
===========================================================================

mkdir -p build/main build/test
javac --release 17 -d build/main src/*.java
javac --release 17 -cp build/main:lib/junit-platform-console-standalone-1.14.4.jar -d build/test test/*.java
java -jar lib/junit-platform-console-standalone-1.14.4.jar execute \
  --class-path build/main:build/test --select-class AllTests
  
===========================================================================