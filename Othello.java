//package othelloMultiplayer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Othello {
	private HashMap<String, Disc> field;
	private int turnCount;
	private char[] letters;
	private HashMap<String, Disc> black;
	private HashMap<String, Disc> white;
	private HashMap<String, JButton> buttons;
	private ArrayList<String> availableMoves;
	private boolean allowAllMoves;
	private boolean whiteHasMoves;
	private boolean blackHasMoves;
	private boolean p1;
	private boolean p2;
	protected boolean gameOver;
	private boolean resultPrinted;
	private ChatClient cc;

	public Othello(boolean p1, ChatClient cc) {
		cc.setBoard(this);
		turnCount = 0;
		String str = " abcdefgh ";
		letters = str.toCharArray();
		field = new HashMap<String, Disc>();
		black = new HashMap<String, Disc>();
		white = new HashMap<String, Disc>();
		buttons = new HashMap<String, JButton>();
		availableMoves = new ArrayList<String>();
		allowAllMoves = false;
		whiteHasMoves = false;
		blackHasMoves = false;
		gameOver = false;
		this.p1 = p1;
		p2 = !p1;
		resultPrinted = false;
		this.cc = cc;
	}

	public void allowAllMoves(boolean state) {
		allowAllMoves = state;
	}

	public void makeMove(String name) {
		JButton button = buttons.get(name);
		int playerID = turnCount % 2;
		boolean succ = addDisc(name, playerID);
		int blackDiscs = black.keySet().size();
		int whiteDiscs = white.keySet().size();
		if (succ) {
			if(!allowAllMoves)
				cc.sendMsg("m:" + name);
			String nextPlayer = "";
			switch (playerID) {
			case 0:
				button.setBackground(Color.BLACK);
				nextPlayer = "white";
				break;
			case 1:
				button.setBackground(Color.WHITE);
				nextPlayer = "black";
				break;
			}

			if (blackDiscs + whiteDiscs == 64) {
				clearAvailableMoves();
				gameOver = true;
			}

			if (!gameOver) {
				System.out.println("Next is: " + nextPlayer + " (Black discs = " + blackDiscs + ", White discs = "
						+ whiteDiscs + ")");
				switchTurn();
			}

			if (gameOver && !resultPrinted) {
				if (blackDiscs > whiteDiscs) {
					System.out.println("\nThe winner is BLACK!");
				} else if (whiteDiscs > blackDiscs) {
					System.out.println("\nThe winner is WHITE!");
				} else {
					System.out.println("\nThe game finished with a tie!");
				}
				System.out.println("Black scored " + blackDiscs + " points.");
				System.out.println("White scored " + whiteDiscs + " points.");
				resultPrinted = true;
			}

			if (!allowAllMoves && !gameOver) {
				updateAvailableMoves();
			}
		}
	}

	private JButton createButton(final String name) {
		JButton button = new JButton();
		buttons.put(name, button);
		button.setPreferredSize(new Dimension(40, 40));
		button.setBackground(Color.green);
		button.setOpaque(true);
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int playerID = turnCount % 2;
				if ((playerID == 0 && p1) || (playerID == 1 && p2))
					makeMove(name);
			}
		});
		return button;
	}

	private JTextField createTextField(String txt) {
		Font font = new Font("SansSerif", Font.BOLD, 20);
		JTextField jt = new JTextField(txt);
		jt.setPreferredSize(new Dimension(40, 40));
		jt.setEditable(false);
		jt.setBackground(Color.LIGHT_GRAY);
		jt.setHorizontalAlignment(JTextField.CENTER);
		jt.setFont(font);
		return jt;
	}

	public void createField() {
		JFrame frame = new JFrame("Reversi");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		GridLayout layout = new GridLayout(10, 10);
		JPanel panel = new JPanel();
		panel.setLayout(layout);

		for (int row = 0; row < 10; row++) {
			for (int col = 0; col < 10; col++) {
				if (row == 0 || row == 9) {
					if (col == 0 || col == 9) {
						panel.add(createTextField(""));
					} else {
						panel.add(createTextField(letters[col] + ""));
					}
				} else if (col == 0 || col == 9) {
					panel.add(createTextField(row + ""));
				} else {
					String name = letters[col] + "" + row;
					panel.add(createButton(name));
				}
			}
		}

		frame.add(panel);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

	private void checkDirection(Disc core, int addToX, int addToY) {
		boolean checkNext = true;
		boolean foundDiff = false;
		boolean sameColor = false;
		int x = core.getX() + addToX;
		int y = core.getY() + addToY;
		while (checkNext && !outOfBounds(x, y)) {
			String name = letters[y] + "" + x;
			Disc nieghbour = field.get(name);
			if (nieghbour != null) {
				sameColor = nieghbour.getColor().equals(core.getColor());
			}
			if ((nieghbour == null && !foundDiff) || sameColor) {
				checkNext = false;
			} else if (nieghbour == null && foundDiff) {
				availableMoves.add(name);
				break;
			} else {
				foundDiff = true;
			}
			x = x + addToX;
			y = y + addToY;
		}
	}

	public void clearAvailableMoves() {
		for (int i = 0; i < availableMoves.size(); i++) {
			JButton button = buttons.get(availableMoves.get(i));
			button.setText("");
		}
		availableMoves.clear();
	}

	public void updateAvailableMoves() {
		clearAvailableMoves();
		int playerID = turnCount % 2;
		HashMap<String, Disc> player = null;
		switch (playerID) {
		case 0:
			player = black;
			break;
		case 1:
			player = white;
			break;
		}
		for (String key : player.keySet()) {
			Disc core = field.get(key);
			checkDirection(core, -1, 0); // up
			checkDirection(core, 1, 0); // down
			checkDirection(core, 0, -1); // left
			checkDirection(core, 0, 1); // right
			checkDirection(core, -1, -1); // up left
			checkDirection(core, 1, 1); // down right
			checkDirection(core, -1, 1); // up right
			checkDirection(core, 1, -1); // down left
		}
		for (int i = 0; i < availableMoves.size(); i++) {
			JButton button = buttons.get(availableMoves.get(i));
			button.setText("*");
		}
		if (availableMoves.size() > 0) {
			switch (playerID) {
			case 0:
				blackHasMoves = true;
				break;
			case 1:
				whiteHasMoves = true;
				break;
			}
		} else if (!allowAllMoves && !gameOver) {
			if (!whiteHasMoves && !blackHasMoves) {
				if (!resultPrinted) {
					System.out.println("\nThe game has finished because both players has no available moves left!");
					String winner = black.size() > white.size() ? "BLACK" : "WHITE";
					System.out.println("\nThe winner is " + winner);
					System.out.println("Black scored " + black.size() + " points.");
					System.out.println("White scored " + white.size() + " points.");
					resultPrinted = true;
				}
				gameOver = true;
			} else if (playerID == 0) {
				blackHasMoves = false;
				System.out.println("\nPlayer Black has no available moves! Switching turns");
				System.out.println(
						"Next is: white (Black discs = " + black.size() + ", White discs = " + white.size() + ")");

				switchTurn();
			} else if (playerID == 1) {
				whiteHasMoves = false;
				System.out.println("\nPlayer White has no available moves! Switching turns");
				System.out.println(
						"Next is: black (Black discs = " + black.size() + ", White discs = " + white.size() + ")");

				switchTurn();
			}
		}

	}

	private void flipDiscs(ArrayList<String> discsToFlip) {
		for (int i = 0; i < discsToFlip.size(); i++) {
			String key = discsToFlip.get(i);
			Disc disc = field.get(key);
			disc.flip();
			field.put(key, disc);
			if (white.containsKey(key)) {
				white.remove(key);
				black.put(key, disc);
			} else {
				black.remove(key);
				white.put(key, disc);
			}
			if (disc.getColor() == "black") {
				buttons.get(key).setBackground(Color.BLACK);
			} else {
				buttons.get(key).setBackground(Color.WHITE);
			}
		}
	}

	private ArrayList<String> update(Disc core, int addToX, int addToY) {
		ArrayList<String> discsToFlip = new ArrayList<String>();
		int x = core.getX() + addToX;
		int y = core.getY() + addToY;
		boolean foundDiff = false;
		boolean flip = false;
		String key = letters[y] + "" + x;
		while (!outOfBounds(x, y) && field.get(key) != null) {
			Disc next = field.get(key);
			foundDiff = !next.getColor().equals(core.getColor());
			if (foundDiff) {
				discsToFlip.add(key);
			} else {
				flip = true;
				break;
			}
			x = x + addToX;
			y = y + addToY;
			key = letters[y] + "" + x;
		}
		if (flip) {
			flipDiscs(discsToFlip);
		} else {
			discsToFlip.clear();
		}
		return discsToFlip;
	}

	public ArrayList<String> updateBoard(Disc core) {
		ArrayList<String> flippedDiscs = new ArrayList<String>();
		flippedDiscs.addAll(update(core, 1, 0)); // down
		flippedDiscs.addAll(update(core, -1, 0)); // up
		flippedDiscs.addAll(update(core, 0, 1)); // right
		flippedDiscs.addAll(update(core, 0, -1)); // left
		flippedDiscs.addAll(update(core, 1, 1)); // down right
		flippedDiscs.addAll(update(core, -1, -1)); // up left
		flippedDiscs.addAll(update(core, -1, 1)); // down left
		flippedDiscs.addAll(update(core, 1, -1)); // up right
		return flippedDiscs;
	}

	public boolean addDisc(String name, int playerID) {
		if (!allowAllMoves && (field.containsKey(name) || !availableMoves.contains(name))) {
			return false;
		} else {
			Disc disc = new Disc(playerID, name);
			field.put(name, disc);
			if (playerID == 0) {
				black.put(name, disc);
			} else {
				white.put(name, disc);
			}
			updateBoard(disc);
			return true;
		}
	}

	private boolean outOfBounds(int x, int y) {
		int rows = 8;
		int columns = 8;
		if (x < 1 || x > rows || y < 1 || y > columns)
			return true;
		return false;
	}

	public void switchTurn() {
		if (turnCount % 2 == 0) {
			turnCount = 1;
		} else {
			turnCount = 0;
		}
		updateAvailableMoves();
	}
}
