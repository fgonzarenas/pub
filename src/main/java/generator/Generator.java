package generator;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Random;

import org.graphstream.graph.*;

import org.graphstream.algorithm.*;
import org.graphstream.algorithm.Dijkstra.Element;

import translator.GUI;

public class Generator {
	private GUI view;
	private Graph graph;
	
	private ArrayList<String> startNodes;
	private ArrayList<String> endNodes;
	private int idCounter;
	
	public Generator() {
		view = new GUI("GraphTest.dgs");
		view.init();
		graph = view.getGraph();
		
		startNodes = new ArrayList<String>(Arrays.asList("I","J","K","L","M","O","P","Q","R"));
		endNodes = new ArrayList<String>(Arrays.asList("A","B","C","D","E","F","G","H","N"));
		idCounter = 1;
	}
	
	// Shortest path between two nodes using Dijkstra
	public Path shortestPath(String n1, String n2) {
		Dijkstra dijkstra = new Dijkstra(Element.EDGE, null, "length");
		dijkstra.setSource(graph.getNode(n1));
		dijkstra.init(graph);
 		dijkstra.compute();
 		
 		Path path = dijkstra.getPath(graph.getNode(n2));
 		/*
 		// Résultat du plus court chemin
 		System.out.println("	path : " + path);
 		System.out.println("	path length : " + dijkstra.getPathLength(graph.getNode(n2)));
 		
 		
 		// Dessin du plus court chemin
 		for (Edge edge : dijkstra.getPathEdges(graph.getNode(n2))) {
			edge.setAttribute("ui.style", "fill-color: red;");
 		}
 		*/
 		
 		return path;
	}
	
	public Agent createAgent() {
		//System.out.println("Agent " + idCounter + " :");
		Agent a = new Agent(idCounter);
		idCounter++;
		
		// random choice of starting and ending node
		Random random = new Random(System.currentTimeMillis());
		String start = startNodes.get(random.nextInt(startNodes.size()));
		String end = endNodes.get(random.nextInt(endNodes.size()));
		
		// creation of the path
		Path path = shortestPath(start,end);
		for (Node n : path.getNodePath()) {
			a.addPosition(new Position(n.getId(), new Timestamp(System.currentTimeMillis())));
		}
			
		return a;
	}
	
	// generate a list of nbAgent Agent
	public ArrayList<Agent> generate(int nbAgent) {
		ArrayList<Agent> result = new ArrayList<Agent>();
		
		for (int i=0; i<nbAgent; i++)
			result.add(createAgent());
		
		return result;
	}
	
	// Depth first for example but useless
	public void exploreDepthFirst(String s) {
		Node source = graph.getNode(s);
        Iterator<? extends Node> k = source.getDepthFirstIterator();

        while (k.hasNext()) {
            Node next = k.next();
            next.setAttribute("ui.style", "fill-color: red;");
            sleep();
        }
    }
	
	// function to slow down the display
    protected void sleep() {
        try { 
        	Thread.sleep(1000); 
        } catch (Exception e) {
        	e.printStackTrace();
        }
    }
    
    public static void main(final String[] args) { 
    	Generator g = new Generator();
    	g.generate(5);
    	//g.exploreDepthFirst("A");
    	//g.shortestPath("K","D");
    	/*
    	for (int i=0; i<5; i++) {
    		g.createAgent();
    	}
    	*/
    }
}
