package theading;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class PortThread2 extends Thread {


	private String host;
	private int port;
	
	public PortThread2(String h, int p) {
		
		this.host = h;
		this.port = p;
		
	}
	
	
	public void run() {
		
		try {
			Socket socket = new Socket(host, port);
			System.out.println("Port "+ port +" is open!!!!!!!!!!!!!");
			socket.close();
		} catch (Exception e) {

//			System.out.println("Port "+ port +" is closed.");
			
		}
		
	}

}
