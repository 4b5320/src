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
		frame.setBounds(100, 100, 540, 430);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout(0,0));
		
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(null);
		
		JTextField inputField = new JTextField();
		JTextArea courtArea = new JTextArea();
		JScrollPane scrollPane1 = new JScrollPane();
		JScrollPane scrollPane2;
		JButton btnNewButton = new JButton("SEND");
		frame.getContentPane().add(mainPanel, BorderLayout.CENTER);
		
		frame.setSize(540, 440);
		
		//Setup the courtroom chat box
		JLabel lblCourtroomsConversation = new JLabel("Courtroom's Conversation");
		lblCourtroomsConversation.setBackground(new Color(240, 240, 240));
		lblCourtroomsConversation.setFont(new Font("Times New Roman", Font.BOLD | Font.ITALIC, 15));
		scrollPane1.setColumnHeaderView(lblCourtroomsConversation);
		courtArea.setForeground(Color.BLACK);
		courtArea.setFont(new Font("Times New Roman", Font.BOLD | Font.ITALIC, 15));
		courtArea.setEditable(false);
		scrollPane1.setViewportView(courtArea);
		scrollPane1.setBounds(10, 10, 340, 350);
		mainPanel.add(scrollPane1);

		//setup the message input field
		inputField.setForeground(Color.BLACK);
		inputField.setFont(new Font("Times New Roman", Font.BOLD | Font.ITALIC, 15));
		inputField.setBounds(10, 370, 430, 20);
		mainPanel.add(inputField);
		
		//setup the send button
		btnNewButton.setForeground(Color.BLACK);
		btnNewButton.setBackground(UIManager.getColor("Button.light"));
		btnNewButton.setFont(new Font("Times New Roman", Font.BOLD | Font.ITALIC, 15));
		btnNewButton.setBounds(450, 370, 75, 20);
		mainPanel.add(btnNewButton);
		
		JButton btnBeginTrial = new JButton("PRESENT TESTIMONY");
		btnBeginTrial.setForeground(Color.BLACK);
		btnBeginTrial.setFont(new Font("Times New Roman", Font.BOLD | Font.ITALIC, 12));
		btnBeginTrial.setBackground(SystemColor.controlHighlight);
		btnBeginTrial.setBounds(360, 10, 165, 30);
		mainPanel.add(btnBeginTrial);
		
		JButton btnCallForPartial = new JButton("OBJECT!");
		btnCallForPartial.setForeground(Color.BLACK);
		btnCallForPartial.setFont(new Font("Times New Roman", Font.BOLD | Font.ITALIC, 14));
		btnCallForPartial.setBackground(SystemColor.controlHighlight);
		btnCallForPartial.setBounds(360, 50, 165, 30);
		mainPanel.add(btnCallForPartial);
		
		JButton btnLeave = new JButton("LEAVE!");
		btnLeave.setForeground(Color.BLACK);
		btnLeave.setFont(new Font("Times New Roman", Font.BOLD | Font.ITALIC, 14));
		btnLeave.setBackground(SystemColor.controlHighlight);
		btnLeave.setBounds(360, 90, 165, 30);
		mainPanel.add(btnLeave);
	}
}
