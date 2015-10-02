package syst;

import filetools.WriteFile;
import hashtools.TwoKeyHash;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Vector;

import mathtools.MapleTools;
import mathtools.VectorsMax;

public class Systm {

	private Vector<Variable> vars;					// Variables in the Systm
	private Vector<Variable> varsH;					// "Hatted" versions of the variables
	private HashMap<Variable, Variable> varToHat;	// HashMap mapping variables to their hatted versions

	private double con;								// energy constant
	private double conNF;							// normal form energy constant
	private TwoKeyHash<Instance, Double> bi;			// binary energy
	private TwoKeyHash<Instance, Double> biNF;		// normal form binary energy

	private boolean isBinary = false;				// is reset to true if the variables all have 2 possibilities

	public static final String[] binaryLabels = {"stay", "move"};

	/* * * * * * * * * *
	 *  CONSTRUCTORS   *
	 * * * * * * * * * */

	/**
	 * Constructs a new Systm with empty Variable list, constant = 0, and
	 * empty unary and binary energies.
	 */
	public Systm(){
		vars = new Vector<Variable>();
		con = 0;
		//un = new HashMap<Instance, Double>();
		bi = new TwoKeyHash<Instance, Double>();
	}

	/* * * * * * * * * * * * *
	 *  GETTERS AND SETTERS  *
	 * * * * * * * * * * * * */

	/**
	 * @param name The name of a variable to find
	 * @return The Variable in this Systm with the given
	 * name. If no such Variable exists, returns null
	 */
	public Variable getVar(String name){
		for (Variable v: vars){
			if (v.getName().equals(name)){
				return v;
			}
		}
		return null;
	}

	/**
	 * @return The vector of Variables for the Systm
	 */
	public Vector<Variable> getVars(){
		return vars;
	}

	/**
	 * @return The energy constant for the Systm
	 */
	public double getConstant(){
		return con;
	}

	/**
	 * @return The normal form energy constant for the Systm
	 */
	public double getConstantNF(){
		return conNF;
	}

	/**
	 * @return The binary energy (TwoKeyHash) for the Systm
	 */
	public TwoKeyHash<Instance, Double> getBinary(){
		return bi;
	}

	/**
	 * @return The normal form binary energy for the Systm
	 */
	public TwoKeyHash<Instance, Double> getBinaryNF(){
		return biNF;
	}

	/**
	 * @return true if this Systm is a binary Systm, false otherwise
	 */
	public boolean getIsBinary(){
		return isBinary;
	}

	/**
	 * @param v A Vector of Variable objects to set the Variables for the Systm
	 */
	public void setVars(Vector<Variable> v){
		vars = v;
	}

	/**
	 * @param c A double value to be the energy constant for the Systm
	 */
	public void setConstant(double c){
		con = c;
	}

	/**
	 * @param b A TwoKeyHash that stores the binary energy for the Systm
	 */
	public void setBinary(TwoKeyHash<Instance, Double> b){
		bi = b;
	}

	/**
	 * @param is true if this Systm is a binary Systm, false otherwise
	 */
	public void setIsBinary(boolean is){
		isBinary = is;
	}

	/**
	 * @param var A Variable object to add to the Systm
	 */
	public void addVar(Variable var){
		if (!contains(var)){
			vars.add(var);
		}else{
			//do nothing, we already have this Variable
		}
	}

	/**
	 * @param name The name of a Variable to add to the Systm
	 * @param labels The possible labels for this Variable
	 */
	public void addVar(String name, String[] labels){
		Variable var = new Variable(name, labels);
		addVar(var);
	}

	/* * * * * * * * * * *
	 *  SYMMETRIC STUFF  *
	 * * * * * * * * * * */

	public boolean isEnergySym(){
		boolean isSym = true;
		for (int i=0; i<vars.size(); i++){
			Variable v = vars.get(i);
			for (int j=i+1; j<vars.size(); j++){
				Variable w = vars.get(j);
					for (Instance vi: v.getInstances()){
					for (Instance wi: w.getInstances()){
						Double bi1 = bi.get(vi, wi);
						Double bi2 = bi.get(wi, vi);
						if (!bi1.equals(bi2)){
							//System.out.println(wi.toString()+" "+vi.toString()+" "+bi1+" "+bi2);
							return false;
						}
					}
				}
			}
		}

		return isSym;
	}

	public void makeEnergySym(){

		for (int i=0; i<vars.size(); i++){
			Variable v = vars.get(i);
			for (int j=i+1; j<vars.size(); j++){
				Variable w = vars.get(j);
				for (Instance vi: v.getInstances()){
					for (Instance wi: w.getInstances()){
						Double bi1 = bi.get(vi, wi);
						Double bi2 = bi.get(wi, vi);
						bi.put(vi, wi, bi1+bi2);
						bi.put(wi, vi, bi1+bi2);
					}
				}
			}
		}

	}

	/* * * * * * * * *
	 *  NORMAL FORM  *
	 * * * * * * * * */

	/**
	 * Makes the normal form energy for the Systm
	 * (creates biNF from bi)
	 */
	public void makeNF(){
		System.out.println("Making the normal form");
		int n = vars.size();
		biNF = bi.clone();

		for (int p=0; p<n; p++){
			Variable varp = vars.get(p);
			Vector<Instance> varpInst = varp.getInstances();

			for (Instance varp_i : varpInst){
				varp_i.setEnergyNF(varp_i.getEnergy());
			}

			for (int q=0; q<n; q++){
				if (p==q)
					continue;
				Variable varq = vars.get(q);
				Vector<Instance> varqInst = varq.getInstances();
				//System.out.println(varp.toString()+" "+varq.toString());
				for (Instance varp_i : varpInst){
					double[] energies = new double[varqInst.size()];
					for (int i=0; i<varqInst.size(); i++){
						energies[i] = biNF.get(varp_i, varqInst.get(i));
					}
					double min = MapleTools.min(energies);
					//System.out.println(varp.toString()+" "+varq.toString()+". Instance "+varp_i.toString()+" of "+varp.toString());
					//System.out.println("min is "+min);
					if (min != Double.MAX_VALUE){
						for (int i=0; i<varqInst.size(); i++){
							biNF.put(varp_i, varqInst.get(i), energies[i]-min);
							biNF.put(varqInst.get(i), varp_i, energies[i]-min);
							//System.out.println("  ("+varp_i.toString()+", "+varqInst.get(i)+") was "+energies[i]+" is now "+biNF.get(varp_i, varqInst.get(i) ));

						}

					}
					//System.out.print("  ("+varp_i.toString()+") was "+varp_i.getEnergyNF());
					varp_i.setEnergyNF(varp_i.getEnergyNF() + min);
					//System.out.println(" is now "+varp_i.getEnergyNF());
					//System.out.println();
				}
			}
		}

		for (int p=0; p<n; p++){
			Variable varp = vars.get(p);
			Vector<Instance> varpInst = varp.getInstances();
			double[] energies = new double[varpInst.size()];
			for (int i=0; i<varpInst.size(); i++){
				energies[i] = varpInst.get(i).getEnergyNF();
			}
			double min = MapleTools.min(energies);
			//System.out.println(varp.toString());
			//System.out.println("min is "+min);
			if (min != Double.MAX_VALUE){
				for (int i=0; i<varpInst.size(); i++){
					//System.out.print("  "+varpInst.get(i).toString()+" was "+varpInst.get(i).getEnergyNF());
					varpInst.get(i).setEnergyNF(varpInst.get(i).getEnergyNF()-min);
					//System.out.println(" is now "+varpInst.get(i).getEnergyNF());

				}
			}
			//System.out.println();
			System.out.print("NF constant was "+conNF);
			conNF += min;
			System.out.println(" is now "+conNF);
		}

//		Comparator<Vector<Instance>> comparator = new Comparator<Vector<Instance>>() {
//		  public int compare(Vector<Instance> o1, Vector<Instance> o2) {
//		    return o1.toString().compareTo(o2.toString());
//		  }
//		};
//		SortedSet<Vector<Instance>> sorted_keys = new TreeSet<Vector<Instance>>(comparator);
//		sorted_keys.addAll(biNF.keySet());
//
//		for(Vector<Instance> key: sorted_keys)
//		{
//			if (key.get(0).getName().equals(key.get(1).getName()))
//				continue;
//			if (key.get(0).getLabel().contains("PROT") &&
//				key.get(1).getLabel().contains("PROT"))
//				System.out.println(key.toString()+"="+biNF.get(key.get(0), key.get(1)).toString());
//		}
//		return;
	}

	/**
	 * Checks whether the NF energy agrees with the non-NF energy for
	 * each configuration of the Systm
	 * @return true if they agree, false otherwise
	 */
	public boolean checkNF(){
		int[] positions = new int[vars.size()];
		for (int i=0; i<vars.size(); i++){
			positions[i] = vars.get(i).getInstances().size()-1;
		}
		System.out.println(Arrays.toString(positions));
		VectorsMax vm = new VectorsMax(positions);
		boolean ret = true;
		while (vm.hasNext()){
			int[] next = vm.next();
			double e1 = evaluateEnergy(next,true);
			double e2 = evaluateEnergy(next,false);
			if (e1 != e2){
				ret = false;
				System.out.println(Arrays.toString(next)+" "+e1+" "+e2);
			}else{
				System.out.println(Arrays.toString(next)+" "+e1);
			}
		}

		return ret;
	}

	/* * * * * * *
	 *  LABELING *
	 * * * * * * */

	/**
	 * @return A HashMap mapping each Variable to one of its
	 * instances. The instance is chosen randomly for each Variable.
	 */
	public HashMap<Variable, Instance> randomLabeling(){
		HashMap<Variable,Instance> randLab = new HashMap<Variable,Instance>();
		for (int i=0; i<vars.size(); i++){
			Variable iVar = vars.get(i);
			String[] iLabels = iVar.getLabels(); //System.out.println(iVar.toString()+" with labels "+Arrays.toString(iLabels));
			int r = (int) Math.floor((iLabels.length)*Math.random());
			randLab.put(iVar, iVar.getInstance(iLabels[r]));
		}
		return randLab;
	}

	/**
	 * @return A vector containing all labels of all
	 * Variables in this Systm
	 */
	public Vector<String> allLabels(){
		Vector<String> ls = new Vector<String>();
		for (Variable v: vars){
			String[] vLabels = v.getLabels();
			for (int i=0; i<vLabels.length; i++){
				if (!ls.contains(vLabels[i])){
					ls.add(vLabels[i]);
				}
			}
		}
		return ls;
	}


	/* * * * * * * * *
	 *  ENERGY EVAL  *
	 * * * * * * * * */

	public void bruteForceMin(){
		int[] varLengths = new int[vars.size()];
		for (int i=0; i<vars.size(); i++){
			Variable iVar = vars.get(i);
			varLengths[i] = iVar.getInstances().size()-1;
		}

		double minEnergy = Double.MAX_VALUE;
		int[] minEnergyConfig = new int[vars.size()];
		VectorsMax vm = new VectorsMax(varLengths);
		boolean warn = true;

		while (vm.hasNext()){
			int[] config = vm.next();
			double energy = evaluateEnergy(config,false);

			if (energy < minEnergy){
				minEnergy = energy;
				minEnergyConfig = config;
			} else if (energy == minEnergy){
				if (warn){
					System.out.println("Warning: there are multiple configs with the minimum energy.");
					System.out.println(Arrays.toString(config));
				}else{
					System.out.println(Arrays.toString(config));
				}
			}
		}

		String[][] configLabels = new String[minEnergyConfig.length][2];
		for (int i=0; i<vars.size(); i++){
			Variable iVar = vars.get(i);
			configLabels[i][0] = iVar.toString();
			configLabels[i][1] = iVar.getInstances().get(minEnergyConfig[i]).getLabel();
		}

		System.out.println("The brute force minimum energy is "+minEnergy);
		System.out.println("with configuration \n"+Arrays.deepToString(configLabels));

	}

	public void bruteForceMin(WriteFile wr){
		int[] varLengths = new int[vars.size()];
		for (int i=0; i<vars.size(); i++){
			Variable iVar = vars.get(i);
			varLengths[i] = iVar.getInstances().size()-1;
		}

		double minEnergy = Double.MAX_VALUE;
		int[] minEnergyConfig = new int[vars.size()];
		if (vars.size() >= 25){
			wr.writeln("There are "+vars.size()+"variables, too many to do brute force");
		} else{
			VectorsMax vm = new VectorsMax(varLengths);
			boolean warn = true;

			while (vm.hasNext()){
				int[] config = vm.next();
				double energy = evaluateEnergy(config,false);

				if (energy < minEnergy){
					minEnergy = energy;
					minEnergyConfig = config;
				} else if (energy == minEnergy){
					if (warn){
						System.out.println("Warning: there are multiple configs with the minimum energy.");
						wr.writeln(Arrays.toString(config));
					}else{
						wr.writeln(Arrays.toString(config));
					}
				}
			}

			String[][] configLabels = new String[minEnergyConfig.length][2];
			for (int i=0; i<vars.size(); i++){
				Variable iVar = vars.get(i);
				configLabels[i][0] = iVar.toString();
				configLabels[i][1] = iVar.getInstances().get(minEnergyConfig[i]).getLabel();
			}

			wr.writeln("The brute force minimum energy is "+minEnergy);
			wr.writeln("with configuration \n"+Arrays.deepToString(configLabels));
		}
	}

	/**
	 * @param labeling A HashMap mapping each Variable to one of its Instances
	 * @param NF true if you want to evaluate the NF energy, false otherwise
	 * @return The energy of this Systm with the indicated labeling
	 */
	public double evaluateEnergy(HashMap<Variable, Instance> labeling, boolean NF){
		Double energy = new Double(0);
		try{
			for (int i=0; i<vars.size(); i++){
				Variable iVar = vars.get(i);
				Double foo = new Double(0);
				if (NF){
					foo = labeling.get(iVar).getEnergyNF();
				}else{
					foo = labeling.get(iVar).getEnergy();
				}
				energy += foo;

				for (int j=i+1; j<vars.size(); j++){
					Variable jVar = vars.get(j);
					Double bar = new Double(0);
					if (NF){
						bar = biNF.get(labeling.get(iVar), labeling.get(jVar));
					}else{
						bar = bi.get(labeling.get(iVar), labeling.get(jVar));
					}
					energy += bar;
				}
			}
		}catch (NullPointerException npe){
			return Double.MIN_VALUE;
		}

		if (NF){
			energy += conNF;
		}else{
			energy += con;
		}

		return energy;

	}

	/**
	 * @param labels An array of labels for vars
	 * @param NF A boolean, true if we are to evaluate the NF energy, false otherwise
	 * @return The energy of the Systm with Variable configurations given by labels
	 */
	public double evaluateEnergy(String[] labels, boolean NF){

		Double energy = new Double(0);
		for (int i=0; i<vars.size(); i++){
			Variable iVar = vars.get(i);
			Double foo = new Double(0);
			if (NF){
				foo = iVar.getInstance(labels[i]).getEnergyNF();
			}else{
				foo = iVar.getInstance(labels[i]).getEnergy();
			}
			energy += foo;

			for (int j=i+1; j<vars.size(); j++){
				Variable jVar = vars.get(j);
				Double bar = new Double(0);
				if (NF){
					bar = biNF.get(iVar.getInstance(labels[i]), jVar.getInstance(labels[j]));
				}else{
					bar = bi.get(iVar.getInstance(labels[i]), jVar.getInstance(labels[j]));
				}
				energy += bar;
			}
		}

		if (NF){
			energy += conNF;
		}else{
			energy += con;
		}

		return energy;
	}

	/**
	 * @param sub An array of integers indicating which Instance for each variable
	 * @param NF A boolean, true if we are to evaluate the NF energy, false otherwise
	 * @return The energy fo the Systm with Variable configurations given by sub
	 */
	public double evaluateEnergy(int[] sub, boolean NF) {
		Double energy = new Double(0);
		for (int i=0; i<vars.size(); i++){
			Variable iVar = vars.get(i);
			Double foo = new Double(0);
			if (NF){
				foo = iVar.getInstances().get(sub[i]).getEnergyNF();
			}else{
				foo = iVar.getInstances().get(sub[i]).getEnergy();
			}
			energy += foo;

			for (int j=i+1; j<vars.size(); j++){
				Variable jVar = vars.get(j);
				Double bar = new Double(0);
				if (NF){
					bar = biNF.get(iVar.getInstances().get(sub[i]), jVar.getInstances().get(sub[j]));
				}else{
					bar = bi.get(iVar.getInstances().get(sub[i]), jVar.getInstances().get(sub[j]));
				}
				energy += bar;
			}
		}

		if (NF){
			energy += conNF;
		}else{
			energy += con;
		}
		//System.out.println(Arrays.toString(sub)+" "+energy);

		return energy;

	}

	/**
	 * @param sub An array of {0,1} noting the state of the Systm
	 * @param NF A boolean, true if we are to evaluate the NF energy, false otherwise
	 * @return The energy of the Systm in the configuration given by sub
	 */
	public double evaluateBinaryEnergy(int[] sub, boolean NF) {

		Double energy = new Double(0);
		for (int i=0; i<vars.size();i++){
			Variable iVar = vars.get(i);
			Instance iVar0 = iVar.getInstance(binaryLabels[0]);
			Instance iVar1 = iVar.getInstance(binaryLabels[1]);
			Double foo = new Double(0);
			if (NF){
				if (sub[i] == 0){
					foo = iVar0.getEnergyNF();
				}else{
					foo = iVar1.getEnergyNF();
				}
			}else{
				if (sub[i] == 0){
					try{
						foo = iVar0.getEnergy();
					}catch(NullPointerException npe){
						System.out.println(iVar.toString() + iVar0.toString());
					}
				}else{
					foo = iVar1.getEnergy();
				}
			}
			energy += foo;

			for (int j=i+1; j<vars.size(); j++){
				Variable jVar = vars.get(j);
				Instance jVar0 = jVar.getInstance(binaryLabels[0]);
				Instance jVar1 = jVar.getInstance(binaryLabels[1]);
				Double bar;
				if (NF){
					if (sub[i]==0 && sub[j]==0){
						bar = biNF.get(iVar0, jVar0);
					}else if (sub[i] == 0 && sub[j] == 1){
						bar = biNF.get(iVar0, jVar1);
					}else if (sub[i] == 1 && sub[j] == 0){
						bar = biNF.get(iVar1, jVar0);
					}else{
						bar = biNF.get(iVar1, jVar1);
					}
				}else{
					if (sub[i]==0 && sub[j]==0){
						//bar = (double) matrixS.get(iResU, jResU);
						bar = bi.get(iVar0, jVar0);
					}else if (sub[i] == 0 && sub[j] == 1){
						//bar = (double) matrixS.get(iResU, jResT);
						bar = bi.get(iVar0, jVar1);
					}else if (sub[i] == 1 && sub[j] == 0){
						//bar = (double) matrixS.get(iResT, jResU);
						bar = bi.get(iVar1, jVar0);
					}else{
						//bar = (double) matrixS.get(iResT, jResT);
						bar = bi.get(iVar1, jVar1);
					}
				}

				energy += bar;
			}
		}
		if (NF){
			energy += conNF;
		}
		else{
			energy += con;
		}

		return energy;

	}

	/* * * * * * * * * *
	 *  SUPPLEMENTARY  *
	 * * * * * * * * * */

	/**
	 * For each Variable in this Systm makes a "hatted" Variable.
	 * This will be used in the BinaryGraph class.
	 */
	public void makeHats() {

		varsH = new Vector<Variable>();
		varToHat = new HashMap<Variable,Variable>();

		for( int i=0; i<vars.size(); i++){
			Variable iVar = vars.get(i);
			iVar.makeHatVariable();

			varsH.add(iVar.getHat());
			varToHat.put(iVar, iVar.getHat());
		}

	}

	/**
	 * @param var A Variable to look for
	 * @return true if the given Variable is contained in the Systm, false otherwise
	 */
	public boolean contains(Variable var){
		for (Variable v: vars){
			if (v.equals(var)){
				return true;
			}
		}
		return false;
	}

	/* * * * * * *
	 *  PRINTING *
	 * * * * * * */

	/**
	 * Prints the variables and instances of the Systm
	 */
	public void printSystm(){
		for (Variable v: vars){
			System.out.println("Variable "+v.toString()+" with Instances:");
			for (Instance i: v.getInstances()){
				System.out.println(i.toString());
			}
			System.out.println();
		}
	}

	public void printSystm(WriteFile wr){
		for (Variable v: vars){
			wr.writeln("Variable "+v.toString()+" with Instances:");
			for (Instance i: v.getInstances()){
				wr.writeln(i.toString());
			}
			wr.writeln();
		}
	}

	/**
	 * Prints the unary energy for the Systm
	 */
	public void printUnEnergy(){
		System.out.println("UNARY:");
		for (Variable v: vars){
			System.out.println("Energy for variable "+v.toString());
			for (Instance i: v.getInstances()){
				System.out.println("   "+i.toString()+": "+i.getEnergy());
			}
		}
		System.out.println();
	}

	public void printUnEnergy(WriteFile wr){
		wr.writeln("UNARY:");
		for (Variable v: vars){
			wr.writeln("Energy for variable "+v.toString());
			for (Instance i: v.getInstances()){
				wr.writeln("   "+i.toString()+": "+i.getEnergy());
			}
		}
		wr.writeln();
	}


	/**
	 * Prints the normal form unary energy for the Systm
	 */
	public void printUnNFEnergy(){
		System.out.println("UNARY NF:");
		for (Variable v: vars){
			System.out.println("Energy for variable "+v.toString());
			for (Instance i: v.getInstances()){
				System.out.println("   "+i.toString()+": "+i.getEnergyNF());
			}
		}
		System.out.println();
	}

	/**
	 * Prints the binary energy for the Systm
	 */
	public void printBiEnergy(){
		System.out.println("BINARY:");
		for (Variable v: vars){
			for (Instance i: v.getInstances()){
				for (Variable w: vars){
					if (w.equals(v))
						continue;
					for (Instance j: w.getInstances()){
						System.out.println("("+i.toString()+", "+j.toString()+"): "+bi.get(i, j));
					}
				}
			}
		}
	}

	public void printBiEnergy(WriteFile wr){
		wr.writeln("BINARY:");
		for (Variable v: vars){
			for (Instance i: v.getInstances()){
				for (Variable w: vars){
					if (w.equals(v))
						continue;
					for (Instance j: w.getInstances()){
						wr.writeln("("+i.toString()+", "+j.toString()+"): "+bi.get(i, j));
					}
				}
			}
		}
	}

	/**
	 * Prints the normal form binary energy for the Systm
	 */
	public void printBiNFEnergy(){
		System.out.println("BINARY NF:");
		for (Variable v: vars){
			for (Instance i: v.getInstances()){
				for (Variable w: vars){
					if (w.equals(v))
						continue;
					for (Instance j: w.getInstances()){
						System.out.println("("+i.toString()+", "+j.toString()+"): "+biNF.get(i, j));
					}
				}
			}
		}
	}

	/**
	 * Prints the constant, unary, and binary energies for the Systm
	 */
	public void printEnergy(){
		System.out.println("CONSTANT: c="+con); System.out.println();
		printUnEnergy();
		printBiEnergy();
	}

	public void printEnergy(WriteFile wr){
		wr.writeln("CONSTANT: c="+con); wr.writeln();
		printUnEnergy(wr);
		printBiEnergy(wr);
	}

	/**
	 * Prints the normal form constant, unary, and binary energies for the Systm
	 */
	public void printNFEnergy(){
		System.out.println("CONSTANT NF: c="+conNF); System.out.println();
		printUnNFEnergy();
		printBiNFEnergy();
	}







}
