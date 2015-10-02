package generaltools;

import java.util.Vector;

public class VectorTools<K> {
	
	private Vector<K> vec;
	
	public VectorTools(Vector<K> vec){
		this.vec = vec;
	}

	public K getElement(K element){
		System.out.println("Looking for "+element.toString());
		for (int i=0; i<vec.size(); i++){
			System.out.println("   "+vec.get(i).toString());
			if (vec.get(i).equals(element)){
				return vec.get(i);
			}
		}
		
		return null;		
		
	}
	
}
