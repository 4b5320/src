
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.LinkedList;
import java.util.UUID;

import javax.swing.*;
import javax.swing.text.DefaultCaret;

public class host extends JFrame{
	private String ip;
	private String playerName = null;
	private String playerRole = null;
	private LinkedList<player> players;
	private ServerSocket serverSocket;
	private int port = 5678;
	private boolean readyToPlay = false;
	private int playersReady = 0;
	private int timeout = 10, timer = 0;
	
	private static JFrame frame = new JFrame();
	private JTextArea textArea = new JTextArea();
	private JTextArea textArea2 = new JTextArea();
	private JProgressBar progressBar = new JProgressBar();
	private JButton btnConnect = new JButton("CONNECT");
	private JButton btnStart = new JButton("START");
	private JPanel mainPanel = new JPanel();
	private JTextField nameField;
	private JToggleButton[] rolebtn;
	private Boolean[] roleTaken = {false, false, false, false};
	private String roles[] = {"Judge","Defense Lawyer","Prosecutor","Juror"};
	private LinkedList<String> clientList = new LinkedList<String>();
	

	private JTextArea courtArea;
	private JTextArea juryArea;
	private JTextField inputField = new JTextField();
	private int guilty = 0, notguilty = 0, numberOfJuror=0;
	JRadioButton rdbtnNewRadioButton = new JRadioButton("Prosecutor");
	JRadioButton rdbtnDefenseLawyer = new JRadioButton("Defense Lawyer");
	ButtonGroup bg = new ButtonGroup();
	JButton btnSend = new JButton("SEND");

	private JButton btnPresent, btnObject, btnLeave;
	
	public host(int port) {
		
		//playerName = JOptionPane.showInputDialog("Input Username");
		playerName = UUID.randomUUID().toString().split("-")[0];
		
		initGUI();
		this.port = port;

		btnConnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new Thread(new Runnable() {
					public void run() {
						try {
							byte[] localIP = InetAddress.getLocalHost().getAddress();
							String subnet = Byte.toUnsignedInt(localIP[0]) + "." + Byte.toUnsignedInt(localIP[1]) + "." + Byte.toUnsignedInt(localIP[2]);
							String myIP = subnet + "." + Byte.toUnsignedInt(localIP[3]);
							
							for(int i=1;i<=6;i++) {
								final int ports = i;
								new Thread(new Runnable() {
									public void run() {
										try {
											if(port != ports) {
												startRunning(myIP, ports);
											}
										} catch (SocketException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
									}
								}).start();
							}
							
							/*JFrame inputFrame = new JFrame();
							inputFrame.setVisible(true);
							inputFrame.getContentPane().setBackground(new Color(192, 192, 192));
							inputFrame.setResizable(false);
							inputFrame.setBounds(500, 500, 250, 360);
							inputFrame.getContentPane().setLayout(null);
							
							JLabel lblEnterIpAddresses = new JLabel("Enter IP Addresses of servers\r\n");
							lblEnterIpAddresses.setForeground(Color.BLACK);
							lblEnterIpAddresses.setFont(new Font("Times New Roman", Font.BOLD, 15));
							lblEnterIpAddresses.setBounds(10, 11, 224, 25);
							inputFrame.getContentPane().add(lblEnterIpAddresses);
							
							JLabel lblseparareByNew = new JLabel("(separare by new lines)");
							lblseparareByNew.setForeground(Color.BLACK);
							lblseparareByNew.setFont(new Font("Times New Roman", Font.BOLD, 15));
							lblseparareByNew.setBounds(10, 29, 224, 18);
							inputFrame.getContentPane().add(lblseparareByNew);
							
							
							JTextArea ipArea = new JTextArea(subnet+".");
							ipArea.setForeground(Color.BLACK);
							ipArea.setCaretPosition(ipArea.getText().length());
							ipArea.setBounds(10, 60, 225, 225);
							ipArea.addKeyListener(new KeyAdapter() {
								public void keyReleased(KeyEvent e) {
									if(e.getKeyCode() == KeyEvent.VK_ENTER) {
										ipArea.append(subnet + ".");
									}
								}
							});
							inputFrame.getContentPane().add(ipArea);

							JButton btnNewButton = new JButton("Connect to servers");
							btnNewButton.setFont(new Font("Times New Roman", Font.BOLD, 15));
							btnNewButton.setBounds(10, 300, 224, 23);
							inputFrame.getContentPane().add(btnNewButton);
							btnNewButton.addActionListener(new ActionListener() {
								public void actionPerformed(ActionEvent e) {
									String[] list = ipArea.getText().split("\n");
									for(String s : list) {
										new Thread(new Runnable() {
											public void run() {
												try {
													if(s.split(";").length>1) {
														if(!s.split(";")[0].equals(myIP) || Integer.parseInt(s.split(";")[1]) != port) {
															startRunning(s.split(";")[0], Integer.parseInt(s.split(";")[1]));
														}
														
													} else {
														startRunning(s, 5678);
													}
													
												} catch (SocketException e1) {
													e1.printStackTrace();
												}
											}
										}).start();
									}
									inputFrame.dispose();
								}
							});
							
							ipArea.requestFocus();*/
						} catch (UnknownHostException e) { }
					}
				}).start();
			}
		});

		btnStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				readyToPlay = true;
				btnStart.setEnabled(false);
				sendMessage(new myMessage(1, true));
				if(playersReady == players.size()) {
					//All players are ready
					textArea.append("\nAll players are ready");
					setupPanel2();
				}
			}
		});
		setupPanel1();
		
		for(int i=1;i<=100;i++) {
			try {
				Thread.sleep(3);
				progressBar.setValue(i);
			} catch (InterruptedException e1) { }
		}
		
		new Thread(new Runnable() {
			public void run() {
				startServer();
			}
		}).start();
		
		for(int i=1;i<=100;i++) {
			try {
				Thread.sleep(3);
				progressBar.setValue(i);
			} catch (InterruptedException e1) { }
		}
		
		//connectToServer(5678);
	}

	private void startServer(){
		try {
			textArea.setEditable(false);
			serverSocket = new ServerSocket(port);
			ip = InetAddress.getLocalHost().getHostAddress();
			textArea.append("Server IP Address: " + ip);
			textArea.append("\nPort: " + port);
			waitForClients();
		} catch (BindException e) {
			JOptionPane.showMessageDialog(new JFrame(), "Port number already in use, cannot start server", "Startup Error", JOptionPane.OK_OPTION);
			System.exit(ERROR);
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	
	//This is used when player wants to host the game
	private void waitForClients() {
		textArea.append("\nConnected hosts:\n");
		
		players = new LinkedList<player>();
		while(true){
			try {
				players.add(new player(serverSocket.accept()));
				textArea.append("\nConnected to " + players.getLast().toString());
				
				//Checks if ready to start, ready if there are at least 4 players
				if(players.size() >= 1) {
					btnStart.setEnabled(true);
				}
			} catch (SocketException e) {
				break;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void connectToServer(int port) {
		try {
			byte[] localIP = InetAddress.getLocalHost().getAddress();
			String subnet = Byte.toUnsignedInt(localIP[0]) + "." + Byte.toUnsignedInt(localIP[1]) + "." + Byte.toUnsignedInt(localIP[2]);
			
			if(subnet.equals("127.0.0")) {
				//If using local host, connect to same computer
				textArea.append("\nRunning on localhost");
				new Thread(new Runnable() {
					public void run() {
						try {
							System.out.println("Connecting to port " + port);
							startRunning("127.0.0.1", port);
						} catch (SocketException e) {
							System.out.println("Cannot connect to port " + port);
						}
					}
				}).start();
			} else {
				//else connect to other hosts only to prevent connecting to itself
				for(int i=0;i<256;i++) {
					if (i != Byte.toUnsignedInt(localIP[3]) || true) {
						final int j = i;
						String host = subnet + "." + j;
						System.out.println("Trying " + host);
						
						try {
							if (InetAddress.getByName(host).isReachable(1000)) {
								try {
									startRunning(host, 5678);
								} catch (SocketException e) {
									//do nothing
								}
							}
						} catch (UnknownHostException e) {
							textArea.append("\nHost not available " + host);
						} catch (IOException e) {
							textArea.append("\nIO exception");
						}
					}
				}
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}
	
	private boolean clientAlready(String host) {
		
		for(String client : clientList) {
			if(client.equals(host)) {
				return true;
			}
		}
		
		return false;
	}
	
	private void startRunning(String serverIPAdd, int port) throws SocketException{
		try {
			//Connect to the server
			Socket server = new Socket(InetAddress.getByName(serverIPAdd), port);
			textArea.append("\nConnected to port " + port + " at " + serverIPAdd);
			
			//Set up the input stream
			ObjectInputStream in = new ObjectInputStream(server.getInputStream());

			//clientList.add(serverIPAdd);
			//Receive messages from the server
			while(true) {
				try {
					myMessage message = (myMessage) in.readObject();
					if(message.isType(1) && message.getMessage().toString().equals("true")) {
						System.out.println("Message received.");
						playersReady++;
						textArea.append("\n" + playersReady + " players are ready.");
						if(readyToPlay && playersReady == players.size()) {
							//All players are ready
							textArea.append("\nAll players are ready");
							setupPanel2();
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
						}
					}else if(message.isType(3)) {
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
						int x = this.getX() + this.getWidth();
						int y = this.getY();
						new Thread(new Runnable() {
							public void run() {
								JFrame votingFrame = new JFrame();
								votingFrame.setVisible(true);
								votingFrame.setResizable(false);
								votingFrame.setBounds(x, y, 150, 165);
								votingFrame.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
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
							sendMessage(new myMessage(3, guilty + " out of " + numberOfJuror + " voted GUILTY!", playerRole, playerName));
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
					textArea.append("\nConnection with the server was lost.");
					break;
				}
			}
		} catch (UnknownHostException | ConnectException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	
	}
	
	private void sendMessage(myMessage message) {
		for(int i=0;i<players.size();i++) {
			players.get(i).writeObject(message);
		}
	}
	
	private void initGUI() {
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
	}
	
	private void removeElements() {
		for(Component c : mainPanel.getComponents()) {
			mainPanel.remove(c);
		}
		mainPanel.revalidate();
		mainPanel.repaint();
	}
	
	private void setupPanel1() {
		mainPanel.setLayout(null);
		
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
	
	private void setupPanel2() {
		removeElements();
		mainPanel.setLayout(null);
		rolebtn = new JToggleButton[4];
		JLabel[] labels = new JLabel[4];
		
		rolebtn[0] = new JToggleButton(roles[0]);
		rolebtn[1] = new JToggleButton(roles[1]);
		rolebtn[2] = new JToggleButton(roles[2]);
		rolebtn[3] = new JToggleButton(roles[3]);
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
		btnLock.setEnabled(true);
		mainPanel.add(btnLock);
		
		for(int i=0;i<rolebtn.length;i++) {
			final int k = i;
			rolebtn[i].addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					playerRole = roles[k];
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
					}
				}
			});
		}
		
		btnLock.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sendMessage(new myMessage(9));
				setupchatUI();
			}
		});
		
		mainPanel.repaint();
		mainPanel.revalidate();
	}
	private void setupchatUI() {
		removeElements();
		mainPanel.setLayout(null);
		this.setSize(540, 440);
		
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
	}
	 	
	public class player{
		private String playerName;
		private Socket socket;
		private ObjectOutputStream outStream;		
		public player(Socket socket) {
			try {
				this.socket = socket;
				this.outStream = new ObjectOutputStream(this.socket.getOutputStream());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		public void setName(String playerName) {
			this.playerName = playerName;
		}
		
		public String getName() {
			return playerName;
		}
		
		public void setReadyToPlay(boolean b) {
			readyToPlay = b;
		}
		
		public boolean isReadyToPlay() {
			return readyToPlay;
		}
		
		public void writeObject(myMessage message) {
			try {
				outStream.writeObject(message);
				System.out.println("The message is sent");
				outStream.flush();
			}catch(IOException e) {
				e.printStackTrace();
			}
		}
		
		public String toString() {
			return "Player["+getName()+";"+socket.getInetAddress().toString()+";"+socket.getPort()+"]";
		}
	}

}
