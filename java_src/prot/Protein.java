package prot;

import hashtools.TwoKeyHash;

import java.util.Vector;

import syst.Instance;
import syst.Variable;

public interface Protein {

	/**
	 * @return The Vector of Variables representing the residues of the protein
	 */
	public Vector<Variable> getResidues();
	
	/**
	 * @return The energy constant
	 */
	public double getConstant();
	
	/**
	 * @return The binary energy
	 */
	public TwoKeyHash<Instance, Double> getBinary();
	
	/**
	 * @param name The name of the residue
	 * @param chain The chain location of the residue
	 * @param loc The location of the residue within the chain
	 * @return The residue with name "name_chain_loc"
	 */
	public Variable getResidue(String name, String chain, String loc);
	
	/**
	 * @param name The name of the residue
	 * @param chain The chain location of the residue
	 * @param loc The location of the residue within the chain
	 * @return The instance with label = name within the
	 * residue sitting at "chain_loc"
	 */
	public Instance getInstance(String name, String chain, String loc);
	
	public double getTemp();
	
	/**
	 * Adds the instance with label "name" sitting  at chain_loc
	 * @param name The label of the instance
	 * @param chain The chain that the instance is sitting on
	 * @param loc The location within the chain for the instance
	 */
	public void addInstance(String name, String chain, String loc);
	
	/**
	 * Adds the given residue without adding any instances
	 * @param name The name of the residue
	 * @param chain The chain that the residue sits on
	 * @param loc The location within the chain for the residue
	 */
	public void addResidueSimple(String name, String chain, String loc);
	
	/**
	 * Adds the given residue if it isn't already there
	 * @param res A residue to add to the protein
	 */
	public void addResidue(Variable res);
	
	/**
	 * Adds the given residue along with all of the instances
	 * that should be there according to resToTaut
	 * @param name The name of the residue
	 * @param chain The chain that the residue sits on
	 * @param loc The location within the chain for the residue
	 */
	public void addResidue(String name, String chain, String loc);
	
	/**
	 * Change the pH. Used in pKa calculations when we incrementally increase the pH. 
	 * @param pH
	 */
	public void setPH(double pH);
	
	/**
	 * @param i1 One instance
	 * @param i2 Another instance
	 * @param ene The interaction energy between the two given instances 
	 */
	public void setEnergy(Instance i1, Instance i2, Double ene);
	
	/**
	 * Sets the binary energy of the protein to be the given binary energy 
	 * @param bi The binary energy for the protein
	 */
	public void setBi(TwoKeyHash<Instance, Double> bi);
	
	/**
	 * Makes sure that each residue of the protein has two choices, prot or deprot.
	 * If any residue has more than one choice (besides HIS) then it is turned into
	 * a binary residue. If any residue has only one choice then its energy is 
	 * put into the constant and the residue is removed from consideration. HIS is 
	 * dealt with separately.
	 */
	public void simplify();
	
	/**
	 * @param var A Variable to look for
	 * @return true if the given Variable is contained in the Systm, false otherwise
	 */
	public boolean contains(Variable res);
	public void printProtein();
	public void printEnergy();
}
