//MapDict.java
package xyz.davidchangx.puzzle;
//import java.util.ListIterator;
//import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import xyz.davidchangx.Dict;
import java.util.TreeSet;
class MapDict implements Dict
{
	private HashMap<String,TreeSet<String>> map;

	MapDict(HashMap<String,TreeSet<String>> map)
	{
		this.map = (HashMap<String,TreeSet<String>>)map.clone();
	}
	MapDict()
	{
		this.map = new HashMap<>();
	}

	public String getRandomOne()
	{
		String[] strs = this.map.keySet().toArray(new String[0]);
		return strs[(new Random()).nextInt(strs.length)];
	}
	public TreeSet<String> getNext(String idiom)
	{
		return this.map.get(idiom);
	}

	public boolean equals(Object dict)
	{
		if(!(dict instanceof MapDict))
			return false;
		return this.map.equals(((MapDict)dict).map);
	}
	public int hashCode()
	{
		return this.map.hashCode();
	}
}
