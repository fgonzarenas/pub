package translator;

import java.io.FileWriter;

public interface PUBTranslator <G,P,T> 
{
	abstract String graphToDGS(G graph);
	abstract String pathsToJSON(P paths);
	abstract String trafficToCSV(T[][] traffic);
	
	default void writeFile(String content, String filename)
	{
		try {
  	      FileWriter writer = new FileWriter(filename);
  	      writer.write(content);
  	      writer.close();
  	      System.out.println("Successfully wrote to new file " + filename);
  	    } catch (Exception e) {
  	      e.printStackTrace();
  	    }
	}
}
