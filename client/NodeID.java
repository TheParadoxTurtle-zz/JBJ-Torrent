import java.net.InetAddress;

//NodeID.java
//contains InetAddress and Port for given node

public class NodeID {
	public InetAddress ip;
	public int port;

	public NodeID(InetAddress ip, int port) {
		this.ip = ip;
		this.port = port;
	}
}