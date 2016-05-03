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
	                
                StringTokenizer st = new StringTokenizer(line);
                String command = st.nextToken();
                String arg = st.nextToken();

		        String message = "";

	        	//actually handle commands
	        	//send bitmap
	        	if(command.equals("CONNECT")) {
	        		boolean[] bitmap = node.getBitmap(arg);
	        		StringBuilder buf = new StringBuilder();
	        		for(int i = 0; i < bitmap.length; i++) {
	        			if(bitmap[i]) {
	        				buf.append("1");
	        			}
	        			else {
	        				buf.append("0");
	        			}
	        			buf.append("\r\n\r\n");
	        			message = buf.toString();
	        		}
	        	}
	        	//peer is telling me he has another piece
	        	else if(command.equals("HAVE")) {
	        		//get port number
	        		int port = Integer.parseInt(st.nextToken());
	        		//get piece number
	        		int index = Integer.parseInt(st.nextToken());
	        		//node.updateBitmaps(arg, new NodeID(connSocket.getInetAddress(),port), index);
	        	}
	        	//peer is asking if they can request a piece
	        	else if(command.equals("INTERESTED")) {
	        		int port = Integer.parseInt(st.nextToken());
	        		//choose whether to unchoke or choke
	        		//message = "UNCHOKE";
	        		//message = "CHOKE"
	        		message += "\r\n";
	        	}
	        	DataOutputStream outToClient = new DataOutputStream(connSocket.getOutputStream());
		        System.out.println(message);
		        outToClient.write(message.getBytes("US-ASCII"));

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}