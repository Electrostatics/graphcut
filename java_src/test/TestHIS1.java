package test;

import java.io.FileNotFoundException;
import java.util.HashMap;

import prot.MakeProtein;
import prot.ProtCompl;
import prot.ProteinToSystm;
import syst.Instance;
import syst.Systm;
import syst.Variable;

public class TestHIS1 {

	public static void main(String[] args) throws FileNotFoundException {
		
		String pdb2pkaFile = "C:\\Users\\hoga886\\workspace\\ApproxEnergyMinNoHIS\\input\\2M8F\\INTERACTION_MATRIX.DAT";
		String desolvFile = "C:\\Users\\hoga886\\workspace\\ApproxEnergyMinNoHIS\\input\\2M8F\\desolvation_energies.txt";
		String backFile = "C:\\Users\\hoga886\\workspace\\ApproxEnergyMinNoHIS\\input\\2M8F\\background_interaction_energies.txt";
		MakeProtein makeProt = new MakeProtein(pdb2pkaFile,desolvFile,backFile,"[ ]+","test", 10.0);
		makeProt.readPDB2PKA();
		ProtCompl prot = (ProtCompl) makeProt.getProt();
		prot.printProtein();
		
		ProteinToSystm makeSys = new ProteinToSystm(prot);
		makeSys.makeSystm();
		Systm sys = makeSys.getSystm();
		
//		HashMap<Variable, Instance> stateOrig = new HashMap<Variable, Instance>();
		Variable NTRA1 = sys.getVar("NTR_A_1");
//		Instance NTRA1_1 = NTRA1.getInstance("1");
//		stateOrig.put(NTRA1, NTRA1_1);
//		
		Variable ASPA9 = sys.getVar("ASP_A_9");
//		Instance ASPA9_1 = ASPA9.getInstance("1");
//		stateOrig.put(ASPA9, ASPA9_1);
//		
		Variable TYRA15 = sys.getVar("TYR_A_15");
//		Instance TYRA15_1 = TYRA15.getInstance("1");
//		stateOrig.put(TYRA15, TYRA15_1);
//		
		Variable ASPA17 = sys.getVar("ASP_A_17");
//		Instance ASPA17_1 = ASPA17.getInstance("1");
//		stateOrig.put(ASPA17, ASPA17_1);
//		
		Variable HISA19 = sys.getVar("HIS_A_19");
//		Instance HISA19_12 = HISA19.getInstance("1");
//		stateOrig.put(HISA19, HISA19_12);
//		
		Variable CTRA24 = sys.getVar("CTR_A_24");
//		Instance CTRA24_2 = CTRA24.getInstance("2");
//		stateOrig.put(CTRA24, CTRA24_2);
//		
		Variable ASPA24 = sys.getVar("ASP_A_24");
//		Instance ASPA24_1 = ASPA24.getInstance("1");
//		stateOrig.put(ASPA24, ASPA24_1);
//		
//		System.out.println(stateOrig.toString());
//		
//		double eneOrig = sys.evaluateEnergy(stateOrig, false);
//		System.out.println("Original energy = "+eneOrig);

		// simplify the protein and compare the energies
		// here we're in HIS#2 option
		prot.simplify();
		prot.printProtein();

		ProteinToSystm makeSysSimp = new ProteinToSystm(prot);
		makeSysSimp.makeSystm();
		Systm sysSimp = makeSysSimp.getSystm();
		
		HashMap<Variable, Instance> state1 = new HashMap<Variable, Instance>();
		Instance NTRA1_P = NTRA1.getInstance("PROT");
		state1.put(NTRA1, NTRA1_P);
		
		Instance ASPA9_P = ASPA9.getInstance("PROT");
		state1.put(ASPA9, ASPA9_P);
		
		Instance TYRA15_P = TYRA15.getInstance("PROT");
		state1.put(TYRA15, TYRA15_P);
		
		Instance ASPA17_P = ASPA17.getInstance("PROT");
		state1.put(ASPA17, ASPA17_P);
		
		Variable HIeA19 = sysSimp.getVar("HIe_A_19");
		Instance HIeA19_D = HIeA19.getInstance("DEPROT");
		Instance HIeA19_P = HIeA19.getInstance("PROT");
		state1.put(HIeA19, HIeA19_D);
		
		Variable HIdA19 = sysSimp.getVar("HId_A_19");
		Instance HIdA19_D = HIdA19.getInstance("DEPROT");
		Instance HIdA19_P = HIdA19.getInstance("PROT");
		state1.put(HIdA19, HIdA19_P);
		
		Instance CTRA24_P = CTRA24.getInstance("PROT");
		state1.put(CTRA24, CTRA24_P);
		
		Instance ASPA24_P = ASPA24.getInstance("PROT");
		state1.put(ASPA24, ASPA24_P);
		
		System.out.println(state1.toString());
		double eneHSD = sysSimp.evaluateEnergy(state1, false);
		System.out.println("HIS1 energy (HSD) = "+eneHSD);
		
		state1.put(HIdA19, HIdA19_D);
		state1.put(HIeA19, HIeA19_P);
		System.out.println(state1.toString());
		double eneHSE = sysSimp.evaluateEnergy(state1, false);
		System.out.println("HIS1 energy (HSE) = "+eneHSE);
		
		state1.put(HIdA19, HIdA19_P);
		System.out.println(state1.toString());
		double eneHSP = sysSimp.evaluateEnergy(state1, false);
		System.out.println("HIS1 energy (HSP) = "+eneHSP);
		
		
		
		
	}

}
