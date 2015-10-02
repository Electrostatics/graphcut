package main;

import java.io.FileNotFoundException;

public class TimeAndPkas {

	/**
	 * Does the full time and pKa calculation experiment. No arguments, 
	 * proteins are hard coded in here (and can obviously be changed, but remain hard coded).
	 * Arguments for PkaValues are created that main method is run through this.
	 * @param args
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws FileNotFoundException {
		// FIXME Auto-generated method stub

		String mainOutFile = "C:\\Users\\hoga886\\workspace\\ApproxEnergyMin\\output\\time2.out";
		
		String in_path = "C:\\Users\\hoga886\\workspace\\ApproxEnergyMin\\input";
		String out_path = "C:\\Users\\hoga886\\workspace\\ApproxEnergyMin\\output\\new";
		
		String[] pdbs1 = {"1c75", "1f94", "1i27", "1jhg", "1lkk"};
		String[] pdbs2 = {"1m1q", "1oai", "1plc", "1r6j", "1rro"};
		String[] pdbs3 = {"1u07", "1vbw", "1whi", "1xmk", "1y6x"};
		String[] pdbs4 = {"2M0W", "2M3F", "2M8F", "2MFM", "2MFO"};
		
		String[] pdbsBMA = {"3BDC", "3D6C", "3D8G", "3DMU", "3EJI", "3ERO", "3ERQ", "3EVQ"};
		
//		String path2 = "C:\\Users\\hoga886\\workspace\\ApproxEnergyMin\\input\\BMA";
//		String[] pdbs2 = {"3bdc", "3d6c", "3d8g", "3dmu", "3eji", "3ero", "3erq", "3evq"};
//		String[] pdbs2 = {"3bdc"};
		
		double[] pHs = {0.0, 2.0, 4.0, 6.0, 8.0, 10.0, 12.0, 14.0, 16.0, 18.0, 20.0};
		
		for (String pdb : pdbs2){
			for (double ph : pHs){
				System.out.println("For pdb = "+pdb+", and pH = "+ph+" doing...");
				String[] args_for_min = {in_path+"\\"+pdb, out_path+"\\"+pdb, pdb, Double.toString(ph), mainOutFile};
				System.out.println("\tSmart energy min...");
				EnergyMinimizer.main(args_for_min);
				System.out.println("\tBrute energy min...");
				BruteForceMinimizer.main(args_for_min);
			}
			
			String[] args_for_pka = {in_path+"\\"+pdb, out_path+"\\"+pdb, pdb};
			System.out.println("For pdb = "+pdb+" doing pka calculation.");
			PkaValues.main(args_for_pka);
		}
		for (String pdb : pdbs3){
			for (double ph : pHs){
				System.out.println("For pdb = "+pdb+", and pH = "+ph+" doing...");
				String[] args_for_min = {in_path+"\\"+pdb, out_path+"\\"+pdb, pdb, Double.toString(ph), mainOutFile};
				System.out.println("\tSmart energy min...");
				EnergyMinimizer.main(args_for_min);
				System.out.println("\tBrute energy min...");
				BruteForceMinimizer.main(args_for_min);
			}
			
			String[] args_for_pka = {in_path+"\\"+pdb, out_path+"\\"+pdb, pdb};
			System.out.println("For pdb = "+pdb+" doing pka calculation.");
			PkaValues.main(args_for_pka);
		}
		for (String pdb : pdbs4){
			for (double ph : pHs){
				System.out.println("For pdb = "+pdb+", and pH = "+ph+" doing...");
				String[] args_for_min = {in_path+"\\"+pdb, out_path+"\\"+pdb, pdb, Double.toString(ph), mainOutFile};
				System.out.println("\tSmart energy min...");
				EnergyMinimizer.main(args_for_min);
				System.out.println("\tBrute energy min...");
				BruteForceMinimizer.main(args_for_min);
			}
			
			String[] args_for_pka = {in_path+"\\"+pdb, out_path+"\\"+pdb, pdb};
			System.out.println("For pdb = "+pdb+" doing pka calculation.");
			PkaValues.main(args_for_pka);
		}
		
	}

}
