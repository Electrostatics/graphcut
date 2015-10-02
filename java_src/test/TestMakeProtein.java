package test;

import java.io.FileNotFoundException;

import prot.MakeProtein;
import prot.ProtCompl;

public class TestMakeProtein {

	/**
	 * @param args
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws FileNotFoundException {
		// TODO Auto-generated method stub
		String pdb2pkaFile = "C:\\Users\\hoga886\\workspace\\ApproxEnergyMin\\input\\2M8F\\INTERACTION_MATRIX.DAT";
		String desolvFile = "C:\\Users\\hoga886\\workspace\\ApproxEnergyMin\\input\\2M8F\\desolvation_energies.txt";
		String backFile = "C:\\Users\\hoga886\\workspace\\ApproxEnergyMin\\input\\2M8F\\background_interaction_energies.txt";
		MakeProtein makeProt = new MakeProtein(pdb2pkaFile,desolvFile,backFile,"[ ]+","test", 5.0);
		makeProt.readPDB2PKA();
		ProtCompl prot = (ProtCompl) makeProt.getProt();
//		prot.printProtein();
//		prot.printEnergy();

		// gets rid of single instance variables (residues), 
		// consolidates >2 instance variables into two instances, 
		// divides each HIS into two residues
		prot.simplify();  
		
		prot.printProtein();
		prot.printEnergy();
		
		prot.setPH(7.0);
		
		prot.printProtein();
		prot.printEnergy();
		
////		prot.setPH(-14.0);
////		
////		prot.printProtein();
////		prot.printEnergy();
////		
		
		MakeProtein makeProt2 = new MakeProtein(pdb2pkaFile,desolvFile,backFile,"[ ]+","test", 7.0);
		makeProt2.readPDB2PKA();
		ProtCompl prot2 = (ProtCompl) makeProt2.getProt();
//		prot2.printProtein();
//		prot2.printEnergy();
		
		prot2.simplify();  
		prot2.printProtein();
		prot2.printEnergy();

		
	}

}
