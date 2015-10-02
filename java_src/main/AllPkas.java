package main;

import java.io.FileNotFoundException;

public class AllPkas {

	/**
	 * @param args
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws FileNotFoundException {
		// FIXME Auto-generated method stub

		String in_path = "C:\\Users\\hoga886\\workspace\\ApproxEnergyMin\\input";
		String out_path = "C:\\Users\\hoga886\\workspace\\ApproxEnergyMin\\output\\new_7-22-14";
		
//		String[] pdbsNew = {"1AEL"};
//		String[] pdbsNew = {"1F94","1JHG","1JKC","1L2Y","1M1Q",
//				"1OAI","1PLC","1R6J","1U07",
//				"1VBW","1WHI","2M0W","2M3F",
//				"2M8F","2O37"};
//		String[] pdbsNew = {"3BDC", "3D6C", "3DMU"};
//		String[] pdbsNew = {"3D8G"};
//		String[] pdbsNew = {"3EJI", "3ERO"};
		String[] pdbsNew = {"3EVQ"};
		
		for (String pdb : pdbsNew){
			String[] args_for_pka = {in_path+"\\"+pdb, out_path+"\\"+pdb, pdb};
			System.out.println("For pdb = "+pdb+" doing pka calculation.");
			PkaValues.main(args_for_pka);
		}
		
		
	}

}
