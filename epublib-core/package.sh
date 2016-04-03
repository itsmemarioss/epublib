#!/bin/sh
mvn package -Dmaven.test.skip=true -Dmaven.javadoc.skip=true
cp ./target/epublib-core-3.1.1.jar ../../ebook-api/lib