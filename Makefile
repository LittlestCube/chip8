all: submodule
	javac *.java

run:
	java Kumquat

clean:
	rm *.class
	rm -r littlecube

submodule: clean
	git submodule update --init --recursive --remote --merge
	git submodule foreach git pull origin master
	cp -r unsigned/littlecube littlecube