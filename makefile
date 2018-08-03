#!/usr/bin/make -f
src = src/davidchangx.charpuz/xyz/davidchangx/puzzle
opt = opt/davidchangx.charpuz/xyz/davidchangx/puzzle
javac = javac --module-source-path src -d opt
java = java -p opt -m

target = Generator CharPuzGenerator CharPuzGUI
.PHONY: all exe test
all: $(addsuffix .class,$(addprefix $(opt)/,$(target)))
exe: $(opt)/CharPuzGenerator.class
test: $(addsuffix .class,$(addprefix $(opt)/,$(target)))
	$(java)  davidchangx.charpuz/xyz.davidchangx.puzzle.CharPuzGenerator

$(opt)/CharPuzGenerator.class: $(opt)/Generator.class $(opt)/CharPuzGUI.class

$(opt)/%.class: $(src)/%.java
	$(javac) $<

.PHONY: clean image jar docs
image: all
	jlink -p opt --add-modules davidchangx.charpuz --launcher CharPuz=davidchangx.charpuz/xyz.davidchangx.puzzle.CharPuzGenerator --output image
.ONESHELL: jar
jar: all
	cd opt/davidchangx.charpuz
	jar --create --file=davidchangx.charpuz.jar --module-version=1.0 --main-class xyz.davidchangx.puzzle.CharPuzGenerator .
	mv davidchangx.charpuz.jar ../..
clean:
	- rm -rf opt/
	- rm -rf image/
	- rm -rf docs/
docs:
	javadoc --module davidchangx.charpuz --module-source-path src -p opt -d docs -html5 -version -author
