package translator;

import org.graphstream.graph.*;
import org.graphstream.graph.implementations.*;

public class GUI 
{
	private Graph graph;
	private String filename;
	
	public GUI(String file)
	{
		filename = "road-networks/" + file;
		graph = new MultiGraph(filename);
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
	}
	
	public static void main(final String[] args) {
		GUI g = new GUI("GraphTest.dgs");
		g.init();
	}
	
}
