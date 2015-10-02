package syst;

public class Instance {
	
	private String name;		// The name of the instance (matches the name of its variable)
	private String label;		// The label of this instance
	
	private Variable var;		// The variable that this instance is associated with
	private Double energy;		// The unary energy for this instance
	private Double energyNF;	// The normal form unary energy for this instance
	
	/* * * * * * * * * *
	 *  CONSTRUCTORS   *
	 * * * * * * * * * */
	
	/**
	 * @param name The name of this Instance
	 * @param label The label for this Instance
	 * @param var The Variable that this is an Instance of
	 */
	public Instance(String name, String label, Variable var){
		this.name = name;
		this.label = label;
		this.var = var;
		energy = new Double(0);
		energyNF = energy;
	}
	
	/**
	 * @param name The name of this Instance
	 * @param label The label for this Instance
	 * @param var The Variable that this is an Instance of
	 * @param energy The unary energy for this Instance
	 */
	public Instance(String name, String label, Variable var, Double energy){
		this.name = name;
		this.label = label;
		this.var = var;
		this.energy = energy;
		energyNF = energy;
	}
	
	
	/* * * * * * * * * * * * *
	 *  GETTERS AND SETTERS  *
	 * * * * * * * * * * * * */
	
	/**
	 * @return The name of this Instance
	 */
	public String getName(){
		return name;
	}
	
	/**
	 * @return The label of this Instance
	 */
	public String getLabel(){
		return label;
	}
	
	/**
	 * @return The Variable that this is an Instance of
	 */
	public Variable getVar(){
		return var;
	}
	
	/**
	 * @return The unary energy associated to this Instance.
	 */
	public Double getEnergy(){
		return energy;
	}
	
	/**
	 * @return The normal form unary energy associated to this Instance
	 */
	public Double getEnergyNF(){
		return energyNF;
	}
	
	/**
	 * Sets the name of this Instance to be the given String
	 * @param n
	 */
	public void setName(String n){
		name = n;
	}
	
	/**
	 * Sets the label of this Instance to be the given String
	 * @param l
	 */
	public void setLabel(String l){
		label = l;
	}
	
	/**
	 * Sets the Variable associated to this Instance to be the given Variable 
	 * @param v
	 */
	public void setVar(Variable v){
		var = v;
	}
	
	/**
	 * @param d The energy (Double) of this Instance
	 */
	public void setEnergy(Double d){
		energy = d;
	}
	
	/**
	 * @param d The energy (double) of this Instance
	 */
	public void setEnergy(double d){
		energy = new Double(d);
	}
	
	/**
	 * @param d The normal form energy (Double) of this Instance
	 */
	public void setEnergyNF(Double d){
		energyNF = d;
	}
	
	/**
	 * @param d The normal form energy (double) of this Instance
	 */
	public void setEnergyNF(double d){
		energyNF = new Double(d);
	}
	
	/* * * * * *
	 *  OTHER  *
	 * * * * * */
	
	/**
	 * @param ins An Instance to compare this one to
	 * @return true if ins is the same as this Instance, false otherwise
	 */
	public boolean equals(Instance ins){
		if (name.equals(ins.getName()) && label.equals(ins.getLabel()) && var.equals(ins.getVar())){
			return true;
		}else{
			return false;
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		return name+" "+label;
	}
	
}
