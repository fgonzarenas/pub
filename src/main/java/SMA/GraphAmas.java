package SMA;

import fr.irit.smac.amak.Amas;
import fr.irit.smac.amak.Configuration;
import fr.irit.smac.amak.Scheduling;
import generator.PosEdge;
import generator.Position;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;


public class GraphAmas extends Amas<GraphEnvironment> {
	
	private Map<String, NodeAgent> agentMap;
	
	private String startNode;
	private String endNode;
	private long maxWaitingTime;
	
	private ArrayList<ArrayList<Position>> pathList;
	
	public GraphAmas(GraphEnvironment env) {
		super(env, Scheduling.DEFAULT);
		pathList = new ArrayList<ArrayList<Position>>();
	}
	
	
	/*
	 * Modification of the AMAS configuration to set the ExecutionPolicy to two phases to synchronize
	 * agents one time after Perception phase and in a second time after Decide and Act phase
	 */
	@Override
	protected void onInitialConfiguration() {
		super.onInitialConfiguration();
		Configuration.executionPolicy = ExecutionPolicy.TWO_PHASES;
	}
	
	@Override
	protected void onInitialAgentsCreation() {
		Graph graph = getEnvironment().getGraph();
		agentMap = new HashMap<String, NodeAgent>();
		
		// Creation of agents
		for (Node n : graph) {
			NodeAgent a = new NodeAgent(this, n.getId());
			agentMap.put(n.getId(), a);
		}
		
		// Creation of neighborhood for all agents
		for (Node n : graph) {
			NodeAgent a = agentMap.get(n.getId());
			Node[] neighborNodes = (Node[]) n.neighborNodes().toArray(size -> new Node[size]);
			for (Node neighborNode : neighborNodes) {
				a.addNeighbor(agentMap.get(neighborNode.getId()));
			}
			
			// Adding the timetable to the agent
			a.setEdgeTimetable(getEnvironment().getEdgeMap().get(n.getId()));
		}	
	}
	
	
	public void setMaxWaitingTime(long maximumWaitingTime) {
		maxWaitingTime = maximumWaitingTime;
	}
	
	
	// Initialize the first and last NodeAgent of the research
	public void initOnePathSearch(Position startPosition, String end) {
		startNode = startPosition.getNode();
		endNode = end;
		
		NodeAgent s = agentMap.get(startNode);
		s.activate(new ArrayList<Position>(), startPosition);
		agentMap.get(endNode).setIsFinalNode(true);
	}
	
	// Finds all the paths between two nodes for all the starting positions of the starting node
	public void searchAllPath(String start, String end) {
		startNode = start;
		endNode = end;
		NodeAgent s = agentMap.get(start);
		NodeAgent e = agentMap.get(end);
				
		// Get all start positions of the start node
		ArrayList<Position> startPositions = new ArrayList<Position>();
		for (ArrayList<PosEdge> list : s.getEdgeTimetable().values()) {
			for (PosEdge edge : list)
				startPositions.add(edge.getFirstPos());
		}
		
		for (Position startPos : startPositions) {
			NodeAgent sa = agentMap.get(startNode);
			sa.activate(new ArrayList<Position>(), startPos);
			agentMap.get(endNode).setIsFinalNode(true);
			
			// we carry out a cycle number equal to the number of nodes because a path will never be longer than the number of nodes
			int startCycleNumber = getEnvironment().getCycleNumber();
			while (getEnvironment().getCycleNumber() < startCycleNumber + getEnvironment().getGraph().nodes().count()+1)
				getScheduler().step();
			
			for (NodeAgent a : agentMap.values()) {
				a.clearActivationHistory();
				a.disable();
			}
		}
	}
	
	// search all paths between all possible combinations of nodes 
	public ArrayList<ArrayList<Position>> graphExplore() {		
		ArrayList<String> nodeIdList = new ArrayList<String>();
		for (Node n : getEnvironment().getGraph()) {
			nodeIdList.add(n.getId());
		}
		for (String start : nodeIdList) {
			NodeAgent s = agentMap.get(start);			
			for (String end : nodeIdList) {
				if (start.compareTo(end) != 0 && s.getEdgeTimetable() != null) {
					System.out.println("Start : " + start + " | End : " + end);
					searchAllPath(start, end);
					agentMap.get(end).setIsFinalNode(false);
				}
			}
		}
			
		return pathList;
	}
	
	public String printList(ArrayList<ArrayList<Position>> list) {
		String res = "";
		for (ArrayList<Position> l : list) {
			res += l + ",\n";
		}
		return res;
	}
	
	public void addPath(ArrayList<Position> path) {
		pathList.add(path);
	}
	
	public ArrayList<ArrayList<Position>> getPathList() {
		return pathList;
	}

	
	public Map<String, NodeAgent> getAgentMap() {
		return agentMap;
	}
	
	public String getStartNode() {
		return startNode;
	}
	
	public String getEndNode() {
		return endNode;
	}
	public long getMaxWaitingTime() {
		return maxWaitingTime;
	}
	
}
