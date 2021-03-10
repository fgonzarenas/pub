package SMA;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import fr.irit.smac.amak.Agent;
import generator.PosEdge;
import generator.Position;

public class NodeAgent extends Agent<GraphAmas, GraphEnvironment> {
	
	/*
	 * On each cycle, a NodeAgent can be activated or disabled. If the agent is disabled 
	 * it will do nothing. 
	 * A NodeAgent "b" can be activated by another NodeAgents "a" if the NodeAgent "a" discovered 
	 * that it could continue his path on node "b". A NodeAgent could be activated by several 
	 * NodeAgents on the same cycle. If the agent is activated, it will look for a next node where 
	 * it could extend the path and it will activate the associated NodeAgents
	 */
	
	// The node of the graph associated to this agent
	private String nodeId;
	// The keys are the adjacent nodes and the values are the position edges between this node and the adjacent node
	private Map<String, ArrayList<PosEdge>> edgeTimetable;
	// True if this agent correspond to the final node of the path search
	private boolean isFinalNode;
	// True if the agent is active on this cycle (he has to search a next node on the path)
	private boolean isActive;
	/* 
	 * Regroup the activation history on each cycle. One activation contains the path traveled, the current 
	 * node (here nodeId) and the neighboring nodes on which it is possible to continue this path
	 */ 
	private Map<Integer, ArrayList<Activation>> activationHistory;
	
	
	public NodeAgent(GraphAmas amas, String node) {
		super(amas);
		nodeId = node;
		edgeTimetable = new HashMap<String, ArrayList<PosEdge>>();
		isActive = false;
		activationHistory = new HashMap<Integer, ArrayList<Activation>>();
	}
	
	// Checks if the node does not appear more times in the path than the maximum number of times it can be crossed
	private boolean multipleCrossedNode(ArrayList<Position> path, String node) {
		int occurence = Collections.frequency(path, node);
		if (occurence > getEnvironment().getMaxCrossedNode())
			return false;
		else
			return true;
	}
	
	// USED IN LOG
	private void printPrecedentPath(Activation a) {
		//System.out.print("   [" + nodeId + "] precedent path : ");
		for (Position pos : a.getPrecedentPositions()) {
			System.out.print(pos.getNode() + " ");
		}
		System.out.println(a.getMyPosition().getNode());
	}
	
	@Override
	protected void onPerceive() {
		super.onPerceive();
		
		if (isActive && !isFinalNode ) {
			
			/*
			 *  LOG
			 */
			if (getEnvironment().hasLog()) {
				if (activationHistory.containsKey(getEnvironment().getCycleNumber()-1)) {
					int cycleNumberPrec = getEnvironment().getCycleNumber()-1;
					System.out.print("[" + nodeId + "] activate | cycleNumberPrec : " + cycleNumberPrec + " | precedentPosition : ");
					for (Activation a : activationHistory.get(getEnvironment().getCycleNumber()-1)) {
						if (getEnvironment().getCycleNumber() != 0)
							System.out.print(a.getPrecedentPositions().get(a.getPrecedentPositions().size()-1).getNode() + " " 
									+ a.getPrecedentPositions().get(a.getPrecedentPositions().size()-1).getTimestamp() + " ");
					}
					System.out.println();
				}
				else
					System.out.println("[" + nodeId + "] activate");
			}
			
			
			// Search if there is candidates to next nodes for each activation
			if (activationHistory.containsKey(getEnvironment().getCycleNumber()-1)) {
				for (Activation a : activationHistory.get(getEnvironment().getCycleNumber()-1)) {
					Timestamp activeTimestamp = a.getMyPosition().getTimestamp();
					
					if (getEnvironment().hasLog())
						printPrecedentPath(a);
					
					// Explore all neighboring NodeAgents
					if (edgeTimetable != null && edgeTimetable.keySet().size() != 0) {
						for (String neighbor : edgeTimetable.keySet()) {
							if (multipleCrossedNode(a.getPrecedentPositions(), neighbor)) {
								// list of edges from this node agent to neighbor
								ArrayList<PosEdge> edgeList = edgeTimetable.get(neighbor);
								int i = 0;
								boolean found = false;
								// Explore the timetable of the NodeAgent
								while (!found && i<edgeList.size()) {
									Timestamp ts = edgeList.get(i).getNextPos().getTimestamp();
									// If a timestamp greater than the active timestamp of this agent is found and with a waiting 
									// period less than the waiting maximum, the following position is validated
									if (ts.compareTo(activeTimestamp) > 0 && (ts.getTime() - activeTimestamp.getTime()) <= getAmas().getMaxWaitingTime()) {
										found = true;
										a.setNextPosition(new Position(neighbor, ts));
										
										if (getEnvironment().hasLog())
											System.out.println("   [" + nodeId + "] setNextPosition : " + neighbor + " | timestamp : " + ts);
									}
									else {
										i++;
									}
								}
							}
						}
					}
				}
			}
		}
		// final node of the path research
		else if (isActive && isFinalNode ) {
			if (getEnvironment().hasLog()) {
				System.out.println("[" + nodeId + "] PATH FOUND !");

				for (Activation a : activationHistory.get(getEnvironment().getCycleNumber()-1)) {
					System.out.print("   PATH : ");
					for (Position p : a.getPrecedentPositions()) {
						System.out.print(p.getNode() + " ");
					}
					System.out.println(getNodeId());				
				}
			}
			
		}
		else {
			if (getEnvironment().hasLog())
				System.out.println("[" + nodeId + "] disable");
		}
	}
	
	@Override
	protected void onDecide() {
		super.onDecide();
	}
	
	@Override
	protected void onAct() {
		super.onAct();
		/*
		 * Activation des noeuds suivants
		 */
		if (isActive && activationHistory.containsKey(getEnvironment().getCycleNumber()-1)) {
			if (getEnvironment().hasLog())
				System.out.println("   [" + nodeId + "] act | taille activationHistory : " 
						+ activationHistory.get(getEnvironment().getCycleNumber()-1).size());
			
			for (Activation a : activationHistory.get(getEnvironment().getCycleNumber()-1)) {
				// Si un noeud suivant a été trouvé
				if (a.hasNextPosition()) {
					if (getEnvironment().hasLog())
						System.out.println("      [" + nodeId + "] act | taille nextPosition : " + a.getNextPositions().size());
					
					for (Position nextPos : a.getNextPositions()) {
						// Recherche de l'agent suivant
						NodeAgent nextAgent = getAmas().getAgentMap().get(nextPos.getNode());
						// Ajout de ce noeud au chemin
						ArrayList<Position> path = a.getPrecedentPositions();
						path.add(a.getMyPosition());
						// Activation de l'agent suivant pour le prochain cycle
						nextAgent.activate(path, nextPos);
					}
				}
			}
		}
		// On desactive l'agent s'il n'a pas ete active sur ce cycle
		if (!activationHistory.containsKey(getEnvironment().getCycleNumber())) {
			disable();
		}
	}
	
	
	// Method to activate the agent for the next cycle, he will search a next position for the path
	public void activate(ArrayList<Position> precedentPos, Position myPosition) {
		isActive = true;
		
		/*
		 *  LOG
		 */
		if (getEnvironment().hasLog()) {
			if (getEnvironment().getCycleNumber() == -1)
				System.out.println("   ACTIVATE [" + nodeId + "] on cycle : " + getEnvironment().getCycleNumber());
			else 
				System.out.println("   ACTIVATE [" + nodeId + "] on cycle : " + getEnvironment().getCycleNumber() 
						+ " | by : " + precedentPos.get(precedentPos.size()-1).getNode());
		}
		
		
		Activation a = new Activation(precedentPos, myPosition);
		
		if (activationHistory.containsKey(getEnvironment().getCycleNumber())) {
			activationHistory.get(getEnvironment().getCycleNumber()).add(a);
		}
		else {
			ArrayList<Activation> list = new ArrayList<Activation>();
			list.add(a);
			activationHistory.put(getEnvironment().getCycleNumber(), list);
		}
	}
	
	// Return the list of path found by the research
	public ArrayList<ArrayList<Position>> getPathList() {
		ArrayList<ArrayList<Position>> pathList = new ArrayList<ArrayList<Position>>();
		if (isFinalNode) {
			Map<Integer, ArrayList<Activation>> hist = new HashMap<Integer, ArrayList<Activation>>(activationHistory);
			for (int i : hist.keySet()) {
				for (Activation a : hist.get(i)) {
					ArrayList<Position> path = new ArrayList<Position>();
					for (Position p : a.getPrecedentPositions())
						path.add(p);
					path.add(a.getMyPosition());
					pathList.add(path);
				}
			}
		}
		
		return pathList;
	}
	
	// method to disable the agent for the next cycle
	public void disable() {
		isActive = false;
	}
	
	public void setEdgeTimetable(Map<String, ArrayList<PosEdge>> edgeMap) {
		edgeTimetable = edgeMap;
	}
	
	public void setIsFinalNode(boolean b) {
		isFinalNode = b;
	}
	
	public void clearActivationHistory() {
		activationHistory.clear();
	}
	
	
	public String getNodeId() {
		return nodeId;
	}
	
	public Map<String, ArrayList<PosEdge>> getEdgeTimetable() {
		return edgeTimetable;
	}
	
	public Map<Integer, ArrayList<Activation>> getActivationHistory() {
		return activationHistory;
	}
}
