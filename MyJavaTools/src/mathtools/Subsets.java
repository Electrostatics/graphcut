package mathtools;

import generaltools.ArrayTools;

public class Subsets {
	
	private int n;
	private int currNum;
	
	/**
	 * This is like an iterator object that runs through 
	 * subsets of a set of size n. A subset is coded as 
	 * an array of {0,1}s of length n.
	 * Use hasNext() and next() to run through all the
	 * subsets.
	 * @param n The size of a set to run through the
	 * subsets of.
	 */
	public Subsets(int n){
		this.n = n;
		currNum = 0;
	}
	
	/**
	 * @return true if currNum isn't 2^n, false if it is
	 */
	public boolean hasNext(){
		if (currNum != Math.pow(2, n))
			return true;
		else
			return false;
		
	}
	
	/**
	 * @return The next "subset" (array of {0,1}s) in the sequence.
	 * This is just the binary version of currNum padded with zeros.
	 */
	public int[] next(){
		
		// change the number to a string of 0s and 1s, the binary representation of the number
		// then turn the string into a vector of 0s and 1s
		String currBin = Integer.toBinaryString(currNum); 
		int[] currBinVec = toVector(currBin);
		int numZeros = n-currBinVec.length;
		int[] zeroVec = new int[numZeros];
		int[] finalSubsetVec = ArrayTools.concat(zeroVec,currBinVec);
		
		currNum++;
		
		return finalSubsetVec;
		
	}
	
	/**
	 * Takes a string of integers and turns it into a vector of integers.
	 * This is used to turn a binary string (e.g., 010101110) into a 
	 * {0,1} array (e.g., {0,1,0,1,0,1,1,1,0}).
	 * @param s A String consisting only of integers
	 * @return An array of ints containing the ints in s
	 */
	private static int[] toVector(String s){
		int n = s.length();
		int[] vec = new int[n];
		for (int i = 0; i<n; i++){
			vec[i] = (int) Integer.parseInt(s.substring(i,i+1));
		}
		return vec;
	}
	
	
	
}
