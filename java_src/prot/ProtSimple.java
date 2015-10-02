package prot;

import hashtools.TwoKeyHash;

import java.util.HashMap;
import java.util.Vector;

import syst.Instance;
import syst.Variable;

public class ProtSimple implements Protein{
	
	private String name;
	
	private Vector<Variable> residues; 
	private double con;
	private TwoKeyHash<Instance,Double> bi;
	
	private static HashMap<String,Double> modPka;
	private static Vector<String> titratableResNames;
	private static Vector<String> nonTitratableResNames;
	private static HashMap<String,String[]> resToTaut;
	private static Vector<String> tRes;
	private static Vector<String> utRes;
	
	/* * * * * * * * * *
	 *  CONSTRUCTORS   *
	 * * * * * * * * * */
	
	public ProtSimple(){
		residues = new Vector<Variable>();
		con = 0;
		bi = new TwoKeyHash<Instance, Double>();
		
		name = "no name given";
		
		makePka();
		makeTResNames();
		makeNonTResNames();
		makeResToTaut();
		makeTURes();
	}
	
	public ProtSimple(String name){
		residues = new Vector<Variable>();
		con = 0;
		bi = new TwoKeyHash<Instance, Double>();
		
		this.name = name;
		
		makePka();
		makeTResNames();
		makeNonTResNames();
		makeResToTaut();
		makeTURes();
	}
	
	/* * * * * * * * * * * * *
	 *  CONSTRUCTOR HELPERS  *
	 * * * * * * * * * * * * */
	
	/**
	 *  Instantiates the modPka HashMap which maps residue (actually titration state) names
	 *  to their model pKa value
	 */
	private static void makePka(){
		modPka = new HashMap<String,Double>();
		modPka.put("ARG", new Double(13.0));			modPka.put("AR0", new Double(13.0));
		modPka.put("ASP", new Double(4.0)); 			modPka.put("ASH", new Double(4.0));
		modPka.put("CYS", new Double(8.7)); 			modPka.put("CYX", new Double(8.7));		modPka.put("CYM", new Double(8.7));
		modPka.put("GLU", new Double(4.4));				modPka.put("GLH", new Double(4.4));
		modPka.put("LYS", new Double(10.4));			modPka.put("LYN", new Double(10.4));
		modPka.put("TYR", new Double(9.6));				modPka.put("TYM", new Double(9.6));
		modPka.put("NEUTRAL-CTERM", new Double(3.8));	modPka.put("CTERM", new Double(3.8));
		modPka.put("NEUTRAL-NTERM", new Double(8.0));	modPka.put("NTERM", new Double(8.0));
		modPka.put("HIS", new Double(3.15)); 			modPka.put("HSD", new Double(3.15));	 modPka.put("HSE", new Double(3.15)); 
		modPka.put("HIP", new Double(3.15));			modPka.put("HID", new Double(3.15));	 modPka.put("HIE", new Double(3.15));
		modPka.put("HSP", new Double(3.15));			modPka.put("HSD0", new Double(3.15));	 modPka.put("HSE0", new Double(3.15)); 
//		modPka.put("HIS", new Double(6.3)); 			modPka.put("HSD", new Double(6.3));		 modPka.put("HSE", new Double(6.3)); 
//		modPka.put("HIP", new Double(6.3));				modPka.put("HID", new Double(6.3));		 modPka.put("HIE", new Double(6.3));
//		modPka.put("HSP", new Double(6.3));				modPka.put("HSD0", new Double(6.3));	 modPka.put("HSE0", new Double(6.3)); 
		modPka.put("HID0", new Double(6.3));			modPka.put("HIE0", new Double(6.3));
		modPka.put("HIS1", new Double(6.3));			modPka.put("HIS2", new Double(6.3));
	}
	
	/**
	 * Instantiates titratableResNames and adds all to the Vector.
	 */
	private static void makeTResNames(){
		titratableResNames = new Vector<String>();
		titratableResNames.add("ARG"); titratableResNames.add("AR0");
		titratableResNames.add("ASP"); titratableResNames.add("ASH");
		titratableResNames.add("CYS"); titratableResNames.add("CYX"); titratableResNames.add("CYM");
		titratableResNames.add("GLU"); titratableResNames.add("GLH");
		titratableResNames.add("LYS"); titratableResNames.add("LYN");
		titratableResNames.add("TYR"); titratableResNames.add("TYM");
		titratableResNames.add("NEUTRAL-CTERM"); titratableResNames.add("CTERM");
		titratableResNames.add("NEUTRAL-NTERM"); titratableResNames.add("NTERM");
		titratableResNames.add("HIS"); titratableResNames.add("HSD"); titratableResNames.add("HSE");
		titratableResNames.add("HIP"); titratableResNames.add("HID"); titratableResNames.add("HIE");
		titratableResNames.add("HSP"); 
	}
	
	/**
	 * Instantiates nonTitratableResNames as an empty vector. The names will be
	 * added as the residues are added.
	 */
	private static void makeNonTResNames(){
		nonTitratableResNames = new Vector<String>();
	}
	
	/**
	 * Instantiates the residue to tautomer (titration state) map. Each residue has 
	 * multiple tautomers and it will me important to know the map from residue names
	 * to their tautomer names.
	 */
	private static void makeResToTaut(){
		// first the titratable residues
		resToTaut = new HashMap<String,String[]>();
		String[][] _titrationSets = 
				{{"ARG","AR0"}, {"ASP", "ASH"}, {"CYS", "CYX"}, {"GLU", "GLH"},
                 {"HSD", "HSE", "HSP"},// {"HID", "HIE", "HIP"},
                 {"LYN", "LYS"}, {"TYM", "TYR"},
                 {"NEUTRAL-CTERM", "CTERM"}, {"NEUTRAL-NTERM", "NTERM"}};
		
		for (int i=0; i<_titrationSets.length; i++){
			for (int j=0; j<_titrationSets[i].length; j++){
				resToTaut.put(_titrationSets[i][j], _titrationSets[i]);
			}
		}
		resToTaut.put("HIS",_titrationSets[4]);
		//resToTaut.put("HIS",_titrationSets[5]);
		resToTaut.put("CYM",_titrationSets[2]);
		resToTaut.put("HSP",_titrationSets[4]);
		//resToTaut.put("HSP",_titrationSets[5]);
		resToTaut.put("HIP",_titrationSets[4]);
		//resToTaut.put("HIP",_titrationSets[5]);
		
		// next the non titratable residues
//		for (int k=0; k<nonTitratableResNames.size(); k++){
//			String name = nonTitratableResNames.get(k);
//			String[] taut = {name};
//			resToTaut.put(name, taut);
//		}
	}

	/**
	 * Instantiates the titrated residue names (tRes) and non titrated
	 * residue names (utRes). Note that when HIS is split into its two
	 * sites the HSD, HSE, HID, HIE titration states will be considered
	 * titrated rather than untitrated.
	 */
	private static void makeTURes(){
		tRes = new Vector<String>();
		utRes = new Vector<String>();
		tRes.add("ASH"); utRes.add("ASP");
		tRes.add("CYS"); utRes.add("CYM"); utRes.add("CYX");
		tRes.add("GLH"); utRes.add("GLU");
		tRes.add("ARG"); utRes.add("AR0");
		tRes.add("LYS"); utRes.add("LYN");
		tRes.add("TYR"); utRes.add("TYM");
		tRes.add("NEUTRAL-CTERM"); utRes.add("CTERM");
		tRes.add("NEUTRAL-NTERM"); utRes.add("NTERM");
		tRes.add("HIS"); utRes.add("HSD"); utRes.add("HSE");
		tRes.add("HIP"); utRes.add("HID"); utRes.add("HIE");
		tRes.add("HSP");
	}
	
	/* * * * * * * * * * * * *
	 *  GETTERS AND SETTERS  *
	 * * * * * * * * * * * * */
	
	public Vector<Variable> getResidues(){
		return residues;
	}
	
	public double getConstant(){
		return con;
	}
	
	public TwoKeyHash<Instance, Double> getBinary(){
		return bi;
	}
	
	public Variable getResidue(String name, String chain, String loc){
		for (Variable r: residues){
			if (r.getName().equals(name+"_"+chain+"_"+loc)){
				return r;
			}
		}
		return null;
	}
	
	public Instance getInstance(String name, String chain, String loc){
		String chainLoc = chain+"_"+loc;
		int endLength = chainLoc.length();
		for (Variable v: residues){
			String nm = v.getName();
			if (chainLoc.equals(nm.substring(nm.length() - endLength))){
				for (Instance i: v.getInstances()){
					if (i.getLabel().equals(name)){
						return i;
					}
				}
			}
		}
		return null;
		
//		String[] tauts;
//		if (titratableResNames.contains(name)){
//			tauts = resToTaut.get(name);
//			
//		}else{
//			tauts = new String[1];
//			tauts[0] = name;
//		}
//		for (Variable r: residues){
//			for (int j=0; j<tauts.length; j++){
//				if (r.getName().equals(tauts[j]+"_"+chain+"_"+loc)){
//					for (Instance i: r.getInstances()){
//						if (i.getLabel().equals(name)){
//							return i;
//						}
//					}
//				}
//			}
//		}
//		return null;
	}
	
	public void addInstance(String name, String chain, String loc){
		String chainLoc = chain+"_"+loc;
		int endLength = chainLoc.length();
		for (Variable v: residues){
			String nm = v.getName();
			if (chainLoc.equals(nm.substring(nm.length() - endLength))){
				v.addInstance(name);
				if (utRes.contains(name)){
					v.getInstance(name).setEnergy(modPka.get(name));
				}
			}
		}
	}
	
	public void addResidueSimple(String name, String chain, String loc){
		Variable var = new Variable(name+"_"+chain+"_"+loc);
		addResidue(var);
	}
	
	public void addResidue(Variable res){
		if (!contains(res)){
			residues.add(res);
		}else{
			//do nothing, we already have this Variable
		}
	}
	
	public void addResidue(String name, String chain, String loc){
		Variable var;
		if( titratableResNames.contains(name) ){
			String[] labels = resToTaut.get(name);
			//for (int i=0; i<labels.length; i++){
			//	labels[i] = labels[i]+"_"+chain+"_"+loc;
			//}
			var = new Variable(name+"_"+chain+"_"+loc, labels);
			for (String l: labels){
				Instance vl = var.getInstance(l);
				if (tRes.contains(l)){
					vl.setEnergy(modPka.get(l));
				}else{
					vl.setEnergy(0);
				}
			}
		}else{
			nonTitratableResNames.add(name);
			String[] label = {name};
			var = new Variable(name+"_"+chain+"_"+loc,label);
			Instance vl = var.getInstance(label[0]);
			vl.setEnergy(0);
		}
		
		addResidue(var);
		
	}
	
	public void setPH(double ph){
		System.out.println("setPH not yet implemented in ProtSimple");
	}
	
	public void setEnergy(Instance i1, Instance i2, Double ene){
		bi.put(i1, i2, ene);
	}
	
	public void setBi(TwoKeyHash<Instance, Double> bi){
		this.bi = bi;
	}
	
	public void simplify(){
		Vector<Variable> toRemove = new Vector<Variable>();
		Vector<Vector<Instance>> toRemoveFromBi = new Vector<Vector<Instance>>();
		
		
		for (Variable v: residues){
			//Instances within variable v
			Vector<Instance> vis = v.getInstances();
			if (vis.size() == 1){
				//if there is only one instance we must
				//(a) remove it
				toRemove.add(v);
				
				//(b) add its energy to the constant
				con += vis.get(0).getEnergy()*0.5;
				
				for (Variable w: residues){
					if (v.equals(w))
						continue;
					
					//(c) add its interaction energy with other instances
					//    either to the constant or to the unary energy
					//    of the other instances
					Vector<Instance> wis = w.getInstances();
					if (wis.size() == 1){
						//if the other variable has only one instance then
						//we add the binary energy between the two instances
						//to the constant and we mark the other variable
						//for removal
						toRemove.add(w);
						con += bi.get(vis.get(0), wis.get(0))*0.5;
						
						Vector<Instance> temp = new Vector<Instance>(2);
						temp.add(0, vis.get(0)); temp.add(1, wis.get(0));
						toRemoveFromBi.add(temp);
						
					}else{
						//if w has multiple instances then we add the
						//binary energy to the unary energy of the 
						//instance of w
						for (Instance wi: wis){
							wi.setEnergy(wi.getEnergy() + bi.get(vis.get(0), wi)*0.5);
							
							Vector<Instance> temp = new Vector<Instance>(2);
							temp.add(0, vis.get(0)); temp.add(1, wi);
							toRemoveFromBi.add(temp);
						}
					}
					
				}
			}else{
				//if there are multiple instances of v then we look at other instances
				//to update the energy of v if needed
				for (Variable w: residues){
					if (v.equals(w))
						continue;
					Vector<Instance> wis = w.getInstances();
					if (wis.size() == 1){
						toRemove.add(w);
						for (Instance vi: vis){
							vi.setEnergy(vi.getEnergy() + bi.get(vi, wis.get(0))*0.5);
							
							Vector<Instance> temp = new Vector<Instance>(2);
							temp.add(0, vi); temp.add(1, wis.get(0));
							toRemoveFromBi.add(temp);
						}
					}else{
						//both v and w have multiple instances so we do nothing
					}
				}
			}
		}
		
		residues.removeAll(toRemove);
		bi.removeAll(toRemoveFromBi);
	}
	
	/* * * * * * * * * * * *
	 *  SUPPORTING METHODS *
	 * * * * * * * * * * * */
	
	public boolean contains(Variable res){
		for (Variable r: residues){
			if (r.equals(res)){
				return true;
			}
		}
		return false;
	}

	public void printProtein(){
		System.out.println();
		System.out.println("*****************");
		System.out.println("*    PROTEIN    *");
		System.out.println("*****************");
		for (Variable v: residues){
			System.out.println(v.toString());
			for (Instance i: v.getInstances()){
				System.out.println("  "+i.toString()+" with energy "+i.getEnergy());
			}
		}
		System.out.println();
	}

	public void printEnergy(){
		System.out.println();
		System.out.println("*****************");
		System.out.println("*     ENERGY    *");
		System.out.println("*****************");
		for (Variable v1: residues){
			for (Instance i1: v1.getInstances()){
				for (Variable v2: residues){
					if (v1.equals(v2)){
						continue;
					}
					for (Instance i2: v2.getInstances()){
						System.out.println(i1.toString()+", "+i2.toString()+" "+bi.get(i1, i2));
					}
				}
			}
		}
	}

	@Override
	public double getTemp() {
		// FIXME Auto-generated method stub
		return 0;
	}
}
