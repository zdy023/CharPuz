//CharPuzGenerator.java
package xyz.davidchangx.puzzle;
import java.io.File;
import java.util.TreeMap;
import xyz.davidchangx.puzzle.Generator;
import java.util.Scanner;
import java.io.FileNotFoundException;
import java.io.PrintStream;
public class CharPuzGenerator
{
	public static void main(String[] args)
	{
		TreeMap<Character,TreeMap<String,Integer>> dictionary = new TreeMap<>();

		try
		{
			//File dictFile = new File("Dict.txt");
			File dictFile = new File("Strings.txt");
			//Scanner s = new Scanner(dictFile,"utf-16");
			Scanner s = new Scanner(dictFile);
			//PrintStream ps = new PrintStream("Strings.txt");
			System.out.println("node 2");
			for(;s.hasNext();)
			{
				String str = s.next();
				//System.out.println("node 1: " + str);
				//ps.println(str);
				for(int i = 0,n = str.length();i<n;i++)
				{
					char c = str.charAt(i);
					if(!dictionary.containsKey(Character.valueOf(c)))
						dictionary.put(c,new TreeMap<String,Integer>());
					dictionary.get(Character.valueOf(c)).put(str,i);
				}
			}
		}
		catch(FileNotFoundException e)
		{
			System.out.println(e);
			System.exit(0);
		}

		Generator g = new Generator(dictionary);
		g.setSize(15,15);
		for(int i = 0;i<1;i++)
		{
			System.out.println("Time " + i);
			if(g.generate())
			{
				char[][] map = g.getMap();
				for(int j = 0;j<map[0].length;j++)
				{
					for(int k = 0;k<map.length;k++)
					{
						System.out.printf("%1$2c ",map[k][j]=='\0'?'〇':map[k][j]);
						//System.out.print((map[j][k]=='\0'?' ':map[j][k]) + " ");
					}
					System.out.println();
				}
				System.exit(0);
			}
		}
		System.out.println("Failed");
	}
}
