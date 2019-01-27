import java.util.ArrayList;

// Object which generates and automatically formats a generic table to match the given inputs
public class el_Table {
	// Column labels (first row)
	private String[] columnLabels;
	
	// Row values (the data)
	private ArrayList<String[]> rows;
	
	// Maximum width of the elements of each row
	private int[] widths;
	
	// Characters used for box drawing
	private char horizLine = '─';
	private char vertLine = '│';
	private char intersection = '┼';
	
	// Constructor
	public el_Table(String[] columnLabels) {
		this.columnLabels = columnLabels;
		// Initialize widths as the same length as the labels
		widths = new int[columnLabels.length];
		for(int i = 0; i < widths.length; i++) {
			widths[i] = columnLabels[i].length();
		}
		rows = new ArrayList<>();
	}
	// Constructor, selecting whether or not to use ASCII
	public el_Table(String[] columnLabels, boolean useAscii) {
		this(columnLabels);
		if(useAscii) {
			horizLine = '-';
			vertLine = '|';
			intersection = '+';
		}
	}
	
	// Add a new row and return true if successful
	public boolean addRow(String[] row) {
		// Check to make sure the row has the right length
		if(row.length != widths.length)
			return false;
		// Update widths
		for(int i = 0; i < row.length; i++) {
			widths[i] = Math.max(row[i].length(), widths[i]);
		}
		// Add the row
		rows.add(row);
		return true;
	}
	
	// Return the formatted table for printing
	public String toString() {
		// Set up the label row
		String s = rowToString(columnLabels);
		// Draw a horizontal line to separate the labels and the data
		s += repeat(horizLine, widths[0] + 2);
		for(int i = 1; i < widths.length; i++) {
			s += intersection + repeat(horizLine, widths[i] + 2);
		}
		s += "\n";
		// Now add the remaining rows
		for(String[] row : rows) {
			s += rowToString(row);
		}
		return s;
	}
	
	// Formats a particular row
	private String rowToString(String[] row) {
		String s = " " + pad(row[0], widths[0]) + " ";
		for(int i = 1; i < row.length; i++) {
			s += vertLine + " " + pad(row[i], widths[i]) + " ";
		}
		return s + "\n";
	}
	
	// Returns the character c repeated n times
	private String repeat(char c, int n) {
		String s = "";
		while(n > 0) {
			s += c;
			n--;
		}
		return s;
	}
	
	// Pads the given string with whitespace to be a given length
	private String pad(String s, int width) {
		int pad = width - s.length();
		int left = pad/2;
		return repeat(' ', left) + s + repeat(' ', pad - left);
	}
}
