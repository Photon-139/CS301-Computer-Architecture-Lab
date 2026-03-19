package generic;

import java.io.PrintWriter;

public class Statistics {
	
	// TODO add your statistics here
	static int numberOfInstructions;
	static int numberOfCycles;
	static int OF_stallCounter;
	static int wrongPath_counter;
	

	public static void printStatistics(String statFile)
	{
		try
		{
			PrintWriter writer = new PrintWriter(statFile);
			
			writer.println("Number of instructions executed = " + numberOfInstructions);
			writer.println("Number of cycles taken = " + numberOfCycles);
			writer.println("Number of times OF stage was stalled = "+OF_stallCounter);
			writer.println("Number of times an instruction on a wrong path entered the pipeline = "+wrongPath_counter);
			double throughput = (double) numberOfInstructions / numberOfCycles;
			writer.println("Throughput (instructions per cycle) = " + String.format("%.4f", throughput));
			
			// TODO add code here to print statistics in the output file
			
			writer.close();
		}
		catch(Exception e)
		{
			Misc.printErrorAndExit(e.getMessage());
		}
	}
	
	// TODO write functions to update statistics
	public static void setNumberOfInstructions(int numberOfInstructions) {
		Statistics.numberOfInstructions = numberOfInstructions;
	}

	public static void setNumberOfCycles(int numberOfCycles) {
		Statistics.numberOfCycles = numberOfCycles;
	}
	public static int getNumberOfCycles() {
		return numberOfCycles;
	}
	public static int getNumberOfInstructions() {
		return numberOfInstructions;
	}

	public static void setOF_stallCounter(int oF_stallCounter) {
		OF_stallCounter = oF_stallCounter;
	}
	public static void setWrongPath_counter(int wrongPath_counter) {
		Statistics.wrongPath_counter = wrongPath_counter;
	}
	public static int getOF_stallCounter() {
		return OF_stallCounter;
	}
	public static int getWrongPath_counter() {
		return wrongPath_counter;
	}
}
