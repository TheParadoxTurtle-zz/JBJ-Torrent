package client;

public class Neighbor {
	public NodeID nodeid;
	public boolean bitmap[];
	
	public Neighbor(NodeID nodeid) {
		this.nodeid = nodeid;
		this.bitmap = null;
	}

	public boolean equals(Object o) {
		Neighbor temp = (Neighbor) o;
		return temp.nodeid.equals(this.nodeid);
	}
}
