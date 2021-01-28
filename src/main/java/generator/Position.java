package generator;

import java.sql.Timestamp;

public class Position {
	private String node;
	private Timestamp timestamp;
	
	public Position(String n, Timestamp  t) {
		timestamp = t;
		node = n;
	}
	
	public String getNode() {
		return node;
	}
	
	public Timestamp getTimestamp() {
		return timestamp;
	}
}
