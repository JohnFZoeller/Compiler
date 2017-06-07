#!/bin/bash

javac *.java
java Main > sample.txt
./interpreter.py sample.txt