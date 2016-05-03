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
	        		int port = Integer.parseInt(st.nextToken());

	        		//get neighbor bitmap
	        		line = inFromClient.readLine();

	        		//update neighbor bitmaps
	        		NodeID nid = new NodeID(connSocket.getInetAddress(),port);
	        		Neighbor neighbor = node.getNeighbor(nid);
	        		neighbor.bitmap = BitMapContainer.bitmapFromString(line);

	        		//send own bitmap
	        		message = BitMapContainer.stringFromBitmap(node.getBitMap(arg));
	        		message += "\r\n\r\n";
	        	}
	        	//peer is telling me he has another piece
	        	else if(command.equals("HAVE")) {
	        		//get port number
	        		int port = Integer.parseInt(st.nextToken());
	        		NodeID nid = new NodeID(connSocket.getInetAddress(),port);
	        		Neighbor neighbor = node.getNeighbor(nid);
	        		//get piece number
	        		int index = Integer.parseInt(st.nextToken());
	        		neighbor.bitmap[index] = true;
	        	}
	        	//peer is asking if they can request a piece
	        	else if(command.equals("INTERESTED")) {
	        		//choose whether to unchoke or choke
	        		//message = "UNCHOKE";
	        		//message = "CHOKE"
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