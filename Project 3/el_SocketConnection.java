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
	private el_Encryptor encryptor;
	private boolean secured = false;
	
	// Constructor
	public el_SocketConnection(Socket link) throws IOException {
		this.link = link;
		in = new Scanner(new InputStreamReader(link.getInputStream())); 
		out = new PrintWriter(link.getOutputStream(), true);
		isOpen = true;
	}
	
	// Sends a String to the client and return true if successful, encrypting if it is able
	public boolean send(String s) {
		// If the connection has been secured, encrypt the message
		if(secured)
			s = encryptor.encrypt(s);
		return sendPlaintext(s);
	}
	
	// Receive a String from the client, or null if it cannot, decrypting if it is able
	public String receive() throws IOException {
		String s = receivePlaintext();
		// If the connection has been secured, decrypt the message. Also, if it receives an unencrypted
		// DONE, it will not attempt decryption.
		if(s != null && !s.equals("DONE") && secured)
			s = encryptor.decrypt(s);
		return s;
	}
	
	// Sends an unencrypted String to the client and return true if successful
	public boolean sendPlaintext(String s) {
		// If the connection is closed, the sending fails
		if(!isOpen)
			return false;
		out.println(s);
		return true;
	}
	
	// Receive an unencrypted String from the client, or null if it cannot
	public String receivePlaintext() throws IOException {
		// If the connection is closed, it cannot receive a message
		if(!isOpen)
			return null;
		String s = in.nextLine();
		return s;
	}
	
	// Initiates a handshake with the other side of the connection
	public void initHandshake(int g, int n) {
		if(!isOpen)
			return;
		encryptor = new el_Encryptor(g, n);
		out.println(g);
		out.println(n);
		out.println(encryptor.publicKey());
		encryptor.calcPad(Integer.parseInt(in.nextLine()));
		secured = true;
	}
	
	// Receives a handshake from the other side of the connection
	public void receiveHandshake() {
		if(!isOpen)
			return;
		int g = Integer.parseInt(in.nextLine());
		int n = Integer.parseInt(in.nextLine());
		encryptor = new el_Encryptor(g, n);
		encryptor.calcPad(Integer.parseInt(in.nextLine()));
		out.println(encryptor.publicKey());
		secured = true;
	}
	
	// Getter for the encryptor
	public el_Encryptor encryptor() {
		return encryptor;
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
