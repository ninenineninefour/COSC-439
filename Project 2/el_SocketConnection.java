import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

// Wrapper class to allow sending and receiving messages from a socket easier
public class el_SocketConnection {
	private Socket link;
	private Scanner in;
	private PrintWriter out;
	private boolean isOpen = false;
	
	// Constructor
	public el_SocketConnection(Socket link) throws IOException {
		this.link = link;
		in = new Scanner(new InputStreamReader(link.getInputStream())); 
		out = new PrintWriter(link.getOutputStream(), true);
		isOpen = true;
	}
	
	// Sends a String to the client and return true if successful
	public boolean send(String message) {
		// If the connection is closed, the sending fails
		if(!isOpen)
			return false;
		out.println(message);
		return true;
	}
	
	// Receive a String from the client, or null if it cannot
	public String receive() throws IOException {
		// If the connection is closed, it cannot receive a message
		if(!isOpen)
			return null;
		return in.nextLine();
	}
	
	// Close the link
	public void close() throws IOException {
		// Setting isOpen happens first, in case one of the close operations throws an exception
		isOpen = false;
		in.close();
		out.close();
		link.close();
	}
}
