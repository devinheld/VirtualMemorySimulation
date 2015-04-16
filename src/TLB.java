/**
 * @author Devin Held Student ID: 26883102
 */

// Class to represent the tlb
public class TLB {
	private int[] frame;
	private int[] lru;
	private int[] sp;

	// TLB initializer
	TLB() {

		// Initialize with -1 because the page isn't in memory yet
		this.sp = new int[] { -1, -1, -1, -1 };

		// lru is an integer from 0 to 3
		this.lru = new int[] { 0, 1, 2, 3 };

		// four frames
		this.frame = new int[4];
	}

	public int sp(int spVal) {
		for (int i = 0; i < Info.TLB_SIZE; i++) {

			// This means that the tlb had a hit
			if (sp[i] == spVal) {

				// Loop through all of the indexes in the lru to compare to the
				// i index
				for (int j = 0; j < Info.TLB_SIZE; j++) {

					// Reduce the lru index by one if the j index is greater
					// than the i index
					if (lru[j] > lru[i])
						lru[j] -= 1;
				}

				// Set the lru index to the max lru number (3)
				lru[i] = Info.MAX_LRU;

				// Return the sp val
				return frame[i];
			}
		}

		// If no hit, then there was an error (miss)
		return Info.ERROR;
	}

	// Update class arrays accordingly
	private void updateArrays(int i, int spVal, int physicalAddress) {
		// The sp index is now equal to the parameter, and the frame has
		// the physical address location
		sp[i] = spVal;
		frame[i] = physicalAddress;

		// Change the index of lru to the max lru (3)
		lru[i] = Info.MAX_LRU;
	}

	// Adjust the TLB accordingly
	public void adjust(int spVal, int physicalAddress) {
		for (int i = 0; i < Info.TLB_SIZE; i++) {
			if (lru[i] == 0) {
				// Updates the index in the class arrays
				updateArrays(i, spVal, physicalAddress);

				// All other lru values will be reduced by 1
				for (int j = 0; j < Info.TLB_SIZE; j++) {
					if (j != i)
						lru[j] -= 1;
				}
			}
		}
	}
}