package prot;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Arrays;
import java.util.Scanner;

import hashtools.TwoKeyHash;
import syst.Instance;
import syst.Variable;

public class MakeProtein {
	
	private Protein prot;
	private File fResinter = null;
	private File fSummary = null;
	private File fPDB2PKAinter = null;
	private File fPDB2PKAdesolv = null;
	private File fPDB2PKAbackground = null;
	private String delims;
	private String name;
	
	
	/* * * * * * * * * *
	 *  CONSTRUCTORS   *
	 * * * * * * * * * */
	
	public MakeProtein(boolean simple){
		if (simple)
			prot = new ProtSimple();
		else
			prot = new ProtCompl();
	}
	
	public MakeProtein(String summaryFile, String resinterFile, String d, String name){
		
		fResinter = new File(resinterFile);
		fSummary = new File(summaryFile);
		delims = d;
		this.name = name;
		
		prot = new ProtSimple(name);
		
	}
	
	public MakeProtein(String pdb2pkaFile, String desolv, String background, String d, String name){
		fPDB2PKAinter = new File(pdb2pkaFile);
		fPDB2PKAdesolv = new File (desolv);
		fPDB2PKAbackground = new File(background);
		delims = d;
		this.name = name;
		
		prot = new ProtCompl(name);
	}
	
	public MakeProtein(String pdb2pkaFile, String desolv, String background, String d, String name, double pH){
		fPDB2PKAinter = new File(pdb2pkaFile);
		fPDB2PKAdesolv = new File (desolv);
		fPDB2PKAbackground = new File(background);
		delims = d;
		this.name = name;
		
		prot = new ProtCompl(name, pH);
	}
	
	public MakeProtein(Protein prot, String pdb2pkaFile, String desolv, String background, String d, String name, double pH){
		fPDB2PKAinter = new File(pdb2pkaFile);
		fPDB2PKAdesolv = new File (desolv);
		fPDB2PKAbackground = new File(background);
		delims = d;
		this.name = name;
		
		this.prot = prot;
		
	}
	
	/* * * * * * * * * *
	 *  MAIN METHOD    *
	 * * * * * * * * * */
	
	public static void main(String[] args){
		MakeProtein test = new MakeProtein(true);
		Protein testProt = test.getProt();
		//testProt = new ProtSimple("test");
		
		test.makeTestProt();
		testProt.printProtein();
		testProt.printEnergy();
	}
	
	/* * * * * * * * * * *
	 *  GETTERS/SETTERS  *
	 * * * * * * * * * * */
	
	public Protein getProt(){
		return prot;
	}
	
	/* * * * * * * * * *
	 *  READ PROTEIN   *
	 * * * * * * * * * */
	
	public void processResidueLine(String line){
		Scanner scn = new Scanner(line);
		scn.useDelimiter(delims);
		
		if (scn.hasNext()){
			String res = scn.next();
			String chain = scn.next();
			String loc = scn.next();
			
			prot.addResidueSimple(res, chain, loc);
			
		}else{
			System.out.println("Empty or invalid line. Unable to process.");
		}
		scn.close();	
	}
	
	public void processInteractionLine(String line){
		Scanner scn = new Scanner(line);
		scn.useDelimiter(delims);
		
		if (line.startsWith("#")){
			return;
		}
		
		if (scn.hasNext()){
			String grp1 = scn.next(); String chain1 = scn.next(); String loc1 = scn.next();
			String grp2 = scn.next(); String chain2 = scn.next(); String loc2 = scn.next();
			Double energy = Double.parseDouble(scn.next());
			
			prot.addInstance(grp1, chain1, loc1);
			prot.addInstance(grp2, chain2, loc2);
			
			Instance i1 = prot.getInstance(grp1, chain1, loc1);
			Instance i2 = prot.getInstance(grp2, chain2, loc2);
			
			prot.setEnergy(i1, i2, energy);
			
		}else{
			System.out.println("Empty or invalid line. Unable to process.");
		}
		scn.close();
	}
	
	public void readProt() throws FileNotFoundException{
		Scanner scannerRes = new Scanner(new FileReader(fSummary));
		scannerRes.useDelimiter(delims);
				
		try{
			scannerRes.nextLine(); //first line of the residue file is a key to the list of amino acids which we ignore
			while ( scannerRes.hasNextLine() ){
				String nxt = scannerRes.nextLine();
				processResidueLine(nxt);
			}
		}finally {
			//ensure the underlying stream is always closed
			//this only has any effect if the item passed to the Scanner
			//constructor implements Closeable (which it does in this case).
			scannerRes.close();
		}
		
		// Parse the .resinter file containing the PDB2PQR energies
		Scanner scannerEne = new Scanner(new FileReader(fResinter));
		scannerEne.useDelimiter(delims);
		
		try{
			while ( scannerEne.hasNextLine() ){
				String nxt = scannerEne.nextLine();
				processInteractionLine(nxt);
			}
		}finally {
			//ensure the underlying stream is always closed
			//this only has any effect if the item passed to the Scanner
			//constructor implements Closeable (which it does in this case).
			scannerEne.close();
		}
	}
	
	/**
	 * Reads in the PDB2PKA generated files *pdb.INTERACTION_MATRIX.DAT, 
	 * *pdb.nice_desolv, and *pdb.nice_background. Saves the protein in
	 * the ProteinCompl object for this class instance.
	 * @throws FileNotFoundException
	 */
	public void readPDB2PKA() throws FileNotFoundException{
		Scanner scn = new Scanner(new FileReader(fPDB2PKAinter));
		scn.useDelimiter(delims);
		try{
			scn.nextLine(); //first line says "Interaction energy matrix"
			scn.nextLine(); //second line has column labels
			while (scn.hasNextLine()){
				String nxt = scn.nextLine();
				processPDB2PKALine(nxt);
				
			}
		}finally{
			//ensure the underlying stream is always closed
			//this only has any effect if the item passed to the Scanner
			//constructor implements Closeable (which it does in this case).
			scn.close();
		}
		
//		prot.printProtein();
		
		Scanner scnDe = new Scanner(new FileReader(fPDB2PKAdesolv));
		scn.useDelimiter(delims);
		try{
			while(scnDe.hasNextLine()){
				String nxt = scnDe.nextLine();
				processPDB2PKAdesolvLine(nxt);
			}
		}finally{
			//ensure the underlying stream is always closed
			//this only has any effect if the item passed to the Scanner
			//constructor implements Closeable (which it does in this case).
			scnDe.close();
		}
		
		Scanner scnBack = new Scanner(new FileReader(fPDB2PKAbackground));
		scn.useDelimiter(delims);
		try{
			while(scnBack.hasNextLine()){
				String nxt = scnBack.nextLine();
				processPDB2PKAbackgroundLine(nxt);
			}
		}finally{
			//ensure the underlying stream is always closed
			//this only has any effect if the item passed to the Scanner
			//constructor implements Closeable (which it does in this case).
			scnBack.close();
		}
	}
	
	private void processPDB2PKALine(String line){
		Scanner scn = new Scanner(line);
		scn.useDelimiter(delims);
		if (scn.hasNext()){
			String grp1 = scn.next();
			String grp2 = scn.next();
			String grp1state = scn.next();
			String grp2state = scn.next();
			double inter1 = Double.parseDouble(scn.next());
			double inter2 = Double.parseDouble(scn.next());
			double interAvg = Double.parseDouble(scn.next());
			
			String[] grp1parts = parsePDB2PKAGroup(grp1); prot.addResidue(grp1parts[grp1parts.length-1], grp1parts[1], grp1parts[2]);
			String[] grp2parts = parsePDB2PKAGroup(grp2); prot.addResidue(grp2parts[grp2parts.length-1], grp2parts[1], grp2parts[2]);
			
//			System.out.println(Arrays.toString(grp1parts));
			
			Variable res1 = prot.getResidue(grp1parts[grp1parts.length-1], grp1parts[1], grp1parts[2]);
			Variable res2 = prot.getResidue(grp2parts[grp2parts.length-1], grp2parts[1], grp2parts[2]);
			Instance ins1 = res1.getInstance(grp1state);
			Instance ins2 = res2.getInstance(grp2state); 
			
			
			prot.setEnergy(
					ins1, //prot.getInstance(grp1state, grp1parts[1], grp1parts[2]), 
					ins2, //prot.getInstance(grp2state, grp2parts[1], grp2parts[2]), 
					interAvg);
			
		}else{
			System.err.println("Empty or invalid line. Unable to process: "+line);
		}
		scn.close();
	}
	
	private String[] parsePDB2PKAGroup(String grp){
		String[] parts = grp.split("[_:]");
		return parts;
	}
	
	private void processPDB2PKAdesolvLine(String line){
		Scanner scn = new Scanner(line);
		scn.useDelimiter(delims);
		if (scn.hasNext()){
			String res = scn.next();
			String state_name = scn.next(); String state_num = dict(state_name);
			double energy = Double.parseDouble(scn.next());
			String[] nameChainLoc = res.split("[_]");
			Variable thisRes = prot.getResidue(nameChainLoc[0], nameChainLoc[1], nameChainLoc[2]);
			Instance thisInst = thisRes.getInstance(state_num);
//			System.out.print(line+": "+thisRes.toString()+" "+ state_num+", "); 
//			System.out.println(thisInst.toString());
			double currEnergy = thisInst.getEnergy();
			thisInst.setEnergy(energy+currEnergy);
		}else{
			System.err.println("Empty or invalid line. Unable to process: "+line);
		}
		scn.close();
	}
	
	private void processPDB2PKAbackgroundLine(String line){
		Scanner scn = new Scanner(line);
		scn.useDelimiter(delims);
		if (scn.hasNext()){
			String res = scn.next();
			String state_name = scn.next(); String state_num = dict(state_name);
			double energy = Double.parseDouble(scn.next());
			String[] nameChainLoc = res.split("[_]");
			Variable thisRes = prot.getResidue(nameChainLoc[0], nameChainLoc[1], nameChainLoc[2]);
			Instance thisInst = thisRes.getInstance(state_num);
			double currEnergy = thisInst.getEnergy();
			thisInst.setEnergy(energy+currEnergy);
		}else{
			System.err.println("Empty or invalid line. Unable to process: "+line);
		}
		scn.close();
	}
	
	private String dict(String state){
		
		String output;
		
		if (state.equals("ASH1c") || state.equals("GLH1c") || state.equals("LYS") || state.equals("TYR") || state.equals("HSD") || state.equals("H3") || state.equals("CTR01c"))
			output = "1";
		else if (state.equals("ASH1t") || state.equals("GLH1t") || state.equals("HSE") || state.equals("H2") || state.equals("CTR01t"))
			output = "2";
		else if (state.equals("ASH2c") || state.equals("GLH2c") || state.equals("CTR02c") )
			output = "3";
		else if (state.equals("ASH2t") || state.equals("GLH2t") || state.equals("CTR02t") )
			output = "4";
		else if (state.equals("ASP") || state.equals("GLU") || state.equals("LYS0") || state.equals("TYR-") || state.equals("CTR-"))
			output = "0";
		else if (state.equals("ARG0"))
			output = "1+2+3+4";
		else if (state.equals("ARG"))
			output = "1+2+3+4+5";
		else if (state.equals("HSP") || state.equals("H3+H2"))
			output = "1+2";
		else
			output = null;
		
//		switch (state) {
//			case "ASH1c":
//			case "GLH1c":
//			case "LYS":
//			case "TYR":
//			case "HSD":
//			case "H3":
//			case "CTR01c":
//				output = "1";
//				break;
//			case "ASH1t":
//			case "GLH1t":
//			case "HSE":
//			case "H2":
//			case "CTR01t":
//				output = "2";
//				break;
//			case "ASH2c":
//			case "GLH2c":
//			case "CTR02c":
//				output = "3";
//				break;
//			case "ASH2t":
//			case "GLH2t":
//			case "CTR02t":
//				output = "4";
//				break;
//			case "ASP":
//			case "GLU":
//			case "LYS0":
//			case "TYR-":
//			case "CTR-":
//				output = "0";
//				break;
//			case "ARG0":
//				output = "1+2+3+4";
//				break;
//			case "ARG":
//				output = "1+2+3+4+5";
//				break;
//			case "HSP":
//			case "H3+H2":
//				output = "1+2";
//				break;
//			default:
//				output = null;
//				break;
//		}
		
		return output;
	}
	
	
	/* * * * * * * * * *
	 *  TEST PROTEINS  *
	 * * * * * * * * * */
	
	public void makeTestProt(){
		
		prot.addResidueSimple("HIS", "A", "1");
			prot.addInstance("HSD", "A", "1");
			prot.addInstance("HSE", "A", "1");
			prot.addInstance("HSP", "A", "1");
		prot.addResidueSimple("ARG", "A", "2");
			prot.addInstance("ARG", "A", "2");
			prot.addInstance("AR0", "A", "2");
		prot.addResidue("ASP", "A", "3");
			prot.addInstance("ASP", "A", "3");
			prot.addInstance("ASH", "A", "3");
		prot.addResidue("non", "A", "4");
			prot.addInstance("non", "A", "5");
		
		Instance HIS1 = prot.getInstance("HSD", "A", "1");
		Instance HIS2 = prot.getInstance("HSE", "A", "1");
		Instance HIS3 = prot.getInstance("HSP", "A", "1");
		
		Instance ARG1 = prot.getInstance("ARG", "A", "2");
		Instance ARG2 = prot.getInstance("AR0", "A", "2");
		
		Instance ASP1 = prot.getInstance("ASP", "A", "3");
		Instance ASP2 = prot.getInstance("ASH", "A", "3");
		
		Instance non = prot.getInstance("non", "A", "4");
		
		TwoKeyHash<Instance, Double> bi = new TwoKeyHash<Instance, Double>();
		
		bi.put(HIS1, ARG1, 1.0);  bi.put(ARG1, HIS1, 1.0);
		bi.put(HIS1, ARG2, 3.0);  bi.put(ARG2, HIS1, 3.0);
		bi.put(HIS1, ASP1, -4.0); bi.put(ASP1, HIS1, -4.0);
		bi.put(HIS1, ASP2, 10.0); bi.put(ASP2, HIS1, 10.0);
		bi.put(HIS1, non, -5.0);  bi.put(non, HIS1, -5.0);
		
		bi.put(HIS2, ARG1, -6.0); bi.put(ARG1, HIS2, -6.0);
		bi.put(HIS2, ARG2, 12.0); bi.put(ARG2, HIS2, 12.0);
		bi.put(HIS2, ASP1, 4.0);  bi.put(ASP1, HIS2, 4.0);
		bi.put(HIS2, ASP2, 5.0);  bi.put(ASP2, HIS2, 5.0);
		bi.put(HIS2, non, 8.0);   bi.put(non, HIS2, 8.0);
		
		bi.put(HIS3, ARG1, -3.0); bi.put(ARG1, HIS3, -3.0);
		bi.put(HIS3, ARG2, -5.0); bi.put(ARG2, HIS3, -5.0);
		bi.put(HIS3, ASP1, -8.0); bi.put(ASP1, HIS3, -8.0);
		bi.put(HIS3, ASP2, 9.0);  bi.put(ASP2, HIS3, 9.0);
		bi.put(HIS3, non, 4.0);   bi.put(non, HIS3, 4.0);
		
		bi.put(ARG1, ASP1, -2.0); bi.put(ASP1, ARG1, -2.0);
		bi.put(ARG1, ASP2, -1.0); bi.put(ASP2, ARG1, -1.0);
		bi.put(ARG1, non, -9.0);  bi.put(non, ARG1, -9.0);
		
		bi.put(ARG2, ASP1, 11.0); bi.put(ASP1, ARG2, 11.0);
		bi.put(ARG2, ASP2, 5.0);  bi.put(ASP2, ARG2, 5.0);
		bi.put(ARG2, non, -3.0);  bi.put(non, ARG2, -3.0);
		
		bi.put(ASP1, non, -6.0);  bi.put(non, ASP1, -6.0);
		bi.put(ASP2, non, 7.0);   bi.put(non, ASP2, 7.0);

		prot.setBi(bi);

		prot.simplify();

	}

	public void makeTestProtNoHIS(){

		prot.addResidueSimple("GLU", "A", "1");
		prot.addInstance("GLU", "A", "1");
		prot.addInstance("GLH", "A", "1");
		prot.addResidueSimple("ARG", "A", "2");
		prot.addInstance("AR0", "A", "2");
		prot.addInstance("ARG", "A", "2");
		prot.addResidueSimple("ASP", "A", "3");
		prot.addInstance("ASP", "A", "3");
		prot.addInstance("ASH", "A", "3");
		prot.addResidueSimple("non", "A", "4");
		prot.addInstance("non", "A", "4");

		Instance GLU1 = prot.getInstance("GLU", "A", "1");
		Instance GLU2 = prot.getInstance("GLH", "A", "1");

		Instance ARG1 = prot.getInstance("ARG", "A", "2");
		Instance ARG2 = prot.getInstance("AR0", "A", "2");

		Instance ASP1 = prot.getInstance("ASP", "A", "3");
		Instance ASP2 = prot.getInstance("ASH", "A", "3");
		
		//System.out.println(ASP1.getEnergy()); System.out.println(ASP2.getEnergy());

		Instance non = prot.getInstance("non", "A", "4");

		TwoKeyHash<Instance, Double> bi = new TwoKeyHash<Instance, Double>();

		bi.put(GLU1, ARG1, 1.0);  bi.put(ARG1, GLU1, 1.0);
		bi.put(GLU1, ARG2, 3.0);  bi.put(ARG2, GLU1, 3.0);
		bi.put(GLU1, ASP1, -4.0); bi.put(ASP1, GLU1, -4.0);
		bi.put(GLU1, ASP2, 10.0); bi.put(ASP2, GLU1, 10.0);
		bi.put(GLU1, non, -5.0);  bi.put(non, GLU1, -5.0);

		bi.put(GLU2, ARG1, -6.0); bi.put(ARG1, GLU2, -6.0);
		bi.put(GLU2, ARG2, 12.0); bi.put(ARG2, GLU2, 12.0);
		bi.put(GLU2, ASP1, 4.0);  bi.put(ASP1, GLU2, 4.0);
		bi.put(GLU2, ASP2, 5.0);  bi.put(ASP2, GLU2, 5.0);
		bi.put(GLU2, non, 8.0);   bi.put(non, GLU2, 8.0);


		bi.put(ARG1, ASP1, -2.0); bi.put(ASP1, ARG1, -2.0);
		bi.put(ARG1, ASP2, -1.0); bi.put(ASP2, ARG1, -1.0);
		bi.put(ARG1, non, -9.0);  bi.put(non, ARG1, -9.0);

		bi.put(ARG2, ASP1, 11.0); bi.put(ASP1, ARG2, 11.0);
		bi.put(ARG2, ASP2, 5.0);  bi.put(ASP2, ARG2, 5.0);
		bi.put(ARG2, non, -3.0);  bi.put(non, ARG2, -3.0);

		bi.put(ASP1, non, -6.0);  bi.put(non, ASP1, -6.0);
		bi.put(ASP2, non, 7.0);   bi.put(non, ASP2, 7.0);

		prot.setBi(bi);

		prot.simplify();


	}

}













