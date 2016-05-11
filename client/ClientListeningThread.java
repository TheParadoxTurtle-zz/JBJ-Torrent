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
			this.welcomeSocket = new ServerSocket(port);
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
				//BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connSocket.getInputStream(), Constants.STR_ENCODING));
				DataInputStream inFromClient = new DataInputStream(connSocket.getInputStream());
	            String line = inFromClient.readLine();
	            Debug.print("Receiving...");
	            Debug.print(line);
	                
                StringTokenizer st = new StringTokenizer(line);
                String command = st.nextToken();
                String arg = st.nextToken();

				int port = Integer.parseInt(st.nextToken());

				NodeID nid = new NodeID(connSocket.getInetAddress(), port);
				Neighbor neighbor = node.findNeighbor(nid,arg);

		        String message = "";

	        	//actually handle commands
	        	//send bitmap
	        	if(command.equals("CONNECT")) {
	        		//get neighbor bitmap
	        		line = inFromClient.readLine();

	        		//update neighbor bitmaps
	        		if(neighbor == null) {
	        			neighbor = node.addNeighbor(nid,arg);
	        		}
	        		neighbor.bitmap = BitMapContainer.bitmapFromString(line);

	        		//send own bitmap
	        		message = "CONNECTED " + arg + " " + node.client_port + "\r\n";
	        		message += BitMapContainer.stringFromBitmap(node.getBitMap(arg));
	        		message += "\r\n\r\n";
	        	}
	        	//neighbor 
	        	else if(command.equals("CONNECTED")) {
	        		//get neighbor bitmap
	        		line = inFromClient.readLine();
	        		Debug.print(line);
	        		//update neighbor bitmaps
	        		if(neighbor == null) {
	        			neighbor = node.addNeighbor(nid,arg);
	        		}
	        		neighbor.bitmap = BitMapContainer.bitmapFromString(line);
	        	}
	        	//peer is telling me he has another piece
	        	else if(command.equals("HAVE")) {
	        		//get piece number
	        		int index = Integer.parseInt(st.nextToken());
	        		neighbor.bitmap[index] = true;
	        	}
	        	//peer is asking if they can request a piece
	        	else if(command.equals("INTERESTED")) {
	        		//set interested variable
	        		neighbor.interested_in_us = true;
	        		//choose whether to unchoke or choke
	        		//for now, just always unchoke
	        		if(node.shouldUnchoke(neighbor,arg)) {
	        			node.unchoke(neighbor,arg);
	        		}
	        		else {
	        			node.choke(neighbor,arg);
	        		}
	        	}
	        	//peer unchoked me
	        	else if(command.equals("UNCHOKE")) {
	        		neighbor.can_send_to_us = true;
	        	}
	        	//peer choked me
	        	else if(command.equals("CHOKE")) {
	        		neighbor.can_send_to_us = false;
	        	}
	        	//peer is requesting specific piece
	        	else if(command.equals("REQUEST")) {
                    if(neighbor.can_request_from_us) {
	        		    //get piece number
	        		    int index = Integer.parseInt(st.nextToken());
	        		    //create separate thread for piece communication?
	        		    if (index >= 0 && index < node.getBitMap(arg).length) {
	        		    	if(node.type == ClientNode.Type.SEEDER) {
	        		    		Thread.sleep(20);
	        		    	}
	        		    	node.send(neighbor,arg,index);
	        		    }
                    }
	        	}
	        	else if(command.equals("SEND")) {
	        		//get piece number
	        		int index = Integer.parseInt(st.nextToken());
	        		//DataInputStream dis = new DataInputStream(connSocket.getInputStream());
	        		//block waiting for piece
	        		//we will have our own thread?
	        		BitMapContainer bmc = node.getBitMapContainer(arg);
	        		int pl = bmc.getPieceLength(index);
	        		byte[] piece = new byte[pl];
	        		inFromClient.readFully(piece);
	        		if(bmc.addPiece(piece,index)) {
	        			//if piece added, send have to all neighbors
	        			node.sendHaveToAll(arg,index);
	        		}
	        	}

	        	if(!message.equals("")) {
		        	Socket sendSocket = new Socket(connSocket.getInetAddress(),port);
		        	DataOutputStream outToClient = new DataOutputStream(sendSocket.getOutputStream());
		        	Debug.print("Sending...");
			        Debug.print(message);
			        outToClient.write(message.getBytes("US-ASCII"));
			        sendSocket.close();
	        	}

	        	connSocket.close();

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
