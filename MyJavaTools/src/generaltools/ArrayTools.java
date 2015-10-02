package generaltools;

public class ArrayTools {
	
	/**
	 * @param A An array of Strings
	 * @param B An array of Strings
	 * @return The array of Strings obtained by concatenating B onto the end of A
	 */
	public static String[] concat(String[] A, String[] B) {
		String[] C= new String[A.length+B.length];
		System.arraycopy(A, 0, C, 0, A.length);
		System.arraycopy(B, 0, C, A.length, B.length);

		return C;
	}
	
	/**
	 * @param A An array of ints
	 * @param B An array of ints
	 * @return The array of ints obtained by concatenating B onto the end of A
	 */
	public static int[] concat(int[] A, int[] B) {
		int[] C= new int[A.length+B.length];
		System.arraycopy(A, 0, C, 0, A.length);
		System.arraycopy(B, 0, C, A.length, B.length);

		return C;
	}

}
