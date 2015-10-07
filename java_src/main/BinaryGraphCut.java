package main;

import filetools.WriteFile;
import graph.BinaryGraph;

import java.util.HashMap;
import java.util.Set;
import java.util.Vector;

import syst.Instance;
import syst.Systm;
import syst.Variable;

public class BinaryGraphCut {
	
	private Systm sys;
	private BinaryGraph sysBiGr;
	
	/* * * * * * * * * *
	 *  CONSTRUCTORS   *
	 * * * * * * * * * */
	
	public BinaryGraphCut(Systm sys){
		this.sys = sys;
	}
	
	/* * * * * *
	 *  OTHER  *
	 * * * * * */
	
	private boolean checkBinary(){
		boolean isBi = true;
		for (Variable v: sys.getVars()){
			Vector<Instance> vinst = v.getInstances();
			if (vinst.size() != 2){
				isBi = false;
				break;
			}
		}
		sys.setIsBinary(isBi);
		return isBi;
	}
	
	public void makeBiGr(){
		if (!checkBinary()){
			System.out.println("Can't do binary graph cut, Systm isn't binary.");
		}else{
			sys.makeNF();
			sysBiGr = new BinaryGraph(sys, false);
		}
	}
	
	public void makeBiGr(WriteFile wr){
		if (!checkBinary()){
			System.out.println("Can't do binary graph cut, Systm isn't binary.");
		}else{
			sys.makeNF();
			
			wr.writeln("REGULAR ENERGIES");
			// binary
			Set<Vector<Instance>> keys = sys.getBinary().keySet();
			for (Vector<Instance> key : keys){
				Instance key1 = key.get(0);
				Instance key2 = key.get(1);
				if (key1.getLabel().equals("PROT") && key2.getLabel().equals("PROT")){
					wr.writeln("("+key1.getName()+"_PROTONATED, "+key2.getName()+"_PROTONATED) "+sys.getBinary().get(key1, key2));
				}else if (key1.getLabel().equals("PROT") && key2.getLabel().equals("DEPROT")){
					wr.writeln("("+key1.getName()+"_PROTONATED, "+key2.getName()+"_DEPROTONATED) "+sys.getBinary().get(key1, key2));
				}else if (key1.getLabel().equals("DEPROT") && key2.getLabel().equals("PROT")){
					wr.writeln("("+key1.getName()+"_DEPROTONATED, "+key2.getName()+"_PROTONATED) "+sys.getBinary().get(key1, key2));
				}else{
					wr.writeln("("+key1.getName()+"_DEPROTONATED, "+key2.getName()+"_DEPROTONATED) "+sys.getBinary().get(key1, key2));
				}
			}
			// unary
			for (Variable var : sys.getVars()){
				for (Instance inst : var.getInstances()){
					if (inst.getLabel().equals("PROT")){
						wr.writeln(inst.getName()+"_PROTONATED "+inst.getEnergy());
					}else if (inst.getLabel().equals("DEPROT")){
						wr.writeln(inst.getName()+"_DEPROTONATED "+inst.getEnergy());
					}else{
						wr.writeln(inst.toString()+"_OTHER "+inst.getEnergy());
					}
				}
			}
			wr.writeln();
			
			wr.writeln("NORMAL FORM ENERGIES");
			// binary
			Set<Vector<Instance>> keysNF = sys.getBinaryNF().keySet();
			for (Vector<Instance> key : keysNF){
				Instance key1 = key.get(0);
				Instance key2 = key.get(1);
				if (key1.getLabel().equals("PROT") && key2.getLabel().equals("PROT")){
					wr.writeln("("+key1.getName()+"_PROTONATED, "+key2.getName()+"_PROTONATED) "+sys.getBinaryNF().get(key1, key2));
				}else if (key1.getLabel().equals("PROT") && key2.getLabel().equals("DEPROT")){
					wr.writeln("("+key1.getName()+"_PROTONATED, "+key2.getName()+"_DEPROTONATED) "+sys.getBinaryNF().get(key1, key2));
				}else if (key1.getLabel().equals("DEPROT") && key2.getLabel().equals("PROT")){
					wr.writeln("("+key1.getName()+"_DEPROTONATED, "+key2.getName()+"_PROTONATED) "+sys.getBinaryNF().get(key1, key2));
				}else{
					wr.writeln("("+key1.getName()+"_DEPROTONATED, "+key2.getName()+"_DEPROTONATED) "+sys.getBinaryNF().get(key1, key2));
				}
			}
			// unary
			wr.write("Instance NF energies: ");
			for (Variable var : sys.getVars()){
				for (Instance inst : var.getInstances()){
					if (inst.getLabel().equals("PROT")){
						wr.writeln(inst.getName()+"_PROTONATED "+inst.getEnergyNF());
					}else if (inst.getLabel().equals("DEPROT")){
						wr.writeln(inst.getName()+"_DEPROTONATED "+inst.getEnergyNF());
					}else{
						wr.writeln(inst.toString()+"_OTHER "+inst.getEnergyNF());
					}
				}
			}
			// constant
			wr.writeln("Normalized constant energy: "+sys.getConstantNF());
			
			sysBiGr = new BinaryGraph(sys, false, wr);
		}
	}

	public void minimize(){
		//makeBiGr();
		HashMap<Variable, Instance> ans = sysBiGr.doCutNonAE();
		System.out.println(ans.toString());
	}
	
	public void minimize(WriteFile wr){
		//makeBiGr();
		HashMap<Variable, Instance> ans = sysBiGr.doCutNonAE(wr);
		wr.writeln(ans.toString());
	}
	
	public HashMap<Variable, Instance> minimizeAns(WriteFile wr){
		HashMap<Variable, Instance> ans = sysBiGr.doCutNonAE(wr);
		return ans;
	}
	
	public HashMap<Variable, Instance> minimizeAns(WriteFile wr, WriteFile sec){
		HashMap<Variable, Instance> ans = sysBiGr.doCutNonAE(wr, sec);
		return ans;
	}
	
}
