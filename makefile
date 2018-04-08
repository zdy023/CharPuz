#!/usr/bin/make -f
src = src/davidchangx.charpuz/xyz/davidchangx/puzzle
opt = opt/davidchangx.charpuz/xyz/davidchangx/puzzle
javac = $$JAVA9_HOME/bin/javac --module-source-path src -d opt
java = $$JAVA9_HOME/bin/java -p opt -m

#target = Dict MapDict
target = Generator CharPuzGenerator CharPuzGUI
.PHONY: all exe test
all: $(addsuffix .class,$(addprefix $(opt)/,$(target)))
exe: $(opt)/CharPuzGenerator.class
test: $(addsuffix .class,$(addprefix $(opt)/,$(target)))
	$(java)  davidchangx.charpuz/xyz.davidchangx.puzzle.CharPuzGenerator

#$(opt)/MapDict.class: $(opt)/Dict.class
$(opt)/CharPuzGenerator.class: $(opt)/Generator.class $(opt)/CharPuzGUI.class

$(opt)/%.class: $(src)/%.java
	$(javac) $<

.PHONY: clean image jar
image: all
	$$JAVA9_HOME/bin/jlink -p $$JAVA9_HOME/jmods:opt --add-modules davidchangx.charpuz --launcher CharPuz=davidchangx.charpuz/xyz.davidchangx.puzzle.CharPuzGenerator --output image
.ONESHELL: jar
jar: all
	cd opt/davidchangx.charpuz
	$$JAVA9_HOME/bin/jar --create --file=davidchangx.charpuz.jar -p $$JAVA9_HOME/jomds --module-version=1.0 --main-class xyz.davidchangx.puzzle.CharPuzGenerator .
	mv davidchangx.charpuz.jar ../..
clean:
	- rm -rf opt/
	- rm -rf image/
