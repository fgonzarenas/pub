package generator;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
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
	
	private int nbAgent;
	private int nbPath;
	
	private int startYear;
	private int startMonth;
	private int startDay;
	private int startHour;
	private int endHour;
	
	
	public Generator(int nbA, int nbP, int startY, int startM, int startD, int startH, int endH) {
		view = new GUI("GraphTest.dgs");
		view.init();
		graph = view.getGraph();
		
		startNodes = new ArrayList<String>(Arrays.asList("I","J","K","L","M","O","P","Q","R"));
		endNodes = new ArrayList<String>(Arrays.asList("A","B","C","D","E","F","G","H","N"));
		idCounter = 1;
		
		nbAgent = nbA;
		nbPath = nbP;
		startYear = startY;
		startMonth = startM;
		startDay = startD;
		startHour = startH;
		endHour = endH;
	}
	
	// Shortest path between two nodes using Dijkstra
	public Path shortestPath(String n1, String n2) {
		Dijkstra dijkstra = new Dijkstra(Element.EDGE, null, "length");
		dijkstra.setSource(graph.getNode(n1));
		dijkstra.init(graph);
 		dijkstra.compute();
 		
 		Path path = dijkstra.getPath(graph.getNode(n2));
 		
 		return path;
	}
	
	public Path pathWithVariation(String n1, String n2) {
		// TODO
		return null;
	}
	
	public Agent createAgent() {
		Agent a = new Agent(idCounter);
		idCounter++;
		
		// random choice of starting and ending node
		Random random = new Random(System.currentTimeMillis());
		String start = startNodes.get(random.nextInt(startNodes.size()));
		String end = endNodes.get(random.nextInt(endNodes.size()));
		
		// generation of starting date
		int hour = random.nextInt(endHour-startHour+1) + startHour;
		int min =  random.nextInt(60);
		Calendar startCal = Calendar.getInstance();
		startCal.set(startYear, startMonth, startDay, hour, min, 0);
				
		
		for (int i=0; i<nbPath; i++) {
			// creation of the path
			Path path = shortestPath(start,end);
			
			// creation of the Calendar for this path
			Calendar travelCalendar = (Calendar) startCal.clone();
			travelCalendar.add(Calendar.DATE, i);
			
			GenPath p = new GenPath();
			List<Node> nodeList = path.getNodePath();
			List<Edge> edgeList = path.getEdgePath();
			
			p.addPosition(new Position(nodeList.get(0).getId(), new Timestamp(travelCalendar.getTimeInMillis())));
			for (int j=0; j<edgeList.size(); j++) {
				Integer travelTime = (Integer) edgeList.get(j).getAttribute("length");
				travelCalendar.add(Calendar.MINUTE, travelTime);
				p.addPosition(new Position(nodeList.get(j+1).getId(), new Timestamp(travelCalendar.getTimeInMillis())));
			}
			
			a.addPath(p);
		}
		
		return a;
	}
	
	// generate a list of nbAgent Agent
	public ArrayList<Agent> generate() {
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
    	int startYear = 2021;
		int startMonth = 0;
		int startDay = 1;
		int startHour = 6;
		int endHour = 10;
    	
    	Generator g = new Generator(10, 1, startYear, startMonth, startDay, startHour, endHour);

    	//g.exploreDepthFirst("A");
    	
    	// Set initial edge color
    	g.graph.edges().forEach(edge -> {
    		edge.setAttribute("ui.color", 0.f);
    	});
    	
        ArrayList<Agent> listAgent = g.generate();
        	
        // Display paths     
        for(Agent agent : listAgent)
        {	
        	for(GenPath gp : agent.getPath())
        	{	
        		ArrayList<Position> path = gp.getPath();
        		
        		for(int i = 0; i < path.size() - 1; i++)
        		{
        			// Edge id is source node id concatenated to target node id
        			String source = path.get(i).getNode();
        			String target = path.get(i+1).getNode();
        				
        			Edge edge = g.graph.getEdge(source + target);
        				
        			// If edge not found, invert source and target id
        			if(edge == null)
        			{
        				edge = g.graph.getEdge(target + source);
        			}
        			
        			float passages = ((float) edge.getNumber("ui.color")) + 1f;
        			
        			edge.setAttribute("ui.color", passages);
        		}
        	}
        }
        
        g.graph.edges().forEach(edge -> {
        	edge.setAttribute("ui.color", ((float) edge.getNumber("ui.color")) / (g.nbAgent * g.nbPath));
    	});
    }
}
