package client;
import java.net.InetAddress;
import java.util.StringTokenizer;

//NodeID.java
//contains InetAddress and Port for given node

public class NodeID {
	public InetAddress ip;
	public int port;

	public NodeID(InetAddress ip, int port) {
		this.ip = ip;
		this.port = port;
	}
	
	public NodeID(String s) {
		StringTokenizer st = new StringTokenizer(s);
		try {
			ip = InetAddress.getByName(st.nextToken());
		}
		catch (Exception e) {
			
		}
		port = Integer.parseInt(st.nextToken());
	}

    public boolean equals(Object o) {
        NodeID temp = (NodeID) o;
        return temp.ip.equals(ip) && temp.port == port;
    }
    
    public String toString() {
    	return ip.toString().substring(1) + " " + port;
    }
}
