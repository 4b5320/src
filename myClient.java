import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import javax.swing.JTextArea;

public class myClient{
	private Socket server = null;
	private ObjectOutputStream out;
	private ObjectInputStream in;
	private String message = "";
	private String clientName = "Client";
	protected JTextArea textArea;
	
	public myClient() {
		textArea = new JTextArea();
		textArea.setBounds(10, 146, 310, 118);
		connectToServer();
	}
	
	public void startRunning(String serverIPAdd) throws SocketException{
		
		try {
			//Connect to the server
			server = new Socket(InetAddress.getByName(serverIPAdd), 5678);
			
			//Set up the streams to and from the server
			out = new ObjectOutputStream(server.getOutputStream());
			out.flush();
			in = new ObjectInputStream(server.getInputStream());
			textArea.append("\n" + "Streams are now setup");
			
			//Receive messages from the server
			while(true) {
				try {
					message = in.readObject().toString();
					textArea.append("\n" + message);
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
	
	protected void sendMessage(String message) {
		try {
			System.out.println("Sending to server");
			out.writeObject(clientName + ": " + message);
			out.flush();
			System.out.println("Sent");
		}catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	protected void connectToServer() {
		try {
			byte[] localIP = InetAddress.getLocalHost().getAddress();
			String subnet = Byte.toUnsignedInt(localIP[0]) + "." + Byte.toUnsignedInt(localIP[1]) + "." + Byte.toUnsignedInt(localIP[2]);
			while(true) {
				if(server != null) {
					break;
				}
				for(int i=0;i<256;i++) {
					final int j = i;
					if(server != null) {
						break;
					}
					new Thread(
						new Runnable() {
							public void run() {
								String host = subnet + "." + j;
								try {
									if(InetAddress.getByName(host).isReachable(1000)) {
										try {
											startRunning(host);
										}catch(SocketException e) {
											server = null;
										}
									}
								} catch (IOException e) {
									// do nothing
								}
							}
						}
					).start();
						
				}
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}
	
	protected void closeSocket() {
		try {
			server.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
