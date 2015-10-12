package prot;

import hashtools.TwoKeyHash;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Vector;

import mathtools.MapleTools;
import mathtools.VectorsMax;

import syst.Instance;
import syst.Variable;

public class ProtCompl implements Protein {

	private String name;

	private double pH;
	private double T; // temperature
	public static final double kln10 = Math.log(10)*8.3144621;//*Math.log(10); // gas constant * ln(10)
	private Vector<Variable> residues;
	private double con;
	private TwoKeyHash<Instance,Double> bi;

	private static HashMap<String,Double> modPka;
	private static HashMap<String,Integer> ionizable;
	private static Vector<String> titratableResNames;
	private static Vector<String> nonTitratableResNames;
	private static HashMap<String,String[]> resToTaut;
	private static Vector<String> tRes;
	private static Vector<String> utRes;

	/* * * * * * * * * *
	 *  CONSTRUCTORS   *
	 * * * * * * * * * */

	public ProtCompl(){
		residues = new Vector<Variable>();
		con = 0;
		bi = new TwoKeyHash<Instance, Double>();

		pH = 7.0;
		T = 300;

		name = "no name given";

		makePka();
		makeIonizable();
		makeTResNames();
		makeNonTResNames();
		makeResToTaut();
		makeTURes();
	}

	public ProtCompl(double pH){
		residues = new Vector<Variable>();
		con = 0;
		bi = new TwoKeyHash<Instance, Double>();

		this.pH = pH;
		T = 300;

		name = "no name given";

		makePka();
		makeIonizable();
		makeTResNames();
		makeNonTResNames();
		makeResToTaut();
		makeTURes();
	}

	public ProtCompl(double pH, double T){
		residues = new Vector<Variable>();
		con = 0;
		bi = new TwoKeyHash<Instance, Double>();

		this.pH = pH;
		this.T = T;

		name = "no name given";

		makePka();
		makeIonizable();
		makeTResNames();
		makeNonTResNames();
		makeResToTaut();
		makeTURes();
	}

	public ProtCompl(String name){
		residues = new Vector<Variable>();
		con = 0;
		bi = new TwoKeyHash<Instance, Double>();

		pH = 7.0;
		T = 300;

		this.name = name;

		makePka();
		makeIonizable();
		makeTResNames();
		makeNonTResNames();
		makeResToTaut();
		makeTURes();
	}

	public ProtCompl(String name, double pH){
		residues = new Vector<Variable>();
		con = 0;
		bi = new TwoKeyHash<Instance, Double>();

		this.pH = pH;
		T = 300;

		this.name = name;

		makePka();
		makeIonizable();
		makeTResNames();
		makeNonTResNames();
		makeResToTaut();
		makeTURes();
	}

	public ProtCompl(String name, double pH, double T){
		residues = new Vector<Variable>();
		con = 0;
		bi = new TwoKeyHash<Instance, Double>();

		this.pH = pH;
		this.T = T;

		this.name = name;

		makePka();
		makeIonizable();
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
		modPka.put("ARG", new Double(13.0));
		modPka.put("ASP", new Double(3.9));
		modPka.put("GLU", new Double(4.1));
		modPka.put("LYS", new Double(10.4));
		modPka.put("TYR", new Double(9.6));
		modPka.put("CTR", new Double(3.2));
		modPka.put("NTR", new Double(8.3));
		modPka.put("HIS", new Double(6.6));
	}

	private static void makeIonizable(){
		ionizable = new HashMap<String, Integer>();
		ionizable.put("ARG", 1);
		ionizable.put("ASP", -1);
		ionizable.put("GLU", -1);
		ionizable.put("LYS", 1);
		ionizable.put("TYR", 1);
		ionizable.put("CTR", -1);
		ionizable.put("NTR", 1);
		ionizable.put("HIS", 1);
	}

	/**
	 * Instantiates titratableResNames and adds all to the Vector.
	 */
	private static void makeTResNames(){
		titratableResNames = new Vector<String>();
		titratableResNames.add("ARG");
		titratableResNames.add("ASP");
		titratableResNames.add("GLU");
		titratableResNames.add("LYS");
		titratableResNames.add("TYR");
		titratableResNames.add("CTR");
		titratableResNames.add("NTR");
		titratableResNames.add("HIS");
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

		String[] ARG_tauts = {"1+2+3+4", "1+2+3+4+5"};
		resToTaut.put("ARG", ARG_tauts);

		String[] ASP_tauts = {"1", "2", "3", "4", "0"};
		resToTaut.put("ASP", ASP_tauts);

		String[] GLU_tauts = {"1", "2", "3", "4", "0"};
		resToTaut.put("GLU", GLU_tauts);

		String[] LYS_tauts = {"1", "0"};
		resToTaut.put("LYS", LYS_tauts);

		String[] TYR_tauts = {"1", "0"};
		resToTaut.put("TYR", TYR_tauts);

		String[] HIS_tauts = {"1", "2", "1+2"};
		resToTaut.put("HIS", HIS_tauts);

		String[] NTR_tauts = {"1", "2", "1+2"};
		resToTaut.put("NTR", NTR_tauts);

		String[] CTR_tauts = {"1", "2", "3", "4", "0"};
		resToTaut.put("CTR", CTR_tauts);

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

//		tRes.add("ASP_1"); tRes.add("ASP_2"); tRes.add("ASP_3"); tRes.add("ASP_4");
//		tRes.add("GLU_1"); tRes.add("GLU_2"); tRes.add("GLU_3"); tRes.add("GLU_4");
//		tRes.add("ARG_1+2+3+4+5");
//		tRes.add("LYS_1");
//		tRes.add("TYR_1");
//		tRes.add("HIS_1"); tRes.add("HIS_2"); utRes.add("HIS_1+2");
//		tRes.add("NTR_1"); tRes.add("NTR_2");
//		tRes.add("CTR_1"); tRes.add("CTR_2"); tRes.add("CTR_3"); tRes.add("CTR_4");
//
//		utRes.add("ASP_0");
//		utRes.add("GLU_0");
//		utRes.add("ARG_1+2+3+4");
//		utRes.add("LYS_0");
//		utRes.add("TYR_0");
//		utRes.add("NTR_1+2");
//		utRes.add("CTR_0");
		
		tRes.add("ASP_1"); tRes.add("ASP_2"); tRes.add("ASP_3"); tRes.add("ASP_4");
		tRes.add("GLU_1"); tRes.add("GLU_2"); tRes.add("GLU_3"); tRes.add("GLU_4");
		tRes.add("ARG_1+2+3+4+5");
		tRes.add("LYS_1");
		tRes.add("TYR_1");
		tRes.add("HIS_1"); tRes.add("HIS_2"); utRes.add("HIS_1+2");
		utRes.add("NTR_1"); utRes.add("NTR_2");
		tRes.add("CTR_1"); tRes.add("CTR_2"); tRes.add("CTR_3"); tRes.add("CTR_4");

		utRes.add("ASP_0");
		utRes.add("GLU_0");
		utRes.add("ARG_1+2+3+4");
		utRes.add("LYS_0");
		utRes.add("TYR_0");
		tRes.add("NTR_1+2");
		utRes.add("CTR_0");

	}
	

	/* * * * * * * * * * * * *
	 *  GETTERS AND SETTERS  *
	 * * * * * * * * * * * * */

	@Override
	public Vector<Variable> getResidues() {
		return residues;
	}

	@Override
	public double getConstant() {
		return con;
	}

	public double getTemp(){
		return T;
	}

	@Override
	public TwoKeyHash<Instance, Double> getBinary() {
		return bi;
	}

	@Override
	public Variable getResidue(String name, String chain, String loc) {
		for (Variable r: residues){
			if (r.getName().equals(name+"_"+chain+"_"+loc)){
				return r;
			}
		}
		return null;
	}

	@Override
	// Not used in the version of the code that uses PDB2PKA input files
	public Instance getInstance(String name, String chain, String loc) {
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
	}

	@Override
	// Not used in the version of the code that uses PDB2PKA input files
	public void addInstance(String name, String chain, String loc) {
		String chainLoc = chain+"_"+loc;
		int endLength = chainLoc.length();
		for (Variable v: residues){
			String nm = v.getName();
			if (chainLoc.equals(nm.substring(nm.length() - endLength))){
				v.addInstance(name);
				if (tRes.contains(name)){
					v.getInstance(name).setEnergy((pH - modPka.get(name)));//*kln10*T);
				}
			}
		}
	}

	@Override
	// Not used in the version of the code that uses PDB2PKA input files
	public void addResidueSimple(String name, String chain, String loc) {
		Variable var = new Variable(name+"_"+chain+"_"+loc);
		addResidue(var);
	}

	@Override
	public void addResidue(Variable res) {
		if (!contains(res)){
			residues.add(res);
		}else{
			//do nothing, we already have this Variable
		}
	}

	@Override
	public void addResidue(String name, String chain, String loc) {
		Variable var;
		if( titratableResNames.contains(name) ){
			String[] labels = resToTaut.get(name);
			//for (int i=0; i<labels.length; i++){
			//	labels[i] = labels[i]+"_"+chain+"_"+loc;
			//}
			var = new Variable(name+"_"+chain+"_"+loc, labels);
			for (String l: labels){
				Instance vl = var.getInstance(l);
				if (tRes.contains(name+"_"+l)){
					vl.setEnergy((pH - modPka.get(name)));//*kln10*T);
				}else{
					vl.setEnergy(0);
				}
			}
		}else{
			nonTitratableResNames.add(name);
			String[] label = {"1"};
			var = new Variable(name+"_"+chain+"_"+loc,label);
			Instance vl = var.getInstance(label[0]);
			vl.setEnergy(0);
		}

		addResidue(var);

	}

	public void setPH(double pH){
		double oldPH = this.pH;
		this.pH = pH;
		HashSet<Variable> his = new HashSet<Variable>();
		for (Variable res : residues){
			if (res.isHIS()){
				his.add(res);
//				continue;
			}
			for (Instance inst : res.getInstances()){
				if (inst.getLabel().equals("PROT") || tRes.contains(inst.getLabel())){
					//inst.setEnergy(inst.getEnergy() - kln10*T*oldPH + kln10*T*pH);
					inst.setEnergy(inst.getEnergy() - oldPH + pH);
				}
			}
		}

		// for any HIS residues we need to reset the interaction between HIe and HId since
		// that depended on the single energy of HIS1, HIS2, and HIS1+2
		for (Variable h1 : his){
			String hisLoc1 = h1.getName().substring(3);
			for (Variable h2 : his){
				String hisLoc2 = h2.getName().substring(3);
				if (hisLoc1.equals(hisLoc2) && !h1.equals(h2)){
					Instance h1PROT = h1.getInstance("PROT");
					Instance h2PROT = h2.getInstance("PROT");
					//Double ene = bi.get(h1PROT, h2PROT) + 2*kln10*T*oldPH - 2*kln10*T*pH;
					Double ene = bi.get(h1PROT, h2PROT) + 2*oldPH - 2*pH;
					this.setEnergy(h1PROT, h2PROT, ene);
				}
			}
		}

	}

	@Override
	public void setEnergy(Instance i1, Instance i2, Double ene) {
		bi.put(i1, i2, ene);
	}

	@Override
	public void setBi(TwoKeyHash<Instance, Double> bi) {
		this.bi = bi;
	}

	public static boolean isT(Instance ins){

		if (tRes.contains(ins.getName()))
			return true;
		else
			return false;

	}

	@Override
	public void simplify() {
		System.out.println("\nRemoving single-instance variables (`simplify`)");

		// We must remove any single-instance variables
		// (residues which have only one state).
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

//		System.out.println();
//		System.out.println("************************");
//		System.out.println("*    AFTER SIMPLIFY    *");
//		System.out.println("************************");
//		configEnergies();

		consolidate();
//		printProtein();
//		printEnergy();

//		System.out.println();
//		System.out.println("***************************");
//		System.out.println("*    AFTER CONSOLIDATE    *");
//		System.out.println("***************************");
//		configEnergies();

		divideHIS();
//
//		System.out.println();
//		System.out.println("**************************");
//		System.out.println("*    AFTER DIVIDE HIS    *");
//		System.out.println("**************************");
//		configEnergies();
//		Comparator<Vector<Instance>> comparator = new Comparator<Vector<Instance>>() {
//		  public int compare(Vector<Instance> o1, Vector<Instance> o2) {
//		    return o1.toString().compareTo(o2.toString());
//		  }
//		};
//		SortedSet<Vector<Instance>> sorted_keys = new TreeSet<Vector<Instance>>(comparator);
//		sorted_keys.addAll(bi.keySet());
//
//		for(Vector<Instance> key: sorted_keys)
//		{
//			if (key.get(0).getName().equals(key.get(1).getName()))
//				continue;
//			if (key.get(0).getLabel().contains("PROT") &&
//				key.get(1).getLabel().contains("PROT"))
//				System.out.println(key.toString()+"="+bi.get(key.get(0), key.get(1)).toString());
//		}


	}


	/**
	 * Each residue has multiple protonated and deprotonated states. Here
	 * we consolidate those into two states for each residue, PROT and DEPROT.
	 * We take minimums of energies between states in each class. For example,
	 * assume we have two amino acids, A and B, where A has protonated states 1, 2, 3
	 * and deprotonated state 4, and B has protonated states 1, 2, and deprotonated
	 * state 3. Then
	 * E(A_PROT, B_PROT) = min{E(A1,B1), E(A1,B2), E(A2,B1), E(A2,B2), E(A3,B1), E(A3,B2)},
	 * E(A_PROT, B_DEPROT) = min{E(A1,B3), E(A2,B3), E(A3,B3)},
	 * E(A_DEPROT, B_PROT) = min{E(A4,B1), E(A4,B2)}, and
	 * E(A_DEPROT, B_DEPROT) = E(A4,B3).
	 * We do not deal with HIS here, it is kept in its 3 states for now.
	 */
	private void consolidate(){
		System.out.println("\nConsolidating variables with more than two instances (`consolidate`)");

		// we will be removing energies
		Vector<Vector<Instance>> toRemoveFromBi = new Vector<Vector<Instance>>();

		// we will remove instances from variables but have to do it all at once at the end
		HashMap<Variable, Vector<Instance>> toRemoveInstances = new HashMap<Variable, Vector<Instance>>();

		// keep track of the hisResidues we see in the outer (upcoming) loop so that we
		// deal with them specially later
		Vector<Variable> hisResidues = new Vector<Variable>();

		// Now (if called after simplify()) every variable has >= 2
		// instances. We must consolidate the variables that have
		// more than two instances into variables with exactly two.
		// Except for HIS which we deal with separately.

		for (int vIndex = 0; vIndex<residues.size(); vIndex++){
			// grab the variable and its instances
			Variable v = residues.get(vIndex);
			Vector<Instance> vInst = v.getInstances();

			// if w is HIS then skip it, but keep it for later in hisResidues
			if (v.getName().substring(0, 3).equals("HIS")){
//				System.out.println("Skipped a HIS ("+v.getName()+") to deal with later.");
				hisResidues.add(v);
				continue;
			}

			// classify each Instance for v as T or U
			Vector<Instance> vtInst = new Vector<Instance>();
			Vector<Instance> vuInst = new Vector<Instance>();
			for (Instance vi: vInst){
				if (tRes.contains(vi.getName().substring(0, 3)+"_"+vi.getLabel())){
					vtInst.add(vi);
				} else if (utRes.contains(vi.getName().substring(0, 3)+"_"+vi.getLabel())){
					vuInst.add(vi);
				} else if (vi.getLabel().equals("PROT") || vi.getLabel().equals("DEPROT")){
					//do nothing
				} else{
					System.out.println("in residue "+v.toString()+
							" we have label "+vi.getLabel()+" which isn't found");
				}
			}

			double[] vtEnergies = new double[vtInst.size()];
			double[] vuEnergies = new double[vuInst.size()];

			int index = 0;
			for (Instance vti: vtInst){
				vtEnergies[index++] = vti.getEnergy();
			}

			index = 0;
			for (Instance vui: vuInst){
				vuEnergies[index++] = vui.getEnergy();
			}


			// create two new instances for the residue called "PROT" and "DEPROT"
			// PROT will replace all of the instances in vtInst and
			// DEPROT will replace all of the instances in vuInst
			Instance vProt = new Instance(v.getName(), "PROT", v, MapleTools.min(vtEnergies));
			Instance vDeprot = new Instance(v.getName(), "DEPROT", v, MapleTools.min(vuEnergies));
			v.addInstance(vDeprot);
			v.addInstance(vProt);
			

			// now look at second Variables for v to interact with
			for (int wIndex = vIndex+1; wIndex < residues.size(); wIndex++){
				// grab the w Variable and its Instances
				Variable w = residues.get(wIndex);
				Vector<Instance> wInst = w.getInstances();

				// if the variable is HIS then we skip it
				// we'll deal with HIS as the first residue/variable later
				if (w.getName().substring(0, 3).equals("HIS")){
//					System.out.println("Skipped a HIS: "+w.getName());
					continue;
				}

				// classify each Instance for Variable w as a T or U
				Vector<Instance> wtInst = new Vector<Instance>();
				Vector<Instance> wuInst = new Vector<Instance>();
				for (Instance wi: wInst){
					if (tRes.contains(wi.getName().substring(0, 3)+"_"+wi.getLabel())){
						wtInst.add(wi);
					} else if (utRes.contains(wi.getName().substring(0, 3)+"_"+wi.getLabel())){
						wuInst.add(wi);
					} else if (wi.getLabel().equals("PROT") || wi.getLabel().equals("DEPROT")){
						//do nothing
					} else {
						System.out.println("in residue "+w.toString()+
								" we have label "+wi.getLabel()+" which isn't found");
					}
				}

				double[] wtEnergies = new double[wtInst.size()];
				double[] wuEnergies = new double[wuInst.size()];

				index = 0;
				for (Instance wti: wtInst){
					wtEnergies[index++] = wti.getEnergy();
				}

				index = 0;
				for (Instance wui: wuInst){
					wuEnergies[index++] = wui.getEnergy();
				}

				// create two new instances for the residue called "PROT" and "DEPROT"
				// PROT will replace all of the instances in wtInst and
				// DEPROT will replace all of the instances in wuInst
				Instance wProt = new Instance(w.getName(), "PROT", w, MapleTools.min(wtEnergies));
				Instance wDeprot = new Instance(w.getName(), "DEPROT", w, MapleTools.min(wuEnergies));
				w.addInstance(wDeprot);
				w.addInstance(wProt);
				
				// gather up all the energies for T with T
				double[] ttEnergies = new double[vtInst.size()*wtInst.size()];
				index = 0;
				for (Instance vti: vtInst){
					for (Instance wti: wtInst){
						try{
							ttEnergies[index++] = bi.get(vti, wti);
//							System.out.println("NonError: bi did have "+vti+" "+wti);
						}catch(NullPointerException err){
							System.err.println("Error: bi did not have "+vti.toString()+" "+wti.toString());
//							System.err.println(bi.toString());
//							System.exit(0);
						}

						// we'll eventually remove these energies from bi
						Vector<Instance> temp1 = new Vector<Instance>(2);
						temp1.add(0, vti); temp1.add(1, wti);
						toRemoveFromBi.add(temp1);
						Vector<Instance> temp2 = new Vector<Instance>(2);
						temp2.add(0, wti); temp2.add(1, vti);
						toRemoveFromBi.add(temp2);
					}
				}
				bi.put(v.getInstance("PROT"), w.getInstance("PROT"), MapleTools.min(ttEnergies));
				bi.put(w.getInstance("PROT"), v.getInstance("PROT"), MapleTools.min(ttEnergies));

				// gather up all the energies for T with U
				double[] tuEnergies = new double[vtInst.size()*wuInst.size()];
				index = 0;
				for (Instance vti: vtInst){
					for (Instance wui: wuInst){
						try{
							tuEnergies[index++] = bi.get(vti, wui);
						}catch(NullPointerException err){
							System.err.println("Error2: bi did not have "+vti+" "+wui);
						}

						// we'll eventually remove these energies from bi
						Vector<Instance> temp1 = new Vector<Instance>(2);
						temp1.add(0, vti); temp1.add(1, wui);
						toRemoveFromBi.add(temp1);
						Vector<Instance> temp2 = new Vector<Instance>(2);
						temp2.add(0, wui); temp2.add(1, vti);
						toRemoveFromBi.add(temp2);
					}
				}
				bi.put(v.getInstance("PROT"), w.getInstance("DEPROT"), MapleTools.min(tuEnergies));
				bi.put(w.getInstance("DEPROT"), v.getInstance("PROT"), MapleTools.min(tuEnergies));

				// gather up all the energies for U with T
				double[] utEnergies = new double[vuInst.size()*wtInst.size()];
				index = 0;
				for (Instance vui: vuInst){
					for (Instance wti: wtInst){
						try{
							utEnergies[index++] = bi.get(vui, wti);
						}catch(NullPointerException err){
							System.err.println("Error3: bi did not have "+vui+" "+wti);
						}

						// we'll eventually remove these energies from bi
						Vector<Instance> temp = new Vector<Instance>(2);
						temp.add(0, vui); temp.add(1, wti);
						toRemoveFromBi.add(temp);
						Vector<Instance> temp2 = new Vector<Instance>(2);
						temp2.add(0, wti); temp2.add(1, vui);
						toRemoveFromBi.add(temp2);
					}
				}
				bi.put(v.getInstance("DEPROT"), w.getInstance("PROT"), MapleTools.min(utEnergies));
				bi.put(w.getInstance("PROT"), v.getInstance("DEPROT"), MapleTools.min(utEnergies));

				// gather up all the energies for U with U
				double[] uuEnergies = new double[vuInst.size()*wuInst.size()];
				index = 0;
				for (Instance vui: vuInst){
					for (Instance wui: wuInst){
						try{
							uuEnergies[index++] = bi.get(vui, wui);
						}catch(NullPointerException err){
							System.err.println("Error4: bi did not have "+vui+" "+wui);
						}

						// we'll eventually remove these energies from bi
						Vector<Instance> temp = new Vector<Instance>(2);
						temp.add(0, vui); temp.add(1, wui);
						toRemoveFromBi.add(temp);
						Vector<Instance> temp2 = new Vector<Instance>(2);
						temp2.add(0, wui); temp2.add(1, vui);
						toRemoveFromBi.add(temp2);
					}
				}
				bi.put(v.getInstance("DEPROT"), w.getInstance("DEPROT"), MapleTools.min(uuEnergies));
				bi.put(w.getInstance("DEPROT"), v.getInstance("DEPROT"), MapleTools.min(uuEnergies));

			}

			// Add v->vtInst union vuInst to the HashMap of instances to remove later
			vtInst.addAll(vuInst);
			toRemoveInstances.put(v, vtInst);

		}

		// now deal with the interactions between HIS residues and non-HIS residues
		for (Variable h : hisResidues){
			// get each of the Instances within h
			Instance h1 = h.getInstance("1");
			Instance h2 = h.getInstance("2");
			Instance h12 = h.getInstance("1+2");

			// for each (non-HIS) other Variable we will need to transform the interactions energies
			// into the energies between the HIS instances and the instances of v with PROT or DEPROT as the label
			for (Variable v : residues){
				// get all the instances of v
				Vector<Instance> vInst = v.getInstances();

				// if the second one is HIS we don't have to look at it because HIS-HIS interactions are already fine
				if (v.getName().substring(0, 3).equals("HIS")){
//					System.out.println("Skipped a HIS (as the second residue when working with HIS as the first): "+v.getName());
					continue;
				}

				// classify each Instance for v as T or U
				Vector<Instance> vtInst = new Vector<Instance>();
				Vector<Instance> vuInst = new Vector<Instance>();
				for (Instance vi: vInst){
					if (tRes.contains(vi.getName().substring(0, 3)+"_"+vi.getLabel())){
						vtInst.add(vi);
					} else if (utRes.contains(vi.getName().substring(0, 3)+"_"+vi.getLabel())){
						vuInst.add(vi);
					} else if (vi.getLabel().equals("PROT") || vi.getLabel().equals("DEPROT")){
						//do nothing
					} else{
						System.out.println("in residue "+v.toString()+
								" we have label "+vi.getLabel()+" which isn't found");
					}
				}

				// gather up the energies between T instances of v and h1, h2, and h12 separately
				double[] t1Energies = new double[vtInst.size()];
				double[] t2Energies = new double[vtInst.size()];
				double[] t12Energies = new double[vtInst.size()];
				int index = 0;
				for (Instance vti : vtInst){
					try{
						t1Energies[index] = bi.get(vti, h1);
					}catch(NullPointerException err){
						System.err.println("Error5: bi did not have "+vti+" "+h1);
					}

					Vector<Instance> temp1 = new Vector<Instance>(2);
					temp1.add(0, vti); temp1.add(1, h1);
					toRemoveFromBi.add(temp1);
					Vector<Instance> temp2 = new Vector<Instance>(2);
					temp2.add(0, h1); temp2.add(1, vti);
					toRemoveFromBi.add(temp2);

					try{
						t2Energies[index] = bi.get(vti, h2);
					}catch(NullPointerException err){
						System.err.println("Error6: bi did not have "+vti+" "+h2);
					}

					Vector<Instance> temp3 = new Vector<Instance>(2);
					temp3.add(0, vti); temp3.add(1, h2);
					toRemoveFromBi.add(temp3);
					Vector<Instance> temp4 = new Vector<Instance>(2);
					temp4.add(0, h2); temp4.add(1, vti);
					toRemoveFromBi.add(temp4);

					try{
						t12Energies[index++] = bi.get(vti, h12);
					}catch(NullPointerException err){
						System.err.println("Error7: bi did not have "+vti+" "+h12);
					}

					Vector<Instance> temp5 = new Vector<Instance>(2);
					temp5.add(0, vti); temp5.add(1, h12);
					toRemoveFromBi.add(temp5);
					Vector<Instance> temp6 = new Vector<Instance>(2);
					temp6.add(0, h12); temp6.add(1, vti);
					toRemoveFromBi.add(temp6);
				}
				double minT1 = MapleTools.min(t1Energies);
				double minT2 = MapleTools.min(t2Energies);
				double minT12 = MapleTools.min(t12Energies);
				bi.put(v.getInstance("PROT"), h1, minT1);
				bi.put(h1, v.getInstance("PROT"), minT1);
				bi.put(v.getInstance("PROT"), h2, minT2);
				bi.put(h2, v.getInstance("PROT"), minT2);
				bi.put(v.getInstance("PROT"), h12, minT12);
				bi.put(h12, v.getInstance("PROT"), minT12);


				// gather up the energies between U instances of v and h1, h2, and h12 separately
				double[] u1Energies = new double[vuInst.size()];
				double[] u2Energies = new double[vuInst.size()];
				double[] u12Energies = new double[vuInst.size()];
				index = 0;
				for (Instance vui : vuInst){
					try{
						u1Energies[index] = bi.get(vui, h1);
					}catch(NullPointerException err){
						System.err.println("Error8: bi did not have "+vui+" "+h2);
					}
					Vector<Instance> temp1 = new Vector<Instance>(2);
					temp1.add(0, vui); temp1.add(1, h1);
					toRemoveFromBi.add(temp1);
					Vector<Instance> temp2 = new Vector<Instance>(2);
					temp2.add(0, h1); temp2.add(1, vui);
					toRemoveFromBi.add(temp2);

					try{
						u2Energies[index] = bi.get(vui, h2);
					}catch(NullPointerException err){
						System.err.println("Error9: bi did not have "+vui+" "+h2);
					}

					Vector<Instance> temp3 = new Vector<Instance>(2);
					temp3.add(0, vui); temp3.add(1, h2);
					toRemoveFromBi.add(temp3);
					Vector<Instance> temp4 = new Vector<Instance>(2);
					temp4.add(0, h2); temp4.add(1, vui);
					toRemoveFromBi.add(temp4);

					try{
						u12Energies[index++] = bi.get(vui, h12);
					}catch(NullPointerException err){
						System.err.println("Error10: bi did not have "+vui+" "+h12);
					}

					Vector<Instance> temp5 = new Vector<Instance>(2);
					temp5.add(0, vui); temp5.add(1, h12);
					toRemoveFromBi.add(temp5);
					Vector<Instance> temp6 = new Vector<Instance>(2);
					temp6.add(0, h12); temp6.add(1, vui);
					toRemoveFromBi.add(temp6);
				}
				bi.put(v.getInstance("DEPROT"), h1, MapleTools.min(u1Energies));
				bi.put(h1, v.getInstance("DEPROT"), MapleTools.min(u1Energies));
				bi.put(v.getInstance("DEPROT"), h2, MapleTools.min(u2Energies));
				bi.put(h2, v.getInstance("DEPROT"), MapleTools.min(u2Energies));
				bi.put(v.getInstance("DEPROT"), h12, MapleTools.min(u12Energies));
				bi.put(h12, v.getInstance("DEPROT"), MapleTools.min(u12Energies));

			}
		}

		// remove all the instances that aren't PROT or DEPROT (besides the HIS instances)
		for (Variable v : toRemoveInstances.keySet()){
			v.removeInstances(toRemoveInstances.get(v));
		}

		// remove all the energies involving non-PROT and non-DEPROT instances (except for HIS ones)
		bi.removeAll(toRemoveFromBi);

	}

	/**
	 * Here we divide HIS into two residues - HID, HIE - each with half the pKa value. We
	 * have to set interaction energies between HIS and other residues and for HIS-HIS
	 * interactions. Do this based on the values given in the paper
	 */
	private void divideHIS(){
		System.out.println("\nDividing HIS (`divideHIS`)");

		// this will be the set of HIS residues that we'll replace with HID and HIE, so
		// we'll need to remove the original HIS ones
		Vector<Variable> toRemoveVars = new Vector<Variable>();

		// we'll have to remove energies where original HIS was involved
		Vector<Vector<Instance>> toRemoveFromBi = new Vector<Vector<Instance>>();

		// keep track of which old HIS residue each of the new HIS residues belongs to
		HashMap<Variable, Variable> newHisToOld = new HashMap<Variable, Variable>();

		// first split all of the HIS residues
		for (int i=0; i<residues.size(); i++){
			Variable v = residues.get(i);
			// if v is not a HIS residue then skip it
			if (!v.getName().substring(0, 3).equals("HIS")){
//				System.out.println("Skipped a non-HIS ("+v.getName()+").");
				continue;
			}
			// make two new variables with their instances, and mark v as one to be removed
			Variable vd = new Variable("HId_"+v.getName().substring(4)); // to encode the "1" state
			Instance vdProt = new Instance("HId_"+v.getName().substring(4), "PROT", vd, v.getInstance("1").getEnergy()); //3.3);
			Instance vdDeprot = new Instance("HId_"+v.getName().substring(4), "DEPROT", vd, 0.0);
			vd.addInstance(vdDeprot);
			vd.addInstance(vdProt); 
			newHisToOld.put(vd, v);

			Variable ve = new Variable("HIe_"+v.getName().substring(4)); // to encode the "2" state
			Instance veProt = new Instance("HIe_"+v.getName().substring(4), "PROT", vd, v.getInstance("2").getEnergy()); // 3.3);
			Instance veDeprot = new Instance("HIe_"+v.getName().substring(4), "DEPROT", vd, 0.0);
			ve.addInstance(veDeprot);
			ve.addInstance(veProt); 
			newHisToOld.put(ve, v);

			// add the interaction energies for the vd instances with the ve instances
			bi.put(vdProt, veProt, v.getInstance("1+2").getEnergy()-v.getInstance("1").getEnergy()-v.getInstance("2").getEnergy());
			bi.put(veProt, vdProt, v.getInstance("1+2").getEnergy()-v.getInstance("1").getEnergy()-v.getInstance("2").getEnergy());

			bi.put(vdProt, veDeprot, 0.0);
			bi.put(veDeprot, vdProt, 0.0);

			bi.put(vdDeprot, veProt, 0.0);
			bi.put(veProt, vdDeprot, 0.0);

			bi.put(vdDeprot, veDeprot, Double.MAX_VALUE);
			bi.put(veDeprot, vdDeprot, Double.MAX_VALUE);

			residues.add(vd); residues.add(ve);
			toRemoveVars.add(v);

		}

		// remove the pre-split HIS variables from residues
		residues.removeAll(toRemoveVars);

		// now will update the HIS-HIS and HIS-nonHIS energies
		for (int i=0; i<residues.size(); i++){
			Variable v = residues.get(i);
			boolean e = false; // is v an HIe residue?
			boolean d = false; // is v an HId residue?

			// if v is not a HIS residue then skip it
			if (!(v.getName().substring(0, 3).equals("HId") || v.getName().substring(0, 3).equals("HIe"))){
				continue;
			} else if (v.getName().substring(0,3).equals("HId")){
				e = false;
				d = true;
			} else if (v.getName().substring(0, 3).equals("HIe")){
				e = true;
				d = false;
			}

			// the two instances within v
			Instance vProt = v.getInstance("PROT");
			Instance vDeprot = v.getInstance("DEPROT");

			// the old HIS residue that we made v from
			Variable vOld = newHisToOld.get(v);
			// the 3 instances of that old HIS residue
			Instance vOld1 = vOld.getInstance("1");
			Instance vOld2 = vOld.getInstance("2");
			Instance vOld12 = vOld.getInstance("1+2");

			for (int j=0; j<residues.size(); j++){
				if (i==j)
					continue;

				Variable w = residues.get(j);

				// the two instances within w
				Instance wProt = w.getInstance("PROT");
				Instance wDeprot = w.getInstance("DEPROT");

				// declaring the old residue that w came from, if w is a HI(d,e) residue
				Variable wOld;

				if (w.getName().substring(0,3).equals("HId")){
					// w is an HId residue
					// we will only update the energy if the chainLoc of v is < the chainLoc of w
					if (v.getChainLoc() >= w.getChainLoc())
						continue;

					// the old HIS residue we made w from
					wOld = newHisToOld.get(w);
					// the instances of wOld that we will need within this if statement
					Instance wOld2 = wOld.getInstance("2");
					Instance wOld12 = wOld.getInstance("1+2");

//					System.out.println("v="+v.toString()+" ("+vOld+"), w="+w.toString()+" ("+wOld+")");

					if (e && !d){
						// v is an HIe and w is an HId
						// vHIe1, wHId1
						bi.put(vProt, wProt, bi.get(vOld12, wOld12)); // EVW(de,de)
						bi.put(wProt, vProt, bi.get(vOld12, wOld12));

						// vHIe1, wHId0
						bi.put(vProt, wDeprot, 0.0); // stay 0.0
						bi.put(wDeprot, vProt, 0.0);

						// vHIe0, wHId1
						bi.put(vDeprot, wProt, 0.0); // stay 0.0
						bi.put(wProt, vDeprot, 0.0);

						// vHIe1, wHId0
						bi.put(vDeprot, wDeprot, bi.get(vOld1, wOld2) - bi.get(vOld1, wOld12) - bi.get(vOld12, wOld2)); // EVW(d,e) - EVW(d,de) - EVW(de,e)
						bi.put(wDeprot, vDeprot, bi.get(vOld1, wOld2) - bi.get(vOld1, wOld12) - bi.get(vOld12, wOld2));


					} else if (!e && d){
						// v is an HId and w is an HId
						// vHId1, wHId1
						bi.put(vProt, wProt, 0.0); // stay 0.0
						bi.put(wProt, vProt, 0.0);

						// vHId1, wHId0
						bi.put(vProt, wDeprot, bi.get(vOld12, wOld2)); // EVW(de,e)
						bi.put(wDeprot, vProt, bi.get(vOld12, wOld2));

						// vHId0, wHId1
						bi.put(vDeprot, wProt, bi.get(vOld2, wOld12) - bi.get(vOld12, wOld12)); // EVW(e,de) - EVW(de,de)
						bi.put(wProt, vDeprot, bi.get(vOld2, wOld12) - bi.get(vOld12, wOld12));

						// vHId1, wHId0
						bi.put(vDeprot, wDeprot, bi.get(vOld2, wOld2)); // EVW(e,e)
						bi.put(wDeprot, vDeprot, bi.get(vOld2, wOld2));
					}

					Vector<Instance> temp = new Vector<Instance>(2);
					temp.add(0, vOld1); temp.add(1, wOld2);
					toRemoveFromBi.add(temp);
					temp.clear();
					temp.add(0, wOld2); temp.add(1, vOld1);
					toRemoveFromBi.add(temp);

					temp.clear();
					temp.add(0, vOld1); temp.add(1, wOld12);
					toRemoveFromBi.add(temp);
					temp.clear();
					temp.add(0, wOld12); temp.add(1, vOld1);
					toRemoveFromBi.add(temp);

					temp.clear();
					temp.add(0, vOld2); temp.add(1, wOld2);
					toRemoveFromBi.add(temp);
					temp.clear();
					temp.add(0, wOld2); temp.add(1, vOld2);
					toRemoveFromBi.add(temp);

					temp.clear();
					temp.add(0, vOld2); temp.add(1, wOld12);
					toRemoveFromBi.add(temp);
					temp.clear();
					temp.add(0, wOld12); temp.add(1, vOld2);
					toRemoveFromBi.add(temp);

					temp.clear();
					temp.add(0, vOld12); temp.add(1, wOld2);
					toRemoveFromBi.add(temp);
					temp.clear();
					temp.add(0, wOld2); temp.add(1, vOld12);
					toRemoveFromBi.add(temp);

					temp.clear();
					temp.add(0, vOld12); temp.add(1, wOld12);
					toRemoveFromBi.add(temp);
					temp.clear();
					temp.add(0, wOld12); temp.add(1, vOld12);
					toRemoveFromBi.add(temp);

				} else if (w.getName().substring(0,3).equals("HIe")){
					// w is an HIe residue
					// we will only update the energy if the chainLoc of v is < the chainLoc of w
					if (v.getChainLoc() >= w.getChainLoc())
						continue;
					// the old HIS residue we made w from
					wOld = newHisToOld.get(w);
					// the instances of wOld that we will need within this if statement
					Instance wOld1 = wOld.getInstance("1");
					Instance wOld12 = wOld.getInstance("1+2");

//					System.out.println("v="+v.toString()+" ("+vOld+"), w="+w.toString()+" ("+wOld+")");

					if (e && !d){
						// v is an HIe and w is an HIe
						// vHIe1, wHIe1
						bi.put(vProt, wProt, 0.0); // stay 0.0
						bi.put(wProt, vProt, 0.0);

						// vHIe1, wHIe0
						bi.put(vProt, wDeprot, bi.get(vOld12, wOld1) - bi.get(vOld12, wOld12)); // EVW(de,d) - EVW(de,de)
						bi.put(wDeprot, vProt, bi.get(vOld12, wOld1) - bi.get(vOld12, wOld12));

						// vHIe0, wHIe1
						bi.put(vDeprot, wProt, bi.get(vOld1, wOld12)); // EVW(d,de)
						bi.put(wProt, vDeprot, bi.get(vOld1, wOld12));

						// vHIe1, wHIe0
						bi.put(vDeprot, wDeprot, bi.get(vOld1, wOld1)); // EVW(d,d)
						bi.put(wDeprot, vDeprot, bi.get(vOld1, wOld1));

					} else if (!e && d){
						// v is an HId and w is an HIe
						// vHId1, wHIe1
						bi.put(vProt, wProt, 0.0); // stay 0.0
						bi.put(wProt, vProt, 0.0);

						// vHId1, wHIe0
						bi.put(vProt, wDeprot, 0.0); // stay 0.0
						bi.put(wDeprot, vProt, 0.0);

						// vHId0, wHIe1
						bi.put(vDeprot, wProt, 0.0); // stay 0.0
						bi.put(wProt, vDeprot, 0.0);

						// vHId0, wHIe0
						bi.put(vDeprot, wDeprot, bi.get(vOld2, wOld1) + bi.get(vOld12, wOld12) - bi.get(vOld2, wOld12) - bi.get(vOld12, wOld1)); // EVW(e,d) + EVW(de,de) - EVW(e,de) - EVW(de,d)
						bi.put(wDeprot, vDeprot, bi.get(vOld2, wOld1) + bi.get(vOld12, wOld12) - bi.get(vOld2, wOld12) - bi.get(vOld12, wOld1));
					}

					Vector<Instance> temp = new Vector<Instance>(2);
					temp.add(0, vOld1); temp.add(1, wOld1);
					toRemoveFromBi.add(temp);
					temp.clear();
					temp.add(0, wOld1); temp.add(1, vOld1);
					toRemoveFromBi.add(temp);

					temp.clear();
					temp.add(0, vOld1); temp.add(1, wOld12);
					toRemoveFromBi.add(temp);
					temp.clear();
					temp.add(0, wOld12); temp.add(1, vOld1);
					toRemoveFromBi.add(temp);

					temp.clear();
					temp.add(0, vOld2); temp.add(1, wOld1);
					toRemoveFromBi.add(temp);
					temp.clear();
					temp.add(0, wOld1); temp.add(1, vOld2);
					toRemoveFromBi.add(temp);

					temp.clear();
					temp.add(0, vOld2); temp.add(1, wOld12);
					toRemoveFromBi.add(temp);
					temp.clear();
					temp.add(0, wOld12); temp.add(1, vOld2);
					toRemoveFromBi.add(temp);

					temp.clear();
					temp.add(0, vOld12); temp.add(1, wOld1);
					toRemoveFromBi.add(temp);
					temp.clear();
					temp.add(0, wOld1); temp.add(1, vOld12);
					toRemoveFromBi.add(temp);

					temp.clear();
					temp.add(0, vOld12); temp.add(1, wOld12);
					toRemoveFromBi.add(temp);
					temp.clear();
					temp.add(0, wOld12); temp.add(1, vOld12);
					toRemoveFromBi.add(temp);

				} else {
					// w is not a HIS residue

//					System.out.println("v="+v.toString()+" ("+vOld+"), w="+w.toString());

					if (e && !d){
						// here v is an HIe and w is a non-HIS
						// vHIe1, w1
						bi.put(vProt, wProt, bi.get(vOld12, wProt)-bi.get(vOld1, wProt)); // EVW(de,1)-EVW(d,1)
						bi.put(wProt, vProt, bi.get(vOld12, wProt)-bi.get(vOld1, wProt));

						// vHIe1, w0
						bi.put(vProt, wDeprot, bi.get(vOld2, wDeprot)); // EVW(e,0)
						bi.put(wDeprot, vProt, bi.get(vOld2, wDeprot));

						// vHIe0, w1
						bi.put(vDeprot, wProt, 0.0); // stay 0.0
						bi.put(wProt, vDeprot, 0.0);

						// vHIe1, w0
						bi.put(vDeprot, wDeprot, bi.get(vOld1, wDeprot) + bi.get(vOld2, wDeprot) - bi.get(vOld12, wDeprot)); // EVW(d,0)+EVW(e,0)-EVW(de,0)
						bi.put(wDeprot, vDeprot, bi.get(vOld1, wDeprot) + bi.get(vOld2, wDeprot) - bi.get(vOld12, wDeprot));

					} else if (!e && d){
						// here v is an HId and w is a non-HIS
						// vHId1, w1
						bi.put(vProt, wProt, bi.get(vOld1, wProt)); // EVW(d,1)
						bi.put(wProt, vProt, bi.get(vOld1, wProt));

						// vHId1, w0
						bi.put(vProt, wDeprot, bi.get(vOld12, wDeprot)-bi.get(vOld2, wDeprot)); // EVW(de,0)-EVW(e,0)
						bi.put(wDeprot, vProt, bi.get(vOld12, wDeprot)-bi.get(vOld2, wDeprot));

						// vHId0, w1
						bi.put(vDeprot, wProt, bi.get(vOld1, wProt)+bi.get(vOld2, wProt)-bi.get(vOld12, wProt)); //EVW(d,1)+EVW(e,1)-EVW(de,1)
						bi.put(wProt, vDeprot, bi.get(vOld1, wProt)+bi.get(vOld2, wProt)-bi.get(vOld12, wProt));

						// vHId1, w0
						bi.put(vDeprot, wDeprot, 0.0); // stay 0.0
						bi.put(wDeprot, vDeprot, 0.0);

					}

					Vector<Instance> temp = new Vector<Instance>(2);
					temp.add(0, vOld1); temp.add(1, wProt);
					toRemoveFromBi.add(temp);
					temp.clear();
					temp.add(0, wProt); temp.add(1, vOld1);
					toRemoveFromBi.add(temp);

					temp.clear();
					temp.add(0, vOld1); temp.add(1, wDeprot);
					toRemoveFromBi.add(temp);
					temp.clear();
					temp.add(0, wDeprot); temp.add(1, vOld1);
					toRemoveFromBi.add(temp);

					temp.clear();
					temp.add(0, vOld2); temp.add(1, wProt);
					toRemoveFromBi.add(temp);
					temp.clear();
					temp.add(0, wProt); temp.add(1, vOld2);
					toRemoveFromBi.add(temp);

					temp.clear();
					temp.add(0, vOld2); temp.add(1, wDeprot);
					toRemoveFromBi.add(temp);
					temp.clear();
					temp.add(0, wDeprot); temp.add(1, vOld2);
					toRemoveFromBi.add(temp);

					temp.clear();
					temp.add(0, vOld12); temp.add(1, wProt);
					toRemoveFromBi.add(temp);
					temp.clear();
					temp.add(0, wProt); temp.add(1, vOld12);
					toRemoveFromBi.add(temp);

					temp.clear();
					temp.add(0, vOld12); temp.add(1, wDeprot);
					toRemoveFromBi.add(temp);
					temp.clear();
					temp.add(0, wDeprot); temp.add(1, vOld12);
					toRemoveFromBi.add(temp);

				}
			}
		}

		bi.removeAll(toRemoveFromBi);
	}


	/* * * * * * * * * * * *
	 *  SUPPORTING METHODS *
	 * * * * * * * * * * * */

	/**
	 * Runs through all configurations of the protein and calculates
	 * the energy of the configuration.
	 */
	public void configEnergies(){
		int[] numStates = new int[residues.size()];
		for (int i=0; i<residues.size(); i++){
			numStates[i] = residues.get(i).getInstances().size()-1;
		}

		VectorsMax vm = new VectorsMax(numStates);
		while (vm.hasNext()){
			int[] nxt = vm.next();
			stateEnergy(nxt);
		}

	}

	/**
	 * @param state An array of ints describing a state of the
	 * protein. state[i] is the number of the instance for residue i
	 */
	private void stateEnergy(int[] state){
//		System.out.println(Arrays.toString(state));
		double energy = 0.0;
		String stringState = "";
		for (int i=0; i<residues.size(); i++){
			energy += residues.get(i).getInstances().get(state[i]).getEnergy();
			stringState = stringState.concat(" "+residues.get(i).getInstances().get(state[i]).getName()+"_"+residues.get(i).getInstances().get(state[i]).getLabel());
			for (int j=i+1; j<residues.size(); j++){
				if (j==i)
					continue;
//				System.out.println(residues.get(i).getInstances().get(state[i])+" "+residues.get(j).getInstances().get(state[j]));
				energy += bi.get(residues.get(i).getInstances().get(state[i]), residues.get(j).getInstances().get(state[j]));
			}
		}
		System.out.println(stringState+" "+energy);
	}

	@Override
	public boolean contains(Variable res) {
		for (Variable r: residues){
			if (r.equals(res)){
				return true;
			}
		}
		return false;
	}

	@Override
	public void printProtein() {
		System.out.println();
		System.out.println("*****************");
		System.out.println("*    PROTEIN    *");
		System.out.println("*****************");
		System.out.println("ph = "+this.pH);
		for (Variable v: residues){
			System.out.println(v.toString());
			for (Instance i: v.getInstances()){
				System.out.println("  "+i.toString()+" with energy "+i.getEnergy());
			}
		}
		System.out.println();
	}

	@Override
	public void printEnergy() {
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

}
