.PHONY: all compile test docs clean

all: test

compile:
	mkdir -p bin
	javac -Xlint:all -d bin src/*.java

test: compile
	javac -cp bin -d bin test/*.java
	java -ea -cp bin BigTwoRulesTest

docs: compile
	javadoc -quiet -Xdoclint:none -d doc src/*.java

clean:
	find bin -name '*.class' -delete
