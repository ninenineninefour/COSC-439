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

-help		: Displays a help message
-p <port>	: Opens the specified port
-l <filename>	: Writes the log to the specified file

Like with the client program, if no port is specified, it will default to port #52903. If no log filename is
specified, it will default to "el_chat.txt".

	3. Using the program

Once you are connected to the server, you can send messages by typing it in and pressing enter. If any other users are
connected, they will also see your message. When you are done, you can enter "DONE" to disconnect from the server. The
server will then send you the duration of your connection, in the format hours::minutes::seconds::milliseconds.

	4. Example run

Server side

>java el_TCPServer -p 22222
Opening port 22222...
Waiting for a new connection...
Waiting for a new connection...
User 'User1' has connected.
User1: This is a message.
User1: This is another message.
User 'User2' has connected.
User2: I am a new user that has connected.
User1: Hello, new user.
User1: I am going to disconnect.
User 'User1' has disconnected.
User2: I am also going to disconnect.
User 'User2' has disconnected.

Client side (User1)

>java el_TCPClient -h 35.231.176.82 -p 22222 -u User1
Connected to server.
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

You may note that the server program has an additional argument; this is because the Google Cloud Platform VM had some
issues with the default file location for some reason, so I needed a way to specify a specific one.

I had some issues with making the interface have a nice format when it is simultaneously expecting the user to enter
inputs as well as printing to the console. I was able to make a bit of a workaround by having the program delete the
input prompt before printing a message by printing the "\b" (backspace) control character repeatedly. However, if
there is already text that has been input, but the Scanner has not received (i.e. the user has not pressed enter), it
does not know to delete that text as well. A possible workaround would be to have the program perform a virtual
keypress, but that could cause issues with other programs the user is running. Ultimately I decided to leave it as-is,
it works fine so long as no one sends you a message while you'retyping. In an actual application, there would probably
be split messages and input boxes to avoid this issue.

I found in the last project I spent lots of time writing the parser to parse the user's inputs. I decided to avoid
this by writing a parser utility to handle the arguments. It can be found in the el_Parser.java file. I should be able
to reuse this object for any other program that needs to handle command line arguments.

I also got rid of the separate client and server objects in favor of a socket connection object usable by the server
or by the client. This is because I needed more direct access to certain objects that were previously encapsulated by
my client/server wrapper classes, and to reduce the number of unique object types in favor of more universal ones.

As always, the bulk of the programming was editing the look and feel of the program, as well as fixing miscellaneous
bugs that arose during testing. It took me about as long as the first project, since I reworked a lot of the
implementation.