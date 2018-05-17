# CharPuz

This is a little programme of word puzzle game. There are several words horizontally or vertically placed in the rectangle area, and there are several blanks inside the words. The target of the game is to fill the correct missing character or letter in the blank so as to complete the puzzle. 

I use a map data structure like &lt;character(char),&lt;word(String),position_of_the_character_in_word(int)&gt;&gt; as the dictionay and use dfs to search in the status space until the first complete puzzle is found. 

## Have a Try

You can clone this repository and compile it and have a try with this word puzzle game. 

```
git clone https://github.com/zdy023/CharPuz
make
```

You can use `make test` or

```
java -p opt -m davidchangx.charpuz/xyz.davidchangx.puzzle.CharPuzGenerator
```

after compilation to launch the programme. 

Also you can use `make jar` to generate a `jar` archieve and use

```
java -jar davidchangx.charpuz.jar
```

to launch the programme. 

Or you can use `make image` to generate a image so that you can run the programme by command

```
image/bin/CharPuz
```

to run this game in an environment without JRE (&gt;=9). 
