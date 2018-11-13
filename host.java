
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.LinkedList;
import javax.swing.*;

public class host extends JFrame{
	private String ip;
	private String playerName = null;
	private String playerRole = null;
	private LinkedList<player> players;
	private ServerSocket serverSocket;
	private int port = 5678;
	private boolean readyToPlay = false;
	private int playersReady = 0;
	private int timeout = 30, timer = 0;
	
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
	private String roles[] = {"Judge","Defense Lawyer","Prosecutor","Jury"};
	private LinkedList<String> clientList = new LinkedList<String>();
	

	private JTextArea courtArea;
	private JTextArea juryArea;
	private JTextField inputField = new JTextField();
	private int guilty = 0, notguilty = 0, numberOfJury=0;
	
	public host(int port) {
		
		//playerName = JOptionPane.showInputDialog("Input Username");
		
		initGUI();
		this.port = port;

		btnConnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new Thread(new Runnable() {
					public void run() {
						try {
							byte[] localIP = InetAddress.getLocalHost().getAddress();
							String subnet = Byte.toUnsignedInt(localIP[0]) + "." + Byte.toUnsignedInt(localIP[1]) + "." + Byte.toUnsignedInt(localIP[2]);
							
							JFrame inputFrame = new JFrame();
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
														startRunning(s.split(";")[0], Integer.parseInt(s.split(";")[1]));
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
							
							ipArea.requestFocus();
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
		
		connectToServer(5678);
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
						} else {
							if(value > 0) {
								numberOfJury++;
							} else {
								numberOfJury--;
							}
						}
					}else if(message.isType(3)) {
						System.out.println("Received role: " + message.getRoleOfSource());
						if(playerRole.equals("Jury")) {
							if(message.getRoleOfSource().equals("Jury")) {
								juryArea.append("\nJury " + message.getSource() + ": " + (String) message.getMessage());
							} else {
								courtArea.append("\n" + message.getRoleOfSource() + " " + message.getSource() + ": " + (String) message.getMessage());
								courtArea.setCaretPosition(courtArea.getText().length());
							}
						} else if(!message.getRoleOfSource().equals("Jury")){
							courtArea.append("\n" + message.getRoleOfSource() + " " + message.getSource() + ": " + (String) message.getMessage());
						}
					}else if(message.isType(4) && (playerRole.equals("Prosecutor") || playerRole.equals("Defense Lawyer"))) {
						if (playerRole.equals((String) message.getMessage())) {
							inputField.setEnabled(true);
							progressBar.setMaximum(timeout);
							timer = 0;
							while (timeout > timer) {
								progressBar.setValue(timer);
								try {
									Thread.sleep(1000);
								} catch (InterruptedException e) {
								}
								timer++;
							}
							inputField.setEnabled(false);
							if (timer == timeout) {
								if (playerRole.equals("Prosecutor")) {
									sendMessage(new myMessage(4, "Defense Lawyer"));
								} else {
									sendMessage(new myMessage(4, "Prosecutor"));
								} 
							} 
						} else {
							timer = timeout+1;
							progressBar.setValue(timeout);
						}
					}else if(message.isType(5) && playerRole.equals("Jury")) {
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
						
						if(numberOfJury == guilty + notguilty) {
							sendMessage(new myMessage(3, guilty + " out of " + numberOfJury + " voted GUILTY!", playerRole, playerName));
						}
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
								sendMessage(new myMessage(2,(j+1)*-1));
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
		JScrollPane scrollPane1 = new JScrollPane();
		JScrollPane scrollPane2;
		JButton btnNewButton = new JButton("SEND");
		
		if(playerRole == "Jury") {
			
			//Setup the courtroom chat box
			JLabel lblCourtroomsConversation = new JLabel("Courtroom's Conversation");
			lblCourtroomsConversation.setBackground(new Color(240, 240, 240));
			lblCourtroomsConversation.setFont(new Font("Times New Roman", Font.BOLD | Font.ITALIC, 15));
			scrollPane1.setColumnHeaderView(lblCourtroomsConversation);
			courtArea.setForeground(Color.BLACK);
			courtArea.setFont(new Font("Times New Roman", Font.BOLD | Font.ITALIC, 15));
			courtArea.setEditable(false);
			scrollPane1.setViewportView(courtArea);
			
			//setup jury chatbox
			scrollPane2 = new JScrollPane();
			juryArea = new JTextArea();
			juryArea.setForeground(Color.BLACK);
			juryArea.setFont(new Font("Times New Roman", Font.BOLD | Font.ITALIC, 15));
			juryArea.setEditable(false);
			scrollPane2.setViewportView(juryArea);
			JLabel lblJurysConversation = new JLabel("Jury's Conversation");
			lblJurysConversation.setFont(new Font("Times New Roman", Font.BOLD | Font.ITALIC, 15));
			lblJurysConversation.setBackground(SystemColor.menu);
			scrollPane2.setColumnHeaderView(lblJurysConversation);

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
			btnNewButton.setForeground(Color.BLACK);
			btnNewButton.setBackground(UIManager.getColor("Button.light"));
			btnNewButton.setFont(new Font("Times New Roman", Font.BOLD | Font.ITALIC, 15));
			btnNewButton.setBounds(445, 370, 75, 20);
			mainPanel.add(btnNewButton);
		} else {
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
			
			if(playerRole.equals("Judge")) {
				ButtonGroup bg = new ButtonGroup();
				JRadioButton rdbtnNewRadioButton = new JRadioButton("Prosecutor");
				JRadioButton rdbtnDefenseLawyer = new JRadioButton("Defense Lawyer");
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
				mainPanel.add(rdbtnNewRadioButton);
				
				rdbtnDefenseLawyer.setForeground(Color.BLACK);
				rdbtnDefenseLawyer.setFont(new Font("Times New Roman", Font.BOLD | Font.ITALIC, 15));
				rdbtnDefenseLawyer.setBounds(370, 140, 155, 20);
				mainPanel.add(rdbtnDefenseLawyer);
				
				JButton btnDecide = new JButton("DECIDE");
				btnDecide.setForeground(Color.BLACK);
				btnDecide.setFont(new Font("Times New Roman", Font.BOLD | Font.ITALIC, 15));
				btnDecide.setBackground(SystemColor.controlHighlight);
				btnDecide.setBounds(360, 220, 165, 30);
				mainPanel.add(btnDecide);
				
				JButton btnCallForPartial = new JButton("PARTIAL VERDICT");
				btnCallForPartial.setForeground(Color.BLACK);
				btnCallForPartial.setFont(new Font("Times New Roman", Font.BOLD | Font.ITALIC, 14));
				btnCallForPartial.setBackground(SystemColor.controlHighlight);
				btnCallForPartial.setBounds(360, 180, 165, 30);
				mainPanel.add(btnCallForPartial);
				
				//Events
				btnBeginTrial.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						btnBeginTrial.setEnabled(false);
						sendMessage(new myMessage(3, "I allow the prosecutor to talk first.", playerRole, playerName));
						sendMessage(new myMessage(4, "Prosecutor"));
					}
				});
				
				btnDemandOrder.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						sendMessage(new myMessage(3, "ORDER IN THE COURT!", playerRole, playerName));
						sendMessage(new myMessage(4, null));
					}
				});
				
				btnCallForPartial.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						sendMessage(new myMessage(3, "Members of the Jury, vote for partial verdict!", playerRole, playerName));
						sendMessage(new myMessage(5));
					}
				});
				
				btnDecide.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						
					}
				});
			} else if (playerRole.equals("Prosecutor")) {
				JButton btnBeginTrial = new JButton("PRESENT EVIDENCE");
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
				
				//Events
				btnBeginTrial.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						sendMessage(new myMessage(3, "I would like to present an evidence!", playerRole, playerName));
					}
				});
				
				btnCallForPartial.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						sendMessage(new myMessage(3, "I OBJECT!", playerRole, playerName));
						sendMessage(new myMessage(4, playerRole));
					}
				});
				
				btnLeave.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						sendMessage(new myMessage(3, "I'm leaving!!", playerRole, playerName));
						sendMessage(new myMessage(7));
					}
				});
			} else if (playerRole.equals("Defense Lawyer")) {
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
				
				//Events
				btnBeginTrial.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						sendMessage(new myMessage(3, "I would like to present a witness' testimony!", playerRole, playerName));
					}
				});
				
				btnCallForPartial.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						sendMessage(new myMessage(3, "I OBJECT!", playerRole, playerName));
						sendMessage(new myMessage(4, playerRole));
					}
				});
				
				btnLeave.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						sendMessage(new myMessage(3, "I'm leaving!!", playerRole, playerName));
						sendMessage(new myMessage(7));
					}
				});
			}
		}
		
		inputField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sendMessage(new myMessage (3,inputField.getText(), playerRole, playerName));
				
				if(playerRole.equals("Jury")) {
					juryArea.append("\nYou: " + inputField.getText());
				} else {
					courtArea.append("\nYou: " + inputField.getText());
				}
				
				inputField.setText("");
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
