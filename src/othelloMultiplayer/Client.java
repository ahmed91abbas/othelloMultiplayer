//package othelloMultiplayer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Client {
	private Othello othello;

	public Client() {
		startGUI();
	}

	public void startGUI() {
		JDialog dialog = new JDialog();
		dialog.setLocationRelativeTo(null);
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		dialog.setTitle("Reversi start menu");
		JPanel[] panelList = new JPanel[5];
		Font font = new Font("Serif", Font.PLAIN, 30);
		for (int i = 0; i < panelList.length; i++) {
			panelList[i] = new JPanel();
			panelList[i].setBackground(Color.decode("#E6CFB8"));
		}
		JLabel startLabel = new JLabel("*Connect to server*");
		startLabel.setFont(font);
		panelList[0].add(startLabel);
		JLabel chooseColorLabel = new JLabel("Choose color ");
		chooseColorLabel.setFont(font);
		String[] colors = new String[] { "Black", "White" };
		JComboBox<String> box = new JComboBox<String>(colors);
		box.setFont(font);
		panelList[1].add(chooseColorLabel);
		panelList[1].add(box);
		JLabel IPLabel = new JLabel("IP ");
		IPLabel.setFont(font);
		panelList[2].add(IPLabel);
		JTextField IPJta = new JTextField();
		IPJta.setFont(font);
		IPJta.setPreferredSize(new Dimension(120, 40));
		panelList[2].add(IPJta);
		JLabel portLabel = new JLabel("Port ");
		portLabel.setFont(font);
		panelList[3].add(portLabel);
		JTextField portJta = new JTextField();
		portJta.setFont(font);
		portJta.setPreferredSize(new Dimension(120, 40));
		panelList[3].add(portJta);

		JButton startButton = new JButton("Start");
		startButton.setFont(font);
		panelList[4].add(startButton);
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(5, 1));
		for (int i = 0; i < panelList.length; i++)
			panel.add(panelList[i]);
		dialog.add(panel);
		dialog.pack();
		dialog.setVisible(true);
		startButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					dialog.dispose();
//					ChatClient cc = new ChatClient(IPJta.getText(), Integer.parseInt(portJta.getText()));
					ChatClient cc = new ChatClient("localhost", 30000);
					if (cc.getNbrConnected() < 2)
						System.out.println("Waiting for player 2 to connect...");
					while (cc.getNbrConnected() != 2) {
						Thread.sleep(200);
					}
					String color = null;
					while(color == null) {
						color = cc.getColor();
					}
					boolean p1 = color.equals("Black");
					othello = new Othello(p1, cc);
					othello.createField();
					othello.allowAllMoves(true);
					othello.makeMove("d5");
					othello.makeMove("e5");
					othello.makeMove("e4");
					othello.makeMove("d4");
					othello.allowAllMoves(false);
				} catch (Exception e) {
					e.printStackTrace();
					System.out.println("An error has occurred");
					startGUI();
				}
			}
		});
	}

	public static void main(String[] args) {
		Client client = new Client();
	}
}
