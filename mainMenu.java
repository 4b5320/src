import javax.swing.JFrame;
import java.awt.BorderLayout;
import javax.swing.JButton;
import java.awt.Font;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import java.awt.Color;
import java.awt.Component;

import javax.swing.JTextArea;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;

import javax.swing.JToggleButton;
import javax.swing.JTextField;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.net.ServerSocket;
import java.util.LinkedList;

public class mainMenu extends JFrame{

	protected JTextArea textArea = new JTextArea();
	protected JProgressBar progressBar = new JProgressBar();
	protected JButton btnConnect = new JButton("CONNECT");
	protected JButton btnStart = new JButton("START");
	private JPanel mainPanel = new JPanel();
	protected JTextField nameField;
	protected JToggleButton[] rolebtn;
	protected Boolean[] roleTaken = {false, false, false, false};

	
	public mainMenu() {
		this.setResizable(false);
		this.setBackground(Color.DARK_GRAY);
		this.setBounds(100, 100, 445, 370);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
		this.getContentPane().add(mainPanel, BorderLayout.CENTER);
		this.getContentPane().add(progressBar, BorderLayout.SOUTH);
		mainPanel.setLayout(null);
		progressBar.setForeground(Color.BLACK);
		progressBar.setBackground(Color.WHITE);
		
		//setupPanel1();
		//setupPanel2();
		//Setup the homescreen
		
		/*JLabel lblCourtsim = new JLabel("CourtSim");
		JLabel lblEnterYourUsername = new JLabel("Enter your username");
		JButton btnStart = new JButton("S T A R T");
		lblCourtsim.setForeground(Color.BLACK);
		lblCourtsim.setHorizontalAlignment(SwingConstants.CENTER);
		lblCourtsim.setFont(new Font("Times New Roman", Font.BOLD | Font.ITALIC, 35));
		lblCourtsim.setBounds(10, 11, 419, 60);
		mainPanel.add(lblCourtsim);
		
		nameField = new JTextField("");
		nameField.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				if(nameField.getText().replaceAll(" ", "").length() > 0) {
					mainPanel.add(btnStart);
					mainPanel.revalidate();
					mainPanel.repaint();
				} else {
					mainPanel.remove(btnStart);
					mainPanel.revalidate();
					mainPanel.repaint();
				}
			}
		});
		nameField.setForeground(Color.BLACK);
		nameField.setHorizontalAlignment(SwingConstants.CENTER);
		nameField.setFont(new Font("Times New Roman", Font.BOLD | Font.ITALIC, 30));
		nameField.setBounds(45, 145, 350, 60);
		mainPanel.add(nameField);
		
		lblEnterYourUsername.setForeground(Color.BLACK);
		lblEnterYourUsername.setHorizontalAlignment(SwingConstants.CENTER);
		lblEnterYourUsername.setFont(new Font("Times New Roman", Font.BOLD | Font.ITALIC, 35));
		lblEnterYourUsername.setBounds(10, 93, 419, 52);
		mainPanel.add(lblEnterYourUsername);
		
		btnStart.setForeground(Color.BLACK);
		btnStart.setFont(new Font("Times New Roman", Font.BOLD | Font.ITALIC, 30));
		btnStart.setBounds(70, 230, 300, 30);
		btnStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				removeElements();
				setupPanel1();
			}
		});*/
		
	}
	
	protected void removeElements() {
		for(Component c : mainPanel.getComponents()) {
			mainPanel.remove(c);
		}
		mainPanel.revalidate();
		mainPanel.repaint();
	}
	
	protected void setupPanel1() {
		
		btnStart.setBackground(Color.LIGHT_GRAY);
		btnStart.setForeground(Color.BLACK);
		btnStart.setEnabled(false);
		btnStart.setFont(new Font("Times New Roman", Font.BOLD | Font.ITALIC, 20));
		btnStart.setBounds(220, 10, 200, 30);
		mainPanel.add(btnStart);
		
		textArea.setBounds(10, 50, 415, 265);
		mainPanel.add(textArea);
		
		btnConnect.setForeground(Color.BLACK);
		btnConnect.setFont(new Font("Times New Roman", Font.BOLD | Font.ITALIC, 20));
		btnConnect.setBackground(Color.LIGHT_GRAY);
		btnConnect.setBounds(10, 10, 200, 30);
		mainPanel.add(btnConnect);
	}
	
	protected void setupPanel2() {
		rolebtn = new JToggleButton[4];
		JLabel[] labels = new JLabel[4];
		
		rolebtn[0] = new JToggleButton("JUDGE");
		rolebtn[1] = new JToggleButton("DEFENSE ATTORNEY");
		rolebtn[2] = new JToggleButton("PROSECUTOR");
		rolebtn[3] = new JToggleButton("JURY");
		for(int i=0;i<rolebtn.length;i++) {
			rolebtn[i].setFont(new Font("Times New Roman", Font.BOLD | Font.ITALIC, 20));
			rolebtn[i].setBounds(10, i*60+50, 420, 30);
			mainPanel.add(rolebtn[i]);
			
		}
		
		for(int i=0;i<labels.length;i++) {
			labels[i] = new JLabel();
			labels[i].setFont(new Font("Courier New", Font.BOLD | Font.ITALIC, 12));
			labels[i].setHorizontalAlignment(SwingConstants.CENTER);
			labels[i].setBounds(10, i*60+80, 420, 14);
			mainPanel.add(labels[i]);
		}
		
		JLabel label = new JLabel("CHOOSE YOUR ROLE");
		label.setHorizontalAlignment(SwingConstants.CENTER);
		label.setFont(new Font("Times New Roman", Font.BOLD | Font.ITALIC, 20));
		label.setBounds(10, 10, 420, 30);
		mainPanel.add(label);
		
		JButton btnLock = new JButton("LOCK-IN");
		btnLock.setBackground(new Color(0, 128, 0));
		btnLock.setForeground(Color.BLACK);
		btnLock.setFont(new Font("Times New Roman", Font.BOLD | Font.ITALIC, 20));
		btnLock.setBounds(86, 285, 267, 30);
		btnLock.setEnabled(false);
		mainPanel.add(btnLock);
	}
}
