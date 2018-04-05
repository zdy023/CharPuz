//Generator.java
package xyz.davidchangx.puzzle;
import java.util.HashMap;
//import java.util.ArrayList;
import java.util.ArrayDeque;
//import xyz.davidchangx.puzzle.Dict;
//import java.util.TreeMap;
import java.util.Arrays;
import java.util.Map;
import java.util.Iterator;
public class Generator
{
	public class StringStatus
	{
		String string; //字符串
		int hMax,hMin; //水平坐标范围
		int vMax,vMin; //竖直坐标范围
		boolean horiOrNot; //水平或竖直的判断
		//int connectedIndex; //已经连接过的字的节点

		
		StringStatus(String string,int hMax,int hMin,int vMax,int vMin,boolean horiOrNot)
		{
			this.string = string;
			this.hMax = hMax;
			this.hMin = hMin;
			this.vMax = vMax;
			this.vMin = vMin;
			this.horiOrNot = horiOrNot;
			//this.connectedIndex = -1;
		}

		public String getString()
		{
			return this.string;
		}
		public int getLeft()
		{
			return this.hMin;
		}
		public int getRight()
		{
			return this.hMax;
		}
		public int getTop()
		{
			return this.vMin;
		}
		public int getBottom()
		{
			return this.vMax;
		}
		public boolean isHorizontal()
		{
			return this.horiOrNot;
		}
	}

	/*public class StringProperty
	{
		int position;
		String string;

		public StringProperty(int position,String string)
		{
			this.position = position;
			this.string = string;
		}
	}*/
	
	//private Dict dictionary;
	//private TreeMap<Character,Treeset<StringProperty>> dictionary;
	private TreeMap<Character,TreeMap<String,Integer>> dictionary;
	private ArrayDeque<StringStatus> addedStrings;
	private boolean succeededOrNot;
	private int hMin,hMax,vMin,vMax;
	private char[][] map;
	int width,height;

	public Generator(TreeMap<Character,TreeMap<String,Integer>> dictionary)
	{
		this.dictionary = dictionary;
		addedStrings = new ArrayDeque<>();
		succeededOrNot = false;
		this.hMin = this.vMin = 0;
		this.hMax = this.vMax = -1;
		this.width = this.height = 0;
		this.map = new char[0][];
	}

	public void setSize(int width,int height)
	{
		this.width = width;
		this.height = height;
		this.map = new char[width<<1][height<<1];
		for(int i = 0;i<map.length;i++)
		{
			Arrays.fill(map[i],'\0');
		}
	}
	private boolean generate(int hMax,int hMin,int vMax,int vMin)
	{
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
			String firstOne = dictionary.getRandomOne();
			addedStrings.push(new StringStatus(firstOne,firstOne.length()-1,0,0,0,true));
			for(int i = 0,n = firstOne.length();i<n;i++)
				map[width+i][height] = firstOne.charAt(i);
			if(generate(firstOne.length()-1,0,0,0))
				return true;
			else
			{
				for(int i = 0,n = firstOne.length();i<n;i++)
					man[width+i][height] = '\0';
				addedStrings.pop();
				return false;
			}
		}
		TreeMap<String,Integer> relativeStrings;
		StringStatus presentStatus = addedStrings.peek();
		char[] singChars = presentStatus.string.toCharArray();
		for(int i = 0;i<singChars.length;i++) //search according to each char in present string
		{
			relativeStrings = dictionary.get(Character.valueOf(singChars[i]));
			Iterator<Map.Entry<String,Integer>> it = relativeStrings.entrySet().iterator();
			for(;it.hasNext();) //search according to each string having common char with present string
			{
				Map.Entry<String,Integer> entry = it.next();
				String nextString = entry.getKey();
				if(nextString.equals(presentStatus.string))
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
					if(;j<n;j++)
					{
						if(map[width+horiP][height+vertP]=='\0')
						{
							overlapOrNot = true;
							break;
						}
						map[width+horiP][height+vertP] = nextString.charAt(j);
						vertP++;
					}
					if(overlapOrNot)
					{
						j--;
						vertP--;
						for(;j>=0;j--)
							map[width+horiP][height+vertP] = '\0';
						continue;
					}

					int newVMin = presentStatus.vMax-index;
					addedStrings.push(new StringStatus(nextString,horiP,horiP,vertP-1,newVMin));
					if(generate(hMax,hMin,vMax<vertP-1?vertP-1:vMax,vMin>newVMin?newVMin:vMin))
						return true;
					else
					{
						StringStatus failedStatus = addedStrings.pop();
						vertP = failedStatus.vMin;
						horiP = failedStatus.hMin;
						for(j = 0;j<n;j++)
						{
							map[width+horiP][height+vertP] = '\0';
							vertP++;
						}
					}
				}
				else
				{
					vertP = presentStatus.vMin+i;
					horiP = presentStatus.hMax-index;
					if(vertP<-height||horiP<-width||vertP+nextString.length()-1>=height||horiP>=width)
						continue;
					if(;j<n;j++)
					{
						if(map[width+horiP][height+vertP]=='\0')
						{
							overlapOrNot = true;
							break;
						}
						map[width+horiP][height+vertP] = nextString.charAt(j);
						horiP++;
					}
					if(overlapOrNot)
					{
						j--;
						horiP--;
						for(;j>=0;j--)
							map[width+horiP][height+vertP] = '\0';
						continue;
					}

					int newHMin = presentStatus.hMax-index;
					addedStrings.push(new StringStatus(nextString,horiP-1,newHMin,vertP,vertP));
					if(generate(hMax<horiP-1?horiP-1:hMax,hMin>newHMin?newHMin:hMin,vMax,vMin))
						return true;
					else
					{
						StringStatus failedStatus = addedStrings.pop();
						vertP = failedStatus.vMin;
						horiP = failedStatus.hMin;
						for(j = 0;j<n;j++)
						{
							map[width+horiP][height+vertP] = '\0';
							horiP++;
						}
					}
				}
			}
		}
		return false;
	}
	public boolean generate()
	{
		return this.succeededOrNot = this.generate(-1,0,-1,0);
	}
	public char[][] getMap()
	{
		if(this.succeededOrNot)
		{
			char[][] realMap = new char[this.width][this.height];
			for(int i = 0;i<width;i++)
			{
				for(int j = 0;j<height;j++)
				{
					realMap[i][j] = map[width+hMin+i][height+vMin+j];
				}
			}
			return realMap;
		}
		else
			return null;
	}
}
