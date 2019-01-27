import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Scanner;

// This object represents a graph of nodes, each of which can be linked by an integer representing the cost or
// distance between them
public class el_NodeGraph {
	// List of nodes
	private ArrayList<Node> nodeList;
	
	// Hashmap, used to quickly lookup a node from its name
	private HashMap<String, Node> nameMap;
	
	// Constructor
	public el_NodeGraph() {
		nodeList = new ArrayList<>();
		nameMap = new HashMap<>();
	}
	
	// Load the given file and add its vertices to the graph
	public void readFile(File file) throws FileNotFoundException {
		Scanner sc = new Scanner(file);
		readFile(sc);
		sc.close();
	}
	// Helper method for readFile (used so the Scanner can be closed in the public method)
	private void readFile(Scanner sc) throws FileNotFoundException {
		while(sc.hasNext()) {
			// Get the name of the first node
			String name1 = sc.next();
			if(!sc.hasNext())
				return;
			
			// Get the name of the second node
			String name2 = sc.next();
			if(!sc.hasNextInt())
				return;
			
			// Get the cost of the link and add that connection
			link(name1, name2, sc.nextInt());
		}
	}
	
	// Retrieves the node with the given name. If none exist, make a new one and return it
	private Node getNode(String name) {
		// Get existing node from the name map
		Node node = nameMap.get(name);
		// If that node already exists, return it
		if(node != null)
			return node;
		
		// Otherwise, create a new node, add it to the node list and name map, then sort the node list
		node = new Node(name);
		nodeList.add(node);
		nodeList.sort(Comparator.comparing(Node::getName));
		nameMap.put(name, node);
		return node;
	}
	
	// Connects the two nodes with a vertex of the given cost, adding nodes if they do not exist
	public void link(String name1, String name2, int cost) {
		Node n1 = getNode(name1);
		Node n2 = getNode(name2);
		n1.adjCost.put(n2, cost);
		n2.adjCost.put(n1, cost);
	}
	
	// Create and return the adjacency matrix of the graph
	public String adjTable(boolean useAscii) {
		// Set up the column labels
		String[] names = new String[nodeList.size() + 1];
		names[0] = "";
		int i = 1;
		for(Node n : nodeList) {
			names[i] = n.name;
			i++;
		}
		
		// Initialize the table
		el_Table table = new el_Table(names, useAscii);
		// Populate the table with data
		
		for(Node n1 : nodeList) {
			String[] row = new String[names.length];
			row[0] = n1.name;
			i = 1;
			for(Node n2 : nodeList) {
				Integer cost = n1.adjCost.get(n2);
				if(cost == null) {
					row[i] = "-";
				} else {
					row[i] = cost.toString();
				}
				i++;
			}
			table.addRow(row);
		}
		
		return table.toString();
	}
	
	// Create and return the minimum path matrix (routing matrix) of the graph
	public String minPathTable(boolean useAscii) {
		// Set up the column labels and calculate the minimum paths
		String[] names = new String[nodeList.size() + 1];
		names[0] = "";
		int i = 1;
		for(Node n : nodeList) {
			n.getPaths();
			names[i] = n.name;
			i++;
		}
		
		// Initialize the table
		el_Table table = new el_Table(names, useAscii);
		
		// Populate the table with data
		for(Node n1 : nodeList) {
			String[] row = new String[names.length];
			row[0] = n1.name;
			i = 1;
			for(Node n2 : nodeList) {
				Node.Pair pair = n1.minPathTo.get(n2);
				if(pair == null) {
					row[i] = "-";
				} else {
					row[i] = pair.node.name + "," + pair.cost;
				}
				i++;
			}
			table.addRow(row);
		}
		
		return table.toString();
	}
	
	// Subclass representing a single node
	private class Node {
		// The node's name
		private String name;
		
		// Hashmap of the cost to each adjacent node. If a node is not in the map, it is not adjacent
		private HashMap<Node, Integer> adjCost;
		
		// Hashmap of the minimum path (next node & cost) to each node
		private HashMap<Node, Pair> minPathTo;
		
		// Constructor
		private Node(String name) {
			this.name = name;
			adjCost = new HashMap<>();
		}
		
		// Getter for the name, used for sorting the main node list
		public String getName() {
			return name;
		}
		
		// Calculate the minimum paths via Dijkstra's algorithm
		private void getPaths() {
			// Initialize the hashmap
			minPathTo = new HashMap<>();
			// Add in all adjacent nodes into the map
			for(Node n : adjCost.keySet()) {
				minPathTo.put(n, new Pair(n, adjCost.get(n)));
			}
			// Traverse the graph from each adjacent node
			for(Node n : adjCost.keySet()) {
				traverse(n, adjCost.get(n), n);
			}
		}
		
		// Recursive method that traverses the graph starting from the given node
		private void traverse(Node node, int cost, Node next) {
			for(Node n : node.adjCost.keySet()) {
				// Check to make sure it's not looping back to the origin!
				if(n != this) {
					// Grab the existing path, if it exists
					Pair path = minPathTo.get(n);
					
					// Calculate the new cost to the node
					int newCost = cost + node.adjCost.get(n);
					
					// If the new cost is lower, or the existing path does not exist, update the minimum value and
					// traverse from that node
					if(path == null || path.cost > newCost) {
						minPathTo.put(n, new Pair(next, newCost));
						traverse(n, newCost, next);
					}
				}
			}
		}
		
		// Subclass containing a node and an integer, linked together
		private class Pair {
			private Node node;
			private Integer cost;
			
			private Pair(Node node, Integer cost) {
				this.node = node;
				this.cost = cost;
			}
			
		}
	}
}
