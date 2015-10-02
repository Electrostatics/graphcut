package main;

import filetools.WriteFile;
import graph.BinaryGraph;

import java.util.HashMap;
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
