//Generator.java
package xyz.davidchangx.puzzle;
import java.util.HashMap;
import java.util.ArrayList;
public class Generator
{
	class Status implements Comparable<Status>,Clonable
	{
		class StringStatus
		{
			int hMax,hMin; //水平坐标范围
			int vMax,vMin; //竖直坐标范围
			boolean horiOrNot; //水平或竖直的判断
			ArrayList<Integer> connectedIndex; //已经连接过的字的节点

			
			StringStatus(int hMax,int hMin,int vMax,int vMin,boolean horiOrNot)
			{
				this.hMax = hMax;
				this.hMin = hMin;
				this.vMax = vMax;
				this.vMin = vMin;
				this.horiOrNot = horiOrNot;
				this.connectedIndex = new ArrayList<>(4);
			}
			StringStatus(int hMax,int hMin,int vMax,int vMin,boolean horiOrNot,int connectedIndex)
			{
				this(hMax,hMin,vMax,vMin,horiOrNot);
				this.connectedIndex.add(connectedIndex);
			}
		}

		int n; //总长度
		int hMax,hMin; //水平坐标范围
		int vMax,vMin; //竖直坐标范围
		int nIdioms; //成语总数
		HashMap<String,StringStatus> strings;

		int appraise;

		Status(int n,String initStr)
		{
			this.n = n;
			this.hMax = initStr.length()-1;
			this.hMin = 0;
			this.vMax = 0;
			this.vMin = 0;
			nIdioms = 1;

			strings = new HashMap<>();
			strings.put(initStr,new StringStatus(0,this.hMin,0,0,true));
			
			this.appraise = getValue();
		}

		private int getValue()
		{
			int hSize = hMax-hMin+1;
			int vSize = vMax-vMin+1;
			int appraise;
			if(hSize>=vSize)
				appraise = 10*vSize-(hSize<<1)+6*(n-1)+nIdioms;
			else
				appraise = 10*hSize-(vSize<<1)+6*(n-1)+nIdioms;
			//appraise = 4(hSize+vSize)+6(n-1-abs(hSize-vSize))+nIdioms
			return appraise;
		}

		Status nextStatus(String prev,int prevIndex,String next,int nextIndex);
		public int compareTo(Status status);

		public boolean equals(Object status);
		public int hashCode();

		public Object clone();
	}
}
