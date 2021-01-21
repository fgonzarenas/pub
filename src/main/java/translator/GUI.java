package translator;

import org.graphstream.graph.*;
import org.graphstream.graph.implementations.*;

public class GUI {
	public static void main(String args[]) {
		System.setProperty("org.graphstream.ui", "swing");
		Graph graph = new MultiGraph("Graph de test");
		try {
			graph.read("road-networks/GraphTest.dgs");
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		graph.setAttribute("ui.quality");
		graph.setAttribute("ui.antialias");
		graph.setAttribute("ui.stylesheet", "node{stroke-mode:plain;stroke-color:black;fill-color:rgb(21,101,192);text-background-mode:rounded-box;text-padding:2;text-alignment:at-left;text-offset:-5;text-size:20;}");
		graph.display(false); // No auto-layout.
	}
}
