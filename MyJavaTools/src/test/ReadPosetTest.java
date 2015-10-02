package test;

import hashtools.Key;

import java.io.IOException;

import mathtools.Order;
import filetools.ReadPoset;

public class ReadPosetTest {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// FIXME Auto-generated method stub
		String filename = "C:\\Users\\hoga886\\Documents\\_Projects\\SINT\\HScode-poset-Java.txt";
		ReadPoset rp = new ReadPoset(filename);
		rp.readPoset();
		Order<String> P = rp.getPoset();
		
		System.out.println("Elements = "+P.getElements().toString());
		System.out.println("Order relations = "+P.getTrans().toString());
		System.out.println("Is graded? "+P.isGraded());
//		System.out.println("Max = "+P.max());
//		System.out.println("Min = "+P.min());
////		System.out.println("Maximal chains = "+P.maximalChains().toString());
//		System.out.println("Height = "+P.height());
//		
//		System.out.println("\nUp(38) = "+P.up("38"));
//		System.out.println("Down(38) = "+P.down("38"));
//		Interval I = P.stdIntRank("38");
//		
		System.out.println("IntRank = "+P.stdIntRank().toString());
		System.out.println("IntRank Poset = "+P.intRankPoset().toString());
		Key<Integer, Integer> iters_height = P.itersToChainAndHeight();
		System.out.println("Num iters to chain = "+iters_height.getFirst());
		System.out.println("Final height of chain = "+iters_height.getSecond());
		
		Key<Integer, Order<?>> totalPre = P.totalPreorder();
		System.out.println("Num iters to total preorder = "+totalPre.getFirst());
		System.out.println(totalPre.getSecond().toString());
	}

}
