/**
 * @author Devin Held Student ID: 26883102
 */

// Entry points into the memory and parsing input files
public class Main {
	public static void main(String[] args) {
		Shell shell;

		// If no file was specified in the run configurations, then open the
		// file input1.txt and input2.txt
		if (args.length == 0) {
			shell = new Shell(Info.INPUT_ONE, Info.INPUT_TWO, Info.TLB_ON);
		} else {
			// Otherwise use the specified input file
			shell = new Shell(args[0], args[1], Info.TLB_ON);
		}

		// Parse both input files
		shell.parseFile();
		shell.parseFile2();
	}

}
