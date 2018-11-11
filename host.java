
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
	
	public host(int port) {
		
		playerName = JOptionPane.showInputDialog("Input Username");
	
		initGUI();
		this.port = port;

		btnConnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new Thread(new Runnable() {
					public void run() {
						try {
							startRunning(JOptionPane.showInputDialog("IP Address please"), 5678);
						} catch (HeadlessException | SocketException e) {
							e.printStackTrace();
						}
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
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		
		new Thread(new Runnable() {
			public void run() {
				startServer();
			}
		}).start();
	}

	private void startServer(){
		try {
			textArea.setEditable(false);
			serverSocket = new ServerSocket(port);
			ip = InetAddress.getLocalHost().getHostAddress();
			textArea.append("Server IP Address: " + ip);
			textArea.append("\nPort: " + port);
			waitForClients();
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
					if (i != Byte.toUnsignedInt(localIP[3])) {
						final int j = i;
						new Thread(new Runnable() {
							public void run() {
								String host = subnet + "." + j;
								System.out.println("Trying " + host);

								while(true) {
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
						}).start();
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
					System.out.println(message.getMessage().toString());
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
						textArea.append("\n"+playerName+"("+playerRole+"): "+(String)message.getMessage());
					}else if(message.isType(4)){
						textArea2.append("\n"+playerName+"("+playerRole+"): "+(String)message.getMessage());
					}
					
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} catch (SocketException e) {
					textArea.append("\nConnection with the server was lost.");
					break;
				}
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (ConnectException e) {
			//do nothing
		} catch (IOException e) {
			e.printStackTrace();
		}
	
	}
	
	private void sendMessage(myMessage message) {
		for(int i=0;i<players.size();i++) {
			players.get(i).writeObject(message);
		}
		if(message.isType(3)){
			textArea.append("\n"+playerName+"("+playerRole+"): "+(String)message.getMessage());	
		}else if(message.isType(4)){
			textArea2.append("\n"+playerName+"("+playerRole+"): "+(String)message.getMessage());
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
				if(playerRole=="Defense Lawyer" || playerRole=="Prosecutor"){
					setupchatUI(playerRole);
				}else if(playerRole=="Jury" || playerRole=="Judge"){
					setupchatUI(playerRole);
					setupchatUI2();
				}
				
			}
		});
	}
	private void setupchatUI(String playerRole) {
		removeElements();
		
		mainPanel.setLayout(new BorderLayout());
		textArea.setEditable(false);
		mainPanel.add(textArea, BorderLayout.CENTER);
		if(playerRole!="Jury"){
			JTextField inputField = new JTextField();
			inputField.setColumns(10);
			mainPanel.add(inputField, BorderLayout.SOUTH);
			inputField.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					sendMessage(new myMessage (3,inputField.getText()));
					System.out.println(inputField.getText());
					inputField.setText("");
				}
			});
		}
	}
	private void setupchatUI2() {

		EventQueue.invokeLater(new Runnable(){
			@Override
			public void run(){
				
				frame = new JFrame("Jury");
				frame.setBounds(100, 100, 450, 357);
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				
				JPanel panel = new JPanel();
				panel.setLayout(null);
				frame.getContentPane().add(panel, BorderLayout.CENTER);
				frame.setResizable(false);
	
				JTextField inputField2 = new JTextField();
				textArea2.setBounds(0, 0, 432, 265);
				panel.add(textArea2);
				textArea2.setEditable(false);
				
				inputField2.setBounds(10, 277, 410, 22);
				panel.add(inputField2);
				inputField2.setColumns(10);
	
				frame.setVisible(true);
				inputField2.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						sendMessage(new myMessage (4,inputField2.getText()));
						System.out.println(inputField2.getText());
						inputField2.setText("");
					}
				});
			}
			
		});
		
	}
	/*public void chatUI(String Name) {
		EventQueue.invokeLater(new Runnable(){
			@Override
			public void run(){

				
				frame = new JFrame(Name);
				frame.setBounds(100, 100, 450, 357);
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				
				JPanel panel = new JPanel();
				panel.setLayout(null);
				frame.getContentPane().add(panel, BorderLayout.CENTER);
				frame.setResizable(false);
				
				textArea2.setBounds(0, 0, 432, 265);
				panel.add(textArea2);
				textArea2.setEditable(false);
				
				textField = new JTextField();
				textField.setBounds(10, 277, 410, 22);
				panel.add(textField);
				textField.setColumns(10);
				frame.setVisible(true);
			}
		
			
		});		
		textField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(!textField.getText().isEmpty()) {
					sendMessage(new myMessage (3,textField.getText()));
					textField.setText("");
					
				}
			}
		});
	}*/
	 	
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
