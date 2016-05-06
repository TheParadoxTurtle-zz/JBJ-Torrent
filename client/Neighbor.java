package client;

public class Neighbor {
	public NodeID nodeid;
	public boolean bitmap[];
    // We are interested in them
	public boolean interested_in_them = false;
    // They are interested in us
	public boolean interested_in_us = false;
    // They can request pieces from us
	public boolean can_request_from_us = false;
    // We can request pieces from them
	public boolean can_send_to_us = false;
	
	
	public Neighbor(NodeID nodeid) {
		this.nodeid = nodeid;
		this.bitmap = null;
	}

	public boolean equals(Object o) {
		Neighbor temp = (Neighbor) o;
		return temp.nodeid.equals(this.nodeid);
	}
}
