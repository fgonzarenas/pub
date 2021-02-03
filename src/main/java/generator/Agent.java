package generator;

import java.util.ArrayList;

public class Agent {
	private Integer agentId;
	private String description;
	private ArrayList<GenPath> pathList;
	
	public Agent(Integer id) {
		agentId = id;
		pathList = new ArrayList<GenPath>();
	}
	
	public Integer getId() {
		return agentId;
	}
	
	public String getDescription() {
		return description;
	}
	
	public ArrayList<GenPath> getPath() {
		return pathList;
	}
	
	public void addPath(GenPath p) {
		pathList.add(p);
	}
	
}
