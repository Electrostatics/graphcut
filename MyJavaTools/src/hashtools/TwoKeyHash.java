package hashtools;

import java.util.HashMap;
import java.util.Set;
import java.util.Vector;

public class TwoKeyHash <K, V> {
	
	private HashMap<Vector<K>,V> twoKeyHash;
	
	/* * * * * * * * * *
	 *  CONSTRUCTORS   *
	 * * * * * * * * * */
	
	public TwoKeyHash(){
		twoKeyHash = new HashMap<Vector<K>,V>();
	}
	
	public TwoKeyHash(HashMap<Vector<K>,V> tkh){
		twoKeyHash = tkh;
	}
	
	
	/* * * * * * *
	 *  METHODS  *
	 * * * * * * */
	
	public void remove(Vector<K> keys){
		twoKeyHash.remove(keys);
	}
	
	public void removeAll(Vector<Vector<K>> keySet){
		for (Vector<K> key: keySet){
			remove(key);
		}
	}
	
	public String toString(){
		return twoKeyHash.toString();
	}
	
	public void put(K key1, K key2, V val){
		Vector<K> temp = new Vector<K>(2);
		temp.add(0,key1);
		temp.add(1,key2);
		twoKeyHash.put(temp, val);		
	}
	
	public V get(K key1, K key2){
		Vector<K> temp = new Vector<K>(2);
		temp.add(0,key1);
		temp.add(1,key2);
		return twoKeyHash.get(temp);
	}
	
	public Set<Vector<K>> keySet(){
		return twoKeyHash.keySet();
	}
	
	public boolean containsKey(K key1, K key2){
		Vector<K> temp = new Vector<K>(2);
		temp.add(0,key1);
		temp.add(1,key2);
		return twoKeyHash.containsKey(temp);
	}
	
	public void remove(K key1, K key2){
		Vector<K> temp = new Vector<K>(2);
		temp.add(0,key1);
		temp.add(1,key2);
		twoKeyHash.remove(temp);
	}
	
	public Vector<K> leftKeys(){
		Vector<K> left = new Vector<K>();
		
		Set<Vector<K>> allKeys = keySet();
		for (Vector<K> k : allKeys){
			K key1 = k.get(0);
			if (!left.contains(key1)){
				left.add(key1);
			}
		}
		
		return left;
	}
	
	public Vector<K> rightKeys(){
		Vector<K> right = new Vector<K>();
		
		Set<Vector<K>> allKeys = keySet();
		for (Vector<K> k : allKeys){
			K key1 = k.get(1);
			if (!right.contains(key1)){
				right.add(key1);
			}
		}
		
		return right;
	}
	
	@SuppressWarnings("unchecked")
	public Vector<K> allKeys(){
		Vector<K> left = leftKeys();
		Vector<K> right = rightKeys();
		Vector<K> all = (Vector<K>) left.clone();
		
		for (K r : right){
			if (!all.contains(r)){
				all.add(r);
			}
		}
		
		return all;
	}
	
	@SuppressWarnings("unchecked")
	public TwoKeyHash<K,V> clone(){
		
		HashMap<Vector<K>,V> tkh = (HashMap<Vector<K>, V>) twoKeyHash.clone();
		
		TwoKeyHash<K,V> theClone = new TwoKeyHash<K,V>(tkh);
		
		return theClone;
		
		
	}
	
}
