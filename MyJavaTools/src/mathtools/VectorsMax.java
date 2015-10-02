package mathtools;

public class VectorsMax {
	
	private int n;
	private int[] M;
	private int[] curr;
	private int soFar;
	private int lastNonZero = 0;
	
	private final int num;
	
	public VectorsMax(int[] M){
		this.M = M;
		n = M.length;
		num = MapleTools.convertToMul(MapleTools.arraySum(M, MapleTools.constArray(n, 1)));
		
		boolean flag = true;
		for (int i=n-1; i>=0 && flag; i--){
			if (M[i] > 0){
				lastNonZero = i;
				flag = false;
			}
		}
	}
	
	/**
	 * @return true if there are more arrays left, false if we've got them all
	 */
	public boolean hasNext(){
		if (soFar < num)
			return true;
		else
			return false;
	}
	
	/**
	 * @return The next array in the sequence. The algorithm:
	 * 1. Find the index of the rightmost element that can 
	 * 		still be increased (using incrLoc()), call the index l
	 * 2. Let everything with index < l stay the same
	 * 		Increase entry with index = l 
	 * 		Let everything with index > l be zero
	 * 
	 * I believe this algorithm will generate the arrays in
	 * lexicographic order.
	 */
	public int[] next(){
		if (curr == null){
			curr = MapleTools.constArray(n, 0);
			soFar = 1;
			return curr;
		}else{
			int l = incrLoc();
			int[] nextOne = new int[n];
			for (int i=0; i<n; i++){
				if (i < l){
					nextOne[i] = curr[i];
				}else if (i == l){
					nextOne[i] = curr[i]+1;
				}else{
					nextOne[i] = 0;
				}
			}
			soFar++;
			curr = nextOne;
			return curr;
		}
	}
	
	/**
	 * @return the rightmost location in curr that can still be incremented
	 * For example, if M = {1,0,2,1} and curr = {0,0,1,1} then incrLoc() would
	 * return 2 because we have a 1 in position 2 which is less than its
	 * prescribed max of 2. Also in position 3 (the only thing farther right) we
	 * have a 1 which is already at its prescribed max so it can't be incremented.
	 */
	private int incrLoc(){
		for (int i=lastNonZero; i>-1; i--){
			if (curr[i] < M[i]){
				return i;
			}
		}
		return -1;
	}

}
