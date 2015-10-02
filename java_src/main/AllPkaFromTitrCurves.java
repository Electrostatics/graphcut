package main;

import java.io.FileNotFoundException;

public class AllPkaFromTitrCurves {

	public static void main(String[] args) throws FileNotFoundException {
		// TODO Auto-generated method stub
		String in_path = "C:\\Users\\hoga886\\workspace\\ApproxEnergyMin\\input";
		String out_path = "C:\\Users\\hoga886\\workspace\\ApproxEnergyMin\\output\\titration-curves-actuallyfixed";
		
		String[] pdbsNew = {//"1AEL",
//				"1F94","1JHG","1JKC","1L2Y","1M1Q",
//						"1OAI","1PLC","1R6J","1U07",
//						"1VBW","1WHI","2M0W","2M3F",
//						"2M8F","2O37",
						"4CZ3", "2MKB", "2M6J", "2RT4",
						"3BDC", "3D6C", "3DMU",
				"3D8G",
				"3EJI", "3ERO",
				"3EVQ"
				};
		
//		String[] pdbsNew = {"4CZ3", "2MKB"};
//		String[] pdbsNew = {"2M6J", "2RT4"};
		
		for (String pdb : pdbsNew){
			String[] args_for_titr = {in_path+"\\"+pdb, out_path+"\\"+pdb, pdb};
			System.out.println("For pdb = "+pdb+" doing titration curve calculation.");
			PkaFromTitrationCurve.main(args_for_titr);
		}
	}

}
