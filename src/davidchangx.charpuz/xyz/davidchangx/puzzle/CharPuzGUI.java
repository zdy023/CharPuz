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
public class CharPuzGUI extends JFrame
{
	private JMenu newGameMenu,setDictMenu;

	private PuzzlePainterPanel gameArea;

	private Box rightSidebarBox;
	private JLabel dictNameLabel;
	private JButton showAnswerButton;

	private final static int X = 60; //the lenth of a character grid
	private final static int S = 60; //the font size
	private final static int L = 300; //the width of sidebar
	private final static int H = 60; //the reserved space for the top of the window

	private Generator gn;
	private int puzzleX,puzzleY,blankPercentage;

	private FileFilter dictFileFilter,txtDictFileFilter;

	public CharPuzGUI(TreeMap<Character,TreeMap<String,Integer>> dictionary)
	{
		super("填字游戏");
		this.setSize((X<<5)+L,(X<<5)+H);

		this.gn = new Generator(dictionary);
		puzzleX = puzzleY = 0;
		blankPercentage = 0;

		dictFileFilter = new FileNameExtensionFilter("二进制词典文件","dict");
		txtDictFileFilter = new FileNameExtensionFilter("词典文本文件","txt");

		JMenuBar menuBar = new JMenuBar();
		newGameMenu = new JMenu("新游戏");
		newGameMenu.addActionListener(this::configNewGame);
		setDictMenu = new JMenu("设置词库");
		setDictMenu.addActionListener(this::setDictionary); 
		menuBar.add(newGameMenu);
		menuBar.add(setDictMenu);
		this.setJMenuBar(menuBar);

		this.setLayout(new BorderLayout());

		gameArea = new PuzzlePainterPanel();
		gameArea.setSize(X<<5,X<<5);
		this.add(gameArea,BorderLayout.CENTER);

		rightSidebarBox = new Box(BoxLayout.Y_AXIS);
		dictNameLabel = new JLabel("当前词库：某?全唐诗??");
		showAnswerButton = new JButton("显示答案");
		showAnswerButton.addActionListener(this::showAnswer);
		showAnswerButton.setEnabled(false);
		rightSidebarBox.add(dictNameLabel);
		rightSidebarBox.add(showAnswerButton);
		this.add(rightSidebarBox,BorderLayout.EAST);

		this.addWindowListener(new WindowAdapter() {
			public void windowClosint(WindowEvent e)
			{
				System.exit(0);
			}
		});
		this.setVisible(true);
	}

	public void setDictionary(TreeMap<Character,TreeMap<String,Integer>> dictionary)
	{
		this.gn.setDictionary(dictionary);
	}

	private void configNewGame(ActionEvent e)
	{
		NewGameDialog newGameDialog = new NewGameDialog();
		newGameDialog.setVisible(true);

		gn.setSize(puzzleX,puzzleY);
		for(int i = 0;i<10;i++)
		{
			if(gn.generate())
			{
				gameArea.realPuzzle = gn.getMap();
				gameArea.status = PuzzlePainterStatus.DRAW_PUZZLE;
				gameArea.draw();
				showAnswerButton.setEnabled(true);
				this.validate();
				return;
			}
		}
		JOptionPane.showMessageDialog(this,"生成失败……您可更换词库或调整区域大小后再次尝试。","生成失败",JOptionPane.INFORMATION_MESSAGE);
	}
	private void setDictionary(ActionEvent e)
	{
		JFileChooser dictFileChooser = new JFileChooser(".");
		dictFileChooser.addChoosableFileFilter(dictFileFilter);
		dictFileChooser.addChoosableFileFilter(txtDictFileFilter);
		dictFileChooser.setMultiSelectionEnabled(false);
		if(dictFileChooser.showOpenDialog(this)==JFileChooser.APPROVE_OPTION)
		{
			try
			{
				File dictFile = dictFileChooser.getSelectedFile();
				String dictFileName = dictFile.getName();
				TreeMap<Character,TreeMap<String,Integer>> dictionary;
				if(dictFileName.substring(dictFileName.length()-4).equals(".txt"))
				{
					dictionary = parseDictionary(dictFile);
					writeDictionary(new File(dictFileName.substring(0,dictFileName.length()-4) + ".dict"),dictionary);
				}
				else
					dictionary = readDictionary(dictFile);
				gn.setDictionary(dictionary);
				dictNameLabel.setText("当前词库：" + dictFileName.substring(0,dictFileName.length()-4));
			}
			catch(FileNotFoundException fnfe)
			{
				System.out.println(fnfe);
				JOptionPane.showMessageDialog(this,"词典打开失败，请检查您的词典文件，确认它存在并符合编码与格式要求","打开失败",JOptionPane.ERROR_MESSAGE);
			}
			catch(IOException ie)
			{
				System.out.println(ie);
				JOptionPane.showMessageDialog(this,"读写错误！","读写错误！",JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	private void showAnswer(ActionEvent e)
	{
		gameArea.status = PuzzlePainterStatus.DRAW_ANSWER;
		gameArea.draw();
		this.validate();
	}

	public static TreeMap<Character,TreeMap<String,Integer>> parseDictionary(File file,String charset) throws FileNotFoundException
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
	public static TreeMap<Character,TreeMap<String,Integer>> parseDictionary(File file) throws FileNotFoundException
	{
		TreeMap<Character,TreeMap<String,Integer>> dictionary = new TreeMap<>();
		Scanner s = new Scanner(file);
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
	public static TreeMap<Character,TreeMap<String,Integer>> readDictionary(File file) throws FileNotFoundException,IOException //read dictionary from a binary file
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
	public static void writeDictionary(File file,TreeMap<Character,TreeMap<String,Integer>> dictionary) throws FileNotFoundException,IOException //save dictionary into a binary file
	{
		ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
		oos.writeObject(dictionary);
		oos.flush();
		oos.close();
	}

	private class PuzzlePainterPanel extends JPanel
	{
		PuzzlePainterStatus status;
		private Random r;
		char[][] realPuzzle; //the full puzzle with answer

		PuzzlePainterPanel()
		{
			status = PuzzlePainterStatus.CLEAR_AREA;
			r = new Random();
			realPuzzle = null;
		}

		void draw()
		{
			this.removeAll();
			switch(status)
			{
				case CLEAR_AREA:
					return;
				case DRAW_PUZZLE:
					this.setLayout(new GridLayout(realPuzzle[0].length,realPuzzle.length));
					for(int j = 0;j<realPuzzle[0].length;j++)
					{
						for(int i = 0;i<realPuzzle.length;i++)
						{
							JTextField charTextField = new JTextField(2);
							if(realPuzzle[i][j]=='\0')
								charTextField.setEnabled(false);
							else
							{
								if(r.nextInt(100)>=blankPercentage)
								{
									charTextField.setText(String.valueOf(realPuzzle[i][j]));
									charTextField.setEditable(false);
								}
							}
							charTextField.setSize(X,X);
							this.add(charTextField);
						}
					}
					break;
				case DRAW_ANSWER:
					this.setLayout(new GridLayout(realPuzzle[0].length,realPuzzle.length));
					for(int j = 0;j<realPuzzle[0].length;j++)
					{
						for(int i = 0;i<realPuzzle.length;i++)
						{
							JTextField charTextField = new JTextField(2);
							if(realPuzzle[i][j]=='\0')
								charTextField.setEnabled(false);
							else
							{
								if(r.nextInt(100)>=blankPercentage)
								{
									charTextField.setText(String.valueOf(realPuzzle[i][j]));
									charTextField.setEditable(false);
								}
							}
							charTextField.setSize(X,X);
							this.add(charTextField);
						}
					}
					break;
			}
		}
	}

	private class NewGameDialog extends JDialog
	{
		private final static int L1 = 200; //the length of the input field
		private final static int HI = 60; //the height of the input field
		private final static int LL = 100; //the length of the labels
		private final static int L2 = 100; //the length of the button
		private final static int W = 400; //the width of dialog
		private final static int H = 300; //the height of dialog
		private final static int HF = 240; //the height of SplitPane

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
			super(CharPuzGUI.this,"新游戏",true);
			this.setSize(W,H);
			this.setLayout(new BorderLayout());

			ratingEditorSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,true);
			ratingEditorSplitPane.setSize(W,HF);
			ratingEditorSplitPane.setDividerLocation(.4);

			inputLabelBox = new Box(BoxLayout.Y_AXIS);
			xSizeLabel = new JLabel("宽度");
			xSizeLabel.setSize(LL,HI);
			ySizeLabel = new JLabel("高度");
			ySizeLabel.setSize(LL,HI);
			difficultyLabel = new JLabel("难易");
			difficultyLabel.setSize(LL,HI);
			inputLabelBox.add(xSizeLabel);
			inputLabelBox.add(ySizeLabel);
			inputLabelBox.add(difficultyLabel);

			inputFieldBox = new Box(BoxLayout.Y_AXIS);
			xSizeSetterSpinner = new JSpinner(new SpinnerNumberModel(15,10,30,1));
			xSizeSetterSpinner.setSize(L1,HI);
			ySizeSetterSpinner = new JSpinner(new SpinnerNumberModel(15,10,30,1));
			ySizeSetterSpinner.setSize(L1,HI);
			difficultySetterSlider = new JSlider(0,100,0);
			difficultySetterSlider.setSize(L1,HI);
			inputFieldBox.add(xSizeSetterSpinner);
			inputFieldBox.add(ySizeSetterSpinner);
			inputFieldBox.add(difficultySetterSlider);

			ratingEditorSplitPane.setLeftComponent(inputLabelBox);
			ratingEditorSplitPane.setRightComponent(inputFieldBox);
			this.add(ratingEditorSplitPane,BorderLayout.CENTER);

			dialogButtonsArea = new JPanel();
			dialogButtonsArea.setLayout(new FlowLayout(FlowLayout.CENTER));
			cancelButton = new JButton("取消");
			cancelButton.addActionListener((ActionEvent e)->NewGameDialog.this.dispose());
			confirmButton = new JButton("确认");
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
			this.dispose();
		}
	}
}
enum PuzzlePainterStatus
{
	DRAW_PUZZLE,DRAW_ANSWER,CLEAR_AREA;
}
