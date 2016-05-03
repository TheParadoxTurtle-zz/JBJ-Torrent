package server;
//details of Tracker Protocol

//Client GET request to server:
//info_hash: uniquely identifies the file
//peer_id: chosen by and uniquely identifies the client
//client IP and port
//numwant: how many peers to return
//stats: e.g. bytes uploaded, downloaded

//Tracker GET response to client:
//interval: how often to contact the tracker
//list of peers: containing peer id, IP and port
//stats

import java.util.*;

import client.NodeID;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.DataOutputStream;
import java.net.*;

public class TrackerServer {

	private static ServerSocket welcomeSocket;

	private static String enc = "US-ASCII";

	protected static int threadPoolSize = 5;
	protected static int serverPort = 6789;

	protected static HashMap<String,ArrayList<NodeID>> map = new HashMap<String,ArrayList<NodeID>>();

	public static void main(String[] args) throws Exception {
		try {
			serverPort = Integer.parseInt(args[0]);
		} catch(Exception e) {
			System.out.println("java TrackerServer port");
			e.printStackTrace();
		}

		//create listening socket on port
		welcomeSocket = new ServerSocket(serverPort);
		System.out.println("Running TrackerServer on port " + serverPort);
		StartSequentialServer();
	}	

    private static void StartSequentialServer() throws Exception {
        while(true) {
            // accept connection from connection queue
            Socket connSocket = welcomeSocket.accept();
            
           	BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connSocket.getInputStream(), enc));
            String line = inFromClient.readLine();
            System.out.println(line);
	        while (!(line.equals(""))) {
	        	//SEED request
                StringTokenizer st = new StringTokenizer(line);
                String command = st.nextToken();
                String arg = st.nextToken();
                int port = Integer.parseInt(st.nextToken());
	        	
	        	if(command.equals("SEED")) {
	        		handleSEED(connSocket, arg, port);
	        	}
                else if(command.equals("GET")) {
                    handleGetNeighbors(connSocket, arg, port);
                }
	        	
		        line = inFromClient.readLine();
	        }
        }
    }
    
    private static void handleSEED(Socket connSocket, String fileName, int port) {
        System.out.println("handleSEED: " + fileName);
    	//add node's InetAddress
		ArrayList<NodeID> list = map.get(fileName);
		NodeID id = new NodeID(connSocket.getInetAddress(), port); 
		if(list == null) {
			list = new ArrayList<NodeID>();
			list.add(id);
			map.put(fileName,list);
		}
		else {
            if(!list.contains(id))
			    list.add(id);
		}
    }

    private static void handleGetNeighbors(Socket connSocket, String fileName, int port) {
        System.out.println("handleGetNeighbors: " + fileName);
        ArrayList<NodeID> list = map.get(fileName);

		NodeID id = new NodeID(connSocket.getInetAddress(), port);
        String message = getNeighbors(list, id);

	    // The message to be sent
        try {
	        DataOutputStream outToClient = new DataOutputStream(connSocket.getOutputStream());
	        System.out.println(message);
	        outToClient.write(message.getBytes("US-ASCII"));
	        
	        connSocket.close();
        } catch(Exception e) {
            System.out.println("Error outputing to client");
            e.printStackTrace();
        }
    }

    // returns the message appropriate for the given list and id
    // this method returns all neighbors that exist that are not the user
    // If there are neighbors this method also adds the user to the 
    // list of neighbors

    public static String getNeighbors(ArrayList<NodeID> list, NodeID id) {
		if(list == null) {
            System.out.println("No neighbors");
            return "NO_NEIGHBORS\r\n\r\n";
		}
        StringBuffer buf = new StringBuffer();
        for(NodeID i : list) {
            if(i.equals(id))
                continue;
            buf.append(i.toString());
            buf.append("\r\n");
        }
        if(buf.length() == 0)
            return "NO_NEIGHBORS\r\n\r\n";
        else {
            buf.append("\r\n");
            return buf.toString();
            if(!list.contains(id))
		        list.add(id);
        }
    }
}








