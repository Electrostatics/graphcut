package mathtools;

import hashtools.Key;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class Order<V> {

	/* * * * * * * *
	 *   FIELDS    *
	 * * * * * * * */
	
	protected HashSet<V> elements;
	protected HashSet<Key<V, V>> trans = null; // The transitive closure of order relations
	protected HashSet<Key<V, V>> covers = null; // The cover relations for the order

	protected HashMap<V, Interval> stdIntRanks = null;
	
	/* * * * * * * * * * *
	 *   CONSTRUCTORS    *
	 * * * * * * * * * * */

	/**
	 * Makes an empty Order, intializes the elements and trans
	 * to be empty.
	 */
	public Order(){
		elements = new HashSet<V>();
		trans = new HashSet<Key<V, V>>();
	}
	
	/**
	 * Makes an Order with the given relations. First
	 * finds all elements within relations, then sets
	 * the transitive closure of the set of relations
	 * to be trans.
	 * @param relations A set of <a, b> relations
	 */
	public Order(HashSet<Key<V, V>> relations){
		makeElements(relations);
		makeTrans(relations);
		
	}
	
	/**
	 * Makes the relations into trans by finding the
	 * transitive closure. Then sets elements to equal
	 * the given elements.
	 * @param elements A set of elements for the Order
	 * @param relations A set of relations for the Order
	 */
	public Order(HashSet<V> elements, HashSet<Key<V, V>> relations){
		this(relations);
		this.elements = elements;
	}
	
	/* * * * * * * * * * * * * *
	 *   CONSTRUCTOR HELPERS   *
	 * * * * * * * * * * * * * */

	/**
	 * Calculates the elements in the poset as being those which
	 * are in the given relations.
	 * 
	 * @param relations A set of pairs representing relations
	 * in the poset
	 */
	public void makeElements(HashSet<Key<V, V>> relations){
		elements = new HashSet<V>();
		for (Key<V, V> pair : relations){
			elements.add(pair.getFirst());
			elements.add(pair.getSecond());
		}
	}
	
	/**
	 * Makes the cover relations. Modeled after PosetToHasse.
	 */
	public void makeCovers(){
		
		covers = new HashSet<Key<V, V>>();
		for (V e : elements){
			Order<V> upe = greaterThan(e);
			HashSet<V> minUpe = upe.min();
			for (V u : minUpe){
				covers.add(new Key<V, V>(e, u));
			}
		}
		
	}
	
	/**
	 * Modeled after HasseToPoset
	 * 
	 * @param relations A set of pairs representing a<b relations
	 * in the Order
	 */
	public void makeTrans(HashSet<Key<V, V>> relations){
		
		HashSet<Key<V, V>> P = new HashSet<Key<V, V>>();
		HashSet<Key<V, V>> P1 = (HashSet<Key<V, V>>) relations.clone();
		
		while (!P.equals(P1)){
			P.addAll(P1);
			HashSet<Key<V, V>> toAdd = new HashSet<Key<V, V>>();
			for (Key<V, V> pair : P1){
				V smaller = pair.getFirst();
				V larger = pair.getSecond();
				for (Key<V, V> pair2 : P){
					if (pair2.getFirst().equals(larger)){
						toAdd.add(new Key<V, V>(smaller, pair2.getSecond()));
					}
				}
			}
			P1.addAll(toAdd);
		}
		
		for (V p : elements){
			P.add(new Key<V, V>(p, p));
		}

		trans = P;
		
	}
	
	/**
	 * Checks whether or not this Order satisfies the definition of a Poset:
	 * Reflexive - p <= p for all p \in P
	 * (translates to [p, p] is in trans for all p in elements)
	 * Transitive - p <= q and q <= r implies p <= r for all p, q, r \in P
	 * (translates to [p,q] and [q,r] in trans implies [p,r] is in trans)
	 * Antisymmetric - if p <= q and q <= p then p = q
	 * @return true if these criteria are satisfied, false otherwise
	 */
	public boolean checkPosetRules(){
		
		if (trans == null){
			System.err.println("Trans has not been ititialized yet, don't know.");
			return false;
		} else 
			return (checkReflexive() && checkTransitive() && checkAntisymmetric());
		
	}
	
	/**
	 * Checks whether or not this Order satisfies the definition of a Preorder:
	 * Reflexive - p <= p for all p \in P
	 * (translates to [p, p] is in trans for all p in elements)
	 * Transitive - p <= q and q <= r implies p <= r for all p, q, r \in P
	 * (translates to [p,q] and [q,r] in trans implies [p,r] is in trans)
	 * @return true if these criteria are satisfied, false otherwise
	 */
	public boolean checkPreorderRules(){
		if (trans == null){
			System.err.println("Trans has not been ititialized yet, don't know.");
			return false;
		} else 
			return (checkReflexive() && checkTransitive());
		
	}
	
	/**
	 * @return true iff the Order is reflexive. I.e., <e, e> is in trans
	 * for all e in elements.
	 */
	private boolean checkReflexive(){
		for (V p : elements){
			if (!trans.contains(new Key<V, V>(p,p))){
				System.err.println("Poset is not fully reflexive. Missing "+p.toString()+"<"+p.toString());
				return false;
			}
		}
		return true;
	}
	
	/**
	 * @return true iff the Order is transitive. I.e., if <e, f> and
	 * <f, g> are in trans then <e, g> is in trans
	 */
	private boolean checkTransitive(){
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
		return true;
	}
	
	/**
	 * @return true iff the Order is antisymmetric. I.e., if <e, f>
	 * and <f, e> are both in trans then e=f.
	 */
	private boolean checkAntisymmetric(){
		for (Key<V, V> pair : trans){
			if (trans.contains(new Key<V, V>(pair.getSecond(), pair.getFirst()))){
				if (!pair.getFirst().equals(pair.getSecond())){
					System.err.println("Poset is not fully antisymmetric.");
					System.err.println("\tHave both "+pair.toString()+" and "+pair.swap().toString());
					return false;
				}
			}
		}
		return true;
	}
	
	/* * * * * * * * * * * *
	 *   GETTERS/SETTERS   *
	 * * * * * * * * * * * */

	/**
	 * @param e Adds an element to the Order
	 */
	public void addElement(V e){
		elements.add(e);
	}
	
	/**
	 * @param relation Adds a relation to the Order
	 */
	public void addRelation(Key<V, V> relation){
		trans.add(relation);
//		makeTrans(trans);
	}
	
	/**
	 * @return The set of Order elements
	 */
	public HashSet<V> getElements(){
		return elements;
	}
	
	/**
	 * @return The full set of Order relations
	 */
	public HashSet<Key<V, V>> getTrans(){
		return trans;
	}
	
	/**
	 * @return The cover relations for this Order
	 */
	public HashSet<Key<V, V>> getCovers(){
		return covers;
	}
	
	/* * * * * * *
	 *   TESTS   *
	 * * * * * * */

	/**
	 * Checks to see if the number of relations is equal to
	 * the number of elements (n) choose 2, plus n. If so, then
	 * all relations must be present and so the poset is a chain.
	 * 
	 * @return true if this poset is a chain, false otherwise
	 */
	public boolean isChain(){
		int n = elements.size();
		int m = trans.size();
		if (m == n*(n-1)/2+n)
			return true;
		else
			return false;
	}
	
	/**
	 * Checks to see if all relations are of the form <p, p>. If one is
	 * found that is <p, q> for p \neq q then it returns false at that time. 
	 * Otherwise, after checking all relations, it returns true.
	 * 
	 * @return true if this poset is an antichain, false otherwise
	 */
	public boolean isAntichain(){
		
		for (Key<V, V> pair : trans){
			if (!pair.getFirst().equals(pair.getSecond()))
				return false;
		}
		return true;
		
	}
	
	/**
	 * @return true if the poset is graded, false otherwise. This is
	 * tested by looking at the standard interval rank. If all
	 * interval ranks are [i,i] then the Order is graded
	 */
	public boolean isGraded(){
		HashMap<V, Interval> stdIntRank = stdIntRank();
		boolean graded = true;
		for (V e : elements){
			Interval r = stdIntRank.get(e);
			if (r.getLeft() != r.getRight()){
				graded = false;
				break;
			}
		}
		return graded;
	}

	/* * * * * * * * * * * * *
	 *   BASIC OPERATIONS    *
	 * * * * * * * * * * * * */
	
	/**
	 * @return The minimum elements of the Poset
	 */
	public HashSet<V> min(){
		// First, find those elements which are second coordiates
		// in covers. These elements can't be minimums because
		// they are larger than something.
		HashSet<V> notMin = new HashSet<V>();
		for (Key<V, V> pair : trans){
			if (!pair.getFirst().equals(pair.getSecond())){
				notMin.add(pair.getSecond());
			}
		}

		// Find those elements which are not in notMin
		HashSet<V> Min = new HashSet<V>();
		for (V p : elements){
			if (!notMin.contains(p)){
				Min.add(p);
			}
		}
		
		return Min;
	}
	
	/**
	 * @return The maximum elements of the Poset
	 */
	public HashSet<V> max(){
		// First, find those elements which are first coordiates
		// in covers. These elements can't be maximums because
		// they are smaller than something.
		HashSet<V> notMax = new HashSet<V>();
		for (Key<V, V> pair : trans){
			if (!pair.getFirst().equals(pair.getSecond())){
				notMax.add(pair.getFirst());
			}
		}

		// Find those elements which are not in notMax
		HashSet<V> Max = new HashSet<V>();
		for (V p : elements){
			if (!notMax.contains(p)){
				Max.add(p);
			}
		}
		
		return Max;
	}
	
	/**
	 * @return A set of the maximal chains in the poset. Each
	 * maximal chain is stored as an ArrayList where the elements 
	 * increase from left to right (first to last).
	 */
	public HashSet<ArrayList<V>> maximalChains(){
		HashSet<ArrayList<V>> MC = new HashSet<ArrayList<V>>();
		if (elements.size() == 1){
			ArrayList<V> C = new ArrayList<V>();
			C.addAll(elements);
			MC.add(C);
		} else if (isAntichain()){
			for (V e : elements){
				ArrayList<V> C = new ArrayList<V>();
				C.add(e);
				MC.add(C);
			}
		} else {
			HashSet<V> M = max();
			for (V m : M){
				Order<V> mDn = lessThan(m);
				HashSet<ArrayList<V>> Cm = mDn.maximalChains();
				for (ArrayList<V> C : Cm){
					C.add(m);
					MC.add(C);
				}
			}
		}
		
		return MC;
	}
	
	/**
	 * @return The height of this poset, i.e. the length
	 * of the largest chain.
	 */
	public int height(){
		HashSet<ArrayList<V>> MC = maximalChains();
		int H = Integer.MIN_VALUE;
		for (ArrayList<V> C : MC){
			if (C.size() > H){
				H = C.size();
			}
		}
		return H;
	}
	
	/**
	 * @param subElements A subset of elements
	 * @return The Poset with only those relations involving 
	 * items from subElements 
	 */
	public Order<V> restriction(HashSet<V> subElements){
		HashSet<Key<V, V>> relations = new HashSet<Key<V, V>>();
		for (Key<V, V> pair : trans){
			if (subElements.contains(pair.getFirst()) && subElements.contains(pair.getSecond()))
				relations.add(pair);
		}
		
		return new Order<V>(subElements, relations);
	}

	/**
	 * @param p An element of this poset
	 * @param q An element of this poset
	 * @return The set of all elements which are greater than or equal to p
	 * and smaller than or equal to q.
	 */
	public HashSet<V> intervalSet(V p, V q){
		HashSet<V> upP = upSet(p);
		HashSet<V> downQ = downSet(q);
		HashSet<V> interval = new HashSet<V>();
		for (V a : upP){
			if (downQ.contains(a)){
				interval.add(a);
			}
		}
		return interval;
	}
	
	/**
	 * @param p An element of this poset
	 * @param q An element of this poset
	 * @return The poset formed by the set of all elements which are 
	 * greater than or equal to p and smaller than or equal to q.
	 */
	public Order<V> interval(V p, V q){
		return restriction(intervalSet(p,q));
	}
	
	/**
	 * @param p An element in this poset
	 * @return The poset formed by all elements smaller than or equal to p
	 */
	public Order<V> down(V p){
				
		return restriction(downSet(p));
	}
	
	/**
	 * @param p An element in this poset
	 * @return The set of elements smaller than or equal to p
	 */
	public HashSet<V> downSet(V p){
		HashSet<V> subElements = new HashSet<V>();
		// For each element, e, check to see if the relation [e, p]
		// is in trans. If so, add it to the downSet
		for (V e : elements){
			if (trans.contains(new Key<V, V>(e, p))){
				subElements.add(e);
			}
		}
		return subElements;
	}
	
	/**
	 * @param p An element in this poset
	 * @return The poset formed by all elements smaller than or equal to p
	 */
	public Order<V> up(V p){
		
		return restriction(upSet(p));
	}
	
	/**
	 * @param p An element in this poset
	 * @return The set of elements larger than or equal to p
	 */
	public HashSet<V> upSet(V p){
		HashSet<V> subElements = new HashSet<V>();
		// For each element, e, check to see if the relation [p, e]
		// is in trans. If so, add it to the upSet
		for (V e : elements){
			if (trans.contains(new Key<V, V>(p, e))){
				subElements.add(e);
			}
		}
		return subElements;
	}
	
	/**
	 * @param p An element in this poset
	 * @return The poset formed by all elements smaller than p
	 */
	public Order<V> lessThan(V p){
		return restriction(lessThanSet(p));
	}
	
	/**
	 * @param p An element in this poset
	 * @return The set of all elements smaller than p
	 */
	public HashSet<V> lessThanSet(V p){
		HashSet<V> subElements = new HashSet<V>();
		// For each element, e, check to see if the relation [e, p]
		// is in trans. If so, add it to the downSet
		for (V e : elements){
			if (trans.contains(new Key<V, V>(e, p)) && !e.equals(p)){
				subElements.add(e);
			}
		}
		return subElements;
	}
	
	/**
	 * @param p An element in this poset
	 * @return The poset formed by all elements larger than p
	 */
	public Order<V> greaterThan(V p){
		return restriction(greaterThanSet(p));
	}
	
	/**
	 * @param p An element in this order
	 * @return The set of all elements larger than p
	 */
	public HashSet<V> greaterThanSet(V p){
		HashSet<V> subElements = new HashSet<V>();
		// For each element, e, check to see if the relation [p, e]
		// is in trans. If so, add it to the upSet
		for (V e : elements){
			if (trans.contains(new Key<V, V>(p, e)) && !e.equals(p)){
				subElements.add(e);
			}
		}
		return subElements;
	}
	
	/**
	 * @param t A given top bound
	 * @param b A given bottom bound
	 * @return A new poset with t and b added to the top and bottom
	 * respectively, only if they don't already exist in the poset
	 */
	public Order<V> bound(V t, V b){
		HashSet<V> newElements = new HashSet<V>();
		newElements.addAll(elements);
		if (!elements.contains(t) && !elements.contains(b)){
			newElements.add(t);
			newElements.add(b);
		} else if (!elements.contains(t) && elements.contains(b)){
			newElements.add(t);
		} else if (elements.contains(t) && !elements.contains(b)){
			newElements.add(b);
		}
		
		HashSet<Key<V, V>> newRelations = new HashSet<Key<V, V>>();
		newRelations.addAll(trans);
		newRelations.add(new Key<V, V>(b, t));
		newRelations.add(new Key<V, V>(b, b));
		newRelations.add(new Key<V, V>(t, t));
		
		for (V e : elements){
			newRelations.add(new Key<V, V>(b, e));
			newRelations.add(new Key<V, V>(e, t));
		}
		
		return new Order<V>(newElements, newRelations);
		
	}
	
	/* * * * * * * * * * * * * * * *
	 *   STANDARD INTERVAL RANK    *
	 * * * * * * * * * * * * * * * */
	
	/**
	 * @return A pair of objects. First, an Integer which is the number
	 * of steps taken to achieve a graded poset. Secondly, the final graded
	 * poset that is achieved. The graded poset can be thought of as a 
	 * preorder by letting everything from rank i be less than everything
	 * from rank j for i < j.
	 */
	public Key<Integer, Order<?>> totalPreorder(){
		
		int numIters = 0;
		
		Order<HashSet<V>> RplusAll;
		Order<?> preorder = this;
		if (!isChain()){
			RplusAll = intRankPosetAll();
			Key<Integer, Order<?>> next = RplusAll.totalPreorder();
			numIters = next.getFirst()+1;
			preorder = next.getSecond();
		}
				
		return new Key<Integer, Order<?>>(numIters, preorder);
		
	}
	
	/**
	 * @return The poset containing the same elements as this poset but 
	 * ordered by their standard interval ranks in the weak interval order.
	 */
	public Order<HashSet<V>> intRankPosetAll(){
		HashMap<V, Interval> stdIntRanks = stdIntRank();
		HashSet<HashSet<V>> newElements = new HashSet<HashSet<V>>();
		HashSet<Key<HashSet<V>, HashSet<V>>> newRelations = new HashSet<Key<HashSet<V>, HashSet<V>>>();
		HashMap<Interval, HashSet<V>> intervalsToSets = new HashMap<Interval, HashSet<V>>();
		
		for (V e : elements){
			Interval rank = stdIntRanks.get(e);
			HashSet<V> values = null;
			if (intervalsToSets.containsKey(rank)){
				if (e instanceof HashSet){
					HashSet<V> newe = (HashSet<V>) e;
					values = intervalsToSets.get(rank);
					HashSet<V> newValues = (HashSet<V>) MapleTools.union(values, newe);
					values = newValues;
				} else {
					values = intervalsToSets.get(rank);
					values.add(e);
				}
			} else {
				if (e instanceof HashSet){
					values = new HashSet<V>();
					HashSet<V> newe = (HashSet<V>) e;
					for (V n : newe){
						values.add(n);
					}
				} else {
					values = new HashSet<V>();
					values.add(e);
				}
			}
			
			intervalsToSets.put(rank, values);
		}
		
		for (Interval i1 : intervalsToSets.keySet()){
			HashSet<V> i1Vals = intervalsToSets.get(i1);
			for (Interval i2 : intervalsToSets.keySet()){
				HashSet<V> i2Vals = intervalsToSets.get(i2);
				if (Interval.iWeakLess(i1, i2))
					newRelations.add(new Key<HashSet<V>, HashSet<V>>(i2Vals, i1Vals));
			}
		}
		Order<HashSet<V>> RplusAll = new Order<HashSet<V>>(newRelations);
		if (RplusAll.checkPosetRules())
			return RplusAll;
		else
			return null;
		
		
		
	}
	
	/**
	 * @return The number of applications of standard interval rank before
	 * the final poset is a chain
	 */
	public Key<Integer, Integer> itersToChainAndHeight(){
		int numIters = 0;
		int height = height();
		
		if (!isChain()){
			Order<Interval> Rplus = intRankPoset();
			Key<Integer, Integer> iters_height = Rplus.itersToChainAndHeight();
			numIters = iters_height.getFirst()+1;
			height = iters_height.getSecond();
		}
		
		return new Key<Integer, Integer>(numIters, height);
	}
	
	/**
	 * @return The poset formed by the interval ranks of the given poset.
	 * The ordering relation on the intervals is weak order.
	 */
	public Order<Interval>	intRankPoset(){
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
		
		Order<Interval> Rplus = new Order<Interval>(sirRelations);
		if (Rplus.checkPosetRules())
			return Rplus;
		else
			return null;
	}
	
	
	/**
	 * @return The standard interval rank for all elements of this poset
	 */
	public HashMap<V, Interval> stdIntRank(){
		if (stdIntRanks != null){
			return stdIntRanks;
		} else {
			stdIntRanks = new HashMap<V, Interval>();
			int n = elements.size(); System.out.print(n+": ");
			int i=0; 
			for (V p : elements){
				stdIntRanks.put(p, stdIntRank(p));
				System.out.print(i+", ");
				i++;
			}System.out.println();
			return stdIntRanks;
		}
	}
	
	/**
	 * @param p An element of this poset
	 * @return The standard interval rank of this poset element. This is
	 * given by the integer inerval:
	 * [height(up(p)) - 1, height(this) - height(down(p))]
	 */
	public Interval stdIntRank(V p){
		int H = height();
//		Order2<V> pUp = up(p);
//		Order2<V> pDn = down(p);
		int r_lower_star = up(p).height()-1;
		int r_upper_star = H-down(p).height();
		
		return new Interval(r_lower_star, r_upper_star);
	}
	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		String out = "";
		out += "["+elements.toString()+", ";
		out += trans.toString()+"]";
		
		return out;
	}
}
