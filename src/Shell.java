/**
 * @author Devin Held Student ID: 26883102
 */

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;

// Works as a middle man between main and the memory agent
public class Shell {
	private String inputfile1;
	private String inputfile2;
	private String out = "";
	private boolean tlbON;
	private TLB tlb = null;
	public Memory memory;

	// The shell needs two input files and whether or not to use tlb in order to
	// initialize
	Shell(String inputfile1, String inputfile2, boolean tlbON) {
		this.inputfile1 = inputfile1;
		this.inputfile2 = inputfile2;
		this.memory = new Memory();
		this.tlbON = tlbON;

		// Initialize TLB if the on signal is specified
		if (tlbON)
			this.tlb = new TLB();
	}

	// Calculate the integer array representing the spw values based on the
	// virtual address
	private static int[] spw(int virtualAddresses) {

		// Calculate necessary values
		// |s| = 9
		// |p| = 10
		// |w| = 9
		int s = (virtualAddresses / Info.S_MOD) % Info.MEMORY_SIZE, w = virtualAddresses
				% Info.MEMORY_SIZE, p = (virtualAddresses / Info.MEMORY_SIZE)
				% Info.MEMORY_LENGTH;

		// return the array containing s, p, w
		return new int[] { s, p, w };
	}

	private void initializeSegment(String[] commands) {
		// Set up the segment table based on all commands from the first input
		// file
		for (int i = 0; i < commands.length; i += 2)
			memory.segment(Integer.parseInt(commands[i]),
					Integer.parseInt(commands[i + 1]));
	}

	// Creates entries in the page table based on the inputs from the second
	// specified file
	private void initializePage(String[] inputs) {
		for (int i = 0; i < inputs.length; i += 3)
			memory.page(Integer.parseInt(inputs[i]),
					Integer.parseInt(inputs[i + 1]),
					Integer.parseInt(inputs[i + 2]));
	}

	public void parseFile() {
		try {
			// Open the initialization file
			FileReader file = new FileReader(inputfile1);
			BufferedReader reader = new BufferedReader(file);

			// Read the lines
			String[] commands = reader.readLine().split(" "), inputs = reader
					.readLine().split(" ");

			// Initialize segments and pages based on parsed info
			initializeSegment(commands);
			initializePage(inputs);

			// Close the reader
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Finish parsing the input file if tlb is off
	private void tlbOffParse2(int[] commands, int[] virtualAddresses) {
		for (int i = 0; i < commands.length; i++) {

			// Get the s, p, w values based on the virtual address
			int[] spw = spw(virtualAddresses[i]);

			// Read from the memory
			if (commands[i] == Info.CLEAR)
				out += memory.getDataInMemory(spw[0], spw[1], spw[2]) + " ";

			// Write to the memory
			else if (commands[i] == Info.WRITE)
				out += memory.writeDataInMemory(spw[0], spw[1], spw[2]) + " ";
		}
	}

	// Finish parsing the input file if on and a tlb miss
	private void tlbOnMiss(int[] commands, int[] spw, int sp, int i) {
		if (commands[i] == Info.CLEAR) {
			// Read from the memory
			String output = memory.getDataInMemory(spw[0], spw[1], spw[2]);

			// If there is no page fault or error from retrieving data
			if (output != Info.PF && output != Info.ERR) {
				out += Info.M + output + " ";

				// Add to the tlb
				tlb.adjust(sp, Integer.parseInt(output) - spw[2]);
			} else
				out += output + " ";
		} else if (commands[i] == Info.WRITE) {
			// Write to the memory
			String output = memory.writeDataInMemory(spw[0], spw[1], spw[2]);

			// If there is no page fault or error from writing data
			if (output != Info.PF && output != Info.ERR) {
				out += Info.M + output + " ";

				// Adjust the tlb
				tlb.adjust(sp, Integer.parseInt(output) - spw[2]);
			} else
				out += output + " ";
		}
	}

	// Finish parsing the input file if tlb is on
	private void tlbOnParse2(int[] commands, int[] virtualAddresses) {
		// For every command in the input file, find the spw based on the
		// virtual addresses and see whether the sp exists or not
		for (int i = 0; i < commands.length; i++) {
			int[] spw = spw(virtualAddresses[i]);
			int sp = spw[0] * (int) Math.pow(2, 10) + spw[1];

			// if we find sp in the tlb (tlb hit)
			if (tlb.sp(sp) != Info.ERROR)
				out += Info.H + Integer.toString(tlb.sp(sp) + spw[2]) + " ";

			// Otherwise, we cannot find sp. Therefore tlb miss.
			else
				tlbOnMiss(commands, spw, sp, i);
		}
	}

	// Begins the parsing of the second input file
	public void parseFile2() {
		try {
			// Open the second input file for parsing
			FileReader file = new FileReader(inputfile2);
			BufferedReader reader = new BufferedReader(file);

			// Get each individual command in a string array
			String[] commands = reader.readLine().split(" ");

			// Create the running commands and virtual address arrays
			int[] toRun = new int[commands.length / 2], virtualAddresses = new int[commands.length / 2];

			// For parsed item, populate the toRun array with commands and the
			// corresponding virtual addresses
			for (int i = 0; i < commands.length; i = i + 2) {
				toRun[i / 2] = Integer.parseInt(commands[i]);
				virtualAddresses[i / 2] = Integer.parseInt(commands[i + 1]);
			}

			// work the data differently depending on whether or not the tlb is
			// on
			if (!tlbON)
				tlbOffParse2(toRun, virtualAddresses);
			else
				tlbOnParse2(toRun, virtualAddresses);

			// Close the file reader and save the outputs to the output file
			reader.close();
			fileSave();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Saves file holding all outputs to disk
	private void fileSave() {
		try {
			// Create the print writer out of the output file, write the out
			// string and finally close the output reader
			PrintWriter outFile = new PrintWriter(Info.OUTPUT);
			outFile.write(out);
			outFile.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

}