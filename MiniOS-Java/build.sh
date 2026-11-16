#!/bin/sh
set -eu
cd "$(dirname "$0")"
mkdir -p out
javac -encoding UTF-8 -source 8 -target 8 -d out ./*.java
java -Djava.awt.headless=true -cp out Tests
java -Djava.awt.headless=true -cp out RubricTests
jar cfe MiniOS.jar MiniOS -C out .
