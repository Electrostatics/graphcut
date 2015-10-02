package prot;

import hashtools.TwoKeyHash;

import java.util.Vector;

import syst.Instance;
import syst.Systm;
import syst.Variable;

public class ProteinToSystm {
	
	private Protein prot;
	private Systm sys;
	
	public ProteinToSystm(Protein prot){
		this.prot = prot;
		sys = new Systm();
	}
	
	public Systm getSystm(){
		return sys;
	}
	
	public void makeSystm(){
		Vector<Variable> vars = prot.getResidues();
		TwoKeyHash<Instance, Double> bi = prot.getBinary();
		
		for (Variable v: vars){
			sys.addVar(v);
		}
		
		sys.setBinary(bi);
		
	}
	
	

}
