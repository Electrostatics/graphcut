package main;

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.HashMap;

import prot.MakeProtein;
import prot.ProtCompl;
import prot.ProteinToSystm;
import syst.Instance;
import syst.Systm;
import syst.Variable;

public class EvalEnergySimplified {
	
	

	/**
	 * @param args
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws FileNotFoundException {
		// TODO Auto-generated method stub
		
		String pdb2pkaFile = args[0]; //"C:\\Users\\hoga886\\workspace\\ApproxEnergyMin\\test\\PDB2PKA\\1CRN.pdb.INTERACTION_MATRIX.DAT";
		String desolvFile = args[1]; //"C:\\Users\\hoga886\\workspace\\ApproxEnergyMin\\test\\PDB2PKA\\1CRN.pdb.nice_desolv";
		String backFile = args[2]; //"C:\\Users\\hoga886\\workspace\\ApproxEnergyMin\\test\\PDB2PKA\\1CRN.pdb.nice_background";
		
		String[][] states = new String[(args.length-3)/2][2];
		int index = 3;
		int jndex = 0;
		while (index < args.length){
			states[jndex][0] = args[index++];
			states[jndex][1] = args[index++];
			jndex++;
		}
		

		
		
		MakeProtein test = new MakeProtein(pdb2pkaFile,desolvFile,backFile,"[ ]+","test", 0.0);
		test.readPDB2PKA();
		ProtCompl testProt = (ProtCompl) test.getProt();
		testProt.simplify();
//		prot.printProtein();
//		prot.printEnergy();
		
		ProteinToSystm makeSys = new ProteinToSystm(testProt);
		makeSys.makeSystm();
		Systm sys = makeSys.getSystm();
		sys.makeNF();
		
		HashMap<Variable, Instance> labeling = new HashMap<Variable, Instance>();
		for (int i=0; i<states.length; i++){
			String res = states[i][0];
			String[] ncl = res.split("_");
			Variable var = testProt.getResidue(ncl[0], ncl[1], ncl[2]); //TODO: split states[i][0] on "_" and pick out the name/chain/loc information
			Instance inst = var.getInstance(states[i][1]);
			labeling.put(var, inst);
		}
//		System.out.println(labeling.toString());
		
		double energy = sys.evaluateEnergy(labeling, true);
		
		System.out.println(energy);
		
		
		
	}

}
