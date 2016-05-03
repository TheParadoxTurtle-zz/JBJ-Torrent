package client;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.Timer;

import lib.Debug;

public class ClientNode implements Node {
	private InetAddress server_ip;
	private int server_port;
	private int client_port;
	private HashMap<String, ArrayList<Neighbor>> neighbor_maps;
	private HashMap<String, BitMapContainer> torrents;
	
	private Timer timer;
	
	public ClientNode(InetAddress server_ip, int server_port, int client_port) {
		this.server_ip = server_ip;
		this.server_port = server_port;
		this.client_port = client_port;
		this.torrents = new HashMap<String, BitMapContainer>();
		this.neighbor_maps = new HashMap<String, ArrayList<Neighbor>>();
		this.timer = new Timer();
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
		ClientListeningThread clt = new ClientListeningThread(client, client_port);
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
			
			// First line is length of bitmap
        	if (line.equals("NO_NEIGHBORS")) {
        		connSocket.close();
        		return false;
        	}
			int bitmap_length = Integer.parseInt(line);
			BitMapContainer bmc = new BitMapContainer(bitmap_length);
			torrents.put(fileName, bmc);
			line = br.readLine();
			
			ArrayList<Neighbor> list = new ArrayList<Neighbor>();
			neighbor_maps.put(fileName, list);

	        while (!(line.equals(""))) {
	        	Debug.print(line);
	        	// Check if no neighbors
	        	
	        	// There are neighbors
                NodeID nid = new NodeID(line);
                Neighbor neighbor = new Neighbor(nid);
                list.add(neighbor);
	        	
		        line = br.readLine();
	        }
			
			connSocket.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return false;
	}

	public void connect(Neighbor neighbor, String fileName) {
		try {
            if(neighbor.bitmap != null)
                return;

			Socket connSocket = new Socket(server_ip, server_port);
			// The message to be sent
			DataOutputStream outToClient = new DataOutputStream(connSocket.getOutputStream());
			String message = createMessageWithBitMap("CONNECT", fileName, client_port, torrents.get(fileName).bitmap);
			Debug.print(message);
			outToClient.write(message.getBytes("US-ASCII"));
			
			// Timeout Event
	        iCallback callback = new ConnectCallback(connSocket, this,
                                                 neighbor, fileName);
	        
	        TimeOutEvent event = new TimeOutEvent(callback);
	        timer.schedule(event, 5000);
			
			InputStream is = connSocket.getInputStream();
			BufferedReader br = new BufferedReader (new InputStreamReader(is, "US-ASCII"));
			String line = br.readLine();
            callback.stop();

			
			// Process the return from them
        	neighbor.bitmap = BitMapContainer.bitmapFromString(line);
        	
        	if (connSocket.isClosed()) {
        		return;
        	}
	        line = br.readLine();
			
			connSocket.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return;
	}

	public void have(Neighbor neighbor, String fileName, int index) {
		try {
			Socket connSocket = new Socket(server_ip, server_port);
			// The message to be sent
			DataOutputStream outToClient = new DataOutputStream(connSocket.getOutputStream());
			String message = createMessage("HAVE", fileName, client_port, index);
			Debug.print(message);
			outToClient.write(message.getBytes("US-ASCII"));

			connSocket.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void interested(Neighbor neighbor, String fileName) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void unchoke(Neighbor neighbor, String fileName) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void request(Neighbor neighbor, String fileName, int index) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void send(Neighbor neighbor, String fileName, int index) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void cancel(Neighbor neighbor, String fileName, int index) {
		// TODO Auto-generated method stub
		
	}
	
	public static void execute(ClientNode client) {
		while (true) {
			Scanner scanner = new Scanner(System.in);
			String line = scanner.nextLine();
			StringTokenizer st = new StringTokenizer(line);
			String command = st.nextToken();
			
			if (!st.hasMoreTokens()) {
				System.out.println("Invalid command");
				continue;
			}
			
			if (command.equals("seed")) {
				client.seed(st.nextToken());
			}
			else if (command.equals("getNeighbors")) {
				client.getNeighbors(st.nextToken());
			}
			else if (command.equals("connect")) { // connect address port fileName
				try {
					InetAddress address = InetAddress.getByName(st.nextToken());
					int port = Integer.parseInt(st.nextToken());
					NodeID nodeid = new NodeID(address, port);
					String fileName = st.nextToken();
					Neighbor neighbor = client.findNeighbor(nodeid, fileName);
					if (neighbor == null) {
						System.out.println("Neighbor invalid");
						continue;
					}
					
					client.connect(neighbor, fileName);
				}
				catch (Exception e) {
					System.out.println("Invalid command");
				}
			}
			else if (command.equals("have")) { // have address port filename index
				try {
					InetAddress address = InetAddress.getByName(st.nextToken());
					int port = Integer.parseInt(st.nextToken());
					NodeID nodeid = new NodeID(address, port);
					String fileName = st.nextToken();
					int index = Integer.parseInt(st.nextToken());
					Neighbor neighbor = client.findNeighbor(nodeid, fileName);
					if (neighbor == null) {
						System.out.println("Neighbor invalid");
						continue;
					}
					
					client.have(neighbor, fileName, index);
				}
				catch (Exception e) {
					System.out.println("Invalid command");
				}
			}
			else {
				System.out.println("Invalid command");
			}
		}
	}

	public Neighbor findNeighbor(NodeID nodeid, String fileName) {
		ArrayList<Neighbor> neighbors = neighbor_maps.get(fileName);
		for (Neighbor neighbor : neighbors) {
			if (neighbor.nodeid.equals(nodeid)) {
				return neighbor;
			}
		}
		return null;
	}
	
	private String createMessage(String action, String fileName, int port, int index) {
		return action + " " + fileName + " " + port + index + "\r\n\r\n";
	}
	
	private String createMessage(String action, String fileName, int port) {
		return action + " " + fileName + " " + port + "\r\n\r\n";
	}
	
	private String createMessage(String action, String fileName) {
		return action + " " + fileName + "\r\n\r\n";
	}
	
	private String createMessageWithBitMap(String action, String fileName, int port, boolean[] bitmap) {
		return action + " " + fileName + " " + port + "\r\n" + BitMapContainer.stringFromBitmap(bitmap) + "\r\n\r\n";
	}
	
	public boolean[] getBitMap(String fileName) {
		return torrents.get(fileName).bitmap;
	}
}




