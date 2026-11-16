#!/bin/sh
set -eu
cd "$(dirname "$0")/javafx"
sh ./mvnw javafx:run
