package syst;

import java.util.Vector;


public class Variable {
	
	private String name;			// The name of the variable
	private Vector<Instance> inst;	// The vector of all instances associated to this variable
	private String[] labels;		// The array of all possible labels for this variable
	private Variable hat;			// The hatted version of this variable
	
	/* * * * * * * * * *
	 *  CONSTRUCTORS   *
	 * * * * * * * * * */
	
	/**
	 * Constructs a new Variable with as many Instances as
	 * there are Strings in labels.
	 * @param name The name of a new Variable
	 * @param labels The list of possible labels for the Variable
	 */
	public Variable(String name, String[] labels){
		this.name = name;
		this.labels = labels;
		inst = new Vector<Instance>();
		for (int i=0; i<labels.length; i++){
			Instance ins = new Instance(name, labels[i], this);
			inst.add(ins);
		}
	}
	
	/**
	 * Constructs a new Variable with the given Vector of Instances
	 * @param name The name of a new Variable
	 * @param inst The Vector of Instances attached to this Variable
	 */
	public Variable(String name, Vector<Instance> inst){
		this.name = name;
		this.inst = inst;
		String[] ls = new String[inst.size()];
		for (int i=0; i<inst.size(); i++){
			ls[i] = inst.get(i).getLabel();
		}
	}
	
	/**
	 * Constructs a new Variable with the given name and 
	 * an empty Vector of Instances
	 * @param name The name of a new Variable
	 */
	public Variable(String name){
		this.name = name;
		inst = new Vector<Instance>();
	}
	
	/* * * * * * * * * * * * *
	 *  GETTERS AND SETTERS  *
	 * * * * * * * * * * * * */
	
	public int getChainLoc(){
		return Integer.parseInt(name.substring(6));
	}
	
	/**
	 * @return The name of the Variable
	 */
	public String getName(){
		return name;
	}
	
	/**
	 * @return The Vector of Instances for the Variable
	 */
	public Vector<Instance> getInstances(){
		return inst;
	}
	
	/**
	 * @return The list of possible labels for the Variable
	 */
	public String[] getLabels(){
		if (labels != null){
			return labels;
		}else{
			String[] ret = new String[inst.size()];
			int i=0;
			for (Instance ins: inst){
				ret[i] = ins.getLabel();
				i++;
			}
			return ret;
		}
	}
	
	/**
	 * @param label String
	 * @return The instance of this Variable with the given label.
	 */
	public Instance getInstance(String label){
		for (Instance i: inst){
			if (i.getLabel().equals(label)){
				return i;
			}
		}

		return null;
	}

	/**
	 * @return The hatted version of this Variable
	 */
	public Variable getHat() {
		return hat;
	}

	/**
	 * @param H The hatted version of this Variable
	 */
	private void setHat(Variable H) {
		hat = H;
	}

	/**
	 * @param n The name of the Variable
	 */
	public void setName(String n){
		name = n;
	}
	
	/**
	 * @param i The Vector of Instances for the Variable
	 */
	public void setInstances(Vector<Instance> i){
		inst = i;
	}
	
	/**
	 * @param l The list of possible labels for the Variable
	 */
	public void setLabels(String[] l){
		labels = l;
	}
	
	/**
	 * Adds the new Instance to the list of Instances only if it is new
	 * @param ins An Instance of this Variable
	 */
	public void addInstance(Instance ins){
		if (!contains(ins)){
			inst.add(ins);
//			String[] labelsMore = new String[labels.length+1];
//			for (int i=0; i<labelsMore.length-1; i++){
//				labelsMore[i] = labels[i];
//			}
//			labelsMore[labels.length] = ins.getLabel();
		}else{
			//do nothing, we already have an Instance with this label
		}
	}
	
	/**
	 * Adds a new Instance to this Variable with the given label only
	 * if this instance isn't already here.
	 * @param label A label for a new Instance of this Variable
	 */
	public void addInstance(String label){
		Instance ins = new Instance(name, label, this);
		addInstance(ins);
	}
	
	public void removeInstance(Instance ins){
		inst.remove(ins);
		removeLabel(ins.getLabel());
	}
	
	public void removeInstances(Vector<Instance> inss){
		for (Instance i : inss){
			removeInstance(i);
		}
	}
	
	public void removeLabel(String label){
		String[] newLabels = new String[labels.length-1];
		int loc = 0;
		for (int i=0; i<labels.length; i++){
			if (!labels[i].equals(label)){
				newLabels[loc] = labels[i];
				loc++;
			}
		}
		labels = newLabels;
	}
	
	/* * * * * *
	 *  OTHER  *
	 * * * * * */
	
	public boolean isHIS(){
		if (name.substring(0, 3).equals("HId") || name.substring(0, 3).equals("HIe")){
			return true;
		} else {
			return false;
		}
	}
	
	public int hashCode() {
        int hash = 5;
        hash = 89*hash + (this.name != null ? this.name.hashCode() : 0);
        return hash;
    }


	/**
	 * @param var A Variable to compare this one to
	 * @return true if var is the same as this Variable, false otherwise
	 */
	public boolean equals(Variable var){
		String[] labels1 = this.getLabels();
		String[] labels2 = var.getLabels();
		boolean labelsEqual = true;
		if (labels1.length != labels2.length){
			labelsEqual = false;
		}else{
			for (int i=0; i<labels1.length; i++){
				if (!labels1[i].equals(labels2[i])){
					labelsEqual = false;
				}
			}
		}
		//if (name == var.getName() && this.getLabels() == var.getLabels()){
		//if (name == var.getName() && labels1.equals(labels2)){
		if (name.equals(var.getName()) && labelsEqual){
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * @param ins An Instance to look for
	 * @return true if the given Instance is contained in this Variable, false otherwise
	 */
	public boolean contains(Instance ins){
		for (Instance i: inst){
			if (i.equals(ins)){
				return true;
			}
		}
		return false;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		return name;
	}

	/**
	 * @return The "hatted" version of the variable. This is
	 * really just a dummy variable with the same name as
	 * the current variable except "_H" is tacked on to the end.
	 * The hatted variable has no Instances.
	 */
	public Variable makeHatVariable() {
		Variable H = new Variable(name+"_H");
		this.hat = H;
		H.setHat(this);
		return H;
	}

	
}
