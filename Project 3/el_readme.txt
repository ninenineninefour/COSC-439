Project 2
Elsie Lewis

	1. Compilation

You can compile the programs with the following commands:

>javac el_SocketConnection.java
>javac el_Parser.java
>javac el_TCPServer.java
>javac el_TCPClient.java

Note that el_SocketConnection and el_Parser are used by both the client and server programs. If you only want to
compile one of the two, the other one can be omitted.

	2. Running

To launch the client program, enter the command:

>java el_TCPClient

There are several arguments that can be added to launch it in different ways. These are:

-help		: Displays a help message
-p <port>	: Connect to the specified port
-h <host>	: Connect to the specified host
-u <username>	: Use the specified username

If no port or host is specified, the program will default to port #52903 and 127.0.0.1, respectively. If no username
is specified, the program will prompt you to enter one.

To launch the server program, enter the command:

>java el_TCPServer

The server also has command line arguments:

-help			: Displays a help message
-p <port>		: Opens the specified port
-l <filename>	: Writes the log to the specified file
-g <integer>	: Use the specified value of g (for encryption)
-p <integer>	: Use the specified value of n (for encryption)

Like with the client program, if no port is specified, it will default to port #52903. If no log filename is
specified, it will default to "el_chat.txt". If g or n are not specified, it will default to 4457 and 6449,
respectively (these values were chosen at random from a list of prime numbers)

	3. Using the program

Once you are connected to the server, you will receive a message displaying the values of g and n, as well as the
shared key with the server and the 1-time pad used for encryption. You can send messages by typing it in and pressing
enter. If any other users are connected, they will also see your message. When you are done, you can enter "DONE" to
disconnect from the server. The server will then send you the duration of your connection, in the format
hours::minutes::seconds::milliseconds.

	4. Example run

Server side

>java el_TCPServer -p 22222 -g 241 -n 2647
Opening port 22222...
Waiting for a connection...
User 'User1' has connected.
g=241, n=2647, shared key=173, pad=10101101
Waiting for a new connection...
User1: This is a message.
User1: This is another message.
User 'User2' has connected.
g=241, n=2647, shared key=185, pad=10111001
User2: I am a new user that has connected.
User1: Hello, new user.
User1: I am going to disconnect.
User 'User1' has disconnected.
User2: I am also going to disconnect.
User 'User2' has disconnected.

Client side (User1)

>java el_TCPClient -h 35.231.176.82 -p 22222 -u User1
Connected to server.
g=241, n=2647, shared key=173, pad=10101101
User 'User1' has connected.
User1: This is a message.
User1: This is another message.
User 'User2' has connected.
User2: I am a new user that has connected.
User1: Hello, new user.
User1: I am going to disconnect.
User1: DONE
Duration: 0::4::40::572
Closing connection...
You have disconnected.

Client side (User2)

>java el_TCPClient -h 35.231.176.82 -p 22222 -u User2
Enter username: User2
Connected to server.
g=241, n=2647, shared key=185, pad=10111001
User 'User1' has connected.
User1: This is a message.
User1: This is another message.
User 'User2' has connected.
User2: I am a new user that has connected.
User1: Hello, new user.
User1: I am going to disconnect.
User 'User1' has disconnected.
User2: I am also going to disconnect.
User2: DONE
Duration: 0::2::29::817
Closing connection...
You have disconnected.

	5. Development notes

This project was fairly simple, I just needed to write the encryptor and I'd already be most of the way done. The
rest of the work was just on improving the look and feel of the program, mainly by rearranging the order of messages
displayed when connecting to make more sense to read (so no more double "Waiting for a new connection..." messages).

I placed the encryptor in its own wrapper object to make implementation easier. Just plug and play!

Another thing I did was make sure that "DONE" and the duration of the connection are sent unencrypted. This is
because both of these can be used for a known-plaintext attack, since "DONE" is always the same, and the duration
can be calculated by an eavesdropper.

There is still one vulnerability in the encryption that could be used to do a known-plaintext attack, but with a
much more limited length. The server always puts a username and ": " before every message, which will remain the
same for all broadcasts from that user. An attacker could look for the part of the messages that are the same (# of
characters in the username +2), and know that the last two characters of that fixed text would always be a colon and
a space. Since this program encrypts each byte individually, they can reverse engineer the pad from these two
characters. However, I'm guessing that this project is more to demonstrate the concept than provide actual security.
Since every ASCII character is one byte long, and the encryptor maps every byte to another byte via the pad, it is
effectively a substitution cipher with 128 characters!