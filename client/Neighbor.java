package client;

public class Neighbor {
	public NodeID nodeid;
	public boolean bitmap[];
	public boolean interested_in_them = false;
	public boolean interested_in_us = false;
	public boolean unchoked_to_them = false;
	public boolean unchoked_to_us = false;
	
	
	public Neighbor(NodeID nodeid) {
		this.nodeid = nodeid;
		this.bitmap = null;
	}

	public boolean equals(Object o) {
		Neighbor temp = (Neighbor) o;
		return temp.nodeid.equals(this.nodeid);
	}
}
