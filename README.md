# epublib

Epublib is a java library for reading/writing/manipulating epub files.

## Changes from original epublib

This is a fork of a fork of the original. The original can be found [here](https://github.com/psiegman/epublib).

Other forks were also integrated in order to get other contributions.

Some significant changes were made in this fork:
 - The tools were removed and only the core was kept. Since there was no interesting in maintaining them.
 - The core subproject was moved to the root of the repository, just to keep it simple.
 - Java version was upgraded to 17 and javafx dependencies were removed from core.

## Objective

The objective of this fork is to create accessible epubs and to guarantee EPUB3 compatibility.

## How to build

````shell
./gradlew clean build
````

