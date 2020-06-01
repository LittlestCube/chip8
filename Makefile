all: submodule dev
	jar cvfe Chip8.jar Chip8 *
	$(MAKE) clean-leavejar

dev: submodule
	javac *.java

run:
	java Chip8

submodule: clean
	git submodule update --init --recursive --remote --merge
	git submodule foreach git pull origin master
	cp -r unsigned/littlecube littlecube

clean: clean-leavejar
	rm *.jar || continue

clean-leavejar: cleansub
	rm *.class || continue
	rm -r littlecube || continue

cleansub:
	git submodule deinit --all --force