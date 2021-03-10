package SMA;

import java.util.ArrayList;
import java.util.Map;

import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.MultiGraph;

import fr.irit.smac.amak.Environment;
import fr.irit.smac.amak.Scheduling;
import generator.Parser;
import generator.PosEdge;
import generator.Position;


public class GraphEnvironment extends Environment {
	
	private Graph graph;
	private String graphFilename;
	private String agentMoveFilename;
	private Map<String, Map<String, ArrayList<PosEdge>>> edgeMap;
	
	// attribute use to count the number of cycle
	private int cycleNumber;
	
	// the maximum number of times that we can pass on a node in a path
	private int maxCrossedNode;
	
	// true if we want to display log
	private boolean log;
	
	
	public GraphEnvironment(String dgsFileName, String moveFilename, boolean log, int maximumCrossedNode) {
		super(Scheduling.DEFAULT);
		graphFilename = "road-networks/" + dgsFileName;
		graph = new MultiGraph(graphFilename);
		agentMoveFilename = moveFilename;
		maxCrossedNode = maximumCrossedNode;
		cycleNumber = -1;
		
		initGraph();
		
		this.log = log;
	}
	
	private void initGraph() {
		try {
			System.out.println("filename : " + graphFilename);
			graph.read(graphFilename);
		} 
		catch(Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		Parser p = new Parser(agentMoveFilename);
		p.parse();
		edgeMap = p.translate();
	}
	
	
	@Override
	public void onInitialization() {
		
	}
	
	@Override
	public void onCycle() {
		super.onCycle();
		cycleNumber++;
		//System.out.println("---------------------------------");
		if (cycleNumber % 1000 == 0)
			System.out.println("   Cycle : " + cycleNumber);
	}
	
	
	public int getCycleNumber() {
		return cycleNumber;
	}
	
	public Graph getGraph() {
		return graph;
	}
	
	public Map<String, Map<String, ArrayList<PosEdge>>> getEdgeMap() {
		return edgeMap;
	}
	
	public int getMaxCrossedNode() {
		return maxCrossedNode;
	}
	
	public boolean hasLog() {
		return log;
	}
	
	
	
	public static void main(String[] args) {
		boolean log = false;
		int maxRepetitionOfNodeInPath = 2;
		GraphEnvironment env = new GraphEnvironment("GraphTest.dgs", "test_serial.json", log, maxRepetitionOfNodeInPath);
		GraphAmas amas = new GraphAmas(env);
		
		long maxWaitingTime = 15*60000; // 15min in milliseconds
		//long maxWaitingTime = 90*60000;
		amas.setMaxWaitingTime(maxWaitingTime);
		
		String start = "M";
		String end = "B";
		
		/*
		Position startPos = env.getEdgeMap().get(start).get(env.getEdgeMap().get(start).keySet().iterator().next()).get(0).getFirstPos();
		amas.initOnePathSearch(startPos, end);
		
		while (env.getCycleNumber() < env.getGraph().nodes().count())
			amas.getScheduler().step();
		
		ArrayList<ArrayList<Position>> pathList = amas.getAgentMap().get(end).getPathList();
		System.out.println("\nAcceptable path from " + start + " to " + end + " : " + pathList);
		*/
		
		/*
		amas.searchAllPath(start, end);
		System.out.println("Chemins entre " + start + " et " + end + " : " + amas.getPathList().size());
		//System.out.println(amas.printList(resultPath));
		*/
		
		
		ArrayList<ArrayList<Position>> resultPath = amas.graphExplore();
		System.out.println("(graph exploration) path found : " + resultPath.size() + " | cycle number : " + env.getCycleNumber());
		//System.out.println(amas.printList(resultPath));
		
	}
}	
