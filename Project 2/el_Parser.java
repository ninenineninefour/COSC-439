import java.util.HashMap;

// Parsing utility. Uses a hashmap to look up the indices of the options, allowing for a program to check the
// arguments in any order.
public class el_Parser {
	private HashMap<String, Integer> opMap;
	private String[] args;
	private boolean caseSensitive = false;
	
	// Constructor, case sensitivity defaults to false
	public el_Parser(String[] args, String[] options) {
		this(args, options, false);
	}
	
	// Constructor for if you wish to have it case sensitive
	public el_Parser(String[] args, String[] options, boolean caseSensitive) {
		this.caseSensitive = caseSensitive;
		// This hashmap size should allow each entry to only have one or two entries on average
		opMap = new HashMap<String, Integer>(options.length);
		// Add every option to the hashmap, with an index of -1
		for(String option : options) {
			opMap.put(checkCase(option), -1);
		}
		// Now update the indices of every option in the arguments that is a valid option
		for(int i = 0; i < args.length; i++) {
			String arg = checkCase(args[i]);
			Integer index = opMap.get(arg);
			if(index != null) {
				// This parser can't handle duplicate options
				if(index != -1)
					throw new IllegalArgumentException();
				opMap.put(arg, i);
			}
		}
		this.args = args;
	}
	
	// Check if the given option exists in args
	public boolean hasOption(String option) {
		Integer index = opMap.get(checkCase(option));
		if(index == null || index == -1)
			return false;
		return true;
	}
	
	// Returns the index of the given option, or -1 if it is not in the args or isn't a valid option
	public int indexOf(String option) {
		if(!caseSensitive)
			option = option.toLowerCase();
		Integer index = opMap.get(option);
		if(index == null)
			return -1;
		return index;
	}
	
	// Get the index of the option after the given index. If there is none, returns the length of the argument array.
	public int nextOptionIndex(int index) {
		if(index < 0)
			return -1;
		int next = index + 1;
		while(next < args.length && !opMap.containsKey(checkCase(args[next]))) {
			next++;
		}
		return next;
	}
	
	// Helper method; creates a sublist from the starting index to the ending index
	private String[] getArgSublist(int index, int end) {
		if(index < 0 || end < 0 || index >= args.length || end > args.length || end <= index)
			return null;
		String[] sub = new String[end - index - 1];
		int j = 0;
		for(int i = index + 1; i < end; i++) {
			sub[j] = args[i];
			j++;
		}
		return sub;
	}
	
	// Returns an array of every argument of the given option
	public String[] argsOf(String option) {
		int index = indexOf(option);
		return getArgSublist(index, nextOptionIndex(index));
	}
	
	// Returns the arguments of the given options as the given classes. If the arguments don't match, returns null.
	@SuppressWarnings("rawtypes")
	public Object[] argsOfAsObjects(String option, Class[] classes) throws InvalidClassException {
		int index = indexOf(option);
		if((nextOptionIndex(index) - index - 1) != classes.length)
			return null;
		
		Object[] objArgs = new Object[classes.length];
		try {
			for(int i = 0; i < objArgs.length; i++) {
				index++;
				objArgs[i] = parseAsClass(args[index], classes[i]);
			}
		} catch(NumberFormatException e) {
			return null;
		}
		return objArgs;
	}
	
	// Similar to argsOfAsObjects, but for single arguments
	@SuppressWarnings("rawtypes")
	public Object argOfAsObject(String option, Class argClass) {
		int index = indexOf(option);
		if(nextOptionIndex(index) != index + 2)
			return null;
		try {
			return parseAsClass(args[index + 1], argClass);
		} catch (InvalidClassException e) {
			return null;
		}
	}
	
	// If case sensitivity is disabled, returns the given string in lowercase
	private String checkCase(String s) {
		if(caseSensitive)
			return s;
		return s.toLowerCase();
	}
	
	// Attempts to parse the given string as an instance of the given class
	// Only Strings or wrapper versions of primitive types are supported
	@SuppressWarnings("rawtypes")
	private Object parseAsClass(String s, Class c) throws InvalidClassException {
		// Class checks are roughly in order from most to least common (my own estimate)
		if(c == String.class)
			return s;
		if(c == Integer.class)
			return Integer.valueOf(s);
		if(c == Double.class)
			return Double.valueOf(s);
		if(c == Character.class && s != null && s.length() == 1)
			return s.charAt(0);
		if(c == Boolean.class)
			return Boolean.valueOf(s);
		if(c == Long.class)
			return Long.valueOf(s);
		if(c == Short.class)
			return Short.valueOf(s);
		if(c == Byte.class)
			return Byte.valueOf(s);
		if(c == Float.class)
			return Float.valueOf(s);
		throw new InvalidClassException(c);
	}
	
	// Exception thrown if the user gives a bad class
	@SuppressWarnings("rawtypes")
	public class InvalidClassException extends Exception {
		private static final long serialVersionUID = -2826234224225848567L;
		private Class c;
		
		public InvalidClassException(Class c) {
			this.c = c;
		}
		
		public Class invalidClass() {
			return c;
		}
		
		public String toString() {
			return "Class '" + c.getCanonicalName() + "' is not a parsable class";
		}
	}
}
