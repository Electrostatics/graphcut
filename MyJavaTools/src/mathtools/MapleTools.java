package mathtools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import generaltools.ArrayTools;

/**
 * This class contains methods that are inspired by Maple procedures. I often
 * find myself wishing I had some Maple procedures so I wrote them. 
 * 
 * Have:
 * convertToMul(array) 			<--> convert(List, `*`)
 * convertToAdd(array) 			<--> convert(List, `+`)
 * arraySum(array1, array2) 	<--> List + List
 * constArray(n, i) 			<--> [i$n]
 * member(value, array) 		<--> member(something, Set) or member(something, List)
 * subsets(integer) 			<--> vaguely equivalent to subsets in maple
 * max(2d array) 				<--> max
 * max(1d array) 				<--> max
 * min(2d array) 				<--> min
 * min(1d array) 				<--> min
 * convertToMulNonZero(array) 	<--> no Maple equivalent
 * arrays(int, int[]) 			<--> no Maple equivalent, but I've written something like it before
 * 
 * Wish list:
 * something like "seq"
 * 
 * @author hoga886
 *
 */
public class MapleTools {
	
	/**
	 * @param X A set
	 * @param Y A set
	 * @return true if the sets intersect, false otherwise 
	 */
	public static Set intersection(Set X, Set Y){
		
		Set intersect = new HashSet();
		for (Object x : X){
			for (Object y : Y){
				if (x.equals(y)){
					intersect.add(x);
				}
			}
		}
		
		return intersect;
		
	}
	
	/**
	 * @param X A set
	 * @param Y A set
	 * @return true if the sets intersect, false otherwise 
	 */
	public static boolean intersect(Set X, Set Y){
		
		for (Object x : X){
			for (Object y : Y){
				if (x.equals(y)){
					return true;
				}
			}
		}
		
		return false;
		
	}
	
	/**
	 * @param X A HashSet
	 * @param Y A Set
	 * @return true if the sets intersect, false otherwise
	 */
	public static boolean intersect(HashSet<Object> X, Set<Object> Y){
		
		for (Object x : X){
			for (Object y : Y){
				if (x.equals(y)){
					return true;
				}
			}
		}
		
		return false;
		
	}

	/**
	 * @param X An ArrayList
	 * @param Y A Set
	 * @return true if the sets intersect, false otherwise
	 */
	public static boolean intersect(ArrayList<Integer> X, Set<Integer> Y){
		
		if (X.size() == 0 || Y.size() == 0){
			return false;
		}
		
		for (Object x : X){
			for (Object y : Y){
				if (x.equals(y)){
					return true;
				}
			}
		}
		
		return false;
		
	}
	
	/**
	 * @param X A collection
	 * @param Y A collection
	 * @return The union of the two collections (as a HashSet)
	 */
	public static HashSet<?> union (Collection<?> X, Collection<?> Y){
		HashSet<Object> union = new HashSet<Object>();
		
		for (Object x : X){
			union.add(x);
		}
		for (Object y : Y){
			union.add(y);
		}
		
		return union;
	}
	
	public static HashSet<?> setminus(Collection<?> X, Collection<?> Y){
		HashSet<Object> minus = new HashSet<Object>();
		
		for (Object x : X){
			if (!Y.contains(x)){
				minus.add(x);
			}
		}
		
		return minus;
	}
	
	
//	public static double[] squish(double[] L){
//		double[] S = new double[L.length];
//		double[] Lsort = L.clone();
//		Arrays.sort(Lsort);
//				
//		return S;
//	}
	
	/**
	 * @param start A starting value
	 * @param end An ending value
	 * @param step A step size
	 * @return An array of doubles starting with start, ending with end,
	 * and increasing by step each time. E.g., seq(1, 5, 0.25) would
	 * return the array
	 * [1, 1.25, 1.5, 1.75, 2, 2.25, ..., 5]
	 */
	public static double[] seq(double start, double end, double step){
		int length = (int) Math.ceil((end-start)/step)+1;
		double[] ret = new double[length];
		for (int i=0; i<length; i++){
			ret[i] = start+i*step;
		}
		return ret;
	}
	
	/**
	 * @param first An array of integers
	 * @param second An array of integers
	 * @return The set minus operation of first minus second 
	 * as an array of integers
	 */
	public static int[] minus(int[] first, int[] second){
		List<Integer> firstList = new ArrayList<Integer>(first.length);
		for (int n : first)
		  firstList.add(n);
		
		List<Integer> secondList = new ArrayList<Integer>(second.length);
		for (int n : second)
		  secondList.add(n);
		
		ArrayList<Integer> result = new ArrayList<Integer>();
		
		for (Integer val : firstList){
			if (!secondList.contains(val))
				result.add(val);
		}
		
		int[] ret = new int[result.size()];
		for (int i=0; i<result.size(); i++){
			ret[i] = result.get(i);
		}
		
		return ret;
		
	}
	
	/**
	 * @param n An integer
	 * @param numParts The number of parts to be in a random set partition
	 * @return A random set partition of {1, 2, ..., n} into numParts sets.
	 */
	public static Vector<Vector<Integer>> randomSetPart(int n, int numParts){
		Vector<Vector<Integer>> parts = new Vector<Vector<Integer>>(numParts);
		
		//make a Vector containing numParts empty Vector<Integer>s
		for (int p=0; p<numParts; p++){
			Vector<Integer> vp = new Vector<Integer>();
			parts.add(vp);
		}
		
		for (int i=0; i<n; i++){
			// pick a random vector from parts and add i to that vector
			Vector<Integer> temp = parts.get((int) Math.floor(numParts*Math.random()));
			temp.add(i);
		}
		
		
		return parts;
	}
	
	/**
	 * @param L An array of ints
	 * @return The average of the entries in L
	 */
	public static double avg(int[] L){
		int sum = MapleTools.convertToAdd(L);
		return ((double) sum)/L.length;
	}
	
	/**
	 * @param L An array of doubles
	 * @return The average of the entries in L
	 */
	public static double avg(double[] L){
		double sum = MapleTools.convertToAdd(L); //System.out.println(sum);
		return ((double) sum)/L.length;
	}
	
	/**
	 * @param L An array of floats
	 * @return The average of the entries in L
	 */
	public static double avg(float[] L){
		float sum = MapleTools.convertToAdd(L);
		return ((double) sum)/L.length;
	}
	
	/**
	 * @param L An array of doubles
	 * @return The median of the entries in L
	 */
	public static double median(double[] L){
		//System.out.println(Arrays.toString(L));
		Arrays.sort(L);
		//System.out.println(Arrays.toString(L));
		if (L.length % 2 == 0){
			int leftEnd = L.length/2-1; //System.out.print(leftEnd+" ");
			int rightEnd = leftEnd+1;   //System.out.println(rightEnd);
			double left = L[leftEnd];
			double right = L[rightEnd];
			return (left+right)/2;
		}else{
			int mid = (L.length-1)/2;   //System.out.println(mid);
			return L[mid];
		}
	}
	
	/**
	 * @param val An int
	 * @param vec An array of ints
	 * @return The locations within vec in which val shows up
	 */
	public static int[] select(int val, int[] vec){
		ArrayList<Integer> Ret = new ArrayList<Integer>();
		
		int pos = 0;
		// for each entry in vec, check if it equals val
		// if it does, we add the index of the entry in vec
		// to the position in Ret equal to how many vals 
		// we've seen so far
		// e.g., if val = 5 and vec = [1,2,5,4,5,3,2] then
		// Ret = [2, 4] since the 5s are in spot 2 and 4 in vec
		for (int i=0; i<vec.length; i++){
			if (vec[i] == val){
				Ret.add(pos, i);
				pos++;
			}
		}
		
		// convert Ret into an int[]
		int n = Ret.size();
		int[] ret = new int[n];
		for (int j=0; j<n; j++){
			ret[j] = Ret.get(j);
		}
		
		return ret;
		
	}
	
	
	/**
	 * @param arr An array of double values.
	 * @return The location of the first positive value in the array
	 * or the length of the array if the array is all
	 * negative.
	 */
	public static int findMiddle(double[] arr){
		int mid = -1;
		boolean negative = true;
		for (int i=0; i<arr.length && negative; i++){
			if (arr[i]>0){
				negative = false;
				mid = i;
			}
		}
		if (mid<0){
			mid = arr.length;
		}
		return mid;
	}
	
	public static double[] padZeros(double[] arr, int mid){
		int arrMid = findMiddle(arr); //System.out.println(arrMid);
		int diff = mid - arrMid; //System.out.println(diff);
		double[] paddedArr = new double[arr.length+diff];
		for (int i=0; i<paddedArr.length; i++){
			if (i<diff){
				paddedArr[i] = 0;
			}else{
				paddedArr[i] = arr[i-diff];
			}
		}
		return paddedArr;
	}
	
	/**
	 * @param H A HashMap mapping Integers to Doubles 
	 * @return the Integer corresponding to the largest Double in H
	 */
	public static Integer getBiggest(HashMap<Integer,Double> H){
		Double currMaxVal = Double.NEGATIVE_INFINITY;
		Integer currMaxKey = -1;
		for (Integer key: H.keySet()){
			Double val = H.get(key);
			if (val > currMaxVal){
				currMaxKey = key;
				currMaxVal = val;
			}
		}
		return currMaxKey;
	}
	
	/**
	 * @param H A HashMap mapping Integers to Doubles 
	 * @return the Integer corresponding to the largest Double in H
	 */
	public static Integer getSmallest(HashMap<Integer,Double> H){
		Double currMaxVal = Double.POSITIVE_INFINITY;
		Integer currMaxKey = -1;
		for (Integer key: H.keySet()){
			Double val = H.get(key);
			if (val < currMaxVal){
				currMaxKey = key;
				currMaxVal = val;
			}
		}
		return currMaxKey;
	}
	
	/**
	 * @param H A HashMap mapping Integers to Integers 
	 * @return the Integer corresponding to the largest Integer in H
	 */
	public static Integer getBiggestInt(HashMap<Integer,Integer> H){
		Integer currMaxVal = Integer.MIN_VALUE;
		Integer currMaxKey = -1;
		for (Integer key: H.keySet()){
			Integer val = H.get(key);
			if (val > currMaxVal){
				currMaxKey = key;
				currMaxVal = val;
			}
		}
		return currMaxKey;
	}
	
	/**
	 * @param H A HashMap mapping Integers to Integers 
	 * @return the Integer corresponding to the largest Integer in H
	 */
	public static Integer getSmallestInt(HashMap<Integer,Integer> H){
		Integer currMaxVal = Integer.MAX_VALUE;
		Integer currMaxKey = -1;
		for (Integer key: H.keySet()){
			Integer val = H.get(key);
			if (val < currMaxVal){
				currMaxKey = key;
				currMaxVal = val;
			}
		}
		return currMaxKey;
	}
	
	/**
	 * @param ar an array of ints
	 * @return The product of elements in ar
	 */
	public static int convertToMul(int[] ar){
		int prod = 1;
		for (int i=0; i<ar.length; i++){
			prod *= ar[i];
		}
		return prod;
	}
	
	/**
	 * @param ar an array of ints
	 * @return The product of nonzero elements in ar
	 */
	public static int convertToMulNonZero(int[] ar){
		int prod = 1;
		for (int i=0; i<ar.length; i++){
			if (ar[i] != 0)
				prod *= ar[i];
		}
		return prod;
	}
	
	/**
	 * @param ar an array of ints
	 * @return The sum of elements in ar
	 */
	public static int convertToAdd(int[] ar){
		int sum = 0;
		for (int i=0; i<ar.length; i++){
			sum += ar[i];
		}
		return sum;
	}
	
	/**
	 * @param ar an array of doubles
	 * @return The product of elements in ar
	 */
	public static double convertToMul(double[] ar){
		int prod = 1;
		for (int i=0; i<ar.length; i++){
			prod *= ar[i];
		}
		return prod;
	}
	
	/**
	 * @param ar an array of doubles
	 * @return The product of nonzero elements in ar
	 */
	public static double convertToMulNonZero(double[] ar){
		int prod = 1;
		for (int i=0; i<ar.length; i++){
			if (ar[i] != 0)
				prod *= ar[i];
		}
		return prod;
	}
	
	/**
	 * @param ar an array of doubles
	 * @return The sum of elements in ar
	 */
	public static double convertToAdd(double[] ar){
		double sum = 0;
		for (int i=0; i<ar.length; i++){
			sum += ar[i];
		}
		return sum;
	}
	
	/**
	 * @param ar an array of floats
	 * @return The product of elements in ar
	 */
	public static float convertToMul(float[] ar){
		int prod = 1;
		for (int i=0; i<ar.length; i++){
			prod *= ar[i];
		}
		return prod;
	}
	
	/**
	 * @param ar an array of floats
	 * @return The product of nonzero elements in ar
	 */
	public static float convertToMulNonZero(float[] ar){
		int prod = 1;
		for (int i=0; i<ar.length; i++){
			if (ar[i] != 0)
				prod *= ar[i];
		}
		return prod;
	}
	
	/**
	 * @param ar an array of floats
	 * @return The sum of elements in ar
	 */
	public static float convertToAdd(float[] ar){
		int sum = 0;
		for (int i=0; i<ar.length; i++){
			sum += ar[i];
		}
		return sum;
	}
	
	/**
	 * @param ar an array of floats
	 * @return The product of elements in ar
	 */
	public static long convertToMul(long[] ar){
		int prod = 1;
		for (int i=0; i<ar.length; i++){
			prod *= ar[i];
		}
		return prod;
	}
	
	/**
	 * @param ar an array of floats
	 * @return The product of nonzero elements in ar
	 */
	public static long convertToMulNonZero(long[] ar){
		int prod = 1;
		for (int i=0; i<ar.length; i++){
			if (ar[i] != 0)
				prod *= ar[i];
		}
		return prod;
	}
	
	/**
	 * @param ar an array of longs
	 * @return The sum of elements in ar
	 */
	public static long convertToAdd(long[] ar){
		int sum = 0;
		for (int i=0; i<ar.length; i++){
			sum += ar[i];
		}
		return sum;
	}
	
	/**
	 * @param first
	 * @param second
	 * @return The component-wise sum of arrays first and second
	 */
	public static int[] arraySum(int[] first, int[] second){
		if (first.length != second.length){
			System.out.println("when adding two arrays they must have the same size");
			return null;
		}
		int[] sum = new int[first.length];
		for (int i=0; i<first.length; i++){
			sum[i] = first[i] + second[i];
		}
		return sum;
	}
	
	/**
	 * @param first
	 * @param second
	 * @return The component-wise sum of arrays first and second
	 */
	public static double[] arraySum(double[] first, double[] second){
		if (first.length != second.length){
			System.out.println("when adding two arrays they must have the same size");
			return null;
		}
		double[] sum = new double[first.length];
		for (int i=0; i<first.length; i++){
			sum[i] = first[i] + second[i];
		}
		return sum;
	}
	
	/**
	 * @param first
	 * @param second
	 * @return The component-wise sum of arrays first and second
	 */
	public static float[] arraySum(float[] first, float[] second){
		if (first.length != second.length){
			System.out.println("when adding two arrays they must have the same size");
			return null;
		}
		float[] sum = new float[first.length];
		for (int i=0; i<first.length; i++){
			sum[i] = first[i] + second[i];
		}
		return sum;
	}
	
	/**
	 * @param first
	 * @param second
	 * @return The component-wise sum of arrays first and second
	 */
	public static long[] arraySum(long[] first, long[] second){
		if (first.length != second.length){
			System.out.println("when adding two arrays they must have the same size");
			return null;
		}
		long[] sum = new long[first.length];
		for (int i=0; i<first.length; i++){
			sum[i] = first[i] + second[i];
		}
		return sum;
	}
	
	/**
	 * @param n length of array
	 * @param i constant for the array
	 * @return An array of length n consisting only of the value i
	 */
	public static int[] constArray(int n, int i){
		int[] ar = new int[n];
		for (int j=0; j<n; j++){
			ar[j] = i;
		}
		return ar;
	}
	
	/**
	 * @param n length of array
	 * @param i constant for the array
	 * @return An array of length n consisting only of the value i
	 */
	public static double[] constArray(int n, double i){
		double[] ar = new double[n];
		for (int j=0; j<n; j++){
			ar[j] = i;
		}
		return ar;
	}
	
	/**
	 * @param n length of array
	 * @param i constant for the array
	 * @return An array of length n consisting only of the value i
	 */
	public static float[] constArray(int n, float i){
		float[] ar = new float[n];
		for (int j=0; j<n; j++){
			ar[j] = i;
		}
		return ar;
	}
	
	/**
	 * @param n length of array
	 * @param i constant for the array
	 * @return An array of length n consisting only of the value i
	 */
	public static long[] constArray(int n, long i){
		long[] ar = new long[n];
		for (int j=0; j<n; j++){
			ar[j] = i;
		}
		return ar;
	}
	
	
	/**
	 * @param n length of arrays to be returned
	 * @param maxVals array of maximum values, one for each location in the array
	 * @return 2D array of ints, each inner array has length n and
	 * the element in the i^{th} spot is not larger than maxVals[i]
	 */
	public static int[][] arrays(int n, int[] maxVals){
		Vector<Vector<Integer>> vecs = vectors(n, maxVals);
		int[] numEntriesAr = arraySum(maxVals, constArray(n,1));
		int numArrays = convertToMulNonZero(numEntriesAr);
		int[][] ret = new int[numArrays][n];
		int i=0;
		for (Vector<Integer> vec : vecs){
			int[] anArray = new int[n];
			for (int j=0; j<vec.size(); j++){
				anArray[j] = vec.get(j);
			}
			ret[i] = anArray;
			i++;
		}
		//System.out.println(Arrays.toString(ret));
		
		return ret;
	}
	
	/**
	 * @param n length of vectors to be returned
	 * @param maxVals array of maximum values, one for each location in the vector
	 * @return Vector of Vectors of Integers, each Vector has length n and
	 * the element in the i^{th} spot is not larger than maxVals[i]
	 */
	public static Vector<Vector<Integer>> vectors(int n, int[] maxVals){
		//System.out.println("n="+n+", maxVals="+Arrays.toString(maxVals));
		Vector<Vector<Integer>> Ret = new Vector<Vector<Integer>>();
		if (maxVals.length != n){
			System.out.println("Input maxVals to vectors must have length n = "+n+" but it has length "+maxVals.length);
			return null;
		}
		// else isn't needed because there is a return statement inside "if"
		// we'll only get here if maxVals.length == n
		
		if (n==1){
			for (int i=0; i<=maxVals[0]; i++){
				Vector<Integer> foo = new Vector<Integer>(1);
				foo.add(i);
				Ret.add(foo);
			}
			return Ret;
		}
		int[] maxValsShorter = new int[n-1];
		maxValsShorter = Arrays.copyOfRange(maxVals, 0, n-1);
		Vector<Vector<Integer>> old = vectors(n-1, maxValsShorter);
		for (Vector<Integer> o : old){
			for (int j=0; j<=maxVals[n-1]; j++){
				@SuppressWarnings("unchecked")
				Vector<Integer> oj = (Vector<Integer>) o.clone(); 
				oj.add(n-1, j); 
				Ret.add(oj);
			}
		}
		//System.out.println(n+" "+Ret);
		return Ret;
	}
	
	/**
	 * @param n The length of the arrays that will be returned
	 * @param twoLocs List of locations which will have 0, 1, 2 as possibilities
	 * @param oneLocs List of locations which will have 0, 1 as possibilities
	 * @return 2D array of 0s, 1s, and 2s. Each location in the
	 * array has a specified maximum (if not in twoLocs or oneLocs the max is 0)
	 */
	public static int[][] arrays012(int n, int[] twoLocs1, int[] oneLocs1){
		int[] maxVals = new int[n];
		int[] twoLocs; int[] oneLocs;
		if (twoLocs1==null){
			twoLocs = new int[0]; 
		}else
			twoLocs = twoLocs1;
		if (oneLocs1 == null){
			oneLocs = new int[0];
		}else{
			oneLocs = oneLocs1;
		}
		for (int i=0; i<n; i++){
			if (member(i, twoLocs)){
				maxVals[i] = 2;
			}else if (member(i, oneLocs)){
				maxVals[i] = 1;
			}else{
				maxVals[i] = 0;
			}
		}
		
		return arrays(n, maxVals);
		
		/*Vector<Vector<Integer>> vecs012 = vectors012(n,twoLocs, oneLocs);
		int[][] ret;
		if (twoLocs == null && oneLocs == null){
			ret = new int[1][n];
		}else if (twoLocs == null && oneLocs != null){
			ret = new int[(int) Math.pow(2, oneLocs.length)][n];
		}else if (twoLocs != null && oneLocs == null){
			ret = new int[(int) Math.pow(3, twoLocs.length)][n];
		}else{
			ret = new int[(int) (Math.pow(2, oneLocs.length)*Math.pow(3, twoLocs.length))][n];
		}
		
		// = new int[(int) (Math.pow(2, oneLocs.length)*Math.pow(3, twoLocs.length))][n];
		for (int i=0; i<vecs012.size(); i++){
			int[] anArray = new int[n];
			Vector<Integer> vec012 = vecs012.get(i);
			for (int j=0; j<vec012.size(); j++){
				anArray[j] = vec012.get(j);
			}
			ret[i] = anArray;
		}
		
		return ret;*/
	}
	
	/**
	 * @param n The length of the vectors that will be returned
	 * @param TL List of locations which will have 0, 1, 2 as possibilities
	 * @param OL List of locations which will have 0, 1 as possibilities
	 * @return Vector of Vectors of 0s, 1s, and 2s. Each location in the
	 * vector has a specified maximum (if not in TL or OL the max is 0)
	 */
	@SuppressWarnings("unchecked")
	public static Vector<Vector<Integer>> vectors012(int n, int[] TL, int[] OL){
		
		Vector<Vector<Integer>> Ret = new Vector<Vector<Integer>>();
		int[] twoLocs;
		int[] oneLocs;
		if (TL == null){
			twoLocs = new int[1]; twoLocs[0] = -1;
		}else{
			twoLocs = TL;
		}
		
		if (OL == null){
			oneLocs = new int[1]; oneLocs[0] = -1;
		}else{
			oneLocs = OL;
		}
		
		if (n == 1){
			if (twoLocs[0] == 0){
				Vector<Integer> zero = new Vector<Integer>();
				zero.add(new Integer(0));
				Vector<Integer> one = new Vector<Integer>();
				one.add(new Integer(1));
				Vector<Integer> two = new Vector<Integer>();
				two.add(new Integer(2));
				Ret.add(zero); Ret.add(one); Ret.add(two);
				//System.out.println(n + " " + Ret);
				return Ret;
			}else if (oneLocs[0] == 0){
				Vector<Integer> zero = new Vector<Integer>();
				zero.add(new Integer(0));
				Vector<Integer> one = new Vector<Integer>();
				one.add(new Integer(1));
				Ret.add(zero); Ret.add(one);
				//System.out.println(n + " " + Ret);
				return Ret;
			}else{
				Vector<Integer> one = new Vector<Integer>();
				one.add(new Integer(1));
				Ret.add(one);
				return Ret;
			}
		}
		
		Vector<Vector<Integer>> old = vectors012(n-1,twoLocs, oneLocs);
		for (int i=0; i<old.size(); i++){
			Vector<Integer> thisOne = old.get(i);
			
			if (member(n-1,oneLocs)){
				Vector<Integer> thisOne0 = (Vector<Integer>) thisOne.clone(); thisOne0.add(n-1, 0); Ret.add(thisOne0);
				Vector<Integer> thisOne1 = (Vector<Integer>) thisOne.clone(); thisOne1.add(n-1, 1); Ret.add(thisOne1);
			}
			else if (member(n-1,twoLocs)){
				Vector<Integer> thisOne0 = (Vector<Integer>) thisOne.clone(); thisOne0.add(n-1, 0); Ret.add(thisOne0);
				Vector<Integer> thisOne1 = (Vector<Integer>) thisOne.clone(); thisOne1.add(n-1, 1); Ret.add(thisOne1);
				Vector<Integer> thisOne2 = (Vector<Integer>) thisOne.clone(); thisOne2.add(n-1, 2); Ret.add(thisOne2);
			}
			else{
				Vector<Integer> thisOne1 = (Vector<Integer>) thisOne.clone(); thisOne1.add(n-1, 1); Ret.add(thisOne1);
				
			}
		}
		//System.out.println(n + " " + Ret);
		return Ret;		
		
	}
	
	/**
	 * @param i int to look for in ar
	 * @param ar Array of ints
	 * @return true if i is in ar, false otherwise
	 */
	public static boolean member(int i, int[] ar){
		for (int j=0; j<ar.length; j++){
			if (ar[j] == i)
				return true;
		}
		
		return false;
	}
	
	/**
	 * @param i double to look for in ar
	 * @param ar Array of doubles
	 * @return true if i is in ar, false otherwise
	 */
	public static boolean member(double i, double[] ar){
		for (int j=0; j<ar.length; j++){
			if (ar[j] == i)
				return true;
		}
		
		return false;
	}
	
	/**
	 * @param i long to look for in ar
	 * @param ar Array of longs
	 * @return true if i is in ar, false otherwise
	 */
	public static boolean member(long i, long[] ar){
		for (int j=0; j<ar.length; j++){
			if (ar[j] == i)
				return true;
		}
		
		return false;
	}
	
	/**
	 * @param i float to look for in ar
	 * @param ar Array of floats
	 * @return true if i is in ar, false otherwise
	 */
	public static boolean member(float i, float[] ar){
		for (int j=0; j<ar.length; j++){
			if (ar[j] == i)
				return true;
		}
		
		return false;
	}
	
	/**
	 * @param i String to look for in ar
	 * @param ar Array of Strings
	 * @return true if i is in ar, false otherwise
	 */
	public static boolean member(String i, String[] ar){
		for (int j=0; j<ar.length; j++){
			if (ar[j].equals(i))
				return true;
		}
		
		return false;
	}
	
	/**
	 * @param n an integer
	 * @return A double array of ints. Each inner array will encode a subset of [n]
	 */
	public static int[][] subsets(int n){
		
		int[][] Ret = new int[(int) Math.pow(2, n)][n];
		
		for (int i=0; i<Math.pow(2,n); i++){
			String iBin = Integer.toBinaryString(i);
			int[] iBinVec = toVector(iBin);
			int numZeros = n-iBinVec.length;
			int[] zeroVec = new int[numZeros];
			int[] finalSubsetVec = ArrayTools.concat(zeroVec,iBinVec);
			Ret[i] = finalSubsetVec;
		}
		
		return Ret;
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
	
	@SuppressWarnings("unchecked")
	public static HashMap<Integer, Integer> scrunch(HashMap<Integer, Integer> H){
		
		Collection<Integer> keysH = H.keySet();
		Collection<Integer> valsH = H.values();
		
		HashMap<Integer, Integer> copyH = (HashMap<Integer, Integer>) H.clone();
		
		if (max(valsH) == valsH.size()-1){
			return copyH;
		}else{
			HashMap<Integer, Integer> reverse = new HashMap<Integer, Integer>();
			for (Integer k : keysH){
				Integer v = H.get(k);
				reverse.put(v, k);
			}
			Integer mexValsH = mex(valsH);
			Integer nextVal = minAbove(valsH,mexValsH);
			
			Integer nextKey = reverse.get(nextVal);
			
			copyH.put(nextKey, mexValsH);

			return scrunch(copyH);
		}
	}
	
	/**
	 * @param C A collection of Integers
	 * @param k An integer
	 * @return The smallest value in C that is larger than k
	 */
	public static Integer minAbove(Collection<Integer> C, Integer k){
		
		Iterator<Integer> it = C.iterator();
		Integer min = Integer.MAX_VALUE;
		while (it.hasNext()){
			Integer next = it.next();
			if (next < min && next > k){
				min = next;
			}
		}
		
		return min;
	}
	
	/**
	 * @param C A Collection of Integers
	 * @return The "mex" of C. The mex is the minimal excluded value greater than zero.
	 * So if C = {0,1,3,4,5,8} then mex(C) = 2. If C = {1,100} then mex(C) = 0.
	 */
	public static Integer mex(Collection<Integer> C){
		for (int i=0; i <= max(C); i++){
			if (!C.contains(i)){
				return i;
			}
		}
		
		return max(C)+1;
		
	}
	
	/**
	 * @param C A Collection of Integers
	 * @return the maximum value in the collection
	 */
	public static Integer max(Collection<Integer> C){
		if (C.size() == 0){
			return -1;
		}
		Iterator<Integer> it = C.iterator();
		Integer maxNow = Integer.MIN_VALUE;
		while (it.hasNext()){
			Integer next = it.next();
			if (next > maxNow){
				maxNow = next;
			}
		}
		
		return maxNow;
	}
	
	/**
	 * @param L a 2D array of doubles
	 * @return The maximum element in the 2D array
	 */
	public static double max(double[][] L){
		int m = L.length;
		double mx = (double) Double.NEGATIVE_INFINITY;
		for (int i=0; i<m; i++){
			double curr = max(L[i]);
			if (curr > mx){
				mx = curr;
			}
		}		
		
		return mx;
	}
	
	/**
	 * @param L an array of doubles
	 * @return The maximum element in the array
	 */
	public static double max(double[] L){
		int n = L.length;
		double mx = (double) Double.NEGATIVE_INFINITY;
		for (int i=0; i<n; i++){
			if (L[i] > mx){
				mx = L[i];
			}
		}
		return mx;
	}
	
	/**
	 * @param L a 2D array of ints
	 * @return The maximum element in the 2D array
	 */
	public static int max(int[][] L){
		int m = L.length;
		int mx = (int) Integer.MIN_VALUE;
		for (int i=0; i<m; i++){
			int curr = max(L[i]);
			if (curr > mx){
				mx = curr;
			}
		}		
		
		return mx;
	}
	
	/**
	 * @param L an array of ints
	 * @return The maximum element in the array
	 */
	public static int max(int[] L){
		int n = L.length;
		int mx = (int) Integer.MIN_VALUE;
		for (int i=0; i<n; i++){
			if (L[i] > mx){
				mx = L[i];
			}
		}
		return mx;
	}

	/**
	 * @param L a 2D array of floats
	 * @return The maximum element in the 2D array
	 */
	public static float max(float[][] L){
		int m = L.length;
		float mx = (float) Float.NEGATIVE_INFINITY;
		for (int i=0; i<m; i++){
			float curr = max(L[i]);
			if (curr > mx){
				mx = curr;
			}
		}		
		
		return mx;
	}
	
	/**
	 * @param L an array of floats
	 * @return The maximum element in the array
	 */
	public static float max(float[] L){
		int n = L.length;
		float mx = (float) Float.NEGATIVE_INFINITY;
		for (int i=0; i<n; i++){
			if (L[i] > mx){
				mx = L[i];
			}
		}
		return mx;
	}
	
	/**
	 * @param L a 2D array of floats
	 * @return The maximum element in the 2D array
	 */
	public static long max(long[][] L){
		int m = L.length;
		long mx = (long) Long.MIN_VALUE;
		for (int i=0; i<m; i++){
			long curr = max(L[i]);
			if (curr > mx){
				mx = curr;
			}
		}		
		
		return mx;
	}
	
	/**
	 * @param L an array of floats
	 * @return The maximum element in the array
	 */
	public static long max(long[] L){
		int n = L.length;
		long mx = (long) Float.MIN_VALUE;
		for (int i=0; i<n; i++){
			if (L[i] > mx){
				mx = L[i];
			}
		}
		return mx;
	}
	
//	public static Double max(HashSet<Double> V){
//		Double mx = Double.MIN_VALUE;
//		for (Double d : V){
//			if (d > mx){
//				mx = d;
//			}
//		}
//		return mx;
//	}
	
	/**
	 * @param V A HashSet of Numbers (Integer, Double, etc.)
	 * @return The max element in the set.
	 */
	public static Number max(HashSet<Number> V){
		Double mx = Double.MIN_VALUE;
		for (Number d : V){
			if (d.doubleValue() > mx){
				mx = d.doubleValue();
			}
		}
		return mx;
	}

	
	/**
	 * @param L a 2D array of doubles
	 * @return The minimum element in the 2D array
	 */
	public static double min(double[][] L){
		int m = L.length;
		double mn = (double) Double.POSITIVE_INFINITY;
		for (int i=0; i<m; i++){
			double curr = min(L[i]);
			if (curr < mn){
				mn = curr;
			}
		}		
		
		return mn;
	}
	
	/**
	 * @param L an array of doubles
	 * @return The minimum element in the array
	 */
	public static double min(double[] L){
		int n = L.length;
		double mn = (double) Double.POSITIVE_INFINITY;
		for (int i=0; i<n; i++){
			if (L[i] < mn){
				mn = L[i];
			}
		}
		return mn;
	}
	
	/**
	 * @param L a 2D array of ints
	 * @return The minimum element in the 2D array
	 */
	public static int min(int[][] L){
		int m = L.length;
		int mn = (int) Integer.MAX_VALUE;
		for (int i=0; i<m; i++){
			int curr = min(L[i]);
			if (curr < mn){
				mn = curr;
			}
		}		
		
		return mn;
	}
	
	/**
	 * @param L an array of ints
	 * @return The minimum element in the array
	 */
	public static int min(int[] L){
		int n = L.length;
		int mn = (int) Integer.MAX_VALUE;
		for (int i=0; i<n; i++){
			if (L[i] < mn){
				mn = L[i];
			}
		}
		return mn;
	}

	/**
	 * @param L a 2D array of floats
	 * @return The minimum element in the 2D array
	 */
	public static float min(float[][] L){
		int m = L.length;
		float mn = (float) Float.POSITIVE_INFINITY;
		for (int i=0; i<m; i++){
			float curr = min(L[i]);
			if (curr < mn){
				mn = curr;
			}
		}		
		
		return mn;
	}
	
	/**
	 * @param L an array of floats
	 * @return The minimum element in the array
	 */
	public static float min(float[] L){
		int n = L.length;
		float mn = (float) Float.POSITIVE_INFINITY;
		for (int i=0; i<n; i++){
			if (L[i] > mn){
				mn = L[i];
			}
		}
		return mn;
	}
	
	/**
	 * @param L a 2D array of floats
	 * @return The minimum element in the 2D array
	 */
	public static long min(long[][] L){
		int m = L.length;
		long mn = (long) Long.MAX_VALUE;
		for (int i=0; i<m; i++){
			long curr = min(L[i]);
			if (curr < mn){
				mn = curr;
			}
		}		
		
		return mn;
	}
	
	/**
	 * @param L an array of floats
	 * @return The minimum element in the array
	 */
	public static long min(long[] L){
		int n = L.length;
		long mn = (long) Float.MAX_VALUE;
		for (int i=0; i<n; i++){
			if (L[i] > mn){
				mn = L[i];
			}
		}
		return mn;
	}
	
	/**
	 * @param V A Vector of Numbers
	 * @return The minimum element in the Vector
	 */
	public static Number min(Vector<Number> V){
		Double mn = Double.MAX_VALUE;
		for (Number d : V){
			if (d.doubleValue() < mn){
				mn = d.doubleValue();
			}
		}
		return mn;
	}
	
//	public static Double min(HashSet<Double> V){
//		Double mn = Double.MAX_VALUE;
//		for (Double d : V){
//			if (d < mn){
//				mn = d;
//			}
//		}
//		return mn;
//	}
	
	/**
	 * @param V A HashSet of Numbers
	 * @return The minimum element in the HashSet
	 */
	public static Number min(HashSet<Number> V){
		Double mn = Double.MAX_VALUE;
		for (Number d : V){
			if (d.doubleValue() < mn){
				mn = d.doubleValue();
			}
		}
		return mn;
	}

	/**
	 * @param V A HashSet of Numbers
	 * @return The minimum element in the HashSet
	 */
	public static Integer min(Collection<Integer> V){
		Integer mn = Integer.MAX_VALUE;
		for (Integer d : V){
			if (d < mn){
				mn = d;
			}
		}
		return mn;
	}
}
