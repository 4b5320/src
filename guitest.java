
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;

import javax.swing.*;
import javax.swing.text.DefaultCaret;

import net.miginfocom.swing.MigLayout;

public class guitest {

	private JFrame frame;
	private JTextField textField;
	int posa = 2;
	JLabel[][] clients = new JLabel[1][5];
	JPanel mainPanel = new JPanel();
	JButton btnNewButton = new JButton("New button");
	private ServerSocket serverSocket;
	LinkedList<ObjectOutputStream> outputStreamList = new LinkedList<ObjectOutputStream>();
	LinkedList<Object[]> serverList = new LinkedList<Object[]>();
	private String localIP = null;
	int localPort = new Random().nextInt(8999)+1000;
	int clientPorta;

	/**
	 * Launch the application.
	 */
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
	}

	/**
	 * Create the application.
	 */
	public guitest() {
		initialize();
		
		//Start my server
		try {
			serverSocket = new ServerSocket(localPort);
			localIP = InetAddress.getLocalHost().getHostAddress();
			frame.setTitle("IP: " + localIP + "; Server port: " + localPort);

			//Wait for handshakes
			new Thread(new Runnable() {
				public void run() {
					while(true){
						try {
							Socket socket = serverSocket.accept();
							//Wait for the client to send his IP and server port, thread this
							new Thread(new Runnable() {
								public void run() {
									try {
										myMessage clientInfo = (myMessage) new ObjectInputStream(socket.getInputStream()).readObject();
										String clientIP = (String) ((Object[]) clientInfo.getMessage())[0];
										int clientPort = (int) ((Object[]) clientInfo.getMessage())[1];
										System.out.println("Receiving client port is " + clientPort);
										clientPorta = clientPort;
										boolean isServerUnique = true;
										for(Object[] obj : serverList) {
											if(((String) obj[0]).equals(clientIP) && ((int) obj[1]) == clientPort) {
												isServerUnique = false;
												break;
											}
										}
										if(isServerUnique) {
											connectToServer(clientIP, clientPort);
										}
									} catch (ClassNotFoundException | IOException e) {}
									
									
								}
							}).start();
						} catch (SocketException e) {
							break;
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}).start();
		} catch (BindException e) {
			JOptionPane.showMessageDialog(new JFrame(), "Port number already in use, cannot start server", "Startup Error", JOptionPane.OK_OPTION);
			System.exit(1);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String[] x = JOptionPane.showInputDialog("").split(";");
				new Thread(new Runnable() {
					public void run() {
						connectToServer(x[0], Integer.parseInt(x[1]));
					}
				}).start();
			}
		});
		frame.repaint();
		frame.revalidate();
	}
	
	private void connectToServer(String clientIP, int clientPort) {
		serverList.add(new Object[] {clientIP, clientPort});
		new Thread(new Runnable() {
			public void run() {
				try {
					//Connect to my client's server
					System.out.println(localIP + " " + localPort + " connects to "
							+ clientIP + " " + clientPort + " Servers: " + serverList.size());
					Socket server = new Socket(clientIP, clientPort);
					//Wait for the client to setup his input stream before I send my server ip and port
					try { Thread.sleep(1000); } catch (InterruptedException e1) {}
					//Send my IP and port
					new ObjectOutputStream(server.getOutputStream()).writeObject(new myMessage(0, new Object[] {localIP, localPort}));
					
					//Set up the input stream
					ObjectInputStream in = new ObjectInputStream(server.getInputStream());
					//Receive messages from this server
					while(true) {
						myMessage message = (myMessage) in.readObject();
						System.out.println(localIP + " " + localPort + " received " + clientIP + " " + clientPort);
						if(message.isType(0)) {
							String clientIP = (String) ((Object[]) message.getMessage())[0];
							int clientPort = (int) ((Object[]) message.getMessage())[1];
							if(!clientIP.equals(localIP) && clientPort != localPort) {
								boolean isServerUnique = true;
								for(Object[] obj : serverList) {
									if(((String) obj[0]).equals(clientIP) && ((int) obj[1]) == clientPort) {
										isServerUnique = false;
										break;
									}
								}
								if(isServerUnique) {
									connectToServer(clientIP, clientPort);
								}
							}
						}
					}
					
				} catch (SocketException e) {} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setResizable(true);
		frame.setBounds(100, 100, 540, 430);
		frame.getContentPane().setLayout(new BorderLayout());
		frame.setVisible(true);
		JScrollPane pane = new JScrollPane();
		pane.setViewportView(mainPanel);
		mainPanel.setLayout(new MigLayout("", "[][][][][]", "[][][][]"));
		frame.getContentPane().add(pane, BorderLayout.CENTER);
		
		JLabel lblNewLabel = new JLabel("  ID  ");
		lblNewLabel.setBorder(null);
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setFont(new Font("Times New Roman", Font.BOLD, 15));
		mainPanel.add(lblNewLabel, "cell 0 0,growx");
		
		JLabel lblIpAddress = new JLabel("  IP Address  ");
		lblIpAddress.setBorder(null);
		lblIpAddress.setHorizontalAlignment(SwingConstants.CENTER);
		lblIpAddress.setFont(new Font("Times New Roman", Font.BOLD, 15));
		
		mainPanel.add(lblIpAddress, "cell 1 0,growx");
		
		JLabel lblServerPort = new JLabel("  Server Port  ");
		lblServerPort.setBorder(null);
		lblServerPort.setHorizontalAlignment(SwingConstants.CENTER);
		lblServerPort.setFont(new Font("Times New Roman", Font.BOLD, 15));
		mainPanel.add(lblServerPort, "cell 2 0,growx");
		
		JLabel lblUsername = new JLabel("  Username  ");
		lblUsername.setBorder(null);
		lblUsername.setHorizontalAlignment(SwingConstants.CENTER);
		lblUsername.setFont(new Font("Times New Roman", Font.BOLD, 15));
		mainPanel.add(lblUsername, "cell 3 0,growx");
		
		JLabel lblStatus = new JLabel("  Status  ");
		lblStatus.setBorder(null);
		lblStatus.setHorizontalAlignment(SwingConstants.CENTER);
		lblStatus.setFont(new Font("Times New Roman", Font.BOLD, 15));
		mainPanel.add(lblStatus, "cell 4 0,growx");
		
		mainPanel.add(btnNewButton, "cell 0 2 5 1,growx");
		
	}
	
	private void addClient() {
		frame.remove(btnNewButton);
		mainPanel.add(btnNewButton, "cell 0 " + (++posa) + " 5 1,growx");
		
		JLabel[][] oldClients = new JLabel[clients.length][5];
		for(int i=0;i<clients.length;i++) {
			for(int j=0;j<clients[i].length;j++) {
				oldClients[i][j] = clients[i][j];
			}
		}
		clients = new JLabel[oldClients.length+1][5];
		for(int i=0;i<clients.length;i++) {
			for(int j=0;j<clients[i].length;j++) {
				
				if(i == clients.length-1) {
					clients[i][j] = new JLabel();
					switch(j) {
						case 0:
							clients[i][j].setText(String.valueOf(i));
							break;
						case 1:
							clients[i][j].setText("192.168.254.254");
							break;
						case 2:
							clients[i][j].setText("5678");
							break;
						case 3:
							clients[i][j].setText("player68pussy");
							break;
						case 4:
							clients[i][j].setText("Connected");
							break;
					}
					clients[i][j].setHorizontalAlignment(SwingConstants.CENTER);
					clients[i][j].setFont(new Font("Times New Roman", Font.BOLD, 15));
					mainPanel.add(clients[i][j], "cell " + j + " " + ((int) i+1) + ",growx");
				}else {
					clients[i][j] = oldClients[i][j];
				}
			}
		}
		
		frame.repaint();
		frame.revalidate();
	}
}
