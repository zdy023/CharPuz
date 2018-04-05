//SufTreeDict.java
package xyz.davidchangx.puzzle;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Random;
class SufTreeDict implements Dict
{
	class Node
	{
		char singChar;
		TreeMap<Character,Node> nextChars;
		TreeSet<String> idioms;

		Node(char singChar)
		{
			this.singChar = singChar;
			this.nextChars = new TreeMap<>();
			this.idioms = new TreeSet<>(2);
		}

		void appendNode(char nextChar)
		{
			this.nextChars.put(nextChar,new Node(nextChar));
		}
		void addIdiom(String idiom)
		{
			this.idioms.add(idiom)
		}
	}

	private Node root;
	
	SufTreeDict()
	{
		this.root = new root('\0');
	}
	SufTreeDict(String... idioms);
	SufTreeDict(List<String> idioms);

	void addIdiom(String idiom)
	{
		char[] singChars = idiom.toCharArray();
		for(int i = 0;i<singChars.length;i++)
		{
			Node root = this.root;
			for(int j = i;j<singChars.length;j++)
			{
				if(!root.nextChars.containsKey(Character.valueOf(singChars[j])))
				{
					root.appendNode(singChars[j]);
				}
				root = root.nextChars.get(Character.valueOf(singChars[j]));
			}
			root.addIdiom(idiom);
		}
	}

	public String getRandomOne()
	{
		Node root = this.root;
		Random r = new Random();
		for(;!root.nextChars.isEmpty();)
		{
			if(!root.idioms.isEmpty())
			{
				if(r.nextBoolean())
				{
					String[] selectedStrings = root.idioms.toArray(new String[0]);
					return selectedStrings[r.nextInt(selectedStrings.length)];
				}
			}
			Node[] nextNodes = root.nextChars.values.toArray(new nextNodes[0]);
			root = nextNodes[r.nextInt(nextNodes.length)];
		}
		String[] selectedStrings = root.idioms.toArray(new String[0]);
		return selectedStrings[r.nextInt(selectedStrings.length)];
	}
	public TreeSet<String> getNext(String idiom)
	{
		TreeSet<String> nextOption = new ArrayList<>();
		for(;!root.nextChars.isEmpty();)
		{
		}
	}
			

	public boolean equals(Object dict);
	public int hashCode();
}
