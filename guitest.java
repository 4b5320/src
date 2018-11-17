
import java.awt.*;
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
	JButton btnStart = new JButton("START");
	JButton btnProfile = new JButton("PROFILE");
	JButton btnSettings = new JButton("SETTINGS");
	JButton btnQuit = new JButton("QUIT");
	JButton btnBack = new JButton("< BACK");
	JButton btnHost = new JButton("HOST");
	JButton btnJoin = new JButton("JOIN");
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
		frame = new JFrame("CourtSim v1.0 2018");
		frame.setAlwaysOnTop(true);
		frame.setResizable(false);
		frame.setBounds(100, 100, 1200, 850);
		frame.getContentPane().setLayout(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JLabel lblNewLabel = new JLabel("CourtSim");
		lblNewLabel.setForeground(Color.BLACK);
		lblNewLabel.setFont(new Font("MV Boli", Font.BOLD, 80));
		frame.getContentPane().add(lblNewLabel);
		
		
		JLabel lblACourtroomSimulator = new JLabel("A courtroom simulator...");
		lblACourtroomSimulator.setForeground(Color.BLACK);
		lblACourtroomSimulator.setFont(new Font("MV Boli", Font.BOLD, 25));
		frame.getContentPane().add(lblACourtroomSimulator);
		btnStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new Thread(new Runnable() {
					public void run() {
						start();
					}
				}).start();
			}
		});
		
		btnStart.setForeground(Color.BLACK);
		btnStart.setBorderPainted(false);
		btnStart.setContentAreaFilled(false);
		btnStart.setFont(new Font("MV Boli", Font.PLAIN, 50));
		btnStart.setBounds(700, 250, 350, 50);
		frame.getContentPane().add(btnStart);
		
		btnProfile.setForeground(Color.BLACK);
		btnProfile.setFont(new Font("MV Boli", Font.PLAIN, 50));
		btnProfile.setContentAreaFilled(false);
		btnProfile.setBorderPainted(false);
		btnProfile.setBounds(700, 330, 350, 50);
		frame.getContentPane().add(btnProfile);
		
		btnSettings.setForeground(Color.BLACK);
		btnSettings.setFont(new Font("MV Boli", Font.PLAIN, 50));
		btnSettings.setContentAreaFilled(false);
		btnSettings.setBorderPainted(false);
		btnSettings.setBounds(700, 410, 350, 50);
		frame.getContentPane().add(btnSettings);
		
		btnQuit.setForeground(Color.BLACK);
		btnQuit.setFont(new Font("MV Boli", Font.PLAIN, 50));
		btnQuit.setContentAreaFilled(false);
		btnQuit.setBorderPainted(false);
		btnQuit.setBounds(700, 490, 350, 50);
		frame.getContentPane().add(btnQuit);
		
		//Animation
		new Thread(new Runnable() {
			public void run() {
				int i = frame.getWidth();
				while(i>70) {
					lblNewLabel.setBounds(i, 50, 390, 90);
					frame.repaint();
					frame.revalidate();
					try {
						Thread.sleep(1);
					} catch (InterruptedException e1) { }
					i -= 10;
				}
			}
		}).start();
		new Thread(new Runnable() {
			public void run() {
				int i = 0;
				while(i<270) {
					lblACourtroomSimulator.setBounds(i, 130, 335, 30);
					frame.repaint();
					frame.revalidate();
					try { Thread.sleep(10); } catch (InterruptedException e1) { }
					i += 3;
				}

				btnStart.addMouseListener(new MouseAdapter() {
					public void mouseEntered(MouseEvent e) { btnStart.setFont(new Font("MV Boli", Font.BOLD, 60)); }
					public void mouseExited(MouseEvent e) { btnStart.setFont(new Font("MV Boli", Font.PLAIN, 50)); }
				});
				btnProfile.addMouseListener(new MouseAdapter() {
					public void mouseEntered(MouseEvent e) { btnProfile.setFont(new Font("MV Boli", Font.BOLD, 60)); }
					public void mouseExited(MouseEvent e) { btnProfile.setFont(new Font("MV Boli", Font.PLAIN, 50)); }
				});
				btnSettings.addMouseListener(new MouseAdapter() {
					public void mouseEntered(MouseEvent e) { btnSettings.setFont(new Font("MV Boli", Font.BOLD, 60)); }
					public void mouseExited(MouseEvent e) { btnSettings.setFont(new Font("MV Boli", Font.PLAIN, 50)); }
				});
				btnQuit.addMouseListener(new MouseAdapter() {
					public void mouseEntered(MouseEvent e) { btnQuit.setFont(new Font("MV Boli", Font.BOLD, 60)); }
					public void mouseExited(MouseEvent e) { btnQuit.setFont(new Font("MV Boli", Font.PLAIN, 50)); }
				});
			}
		}).start();
		
		JLabel status = new JLabel("Game ID: ");
		status.setForeground(Color.BLACK);
		status.setFont(new Font("MV Boli", Font.PLAIN, 40));
		status.setBounds(90,250, 195, 50);
		frame.getContentPane().add(status);
		
		textField = new JTextField();
		textField.setFont(new Font("MV Boli", Font.PLAIN, 40));
		textField.setHorizontalAlignment(SwingConstants.CENTER);
		textField.setBounds(285, 250, 160, 50);
		textField.setOpaque(false);
		textField.setBorder(null);
		frame.getContentPane().add(textField);
		
		JButton btnJoin_1 = new JButton("JOIN");
		btnJoin_1.setBackground(Color.LIGHT_GRAY);
		btnJoin_1.setForeground(Color.BLACK);
		btnJoin_1.setFont(new Font("MV Boli", Font.PLAIN, 40));
		btnJoin_1.setBounds(450, 250, 135, 50);
		frame.getContentPane().add(btnJoin_1);
	}
	
	private void start() {
		btnBack.setForeground(Color.BLACK);
		btnBack.setBorderPainted(false);
		btnBack.setContentAreaFilled(false);
		btnBack.setFont(new Font("MV Boli", Font.BOLD, 60));
		btnBack.setBounds(700, 250, 350, 50);
		frame.getContentPane().add(btnBack);
		
		btnHost.setForeground(Color.BLACK);
		btnHost.setFont(new Font("MV Boli", Font.PLAIN, 50));
		btnHost.setContentAreaFilled(false);
		btnHost.setBorderPainted(false);
		btnHost.setBounds(700, 330, 350, 50);
		frame.getContentPane().add(btnHost);
		
		btnJoin.setForeground(Color.BLACK);
		btnJoin.setFont(new Font("MV Boli", Font.PLAIN, 50));
		btnJoin.setContentAreaFilled(false);
		btnJoin.setBorderPainted(false);
		btnJoin.setBounds(700, 410, 350, 50);
		frame.getContentPane().add(btnJoin);
		
		frame.remove(btnStart);
		frame.remove(btnProfile);
		frame.remove(btnSettings);
		frame.remove(btnQuit);
		frame.repaint();
		frame.revalidate();

		btnBack.addMouseListener(new MouseAdapter() {
			public void mouseEntered(MouseEvent e) { btnBack.setFont(new Font("MV Boli", Font.BOLD, 60)); }
			public void mouseExited(MouseEvent e) { btnBack.setFont(new Font("MV Boli", Font.PLAIN, 50)); }
		});
		btnHost.addMouseListener(new MouseAdapter() {
			public void mouseEntered(MouseEvent e) { btnHost.setFont(new Font("MV Boli", Font.BOLD, 60)); }
			public void mouseExited(MouseEvent e) { btnHost.setFont(new Font("MV Boli", Font.PLAIN, 50)); }
		});
		btnJoin.addMouseListener(new MouseAdapter() {
			public void mouseEntered(MouseEvent e) { btnJoin.setFont(new Font("MV Boli", Font.BOLD, 60)); }
			public void mouseExited(MouseEvent e) { btnJoin.setFont(new Font("MV Boli", Font.PLAIN, 50)); }
		});
		
		btnBack.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new Thread(new Runnable() {
					public void run() {
						frame.remove(btnBack);
						frame.remove(btnHost);
						frame.remove(btnJoin);
						frame.getContentPane().add(btnStart);
						frame.getContentPane().add(btnProfile);
						frame.getContentPane().add(btnSettings);
						frame.getContentPane().add(btnQuit);
						frame.repaint();
						frame.revalidate();
					}
				}).start();
			}
		});
		btnHost.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				host();
			}
		});
	}
	
	private void host() {
		frame.remove(btnBack);
		frame.remove(btnHost);
		frame.remove(btnJoin);
		JLabel status = new JLabel("Waiting for other players...");
		status.setForeground(Color.BLACK);
		status.setFont(new Font("MV Boli", Font.PLAIN, 40));
		status.setBounds(0,750, 1150, 50);
		status.setHorizontalAlignment(SwingConstants.TRAILING);
		
		JLabel gameID = new JLabel("Game ID: " + (new Random().nextInt(8999)+1000));
		gameID.setForeground(Color.BLACK);
		gameID.setFont(new Font("MV Boli", Font.PLAIN, 40));
		gameID.setBounds(90,250, 450, 50);
		frame.getContentPane().add(gameID);
		
		frame.getContentPane().add(status);
		frame.repaint();
		frame.revalidate();
	}
}
