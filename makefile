#!/usr/bin/make -f

version=1.3

src = src/davidchangx.charpuz/xyz/davidchangx/puzzle
opt = opt/davidchangx.charpuz/xyz/davidchangx/puzzle
javac = javac --module-source-path src -d opt --module-version $(version)
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

.PHONY: clean image jar docs tar install
.ONESHELL: jar image tar
image: all
	- rm -rf image/
	jlink -p opt --add-modules davidchangx.charpuz --launcher CharPuz=davidchangx.charpuz/xyz.davidchangx.puzzle.CharPuzGenerator --output image

jar: all
	cd opt/davidchangx.charpuz
	jar --create --file=davidchangx.charpuz.jar --module-version=$(version) --main-class xyz.davidchangx.puzzle.CharPuzGenerator .
	mv davidchangx.charpuz.jar ../..
clean:
	- rm -rf opt/
	- rm -rf image/
	- rm -rf docs/
	- rm -rf davidchangx.charpuz.jar
	- rm -rf CharPuz.tar.gz
docs:
	javadoc --module davidchangx.charpuz --module-source-path src -p opt -d docs -html5 -version -author

tar: image
	- rm -rf CharPuz.tar.gz
	tar -czvf CharPuz.tar.gz image/
