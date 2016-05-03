package client;

import java.io.*;
import java.net.*;
import java.util.*;

import lib.*;

//thread to handle connections to client 
public class ClientListeningThread implements Runnable {

	private ServerSocket welcomeSocket = null;
	private int port;
	private ClientNode node;

	public ClientListeningThread(ClientNode node, int port) {
		try {
			this.node = node;
			this.port = port;
			welcomeSocket = new ServerSocket(port);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//simple sequential server behavior
	public void run() {
		while(true) {
			try {
				Socket connSocket = welcomeSocket.accept();
				//handles peer to peer functions
				BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connSocket.getInputStream(), Constants.STR_ENCODING));
	            String line = inFromClient.readLine();
	            Debug.print(line);
		        while (!(line.equals(""))) {
	                StringTokenizer st = new StringTokenizer(line);
	                String command = st.nextToken();
	                String arg = st.nextToken();

			        	
		        	//actually handle commands
		        	//send bitmap
		        	if(command.equals("CONNECT")) {
		        		boolean[] bitmap = new boolean[5];
		        		//boolean[] bitmap = node.getBitmap();
		        		StringBuilder buf = new StringBuilder();
		        		for(int i = 0; i < bitmap.length; i++) {
		        			if(bitmap[i]) {
		        				buf.append("1");
		        			}
		        			else {
		        				buf.append("0");
		        			}
		        			buf.append("\r\n\r\n");
		        		}
		        	}	

			        line = inFromClient.readLine();
		        }
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}