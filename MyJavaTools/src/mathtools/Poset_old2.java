package mathtools;

import hashtools.Key;

import java.util.HashMap;
import java.util.HashSet;

public class Poset_old2<V> extends Order_old<V> {

	public Poset_old2(){
		super();
	}
	
	public Poset_old2(HashSet<Key<V, V>> relations, boolean check){
		super(relations);
		if (check)
			System.out.println("poset check: "+checkPosetRules());
	}
	
	public Poset_old2(HashSet<V> elements, HashSet<Key<V, V>> relations, boolean check){
		super(elements, relations);
		if (check)
			System.out.println("poset check: "+checkPosetRules());
	}
	
	/**
	 * Checks whether or not this Poset satisfies the definition of a Poset:
	 * Reflexive - p <= p for all p \in P
	 * (translates to [p, p] is in trans for all p in elements)
	 * Transitive - p <= q and q <= r implies p <= r for all p, q, r \in P
	 * (translates to [p,q] and [q,r] in trans implies [p,r] is in trans)
	 * Antisymmetric - if p <= q and q <= p then p = q
	 * @return true if these criteria are satisfied, false otherwise
	 */
	protected boolean checkPosetRules(){
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
			
			// antisymmetric
			for (Key<V, V> pair : trans){
				if (trans.contains(new Key<V, V>(pair.getSecond(), pair.getFirst()))){
					if (!pair.getFirst().equals(pair.getSecond())){
						System.err.println("Poset is not fully antisymmetric.");
						System.err.println("\tHave both "+pair.toString()+" and "+pair.swap().toString());
						return false;
					}
				}
			}
		}
		
		return true;
	}
	
	/**
	 * @return The poset formed by the interval ranks of the given poset.
	 * The ordering relation on the intervals is weak order.
	 */
	public Poset_old2<Interval>	intRankPoset(){
		HashMap<V, Interval> stdIntRanks = stdIntRank();
		HashSet<Interval> ranks = new HashSet<Interval>();
		for (V e : stdIntRanks.keySet()){
			Interval r = stdIntRanks.get(e);
			boolean add = true;
			for (Interval r2 : ranks){
				if (r.equals(r2)){
					add = false;
					break;
				}
			}
			if (add)
				ranks.add(r);
		}
		
		HashSet<Key<Interval, Interval>> sirRelations = new HashSet<Key<Interval, Interval>>();
		for (Interval rank1 : ranks){
			for (Interval rank2 : ranks){
				if (Interval.iWeakLess(rank1, rank2))
					sirRelations.add(new Key<Interval, Interval>(rank2, rank1));
			}
		}
		return new Poset_old2<Interval>(sirRelations, true);
	}
	
	/**
	 * @return The number of applications of standard interval rank before
	 * the final poset is a chain
	 */
	public Key<Integer, Integer> itersToChainAndHeight(){
		int numIters = 0;
		int height = height();
		
		if (!isChain()){
			Poset_old2<Interval> Rplus = intRankPoset();
			Key<Integer, Integer> iters_height = Rplus.itersToChainAndHeight();
			numIters = iters_height.getFirst()+1;
			height = iters_height.getSecond();
		}
		
		return new Key<Integer, Integer>(numIters, height);
	}
}
