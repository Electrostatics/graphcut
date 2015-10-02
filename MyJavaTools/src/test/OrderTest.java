package test;

import hashtools.Key;

import java.util.HashSet;

import mathtools.Order;

public class OrderTest {

	/**
	 * @param args
	 * @throws CloneNotSupportedException 
	 */
	public static void main(String[] args) throws CloneNotSupportedException {
		// FIXME Auto-generated method stub

		HashSet<Key<Integer, Integer>> relations = new HashSet<Key<Integer, Integer>>();
		relations.add(new Key<Integer, Integer>(0, 1));
		relations.add(new Key<Integer, Integer>(0, 2));
		relations.add(new Key<Integer, Integer>(3, 4));
		relations.add(new Key<Integer, Integer>(2, 4));
		relations.add(new Key<Integer, Integer>(1, 5));
		relations.add(new Key<Integer, Integer>(5, 3));
		
		Order<Integer> P = new Order<Integer>(relations);
		
		System.out.println("Elements = "+P.getElements().toString());
		System.out.println("Order relations = "+P.getTrans().toString());
		System.out.println("Poset check: "+P.checkPosetRules());
		System.out.println("Max = "+P.max());
		System.out.println("Min = "+P.min());
		System.out.println("Maximal chains = "+P.maximalChains().toString());
		System.out.println("Height = "+P.height());
		System.out.println("up(1) = "+P.up(1).toString());
		System.out.println("down(2) = "+P.down(2).toString());
		System.out.println("IntRank = "+P.stdIntRank().toString());
		System.out.println("IntRank Poset = "+P.intRankPoset().toString());
		Key<Integer, Integer> iters_height = P.itersToChainAndHeight();
		System.out.println("Num iters to chain = "+iters_height.getFirst());
		System.out.println("Height of final chain = "+iters_height.getSecond());
		
		Key<Integer, Order<?>> totalPre = P.totalPreorder();
		System.out.println("Num iters to total preorder = "+totalPre.getFirst());
		System.out.println(totalPre.getSecond().toString());
		
		System.out.println("\nA sub-poset");
		HashSet<Integer> subElements = new HashSet<Integer>();
		subElements.add(1); subElements.add(2); subElements.add(3);
		Order<Integer> Q = P.restriction(subElements);
		System.out.println("Elements = "+Q.getElements().toString());
		System.out.println("Order relations = "+Q.getTrans().toString());
		System.out.println("Max = "+Q.max());
		System.out.println("Min = "+Q.min());
		
		P.makeCovers();
		System.out.println(P.getCovers().toString());

		
	}

}
