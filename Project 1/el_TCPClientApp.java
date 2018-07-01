import java.io.*;
import java.util.Scanner;

public class el_TCPClientApp {
	private static int DEFAULT_PORT = 52903; // Default port #, chosen at random
	private static String DEFAULT_HOST = "127.0.0.1"; // Default host (localhost)
	private static String COMMAND_REGEX = "-(help|(u|h|p))"; // Regular expression that matches valid commands
	
	// I put the main functions of the client into a wrapper class so that I can create and delete a connection
	// and its read/write functions all at once.
	private static el_TCPClient client;
	
	// Parse any and all args, then create and run a client instance
	public static void main(String[] args) {
		String username = null;
		String hostAddress = DEFAULT_HOST;
		int port = DEFAULT_PORT;
		
		// While loop to parse the args and make changes
		int i = 0;
		while(i < args.length) {
			// Switch the input to lowercase, to avoid case-sensitivity
			String command = args[i].toLowerCase();
			if(!command.matches(COMMAND_REGEX)) {
				System.out.println("'" + args[i] + "' is not a valid command. Try '-help'." );
				System.exit(1);
			}
			// Check if the user wants to see the help message
			if(command.equals("-help")) {
				printHelpMsg();
				System.exit(0);
			}
			// Make sure the argument has a parameter to go along with it
			if(i + 1 >= args.length || args[i+1].toLowerCase().matches(COMMAND_REGEX)) {
				System.out.println("'" + args[i] + "' is missing an argument. Try '-help'.");
				System.exit(2);
			}
			
			if(command.equals("-u")) { // Set the username
				username = args[i + 1];
			} else if(command.equals("-h")) { // Set the host address
				hostAddress = args[i + 1];
			} else if(command.equals("-p")) { // Set the port number
				try {
					port = Integer.parseInt(args[i+1]);
				} catch(NumberFormatException e) {
					System.out.println("'" + args[i+1] + "' is not a valid port number.");
					System.exit(3);
				}
			}
			i += 2;
		}
		
		try {
			// Get server IP address
			client = new el_TCPClient(hostAddress, port);
		} catch(IOException e) {
			System.out.println("Host '" + hostAddress + "' not found!");
			System.exit(4);
		}
		
		// Initialize keyboard input Scanner
		Scanner sc = new Scanner(System.in);
		
		// If the user did not specify a username, ask for one now
		if(username == null) {
			System.out.print("Enter username: ");
			username = sc.nextLine();
		}
		
		// Run the client session
		run(username, sc);
		
		// Close the Scanner
		sc.close();
	}
	
	// This method handles the client session
	public static void run(String username, Scanner sc) {
		try {
			// Send the username to the server
			client.send(username);
			// Send messages from the input until the user enters "DONE"
			String msg;
			do {
				System.out.print("Enter message: ");
				msg = sc.nextLine();
				client.send(msg);
			} while(!msg.equals("DONE"));
			
			// Begin ending the session, gathering data back from the server
			System.out.println("Ending session...\n\nChat log:\n");
			
			msg = client.receive();
			while(!msg.equals("DONE")) {
				System.out.println(msg);
				msg = client.receive();
			}
			// Finally, get the duration of the session
			System.out.println("Elapsed time: " + client.receive());
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			try {
				// Close the connection
				System.out.println("\n!!!! Closing connection... !!!!");
				client.close(); 
			} catch(IOException e) {
				System.out.println("Unable to disconnect!");
				System.exit(5);
			}
		}
	}
	
	// Print a help message containing the various commands and their syntax
	public static void printHelpMsg() {
		System.out.println("-u <username> - Set the username");
		System.out.println("-h <host>     - Set the host address");
		System.out.println("-p <port>     - Set the port number");
		System.out.println("-help         - Show this message again");
	}
}
