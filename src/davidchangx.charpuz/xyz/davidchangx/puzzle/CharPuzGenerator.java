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
import java.util.Properties;
import java.io.FileReader;
import java.io.BufferedReader;
/**
 * The main class of this programme.
 *
 * @author David Chang
 * @version 1.3
 */
public class CharPuzGenerator
{
	/**
	 * The main programme of the CharPuz game. 
	 */
	public static void main(String[] args)
	{
		try
		{
			String configFileName;
			if(args.length>=1)
				configFileName = args[0];
			else
				configFileName = System.getProperty("user.home") + System.getProperty("file.separator") + ".charpuz_config";
			Properties properties = new Properties();
			File configFile = new File(configFileName);
			if(configFile.exists())
				properties.load(new BufferedReader(new FileReader("config")));
			String encodingProperty = properties.getProperty("encoding","utf-8, gbk, utf-16");
			String[] altEncodings = encodingProperty.split("\\ *,\\ *");

			String initDir = properties.getProperty("dict_path",".");
			TreeMap<Character,TreeMap<String,Integer>> dict;
			File binaryDictFile = new File(initDir,"Dict.dict");
			File txtDictFile = new File(initDir,"Dict.txt");
			if(binaryDictFile.exists())
				dict = CharPuzGUI.readDictionary(binaryDictFile);
			else if(txtDictFile.exists())
			{
				dict = CharPuzGUI.parseDictionary(txtDictFile,altEncodings);
				CharPuzGUI.writeDictionary(binaryDictFile,dict);
			}
			else
			{
				System.out.println("The default dict file does not exist, you should set the dict file first after you enter the application! ");
				//JOptionPane.showMessageDialog(null,"默认的词典文件不存在，请在进入程序后首先设置您自己的词典文件。","词典文件不存在",JOptionPane.WARNING_MESSAGE);
				JOptionPane.showMessageDialog(null,"The default dictionary file does not exist, please set your own dictionary first after enterring programme. ","Dictionary file does not exist. ",JOptionPane.WARNING_MESSAGE);
				dict = null;
			}
			CharPuzGUI gui = new CharPuzGUI(dict,altEncodings);
			gui.initDictDirectory(new File(initDir));
		}
		catch(FileNotFoundException e)
		{
			System.out.println(e);
			//System.out.println("请确认默认词库文件Dict.txt或Dict.dict存在，再重新启动程序。");
			System.out.println("Please ensure the existing of Dict.txt or Dict.dict and restart the programme. ");
			System.exit(0);
		}
		catch(IOException e)
		{
			System.out.println(e);
			System.exit(0);
		}
	}
}
