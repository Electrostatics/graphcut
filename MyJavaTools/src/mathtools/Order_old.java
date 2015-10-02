package mathtools;

import hashtools.Key;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class Order_old<V> {

	/* * * * * * * *
	 *   FIELDS    *
	 * * * * * * * */
	
	protected HashSet<V> elements;
	protected HashSet<Key<V, V>> trans = null; // The transitive closure of order relations

	protected HashMap<V, Interval> stdIntRanks = null;
	
	/* * * * * * * * * * *
	 *   CONSTRUCTORS    *
	 * * * * * * * * * * */

	public Order_old(){
		elements = new HashSet<V>();
		trans = new HashSet<Key<V, V>>();
	}
	
	public Order_old(HashSet<Key<V, V>> relations){
		makeElements(relations);
		makeTrans(relations);
		
	}
	
	public Order_old(HashSet<V> elements, HashSet<Key<V, V>> relations){
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
		makeTrans(trans);
	}
	
	/**
	 * @return The set of poset elements
	 */
	public HashSet<V> getElements(){
		return elements;
	}
	
	/**
	 * @return The full set of poset relations
	 */
	public HashSet<Key<V, V>> getTrans(){
		return trans;
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
				Order_old<V> mDn = lessThan(m);
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
	public Order_old<V> restriction(HashSet<V> subElements){
		HashSet<Key<V, V>> relations = new HashSet<Key<V, V>>();
		for (Key<V, V> pair : trans){
			if (subElements.contains(pair.getFirst()) && subElements.contains(pair.getSecond()))
				relations.add(pair);
		}
		
		return new Order_old<V>(subElements, relations);
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
	public Order_old<V> interval(V p, V q){
		HashSet<V> upP = upSet(p);
		HashSet<V> downQ = downSet(q);
		HashSet<V> interval = new HashSet<V>();
		for (V a : upP){
			if (downQ.contains(a)){
				interval.add(a);
			}
		}
		return restriction(interval);
	}
	
	/**
	 * @param p An element in this poset
	 * @return The poset formed by all elements smaller than or equal to p
	 */
	public Order_old<V> down(V p){
		HashSet<V> subElements = new HashSet<V>();
		// for each relation, if the larger (second) is p then add the
		// smaller (first) to subElements
		for (Key<V, V> pair : trans){
			if (pair.getSecond().equals(p))
				subElements.add(pair.getFirst());
		}
		
		return restriction(subElements);
	}
	
	/**
	 * @param p An element in this poset
	 * @return The set of elements smaller than or equal to p
	 */
	public HashSet<V> downSet(V p){
		HashSet<V> subElements = new HashSet<V>();
		// for each relation, if the larger (second) is p then add the
		// smaller (first) to subElements
		for (Key<V, V> pair : trans){
			if (pair.getSecond().equals(p))
				subElements.add(pair.getFirst());
		}
		return subElements;
	}
	
	/**
	 * @param p An element in this poset
	 * @return The poset formed by all elements smaller than or equal to p
	 */
	public Order_old<V> up(V p){
		HashSet<V> subElements = new HashSet<V>();
		// for each relation, if the smaller (first) is p then add the
		// larger (second) to subElements
		for (Key<V, V> pair : trans){
			if (pair.getFirst().equals(p))
				subElements.add(pair.getSecond());
		}
		
		return restriction(subElements);
	}
	
	/**
	 * @param p An element in this poset
	 * @return The set of elements larger than or equal to p
	 */
	public HashSet<V> upSet(V p){
		HashSet<V> subElements = new HashSet<V>();
		// for each relation, if the smaller (first) is p then add the
		// larger (second) to subElements
		for (Key<V, V> pair : trans){
			if (pair.getFirst().equals(p))
				subElements.add(pair.getSecond());
		}
		return subElements;
	}
	
	/**
	 * @param p An element in this poset
	 * @return The poset formed by all elements smaller than p
	 */
	public Order_old<V> lessThan(V p){
		HashSet<V> subElements = new HashSet<V>();
		// for each relation, if the larger (second) is p then add the
		// smaller (first) to subElements
		for (Key<V, V> pair : trans){
			if (pair.getSecond().equals(p) && !pair.getFirst().equals(p))
				subElements.add(pair.getFirst());
		}
		
		return restriction(subElements);
	}
	
	/**
	 * @param p An element in this poset
	 * @return The set of all elements smaller than p
	 */
	public HashSet<V> lessThanSet(V p){
		HashSet<V> subElements = new HashSet<V>();
		// for each relation, if the larger (second) is p then add the
		// smaller (first) to subElements
		for (Key<V, V> pair : trans){
			if (pair.getSecond().equals(p) && !pair.getFirst().equals(p))
				subElements.add(pair.getFirst());
		}
		
		return subElements;
	}
	
	/**
	 * @param p An element in this poset
	 * @return The poset formed by all elements larger than p
	 */
	public Order_old<V> greaterThan(V p){
		HashSet<V> subElements = new HashSet<V>();
		// for each relation, if the smaller (first) is p then add the
		// larger (second) to subElements
		for (Key<V, V> pair : trans){
			if (pair.getFirst().equals(p) && !pair.getSecond().equals(p))
				subElements.add(pair.getSecond());
		}
		
		return restriction(subElements);
	}
	
	/**
	 * @param p An element in this order
	 * @return The set of all elements larger than p
	 */
	public HashSet<V> greaterThanSet(V p){
		HashSet<V> subElements = new HashSet<V>();
		// for each relation, if the smaller (first) is p then add the
		// larger (second) to subElements
		for (Key<V, V> pair : trans){
			if (pair.getFirst().equals(p) && !pair.getSecond().equals(p))
				subElements.add(pair.getSecond());
		}
		
		return subElements;
	}
	
	/**
	 * @param t A given top bound
	 * @param b A given bottom bound
	 * @return A new poset with t and b added to the top and bottom
	 * respectively, only if they don't already exist in the poset
	 */
	public Order_old<V> bound(V t, V b){
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
		
		return new Order_old<V>(newElements, newRelations);
		
	}
	
	/* * * * * * * * * * * * * * * *
	 *   STANDARD INTERVAL RANK    *
	 * * * * * * * * * * * * * * * */
	
	/**
	 * @return The standard interval rank for all elements of this poset
	 */
	public HashMap<V, Interval> stdIntRank(){
		if (stdIntRanks != null){
			return stdIntRanks;
		} else {
			HashMap<V, Interval> sirs = new HashMap<V, Interval>();
			int n = elements.size(); System.out.print(n+": ");
			int i=0; 
			for (V p : elements){
				sirs.put(p, stdIntRank(p));
				System.out.print(i+", ");
				i++;
			}System.out.println();
			return sirs;
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
		Order_old<V> pUp = up(p);
		Order_old<V> pDn = down(p);
		int r_lower_star = pUp.height()-1;
		int r_upper_star = H-pDn.height();
		
		return new Interval(r_lower_star, r_upper_star);
	}
	
	public Preorder<V> copyToPreorder(){
		Preorder<V> pre = new Preorder<V>(new HashSet<V>(elements), new HashSet<Key<V, V>>(trans), false);
		if (!pre.checkPreorderRules())
			return null;
		else
			return pre;
	}
	
	public Poset_old2<V> copyToPoset(){
		Poset_old2<V> po = new Poset_old2<V>(new HashSet<V>(elements), new HashSet<Key<V, V>>(trans), false);
		if (!po.checkPosetRules())
			return null;
		else
			return po;
		
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
