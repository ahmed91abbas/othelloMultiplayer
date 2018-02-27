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
		dialog.setTitle("Othello start menu");
		JPanel[] panelList = new JPanel[4];
		Font font = new Font("Serif", Font.PLAIN, 30);
		for (int i = 0; i < panelList.length; i++) {
			panelList[i] = new JPanel();
			panelList[i].setBackground(Color.decode("#E6CFB8"));
		}
		JLabel startLabel = new JLabel("*Connect to server*");
		startLabel.setFont(font);
		panelList[0].add(startLabel);
		JLabel IPLabel = new JLabel("IP ");
		IPLabel.setFont(font);
		IPLabel.setPreferredSize(new Dimension(70, 40));
		panelList[1].add(IPLabel);
		JTextField IPJta = new JTextField();
		IPJta.setFont(font);
		IPJta.setPreferredSize(new Dimension(220, 40));
		panelList[1].add(IPJta);
		JLabel portLabel = new JLabel("Port ");
		portLabel.setFont(font);
		portLabel.setPreferredSize(new Dimension(70, 40));
		panelList[2].add(portLabel);
		JTextField portJta = new JTextField();
		portJta.setFont(font);
		portJta.setPreferredSize(new Dimension(220, 40));
		panelList[2].add(portJta);

		JButton startButton = new JButton("Start");
		startButton.setFont(font);
		panelList[3].add(startButton);
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(4, 1));
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
					CommunicationClient cc = new CommunicationClient(IPJta.getText(), Integer.parseInt(portJta.getText()));
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
