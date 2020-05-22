all: submodule dev
	jar cvfe Chip8.jar Chip8 *

dev:
	javac *.java

run:
	java Chip8 "tetris.ch8"

clean: cleansub
	rm *.class || continue
	rm -r littlecube || continue

submodule: clean
	git submodule update --init --recursive --remote --merge
	git submodule foreach git pull origin master
	cp -r unsigned/littlecube littlecube

cleansub:
	git submodule deinit --all --force