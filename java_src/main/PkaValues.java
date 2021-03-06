package main;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Vector;

import mathtools.MapleTools;
import prot.MakeProtein;
import prot.Protein;
import prot.ProteinToSystm;
import syst.Instance;
import syst.Systm;
import syst.Variable;
import filetools.WriteFile;

public class PkaValues {

//	private static Systm sys;

	/**
	 * Calculates pKa values for a given protein. Interaction, background, and desolvation
	 * energies are given in three separate files. The arguments should be:
	 * args[0] = the path to the input files so that "path+\\+args[2]+\\+.pdb.INTERACTION_MATRIX.DAT" is the interaction energy file, 
	 * "path+\\+args[2]+\\+.pdb.nice_desolv" is the desolvation energy file, and "path+\\+args[2]+\\+.pdb.nice_background" is the
	 * background energy file
	 * args[1] = out_path where you want the output to go. output files are named automatically
	 * args[2] = protein name
	 * @param args
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws FileNotFoundException {
		// FIXME Auto-generated method stub
		
		String in_path = args[0];
		String out_path = args[1];
		String proteinName = args[2];
		String outFile = out_path+"\\"+proteinName+".pka.out";
		WriteFile wr = new WriteFile(outFile);
		
		WriteFile wrPartOpt = new WriteFile(out_path+"\\"+proteinName+".partOpt.out", true);
		
		String pdb2pkaFile = in_path+"\\INTERACTION_MATRIX.DAT";
		String desolvFile = in_path+"\\desolvation_energies.txt";
		String backFile = in_path+"\\background_interaction_energies.txt";
		
		double[] pHs = MapleTools.seq(0.0, 20.0, 0.1);
		
		wr.writeln("********************** pH = 0.0 **********************");
		//get start state, when pH=0.0
		MakeProtein test = new MakeProtein(pdb2pkaFile, desolvFile, backFile, "[ ]+", proteinName, 0.0);
		test.readPDB2PKA();
		
		Protein testProt = test.getProt();
		testProt.simplify(); 
		ProteinToSystm makeSys = new ProteinToSystm(testProt);
		makeSys.makeSystm();
		Systm sys = makeSys.getSystm();
		
		Vector<Variable> residues = sys.getVars();
		
		long startPkaTime = System.nanoTime();
		
		BinaryGraphCut bgc = new BinaryGraphCut(sys);
		bgc.makeBiGr(wr);
		HashMap<Variable, Instance> currState = bgc.minimizeAns(wr,wrPartOpt);
		HashMap<Variable, Instance> prevState;
		
		HashMap<Variable, Double> pkas = new HashMap<Variable, Double>();
		
	
		for (int i=1; i<pHs.length; i++){
			wr.writeln("********************** pH = "+pHs[i]+" **********************");
			prevState = new HashMap<Variable, Instance>(currState);
			currState = null;
//			test = new MakeProtein(pdb2pkaFile, desolvFile, backFile, "[ ]+", proteinName, pHs[i]);
//			test.readPDB2PKA();
//			
//			testProt = test.getProt();
//			testProt.simplify();
			testProt.setPH(pHs[i]);
			makeSys = new ProteinToSystm(testProt);
			makeSys.makeSystm();
			sys = makeSys.getSystm();
			
//			residues = sys.getVars();
			
			bgc = new BinaryGraphCut(sys);
			bgc.makeBiGr(wr);
			currState = bgc.minimizeAns(wr,wrPartOpt);
			
			for (Variable res : residues){
				Instance currRes = currState.get(res);
				Instance prevRes = prevState.get(res);
				try{
					if (pHs[i-1] == 0.0 && currRes.getLabel().equals("DEPROT")){
						pkas.put(res, -1.0);
					}
					if (!prevRes.equals(currRes)){
						if (currRes.getLabel().equals("DEPROT") && prevRes.getLabel().equals("PROT")){
							pkas.put(res, pHs[i]);
						}
					}
				}catch(NullPointerException npe){
					// this happens if the current state doesn't have all
					// residues covered, if brute was too high for example
					currState.put(res, prevRes);
				}
			}
		}
		
		long endPkaTime = System.nanoTime();
		
		for (Variable res : residues){
			if (!pkas.containsKey(res)){
				wr.writeln(res.toString()+", "+(999.9));
			} else{
				wr.writeln(res.toString()+", "+pkas.get(res));
			}
		}
		
		wr.writeln("Time, "+(endPkaTime - startPkaTime)+", Res "+residues.size());
		
		
		System.out.println(pkas.toString());
		
		wrPartOpt.close();
		wr.close();
		
	}

}
