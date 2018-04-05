src = src/davidchangx.charpuz/xyz/davidchangx/puzzle
opt = opt/davidchangx.charpuz/xyz/davidchangx/puzzle
javac = javac --module-source-path src -d opt

target = Dict

%.class: %.java
	$(javac) $@
