package translator;

import org.graphstream.graph.*;
import org.graphstream.graph.implementations.*;
import org.graphstream.ui.swing_viewer.ViewPanel;
import org.graphstream.ui.view.Viewer;

public class GUI 
{
	private Graph graph;
	private String filename;
	
	public GUI(String file)
	{
		filename = "road-networks/" + file;
		graph = new MultiGraph(filename);
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
		
		// Display graph
		init_stylesheet();
		Viewer viewer = graph.display(false);   // No auto-layout.
		
		// Change edge colors (can be done dynamically)
		graph.edges().forEach(edge -> {
			float speedMax = ((float) edge.getNumber("speedMax")) / 130.0f;
			edge.setAttribute("ui.color", speedMax);
		});
	}
	
	public void init_stylesheet()
	{
		graph.setAttribute("ui.quality");
		graph.setAttribute("ui.antialias");
		graph.setAttribute("ui.stylesheet", "url('file://stylesheet')");
//		graph.setAttribute("ui.stylesheet", "node{stroke-mode:plain;stroke-color:black;fill-color:rgb(21,101,192);text-background-mode:rounded-box;text-padding:2;text-alignment:at-left;text-offset:-5;text-size:20;}");
	}
	
	public static void main(String args[]) 
	{	
		GUI view = new GUI("LeHavre.dgs");
		view.init();
	}
}
