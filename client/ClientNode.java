package client;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

import debug.Debug;

public class ClientNode implements Node {
	private InetAddress server_ip;
	private int server_port;
	private int client_port;
	private HashMap<String, ArrayList<NodeID>> neighbor_map;
	
	public ClientNode(InetAddress server_ip, int server_port, int client_port) {
		this.server_ip = server_ip;
		this.server_port = server_port;
		this.client_port = client_port;
		this.neighbor_map = new HashMap<String, ArrayList<NodeID>>();
	}
	
	public static void main(String args[]) throws Exception  {	
		InetAddress server_ip = InetAddress.getByName(args[0]);
		int server_port = Integer.parseInt(args[1]);
		int client_port = Integer.parseInt(args[2]);
		//String mode = args[3];
		
		ClientNode client = new ClientNode(server_ip, server_port, client_port);
		
		// Do stuff
		client.seed("dzc");

		client.getNeighbors("dzc");

		//start listening on specified port
		ClientListeningThread clt = new ClientListeningThread(client_port);
		Thread thread = new Thread(clt);
		thread.start();
	}
	
	//seeds to server
	public void seed(String fileName) {
		try {
			Socket connSocket = new Socket(server_ip, server_port);
			// The message to be sent
			DataOutputStream outToClient = new DataOutputStream(connSocket.getOutputStream());
			String message = createMessage("SEED", fileName, client_port);
			//String message = "SEED " + fileName + " " + client_port;
			Debug.print(message);
			outToClient.write(message.getBytes("US-ASCII"));
			
			connSocket.close();
		}
		catch (Exception e) {
			
		}
	}

	//gets neighbors from server
	public boolean getNeighbors(String fileName) {
		try {
			Socket connSocket = new Socket(server_ip, server_port);
			// The message to be sent
			DataOutputStream outToClient = new DataOutputStream(connSocket.getOutputStream());
			String message = createMessage("GET", fileName, client_port);
			Debug.print(message);
			outToClient.write(message.getBytes("US-ASCII"));
			
			
			InputStream is = connSocket.getInputStream();
			BufferedReader br = new BufferedReader (new InputStreamReader(is, "US-ASCII"));
			String line = br.readLine();

			ArrayList<NodeID> list = new ArrayList<NodeID>();
			neighbor_map.put(fileName, list);

	        while (!(line.equals(""))) {
	        	Debug.print(line);
	        	// Check if no neighbors
	        	if (line.equals("NO_NEIGHBORS")) {
	        		connSocket.close();
	        		return false;
	        	}
	        	
	        	// There are neighbors
                NodeID nid = new NodeID(line);
                list.add(nid);
	        	
		        line = br.readLine();
	        }
			
			connSocket.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		
		
		return false;
	}

	@Override
	public boolean[] connect(NodeID neighbor, String filename) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void have(NodeID neighbor, String fileName, int index) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void interested(NodeID neighbor, String fileName) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void unchoke(NodeID neighbor, String fileName) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void request(NodeID neighbor, String fileName, int index) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void send(NodeID neighbor, String fileName, int index) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void cancel(NodeID neighbor, String fileName, int index) {
		// TODO Auto-generated method stub
		
	}
	
	private String createMessage(String action, String fileName, int port) {
		return action + " " + fileName + " " + port + "\r\n\r\n";
	}
	
}
