package main;

import filetools.WriteFile;
import hashtools.TwoKeyHash;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Vector;

import prot.MakeProtein;
import prot.ProtSimple;
import prot.Protein;
import prot.ProteinToSystm;

import returntools.Tuple2;
import syst.Instance;
import syst.Systm;
import syst.Variable;

public class EnergyMinimizer {
	
	private static Systm sys;

	/**
	 * @param args
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws FileNotFoundException{
		// TODO Auto-generated method stub
		
//		File file  = new File("C:\\Users\\hoga886\\Documents\\_Projects\\Electrostatics\\Elec_workspace\\ApproxEnergyMin\\out\\1crn_test.out");
//		PrintStream printStream = null;
//		try{
//			printStream = new PrintStream(new FileOutputStream(file));
//		}catch(FileNotFoundException fnfe){
//			System.out.println("couldn't find the file to write to");
//		}
//
//	    System.setOut(printStream);

//		MakeProtein test = new MakeProtein(true);
//		test.makeTestProtNoHIS();

//		String summaryFile = "C:\\Users\\hoga886\\Documents\\_Projects\\Electrostatics\\Elec_workspace\\ProteinEnergy\\test\\frompaper1\\1crn.summary";
//		String resinterFile = "C:\\Users\\hoga886\\Documents\\_Projects\\Electrostatics\\Elec_workspace\\ProteinEnergy\\test\\frompaper1\\1crn.newresinter";
//		MakeProtein test = new MakeProtein(summaryFile, resinterFile, "[ ]", "1crn");
//		try{
//			test.readProt();
//		}catch(FileNotFoundException fnfe){
//			System.out.println("couldnt find a file or something");
//		}
		
//		String pdb2pkaFile = "C:\\Users\\hoga886\\workspace\\ApproxEnergyMin\\test\\PDB2PKA\\1a1p.matrix";
		
		long startReadTime = System.nanoTime();
		
		String in_path = args[0];
		String out_path = args[1];
		String proteinName = args[2];
		double pH = Double.parseDouble(args[3]);
		String outFile = out_path+"\\"+proteinName+"_"+pH+".cut.out";
		WriteFile wr = new WriteFile(outFile);
		
		String mainOutFile = "";
		WriteFile wrMain = null;
		if (args.length==5){
			mainOutFile = args[4];
			wrMain = new WriteFile(mainOutFile,true);
		}
		
		String pdb2pkaFile = in_path+"\\INTERACTION_MATRIX.DAT";
		String desolvFile = in_path+"\\desolvation_energies.txt";
		String backFile = in_path+"\\background_interaction_energies.txt";
	
		MakeProtein test = new MakeProtein(pdb2pkaFile, desolvFile, backFile, "[ ]+", "dummy", pH);
		test.readPDB2PKA();
		
		long endReadTime = System.nanoTime();
		
		long startSimplifyAndSystmTime = System.nanoTime();
		
		Protein testProt = test.getProt();
		testProt.simplify(); 
		ProteinToSystm makeSys = new ProteinToSystm(testProt);
		makeSys.makeSystm();
		sys = makeSys.getSystm();
		
		long endSimplifyAndSystmTime = System.nanoTime();
		
		sys.printSystm(wr);
		sys.printEnergy(wr);
		if (!sys.isEnergySym()){
			wr.writeln("\nEnergy was not symmetric, making it symmetric now...");
			sys.makeEnergySym();
		}
		wr.writeln("=====================================================");

		long startMinTime = System.nanoTime();
		
		BinaryGraphCut bgc = new BinaryGraphCut(sys);
		bgc.makeBiGr(wr);
		bgc.minimize(wr);
		
		long endMinTime = System.nanoTime();
		
		wr.writeln("\nNumber of residues = "+ sys.getVars().size());
		wr.writeln("Read time = "+(endReadTime - startReadTime)/1000000+" milliseconds");
		wr.writeln("Simplify and make Systm time = "+(endSimplifyAndSystmTime - startSimplifyAndSystmTime)/1000000+" milliseconds");
		wr.writeln("Energy minimization time = "+(endMinTime - startMinTime)/1000000+" milliseconds");
		
		if (wrMain != null){
			wrMain.writeln("cut "+proteinName+" "+pH+" "+sys.getVars().size()+" "+
				(endReadTime - startReadTime)/1000000+" "+
				(endSimplifyAndSystmTime - startSimplifyAndSystmTime)/1000000+" "+
				(endMinTime - startMinTime)/1000000);
			wrMain.close();
		}
		
		wr.close();
		
//		doExpansion();

//		printStream.close();

//		sys = new Systm();
//		makeExample();
//		doExpansion();
		
//		HashMap<Variable, Instance> initLab = new HashMap<Variable, Instance>();
//		Variable v1 = sys.getVar("1");
//		initLab.put(v1, v1.getInstance("beta"));
//		
//		Variable v2 = sys.getVar("2");
//		initLab.put(v2, v2.getInstance("alpha"));
//		
//		Variable v3 = sys.getVar("3");
//		initLab.put(v3, v3.getInstance("beta"));
//		
//		Variable v4 = sys.getVar("4");
//		initLab.put(v4, v4.getInstance("gamma"));
//		
//		AlphaExpansion ae = new AlphaExpansion(sys, initLab, "beta");
//		HashMap<Variable, Instance> newLab = ae.doExpansion();
//		
//		System.out.println(newLab.toString());
//		
//		AlphaExpansion ae2 = new AlphaExpansion(sys, newLab, "gamma");
//		HashMap<Variable, Instance> newLab2 = ae2.doExpansion();
//		
//		System.out.println(newLab2.toString());

	}

	/**
	 * Do the iterated alpha-expansion until the labeling doesn't change
	 */
	@SuppressWarnings("unchecked")
	private static void doExpansion(){
		//Start with a random labeling of the Systm
		HashMap<Variable, Instance> lab = sys.randomLabeling();
		
		//Choose alpha (for the alpha-expansion) randomly
		Vector<String> allLabels = sys.allLabels();
		int r = (int) Math.round((allLabels.size()-1)*Math.random());
		String alpha = allLabels.get(r);
		
		//The starting energy for the Systm with the random labeling
		double energy = sys.evaluateEnergy(lab, false);
		System.out.println(lab.toString()+" with energy "+energy);
		
		//Create the alpha-expansion object
		AlphaExpansion ae = new AlphaExpansion(sys,lab,alpha);
		
		//Do the alpha-expansion and get the new energy of the Systm
		HashMap<Variable, Instance> newLab = ae.doExpansion();
		double newEnergy = sys.evaluateEnergy(newLab, false);
		System.out.println(newLab.toString()+" with energy "+newEnergy);
		
		//Until the new labeling (after the alpha-expansion) is the same
		//as the original labeling we continue to do alpha-expansions
		while (!newLab.equals(lab)){
			//Let lab be the new labeling.
			lab = (HashMap<Variable, Instance>) newLab.clone();
			
			//Pick the next alpha making sure that it is different
			//from the current one
			String alpha1;
			do{
				r = (int) Math.round((allLabels.size()-1)*Math.random());
				alpha1 = allLabels.get(r);
			}while (alpha.equals(alpha1));
			alpha = alpha1;
			
			//Do the next alpha-expansion, and get the new energy
			ae = new AlphaExpansion(sys,lab,alpha);
			newLab = ae.doExpansion();
			newEnergy = sys.evaluateEnergy(newLab, false);
			System.out.println(newLab.toString()+" with energy "+newEnergy);
		}
		
	}
	
	/**
	 * Instantiate the example Systm
	 */
	private static void makeExample(){
		System.out.println("Setting up the example");
		
		String[] labels = {"alpha","beta","gamma"};
		
		Variable v1 = new Variable("1", labels);
		Variable v2 = new Variable("2", labels);
		Variable v3 = new Variable("3", labels);
		Variable v4 = new Variable("4", labels);
		
		sys.addVar(v1); sys.addVar(v2); sys.addVar(v3); sys.addVar(v4);
		//sys.printSystm();
		
		Instance i1a = v1.getInstance(labels[0]); i1a.setEnergy(-2);
		Instance i1b = v1.getInstance(labels[1]); i1b.setEnergy(10);
		Instance i1c = v1.getInstance(labels[2]); i1c.setEnergy(-2);
		
		Instance i2a = v2.getInstance(labels[0]); i2a.setEnergy(2);
		Instance i2b = v2.getInstance(labels[1]); i2b.setEnergy(8);
		Instance i2c = v2.getInstance(labels[2]); i2c.setEnergy(-1);
		
		Instance i3a = v3.getInstance(labels[0]); i3a.setEnergy(2);
		Instance i3b = v3.getInstance(labels[1]); i3b.setEnergy(8);
		Instance i3c = v3.getInstance(labels[2]); i3c.setEnergy(-1);
		
		Instance i4a = v4.getInstance(labels[0]); i4a.setEnergy(4);
		Instance i4b = v4.getInstance(labels[1]); i4b.setEnergy(-2);
		Instance i4c = v4.getInstance(labels[2]); i4c.setEnergy(1);
		
		TwoKeyHash<Instance, Double> bi = new TwoKeyHash<Instance, Double>();
		bi.put(i1a, i2a, new Double(4));         bi.put(i2a, i1a, new Double(4)); 
		bi.put(i1a, i2b, new Double(5));         bi.put(i2b, i1a, new Double(5)); 
		bi.put(i1a, i2c, new Double(-5));        bi.put(i2c, i1a, new Double(-5));
		bi.put(i1b, i2a, new Double(-9));        bi.put(i2a, i1b, new Double(-9));
		bi.put(i1b, i2b, new Double(1));         bi.put(i2b, i1b, new Double(1)); 
		bi.put(i1b, i2c, new Double(-7));        bi.put(i2c, i1b, new Double(-7));
		bi.put(i1c, i2a, new Double(-5));        bi.put(i2a, i1c, new Double(-5));
		bi.put(i1c, i2b, new Double(-6));        bi.put(i2b, i1c, new Double(-6));
		bi.put(i1c, i2c, new Double(6));         bi.put(i2c, i1c, new Double(6)); 
		
		bi.put(i1a, i3a, new Double(-3));        bi.put(i3a, i1a, new Double(-3)); 
		bi.put(i1a, i3b, new Double(-6));        bi.put(i3b, i1a, new Double(-6)); 
		bi.put(i1a, i3c, new Double(-9));        bi.put(i3c, i1a, new Double(-9)); 
		bi.put(i1b, i3a, new Double(-7));        bi.put(i3a, i1b, new Double(-7)); 
		bi.put(i1b, i3b, new Double(9));         bi.put(i3b, i1b, new Double(9));  
		bi.put(i1b, i3c, new Double(0));         bi.put(i3c, i1b, new Double(0));  
		bi.put(i1c, i3a, new Double(8));         bi.put(i3a, i1c, new Double(8));  
		bi.put(i1c, i3b, new Double(3));         bi.put(i3b, i1c, new Double(3));  
		bi.put(i1c, i3c, new Double(7));         bi.put(i3c, i1c, new Double(7));  
		
		bi.put(i1a, i4a, new Double(4));         bi.put(i4a, i1a, new Double(4));   
		bi.put(i1a, i4b, new Double(-3));        bi.put(i4b, i1a, new Double(-3));  
		bi.put(i1a, i4c, new Double(-1));        bi.put(i4c, i1a, new Double(-1));  
		bi.put(i1b, i4a, new Double(3));         bi.put(i4a, i1b, new Double(3));   
		bi.put(i1b, i4b, new Double(1));         bi.put(i4b, i1b, new Double(1));   
		bi.put(i1b, i4c, new Double(-2));        bi.put(i4c, i1b, new Double(-2));  
		bi.put(i1c, i4a, new Double(2));         bi.put(i4a, i1c, new Double(2));   
		bi.put(i1c, i4b, new Double(5));         bi.put(i4b, i1c, new Double(5));   
		bi.put(i1c, i4c, new Double(-10));       bi.put(i4c, i1c, new Double(-10)); 
		
		bi.put(i2a, i3a, new Double(-5));        bi.put(i3a, i2a, new Double(-5));  
		bi.put(i2a, i3b, new Double(2));         bi.put(i3b, i2a, new Double(2));   
		bi.put(i2a, i3c, new Double(3));         bi.put(i3c, i2a, new Double(3));   
		bi.put(i2b, i3a, new Double(-4));        bi.put(i3a, i2b, new Double(-4));  
		bi.put(i2b, i3b, new Double(-4));        bi.put(i3b, i2b, new Double(-4));  
		bi.put(i2b, i3c, new Double(-8));        bi.put(i3c, i2b, new Double(-8));  
		bi.put(i2c, i3a, new Double(3));         bi.put(i3a, i2c, new Double(3));   
		bi.put(i2c, i3b, new Double(4));         bi.put(i3b, i2c, new Double(4));   
		bi.put(i2c, i3c, new Double(4));         bi.put(i3c, i2c, new Double(4));   
		
		bi.put(i2a, i4a, new Double(8));         bi.put(i4a, i2a, new Double(8));   
		bi.put(i2a, i4b, new Double(10));        bi.put(i4b, i2a, new Double(10));  
		bi.put(i2a, i4c, new Double(1));         bi.put(i4c, i2a, new Double(1));   
		bi.put(i2b, i4a, new Double(8));         bi.put(i4a, i2b, new Double(8));   
		bi.put(i2b, i4b, new Double(-3));        bi.put(i4b, i2b, new Double(-3));  
		bi.put(i2b, i4c, new Double(-8));        bi.put(i4c, i2b, new Double(-8));  
		bi.put(i2c, i4a, new Double(6));         bi.put(i4a, i2c, new Double(6));   
		bi.put(i2c, i4b, new Double(6));         bi.put(i4b, i2c, new Double(6));   
		bi.put(i2c, i4c, new Double(-8));        bi.put(i4c, i2c, new Double(-8));  
		
		bi.put(i3a, i4a, new Double(-6));        bi.put(i4a, i3a, new Double(-6)); 
		bi.put(i3a, i4b, new Double(6));         bi.put(i4b, i3a, new Double(6));  
		bi.put(i3a, i4c, new Double(-1));        bi.put(i4c, i3a, new Double(-1)); 
		bi.put(i3b, i4a, new Double(-1));        bi.put(i4a, i3b, new Double(-1));  
		bi.put(i3b, i4b, new Double(-9));        bi.put(i4b, i3b, new Double(-9)); 
		bi.put(i3b, i4c, new Double(-3));        bi.put(i4c, i3b, new Double(-3)); 
		bi.put(i3c, i4a, new Double(9));         bi.put(i4a, i3c, new Double(9));  
		bi.put(i3c, i4b, new Double(4));         bi.put(i4b, i3c, new Double(4));  
		bi.put(i3c, i4c, new Double(-9));        bi.put(i4c, i3c, new Double(-9)); 
		
		sys.setBinary(bi);
		sys.setConstant(0);
	}
	
}
