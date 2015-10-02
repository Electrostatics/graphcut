package main;

import java.io.FileNotFoundException;

import filetools.WriteFile;

public class TimeExperiment {

	/**
	 * @param args
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws FileNotFoundException {
		// FIXME Auto-generated method stub
		
		String file = "C:\\Users\\hoga886\\workspace\\ApproxEnergyMin\\output\\titration-curves-fixed\\time.out";
		
		String in_path = "C:\\Users\\hoga886\\workspace\\ApproxEnergyMin\\input";
		String out_path = "C:\\Users\\hoga886\\workspace\\ApproxEnergyMin\\output\\titration-curves-fixed";
//		String[] pdbsNew = {"1F94","1JHG","1JKC","1L2Y","1M1Q",
//				"1OAI","1PLC","1R6J","1U07",
//				"1VBW","1WHI","2M0W","2M3F",
//				"2M8F","2O37",
//		"3BDC", "3D6C", "3DMU",
//		"3D8G",
//		"3EJI", "3ERO",
//		"3EVQ"};
		
//		String[] pdbsNew = {"4CZ3", "2MKB"};
		String[] pdbsNew = {"2M6J", "2RT4"};
		
		double[] pHs = {0.0, 2.0, 4.0, 6.0, 8.0, 10.0, 12.0, 14.0, 16.0, 18.0, 20.0};
		
		for (String pdb : pdbsNew){
			for (double ph : pHs){
				System.out.println("For pH = "+ph+" doing...");
				String[] argus = {in_path+"\\"+pdb, out_path+"\\"+pdb, pdb, Double.toString(ph), file};
				System.out.println("\tSmart energy min...");
				EnergyMinimizer.main(argus);
				System.out.println("\tBrute energy min...");
				BruteForceMinimizer.main(argus);
			}
		}

	}

}
