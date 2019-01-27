import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

// Main driver class of the program. This program takes the given input file, generates the minimum paths between the
// nodes via Dijkstra's algorithm, then outputs and saves the result to the output file.
public class el_RMatrix {
	// Default filenames
	private static String DEFAULT_INPUT_FILE = "el_input.txt";
	private static String DEFAULT_OUTPUT_FILE = "el_output.txt";
	
	public static void main(String[] args) {
		// Initialize filenames to their default values
		String inputFile = DEFAULT_INPUT_FILE;
		String outputFile = DEFAULT_OUTPUT_FILE;
		// By default, the program will use Unicode
		boolean useAscii = false;
		// Iterate through the arguments and update filenames and useAscii accordingly
		int i = 0;
		while(i < args.length) {
			if(args[i].equals("-ascii")) {
				// Set useAscii to true
				useAscii = true;
			}
			// These options require an argument, so make sure they are not the last argument
			if(i + 1 < args.length) {
				if(args[i].equals("-i")) {
					// Set the input file
					inputFile = args[i + 1];
					i++;
				} else if(args[i].equals("-o")) {
					// Set the output file
					outputFile = args[i + 1];
					i++;
				}
			}
			i++;
		}
		// Initialize the graph
		el_NodeGraph graph = new el_NodeGraph();
		// Attempt to load the input file
		try {
			graph.readFile(new File(inputFile));
		} catch (FileNotFoundException e) {
			System.out.println("File '" + inputFile + "' not found!");
			System.exit(1);
		}
		// Display the adjacency table and minimum paths
		System.out.println("\nLoaded graph from file '" + inputFile + "'");
		System.out.println("\nAdjacency table:");
		System.out.println(graph.adjTable(useAscii));
		System.out.println("Minimum path table:");
		String minPathTable = graph.minPathTable(useAscii);
		System.out.println(minPathTable);
		System.out.println("Saving paths to '" + outputFile + "'...");
		// Attempt to save the minimum path table to the output file
		try {
			Scanner sc = new Scanner(minPathTable);
			BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile));
			while(sc.hasNextLine()) {
				bw.write(sc.nextLine());
				bw.newLine();
				bw.flush();
			}
			sc.close();
			bw.close();
			System.out.println("File saved.");
		} catch (IOException e) {
			System.out.println("Unable to save to file!");
		}
	}
}