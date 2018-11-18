
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.BindException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.*;

public class guitest{
	
	//Application Layer Variable
	private JFrame frame;
	private ImagePanel mainPanel = new ImagePanel(new ImageIcon(this.getClass().getResource("/images/maxresdefault.jpg")).getImage().getScaledInstance(1200, 850, Image.SCALE_DEFAULT));
	private ImagePanel subPanel = new ImagePanel(new ImageIcon(this.getClass().getResource("/images/subPanelBG.png")).getImage().getScaledInstance(650, 490, Image.SCALE_DEFAULT));
	private JLabel status;
	private JButton btnStart = new JButton("START");
	private JButton btnAvatar = new JButton("AVATAR");
	private JButton btnSettings = new JButton("SETTINGS");
	private JButton btnQuit = new JButton("QUIT");
	private JButton btnBack = new JButton("< BACK");
	private JButton btnHost = new JButton("HOST");
	private JButton btnJoin = new JButton("JOIN");
	private JButton btnReady = new JButton("READY");
	private JButton btnFind = new JButton("FIND GAME");
	private JButton btnCancel = new JButton("CANCEL");
	private JTextField textField = new JTextField();
	
	//Network Variables
	private String localIP = null;
	private int localPort;
	private LinkedList<ObjectOutputStream> outStreamList = new LinkedList<ObjectOutputStream>();
	private LinkedList<Object[]> serverList = new LinkedList<Object[]>();
	private boolean serverFound;

	public static void main(String[] args) {
		new Thread(new Runnable() {
			public void run() {
				new guitest();
			}
		}).start();;
		new Thread(new Runnable() {
			public void run() {
				new guitest();
			}
		}).start();;
	}

	public guitest() {
		frame = new JFrame("CourtSim v1.0 2018");
		frame.setIconImage(new ImageIcon(this.getClass().getResource("/images/logo.png")).getImage());
		frame.setResizable(false);
		frame.setBounds(100, 100, 1200, 850);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setContentPane(mainPanel);
		frame.setVisible(true);
		initializeComponents();
		addActionListeners();
	}
	
	private void initializeComponents() {
		mainPanel.setLayout(null);
		createLabel("CourtSim", new Rectangle(70, 50, 390, 90), 80, mainPanel);
		createLabel("A courtroom simulator...", new Rectangle(270, 130, 335, 30), 25, mainPanel);
		status = createLabel("", new Rectangle(10,750, 1150, 50), 40, mainPanel);
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
				
				do {
					localPort = new Random().nextInt(8999)+1000;
					try {
						localIP = InetAddress.getLocalHost().getHostAddress();
					} catch (UnknownHostException exp) {
						localIP = null;
					}
					try {
						startServer();
					} catch (BindException exp) {
						localPort = -1;
					}
				}while(localIP == null || localPort == -1);
				
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
				textField.setBackground(Color.BLACK);
				textField.setOpaque(false);
				subPanel.add(textField);
				
				mainPanel.repaint();
				mainPanel.revalidate();
				textField.requestFocus();
			}
		});
		btnFind.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnFind.setEnabled(false);
				textField.setEnabled(false);
				new Thread(new Runnable() {
					public void run() {
						byte[] localIP;
						do {
							try {
								localIP = InetAddress.getLocalHost().getAddress();
							} catch (UnknownHostException e1) {
								localIP = null;
							}
						}while(localIP == null);
						String subnet = Byte.toUnsignedInt(localIP[0]) + "." + Byte.toUnsignedInt(localIP[1]) + "." + Byte.toUnsignedInt(localIP[2]);
						
						status.setText("Finding game host...");
						serverFound = false;
						for(int i=0;i<256;i++) {
							final int j = i;
							String host = subnet + "." + j;
							new Thread(new Runnable() {
								public void run() {
									System.out.println("Trying " + host);
									try {
										if (InetAddress.getByName(host).isReachable(1000)) {
											connectToServer(host, Integer.parseInt(textField.getText()));
										}
									} catch (UnknownHostException uhe) { } catch (IOException ioe) { }
								}
							}).start();
						}
						try {
							Thread.sleep(5000);
						} catch (InterruptedException e) { }
						if(!serverFound) {
							status.setText("Game not found!");
							btnFind.setEnabled(true);
							textField.setEnabled(true);
						}
					}
				}).start();
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

	//NETWORK METHODS
	private void startServer() throws BindException{
		//Start my server
		try {
			status.setText("Starting server...");
			ServerSocket serverSocket = new ServerSocket(localPort);
			createLabel("Game ID: " + localPort, new Rectangle(50, 50, 450, 50), 40, subPanel);
			status.setText("Waiting for players...");
			
			//Wait for handshakes
			new Thread(new Runnable() {
				public void run() {
					while(true){
						try {
							Socket socket = serverSocket.accept();
							outStreamList.add(new ObjectOutputStream(socket.getOutputStream()));
							//Wait for the client to send his IP and server port, thread this
							new Thread(new Runnable() {
								public void run() {
									try {
										myMessage clientsInfo = (myMessage) new ObjectInputStream(socket.getInputStream()).readObject();
										LinkedList<Object[]> infoList = ((LinkedList<Object[]>) clientsInfo.getMessage());
										for (int i = 0; i < infoList.size(); i++) {
											final int index = i;
											new Thread(new Runnable() {
												public void run() {
													String clientIP = (String) infoList.get(index)[0];
													int clientPort = (int) infoList.get(index)[1];
													boolean isServerUnique = true;
													for(Object[] obj : serverList) {
														if(((String) obj[0]).equals(clientIP) && ((int) obj[1]) == clientPort) {
															isServerUnique = false;
															break;
														}
													}
													if(isServerUnique && (!localIP.equals(clientIP) || localPort != clientPort)) {
														try {
															connectToServer(clientIP, clientPort);
														} catch (UnknownHostException e) { }
													}
												}
											}).start();
										}
									} catch (ClassNotFoundException | IOException e) {}
								}
							}).start();
						} catch (SocketException e) {
							break;
						} catch (IOException e) { }
					}
				}
			}).start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void connectToServer(String clientIP, int clientPort) throws UnknownHostException{
		try {
			if(localIP.equals(clientIP) && localPort == clientPort) {
				return;
			}
		} catch (NullPointerException e1) { }
		try {
			//Connect to my client's server
			serverList.add(new Object[] {clientIP, clientPort});
			System.out.println(localPort + " connects to "
					+ clientIP + " " + clientPort + " Servers: " + serverList.size());
			for(Object[] a : serverList) {
				System.out.println(a[1]);
			}
			Socket server = new Socket(clientIP, clientPort);
			serverFound = true;
			//Send my IP and port and colleagues
			LinkedList<Object[]> temp = new LinkedList<Object[]>();
			for(int i=0;i<serverList.size();i++) {
				temp.add(serverList.get(i));
			}
			temp.add(new Object[] {localIP, localPort});
			new ObjectOutputStream(server.getOutputStream()).writeObject(new myMessage(0, temp));
			
			//Set up the input stream and Receive messages from this server
			//receiveMessage(new ObjectInputStream(server.getInputStream()));
			
		} catch (SocketException | EOFException e) { } catch (IOException e) { }
	}
}
