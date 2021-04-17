package generator;

import java.sql.Timestamp;
import java.util.Calendar;

public class Position {
	private String node;
	private Timestamp timestamp;
	
	public Position(String n, Timestamp  t) {
		timestamp = t;
		node = n;
	}
	
	public Position(String n, Calendar t) {
		this(n, new Timestamp(t.getTimeInMillis()));
	}
	
	public String getNode() {
		return node;
	}
	
	public Timestamp getTimestamp() {
		return timestamp;
	}
	
	public String toString() {
		return "(" + node + "," + timestamp + ")";
	}
}
