//CharPuzGenerator.java
package xyz.davidchangx.puzzle;
import java.io.File;
import java.util.TreeMap;
import xyz.davidchangx.puzzle.Generator;
public class CharPuzGenerator
{
	public void static main(String[] args)
	{
		TreeMap<Character,TreeMap<String,Integer>> dictionary;

		File dictFile = new File("Dict.txt");
		Scanner s = new Scanner(dictFile);
		for(;s.hasNext();)
		{
			String str = s.next();
			for(int i = 0,n = str.length();i<n;i++)
			{
				char c = str.charAt(i);
				if(!dictionary.containsKey(Character.valueOf(c)))
					dictionary.put(c,new TreeMap<String,Integer>());
				dictionary.get(Character.valueOf(c)).put(str,i);
			}
		}

		Generator g = new Generator(dictionary);
		g.setSize(10,10);
		for(int i = 0;i<10;i++)
		{
			System.out.println("Time " + i);
			if(g.generate())
			{
				char[][] map = g.getMap();
				for(int j = 0;j<map.length;j++)
				{
					for(int k = 0;k<map[j].length;k++)
					{
						System.out.print((map[j][k]=='\0'?' ':map[j][k]) + " ");
					}
					System.out.println();
				}
				break;
			}
		}
	}
}
