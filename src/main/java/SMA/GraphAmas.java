package SMA;

import fr.irit.smac.amak.Amas;
import fr.irit.smac.amak.Configuration;
import fr.irit.smac.amak.Scheduling;
import generator.Position;

import java.util.HashMap;
import java.util.Map;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;


public class GraphAmas extends Amas<GraphEnvironment> {
	
	private Map<String, NodeAgent> agentMap;
	
	private String startNode;
	private String endNode;
	
	public GraphAmas(GraphEnvironment env) {
		super(env, Scheduling.DEFAULT);
	}
	
	
	/*
	 * Modification of the configuration to set the ExecutionPolicy to two phases to synchronize
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
			a.setTimetable(getEnvironment().getTimestampMap().get(n.getId()));
		}	
	}
	
	// Initialize the first NodeAgent of the search
	public void initPathSearch(String start, String end) {
		startNode = start;
		endNode = end;
		
		NodeAgent s = agentMap.get(start);
		s.activate(new Position(null, null), s.getTimetable().get(0));
		agentMap.get(end).setFinalNode();
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
	
}
