# builds cards.jar and cardsTest.zip
# run from the main folder of the repo:  bash build.sh
set -e
J=lib/junit-platform-console-standalone-1.14.4.jar

rm -rf build cardsTest cards.jar cardsTest.zip MANIFEST.MF
mkdir -p build/main build/test

javac --release 17 -d build/main src/*.java
javac --release 17 -cp build/main:$J -d build/test test/*.java

printf 'Manifest-Version: 1.0\nMain-Class: CardGame\n' > MANIFEST.MF
jar cfm cards.jar MANIFEST.MF -C build/main . -C src .

mkdir -p cardsTest
cp -r src test packs build lib README.md cardsTest/
zip -qr cardsTest.zip cardsTest

echo "built cards.jar and cardsTest.zip"