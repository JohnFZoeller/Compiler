#!/bin/bash

javac *.java;
java Main > sample.txt;
python interpreter.py sample.txt;

$SHELL