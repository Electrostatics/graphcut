package main;

import java.io.FileNotFoundException;
import java.util.Arrays;

import filetools.WriteFile;

import mathtools.VectorsMax;

import prot.MakeProtein;
import prot.Protein;
import prot.ProteinToSystm;
import syst.Systm;

public class BruteForceMinimizer {

	private static Systm sys;
	
	/**
	 * @param args
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws FileNotFoundException {
		// FIXME Auto-generated method stub

		long startReadTime = System.nanoTime();

		String in_path = args[0];
		String out_path = args[1];
		String proteinName = args[2];
		double pH = Double.parseDouble(args[3]);
		String outFile = out_path+"\\"+proteinName+"_"+pH+".brute.out";
		WriteFile wr = new WriteFile(outFile);
		
		String mainOutFile = "";
		WriteFile wrMain = null;
		if (args.length==5){
			mainOutFile = args[4];
			wrMain = new WriteFile(mainOutFile,true);
		}
		
		String pdb2pkaFile = in_path+"\\INTERACTION_MATRIX.DAT"; System.out.println(pdb2pkaFile);
		String desolvFile = in_path+"\\desolvation_energies.txt";
		String backFile = in_path+"\\background_interaction_energies.txt";
		MakeProtein test = new MakeProtein(pdb2pkaFile, desolvFile, backFile, "[ ]+", proteinName, pH);
		test.readPDB2PKA();

		long endReadTime = System.nanoTime();

		long startSimplifyAndSystmTime = System.nanoTime();

		Protein testProt = test.getProt();
		testProt.simplify(); 
		ProteinToSystm makeSys = new ProteinToSystm(testProt);
		makeSys.makeSystm();
		sys = makeSys.getSystm();

		long endSimplifyAndSystmTime = System.nanoTime();

//		sys.printSystm(wr);
//		sys.printEnergy(wr);
		if (!sys.isEnergySym()){
			System.out.println("\nEnergy was not symmetric, making it symmetric now...");
			sys.makeEnergySym();
		}
		System.out.println("=====================================================");

		long startMinTime = System.nanoTime();
		
		sys.bruteForceMin(wr);
		
		long endMinTime = System.nanoTime();
	
		wr.writeln("\nNumber of residues = "+ sys.getVars().size());
		wr.writeln("Read time = "+(endReadTime - startReadTime)/1000000+" milliseconds");
		wr.writeln("Simplify and make Systm time = "+(endSimplifyAndSystmTime - startSimplifyAndSystmTime)/1000000+" milliseconds");
		wr.writeln("Energy minimization time = "+(endMinTime - startMinTime)/1000000+" milliseconds");
		
		if (wrMain != null){
			wrMain.writeln("brute "+proteinName+" "+pH+" "+sys.getVars().size()+" "+
				(endReadTime - startReadTime)/1000000+" "+
				(endSimplifyAndSystmTime - startSimplifyAndSystmTime)/1000000+" "+
				(endMinTime - startMinTime)/1000000);
			wrMain.close();
		}
		
		wr.close();

	}

}
