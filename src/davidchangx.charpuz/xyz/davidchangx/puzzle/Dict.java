//Dict.java
package xyz.davidchangx.puzzle;
import java.util.ListIterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
class Dict
{
	private HashMap<String,ArrayList<String>> map;

	Dict(HashMap<String,ArrayList<String>> map)
	{
		this.map = (HashMap<String,ArrayList<String>>)map.clone();
	}
	Dict()
	{
		this.map = new HashMap<>();
	}

	String getRandomOne()
	{
		String[] strs = this.map.keySet().toArray(new String[0]);
		return strs[(new Random()).nextInt(strs.length)];
	}
	ArrayList<String> getNext(String idiom)
	{
		return this.map.get(idiom);
	}

	public boolean equals(Object dict)
	{
		if(!(dict instanceof Dict))
			return false;
		return this.map.equals(((Dict)dict).map);
	}
	public int hashCode()
	{
		return this.map.hashCode();
	}
}
