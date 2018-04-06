//Generator.java
package xyz.davidchangx.puzzle;
import java.util.HashMap;
//import java.util.ArrayList;
import java.util.ArrayDeque;
//import xyz.davidchangx.puzzle.Dict;
import java.util.TreeMap;
import java.util.Arrays;
import java.util.Map;
import java.util.Iterator;
import java.util.Random;
import java.util.OptionalInt;
public class Generator
{
	public class StringStatus
	{
		String string; //字符串
		int hMax,hMin; //水平坐标范围
		int vMax,vMin; //竖直坐标范围
		boolean horiOrNot; //水平或竖直的判断
		OptionalInt connectedIndex; //已经连接过的字的节点

		
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

		public boolean equals(Object status)
		{
			if(!(status instanceof StringStatus))
				return false;
			return this.string.equals(((StringStatus)status).string);
		}
		public int hashCode()
		{
			return this.string.hashCode();
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
		System.out.println("node 3: " + this.width + " " + this.height);
		this.map = new char[width<<1][height<<1];
		for(int i = 0;i<map.length;i++)
		{
			Arrays.fill(map[i],'\0');
		}
	}
	private boolean generate(int hMax,int hMin,int vMax,int vMin,int deepLimit)
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
			System.out.println("node 4: " + firstOne + " " + (firstOne.length()-1) + " " + 0 + " " + 0 + " " + 0);
			if(firstOne.length()>=width)
			{
				addedStrings.pop();
				return false;
			}
			System.out.println("node 7");
			for(int i = 0,n = firstOne.length();i<n;i++)
				map[width+i][height] = firstOne.charAt(i);
			if(generate(firstOne.length()-1,0,0,0,deepLimit+1))
				return true;
			else
			{
				System.out.println("node 8");
				for(int i = 0,n = firstOne.length();i<n;i++)
					map[width+i][height] = '\0';
				//Roll-back
				addedStrings.pop();
				return false;
			}
		}
		//System.out.println("node 12");
		TreeMap<String,Integer> relativeStrings;
		StringStatus presentStatus = addedStrings.peek();
		char[] singChars = presentStatus.string.toCharArray();
		int con = presentStatus.connectedIndex.orElse(-5);
		for(int i = 0;i<singChars.length;i++) //search according to each char in present string
		{
			if(con==i||con-1==i||con+1==i)
				continue;
			relativeStrings = dictionary.get(Character.valueOf(singChars[i]));
			Iterator<Map.Entry<String,Integer>> it = relativeStrings.entrySet().iterator();
			for(;it.hasNext();) //search according to each string having common char with present string
			{
				Map.Entry<String,Integer> entry = it.next();
				String nextString = entry.getKey();
				//System.out.println("node 9: " + nextString);
				/*if(nextString.equals(presentStatus.string))
					continue;*/
				if(addedStrings.contains(new StringStatus(nextString,0,0,0,0,false)))
					continue;
				int index = entry.getValue();
				
				int j = 0,n = nextString.length(),vertP,horiP;
				boolean overlapOrNot = false;
				if(presentStatus.horiOrNot)
				{
					//System.out.println("node 10: " + nextString);
					vertP = presentStatus.vMax-index;
					horiP = presentStatus.hMin+i;
					//System.out.println("node 13: " + vertP + " " + horiP);
					if(vertP<-height||horiP<-width||vertP+nextString.length()-1>=height||horiP>=width)
						continue;
					//System.out.println("node 14");
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
						//System.out.println("node 15");
						map[width+horiP][height+vertP] = nextString.charAt(j);
						vertP++;
					}
					if((vertP<height)&&map[width+horiP][height+vertP]!='\0')
						overlapOrNot = true;
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
					} //Roll-back

					int newVMin = presentStatus.vMax-index;
					addedStrings.push(new StringStatus(nextString,horiP,horiP,vertP-1,newVMin,false,index));
					//System.out.println("node 5: " + nextString + " " + horiP + " " + horiP + " " + (vertP-1) + " " + newVMin);
					if(generate(hMax,hMin,vMax<vertP-1?vertP-1:vMax,vMin>newVMin?newVMin:vMin,deepLimit+1))
					{
						//System.out.println("node 18: " + nextString);
						return true;
					}
					else
					{
						//System.out.println("node 16: " + nextString);
						StringStatus failedStatus = addedStrings.pop();
						vertP = failedStatus.vMin;
						horiP = failedStatus.hMin;
						for(j = 0;j<n;j++,vertP++)
						{
							if(j==index)
								continue;
							map[width+horiP][height+vertP] = '\0';
						} //Roll-back
					}
				}
				else
				{
					//System.out.println("node 11: " + nextString);
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
						//System.out.println("node 15");
						map[width+horiP][height+vertP] = nextString.charAt(j);
						horiP++;
					}
					if((horiP<width)&&map[width+horiP][height+vertP]!='\0')
						overlapOrNot = true;
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
					} //Roll-back

					int newHMin = presentStatus.hMax-index;
					addedStrings.push(new StringStatus(nextString,horiP-1,newHMin,vertP,vertP,true,index));
					//System.out.println("node 6: " + nextString + " " + (horiP-1) + " " + newHMin + " " + vertP + " " + vertP);
					if(generate(hMax<horiP-1?horiP-1:hMax,hMin>newHMin?newHMin:hMin,vMax,vMin,deepLimit+1))
					{
						//System.out.println("node 19: " + nextString);
						return true;
					}
					else
					{
						//System.out.println("node 17: " + nextString);
						StringStatus failedStatus = addedStrings.pop();
						vertP = failedStatus.vMin;
						horiP = failedStatus.hMin;
						for(j = 0;j<n;j++,horiP++)
						{
							if(j==index)
								continue;
							map[width+horiP][height+vertP] = '\0';
						} //Roll-back
					}
				}
			}
		}
		return false;
	}
	public boolean generate()
	{
		return this.succeededOrNot = this.generate(-1,0,-1,0,1);
	}
	public char[][] getMap()
	{
		if(this.succeededOrNot)
		{
			addedStrings.stream().forEach((StringStatus x)->System.out.println(x.string + " " + x.hMax + " " + x.hMin + " " + x.vMax + " " + x.vMin));
			for(int i = 0;i<(height<<1);i++)
			{
				for(int j = 0;j<(width<<1);j++)
					System.out.printf("%1$2c ",map[j][i]=='\0'?'〇':map[j][i]);
				System.out.println();
			}
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
