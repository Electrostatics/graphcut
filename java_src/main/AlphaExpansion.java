package main;

import graph.BinaryGraph;
import hashtools.TwoKeyHash;

import java.util.HashMap;
import java.util.Set;
import java.util.Vector;

import returntools.Tuple2;
import syst.Instance;
import syst.Systm;
import syst.Variable;

public class AlphaExpansion {
	
	private final Systm origSyst;
	private HashMap<Variable, Instance> initLab;
	private Systm newSyst;
	private String alpha;
	
	/* * * * * * * * * *
	 *  CONSTRUCTORS   *
	 * * * * * * * * * */
	
	/**
	 * @param syst The starting Systm
	 * @param lab A labeling of syst to do an alpha-expansion from
	 * @param alpha A label
	 */
	public AlphaExpansion(Systm syst, HashMap<Variable, Instance> lab, String alpha){
		System.out.println("Starting the alpha expansion with alpha = "+alpha);
		
		// Instantiate the alpha-expansion object with the given Systm, labeling, and alpha.
		// Also set newSyst to be an empty Systm object. 
		origSyst = syst;
		newSyst = new Systm();
		initLab = lab;
		this.alpha = alpha;
		
		// instantiateNewSyst will make a new binary Systm where each variable has 
		// two labels: "move" and "stay". The label "move" means that the variable 
		// takes on the label alpha, and "stay" means it keeps the label it has 
		// in initLab.
		instantiateNewSyst();
		
		// Makes the normal form within newSyst
		newSyst.makeNF();
		
	}
	
	/* * * * * *
	 *  OTHER  *
	 * * * * * */
	
	/**
	 * @return The variable -> instance HashMap indicating the result 
	 * of the alpha-expansion
	 */
	public HashMap<Variable, Instance> doExpansion(){
		//Make the binary energy graph
		BinaryGraph bg = new BinaryGraph(newSyst,true);
		//Do the graph cut
		HashMap<Variable,Integer> result = bg.doCut();
		//System.out.println(result.toString());
		
		HashMap<Variable,Instance> newLab = new HashMap<Variable, Instance>();
		Vector<Variable> vars = origSyst.getVars();
		for (Variable v: vars){
			int vLab = result.get(newSyst.getVar(v.getName())); //System.out.println(v.toString()+" "+vLab);
			if (vLab==0){
				newLab.put(v, initLab.get(origSyst.getVar(v.getName())));
			}else if (vLab==1){
				newLab.put(v, origSyst.getVar(v.getName()).getInstance(alpha));
			}else{
				System.out.println("Variable "+v.toString()+" wasn't labeled");
			}
		}
		
		return newLab;
		
	}
	
	/**
	 * Creates newSyst from origSyst by keeping all Variables but only with
	 * labels "stay" and "move" (to alpha)
	 */
	private void instantiateNewSyst(){
		System.out.println("Setting up the binary version");
		
		// The new Systm will be binary with labels "stay" and "move"
		newSyst.setIsBinary(true);
		String[] labels = Systm.binaryLabels; // labels[0]="stay" and labels[1]="move"
		
		// Set the unary energies stored in each Instance as the energy field
		for (Variable v: origSyst.getVars()){
			Variable vp = new Variable(v.getName());
			
			if (initLab.get(v).getLabel().equals(alpha)){
				// if the variable is initially labeled alpha then there is no change
				// in energy if we "stay" and infinite if we "move" (i.e., we can't move)
				Instance vp0 = new Instance(vp.getName(), labels[0], vp, new Double(0));
				Instance vp1 = new Instance(vp.getName(), labels[1], vp, Double.MAX_VALUE);
				vp.addInstance(vp0); vp.addInstance(vp1);
				newSyst.addVar(vp);
			}else{
				if (v.getInstance(alpha) != null){
					// if the variable isn't initially labeled alpha, but alpha is a 
					// possible label for this variable then there is no change in
					// unary energy if we "stay" and if we "move" then the change in 
					// unary energy is E_v(alpha)-E_v(current label) 
					Instance vp0 = new Instance(vp.getName(), labels[0], vp, new Double(0));
					Instance vp1 = new Instance(vp.getName(), labels[1], vp, 
							v.getInstance(alpha).getEnergy() - v.getInstance(initLab.get(v).getLabel()).getEnergy());
					vp.addInstance(vp0); vp.addInstance(vp1);
					newSyst.addVar(vp);
				}else{
					// if the variable isn't initially labeled alpha, and alpha is not 
					// a possible label for this variable then there is no change in
					// unary energy if we "stay" and infinite if we "move" (i.e., we can't move)
					Instance vp0 = new Instance(vp.getName(), labels[0], vp, new Double(0));
					Instance vp1 = new Instance(vp.getName(), labels[1], vp, Double.MAX_VALUE);
					vp.addInstance(vp0); vp.addInstance(vp1);
					newSyst.addVar(vp);
				}
			}
		}
		
		// We will use the binary energy from origSyst when making the new binary energy.
		TwoKeyHash<Instance, Double> oldBi = origSyst.getBinary();
		TwoKeyHash<Instance, Double> newBi = new TwoKeyHash<Instance, Double>();
		for (Variable v: newSyst.getVars()){
			for (Variable w: newSyst.getVars()){
				if (v.equals(w)){
					//System.out.println("here");
					continue;
				}
				//System.out.println(v.toString()+" "+w.toString());
				Instance v0 = v.getInstance(labels[0]);
				Instance v1 = v.getInstance(labels[1]);
				Instance w0 = w.getInstance(labels[0]);
				Instance w1 = w.getInstance(labels[1]);
				
				boolean vIsAlpha = initLab.get(origSyst.getVar(v.getName())).getLabel().equals(alpha);
				boolean wIsAlpha = initLab.get(origSyst.getVar(w.getName())).getLabel().equals(alpha);
				
				if (vIsAlpha && wIsAlpha){
					// if both v and w are already labeled alpha then neither
					// can "move" so there is no change in energy for <v0, w0> and
					// infinite change in energy for all others
					newBi.put(v0, w0, new Double(0));
					newBi.put(v0, w1, Double.MAX_VALUE); 
					newBi.put(v1, w0, Double.MAX_VALUE);
					newBi.put(v1, w1, Double.MAX_VALUE);
										
				}else if (vIsAlpha && !wIsAlpha){
					// if v is labeled alpha and w is not labeled alpha then we can
					// move w but not v. 
					Instance origVOrig = origSyst.getVar(v.getName()).getInstance(alpha);
					
					Variable origW = origSyst.getVar(w.getName());
					Instance origWAlpha = origW.getInstance(alpha);
					Instance origWOrig = origW.getInstance(initLab.get(origW).getLabel());
					
					if (origWAlpha != null){
						// if w can be labeled alpha then its change in energy is 
						// energy(<valpha,walpha>)-energy(<valpha,worig>)
						newBi.put(v0, w1, oldBi.get(origVOrig, origWAlpha) - oldBi.get(origVOrig, origWOrig)); 
					}else{
						// if w cannot be labeled alpha then its change in energy if
						// we "move" it to alpha is infinite
						newBi.put(v0, w1, Double.MAX_VALUE); 
					}
					
					// change in energy for not moving v and w is zero, 
					// change in energy for moving v regardless of what
					// we do to w is infinite (since v is already labeled alpha)
					newBi.put(v0, w0, new Double(0));
					newBi.put(v1, w0, Double.MAX_VALUE);
					newBi.put(v1, w1, Double.MAX_VALUE);
					
				}else if (!vIsAlpha && wIsAlpha){
					// if w is labeled alpha and v is not labeled alpha then we can
					// move v but not w. 
					Variable origV = origSyst.getVar(v.getName());
					Instance origVAlpha = origV.getInstance(alpha);
					Instance origVOrig = origV.getInstance(initLab.get(origV).getLabel());
					
					Instance origWOrig = origSyst.getVar(w.getName()).getInstance(alpha);
					
					if (origVAlpha != null){
						// if v can be labeled alpha then its change in energy is 
						// energy(<valpha,walpha>)-energy(<vorig,walpha>)
						newBi.put(v1, w0, oldBi.get(origVAlpha, origWOrig) - oldBi.get(origVOrig, origWOrig));
					}else{
						// if v cannot be labeled alpha then its change in energy if
						// we "move" it to alpha is infinite
						newBi.put(v1, w0, Double.MAX_VALUE); 
					}
					
					// change in energy for not moving v and w is zero, 
					// change in energy for moving w regardless of what
					// we do to v is infinite (since w is already labeled alpha)
					newBi.put(v0, w0, new Double(0));
					newBi.put(v0, w1, Double.MAX_VALUE); 
					newBi.put(v1, w1, Double.MAX_VALUE);
					
				}else{
					// if both v and w are not originally labeled alpha then we can
					// move both v and w
					Variable origV = origSyst.getVar(v.getName());
					Instance origVAlpha = origV.getInstance(alpha);
					Instance origVOrig = origV.getInstance(initLab.get(origV).getLabel());
					
					Variable origW = origSyst.getVar(w.getName());
					Instance origWAlpha = origW.getInstance(alpha);
					Instance origWOrig = origW.getInstance(initLab.get(origW).getLabel());
					
					// binary energy depends on whether or not we can move v or w to alpha
					// if we can't (alpha isn't a possible label) then the energy
					// will be infinite
					if (origVAlpha != null && origWAlpha != null){
						newBi.put(v0, w1, oldBi.get(origVOrig, origWAlpha) - oldBi.get(origVOrig, origWOrig)); 
						newBi.put(v1, w0, oldBi.get(origVAlpha, origWOrig) - oldBi.get(origVOrig, origWOrig));
						newBi.put(v1, w1, oldBi.get(origVAlpha, origWAlpha) - oldBi.get(origVOrig, origWOrig));
					}else if (origVAlpha == null && origWAlpha != null){
						newBi.put(v0, w1, oldBi.get(origVOrig, origWAlpha) - oldBi.get(origVOrig, origWOrig)); 
						newBi.put(v1, w0, Double.MAX_VALUE);
						newBi.put(v1, w1, Double.MAX_VALUE);
					}else if (origVAlpha != null && origWAlpha == null){
						newBi.put(v0, w1, Double.MAX_VALUE);
						newBi.put(v1, w0, oldBi.get(origVAlpha, origWOrig) - oldBi.get(origVOrig, origWOrig));
						newBi.put(v1, w1, Double.MAX_VALUE);
					}else{
						newBi.put(v0, w1, Double.MAX_VALUE);
						newBi.put(v1, w0, Double.MAX_VALUE);
						newBi.put(v1, w1, Double.MAX_VALUE);
					}
					
					// change in energy for not moving v and w is zero, 
					newBi.put(v0, w0, new Double(0));
					
				}
				
			}
		}
		
		newSyst.setBinary(newBi);
		
		//newSyst.printSystm();
		//newSyst.printEnergy();
		//System.out.println();
	}
	
	
}
