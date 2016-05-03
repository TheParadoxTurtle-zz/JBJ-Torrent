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

public class ClientNode implements Node {
	private InetAddress server_ip;
	private int server_port;
	private HashMap<String, ArrayList<NodeID>> neighbor_map;
	
	public ClientNode(InetAddress server_ip, int server_port) {
		this.server_ip = server_ip;
		this.server_port = server_port;
		this.neighbor_map = new HashMap<String, ArrayList<NodeID>>();
	}
	
	public static void main(String args[]) throws Exception  {	
		InetAddress server_ip = InetAddress.getByName(args[0]);
		int server_port = Integer.parseInt(args[1]);
		String mode = args[2];
		
		ClientNode client = new ClientNode(server_ip, server_port);
		
		// Do stuff
		client.seed("dzc");

		client.getNeighbors("dzc");
	}
	
	public void seed(String fileName) {
		try {
			Socket connSocket = new Socket(server_ip, server_port);
			// The message to be sent
			DataOutputStream outToClient = new DataOutputStream(connSocket.getOutputStream());
			String message = createMessage("SEED", fileName);
			System.out.println(message);
			outToClient.write(message.getBytes("US-ASCII"));
			
			connSocket.close();
		}
		catch (Exception e) {
			
		}
	}

	public boolean getNeighbors(String fileName) {
		try {
			Socket connSocket = new Socket(server_ip, server_port);
			// The message to be sent
			DataOutputStream outToClient = new DataOutputStream(connSocket.getOutputStream());
			String message = createMessage("GET", fileName);
			System.out.println(message);
			outToClient.write(message.getBytes("US-ASCII"));
			
			
			InputStream is = connSocket.getInputStream();
			BufferedReader br = new BufferedReader (new InputStreamReader(is, "US-ASCII"));
			String line = br.readLine();

			ArrayList<NodeID> list = new ArrayList<NodeID>();
			neighbor_map.put(fileName, list);

	        while (!(line.equals(""))) {
	        	System.out.println(line);
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
	public boolean[] connect(Node neighbor, String filename) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void have(Node neighbor, String fileName, int index) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void interested(Node neighbor, String fileName) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void unchoke(Node neighbor, String fileName) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void request(Node neighbor, String fileName, int index) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void send(Node neighbor, String fileName, int index) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void cancel(Node neighbor, String fileName, int index) {
		// TODO Auto-generated method stub
		
	}
	
	private String createMessage(String action, String fileName) {
		return action + " " + fileName + "\r\n\r\n";
	}
	
}
