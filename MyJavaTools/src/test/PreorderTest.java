package test;

import hashtools.Key;

import java.util.HashSet;

import mathtools.Order;

public class PreorderTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// FIXME Auto-generated method stub

		String a = "a"; String b = "b"; String c = "c"; String d = "d";
		String e = "e"; String f = "f"; String g = "g"; String h = "h";
		HashSet<Key<String, String>> relations = new HashSet<Key<String, String>>();
		relations.add(new Key<String, String>(g, f));
		relations.add(new Key<String, String>(g, h));
		relations.add(new Key<String, String>(f, e));
		relations.add(new Key<String, String>(e, d));
		relations.add(new Key<String, String>(d, c));
		relations.add(new Key<String, String>(c, b));
		relations.add(new Key<String, String>(b, a));
		relations.add(new Key<String, String>(h, a));
		
		Order<String> P = new Order<String>(relations);
		System.out.println("Elements = "+P.getElements().toString());
		System.out.println("Order relations = "+P.getTrans().toString());
		System.out.println("Poset check: "+P.checkPosetRules());
		System.out.println("Max = "+P.max());
		System.out.println("Min = "+P.min());
		System.out.println("Maximal chains = "+P.maximalChains().toString());
		System.out.println("Height = "+P.height());
		System.out.println("up(e) = "+P.up(e).toString());
		System.out.println("down(h) = "+P.down(h).toString());
		System.out.println("IntRank = "+P.stdIntRank().toString());
		System.out.println("IntRank Poset = "+P.intRankPoset().toString());
		Key<Integer, Integer> iters_height = P.itersToChainAndHeight();
		System.out.println("Num iters to chain = "+iters_height.getFirst());
		System.out.println("Height of final chain = "+iters_height.getSecond());
		
		Key<Integer, Order<?>> totalPre = P.totalPreorder();
		System.out.println("Num iters to total preorder = "+totalPre.getFirst());
		System.out.println(totalPre.getSecond().toString());
	}

}
