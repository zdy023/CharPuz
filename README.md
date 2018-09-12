# CharPuz

This is a little programme of word puzzle game. There are several words horizontally or vertically placed in the rectangle area, and there are several blanks inside the words. The target of the game is to fill the correct missing character or letter in the blank so as to complete the puzzle. 

I use a map data structure like `<character(char),<word(String),position_of_the_character_in_word(int)>>` as the dictionary and use dfs to search in the status space until the first complete puzzle is found. 

## Have a Try

### Build the Programme

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

Also you can use `make jar` to generate a `jar` archive and use

```
java -jar davidchangx.charpuz.jar
```

to launch the programme. 

Or you can use `make image` to generate a image so that you can run the programme by command

```
image/bin/CharPuz
```

to run this game in an environment without JRE. 

### Build the Dictionary

To generate a game, you need a proper dictionary. This programme support dictionary form as plain text (.txt) and binary data (.dict). 

The plain text dictionary is supposed to be in form: 

```
worda
wordb
wordc
...
```

In each line, there is a word without space or other white space character in it. By default, the programme support encoding: UTF-8, GBK and UTF-16. But you can modify the `encoding` property in config file `~/.charpuz_config` to make the programme support more kinds of encodings. 

The binary dictionary is in form of serialization of dictionary `<Character,<String,int>>` and will be generated automatically if you choose a txt dictionary for the programme. 

By the way, the programme's running requires a default dictionary named `Dict.dict` or `Dict.txt` under the runtime path or the "dict_path" property set in config file, so you can rename your own dictionary as `Dict.txt` to load it while launching programme. And it's sure you can change dictionary in runtime. 
