package generator;

import java.util.ArrayList;

public class Agent {
	private Integer agentId;
	private String description;
	private ArrayList<Position> path;
	
	public Agent(Integer id) {
		agentId = id;
		path = new ArrayList<Position>();
	}
	
	public Integer getId() {
		return agentId;
	}
	
	public String getDescription() {
		return description;
	}
	
	public ArrayList<Position> getPath() {
		return path;
	}
	
	public void addPosition(Position p) {
		path.add(p);
	}
	
}
