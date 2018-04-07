//CharPuzGenerator.java
package xyz.davidchangx.puzzle;
import java.io.File;
import java.util.TreeMap;
import xyz.davidchangx.puzzle.Generator;
import java.util.Scanner;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import xyz.davidchangx.puzzle.CharPuzGUI;
public class CharPuzGenerator
{
	public static void main(String[] args)
	{
		try
		{
			File dictFile = new File("Dict.txt");
			TreeMap<Character,TreeMap<String,Integer>> dict = CharPuzGUI.parseDictionary(dictFile);
			CharPuzGUI gui = new CharPuzGUI(dict);
		}
		catch(FileNotFoundException e)
		{
			System.out.println(e);
			System.exit(0);
		}
	}
}
