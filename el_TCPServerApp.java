import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.*;
import java.util.Scanner;

public class el_TCPServerApp {
	private static int DEFAULT_PORT = 52903; // Default port #, chosen at random
	private static String COMMAND_REGEX = "-p"; // Regular expression that matches valid commands
	private static String LOG_FILENAME = "el_chat.txt"; // Filename of the chat log
	private static Clock clock; // A clock to keep track of chat duration
	
	// I put the main functions of the server into a wrapper class so that I can create and delete a connection
	// and its read/write functions all at once.
	private static el_TCPServer server;
	
	// Parse any and all args, then create and run the server
	public static void main(String[] args) {
		// Set the port # to the default port
		int port = DEFAULT_PORT;
		
		// While loop to parse the args and make changes
		int i = 0;
		while(i < args.length) {
			// Switch the input to lowercase, to avoid case-sensitivity
			String command = args[i].toLowerCase();
			// Check if the command is valid
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
			// Set the port number
			if(command.equals("-p")) {
				try {
					port = Integer.parseInt(args[i+1]);
				} catch(NumberFormatException e) {
					System.out.println("'" + args[i+1] + "' is not a valid port number.");
					System.exit(3);
				}
			}
			// Move on to the next command
			i += 2;
		}
		
		System.out.println("Opening port...");
		try {
			// Create a server object 
			server = new el_TCPServer(port);
		} catch(IOException e) {
			System.out.println("Unable to attach to port!");
			System.exit(4);
		}
		
		// Set up the clock used to time the length of a connection
		clock = Clock.systemDefaultZone();
		
		// Connect to clients until the program is halted, by error or by ctrl+c
		while(true) {  
			run();
		}
	}
	
	// This method gets run once per chat session and handles the server-side interactions
	private static void run() {
		try {
			System.out.println("!! Waiting for connection... !!");
			
			// Put the server into a waiting state
			server.openLink();
			// Save the start time
			Instant start = clock.instant();
			
			// Print local host name
			String host = InetAddress.getLocalHost().getHostName();
			System.out.println("Client has estabished a connection to " + host);
			
			// Set up a buffered writer to temporarily save the chat log, and delete any existing log
			Files.deleteIfExists(Paths.get(LOG_FILENAME));
			File file = new File(LOG_FILENAME);
			BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));
			
			// Receive and process the incoming data
			String msg = server.receive();
			while(!msg.equals("DONE")) {
				// Write the message to the log file
				bw.write(msg);
				bw.newLine();
				bw.flush();
				// Print the message to console
				System.out.println(msg);
				msg = server.receive();
			}
			bw.close();
			
			// Send a chat log back
			Scanner sc = new Scanner(file);
			while(sc.hasNextLine()) {
				msg = sc.nextLine();
				server.send(msg);
			}
			
			// Tell the client that the log is complete
			server.send("DONE");
			
			// Calculate and send session length
			Duration sessionLength = Duration.between(start, clock.instant());
			long lengthInSeconds = sessionLength.getSeconds();
			int hours = (int)(lengthInSeconds/3600);
			int minutes = (int)(lengthInSeconds/60%60);
			int seconds = (int)(lengthInSeconds%60);
			int milliseconds = (int)(sessionLength.getNano()/1000000);
			server.send(hours + "::" + minutes + "::" + seconds + "::" + milliseconds);
			
			// Close scanner and delete chat log
			sc.close();
			Files.deleteIfExists(file.toPath());
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			// Attempt to close the connection
			try {
				System.out.println("!!!! Closing connection... !!!!");
				server.close(); 
			} catch(IOException e) {
				System.out.println("Unable to disconnect!");
				System.exit(5);
			}
		}
	}
	
	// Print the help menu
	public static void printHelpMsg() {
		System.out.println("-p <port> - Set the port number");
		System.out.println("-help     - Show this message again");
	}
}
