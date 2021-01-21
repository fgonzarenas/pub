package translator;

import org.graphstream.graph.*;
import org.graphstream.graph.implementations.*;
import org.graphstream.ui.swing_viewer.ViewPanel;
import org.graphstream.ui.view.Viewer;

public class GUI 
{
	private Graph graph;
	
	public GUI(String file)
	{
		graph = new MultiGraph(file);
	}
	
	public void init()
	{
		System.setProperty("org.graphstream.ui", "swing");
		
		try
		{
			graph.read("road-networks/GraphTest.dgs");
		} 
		catch(Exception e) 
		{
			e.printStackTrace();
			System.exit(1);
		}
			
		init_stylesheet();
		
		Viewer viewer = graph.display(false);   // No auto-layout.
		/*ViewPanel view = (ViewPanel) viewer.getDefaultView(); // ViewPanel is the view for gs-ui-swing
		//view.resizeFrame(800, 600);
		view.getCamera().setViewCenter(3000, 8000, 0);
		view.getCamera().setViewPercent(0.25);*/
	}
	
	public void init_stylesheet()
	{
		graph.setAttribute("ui.quality");
		graph.setAttribute("ui.antialias");
//		graph.setAttribute("ui.stylesheet", "url('file://stylesheet')"); // J'ai pas trouv� la feuille de style, alors en attendant je l'ai mise en dur en dessous
		graph.setAttribute("ui.stylesheet", "node{stroke-mode:plain;stroke-color:black;fill-color:rgb(21,101,192);text-background-mode:rounded-box;text-padding:2;text-alignment:at-left;text-offset:-5;text-size:20;}");
	}
	
	public static void main(String args[]) 
	{	
		GUI view = new GUI("LeHavre.dgs");
		view.init();
	}
}
