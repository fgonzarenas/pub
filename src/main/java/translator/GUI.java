package translator;

import java.util.ArrayList;

import org.graphstream.graph.*;
import org.graphstream.graph.implementations.*;
//import org.graphstream.ui.swing_viewer.ViewPanel;
import org.graphstream.ui.view.Viewer;
import generator.*;

public class GUI 
{
	private Graph graph;
	private String filename;
	
	public GUI(String file)
	{
		filename = file;
		graph = new MultiGraph(filename);
	}
	
	public GUI(String file, Graph g)
	{
		filename = file;
		graph = g;
	}
	
	public Graph getGraph() 
	{
		return graph;
	}
	
	public void init()
	{
		System.setProperty("org.graphstream.ui", "swing");
		
		// Load graph
		try
		{
			graph.read(filename);
		} 
		catch(Exception e) 
		{
			e.printStackTrace();
			System.exit(1);
		}
		
		init_stylesheet();
		graph.display(false);
	}
	
	public void init_stylesheet()
	{
		graph.setAttribute("ui.quality");
		graph.setAttribute("ui.antialias");
		graph.setAttribute("ui.stylesheet", "url('file://stylesheet')");
		//graph.setAttribute("ui.stylesheet", "node{stroke-mode:plain;stroke-color:black;fill-color:rgb(21,101,192);text-background-mode:rounded-box;text-padding:2;text-alignment:at-left;text-offset:-5;text-size:20;}node.marked{fill-color: red;}");
	}
	
	// Display traffic across all recorded timestamps 
	public void displayAllTraffic(int[][] traffic, float div)
	{
		// Initialize to 0
		graph.edges().forEach(edge -> {
	    	edge.setAttribute("ui.color", 0f);
	    });
		
		// Accumulate passages
	    for(int i = 0; i < traffic.length; i++)
	    {
	    	for(int i_edge = 0; i_edge < traffic[i].length; i_edge++)
	    	{
	    		Edge edge = graph.getEdge(i_edge);
	        		
	        	float passages = ((float) edge.getNumber("ui.color")) + traffic[i][i_edge];
	    			
	    		edge.setAttribute("ui.color", passages);
	        }
	    }
	     
	    // Normalize
	    graph.edges().forEach(edge -> {
	    	edge.setAttribute("ui.color", ((float) edge.getNumber("ui.color")) / div);
	    });
	}
		
	// Display traffic on a specific timestamp 
	public void displayTraffic(int[][] traffic, int i, float div)
	{
		// Set number of passages and normalize
		for(int i_edge = 0; i_edge < traffic[i].length; i_edge++)
		{
			Edge edge = graph.getEdge(i_edge);
		        	
		    float passages = traffic[i][i_edge] / div;
		    			
		    edge.setAttribute("ui.color", passages);
		}
	}
			
	
	// function to slow down the display
    public void sleep(int millis) {
        try { 
        	Thread.sleep(millis); 
        } catch (Exception e) {
        	e.printStackTrace();
        }
    }		
}
