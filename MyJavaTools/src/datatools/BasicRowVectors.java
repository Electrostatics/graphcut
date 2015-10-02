package datatools;

import java.util.HashMap;
import java.util.Set;
import java.util.Vector;

import mathtools.MapleTools;

public class BasicRowVectors<H, D> {
	
	/* * * * * *
	 * MEMBERS *
	 * * * * * */
	
	protected HashMap<H, Vector<D>> dataMap;
	protected int dataSize;
	
	/* * * * * * * * *
	 * CONSTRUCTORS  *
	 * * * * * * * * */
	
	/**
	 * Constructor. Initializes the dataMap to be an empty HashMap with 
	 * keys as H objects and entries as Vector<D> objects.
	 */
	public BasicRowVectors(){
		dataMap = new HashMap<H, Vector<D>>();
	}
	
	/* * * * * * * * * *
	 * GETTERS/SETTERS *
	 * * * * * * * * * */
		
	/**
	 * @return The set of row identifiers
	 */
	public Set<H> getIdentifiers(){
		return dataMap.keySet();
	}
	
	/**
	 * @param id A data row identifier
	 * @return The data associated to the identifier id
	 */
	public Vector<D> getIdentifiedData(H id){
		return dataMap.get(id);
	}
	
	/**
	 * @return The number of identifiers in the data set
	 */
	public int getNumIDs(){
		return dataMap.keySet().size();
	}
	
	/**
	 * @param id Data identifier
	 * @param data Data value
	 * Puts the pair <id, data> into the HashMap
	 */
	public void addData(H id, Vector<D> data){
		dataMap.put(id, data);
		dataSize = data.size();
	}
	
	public int getDataSize(){
		return dataSize;
	}

	/* * * * *
	 * OTHER *
	 * * * * */
	
	public boolean dataSizeConsistency(){
		int size = -1;
		for (H id : dataMap.keySet()){
			Vector<D> idVec = dataMap.get(id);
			if (size == -1){
				size = idVec.size();
			} else{
				if (size != idVec.size()){
					System.out.println("Vector for "+id.toString()+" has size "+idVec.size()+" not consistent with "+size);
					return false;
				}
			}
		}
		return true;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		String out = "";
		Set<H> ids = dataMap.keySet();
		for (H id : ids){
			String idString = id.toString();
			out += idString+" (size ";
			Vector<D> thisData = dataMap.get(id);
			out += thisData.size()+"): ";
			for (int i=0; i<thisData.size()-1; i++){
				out += thisData.get(i).toString()+", ";
			}
			out += thisData.get(thisData.size()-1)+"\n";
		}
		return out;
	}
	
//	public Vector<Double> averageData(){
//		// add up all of the vectors
//		Vector<Double> sum = new Vector<Double>();
//		for (int i=0; i<dataSize; i++){
//			sum.add(i, 0.0);
//		}
//		for (H id : dataMap.keySet()){
//			Vector<Double> idVec = dataMap.get(id);
//			for (int i=0; i<idVec.size(); i++){
//				Double ithSum = sum.get(i);
//				sum.set(i, ithSum+idVec.get(i));
//			}
//		}
//		
//		// divide by the number of vectors
//		Vector<Double> avg = new Vector<Double>(sum.size());
//		int N = dataMap.keySet().size();
//		for (int i=0; i<sum.size(); i++){
//			avg.add(i, sum.get(i)/N);
//		}
////		System.out.println("Average vector size: "+avg.size());
//		return avg;
//	}
}
