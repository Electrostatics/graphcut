package main;

import filetools.WriteFile;
import hashtools.Key;

import java.io.FileNotFoundException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.Vector;

import syst.Variable;

public class PkaFromTitrationCurve {
	
	public static void main(String[] args) throws FileNotFoundException{
		PkaFromTitrationCurve pftc = new PkaFromTitrationCurve();
		pftc.doIt(args);
	}

	public void doIt(String[] args) throws FileNotFoundException {
		// TODO Auto-generated method stub
		
//		String in_path = args[0];
		String out_path = args[1];
		String proteinName = args[2];
		String outFile = out_path+"\\"+proteinName+".pkaTC.out";
		WriteFile wr = new WriteFile(outFile);
		
		TitrationCurvesHIS tc = new TitrationCurvesHIS();		
		HashMap<Variable, Vector<Key<Double, Double>>> titrCurves = tc.doIt(args, false); 
		
		Set<Variable> Vars = titrCurves.keySet();
		for (Variable v : Vars){
//			System.out.println("Residue "+v.toString());
			Vector<Key<Double, Double>> titr = titrCurves.get(v);
			if (titr.size() == 0)
				continue;
			Comparator<Key<Double, Double>> comp = new pHComparator();
			PriorityQueue<Key<Double, Double>> Q = new PriorityQueue<Key<Double, Double>>(titr.size(),comp);
			Q.addAll(titr);
			
			double pHaboveHalf = Double.MIN_VALUE; double titrAboveHalf = Double.MIN_VALUE;
			double pHbelowHalf = Double.MAX_VALUE; double titrBelowHalf = Double.MAX_VALUE;
			
			while ((pHaboveHalf == Double.MIN_VALUE || pHbelowHalf == Double.MAX_VALUE) && !Q.isEmpty()){
				Key<Double, Double> next = Q.poll(); 
//				System.out.println(next.getFirst()+" "+next.getSecond());
				if (next.getSecond() >= 0.5){
					pHaboveHalf = next.getFirst();
					titrAboveHalf = next.getSecond();
				} else if (next.getSecond() < 0.5){
					pHbelowHalf = next.getFirst();
					titrBelowHalf = next.getSecond();
				}
			}
//			System.out.println(pHaboveHalf+" "+titrAboveHalf);
//			System.out.println(pHbelowHalf+" "+titrBelowHalf);
//			
			double x1 = pHaboveHalf; double y1 =  titrAboveHalf;
			double x2 = pHbelowHalf; double y2 = titrBelowHalf;

			double pka = (0.5 - y1)*(x2-x1)/(y2-y1) + x1;
//			System.out.println(pka);
			
			wr.writeln(v.toString()+" "+pka);
			
		}
		
		wr.close();
		
	}
	
	class pHComparator implements Comparator<Key<Double, Double>>{

		
		public pHComparator(){
			
		}

		// Note: this comparator imposes orderings that are inconsistent with equals.
		public int compare(Key<Double, Double> a, Key<Double, Double> b){
			if (a.getFirst() < b.getFirst()){
				return -1;
			} else if (a.getFirst() == b.getFirst()){
				return 0;
			} else {
				return 1;
			}
		}
	}

}
