package edu.miamioh.nguyenq2;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;

public class Chess extends JFrame {
	
	//The buttons to hold positions of 64 squares on the board
	private JButton[][] squares;
	//An array of current queens on the board
	private Queen[] queens;
	//Label notifying user of what's happening on the board
	private JLabel warning;
	//Label to specify how many "remake" moves are available
	private JLabel moveLabel;
	//Size of the frame
	private static final int FRAME_WIDTH = 700;
	private static final int FRAME_HEIGHT = 700;
	//List of buttons available for each purpose
	private JButton check;
	private JButton tip;
	private JButton smartTip;
	private JButton reset;
	//State whether the current queens on the board are in danger or not
	private boolean isDangerous = false;
	//Array List to hold all the possible solutions
	private ArrayList<PartialSolution> solutions;
	private String header = "ABCDEFGH";
	private int moveLeft = 5;

	// Default constructor
	public Chess() throws FontFormatException, IOException {
		//Initialize the arrays
		squares = new JButton[8][8];
		queens = new Queen[8];
		solutions = new ArrayList<PartialSolution>();
		//Get all the solutions
		getSolutions(new PartialSolution(0));
		//Create the chess board
		createChessBoard();
		setSize(FRAME_WIDTH, FRAME_HEIGHT);
	}
	
	//Create the layout of the chess board 
	public void createChessBoard() throws FontFormatException, IOException {
		JPanel finalPanel = new JPanel();
		//Set the layout of the overall panel
		finalPanel.setLayout(new BorderLayout());
		JPanel centerPanel = new JPanel();
		centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
		//Add each components into the panel following its layout
		centerPanel.add(upperBorder());
		centerPanel.add(boardSquares());
		finalPanel.add(leftBorder(), BorderLayout.WEST);
		finalPanel.add(centerPanel, BorderLayout.CENTER);
		finalPanel.add(labelPanel(), BorderLayout.SOUTH);
		finalPanel.add(toolBar(), BorderLayout.PAGE_START);
		//Add the panel to the frame
		add(finalPanel);
	}

	// Create the left border of the chess board
	public JPanel leftBorder() throws FontFormatException, IOException {
		//Create a panel to hold all the labels presenting the rows of the chess board
		JPanel panel = new JPanel();
		//Set the layout of the panel
		panel.setLayout(new GridLayout(8, 1));
		//Set the color of the panel
		panel.setBackground(Color.decode("#336699"));
		//Create 8 labels and assign specific numbers to represent the rows
		for (int i = 0; i < 8; i++) {
			JLabel label = new JLabel(i+1+"",SwingConstants.CENTER);
			//Set the font of the label
			label.setFont(newFont("Baloo-Regular.ttf"));
			label.setForeground(Color.white);
			//Add the label to the panel
			panel.add(label);
		}
		panel.setPreferredSize(new Dimension(30, 600));
		return panel;
	}

	// Create the upper border of the chess board
	public JPanel upperBorder() throws FontFormatException, IOException {
		//Create a panel to hold all the letter specifying the columns of the chess board
		JPanel panel = new JPanel();
		//Set the layout of the panel
		panel.setLayout(new GridLayout(0, 8));
		//Set the background of the panel
		panel.setBackground(Color.decode("#336699"));
		for (int i = 0; i < 8; i++) {
			//Create 8 labels specifying letters of the columns
			JLabel label = new JLabel(header.substring(i, i + 1), SwingConstants.CENTER);
			label.setFont(newFont("Baloo-Regular.ttf"));
			label.setForeground(Color.white);
			panel.add(label);
		}
		panel.setPreferredSize(panel.getPreferredSize());
		return panel;
	}

	// Create the 64 squares of the chessboard
	public JPanel boardSquares() {
		//Create a label to hold the chess board's squares
		JPanel panel = new JPanel();
		//Set the layout of the panel
		panel.setLayout(new GridLayout(8, 8));
		for (int i = 0; i < squares.length; i++) {
			for (int j = 0; j < squares[i].length; j++) {
				//Initialize the square and add an action listener to it
				squares[i][j] = new JButton();
				squares[i][j].setPreferredSize(new Dimension(69, 69));
				squares[i][j].addActionListener(new ClickListener());
				//Color the square according to its coordinates
				resetBackground(i, j);
				panel.add(squares[i][j]);
			}
		}
		return panel;
	}

	// Create a new tool bar to hold the buttons
	public JToolBar toolBar() throws FontFormatException, IOException {
		//Create a new tool bard to hold all the buttons
		JToolBar bar = new JToolBar();
		bar.setFloatable(false);
		bar.setLayout(new FlowLayout(FlowLayout.CENTER));
		//Initialize the buttons and add action listener to them
		check = new JButton("Check");
		check.setFont(newFont("Baloo-Regular.ttf"));
		check.addActionListener(new CheckListener());
		
		tip = new JButton("Tip");
		tip.setFont(newFont("Baloo-Regular.ttf"));
		tip.addActionListener(new TipListener());
		
		smartTip = new JButton("Smart Tip");
		smartTip.setFont(newFont("Baloo-Regular.ttf"));
		smartTip.addActionListener(new SmartTipListener());
		
		reset = new JButton("Reset Game");
		reset.setFont(newFont("Baloo-Regular.ttf"));
		reset.addActionListener(new ResetListener());
		
		//Add the buttons to the tool bar
		bar.add(reset);
		bar.add(check);
		bar.add(tip);
		bar.add(smartTip);
		
		return bar;
	}
	
	//Import new fonts for the application
	public Font newFont(String fileName) throws FontFormatException, IOException{
		//Get the file of the font
		File fontFile = new File(".\\image\\"+fileName);
		//Create the font and set its size
		Font font = Font.createFont(Font.TRUETYPE_FONT, fontFile).deriveFont(14f);
		return font;
	}
	
	// Create a panel to textually notify user
	public JPanel labelPanel() throws FontFormatException, IOException {
		
		//Initialize the label to notify user of current status
		warning = new JLabel();
		warning.setFont(newFont("Baloo-Regular.ttf"));
		warning.setForeground(Color.YELLOW);
		
		//Initialize the label specifying how many "remake" moves they have
		moveLabel = new JLabel();		
		moveLabel.setFont(newFont("Baloo-Regular.ttf"));
		moveLabel.setText("Moves left: " + moveLeft);
		moveLabel.setForeground(Color.WHITE);
		
		//Add the label to the panel
		JPanel bottomPanel = new JPanel();
		bottomPanel.setBackground(Color.decode("#336699"));
		bottomPanel.setPreferredSize(new Dimension(30, 35));
		bottomPanel.add(warning);
		bottomPanel.add(moveLabel);
		return bottomPanel;
	}

	//Listens each time the user clicks on a square in a chess board
	class ClickListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			//Get the button that was clicked
			JButton button = (JButton) event.getSource();
			//Store its coordinates in an array
			int[] a = getPosition(button);
			//If the button doesn't have an icon
			if (button.getIcon() == null) {
				//If there aren't 8 queens on the board, place an icon into that square
				if (!isFull()) {
					button.setIcon(new ImageIcon(".\\image\\5e9af3fcdb.png"));
					//Add a new queen into the array of current queens on the board
					for (int i = 0; i < 8; i++) {
						if (queens[i] == null) {
							queens[i] = new Queen(a[0], a[1]);
							break;
						}
					}
				//If it is full, then textually warns the user of the info
				} else
					warning.setText("You can place only 8 queens on the board!");
			//If there is already an icon
			} else {
				//If the user still have chances for a remake move, then erase the icon
				if (moveLeft > 0) {
					//If it's an endangered queen, reset the background of every square
					if (button.getBackground().equals(Color.red)) {
						resetAll();
					}
					//Remove that queen from the list of current queens on the board
					for (int i = 0; i < 8; i++) {
						if (queens[i] != null && queens[i].getRow() == a[0] && queens[i].getColumn() == a[1]) {
							queens[i] = null;
							break;
						}
					}
					button.setIcon(null);
					//Decrement the number of remake moves, the user has left
					moveLabel.setText("Moves left: " + --moveLeft);
				//If the user runs out of remake move, warns the user and don't delete the icon
				} else {
					warning.setText("You've run out of number of redoing");
				}

			}
			//Reset the background of the squares that is clicked
			resetBackground(a[0], a[1]);
		}
	}

	// Get the position of the queen;
	public int[] getPosition(JButton button) {
		//Initialize an array to hold the coordinates of the queens
		int[] a = new int[2];
		//Search through the whole board and find that queen
		for (int i = 0; i < squares.length; i++) {
			for (int j = 0; j < squares[i].length; j++) {
				if (squares[i][j].equals(button)) {
					a[0] = i;
					a[1] = j;
					break;
				}
			}
		}
		return a;
	}

	//Listens when the user clicks on the "Check" button
	class CheckListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			warn();
		}
	}

	// Alert of the event happening on the board
	public void warn() {
		isDangerous = false;
		//Iterate through the list of current queens on the board
		for (int i = 0; i < queens.length; i++) {
			if (queens[i] != null) {
				//If a queen is safe, then reset the background of that square
				if (isSafe(queens[i]))
					resetBackground(queens[i].getRow(), queens[i].getColumn());
				else {
					//If not, make the background color of the endangered queens into red
					squares[queens[i].getRow()][queens[i].getColumn()].setBackground(Color.RED);
					isDangerous = true;
				}
			}
		}
		//If there are no threats with the current queens
		if (!isDangerous) {
			//If 8 queens are placed on the board, congrats the user for winning
			if (isFull()){
				warning.setText("Congratulations! You've won the game! Press 'Reset' to play it again!");
			}
			//Else, notify that their solution is good
			else {
			warning.setText("Everything looks good!");
			}
			//Reset background of every squares
			resetAll();
			//If it is dangerous, notify the user of the threat textually
		} else {
			warning.setText("You have placed your queen in a dangerous position!");
		}
	}

	// Reset the background of a square
	public void resetBackground(int row, int column) {
		//Get the column and row of the square and set it to the specific colors in a zig zag style
		if ((row%2==0 && column%2==0) || (row%2!=0 && column%2!=0)){
			squares[row][column].setBackground(Color.decode("#CC6600"));
		}
		else{
			squares[row][column].setBackground(Color.decode("#FFFF66"));
		}
	}

	// Reset all the background of the chess
	public void resetAll() {
		//Iterate through the whole chess board and reset the background of every square
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				resetBackground(i, j);
			}
		}
	}

	// Check if a queen is safe
	public boolean isSafe(Queen a) {
		//Select a queen and compare it to other active queens on the board
		//Check if it is endangered or not with the current solution on the board
		for (int i = 0; i < queens.length; i++) {
			if (queens[i] != null && !queens[i].equals(a) && queens[i].attacks(a))
				return false;
		}
		return true;
	}

	// Check if all the queens are safe
	public boolean allSafe() {
		//Check if all the queens are safe
		for (Queen b : queens) {
			if (b != null && !isSafe(b))
				return false;
		}
		//If they are, then reset the background of every square
		resetAll();
		return true;
	}

	// Check if 8 queens are placed on the board
	public boolean isFull() {
		//Iterate through the arrays and check if every queen in the list
		//is of "null" type or not
		for (Queen i : queens) {
			if (i == null)
				return false;
		}
		//Return true if there are 8 queens on the board
		return true;
	}

	// Check if a queen is already selected
	public boolean isSelected(Queen a) {
		//Iterate through the whole board to check if a queen is selected or not
		for (Queen b : queens) {
			if (b != null && b.equals(a))
				return true;
		}
		return false;
	}

	// Listens when the user clicks the tip button
	class TipListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			warning.setText("");
			if (allSafe())
				isDangerous = false;
			else
				isDangerous = true;
			//If there are less than 8 queens on the board and they are not in danger
			//Find a safe square to put the next queen in
			if (!isFull() && !isDangerous) {
				//Put the outerloop notation outside to break out of it (Cited from StackOverflow)
				outerloop: for (int i = 0; i < 8; i++) {
					for (int j = 0; j < 8; j++) {
						Queen a = new Queen(i, j);
						if (isSafe(a) && !isSelected(a)) {
							squares[i][j].setBackground((Color.GREEN));
							String position = i + 1 + header.substring(j, j + 1);
							warning.setText("There's a move you can take! It's in location " + position);
							break outerloop;
						}
					}
				}
				//If there aren't any proposed square, then notify the users that there aren't any
				//safe move the user can take next
				if (warning.getText().equals(""))
					warning.setText("Sorry, no move available!");
				//If the queens are in danger, notify the user to remove the threat first
			} else if (isDangerous) {
				warning.setText("Sorry! It seems like a queen is in danger now! Remove the threat first!");
			}
		}
	}

	// Smart Tip

	// Get the current queens on the board (aka sort the queens)
	public ArrayList<Queen> getCurrentQueens() {
		//Get the list of currently active queens on the board
		ArrayList<Queen> a = new ArrayList<Queen>();
		for (int i = 0; i < queens.length; i++) {
			if (queens[i] != null)
				a.add(queens[i]);
		}
		return a;
	}

	// Get the solutions that have the current queens on the board
	public ArrayList<PartialSolution> getSortedSolutions() {
		// Create the update solution arraylist
		ArrayList<PartialSolution> updatedSolution = new ArrayList<PartialSolution>();
		//Search through the whole lists of possible solutions
		for (PartialSolution p : solutions) {
			//If that solutions have the current queens in it, then add that to the 
			//updated list of solutions
			if (containsSolution(p)) {
				updatedSolution.add(p);
			}
		}
		return updatedSolution;
	}

	// check if a solution contains all the current queens
	public boolean containsSolution(PartialSolution p) {
		// List of current queens
		ArrayList<Queen> a = getCurrentQueens();
		Queen[] b = p.getQueen();
		//Check if the solution have the currently active queens in its solution or not
		for (Queen i : a) {
			for (int j = 0; j < b.length; j++) {
				if (b[j].equals(i))
					break;
				else {
					if (j == b.length - 1)
						return false;
				}
			}
		}
		return true;
	}

	// Get the next queen to be place on a smartTip
	public Queen getNextQueen(ArrayList<PartialSolution> p) {
		//Create a Map ADT to store the number of appearance of each possible moves in the
		//list of possible solutions
		Map<Queen, Integer> map = new HashMap<Queen, Integer>();
		ArrayList<Queen> a = getCurrentQueens();
		Queen next;
		int largest = 1;
		for (PartialSolution i : p) {
			for (Queen b : i.getQueen()) {
				//Get the queens in the solution that are not on the board already
				if (!isSelected(b)) {
					//Put it in the map and initialize its appearance to 1
					if (!map.containsKey(b)) {
						map.put(b, 1);
					} else {
						//If it has already appear, increment its appearance
						if (largest <= map.get(b))
							largest = map.get(b) + 1;
						map.put(b, map.get(b) + 1);
					}
				}
			}
		}
		//Search the map for the queens that has the largest appearance
		//Get that queen and return that queen
		for (Map.Entry<Queen, Integer> e : map.entrySet()) {
			if (e.getValue() == largest) {
				next = e.getKey();
				return next;
			}
		}
		return null;
	}

	// Smart Tip
	class SmartTipListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			//Only works if the board is not full
			if (!isFull()) {
				//Get the list of updated solutions
				ArrayList<PartialSolution> a = getSortedSolutions();
				//If the list isn't empty and the queens aren't in danger
				if (!a.isEmpty() &&!isDangerous) {
					//Get the next queen and notify the user by coloring the background 
					//of that square to be green
					Queen next = getNextQueen(a);
					squares[next.getRow()][next.getColumn()].setBackground(Color.GREEN);
					String position = next.getRow() + 1 + header.substring(next.getColumn(), next.getColumn() + 1);
					warning.setText("Seems like you're on the right track! Put a queen at location " + position);
				} 
				//Notify the user if the queens on the board are in danger
				else if (isDangerous){
					warning.setText("Your queens are in danger. Remove the threat first!");
				}
				//Notify if there aren't any hint
				else {
					warning.setText("Your move is leading to a dead end!");
				}
			}
		}
	}

	// Get all the possible solutions for the n-queen problem
	public void getSolutions(PartialSolution sol) {
		//Get the current status of the solution
		int exam = sol.examine();
		//If it's an accepted one, add it to the list
		if (exam == PartialSolution.ACCEPT) {
			solutions.add(sol);
		//If it's correct, but still not complete, recursively check to find the solution
		} else if (exam != PartialSolution.ABANDON) {
			for (PartialSolution p : sol.extend()) {
				getSolutions(p);
			}
		}
	}

	//Listens when the user clicks the reset button
	class ResetListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			//Reset all the background of the squares
			resetAll();
			//Set all the squares to have 0 icons
			for (int i = 0; i < queens.length; i++) {
				if (queens[i]!=null){
					squares[queens[i].getRow()][queens[i].getColumn()].setIcon(null);
				}
				queens[i] = null;
			}
			//Reset the remake opportunities to be 5 again
			moveLeft = 5;
			//Reset all the label
			moveLabel.setText("Moves left: " + moveLeft);
			warning.setText("");
			//Reset the status of all queens
			isDangerous = false;
		}
	}

}
