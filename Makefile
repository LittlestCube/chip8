all: submodule
	javac *.java

run:
	java Kumquat

clean:
	rm *.class

submodule:
	git submodule update --init --recursive --remote --merge
	cp -r unsigned/littlecube ./littlecube