
import java.awt.*;
import javax.swing.*;
import javax.swing.text.DefaultCaret;

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
	private boolean readyToPlay = false;
	private int playersReady = 0;
	private JToggleButton[] rolebtn;
	private Boolean[] roleTaken = {false, false, false, false};
	private String roles[] = {"Judge","Defense Lawyer","Prosecutor","Juror"};
	private String playerName = null;
	private String playerRole = null;
	private boolean isGameHost = false;
	private int timeout = 10, timer = 0;
	
	//Network Variables
	private ServerSocket serverSocket;
	private String localIP = null;
	private int localPort = -1;
	private LinkedList<ObjectOutputStream> outStreamList = new LinkedList<ObjectOutputStream>();
	private LinkedList<Object[]> serverList = new LinkedList<Object[]>();
	
	private JTextArea textArea = new JTextArea();
	private JProgressBar progressBar = new JProgressBar();
	private JButton btnConnect = new JButton("CONNECT");
	private LinkedList<Object[]> clientList = new LinkedList<Object[]>();
	private JTextArea courtArea;
	private JTextArea juryArea;
	private JTextField inputField = new JTextField();
	private int guilty = 0, notguilty = 0, numberOfJuror=0;
	private JRadioButton rdbtnNewRadioButton = new JRadioButton("Prosecutor");
	private JRadioButton rdbtnDefenseLawyer = new JRadioButton("Defense Lawyer");
	private ButtonGroup bg = new ButtonGroup();
	private JButton btnSend = new JButton("SEND");
	private JButton btnPresent, btnObject;
	
	
	public static void main(String[] args) {
		new Thread(new Runnable() {
			public void run() {
				new guitest();
			}
		}).start();
		new Thread(new Runnable() {
			public void run() {
				new guitest();
			}
		}).start();
		new Thread(new Runnable() {
			public void run() {
				new guitest();
			}
		}).start();
		new Thread(new Runnable() {
			public void run() {
				new guitest();
			}
		}).start();
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
				isGameHost = true;
				configureButton(btnReady, 700, 250, mainPanel);
				configureButton(btnCancel, 700, 330, mainPanel);
				btnReady.setEnabled(false);
				
				mainPanel.remove(btnBack);
				mainPanel.remove(btnHost);
				mainPanel.remove(btnJoin);
				status.setText("WAITING FOR PLAYERS TO JOIN...");
				
				do {
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
				
				byte[] localIP;
				do {
					try {
						localIP = InetAddress.getLocalHost().getAddress();
					} catch (UnknownHostException e1) {
						localIP = null;
					}
				}while(localIP == null);
				createLabel("Game ID: " + localPort + Byte.toUnsignedInt(localIP[3]), new Rectangle(50, 50, 450, 50), 40, subPanel);
				
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
				do {
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
				btnFind.setEnabled(false);
				btnFind.setFont(new Font("MV Boli", Font.PLAIN, 60));
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
						try {
							String clientIP = subnet + "." + textField.getText().substring(4, textField.getText().length());
							int clientPort = Integer.parseInt(textField.getText().substring(0, 4));
							try {
								connectToServer(clientIP, clientPort, "Player" + new Random().nextInt(100));
								createLabel("Game ID: " + textField.getText() + Byte.toUnsignedInt(localIP[3]), new Rectangle(50, 50, 450, 50), 40, subPanel);
								subPanel.remove(textField);
								status.setText("Waiting for other players...");
								btnCancel.setEnabled(false);
								
								mainPanel.repaint();
								mainPanel.revalidate();
							} catch (Exception e) {
								status.setText("Game not found!");
								btnFind.setEnabled(true);
								textField.setEnabled(true);
								textField.setText("");
								textField.requestFocus();
							}
						} catch(StringIndexOutOfBoundsException | NumberFormatException e) {
							status.setText("Invalid Game ID!");
							btnFind.setEnabled(true);
							textField.setEnabled(true);
							textField.setText("");
							textField.requestFocus();
						}
					}
				}).start();
			}
		});
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					serverSocket.close();
				} catch (NullPointerException | IOException e1) { };
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
		btnReady.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnReady.setEnabled(false);
				readyToPlay = true;
				btnStart.setEnabled(false);
				sendMessage(new myMessage(1, true));
				System.out.println("\nPlayers ready: " + playersReady + " outstreamlist: " + outStreamList.size());
				if(playersReady == outStreamList.size()) {
					new Thread(new Runnable() {
						public void run() {
							chooseRolesUI();
						}
					}).start();
				}
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
			localPort = new Random().nextInt(8999)+1000;
			status.setText("Starting server...");
			serverSocket = new ServerSocket(localPort);
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
															connectToServer(clientIP, clientPort, "Player" + new Random().nextInt(100));
														} catch (Exception e) { }
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
	
	private void connectToServer(String clientIP, int clientPort, String name) throws Exception {
		try {
			if(localIP.equals(clientIP) && localPort == clientPort) {
				return;
			}
		} catch (NullPointerException e1) { }
		System.out.println("ClientIP: " + clientIP + " clientPort: " + clientPort);
		//Connect to my client's server
		Socket server = new Socket(clientIP, clientPort);
		serverList.add(new Object[] {clientIP, clientPort});
		
		//Check if there is enough number of players, that is atleast 4, 3 servers
		if(serverList.size() == 1) {
			btnReady.setEnabled(true);
		}
		
		//Display the connected server
		createLabel(name, new Rectangle(50, serverList.size()*60+50, 450, 50), 40, subPanel);
		frame.repaint();
		frame.revalidate();
		
		System.out.println(localPort + " connects to "
				+ clientIP + " " + clientPort );
		//Send my IP and port and colleagues
		LinkedList<Object[]> temp = new LinkedList<Object[]>();
		for(int i=0;i<serverList.size();i++) {
			temp.add(serverList.get(i));
		}
		temp.add(new Object[] {localIP, localPort});
		new ObjectOutputStream(server.getOutputStream()).writeObject(new myMessage(0, temp));
		
		//Set up the input stream and Receive messages from this server
		ObjectInputStream in = new ObjectInputStream(server.getInputStream());
		//receiveMessage(new ObjectInputStream(server.getInputStream()));
		while(true) {
			try {
				myMessage message = (myMessage) in.readObject();
				if(message.isType(1) && message.getMessage().toString().equals("true")) {
					System.out.println("Message received.");
					playersReady++;
					if(readyToPlay && playersReady == serverList.size() && playerRole==null) {
						new Thread(new Runnable() {
							public void run() {
								chooseRolesUI();
							}
						}).start();
					} else if(readyToPlay && playersReady == serverList.size() && playerRole != null) {
						mainPanel.removeAll();
						frame.repaint();
						frame.revalidate();
						setGameUI();
					}
				} else if(message.isType(2)) {
					int value = Integer.parseInt(message.getMessage().toString());
					if (Math.abs(value) != 4) {
						if (value > 0) {
							rolebtn[value - 1].setSelected(true);
							rolebtn[value - 1].setEnabled(false);
							roleTaken[value - 1] = true;
						} else {
							value = value * -1;
							rolebtn[value - 1].setSelected(false);
							rolebtn[value - 1].setEnabled(true);
							roleTaken[value - 1] = false;
						} 
					} else {
						if(value == 4) {
							numberOfJuror++;
						} else {
							numberOfJuror--;
						}
					}
				} else if(message.isType(3)) {
					System.out.println("Received role: " + message.getRoleOfSource());
					if(playerRole.equals("Juror")) {
						if(message.getRoleOfSource().equals("Juror")) {
							juryArea.append("\nJuror " + message.getSource() + ": " + (String) message.getMessage());
						} else {
							courtArea.append("\n" + message.getRoleOfSource() + " " + message.getSource() + ": " + (String) message.getMessage());
							courtArea.setCaretPosition(courtArea.getText().length());
						}
					} else if(!message.getRoleOfSource().equals("Juror")){
						courtArea.append("\n" + message.getRoleOfSource() + " " + message.getSource() + ": " + (String) message.getMessage());
					}
				}else if(message.isType(4)) {
					System.out.println((String) message.getMessage() + " is the allowed to talk");
					if(playerRole.equals("Prosecutor") || playerRole.equals("Defense Lawyer")) {
						if (playerRole.equals((String) message.getMessage())) {
							inputField.setEnabled(true);
							btnSend.setEnabled(true);
							btnPresent.setEnabled(true);
							btnObject.setEnabled(false);
							progressBar.setMaximum(timeout);
							timer = 0;
							progressBar.setValue(timer);
							new Thread(new Runnable() {
								public void run() {
									while (timer < timeout) {
										try {
											Thread.sleep(1000);
										} catch (InterruptedException e) {
										}
										if(timer == timeout) {
											progressBar.setValue(timer);
											break;
										}
										timer++;
										progressBar.setValue(timer);
									}
									if (timer == timeout) {
										System.out.println(playerRole + " is finished talking");
										sendMessage(new myMessage(10, playerRole));
									}
								}
							}).start(); 
						} else {
							timer = timeout+1;
							progressBar.setValue(progressBar.getMaximum());
							inputField.setEnabled(false);
							btnSend.setEnabled(false);
							btnPresent.setEnabled(false);
							btnObject.setEnabled(true);
						}
					} else if(playerRole.equals("Judge")) {
						if("Prosecutor".equals((String) message.getMessage())) {
							rdbtnNewRadioButton.setSelected(true);
						}else if("Defense Lawyer".equals((String) message.getMessage())) {
							rdbtnDefenseLawyer.setSelected(true);
							System.out.println("Defense Lawyer is talking");
						}else {
							bg.clearSelection();
							System.out.println("Nobody is talking");
						}
						System.out.println((String) message.getMessage() + " is talking");
					}
				}else if(message.isType(5) && playerRole.equals("Juror")) {
					int x = frame.getX() + frame.getWidth();
					int y = frame.getY();
					new Thread(new Runnable() {
						public void run() {
							JFrame votingFrame = new JFrame();
							votingFrame.setVisible(true);
							votingFrame.setResizable(false);
							votingFrame.setBounds(x, y, 150, 165);
							votingFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
							votingFrame.getContentPane().setLayout(null);
							
							JLabel lblWhatIsYour = new JLabel("What is your vote?");
							lblWhatIsYour.setForeground(Color.BLACK);
							lblWhatIsYour.setFont(new Font("Times New Roman", Font.BOLD | Font.ITALIC, 15));
							lblWhatIsYour.setBounds(10, 10, 125, 23);
							votingFrame.getContentPane().add(lblWhatIsYour);
							
							JButton btnNewButton = new JButton("GUILTY");
							btnNewButton.setForeground(Color.BLACK);
							btnNewButton.setFont(new Font("Times New Roman", Font.BOLD | Font.ITALIC, 15));
							btnNewButton.setBounds(10, 45, 125, 30);
							votingFrame.getContentPane().add(btnNewButton);
							
							JButton btnNotGuilty = new JButton("NOT GUILTY");
							btnNotGuilty.setForeground(Color.BLACK);
							btnNotGuilty.setFont(new Font("Times New Roman", Font.BOLD | Font.ITALIC, 15));
							btnNotGuilty.setBounds(10, 90, 125, 30);
							votingFrame.getContentPane().add(btnNotGuilty);
							
							btnNewButton.addActionListener(new ActionListener() {
								public void actionPerformed(ActionEvent e) {
									sendMessage(new myMessage(8, "GUILTY", playerRole, playerName));
									votingFrame.dispose();
								}
							});
							
							btnNotGuilty.addActionListener(new ActionListener() {
								public void actionPerformed(ActionEvent e) {
									sendMessage(new myMessage(8, "NOT GUILTY", playerRole, playerName));
									votingFrame.dispose();
								}
							});
						}
					}).start();
				}else if(message.isType(6) && playerRole.equals("Judge")) {
					
				}else if(message.isType(7) && playerRole.equals("Judge")) {
					
				}else if(message.isType(8)) {
					courtArea.append("\n" + message.getRoleOfSource() + " " + message.getSource() + ": I vote for "
							+ (String) message.getMessage() + "!");
					if(((String) message.getMessage()).equals("GUILTY")) {
						guilty++;
					}else {
						notguilty++;
					}
					
					if(numberOfJuror == guilty + notguilty) {
						if(guilty > notguilty) {
							sendMessage(new myMessage(3, "Majority of the Jury voted for guilty!"));
						} else if (guilty < notguilty){
							sendMessage(new myMessage(3, "Majority of the Jury voted for  not guilty!"));
						} else {
							sendMessage(new myMessage(3, "The votes are equal!"));
						}
						
					}
				}else if(message.isType(9) && playerRole.equals("Judge")) {
					numberOfJuror++;
				}else if(message.isType(10) && playerRole.equals("Judge")) {
					if("Prosecutor".equals((String) message.getMessage())) {
						sendMessage(new myMessage(4, "Defense Lawyer"));
					}else {
						sendMessage(new myMessage(4, "Prosecutor"));
					}
				}else if(message.isType(11) && (playerRole.equals("Prosecutor") || playerRole.equals("Defense Lawyer"))) {
					timer = timeout;
					//sendMessage(new myMessage(10, playerRole));
				}else if(message.isType(12) && (playerRole.equals("Prosecutor") || playerRole.equals("Defense Lawyer"))) {
					timer = timeout;
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (SocketException e) {
				break;
			}
		}
	}
	
	private void sendMessage(myMessage message) {
		for(ObjectOutputStream outStream : outStreamList) {
			try {
				outStream.writeObject(message);
			} catch (IOException e) {
			
			}
		}
	}
	
	private void chooseRolesUI() {
		int iop = 5;
		while(iop > 0) {
			status.setText("Game starts in " + iop + "...");
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e1) { }
			iop--;
		}
		status.setText("Choose your role");
		
		readyToPlay = false;
		playersReady = 0;
		
		subPanel.removeAll();
		frame.repaint();
		frame.revalidate();
		
		rolebtn = new JToggleButton[4];
		JLabel[] labels = new JLabel[4];
		
		rolebtn[0] = new JToggleButton(roles[0]);
		rolebtn[1] = new JToggleButton(roles[1]);
		rolebtn[2] = new JToggleButton(roles[2]);
		rolebtn[3] = new JToggleButton(roles[3]);
		for(int i=0;i<rolebtn.length;i++) {
			rolebtn[i].setFont(new Font("MV Boli", Font.BOLD , 40));
			rolebtn[i].setBounds(125, i*100+50, 400, 80);
			rolebtn[i].setBackground(new Color(193, 154, 107));
			subPanel.add(rolebtn[i]);
		}
		
		for(int i=0;i<labels.length;i++) {
			labels[i] = new JLabel();
			labels[i].setFont(new Font("Courier New", Font.BOLD | Font.ITALIC, 12));
			labels[i].setHorizontalAlignment(SwingConstants.CENTER);
			labels[i].setBounds(10, i*60+80, 420, 14);
			subPanel.add(labels[i]);
		}
		JButton btnLock = new JButton("LOCK IN");
		
		for(int i=0;i<rolebtn.length;i++) {
			final int k = i;
			rolebtn[i].addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					playerRole = roles[k];
					btnLock.setEnabled(true);
					if(rolebtn[k].isSelected()) {
						sendMessage(new myMessage(2,k+1));
						for (int j = 0; j < rolebtn.length; j++) {
							if (k != j && !roleTaken[j]) {
								rolebtn[j].setSelected(!rolebtn[k].isSelected());
								if(k!=3 || (k==3 && rolebtn[k].isSelected())) {
									sendMessage(new myMessage(2,(j+1)*-1));
								}
								
							}
						}
					} else {
						sendMessage(new myMessage(2,(k+1)*-1));
						btnLock.setEnabled(false);
					}
				}
			});
		}
		

		if(isGameHost) {
			configureButton(btnLock, 700, 410, mainPanel);
		} else {
			configureButton(btnLock, 700, 490, mainPanel);
		}
		btnLock.setEnabled(false);
		btnLock.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnLock.setEnabled(false);
				btnLock.setFont(new Font("MV Boli", Font.PLAIN, 60));
				sendMessage(new myMessage(1, true));
				readyToPlay = true;
				if(playersReady == outStreamList.size()) {
					new Thread(new Runnable() {
						public void run() {
							mainPanel.removeAll();
							frame.repaint();
							frame.revalidate();
							setGameUI();
						}
					}).start();
				}
			}
		});
		
		mainPanel.repaint();
		mainPanel.revalidate();
	}
	
	private void setGameUI() {
		courtArea = new JTextArea();
		((DefaultCaret) courtArea.getCaret()).setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		JScrollPane scrollPane1 = new JScrollPane();
		JScrollPane scrollPane2;
		
		if(playerRole == "Juror") {
			
			//Setup the courtroom chat box
			JLabel lblCourtroomsConversation = new JLabel("Courtroom's Conversation");
			lblCourtroomsConversation.setBackground(new Color(240, 240, 240));
			lblCourtroomsConversation.setFont(new Font("Times New Roman", Font.BOLD | Font.ITALIC, 15));
			scrollPane1.setColumnHeaderView(lblCourtroomsConversation);
			courtArea.setForeground(Color.BLACK);
			courtArea.setFont(new Font("Times New Roman", Font.BOLD | Font.ITALIC, 15));
			courtArea.setEditable(false);
			courtArea.setLineWrap(true);
			courtArea.setWrapStyleWord(true);
			scrollPane1.setViewportView(courtArea);
			
			//setup jury chatbox
			scrollPane2 = new JScrollPane();
			juryArea = new JTextArea();
			juryArea.setForeground(Color.BLACK);
			juryArea.setFont(new Font("Times New Roman", Font.BOLD | Font.ITALIC, 15));
			juryArea.setEditable(false);
			juryArea.setLineWrap(true);
			juryArea.setWrapStyleWord(true);
			((DefaultCaret) juryArea.getCaret()).setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
			scrollPane2.setViewportView(juryArea);
			JLabel lblJurorsConversation = new JLabel("Jury's Conversation");
			lblJurorsConversation.setFont(new Font("Times New Roman", Font.BOLD | Font.ITALIC, 15));
			lblJurorsConversation.setBackground(SystemColor.menu);
			scrollPane2.setColumnHeaderView(lblJurorsConversation);

			//setup the chat boxes
			JSplitPane chatSplitter = new JSplitPane();
			chatSplitter.setBounds(10,10, 510, 350);
			chatSplitter.setLeftComponent(scrollPane1);
			chatSplitter.setRightComponent(scrollPane2);
			chatSplitter.setResizeWeight(0.5);
			chatSplitter.setContinuousLayout(true);
			mainPanel.add(chatSplitter);
			
			//setup the message input field
			inputField.setForeground(Color.BLACK);
			inputField.setFont(new Font("Times New Roman", Font.BOLD | Font.ITALIC, 15));
			inputField.setBounds(10, 370, 435, 20);
			mainPanel.add(inputField);
			
			//setup the send button
			btnSend.setForeground(Color.BLACK);
			btnSend.setBackground(UIManager.getColor("Button.light"));
			btnSend.setFont(new Font("Times New Roman", Font.BOLD | Font.ITALIC, 15));
			btnSend.setBounds(445, 370, 75, 20);
			mainPanel.add(btnSend);
		} else {
			//Setup the courtroom chat box
			JLabel lblCourtroomsConversation = new JLabel("Courtroom's Conversation");
			lblCourtroomsConversation.setBackground(new Color(240, 240, 240));
			lblCourtroomsConversation.setFont(new Font("Times New Roman", Font.BOLD | Font.ITALIC, 15));
			scrollPane1.setColumnHeaderView(lblCourtroomsConversation);
			courtArea.setForeground(Color.BLACK);
			courtArea.setFont(new Font("Times New Roman", Font.BOLD | Font.ITALIC, 15));
			courtArea.setEditable(false);
			courtArea.setLineWrap(true);
			courtArea.setWrapStyleWord(true);
			scrollPane1.setViewportView(courtArea);
			scrollPane1.setBounds(10, 10, 340, 350);
			mainPanel.add(scrollPane1);

			//setup the message input field
			inputField.setForeground(Color.BLACK);
			inputField.setFont(new Font("Times New Roman", Font.BOLD | Font.ITALIC, 15));
			inputField.setBounds(10, 370, 430, 20);
			mainPanel.add(inputField);
			
			//setup the send button
			btnSend.setForeground(Color.BLACK);
			btnSend.setBackground(UIManager.getColor("Button.light"));
			btnSend.setFont(new Font("Times New Roman", Font.BOLD | Font.ITALIC, 15));
			btnSend.setBounds(450, 370, 75, 20);
			mainPanel.add(btnSend);
			
			if(playerRole.equals("Judge")) {
				bg.add(rdbtnNewRadioButton);
				bg.add(rdbtnDefenseLawyer);
				
				JButton btnBeginTrial = new JButton("BEGIN TRIAL");
				btnBeginTrial.setForeground(Color.BLACK);
				btnBeginTrial.setFont(new Font("Times New Roman", Font.BOLD | Font.ITALIC, 15));
				btnBeginTrial.setBackground(SystemColor.controlHighlight);
				btnBeginTrial.setBounds(360, 10, 165, 30);
				mainPanel.add(btnBeginTrial);
				
				JButton btnDemandOrder = new JButton("DEMAND ORDER");
				btnDemandOrder.setForeground(Color.BLACK);
				btnDemandOrder.setFont(new Font("Times New Roman", Font.BOLD | Font.ITALIC, 15));
				btnDemandOrder.setBackground(SystemColor.controlHighlight);
				btnDemandOrder.setBounds(360, 50, 165, 30);
				btnDemandOrder.setEnabled(false);
				mainPanel.add(btnDemandOrder);
				
				JLabel lblWhoWillTalk = new JLabel("Who is allowed to speak?");
				lblWhoWillTalk.setForeground(Color.BLACK);
				lblWhoWillTalk.setFont(new Font("Times New Roman", Font.BOLD | Font.ITALIC, 15));
				lblWhoWillTalk.setBackground(SystemColor.menu);
				lblWhoWillTalk.setBounds(360, 91, 164, 20);
				mainPanel.add(lblWhoWillTalk);
				
				rdbtnNewRadioButton.setForeground(Color.BLACK);
				rdbtnNewRadioButton.setFont(new Font("Times New Roman", Font.BOLD | Font.ITALIC, 15));
				rdbtnNewRadioButton.setBounds(370, 115, 155, 20);
				rdbtnNewRadioButton.setEnabled(false);
				mainPanel.add(rdbtnNewRadioButton);
				
				rdbtnDefenseLawyer.setForeground(Color.BLACK);
				rdbtnDefenseLawyer.setFont(new Font("Times New Roman", Font.BOLD | Font.ITALIC, 15));
				rdbtnDefenseLawyer.setBounds(370, 140, 155, 20);
				rdbtnDefenseLawyer.setEnabled(false);
				mainPanel.add(rdbtnDefenseLawyer);
				
				JButton btnDecide = new JButton("DECIDE");
				btnDecide.setForeground(Color.BLACK);
				btnDecide.setFont(new Font("Times New Roman", Font.BOLD | Font.ITALIC, 15));
				btnDecide.setBackground(SystemColor.controlHighlight);
				btnDecide.setBounds(360, 220, 165, 30);

				btnDecide.setEnabled(false);
				mainPanel.add(btnDecide);
				
				JButton btnCallForPartial = new JButton("PARTIAL VERDICT");
				btnCallForPartial.setForeground(Color.BLACK);
				btnCallForPartial.setFont(new Font("Times New Roman", Font.BOLD | Font.ITALIC, 14));
				btnCallForPartial.setBackground(SystemColor.controlHighlight);
				btnCallForPartial.setBounds(360, 180, 165, 30);
				btnCallForPartial.setEnabled(false);
				mainPanel.add(btnCallForPartial);

				
				//Events
				btnBeginTrial.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						btnBeginTrial.setEnabled(false);
						btnDemandOrder.setEnabled(true);
						rdbtnNewRadioButton.setEnabled(true);
						rdbtnDefenseLawyer.setEnabled(true);
						btnDecide.setEnabled(true);
						btnCallForPartial.setEnabled(true);
						courtArea.append("\nYou: Let the trial begin! I allow the prosecutor to talk first.");
						sendMessage(new myMessage(3, "Let the trial begin! I allow the prosecutor to talk first.", playerRole, playerName));
						sendMessage(new myMessage(4, (Object) "Prosecutor"));
						rdbtnNewRadioButton.setSelected(true);
						rdbtnNewRadioButton.setEnabled(false);
						rdbtnDefenseLawyer.setEnabled(false);
					}
				});
				
				btnDemandOrder.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						courtArea.append("\nYou: ORDER IN THE COURT!");
						sendMessage(new myMessage(3, "ORDER IN THE COURT!", playerRole, playerName));
						sendMessage(new myMessage(4, null));
					}
				});
				
				btnCallForPartial.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						courtArea.append("\nYou: Members of the Juror, vote for partial verdict!");
						sendMessage(new myMessage(3, "Members of the Juror, vote for partial verdict!", playerRole, playerName));
						sendMessage(new myMessage(5));
					}
				});
				
				btnDecide.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						
					}
				});
			} else if (playerRole.equals("Prosecutor") || playerRole.equals("Defense Lawyer")) {
				if(playerRole.equals("Prosecutor")) {
					btnPresent = new JButton("PRESENT EVIDENCE");
				}else {
					btnPresent = new JButton("PRESENT TESTIMONY");
				}
				btnPresent.setForeground(Color.BLACK);
				btnPresent.setFont(new Font("Times New Roman", Font.BOLD | Font.ITALIC, 12));
				btnPresent.setBackground(SystemColor.controlHighlight);
				btnPresent.setBounds(360, 10, 165, 30);
				btnPresent.setEnabled(false);
				mainPanel.add(btnPresent);
				
				btnObject = new JButton("OBJECT!");
				btnObject.setForeground(Color.BLACK);
				btnObject.setFont(new Font("Times New Roman", Font.BOLD | Font.ITALIC, 14));
				btnObject.setBackground(SystemColor.controlHighlight);
				btnObject.setBounds(360, 50, 165, 30);
				btnObject.setEnabled(false);
				mainPanel.add(btnObject);
				
				//Events
				btnPresent.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						//can only present one evidence/testimony
						btnPresent.setEnabled(false);
						if(playerRole.equals("Prosecutor")) {
							courtArea.append("\nYou: I would like to present an evidence!");
							sendMessage(new myMessage(3, "I would like to present an evidence!", playerRole, playerName));
						}else {
							courtArea.append("\nYou: I would like to present a testimony!");
							sendMessage(new myMessage(3, "I would like to present a testimony!", playerRole, playerName));
						}
					}
				});
				
				btnObject.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						courtArea.append("\nYou: OBJECTION YOUR HONOR!");
						sendMessage(new myMessage(3, "OBJECTION YOUR HONOR!", playerRole, playerName));
						sendMessage(new myMessage(11, playerRole));
					}
				});
			}
		}
		
		inputField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(inputField.getText().length() > 0) {
					sendMessage(new myMessage (3,inputField.getText(), playerRole, playerName));
					
					if(playerRole.equals("Juror")) {
						juryArea.append("\nYou: " + inputField.getText());
					} else {
						courtArea.append("\nYou: " + inputField.getText());
					}
					
					inputField.setText("");
				}
			}
		});
		
		btnSend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(inputField.getText().length() > 0) {
					sendMessage(new myMessage (3,inputField.getText(), playerRole, playerName));
					
					if(playerRole.equals("Juror")) {
						juryArea.append("\nYou: " + inputField.getText());
					} else {
						courtArea.append("\nYou: " + inputField.getText());
					}
					
					inputField.setText("");
				}
			}
		});
		
		frame.repaint();
		frame.revalidate();
	}
}
