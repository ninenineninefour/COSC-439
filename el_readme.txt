					TCP Client/Server User's Guide

	1. Compiling

The client and server programs both come as uncompiled .java files. To use them, you must first install Java (look online for
guides if it isn't installed already). To compile the TCP client, open a command line and navigate to the folder containing the
.java files. Then, enter the following commands:

> javac el_TCPClient
> javac el_TCPClientApp

To compile the TCP server, use these commands:

> javac el_TCPServer
> javac el_TCPServerApp

This should create several .class files which you can use to run the program.

	2. Running the Programs

After you have compiled the client or server files, you can run the program with either

> java el_TCPClientApp

for the client program, or

> java el_TCPServerApp

for the server program. In addition, there are several arguments you can run them with to specify certain settings, which are
covered in section 3.

	3. Arguments

There are four valid arguments for the client program. These are "-help", "-u", "-h", and "-p". These are explained below.

> -help

This makes the program display a help menu.

> -u <username>

This sets the username used to connect to a desired value. If the username is not specified with this argument, the program will
prompt you to enter one.

> -h <hostname>

This is used to select a host to connect to. This can be an IP address, such as 101.95.143.191, or a domain, such as example.com.
If this argument is not used, the program will default to using 127.0.0.1, which is your local IP address.

> -p <port>

This is used to specify a specific port to use. The port can be any 16-bit number, that is, any number between 0 and 65535. If
this argument is not used, it will default to 52903, which was selected randomly when writing the program.

The server only accepts two arguments, "-help" and "-p". These function identically to how they would in the client program.

Some examples of valid inputs to run the programs include:

> java el_TCPClientApp -u John -h 101.95.143.191

This would start up the client program with the username "John", attempting to communicate with the host at 101.95.143.191.

> java el_TCPServerApp -p 12345

This would start up the server program, which would listen for users attempting to access port #12345.

	4. Using the program

When you start the client program, it will attempt to open a connection to the specified host and port. If it fails to do so, it will
report that it could not connect and exit. At this point, if you have not yet specified a username, it will prompt you to enter
one now.

The program will then ask you to enter a message. You can enter messages to send to the server now. When you are finished, write
send "DONE". The program will then begin to end the session. It will display the chat log sent back from the server, as well as
the total elapsed time of the connection (in the format hours::minutes::seconds::milliseconds). Finally, it will close the
connection and exit.

When you start the server program, it will open the port and then wait for a user to connect, and notify you if that occurs. It
will then display the username of the connecting user, and display every message they send until they disconnect. When the user
disconnects, it replays the chat log and closes the connection. Finally, it opens the connection to allow for a new user to
connect. To exit out of the server program, use CTRL+C.

	5. Example Run

Server side:

> java el_TCPServerApp -p 12345
> Opening port...
> !! Waiting for connection... !!
> Client has estabished a connection to HOSTNAME
> John
> This is a message.
> This is another message.
> This will be my last message.
> !!!! Closing connection... !!!!
> !! Waiting for connection... !!

Client side:

> java el_TCPClientApp -h 101.95.143.191 -p 12345
> Enter username: John
> Enter message: This is a message.
> Enter message: This is another message.
> Enter message: This will be my last message.
> Enter message: DONE
> Ending session...
>
> Chat log:
>
> John
> This is a message.
> This is another message.
> This will be my last message.
> Elapsed time: 0::0::32::937
>
> !!!! Closing connection... !!!!

	6. Development notes

I decided partway through the project that I wanted to seperate the TCP client/server from the driver program. This should make it
easier to reuse some code further down the line, since I don't need to set up and close them manually. This is the reason why
there are four classes instead of two.

I thought it might be a clever trick to define the port variable as a short, since I realized that ports were a 16-bit number.
However, this ran into problems, since I forgot that Java uses two's compliment, so a short actually goes from -32768 to 32767.
Similarly, I tried initializing all of the time variables other than hours as bytes, since they should only range from 0-60.
However, I forgot that milliseconds were 1/1000 of a second, rather than 1/100th of a second like I anticipated. In the end I
decided to just make them all regular ints because the memory saved would be fairly small and it probably wouldn't be worth the
headache.

I think I spent the most time in the project working on the parser for the user's arguments, and making sure it could handle
various invalid inputs. I think in the future I will either see if there is a convenient library for this, or perhaps write one
myself to use for future projects.

When the server sends back the chat log, I had it mark the end of the log by sending "DONE", since there is no way for this to be
added to the log during normal operation (since the client sending "DONE" ends the session). In theory, if the log file is
modified by another program during runtime to add a "DONE" to it, this could cause unexpected errors. However, it seemed like a
reasonable assumption to make.

As is usual for projects I work on, the main program itself was completed fairly rapidly, in about an hour or two. The remaining
time was spent polishing it, making sure it matches the assignment guidelines, and adding documentation. Another fairly large
chunk of time was spent troubleshooting issues with setting up the GCP virtual machine. I ran into issues with it when I used
copy & paste to enter some of the commands more quickly; the PDF file uses special characters for quotation marks and hyphens,
which the command line doesn't recognize, despite their visual similarity.