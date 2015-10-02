package main;

import hashtools.Key;

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Vector;

import prot.MakeProtein;
import prot.Protein;
import prot.ProteinToSystm;
import syst.Instance;
import syst.Systm;
import syst.Variable;
import mathtools.MapleTools;
import filetools.WriteFile;

public class TitrationCurves {

	public static void main(String[] args) throws FileNotFoundException{
		TitrationCurves tc = new TitrationCurves();
		tc.doIt(args, true);
	}
	
	/**
	 * @param args
	 * @throws FileNotFoundException 
	 */
	public HashMap<Variable, Vector<Key<Double, Double>>> doIt(String[] args, boolean write) throws FileNotFoundException {
		// FIXME Auto-generated method stub
		
//		For each pH and residue, find the minimum energy state 
//		(and energy value) at the given pH. Then protonate/deprotonate 
//		(change) the residue and measure the change in energy. 
//		Take exp(-change/RT)/(1+exp(-change/RT)) and plot that as the y value for x=pH. 
//		So it seems that there is a titration curve with pH on 
//		the x axis and exp(-change) on the y axis for each residue. 
		
		String in_path = args[0];
		String out_path = args[1];
		String proteinName = args[2];
		String outFile = out_path+"\\"+proteinName+".pka.out";
		WriteFile wr = new WriteFile(outFile);
		
		WriteFile wrPartOpt = new WriteFile(out_path+"\\"+proteinName+".partOpt.out", true);
		
		String pdb2pkaFile = in_path+"\\INTERACTION_MATRIX.DAT";
		String desolvFile = in_path+"\\desolvation_energies.txt";
		String backFile = in_path+"\\background_interaction_energies.txt";
		
		double[] pHs = MapleTools.seq(-15.0, 30.0, 0.1);
		
		//get start state, when pH is at its minimum
		MakeProtein test = new MakeProtein(pdb2pkaFile, desolvFile, backFile, "[ ]+", proteinName, pHs[0]);
		test.readPDB2PKA();
		
		Protein testProt = test.getProt();
		testProt.simplify(); 
		ProteinToSystm makeSys = new ProteinToSystm(testProt);
		makeSys.makeSystm();
		Systm sys = makeSys.getSystm();
		
		Vector<Variable> residues = sys.getVars();
		
		BinaryGraphCut bgc = new BinaryGraphCut(sys);
		bgc.makeBiGr(wr);
		HashMap<Variable, Instance> currState = bgc.minimizeAns(wr, wrPartOpt);
		
		HashMap<Variable, Vector<Key<Double, Double>>> titrCurves = new HashMap<Variable, Vector<Key<Double, Double>>>();
		
		for (Variable r : residues) {
			Vector<Key<Double, Double>> init = new Vector<Key<Double, Double>>();
			titrCurves.put(r, init);
		}
		
		System.out.println("pH="+pHs[0]);
		for (Variable r : residues){
			Instance rCurr = currState.get(r);
			HashMap<Variable, Instance> newState = (HashMap<Variable, Instance>) currState.clone();
			Double protEnergy = 0.0;
			Double deprotEnergy = 0.0;
			if (rCurr == null){
				protEnergy = Double.MIN_VALUE;
				deprotEnergy = Double.MIN_VALUE;
			} else if (rCurr.getLabel().equals("DEPROT")){
				Instance rPROT = r.getInstance("PROT");
				newState.put(r, rPROT);
				protEnergy = sys.evaluateEnergy(newState, true);
				deprotEnergy = sys.evaluateEnergy(currState, true);
			} else if (rCurr.getLabel().equals("PROT")){
				Instance rDEPROT = r.getInstance("DEPROT");
				newState.put(r, rDEPROT);
				protEnergy = sys.evaluateEnergy(currState, true);
				deprotEnergy = sys.evaluateEnergy(newState, true);
			} else{
				//shouldn't happen
			}
			
			Double titrValue = 0.0;
			if (protEnergy == Double.MIN_VALUE || deprotEnergy == Double.MIN_VALUE){
				titrValue = null;
			} else{
				titrValue = Math.exp(-(protEnergy - deprotEnergy)/2479)/(1+Math.exp(-(protEnergy - deprotEnergy)/2479));
				
				Vector<Key<Double, Double>> theseTitrValues = titrCurves.get(r);
				Key<Double, Double> firstTitr = new Key<Double, Double>(pHs[0], titrValue);
				theseTitrValues.add(firstTitr);
				titrCurves.put(r, theseTitrValues);
				
			}
			
		}
		
		for (int i=1; i<pHs.length; i++){
			// reset the pH value
			testProt.setPH(pHs[i]);
			makeSys = new ProteinToSystm(testProt);
			makeSys.makeSystm();
			sys = makeSys.getSystm();
			
			bgc = new BinaryGraphCut(sys);
			bgc.makeBiGr(wr);
			currState = bgc.minimizeAns(wr);
			
			System.out.println("pH="+pHs[i]);
			for (Variable r : residues){
				Instance rCurr = currState.get(r);
				HashMap<Variable, Instance> newState = (HashMap<Variable, Instance>) currState.clone();
				Double protEnergy = 0.0;
				Double deprotEnergy = 0.0;
				if (rCurr == null){
					protEnergy = Double.MIN_VALUE;
					deprotEnergy = Double.MIN_VALUE;
				} else if (rCurr.getLabel().equals("DEPROT")){
					Instance rPROT = r.getInstance("PROT");
					newState.put(r, rPROT);
					protEnergy = sys.evaluateEnergy(newState, true);
					deprotEnergy = sys.evaluateEnergy(currState, true);
				} else if (rCurr.getLabel().equals("PROT")){
					Instance rDEPROT = r.getInstance("DEPROT");
					newState.put(r, rDEPROT);
					protEnergy = sys.evaluateEnergy(currState, true);
					deprotEnergy = sys.evaluateEnergy(newState, true);
				} else{
					//shouldn't happen
				}
//				Double currEnergy = sys.evaluateEnergy(currState, true);
//				Double newEnergy = sys.evaluateEnergy(newState, true);
				
				Double titrValue = 0.0;
				if (protEnergy == Double.MIN_VALUE || deprotEnergy == Double.MIN_VALUE){
					titrValue = null;
				} else{
					//			Double currEnergy = sys.evaluateEnergy(currState, true);
					//			Double newEnergy = sys.evaluateEnergy(newState, true);

					// options: 
					// Double titrValue = Math.exp(-Math.abs(currEnergy-newEnergy)/2479);
					// Double titrValue = Math.exp(-(currEnergy-newEnergy)/2479);
					// Double titrValue = Math.exp(-(protEnergy-deprotEnergy)/2479);
					// Double titrValue = Math.exp((currEnergy-newEnergy)/2479);
					// Double titrValue = Math.exp(-Math.abs(currEnergy - newEnergy)/2479)/(1+Math.exp(-Math.abs(currEnergy - newEnergy)/2479));
					// Double titrValue = Math.exp((currEnergy - newEnergy)/2479)/(1+Math.exp((currEnergy - newEnergy)/2479));
					titrValue = Math.exp(-(protEnergy - deprotEnergy)/2479)/(1+Math.exp(-(protEnergy - deprotEnergy)/2479));

					Vector<Key<Double, Double>> theseTitrValues = titrCurves.get(r);
					Key<Double, Double> thisTitrValue = new Key<Double, Double>(pHs[i], titrValue);
					theseTitrValues.add(thisTitrValue);
					titrCurves.put(r, theseTitrValues);
					//				System.out.println(r.getName()+" "+(newEnergy-currEnergy));
				}
			}
			
		}
		
		System.out.println(titrCurves.toString());
		
		if (write){
			for (Variable r : residues){
				String outFileR = out_path+"\\"+r.getName()+".titr";
				WriteFile wrR = new WriteFile(outFileR);
				Vector<Key<Double, Double>> values = titrCurves.get(r);
				for (int i=0; i<values.size(); i++){
					wrR.writeln(values.get(i).getFirst()+", "+values.get(i).getSecond());
				}
				wrR.close();
			}
			return null;
		} else{
			return titrCurves;
		}

	}

}
