//Generator.java
package xyz.davidchangx.puzzle;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.ArrayDeque;
import java.util.TreeMap;
import java.util.Arrays;
import java.util.Map;
import java.util.Iterator;
import java.util.Random;
import java.util.OptionalInt;
/**
 * The puzzle generator.
 *
 * This puzzle generator uses dictionary in form like {@code TreeMap<Character,TreeMap<String,Integer>>}. The {@code Character} is needed as possible common character of several strings. The {@String} is considered as the string. The {@code Integer} is the position of the character in this string. So this structure means, a specific charater is in a string, and it is on the position of index {@Integer}. 
 *
 * @author David Chang
 * @version 1.2
 */
public class Generator
{
	/**
	 * Stores the status of a string like horizontal or vertical coordinates and etc during dfs. 
	 *
	 * @author David Chang
	 * @version 1.2
	 */
	public class StringStatus implements Cloneable //for the status of the string like horizontal or vertical coordinates and etc.
	{
		String string; //字符串
		int hMax,hMin; //水平坐标范围
		int vMax,vMin; //竖直坐标范围
		boolean horiOrNot; //水平或竖直的判断
		OptionalInt connectedIndex; //已经连接过的字的节点

		
		/**
		 * Constructs a {@code StringStatus} object. 
		 *
		 * @param string the string
		 * @param hMax the horizontal coordinate of the last character
		 * @param hMin the horizontal coordinate of the first character
		 * @param vMax the horizontal coordinate of the last character
		 * @param vMin the horizontal coordinate of the first character
		 * @param horiOrNot if this string is placed horizontally or vertically
		 */
		StringStatus(String string,int hMax,int hMin,int vMax,int vMin,boolean horiOrNot)
		{
			this.string = string;
			this.hMax = hMax;
			this.hMin = hMin;
			this.vMax = vMax;
			this.vMin = vMin;
			this.horiOrNot = horiOrNot;
			this.connectedIndex = OptionalInt.empty();
		}
		/**
		 * Constructs a {@code StringStatus} object. 
		 *
		 * @param string the string
		 * @param hMax the horizontal coordinate of the last character
		 * @param hMin the horizontal coordinate of the first character
		 * @param vMax the horizontal coordinate of the last character
		 * @param vMin the horizontal coordinate of the first character
		 * @param horiOrNot if this string is placed horizontally or vertically
		 * @param connectedIndex the index of the common character with previous string
		 */
		StringStatus(String string,int hMax,int hMin,int vMax,int vMin,boolean horiOrNot,int connectedIndex)
		{
			this.string = string;
			this.hMax = hMax;
			this.hMin = hMin;
			this.vMax = vMax;
			this.vMin = vMin;
			this.horiOrNot = horiOrNot;
			this.connectedIndex = OptionalInt.of(connectedIndex);
		}

		/**
		 * Get the string. 
		 *
		 * @return the string
		 */
		public String getString()
		{
			return this.string;
		}
		/**
		 * Get the horizontal coordinate of the first character. 
		 *
		 * @return the horizontal coordinate of the first character
		 */
		public int getLeft()
		{
			return this.hMin;
		}
		/**
		 * Get the horizontal coordinate of the last character. 
		 *
		 * @return the horizontal coordinate of the last character
		 */
		public int getRight()
		{
			return this.hMax;
		}
		/**
		 * Get the vertical coordinate of the first character. 
		 *
		 * @return the vertical coordinate of the first character
		 */
		public int getTop()
		{
			return this.vMin;
		}
		/**
		 * Get the vertical coordinate of the last character. 
		 *
		 * @return the vertical coordinate of the last character
		 */
		public int getBottom()
		{
			return this.vMax;
		}
		/**
		 * Check if this string is placed horizontally. 
		 *
		 * @return if this string is placed horizontally
		 */
		public boolean isHorizontal()
		{
			return this.horiOrNot;
		}

		/**
		 * Compares this object with another object. 
		 *
		 * We simply considered two {@code StringStatus} objects equal when the strings of these two objects equal, but don't consider the other properties. 
		 *
		 * @return if this object equals another object
		 */
		@Override
		public boolean equals(Object status)
		{
			if(!(status instanceof StringStatus))
				return false;
			return this.string.equals(((StringStatus)status).string);
		}
		/**
		 * Get the hash code of this object. 
		 *
		 * We simply use the hash code of the string as the hash code of the {@code StringStatus} object. 
		 *
		 * @return the hash code of the {@code StringStatus} object
		 */
		@Override
		public int hashCode()
		{
			return this.string.hashCode();
		}
		
		/**
		 * Returns a clone of this object. 
		 *
		 * @return a clone of this object
		 */
		@Override
		public Object clone()
		{
			StringStatus stringStatus = new StringStatus(this.string,this.hMax,this.hMin,this.vMax,this.vMin,this.horiOrNot);
			this.connectedIndex.ifPresentOrElse(x->{stringStatus.connectedIndex = OptionalInt.of(x);},()->{stringStatus.connectedIndex = OptionalInt.empty();});
			return stringStatus;
		}
	}

	private TreeMap<Character,TreeMap<String,Integer>> dictionary;
	private ArrayDeque<StringStatus> addedStrings;
	private boolean succeededOrNot; //the flag to check if the puzzle is generated successfully
	private int hMin,hMax,vMin,vMax;
	private char[][] map; //the map to save the full puzzle
	private int width,height; //the width and the height of the logical target-map

	/**
	 * Constructs a puzzle generator with given dictionary. 
	 * 
	 * @param dictionary the dictionary in need
	 */
	public Generator(TreeMap<Character,TreeMap<String,Integer>> dictionary)
	{
		this.dictionary = dictionary;
		addedStrings = new ArrayDeque<>();
		succeededOrNot = false;
		this.hMin = this.vMin = 0;
		this.hMax = this.vMax = -1;
		this.width = this.height = 0;
		this.map = null;
	}

	/**
	 * Set the size of the target puzzle and clear the previous result remained. 
	 *
	 * Our puzzle generation method won't clear the generation result (so that the invoker can get the result for times after generation). So it's necessary for the invoker to invoke this method first to guarantee that the result of the last generation should be cleared when he wants to generate a new puzzle using {@link generate()}, or unpredictable result may occur. 
	 *
	 * @param width the width of target puzzle
	 * @param height the height of target puzzle
	 */
	public void setSize(int width,int height) //set the size of target puzzle and clear the previous result, whenever the invoker wants to generate a new puzzle by invoking generate(), he should invoke this method first to guarantee that the result of the last generation should be cleared or it might cause unpredictable wrong generation result
	{
		this.width = width;
		this.height = height;
		this.map = new char[width<<1][height<<1];
		for(int i = 0;i<map.length;i++)
		{
			Arrays.fill(map[i],'\0');
		}
		addedStrings.clear();
	}
	/**
	 * Change the dictionary used for generation. 
	 *
	 * {@code Generator} never create a copy of the dictionary, it simply get the reference of the dictionary object and use it. 
	 *
	 * About the dictionary's structure, please refer to {@link Generator}. 
	 *
	 * @param dictionary the new dictionary
	 */
	public void setDictionary(TreeMap<Character,TreeMap<String,Integer>> dictionary)
	{
		this.dictionary = dictionary;
	}
	private boolean generate(int hMax,int hMin,int vMax,int vMin,int deepLimit) //dfs to generate a puzzle
	{
		if(deepLimit>=100)
			return false;
		if(hMax-hMin+1==width&&vMax-vMin+1==height)
		{
			this.hMax = hMax;
			this.hMin = hMin;
			this.vMax = vMax;
			this.vMin = vMin;
			return true;
		}
		if(hMax-hMin+1>width||vMax-vMin+1>height)
		{
			return false;
		}
		if(addedStrings.isEmpty())
		{
			Random r = new Random();
			TreeMap[] char_Strings = dictionary.values().toArray(new TreeMap[0]);
			Object[] firstOptions = char_Strings[r.nextInt(char_Strings.length)].keySet().toArray();
			String firstOne = (String)firstOptions[r.nextInt(firstOptions.length)];
			addedStrings.push(new StringStatus(firstOne,firstOne.length()-1,0,0,0,true));
			if(firstOne.length()>=width)
			{
				addedStrings.pop();
				return false;
			}
			for(int i = 0,n = firstOne.length();i<n;i++)
				map[width+i][height] = firstOne.charAt(i);
			//try to put the idiom into the map
			if(generate(firstOne.length()-1,0,0,0,deepLimit+1))
				return true;
			else
			{
				for(int i = 0,n = firstOne.length();i<n;i++)
					map[width+i][height] = '\0';
				//Roll-back when failed
				addedStrings.pop();
				return false;
			}
		}
		TreeMap<String,Integer> relativeStrings;
		StringStatus presentStatus = addedStrings.peek();
		char[] singChars = presentStatus.string.toCharArray();
		int con = presentStatus.connectedIndex.orElse(-5);
		for(int i = 0;i<singChars.length;i++) //search according to each char in present string
		{
			if(con==i||con-1==i||con+1==i)
				continue;
			//prevent the appearence of the condition that two parrallel idioms are next to each other or trying putting the idiom to a place where there is already an idiom
			relativeStrings = dictionary.get(Character.valueOf(singChars[i]));
			Iterator<Map.Entry<String,Integer>> it = relativeStrings.entrySet().iterator();
			for(;it.hasNext();) //search according to each string having common char with present string
			{
				Map.Entry<String,Integer> entry = it.next();
				String nextString = entry.getKey();
				if(addedStrings.contains(new StringStatus(nextString,0,0,0,0,false)))
					continue;
				int index = entry.getValue();
				
				int j = 0,n = nextString.length(),vertP,horiP;
				boolean overlapOrNot = false;
				if(presentStatus.horiOrNot)
				{
					vertP = presentStatus.vMax-index;
					horiP = presentStatus.hMin+i;
					if(vertP<-height||horiP<-width||vertP+nextString.length()-1>=height||horiP>=width)
						continue;
					if((vertP-1>=-height)&&map[width+horiP][height+vertP-1]!='\0')
						continue;
					for(;j<n;j++)
					{
						if(j!=index)
						{
							if(map[width+horiP][height+vertP]!='\0'||(horiP-1>=-width&&map[width+horiP-1][height+vertP]!='\0')||(horiP+1<width&&map[width+horiP+1][height+vertP]!='\0'))
							{
								overlapOrNot = true;
								break;
							}
						}
						map[width+horiP][height+vertP] = nextString.charAt(j);
						vertP++;
					}
					if((vertP<height)&&map[width+horiP][height+vertP]!='\0')
						overlapOrNot = true;
					//try to put the idiom into the map
					if(overlapOrNot)
					{
						j--;
						vertP--;
						for(;j>=0;j--,vertP--)
						{
							if(j==index)
								continue;
							map[width+horiP][height+vertP] = '\0';
						}
						continue;
					} //Roll-back when failed

					int newVMin = presentStatus.vMax-index;
					addedStrings.push(new StringStatus(nextString,horiP,horiP,vertP-1,newVMin,false,index));
					if(generate(hMax,hMin,vMax<vertP-1?vertP-1:vMax,vMin>newVMin?newVMin:vMin,deepLimit+1))
					{
						return true;
					}
					else
					{
						StringStatus failedStatus = addedStrings.pop();
						vertP = failedStatus.vMin;
						horiP = failedStatus.hMin;
						for(j = 0;j<n;j++,vertP++)
						{
							if(j==index)
								continue;
							map[width+horiP][height+vertP] = '\0';
						} //Roll-back when failed
					}
				}
				else
				{
					vertP = presentStatus.vMin+i;
					horiP = presentStatus.hMax-index;
					if(vertP<-height||horiP<-width||vertP>=height||horiP+nextString.length()-1>=width)
						continue;
					if((horiP-1>=-width)&&map[width+horiP-1][height+vertP]!='\0')
						continue;
					for(;j<n;j++)
					{
						if(j!=index)
						{
							if(map[width+horiP][height+vertP]!='\0'||(vertP-1>=-height&&map[width+horiP][height+vertP-1]!='\0')||(vertP+1>height&&map[width+horiP][height+vertP+1]!='\0'))
							{
								overlapOrNot = true;
								break;
							}
						}
						map[width+horiP][height+vertP] = nextString.charAt(j);
						horiP++;
					}
					if((horiP<width)&&map[width+horiP][height+vertP]!='\0')
						overlapOrNot = true;
					//try to put the idiom into the map
					if(overlapOrNot)
					{
						j--;
						horiP--;
						for(;j>=0;j--,horiP--)
						{
							if(j==index)
								continue;
							map[width+horiP][height+vertP] = '\0';
						}
						continue;
					} //Roll-back when failed

					int newHMin = presentStatus.hMax-index;
					addedStrings.push(new StringStatus(nextString,horiP-1,newHMin,vertP,vertP,true,index));
					if(generate(hMax<horiP-1?horiP-1:hMax,hMin>newHMin?newHMin:hMin,vMax,vMin,deepLimit+1))
					{
						return true;
					}
					else
					{
						StringStatus failedStatus = addedStrings.pop();
						vertP = failedStatus.vMin;
						horiP = failedStatus.hMin;
						for(j = 0;j<n;j++,horiP++)
						{
							if(j==index)
								continue;
							map[width+horiP][height+vertP] = '\0';
						} //Roll-back when failed
					}
				}
			}
		}
		return false;
	}
	/**
	 * Generates a puzzle. 
	 *
	 * @return if the generation succeeds
	 */
	public boolean generate() //the interface for the other applications to invoke to generate a puzzle
	{
		return this.succeededOrNot = this.generate(-1,0,-1,0,1);
	}
	/**
	 * Get the puzzle in form of {@code char[][]} or null if last generation has failed. 
	 *
	 * @return the puzzle in form of {@code char[][]} or null if last generation has failed
	 */
	public char[][] getMap()
	{
		if(this.succeededOrNot)
		{
			char[][] realMap = new char[this.width][this.height];
			for(int i = 0;i<width;i++)
			{
				System.arraycopy(map[width+hMin+i],height+vMin,realMap[i],0,height);
			}
			return realMap;
		}
		else
			return null;
	}
	/**
	 * Returns the puzzle in form of list of {@link StringStatus}. 
	 *
	 * If the last generation has failed, the returned list will be empty. 
	 *
	 * If the list is not empty, the invoker may use the list of {@link StringStatus} to generate a proper form of puzzle by itself. 
	 *
	 * @return the list of {@link StringStatus}
	 */
	public StringStatus[] getIdiomList()
	{
		return addedStrings.stream().map((StringStatus x)->{
			StringStatus y = (StringStatus)x.clone();
			y.hMax -= hMin;
			y.hMin -= hMin;
			y.vMax -= vMin;
			y.vMin -= vMin;
			return y;
		}).toArray(StringStatus[]::new);
	}
}
