# TierMart - COE 528 (Object-Oriented Analysis & Design)
# State design pattern + design by contract. Needs a JDK (17+).

RELEASE ?= 17
SRC := $(shell find src -name '*.java')

.PHONY: help build demo run-cli run-gui clean

help:
	@echo "make build    - compile everything into out/"
	@echo "make demo     - run the scripted State-pattern self-check"
	@echo "make run-cli  - run the text (command-line) front-end"
	@echo "make run-gui  - run the Swing desktop front-end (needs a display)"
	@echo "make clean    - delete compiled classes"

build:
	mkdir -p out
	javac --release $(RELEASE) -d out $(SRC)

demo: build
	java -cp out tiermart.app.Main demo

run-cli: build
	java -cp out tiermart.app.Main cli

run-gui: build
	java -cp out tiermart.app.Main gui

clean:
	rm -rf out
