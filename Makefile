all: submodule
	javac *.java

run:
	java Kumquat

clean:
	rm *.class || continue
	rm -r littlecube || continue

submodule: clean
	git submodule update --init --recursive --remote --merge
	git submodule foreach git pull origin master
	cp -r unsigned/littlecube littlecube