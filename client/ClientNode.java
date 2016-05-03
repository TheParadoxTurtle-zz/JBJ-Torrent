package client;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;

public class ClientNode implements Node {
	private InetAddress server_ip;
	private int server_port;
	
	public ClientNode(InetAddress server_ip, int server_port) {
		this.server_ip = server_ip;
		this.server_port = server_port;
	}
	
	public static void main(String args[]) throws Exception  {	
		InetAddress server_ip = InetAddress.getByName(args[1]);
		int server_port = Integer.parseInt(args[2]);
		String mode = args[3];
		
		ClientNode client = new ClientNode(server_ip, server_port);
		
		// Do stuff
		client.seed("dzc");
		
		
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

	@Override
	public boolean getNeighbors(String fileName) {
		// TODO Auto-generated method stub
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
