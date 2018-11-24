import java.io.*;
import java.net.*;
import java.util.*;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public final class network {
	private String localIP = null;
	private int localPort;
	private LinkedList<ObjectOutputStream> outStreamList = new LinkedList<ObjectOutputStream>();
	private LinkedList<Object[]> serverList = new LinkedList<Object[]>();
	
	
	public network(int localPort) {
		this.localPort = localPort;
		try {
			localIP = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			new network(localPort);
			return;
		}
	}
	
	
	
	
}
