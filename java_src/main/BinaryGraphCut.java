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
			for (Variable var1 : sys.getVars()){
				for (Instance inst1 : var1.getInstances()){
					for (Variable var2 : sys.getVars()){
						if (var1.equals(var2))
							continue;
						for (Instance inst2 : var2.getInstances()){
							if (inst1.getLabel().equals("PROT") && inst2.getLabel().equals("PROT")){
								wr.writeln("("+inst1.getName()+"_PROTONATED, "+
										inst2.getName()+"_PROTONATED) "+
										Math.round(sys.getBinary().get(inst1, inst2)*10000.0)/10000.0);
							}else if (inst1.getLabel().equals("PROT") && inst2.getLabel().equals("DEPROT")){
								wr.writeln("("+inst1.getName()+"_PROTONATED, "+inst2.getName()+"_DEPROTONATED) "+Math.round(sys.getBinary().get(inst1, inst2)*10000.0)/10000.0);
							}else if (inst1.getLabel().equals("DEPROT") && inst2.getLabel().equals("PROT")){
								wr.writeln("("+inst1.getName()+"_DEPROTONATED, "+inst2.getName()+"_PROTONATED) "+Math.round(sys.getBinary().get(inst1, inst2)*10000.0)/10000.0);
							}else if (inst1.getLabel().equals("DEPROT") && inst2.getLabel().equals("DEPROT")){
								wr.writeln("("+inst1.getName()+"_DEPROTONATED, "+inst2.getName()+"_DEPROTONATED) "+Math.round(sys.getBinary().get(inst1, inst2)*10000.0)/10000.0);
							}
						}
					}
				}
				
			}
			
//			Set<Vector<Instance>> keys = sys.getBinary().keySet();
//			for (Vector<Instance> key : keys){
//				Instance key1 = key.get(0);
//				Instance key2 = key.get(1);
//				if (key1.getLabel().equals("PROT") && key2.getLabel().equals("PROT")){
//					wr.writeln("("+key1.getName()+"_PROTONATED, "+key2.getName()+"_PROTONATED) "+Math.round(sys.getBinary().get(key1, key2)*10000.0)/10000.0);
//				}else if (key1.getLabel().equals("PROT") && key2.getLabel().equals("DEPROT")){
//					wr.writeln("("+key1.getName()+"_PROTONATED, "+key2.getName()+"_DEPROTONATED) "+Math.round(sys.getBinary().get(key1, key2)*10000.0)/10000.0);
//				}else if (key1.getLabel().equals("DEPROT") && key2.getLabel().equals("PROT")){
//					wr.writeln("("+key1.getName()+"_DEPROTONATED, "+key2.getName()+"_PROTONATED) "+Math.round(sys.getBinary().get(key1, key2)*10000.0)/10000.0);
//				}else{
//					wr.writeln("("+key1.getName()+"_DEPROTONATED, "+key2.getName()+"_DEPROTONATED) "+Math.round(sys.getBinary().get(key1, key2)*10000.0)/10000.0);
//				}
//			}
			// unary
			for (Variable var : sys.getVars()){
				for (Instance inst : var.getInstances()){
					if (inst.getLabel().equals("PROT")){
						wr.writeln(inst.getName()+"_PROTONATED "+Math.round(inst.getEnergy()*10000.0)/10000.0);
					}else if (inst.getLabel().equals("DEPROT")){
						wr.writeln(inst.getName()+"_DEPROTONATED "+Math.round(inst.getEnergy()*10000.0)/10000.0);
					}else{
						wr.writeln(inst.toString()+"_OTHER "+Math.round(inst.getEnergy()*10000.0)/10000.0);
					}
				}
			}
			wr.writeln();
			
			wr.writeln("NORMAL FORM ENERGIES");
			// binary
			
			for (Variable var1 : sys.getVars()){
				for (Instance inst1 : var1.getInstances()){
					for (Variable var2 : sys.getVars()){
						if (var1.equals(var2))
							continue;
						for (Instance inst2 : var2.getInstances()){
							if (inst1.getLabel().equals("PROT") && inst2.getLabel().equals("PROT")){
								wr.writeln("("+inst1.getName()+"_PROTONATED, "+inst2.getName()+"_PROTONATED) "+Math.round(sys.getBinaryNF().get(inst1, inst2)*10000.0)/10000.0);
							}else if (inst1.getLabel().equals("PROT") && inst2.getLabel().equals("DEPROT")){
								wr.writeln("("+inst1.getName()+"_PROTONATED, "+inst2.getName()+"_DEPROTONATED) "+Math.round(sys.getBinaryNF().get(inst1, inst2)*10000.0)/10000.0);
							}else if (inst1.getLabel().equals("DEPROT") && inst2.getLabel().equals("PROT")){
								wr.writeln("("+inst1.getName()+"_DEPROTONATED, "+inst2.getName()+"_PROTONATED) "+Math.round(sys.getBinaryNF().get(inst1, inst2)*10000.0)/10000.0);
							}else if (inst1.getLabel().equals("DEPROT") && inst2.getLabel().equals("DEPROT")){
								wr.writeln("("+inst1.getName()+"_DEPROTONATED, "+inst2.getName()+"_DEPROTONATED) "+Math.round(sys.getBinaryNF().get(inst1, inst2)*10000.0)/10000.0);
							}
						}
					}
				}
				
			}
			
//			Set<Vector<Instance>> keysNF = sys.getBinaryNF().keySet();
//			for (Vector<Instance> key : keysNF){
//				Instance key1 = key.get(0);
//				Instance key2 = key.get(1);
//				if (key1.getLabel().equals("PROT") && key2.getLabel().equals("PROT")){
//					wr.writeln("("+key1.getName()+"_PROTONATED, "+key2.getName()+"_PROTONATED) "+Math.round(sys.getBinaryNF().get(key1, key2)*10000.0)/10000.0);
//				}else if (key1.getLabel().equals("PROT") && key2.getLabel().equals("DEPROT")){
//					wr.writeln("("+key1.getName()+"_PROTONATED, "+key2.getName()+"_DEPROTONATED) "+Math.round(sys.getBinaryNF().get(key1, key2)*10000.0)/10000.0);
//				}else if (key1.getLabel().equals("DEPROT") && key2.getLabel().equals("PROT")){
//					wr.writeln("("+key1.getName()+"_DEPROTONATED, "+key2.getName()+"_PROTONATED) "+Math.round(sys.getBinaryNF().get(key1, key2)*10000.0)/10000.0);
//				}else{
//					wr.writeln("("+key1.getName()+"_DEPROTONATED, "+key2.getName()+"_DEPROTONATED) "+Math.round(sys.getBinaryNF().get(key1, key2)*10000.0)/10000.0);
//				}
//			}
			// unary
			for (Variable var : sys.getVars()){
				for (Instance inst : var.getInstances()){
					if (inst.getLabel().equals("PROT")){
						wr.writeln(inst.getName()+"_PROTONATED "+Math.round(inst.getEnergyNF()*10000.0)/10000.0);
					}else if (inst.getLabel().equals("DEPROT")){
						wr.writeln(inst.getName()+"_DEPROTONATED "+Math.round(inst.getEnergyNF()*10000.0)/10000.0);
					}else{
						wr.writeln(inst.toString()+"_OTHER "+Math.round(inst.getEnergyNF()*10000.0)/10000.0);
					}
				}
			}
			// constant
			wr.writeln("Normalized constant energy: "+Math.round(sys.getConstantNF()*10000.0)/10000.0);
			
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
