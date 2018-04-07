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

.PHONY: clean
clean:
	- rm -rf opt/
