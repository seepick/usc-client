#!/bin/bash

./gradlew publishToMavenLocal -PlocalRelease

echo "Add dependency for 2000.0.SNAPSHOT (and don't forget mavenLocal())"
