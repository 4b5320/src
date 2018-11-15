import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.awt.Font;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.SystemColor;
import javax.swing.JButton;
import javax.swing.UIManager;
import javax.swing.JSplitPane;
import javax.swing.JRadioButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class guitest {

	private JFrame frame;
	private JTextField textField;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					guitest window = new guitest();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public guitest() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setResizable(false);
		frame.setBounds(100, 100, 150, 165);
		frame.getContentPane().setLayout(null);
		
		JLabel lblWhatIsYour = new JLabel("What is your vote?");
		lblWhatIsYour.setForeground(Color.BLACK);
		lblWhatIsYour.setFont(new Font("Times New Roman", Font.BOLD | Font.ITALIC, 15));
		lblWhatIsYour.setBounds(10, 10, 125, 23);
		frame.getContentPane().add(lblWhatIsYour);
		
		JButton btnNewButton = new JButton("GUILTY");
		btnNewButton.setForeground(Color.BLACK);
		btnNewButton.setFont(new Font("Times New Roman", Font.BOLD | Font.ITALIC, 15));
		btnNewButton.setBounds(10, 45, 125, 30);
		frame.getContentPane().add(btnNewButton);
		
		JButton btnNotGuilty = new JButton("NOT GUILTY");
		btnNotGuilty.setForeground(Color.BLACK);
		btnNotGuilty.setFont(new Font("Times New Roman", Font.BOLD | Font.ITALIC, 15));
		btnNotGuilty.setBounds(10, 90, 125, 30);
		frame.getContentPane().add(btnNotGuilty);
	}
}
