#!/usr/bin/make -f
src = src/davidchangx.charpuz/xyz/davidchangx/puzzle
opt = opt/davidchangx.charpuz/xyz/davidchangx/puzzle
javac = javac --module-source-path src -d opt

#target = Dict MapDict
target = Generator CharPuzGenerator

#$(opt)/MapDict.class: $(opt)/Dict.class
$(opt)/CharPuzGenerator.class: $(opt)/Generator.class

%.class: %.java
	$(javac) $@
