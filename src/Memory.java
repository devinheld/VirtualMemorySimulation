/**
 * @author Devin Held Student ID: 26883102
 */

// Memory abstraction for the memory system
public class Memory {
	private int[][] memory;
	private Bitmap bitMap;

	Memory() {
		// The memory is of length 1024, and size 512 - represented by a nested
		// integer array
		this.memory = new int[Info.MEMORY_LENGTH][Info.MEMORY_SIZE];

		// The bitmap helps track which position are full
		this.bitMap = new Bitmap();
	}

	// Create a new segment with the segment index in the memory, and the memory
	// location
	public void segment(int seg, int location) {
		memory[0][seg] = location;

		// Fill the places in the bitmap
		if (location != Info.ERROR) {
			bitMap.fillPosition(location / Info.MEMORY_SIZE);
			bitMap.fillPosition(location / Info.MEMORY_SIZE + 1);
		}
	}

	// Create a new page given the page number, segment number, and location
	// (memory address)
	public void page(int page, int seg, int location) {

		// If the page is greater than the number of pages, subtract the memory
		// size from the page number
		if (page >= Info.MEMORY_SIZE)
			memory[memory[0][seg] / Info.MEMORY_SIZE + 1][page
					- Info.MEMORY_SIZE] = location;

		// Otherwise, set the location to the page index
		else
			memory[memory[0][seg] / Info.MEMORY_SIZE][page] = location;

		// Fill the location in the bitmap
		if (location != Info.ERROR)
			bitMap.fillPosition(location / Info.MEMORY_SIZE);
	}

	// Get data from the memory
	private String getData(int s, int p, int w) {
		int frame = memory[0][s] / Info.MEMORY_SIZE, page;

		// If p is greater than 512, subtract 512 from it to index
		if (p >= Info.MEMORY_SIZE)
			page = memory[frame + 1][p - Info.MEMORY_SIZE];

		// Otherwise, index p to get the correct page
		else
			page = memory[frame][p];

		// Return the appropriate string based on the value of page
		switch (page) {
		case Info.ERROR:
			return Info.PF;
		case Info.CLEAR:
			return Info.ERR;
		default:
			return Integer.toString(page + w);
		}
	}

	// Gets data from memory
	public String getDataInMemory(int s, int p, int w) {

		// Return the appropriate string based on the value of memory at
		// position s
		switch (memory[0][s]) {
		case Info.ERROR:
			return Info.PF;
		case Info.CLEAR:
			return Info.ERR;
		default:
			return getData(s, p, w);
		}
	}

	// Adjust memory
	private void adjustMem(int frame, int p) {

		// Find an empty block in the bitmap
		int emptyBlock = bitMap.findEmptyBlock();

		// If p is greater than 512, subtract 512 from the page number to set
		// the correct block to it
		if (p > Info.MEMORY_SIZE - 1) {
			memory[frame + 1][p - Info.MEMORY_SIZE] = emptyBlock
					* Info.MEMORY_SIZE;

			// Fill the bitmap position
			bitMap.fillPosition(emptyBlock);
		} else {
			// Otherwise, simply index the page and fill the bitmap
			memory[frame][p] = emptyBlock * Info.MEMORY_SIZE;
			bitMap.fillPosition(emptyBlock);
		}
	}

	// Read data from the memory
	private String readData(int s, int p, int w) {
		// Get the frame
		int frame = memory[0][s] / Info.MEMORY_SIZE, page;

		// If p is greater than 512, subtract 512 from the page number to set
		// the correct block to it
		if (p > Info.MEMORY_SIZE - 1)
			page = memory[frame + 1][p - Info.MEMORY_SIZE];

		// Otherwise, simply index with p to get the page
		else
			page = memory[frame][p];

		// Return appropriate string based on the value of p
		switch (page) {
		case Info.ERROR:
			return Info.PF;
		case Info.CLEAR:
			adjustMem(frame, p);
			return writeDataInMemory(s, p, w);
		default:
			return Integer.toString(page + w);
		}
	}

	// Assists in populating the bitmap
	private String populateBitMap(int s, int p, int w) {

		// Find the first empty block
		int emptyBlock = bitMap.findEmptyBlock();

		// Set the index s to the empty block time 512
		memory[0][s] = emptyBlock * Info.MEMORY_SIZE;

		// Fill the position where empty block and the subsequent block
		bitMap.fillPosition(emptyBlock);
		bitMap.fillPosition(emptyBlock + 1);

		// Return the string resulting from writing data in memory
		return writeDataInMemory(s, p, w);
	}

	// Writes data to memory
	public String writeDataInMemory(int s, int p, int w) {

		// Return the appropriate string based on the value in the memory at
		// position s
		switch (memory[0][s]) {
		case Info.ERROR:
			return Info.PF;
		case Info.CLEAR:
			return populateBitMap(s, p, w);
		default:
			return readData(s, p, w);
		}
	}
}