Project 4
Elsie Lewis

This project allows you to calculate the minimum paths in a graph to form a routing matrix. You can load the graph
through a text file, and the results are printed to the console and saved to an output file.

	1. Compilation

You can compile this program using these commands (once you have navigated to the source folder):

>javac el_RMatrix.java
>javac el_NodeGraph.java
>javac el_Table.java

Alternatively, you can run this command to compile all three files at once:

>javac *.java

	2. Running

You can run the program with the command:

>java el_RMatrix

There are a few command line arguments that can be entered to specify the desired behavior. These are:

-i <filename>	: Set the input file
-o <filename>	: Set the output file
-ascii		: Use ASCII characters only for box drawing

If no input or output file is selected, it will default to el_input.txt and el_output.txt, respectively.

When running the program, you may encounter a weird looking box like this:

   ?  A  ?  B  ?  C  ?  D  ?  E  
?????????????????????????????????
 A ?  -  ? B,1 ? D,4 ? D,2 ? D,6 
 B ? A,1 ?  -  ? C,4 ? A,3 ? C,6 
 C ? D,4 ? B,4 ?  -  ? D,2 ? E,2 
 D ? A,2 ? A,3 ? C,2 ?  -  ? C,4 
 E ? C,6 ? C,6 ? C,2 ? C,4 ?  -  

If this happens, your console interface does not support Unicode characters. You can add the -ascii argument to make it
use ASCII instead (for compatibility).

	3. Using the program

The program will then attempt to load the input file. If it fails, it will give an error and quit. Otherwise, it will
output an adjacency matrix, listing the costs between all adjacent nodes. Then, it will apply Dijkstra's algorithm to
find the minimum paths between the nodes and output that matrix. Finally, it will attempt to save the output to the
output file.

	4. Sample run

>java el_RMatrix
>
>Loaded graph from file 'el_input.txt'
>
>Adjacency table:
>   │ A │ B │ C │ D │ E 
>───┼───┼───┼───┼───┼───
> A │ - │ 1 │ - │ 2 │ - 
> B │ 1 │ - │ 4 │ - │ - 
> C │ - │ 4 │ - │ 2 │ 2 
> D │ 2 │ - │ 2 │ - │ 7 
> E │ - │ - │ 2 │ 7 │ - 
>
>Minimum path table:
>   │  A  │  B  │  C  │  D  │  E  
>───┼─────┼─────┼─────┼─────┼─────
> A │  -  │ B,1 │ D,4 │ D,2 │ D,6 
> B │ A,1 │  -  │ C,4 │ A,3 │ C,6 
> C │ D,4 │ B,4 │  -  │ D,2 │ E,2 
> D │ A,2 │ A,3 │ C,2 │  -  │ C,4 
> E │ C,6 │ C,6 │ C,2 │ C,4 │  -  
>
>Saving paths to 'el_output.txt'...
>File saved.

	5. Development notes

I found this project to be surprisingly easy. The main issue I dealt with early on was whether to represent the graph
using a matrix (in this case, an int array array) or using linked objects. In the end I decided that the memory savings
from using a fixed array would not be worth it compared the additional conceptual difficulty for implementing such a
design. Additionally, I figured that using linked objects would conceptually represent the physical system being
modeled (that is, a network of computers) better.

I found hashmaps to have surprising usefulness in situations where you need to access objects by name in a fast and
convenient manner. I used a similar method to lookup the index of arguments from their string in previous projects
using the ArgParser object I made. In this project, however, there were so few arguments I decided against using that
object again.

The -ascii option was added later in development when I realized that whatever console you use may or may not support
Unicode, so I wanted to include a workaround just in case.