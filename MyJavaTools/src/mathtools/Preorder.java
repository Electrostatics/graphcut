package mathtools;

import hashtools.Key;

import java.util.HashMap;
import java.util.HashSet;

public class Preorder<V> extends Order_old<V> {

	public Preorder(HashSet<V> elements, HashSet<Key<V, V>> relations, boolean check){
		super(elements, relations);
		if (check)
			System.out.println("preorder check: "+checkPreorderRules());
	}
	
	/**
	 * Checks whether or not this Poset satisfies the definition of a Poset:
	 * Reflexive - p <= p for all p \in P
	 * (translates to [p, p] is in trans for all p in elements)
	 * Transitive - p <= q and q <= r implies p <= r for all p, q, r \in P
	 * (translates to [p,q] and [q,r] in trans implies [p,r] is in trans)
	 * @return true if these criteria are satisfied, false otherwise
	 */
	protected boolean checkPreorderRules(){
		if (trans == null){
			System.err.println("Trans has not been ititialized yet, don't know.");
			return false;
		} else {
			// reflexive
			for (V p : elements){
				if (!trans.contains(new Key<V, V>(p,p))){
					System.err.println("Poset is not fully reflexive. Missing "+p.toString()+"<"+p.toString());
					return false;
				}
			}
			
			// transitive
			for (V p : elements){
				HashSet<V> pUp = greaterThanSet(p);
				for (V q : pUp){
					HashSet<V> qUp = greaterThanSet(q);
					for (V r : qUp){
						if (!trans.contains(new Key<V, V>(p, r))){
							System.err.println("Poset is not fully transitive.");
							System.err.println("\tHave <"+p.toString()+", "+q.toString()+"> and <"+q.toString()+", "+r.toString()+">, but not <"+p.toString()+", "+r.toString()+">.");
							return false;
						}
					}
				}
			}
			
		}
		
		return true;
	}

	/**
	 * @return A pair of objects. First, an Integer which is the number
	 * of steps taken to achieve a graded poset. Secondly, the final graded
	 * poset that is achieved. The graded poset can be thought of as a 
	 * preorder by letting everything from rank i be less than everything
	 * from rank j for i < j.
	 */
	public Key<Integer, Preorder<V>> totalPreorder(){
		
		int numIters = 0;
		
		Preorder<V> RplusAll;
		Preorder<V> preorder = this;
		if (!isGraded()){
			RplusAll = intRankPosetAll();
			Key<Integer, Preorder<V>> next = RplusAll.totalPreorder();
			numIters = next.getFirst()+1;
			preorder = next.getSecond();
		}
		
		
		return new Key<Integer, Preorder<V>>(numIters, preorder);
		
	}
	
	/**
	 * @return The poset containing the same elements as this poset but 
	 * ordered by their standard interval ranks in the weak interval order.
	 */
	public Preorder<V> intRankPosetAll(){
		HashMap<V, Interval> stdIntRanks = stdIntRank();
		HashSet<Key<V, V>> newRelations = new HashSet<Key<V, V>>();
		for (V v1 : elements){
			Interval v1rank = stdIntRanks.get(v1);
			for (V v2 : elements){
				Interval v2rank = stdIntRanks.get(v2);
				if (Interval.iWeakLess(v1rank, v2rank))
					newRelations.add(new Key<V, V>(v2, v1));
			}
		}
		return new Preorder<V>(elements, newRelations, true);
	}
}

