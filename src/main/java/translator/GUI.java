package translator;
import org.graphstream.graph.*;
import org.graphstream.graph.implementations.*;

public class GUI 
{
	public static void main(String args[]) 
	{
		System.setProperty("org.graphstream.ui", "swing");
		Graph graph = new MultiGraph("Le Havre");
			
		try
		{
			graph.read("road-networks/LeHavre.dgs");
		} 
		catch(Exception e) 
		{
			e.printStackTrace();
			System.exit(1);
		}
			
		graph.display(false);   // No auto-layout.
	}
}
