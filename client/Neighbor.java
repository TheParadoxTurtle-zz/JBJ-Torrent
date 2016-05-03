package client;

public class Neighbor {
	NodeID nodeid;
	boolean bitmap[];
	
	public Neighbor(NodeID nodeid) {
		this.nodeid = nodeid;
		this.bitmap = null;
	}
}
