package translator;

import java.io.FileWriter;

public interface PUBTranslator 
{
	abstract String graphToDGS(String filename);
	abstract String pathsToJSON(String filename);
	abstract String trafficToCSV(int[][] traffic);
	
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
