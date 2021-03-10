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
	
	public GraphAmas(GraphEnvironment env) {
		super(env, Scheduling.DEFAULT);
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
	
	private void clearAllActivationHistory() {
		
		for (NodeAgent a : agentMap.values()) {
			a.clearActivationHistory();
		}
		
		/*
		ArrayList<Thread> threadList = new ArrayList<Thread>();
		
		for (NodeAgent a : agentMap.values()) {
			Thread t = new Thread(new Runnable() {
				public void run() {
					a.clearActivationHistory();
					a.disable();
				}
			});
			threadList.add(t);
		}
		
		for (Thread t : threadList) {
			try {
				t.join();
			} catch (final InterruptedException e) {
				e.printStackTrace();
			}
		}
		*/
		
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
	public ArrayList<ArrayList<Position>> searchAllPath(String start, String end) {
		startNode = start;
		endNode = end;
		NodeAgent s = agentMap.get(start);
		NodeAgent e = agentMap.get(end);
		
		ArrayList<ArrayList<Position>> resultPath = new ArrayList<ArrayList<Position>>();
		
		// Get all start positions of the start node
		ArrayList<Position> startPositions = new ArrayList<Position>();
		for (ArrayList<PosEdge> list : s.getEdgeTimetable().values()) {
			for (PosEdge edge : list)
				startPositions.add(edge.getFirstPos());
		}
		
		for (Position startPos : startPositions) {
			// System.out.println("START POSITION : " + startPos);

			clearAllActivationHistory();
			
			/*
			 * SYNCHRONISATION A FAIRE
			 */
			
			try {
				Thread.sleep(5);
			} catch (final InterruptedException ex) {
				ex.printStackTrace();
			}
			
			/*
			 * SYNCHRONISATION A FAIRE
			 */
			
			initOnePathSearch(startPos, end);
			int startCycleNumber = getEnvironment().getCycleNumber();
			while (getEnvironment().getCycleNumber() < startCycleNumber + getEnvironment().getGraph().nodes().count())
				getScheduler().step();
			
			ArrayList<ArrayList<Position>> pathList = e.getPathList();
			//System.out.println("\nAcceptable path from " + start + " to " + end + " : " + pathList);
			
			getAgentMap().get(end).setIsFinalNode(false);
			
			resultPath.addAll(pathList);
		}
	
		
		return resultPath;
	}
	
	public ArrayList<ArrayList<Position>> graphExplore() {
		ArrayList<ArrayList<Position>> resultPath = new ArrayList<ArrayList<Position>>();
		
		ArrayList<String> nodeIdList = new ArrayList<String>();
		for (Node n : getEnvironment().getGraph()) {
			nodeIdList.add(n.getId());
		}
		
		System.out.println("nodeIdList : " + nodeIdList);
		
		// list for test ...
		ArrayList<String> list = new ArrayList<String>();
		list.add("P"); list.add("Q"); list.add("R"); list.add("B"); 
		
		for (String start : nodeIdList) {
			NodeAgent s = agentMap.get(start);			
			for (String end : nodeIdList) {
				if (start.compareTo(end) != 0 && s.getEdgeTimetable() != null) {
					System.out.println("Start : " + start + " | End : " + end);
					
					ArrayList<ArrayList<Position>> pathSearch = searchAllPath(start, end);
					resultPath.addAll(pathSearch);
				}
			}
		}
			
		return resultPath;
	}
	
	public String printList(ArrayList<ArrayList<Position>> list) {
		String res = "";
		for (ArrayList<Position> l : list) {
			res += l + ",\n";
		}
		return res;
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
