//CharPuzGenerator.java
package xyz.davidchangx.puzzle;
import java.io.File;
import java.util.TreeMap;
import xyz.davidchangx.puzzle.Generator;
import java.util.Scanner;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import xyz.davidchangx.puzzle.CharPuzGUI;
import java.io.IOException;
import javax.swing.JOptionPane;
public class CharPuzGenerator
{
	public static void main(String[] args)
	{
		try
		{
			TreeMap<Character,TreeMap<String,Integer>> dict;
			File binaryDictFile = new File("Dict.dict");
			File txtDictFile = new File("Dict.txt");
			if(binaryDictFile.exists())
				dict = CharPuzGUI.readDictionary(binaryDictFile);
			else if(txtDictFile.exists())
			{
				dict = CharPuzGUI.parseDictionary(txtDictFile);
				CharPuzGUI.writeDictionary(binaryDictFile,dict);
			}
			else
			{
				System.out.println("The default dict file does not exist, you should set the dict file first after you enter the application! ");
				JOptionPane.showMessageDialog(null,"默认的词典文件不存在，请再进入程序后首先设置您自己的词典文件。","词典文件不存在",JOptionPane.WARNING_MESSAGE);
				dict = null;
			}
			CharPuzGUI gui = new CharPuzGUI(dict);
		}
		catch(FileNotFoundException e)
		{
			System.out.println(e);
			System.out.println("请确认默认词库文件Dict.txt或Dict.dict存在，再重新启动程序。");
			System.exit(0);
		}
		catch(IOException e)
		{
			System.out.println(e);
			System.exit(0);
		}
	}
}
