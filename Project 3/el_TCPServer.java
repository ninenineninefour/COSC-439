// Elsie Lewis Project 3
// VM IP: 35.227.23.98
// VM Port: 30100

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Scanner;

public class el_TCPServer {
	private static String[] OPTIONS = new String[] {"-p", "-l", "-g", "-n", "-help"}; // List of valid options
	private static int DEFAULT_PORT = 30100; // Default port #
	private static String DEFAULT_LOG_FILENAME = "el_chat.txt"; // Default filename of the chat log
	private static int DEFAULT_PRIME_G = 4457; // Default value of 'g'
	private static int DEFAULT_PRIME_N = 6449; // Default value of 'n'
	private static Clock clock; // A clock to keep track of chat duration
	private static ServerSocket servSock; // Server socket used
	private static ArrayList<el_SocketConnection> sockets; // List of active connections
	private static File file; // Log file
	private static BufferedWriter bw; // File writer for logging
	
	public static void main(String[] args) {
		sockets = new ArrayList<>(); // initialize socket list
		
		// Initialize variables to their default values
		int port = DEFAULT_PORT;
		String logFilename = DEFAULT_LOG_FILENAME;
		int primeG = DEFAULT_PRIME_G;
		int primeN = DEFAULT_PRIME_N;
		
		// Use a Parser to parse the input (see el_Parser.java for relevant documentation)
		el_Parser parser = new el_Parser(args, OPTIONS);
		if(parser.hasOption("-help")) {
			printHelpMsg();
			System.exit(0);
		}
		Object temp = parser.argOfAsObject("-p", Integer.class);
		if(temp != null)
			port = (int)temp;
		temp = parser.argOfAsObject("-l", String.class);
		if(temp != null)
			logFilename = (String)temp;
		temp = parser.argOfAsObject("-g", Integer.class);
		if(temp != null)
			primeG = (int)temp;
		temp = parser.argOfAsObject("-n", Integer.class);
		if(temp != null)
			primeN = (int)temp;
		
		// Check if the port number is valid
		if(port < 0 || port > 65535) {
			System.out.println("Invalid port number: " + port);
			System.exit(1);
		}
		
		System.out.println("Opening port " + port + "...");
		try {
			// Initialize the server socket, file, and buffered writer
			servSock = new ServerSocket(port);
			Files.deleteIfExists(Paths.get(logFilename));
			file = new File(logFilename);
			bw = new BufferedWriter(new FileWriter(file, true));
		} catch(IOException e) {
			System.out.println("Unable to attach to port!");
			e.printStackTrace();
			System.exit(1);
		}
		
		// Initialize the clock
		clock = Clock.systemDefaultZone();
		
		// Loop to continue indefinitely
		while(true) {
			try {
				System.out.println("Waiting for a new connection...");
				// Set up the socket connection
				el_SocketConnection connection = new el_SocketConnection(servSock.accept());
				connection.initHandshake(primeG, primeN);
				sockets.add(connection);
				
				// Check if file exists, and if not, create a new one
				if(!file.exists()) {
					file = new File(logFilename);
					file.createNewFile();
					bw = new BufferedWriter(new FileWriter(file, true));
				}
				
				// Initialize and run client handler thread
				ClientHandler clientHandler = new ClientHandler(sockets, connection, clock, bw, file);
				clientHandler.username = connection.receive();
				clientHandler.sendLog();
				// Broadcast that the user has connected
				String msg = "User '" + clientHandler.username + "' has connected.";
				clientHandler.sendAll(msg);
				System.out.println(msg);
				clientHandler.writeToLog(msg);
				connection.send(msg); // Echo message to user to be consistent with the log
				System.out.println(connection.encryptor()); // Print encryption variables
				clientHandler.start();
			} catch(IOException e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}
	
	// Print a help message containing the various commands and their syntax
	private static void printHelpMsg() {
		System.out.println("-p <port>     - Set the port number");
		System.out.println("-l <filename> - Set the log's filename");
		System.out.println("-help         - Show this message again");
	}
	
	// Client handler thread object, one is created per client
	private static class ClientHandler extends Thread {
		private ArrayList<el_SocketConnection> sockets; // List of connections
		private el_SocketConnection connection; // This client's connection
		private Clock clock; // Clock, for giving duration of connection
		private BufferedWriter bw; // Log file writer
		private File file; // Log file
		private String username = "NO_USERNAME"; // Client's username (default should never appear!)
		
		// Constructor, all items are initialize in the main method and passed on to each object
		public ClientHandler(ArrayList<el_SocketConnection> sockets, el_SocketConnection connection, Clock clock, BufferedWriter bw, File file) {
			this.sockets = sockets;
			this.connection = connection;
			this.clock = clock;
			this.bw = bw;
			this.file = file;
		}
		
		public void run() {
			// Record the start of the connection
			Instant start = clock.instant();
			
			try {
				// Keep receiving messages until it receives DONE
				String msg = connection.receive();
				while(!msg.equals("DONE")) {
					// Add the username to the message
					msg = username + ": " + msg;
					// Send, print, and log message
					sendAll(msg);
					System.out.println(msg);
					writeToLog(msg);
					// Receive the next message
					msg = connection.receive();
				}
				
				// Calculate and send the length of the session
				Duration sessionLength = Duration.between(start, clock.instant());
				long lengthInSeconds = sessionLength.getSeconds();
				msg = "Duration: " + lengthInSeconds/3600;
				msg += "::" + lengthInSeconds/60%60;
				msg += "::" + lengthInSeconds%60;
				msg += "::" + sessionLength.getNano()/1000000;
				// Both the DONE message and the duration are sent as plaintext to avoid a known-plaintext attack, 
				// since an attacker would know that DONE is the second-to-last message, and the last message is
				// always the elapsed time. This would allow an eavesdropper to read the messages afterwards.
				connection.sendPlaintext("DONE");
				connection.sendPlaintext(msg);
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				// Attempt to close the connection
				try {
					connection.close();
					// Broadcast that the user has disconnected
					String msg = "User '" + username + "' has disconnected.";
					System.out.println(msg);
					sendAll(msg);
					// Remove that connection from the list of connections
					sockets.remove(connection);
					// If the list is empty, that means all users have disconnected, so the log file is deleted
					if(sockets.size() == 0)
						file.delete();
				} catch(IOException e) {
					System.out.println("Unable to disconnect user '" + username + "'!");
					System.exit(1);
				}
			}
		}
		
		// Sends the given string to every connection except for the current one
		private void sendAll(String s) {
			for(el_SocketConnection socket : sockets) {
				if(socket != connection)
					socket.send(s);
			}
		}
		
		// Writes a message to the log file, synchronized between threads
		private synchronized void writeToLog(String s) {
			try {
				bw.write(s);
				bw.newLine();
				bw.flush();
			} catch (IOException e) {
				System.out.println("Unable to write message: " + s);
				e.printStackTrace();
				System.exit(1);
			}
		}
		
		// Sends the entire log file to the client
		private synchronized void sendLog() {
			try {
				// Create file scanner
				Scanner sc = new Scanner(file);
				// Send each line
				while(sc.hasNext()) {
					connection.send(sc.nextLine());
				}
				sc.close();
			} catch (FileNotFoundException e) {
				System.out.println("Unable to send log file!");
				e.printStackTrace();
				System.exit(1);
			}
			
		}
	}
}
