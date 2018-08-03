//CharPuzGUI.java
package xyz.davidchangx.puzzle;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JMenuBar;
import java.awt.event.ActionEvent;
import xyz.davidchangx.puzzle.Generator;
import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.Box;
import javax.swing.BoxLayout;
import java.awt.Container;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;
import javax.swing.JDialog;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.JFileChooser;
import java.util.TreeMap;
import java.io.File;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.Random;
import java.util.Scanner;
import javax.swing.JSplitPane;
import javax.swing.JSpinner;
import javax.swing.JSlider;
import javax.swing.JOptionPane;
import java.awt.GridLayout;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import java.awt.FlowLayout;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.awt.Color;
import javax.swing.JMenuItem;
import java.awt.Font;
import java.io.UnsupportedEncodingException;
/**
 * The GUI of the programme.
 *
 * @author David Chang
 * @version 1.2
 */
public class CharPuzGUI extends JFrame
{
	private JMenuItem newGameMenuItem,setDictMenuItem;

	private PuzzlePainterPanel gameArea;

	private Box rightSidebarBox;
	private JLabel dictNameLabel;
	private JButton showAnswerButton;

	private final static int X = 40; //the length of a character grid
	private final static int S = 20; //the font size of the text on the face panel
	private final static int MF = 18; //the font size of the text on menu bar
	private final static int FS = 30; //the font size of the text in puzzle
	private final static int L = 300; //the width of sidebar
	private final static int H = 60; //the reserved space for the top of the window

	private Generator gn;
	private int puzzleX,puzzleY,blankPercentage;

	private boolean generateNewGameOrNot;
	private FileFilter dictFileFilter,txtDictFileFilter,combinedDictFileFilter;
	private File lastDirectory;

	private String[] charsets;

	/**
	 * Construcsts a {@code CharPuzGUI}. 
	 *
	 * About the dictionary's structure, please refer to {@link Generator}. 
	 *
	 * @param dictionary the dictionary in need
	 * @param charsets the names of needed encodings
	 */
	public CharPuzGUI(TreeMap<Character,TreeMap<String,Integer>> dictionary,String[] charsets)
	{
		super("CharPuz - by David Chang");

		this.gn = new Generator(dictionary);
		puzzleX = puzzleY = 18;
		blankPercentage = 0;
		this.setSize(X*puzzleX+L,X*puzzleY+H);

		this.charsets = charsets;

		generateNewGameOrNot = false;
		dictFileFilter = new FileNameExtensionFilter("Binary Dictionary File","dict");
		txtDictFileFilter = new FileNameExtensionFilter("Plain Text Dictionary","txt");
		combinedDictFileFilter = new FileNameExtensionFilter("Dictionary File (*.dict;*.txt)","dict","txt");
		lastDirectory = new File(".");

		JMenuBar menuBar = new JMenuBar();
		JMenu gameMenu = new JMenu("Game");
		gameMenu.setFont(new Font(null,Font.PLAIN,MF));
		newGameMenuItem = new JMenuItem("New Game");
		newGameMenuItem.setFont(new Font(null,Font.PLAIN,MF));
		newGameMenuItem.addActionListener(this::configNewGame);
		setDictMenuItem = new JMenuItem("Change Dictionary");
		setDictMenuItem.setFont(new Font(null,Font.PLAIN,MF));
		setDictMenuItem.addActionListener(this::setDictionary); 
		gameMenu.add(newGameMenuItem);
		gameMenu.add(setDictMenuItem);
		menuBar.add(gameMenu);
		this.setJMenuBar(menuBar);

		this.setLayout(new BorderLayout());

		gameArea = new PuzzlePainterPanel();
		gameArea.setSize(X<<5,X<<5);
		this.add(gameArea,BorderLayout.CENTER);

		rightSidebarBox = new Box(BoxLayout.Y_AXIS);
		dictNameLabel = new JLabel("Current Dictionary: Dict(Default)");
		dictNameLabel.setFont(new Font(null,Font.PLAIN,S));
		showAnswerButton = new JButton("Show Answer");
		showAnswerButton.setFont(new Font(null,Font.PLAIN,S));
		showAnswerButton.addActionListener(this::showAnswer);
		showAnswerButton.setEnabled(false);
		rightSidebarBox.add(dictNameLabel);
		rightSidebarBox.add(showAnswerButton);
		this.add(rightSidebarBox,BorderLayout.EAST);

		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e)
			{
				System.exit(0);
			}
		});
		this.setVisible(true);
	}

	/**
	 * Change the use dictionary. 
	 *
	 * About the dictionary's structure, please refer to {@link Generator}. 
	 *
	 * @param dictionary the new dictionary
	 */
	public void setDictionary(TreeMap<Character,TreeMap<String,Integer>> dictionary)
	{
		this.gn.setDictionary(dictionary);
	}

	private void configNewGame(ActionEvent e) //handle the event to start a new game
	{
		NewGameDialog newGameDialog = new NewGameDialog();
		newGameDialog.setVisible(true);

		if(generateNewGameOrNot)
		{
			gn.setSize(puzzleX,puzzleY);
			for(int i = 0;i<10;i++)
			{
				if(gn.generate())
				{
					this.setSize(X*puzzleX+L,X*puzzleY+H);
					gameArea.realPuzzle = gn.getMap();
					gameArea.status = PuzzlePainterStatus.DRAW_PUZZLE;
					gameArea.draw();
					showAnswerButton.setEnabled(true);
					this.validate();
					return;
				}
			}
			//JOptionPane.showMessageDialog(this,"生成失败……您可更换词库或调整区域大小后再次尝试。","生成失败",JOptionPane.INFORMATION_MESSAGE);
			JOptionPane.showMessageDialog(this,"Generation failed. You can change a dictionary or adjust the size of puzzle and then retry. ","Generation Fail",JOptionPane.INFORMATION_MESSAGE);
		}
	}
	private void setDictionary(ActionEvent e) //handle the event to change the dictionary
	{
		JFileChooser dictFileChooser = new JFileChooser(".");
		dictFileChooser.setAcceptAllFileFilterUsed(false);
		dictFileChooser.addChoosableFileFilter(combinedDictFileFilter);
		dictFileChooser.addChoosableFileFilter(dictFileFilter);
		dictFileChooser.addChoosableFileFilter(txtDictFileFilter);
		dictFileChooser.setMultiSelectionEnabled(false);
		dictFileChooser.setCurrentDirectory(lastDirectory);
		if(dictFileChooser.showOpenDialog(this)==JFileChooser.APPROVE_OPTION)
		{
			try
			{
				File dictFile = dictFileChooser.getSelectedFile();
				lastDirectory = dictFile.getParentFile();
				String dictFileName = dictFile.getName();
				TreeMap<Character,TreeMap<String,Integer>> dictionary;
				if(dictFileName.substring(dictFileName.length()-4).equals(".txt"))
				{
					dictionary = parseDictionary(dictFile,this.charsets);
					writeDictionary(new File(dictFile.getParent() + dictFileName.substring(0,dictFileName.length()-4) + ".dict"),dictionary);
				}
				else
					dictionary = readDictionary(dictFile);
				gn.setDictionary(dictionary);
				dictNameLabel.setText("Current Dictionary: " + dictFileName.substring(0,dictFileName.lastIndexOf((int)'.')));
				JOptionPane.showMessageDialog(this,"Dictionary Update Finished! ","Notification",JOptionPane.INFORMATION_MESSAGE);
			}
			catch(FileNotFoundException fnfe)
			{
				System.out.println(fnfe);
				//JOptionPane.showMessageDialog(this,"词典打开失败，请检查您的词典文件，确认它存在并符合编码与格式要求","打开失败",JOptionPane.ERROR_MESSAGE);
				JOptionPane.showMessageDialog(this,"Dictionary opening failed. Please check your dictionary file's existing and ensure its encoding and format is correct.  ","Opening Fail",JOptionPane.ERROR_MESSAGE);
			}
			catch(IOException ie)
			{
				System.out.println(ie);
				//JOptionPane.showMessageDialog(this,"读写错误！","读写错误！",JOptionPane.ERROR_MESSAGE);
				JOptionPane.showMessageDialog(this,"I/O error! ","I/O Error! ",JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	private void showAnswer(ActionEvent e) //handle the event to show the answer of the puzzle
	{
		gameArea.status = PuzzlePainterStatus.DRAW_ANSWER;
		gameArea.draw();
	}

	/**
	 * Tool method to parse a text dictionary (with expand name "txt"). 
	 *
	 * @param file the text dictionary file
	 * @param charset the name of used encoding
	 * @return the tree map dictionary use in programme
	 * @throws FileNotFoundException if the dictionary file wasn't found
	 */
	public static TreeMap<Character,TreeMap<String,Integer>> parseDictionary(File file,String charset) throws FileNotFoundException //a tool method to parse a text dict file to a runtime dict
	{
		TreeMap<Character,TreeMap<String,Integer>> dictionary = new TreeMap<>();
		Scanner s = new Scanner(file,charset);
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
		return dictionary;
	}
	/**
	 * Tool method to parse a text dictionary (with expand name "txt"). 
	 *
	 * This method will try for several charsets until a proper encoding has found. 
	 *
	 * @param file the text dictionary file
	 * @param charsets the names of possible encodings
	 * @return the tree map dictionary use in programme
	 * @throws FileNotFoundException if the dictionary file wasn't found
	 * @throws UnsupportedEncodingException if no proper encoding has been found
	 */
	public static TreeMap<Character,TreeMap<String,Integer>> parseDictionary(File file,String[] charsets) throws FileNotFoundException,UnsupportedEncodingException //a tool method to parse a text dict file to a runtime dict
	{
		TreeMap<Character,TreeMap<String,Integer>> dictionary = new TreeMap<>();
		//String[] charsets = {"utf-8","gbk","utf-16"};
		Scanner s = null;
		{
			int i;
			for(i = 0;i<charsets.length;i++)
			{
				s = new Scanner(file,charsets[i]);
				if(s.hasNext())
					break;
			}
			if(i==3)
				throw new UnsupportedEncodingException("The scanner can scan the dict file as neither encoding configured in config file,  please ensure that your dict file is encoded in the correct encoding. ");
		}
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
		return dictionary;
	}
	/**
	 * Reads in a binary dictionary file (with expand name "dict" generated by this programme. 
	 *
	 * @param file the binary dictionary file
	 * @return the tree map dictionary use in programme
	 * @throws FileNotFoundException if the dictionary file wasn't found
	 * @throws IOException
	 */
	@SuppressWarnings({"unchecked"})
	public static TreeMap<Character,TreeMap<String,Integer>> readDictionary(File file) throws FileNotFoundException,IOException //a tool method to read a dict in from a binary dict file
	{
		try
		{
			ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(file)));
			TreeMap<Character,TreeMap<String,Integer>> dict = (TreeMap<Character,TreeMap<String,Integer>>)ois.readObject();
			ois.close();
			return dict;
		}
		catch(ClassNotFoundException e)
		{
			System.out.println(e);
			return null;
		}
	}
	/**
	 * Tool method to dump the tree map dictionary into a dict file. 
	 * 
	 * The dict file is a serialization of the tree map dictionary. 
	 *
	 * @param file the target binary dictionary file
	 * @param dictionary the dictionary to be serialized
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static void writeDictionary(File file,TreeMap<Character,TreeMap<String,Integer>> dictionary) throws FileNotFoundException,IOException //a tool method to save a dict into a binary dict file by serialization
	{
		ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
		oos.writeObject(dictionary);
		oos.flush();
		oos.close();
	}

	private class PuzzlePainterPanel extends JPanel //the panel to render the game
	{
		PuzzlePainterStatus status; //mark to determine the paint behavior of the panel
		private Random r;
		char[][] realPuzzle; //the full puzzle with answer
		private JTextField[][] userPuzzleTextFields; //the puzzle for user to play

		PuzzlePainterPanel()
		{
			status = PuzzlePainterStatus.CLEAR_AREA;
			r = new Random();
			realPuzzle = null;
		}

		void draw()
		{
			switch(status)
			{
				case CLEAR_AREA:
					this.removeAll();
					return;
				case DRAW_PUZZLE:
					this.removeAll();
					this.setLayout(new GridLayout(realPuzzle[0].length,realPuzzle.length));
					userPuzzleTextFields = new JTextField[puzzleX][puzzleY];
					for(int j = 0;j<realPuzzle[0].length;j++)
					{
						for(int i = 0;i<realPuzzle.length;i++)
						{
							userPuzzleTextFields[i][j] = new JTextField(2);
							if(realPuzzle[i][j]=='\0')
							{
								userPuzzleTextFields[i][j].setEnabled(false);
								userPuzzleTextFields[i][j].setBackground(Color.GRAY);
							}
							else
							{
								if(r.nextInt(100)>=blankPercentage)
								{
									userPuzzleTextFields[i][j].setText(String.valueOf(realPuzzle[i][j]));
									userPuzzleTextFields[i][j].setEditable(false);
									userPuzzleTextFields[i][j].setBackground(new Color(0x4d7a97));
								}
							}
							userPuzzleTextFields[i][j].setSize(X,X);
							userPuzzleTextFields[i][j].setFont(new Font(null,Font.PLAIN,FS));
							this.add(userPuzzleTextFields[i][j]);
						}
					}
					break;
				case DRAW_ANSWER:
					for(int j = 0;j<realPuzzle[0].length;j++)
					{
						for(int i = 0;i<realPuzzle.length;i++)
						{
							if(realPuzzle[i][j]!='\0'&&!userPuzzleTextFields[i][j].getText().equals(String.valueOf(realPuzzle[i][j])))
							{
								userPuzzleTextFields[i][j].setText(String.valueOf(realPuzzle[i][j]));
								userPuzzleTextFields[i][j].setBackground(Color.RED);
							}
							userPuzzleTextFields[i][j].setEditable(false);
						}
					}
					break;
			}
		}
	}

	private class NewGameDialog extends JDialog //dialog of the configuration of new game
	{
		private final static int L1 = 150; //the length of the input field
		private final static int HI = 30; //the height of the input field
		private final static int LL = 30; //the length of the labels
		private final static int LF = 18; //the font size of the labels
		private final static int L2 = 100; //the length of the button
		private final static int W = 180; //the width of dialog
		private final static int H = 130; //the height of dialog
		private final static int HF = 100; //the height of SplitPane

		private JSplitPane ratingEditorSplitPane;

		private Box inputLabelBox;
		private JLabel xSizeLabel,ySizeLabel,difficultyLabel;

		private Box inputFieldBox;
		private JSpinner xSizeSetterSpinner,ySizeSetterSpinner;
		private JSlider difficultySetterSlider;

		private JPanel dialogButtonsArea;
		private JButton cancelButton,confirmButton;

		NewGameDialog()
		{
			super(CharPuzGUI.this,"New Game",true);
			this.setSize(W,H);
			this.setLocation((CharPuzGUI.this.getWidth()>>1)-(W>>1),(CharPuzGUI.this.getHeight()>>1)-(H>>1));
			this.setLayout(new BorderLayout());

			ratingEditorSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,true);
			ratingEditorSplitPane.setSize(W,HF);
			ratingEditorSplitPane.setDividerLocation(.3);

			inputLabelBox = new Box(BoxLayout.Y_AXIS);
			xSizeLabel = new JLabel("Width");
			xSizeLabel.setSize(LL,HI);
			xSizeLabel.setFont(new Font(null,Font.PLAIN,LF));
			ySizeLabel = new JLabel("Height");
			ySizeLabel.setSize(LL,HI);
			ySizeLabel.setFont(new Font(null,Font.PLAIN,LF));
			difficultyLabel = new JLabel("Difficulty");
			difficultyLabel.setSize(LL,HI);
			difficultyLabel.setFont(new Font(null,Font.PLAIN,LF));
			inputLabelBox.add(xSizeLabel);
			inputLabelBox.add(ySizeLabel);
			inputLabelBox.add(difficultyLabel);

			inputFieldBox = new Box(BoxLayout.Y_AXIS);
			xSizeSetterSpinner = new JSpinner(new SpinnerNumberModel(puzzleX,10,30,1));
			xSizeSetterSpinner.setSize(L1,HI);
			ySizeSetterSpinner = new JSpinner(new SpinnerNumberModel(puzzleY,10,30,1));
			ySizeSetterSpinner.setSize(L1,HI);
			difficultySetterSlider = new JSlider(0,100,blankPercentage);
			difficultySetterSlider.setSize(L1,HI);
			inputFieldBox.add(xSizeSetterSpinner);
			inputFieldBox.add(ySizeSetterSpinner);
			inputFieldBox.add(difficultySetterSlider);

			ratingEditorSplitPane.setLeftComponent(inputLabelBox);
			ratingEditorSplitPane.setRightComponent(inputFieldBox);
			this.add(ratingEditorSplitPane,BorderLayout.CENTER);

			dialogButtonsArea = new JPanel();
			dialogButtonsArea.setLayout(new FlowLayout(FlowLayout.CENTER));
			cancelButton = new JButton("Cancle");
			cancelButton.addActionListener((ActionEvent e)->{
				NewGameDialog.this.dispose();
				generateNewGameOrNot = false;
			});
			confirmButton = new JButton("Ensure");
			confirmButton.addActionListener(this::setNewGameConfig);
			dialogButtonsArea.add(cancelButton);
			dialogButtonsArea.add(confirmButton);
			this.add(dialogButtonsArea,BorderLayout.SOUTH);
		}

		private void setNewGameConfig(ActionEvent e)
		{
			puzzleX = ((Number)(xSizeSetterSpinner.getValue())).intValue();
			puzzleY = ((Number)(ySizeSetterSpinner.getValue())).intValue();
			blankPercentage = difficultySetterSlider.getValue();
			generateNewGameOrNot = true;
			this.dispose();
		}
	}
}
/**
 * Status of {@link PuzzlePainterStatus} to mark to determine the paint behavior of the panel. 
 *
 * @author David Chang
 * @version 1.2
 */
enum PuzzlePainterStatus //status of PuzzlePainterPanel to mark to determine the paint behavior of the panel
{
	DRAW_PUZZLE,DRAW_ANSWER,CLEAR_AREA;
}
