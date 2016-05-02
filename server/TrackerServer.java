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
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.*;

public class TrackerServer {

	private static ServerSocket welcomeSocket;

	private static String enc = "US-ASCII";

	protected static int threadPoolSize = 5;
	protected static int serverPort = 6789;

	protected static HashMap<String,ArrayList<NodeID>> map;

	public static void main(String[] args) throws Exception {
		serverPort = Integer.parseInt(args[1]);
		StartSequentialServer();
	}	

    private static void StartSequentialServer() throws Exception {
        while(true) {
            // accept connection from connection queue
            Socket connSocket = welcomeSocket.accept();
            
           	BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connSocket.getInputStream(), enc));
            String line = inFromClient.readLine();

	        while (!(line == "")) {
	        	//SEED request
	        	String[] pair = line.split(" ");
	        	if(pair[0].equals("SEED")) {
	        		//add node's InetAddress
	        		ArrayList<NodeID> list = map.get(pair[1]);
	        		NodeID id = new NodeID(connSocket.getInetAddress(), connSocket.getPort());
	        		if(list == null) {
	        			list = new ArrayList<NodeID>();
	        			list.add(id);
						map.put(pair[1],list);
	        		}
	        		else {
	        			list.add(id);
	        		}
	        	}
		        line = inFromClient.readLine();
	        }
        }
    }
}








