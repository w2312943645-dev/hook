#!/usr/bin/env bash
# build-support/make-xposed-stub.sh — compile + jar the compile-only Xposed stub.
#
#   * javac --release 17 over src/  ->  classes/
#   * jar  ->  xposed-stub.jar   (consumed by app/build.gradle.kts compileOnly)
#   * NOT packaged into the APK; runtime FQNs belong to the LSPosed host.
#
# Needs a JDK (JAVA_HOME or javac/jar on PATH). This box: JDK17 at
# /usr/lib/jvm/java-17-openjdk-arm64
set -eu
export JAVA_HOME=${JAVA_HOME:-/usr/lib/jvm/java-17-openjdk-arm64}
export PATH="$JAVA_HOME/bin:$PATH"
HERE="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$HERE"
rm -rf classes xposed-stub.jar
mkdir -p classes
find src -name '*.java' | sort > sources.txt
javac --release 17 -Xlint:none -d classes @sources.txt
jar --create --file xposed-stub.jar -C classes .
echo "stub jar -> $(pwd)/xposed-stub.jar"
