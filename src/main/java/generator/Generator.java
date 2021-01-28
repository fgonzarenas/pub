package generator;

import java.util.Iterator;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

import translator.GUI;

public class Generator {
	private GUI view;
	private Graph graph;
	
	public Generator() {
		view = new GUI("GraphTest.dgs");
		view.init();
		graph = view.getGraph();
		
		explore(graph.getNode("A"));
	}
	
	public void explore(Node source) {
        Iterator<? extends Node> k = source.getDepthFirstIterator();

        while (k.hasNext()) {
            Node next = k.next();
            next.setAttribute("ui.class", "marked");
            sleep();
        }
    }

    protected void sleep() {
        try { 
        	Thread.sleep(1000); 
        } catch (Exception e) {
        	e.printStackTrace();
        }
    }
    
    public static void main(final String[] args) { 
    	Generator g = new Generator();
    }
}
