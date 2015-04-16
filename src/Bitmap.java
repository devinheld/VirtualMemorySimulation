/**
 * @author Devin Held ID: 26883102
 */

// Created from the provided slide description of BitMaps
public class Bitmap {
	private int[] bitmap;
	private int[] mask;

	// Initialize new bitmap
	Bitmap() {
		this.bitmap = new int[32];

		// Set everything initially to 0 to use with mask
		for (int i = 0; i <= 31; i++)
			this.bitmap[i] = 0;

		// Mask is an array of 32 ints. Very last one is 1 to create the
		// diagonal 1's
		mask = new int[32];
		mask[31] = 1;

		// Shift everything left in order to create the mask
		for (int i = 30; i >= 0; i--)
			mask[i] = mask[i + 1] << 1;
	}

	// Sets the position to 1
	public void fillPosition(int pos) {
		bitmap[pos / 32] = bitmap[pos / 32] | mask[pos % 32];
	}

	// Sets the position to 0
	public void deletePosition(int pos) {
		bitmap[pos / 32] = bitmap[pos / 32] & ~mask[pos % 32];
	}

	// Finds empty block to store info
	public int findEmptyBlock() {
		for (int i = 0; i < 32; i++) {
			for (int j = 0; j < 32; j++) {

				// If the bitmap is equal to 0 then we have found the first
				// empty block
				if ((bitmap[i] & mask[j]) == 0)
					return i * 32 + j;
			}
		}

		// If it reaches here, then no empty locations have been found
		return Info.ERROR;
	}
}