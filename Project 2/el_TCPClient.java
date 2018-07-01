import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class el_TCPClient {
	private static int DEFAULT_PORT = 52903; // Default port #, chosen at random
	private static String DEFAULT_HOST = "127.0.0.1"; // Default host (localhost)
	private static String[] OPTIONS = new String[] {"-u", "-h", "-p", "-help"};
	// I put the main functions of the client into a wrapper class so that I can create and delete a connection and
	// its read/write functions all at once.
	private static el_SocketConnection connection;
	private static ClientSender sender;
	private static ClientReceiver receiver;
	
	// Parse any and all args, then create and run a client instance
	public static void main(String[] args) {
		String username = null;
		String hostAddress = DEFAULT_HOST;
		int port = DEFAULT_PORT;
		// Use a Parser to parse the input (see el_Parser.java for relevant documentation)
		el_Parser parser = new el_Parser(args, OPTIONS);
		if(parser.hasOption("-help")) {
			printHelpMsg();
			System.exit(0);
		}
		Object temp = parser.argOfAsObject("-u", String.class);
		if(temp != null)
			username = (String)temp;
		temp = parser.argOfAsObject("-h", String.class);
		if(temp != null)
			hostAddress = (String)temp;
		temp = parser.argOfAsObject("-p", Integer.class);
		if(temp != null)
			port = (int)temp;
		// Set up the connection
		try {
			Socket link = new Socket(InetAddress.getByName(hostAddress), port);
			connection = new el_SocketConnection(link);
		} catch(IOException e) {
			System.out.println("Host '" + hostAddress + "' not found!");
			System.exit(1);
		}
		
		// Initialize keyboard input Scanner
		Scanner sc = new Scanner(System.in);
		
		// If the user did not specify a username, ask for one now
		if(username == null) {
			System.out.print("Enter username: ");
			username = sc.nextLine();
		}
		
		// Set up and run the sender and receiver threads
		sender = new ClientSender(connection, sc, username);
		receiver = new ClientReceiver(connection, username);
		
		sender.start();
		receiver.start();
	}
	
	// Print a help message containing the various commands and their syntax
	public static void printHelpMsg() {
		System.out.println("-u <username> - Set the username");
		System.out.println("-h <host>     - Set the host address");
		System.out.println("-p <port>     - Set the port number");
		System.out.println("-help         - Show this message again");
	}
	
	// This thread handles sending messages to the server
	static class ClientSender extends Thread {
		private el_SocketConnection connection;
		private Scanner sc;
		private String username;
		
		// Constructor; this object shares the connection and username with the receiver
		private ClientSender(el_SocketConnection connection, Scanner sc, String username) {
			this.connection = connection;
			this.sc = sc;
			this.username = username;
			connection.send(username);
			System.out.println("Connected to server.");
		}
		
		// Runs the thread
		public void run() {
			System.out.print(username + ": ");
			String msg = sc.nextLine();
			while(!msg.equals("DONE")) {
				connection.send(msg);
				System.out.print(username + ": ");
				msg = sc.nextLine();
			}
			connection.send("DONE");
			sc.close();
		}
	}
	
	// This thread handles receiving messages from the server
	static class ClientReceiver extends Thread {
		private el_SocketConnection connection;
		private String username;
		
		// Constructor; this object shares the connection and username with the sender
		private ClientReceiver(el_SocketConnection connection, String username) {
			this.connection = connection;
			this.username = username;
		}
		
		// Runs the thread
		public void run() {
			try {
				String msg = connection.receive();
				while(!msg.equals("DONE")) {
					// Delete the input prompt before printing message (this makes it look nicer, assuming no text is
					// already entered). To make this work and look good if there is already text there, I'd need a
					// way to get the text that has already been entered before the user presses enter, and I'm not
					// sure there's a good way to do that.
					for(int i = 0; i < (username + ": ").length(); i++) {
						System.out.print("\b");
					}
					System.out.println(msg);
					// Put the input prompt back in
					System.out.print(username + ": ");
					msg = connection.receive();
				}
				// Delete extra input prompt
				for(int i = 0; i < (username + ": ").length(); i++) {
					System.out.print("\b");
				}
				// Close connection and exit the program
				System.out.println("Closing connection...");
				connection.close();
				System.out.println("You have disconnected.");
				System.exit(0);
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}
}