#!/bin/bash
set -euo pipefail

pushd samples-repo >/dev/null
./gradlew --no-daemon clean check -Dorg.gradle.jvmargs="-Xmx512m -Xmx2048m"
popd >/dev/null
