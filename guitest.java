
import java.awt.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Random;
import java.awt.Window.Type;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class guitest {

	private JFrame frame;
	ImagePanel mainPanel = new ImagePanel(new ImageIcon(this.getClass().getResource("/images/maxresdefault.jpg")).getImage().getScaledInstance(1200, 850, Image.SCALE_DEFAULT));
	ImagePanel subPanel = new ImagePanel(new ImageIcon(this.getClass().getResource("/images/subPanelBG.png")).getImage().getScaledInstance(650, 490, Image.SCALE_DEFAULT));
	JLabel status;
	JButton btnStart = new JButton("START");
	JButton btnAvatar = new JButton("AVATAR");
	JButton btnSettings = new JButton("SETTINGS");
	JButton btnQuit = new JButton("QUIT");
	JButton btnBack = new JButton("< BACK");
	JButton btnHost = new JButton("HOST");
	JButton btnJoin = new JButton("JOIN");
	JButton btnReady = new JButton("READY");
	JButton btnFind = new JButton("FIND GAME");
	JButton btnCancel = new JButton("CANCEL");
	JTextField textField = new JTextField();

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
		frame = new JFrame("CourtSim v1.0 2018");
		frame.setResizable(false);
		frame.setBounds(100, 100, 1200, 850);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setContentPane(mainPanel);
		initializeComponents();
		addActionListeners();
	}
		
	class ImagePanel extends JPanel {

		  private Image img;

		  public ImagePanel(String img) {
		    this(new ImageIcon(img).getImage());
		  }

		  public ImagePanel(Image img) {
		    this.img = img;
		    Dimension size = new Dimension(img.getWidth(null), img.getHeight(null));
		    setPreferredSize(size);
		    setMinimumSize(size);
		    setMaximumSize(size);
		    setSize(size);
		    setLayout(null);
		  }

		  public void paintComponent(Graphics g) {
		    g.drawImage(img, 0, 0, null);
		  }

		}
	
	/**
	 * Initialize the contents of the frame.
	 */
	private void initializeComponents() {
		mainPanel.setLayout(null);
		createLabel("CourtSim", new Rectangle(70, 50, 390, 90), 80, mainPanel);
		createLabel("A courtroom simulator...", new Rectangle(270, 130, 335, 30), 25, mainPanel);
		status = createLabel("", new Rectangle(0,750, 1150, 50), 40, mainPanel);
		status.setHorizontalAlignment(SwingConstants.TRAILING);
		configureButton(btnStart, 700, 250, mainPanel);
		configureButton(btnAvatar, 700, 330, mainPanel);
		configureButton(btnSettings, 700, 410, mainPanel);
		configureButton(btnQuit, 700, 490, mainPanel);
		subPanel.setLayout(null);
		subPanel.setBackground(Color.CYAN);
		subPanel.setBounds(50, 210, 650, 490);
		mainPanel.add(subPanel);
	}
	
	private JLabel createLabel(String title, Rectangle bounds, int fontSize, Component parent) {
		JLabel label = new JLabel(title);
		label.setForeground(Color.BLACK);
		label.setFont(new Font("MV Boli", Font.BOLD, fontSize));
		label.setBounds(bounds);
		((JPanel) parent).add(label);
		return label;
	}
	
	private void configureButton(JButton btn, int x, int y, Component parent) {
		btn.setForeground(Color.BLACK);
		btn.setBorderPainted(false);
		btn.setContentAreaFilled(false);
		btn.setFont(new Font("MV Boli", Font.PLAIN, 60));
		btn.setBounds(x, y, 500, 50);
		btn.addMouseListener(new MouseAdapter() {
			public void mouseEntered(MouseEvent e) { if(btn.isEnabled()) { btn.setFont(new Font("MV Boli", Font.BOLD, 65)); } }
			public void mouseExited(MouseEvent e) { if(btn.isEnabled()) { btn.setFont(new Font("MV Boli", Font.PLAIN, 60)); } }
		});
		((JPanel) parent).add(btn);
	}
	
	private void addActionListeners() {
		btnStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				configureButton(btnBack, 700, 250, mainPanel);
				configureButton(btnHost, 700, 330, mainPanel);
				configureButton(btnJoin, 700, 410, mainPanel);
				
				mainPanel.remove(btnStart);
				mainPanel.remove(btnAvatar);
				mainPanel.remove(btnSettings);
				mainPanel.remove(btnQuit);
				mainPanel.repaint();
				mainPanel.revalidate();
			}
		});
		btnQuit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		btnBack.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mainPanel.remove(btnBack);
				mainPanel.remove(btnHost);
				mainPanel.remove(btnJoin);
				mainPanel.add(btnStart);
				mainPanel.add(btnAvatar);
				mainPanel.add(btnSettings);
				mainPanel.add(btnQuit);
				mainPanel.repaint();
				mainPanel.revalidate();
			}
		});
		btnHost.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				configureButton(btnReady, 700, 250, mainPanel);
				configureButton(btnCancel, 700, 330, mainPanel);
				btnReady.setEnabled(false);
				
				mainPanel.remove(btnBack);
				mainPanel.remove(btnHost);
				mainPanel.remove(btnJoin);
				status.setText("WAITING FOR PLAYERS TO JOIN...");
				
				createLabel("Game ID: " + (new Random().nextInt(8999)+1000), new Rectangle(50, 50, 450, 50), 40, subPanel);
				
				mainPanel.repaint();
				mainPanel.revalidate();
			}
		});
		btnJoin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				configureButton(btnReady, 700, 250, mainPanel);
				configureButton(btnFind, 700, 330, mainPanel);
				configureButton(btnCancel, 700, 410, mainPanel);
				btnReady.setEnabled(false);
				btnFind.setEnabled(false);
				
				mainPanel.remove(btnBack);
				mainPanel.remove(btnHost);
				mainPanel.remove(btnJoin);
				
				createLabel("Game ID: ", new Rectangle(50, 50, 450, 50), 40, subPanel);
				textField.setFont(new Font("MV Boli", Font.PLAIN, 40));
				textField.setHorizontalAlignment(SwingConstants.CENTER);
				textField.setBounds(250, 50, 300, 50);
				subPanel.add(textField);
				
				mainPanel.repaint();
				mainPanel.revalidate();
				textField.requestFocus();
			}
		});
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mainPanel.remove(btnReady);
				mainPanel.remove(btnFind);
				mainPanel.remove(btnCancel);
				subPanel.removeAll();
				status.setText(null);
				
				mainPanel.add(btnBack);
				mainPanel.add(btnHost);
				mainPanel.add(btnJoin);
				status.setText("");
				mainPanel.repaint();
				mainPanel.revalidate();
			}
		});
		textField.addKeyListener(new KeyAdapter() {
			public void keyTyped(KeyEvent e) {
				if(textField.getText().length() > 0) {
					btnFind.setEnabled(true);
				} else {
					btnFind.setEnabled(false);
				}
			}
		});
	}
}
