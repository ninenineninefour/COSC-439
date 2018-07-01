import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

// Wrapper class for a TCP client which sends and receives Strings from a server
// This client is only used for one connection, so multiple connections require re-initialization
public class el_TCPClient {
	private InetAddress host;
	private Socket link;
	private Scanner in;
	private PrintWriter out;
	private boolean isOpen = false;
	
	// Constructor, sets up the connection as well as the IO objects
	public el_TCPClient(String hostName, int port) throws IOException {
		host = InetAddress.getByName(hostName);
		link = new Socket(host, port);
		in = new Scanner(new InputStreamReader(link.getInputStream())); 
		out = new PrintWriter(link.getOutputStream(), true);
		// Setting isOpen happens last, since all three must have been opened successfully
		isOpen = true;
	}
	
	// Sends a String to the server and returns true if successful
	public boolean send(String message) {
		// Cannot send a message if it is closed!
		if(!isOpen)
			return false;
		out.println(message);
		return true;
	}
	
	// Receives a String from the server, or return null if unsuccessful
	public String receive() throws IOException {
		// Cannot receive a message if it is closed!
		if(!isOpen)
			return null;
		return in.nextLine();
	}
	
	// Closes the various objects when they are no longer needed
	public void close() throws IOException {
		// Setting isOpen happens first, in case one of the close operations throws an exception
		isOpen = false;
		in.close();
		out.close();
		link.close();
	}
}
