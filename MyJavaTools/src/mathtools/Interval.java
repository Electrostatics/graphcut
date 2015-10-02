package mathtools;


public class Interval {
	
	private double left;
	private double right;
	
	public Interval(double l, double r){
		this.left = l;
		this.right = r;
	}
	
	public double getLeft(){
		return left;
	}
	
	public double getRight(){
		return right;
	}
	
	public double width(){
		return right - left;
	}
	
	public double midpoint(){
		return 0.5*(right+left);
	}
	
	public static boolean iWeakLess(Interval a, Interval b){
		return (a.getLeft() <= b.getLeft() && a.getRight() <= b.getRight());
	}
	
	public static boolean iContains(Interval small, Interval big){
		return (big.getRight() >= small.getRight() && big.getLeft() <= small.getRight());
	}
	
	public static boolean iStrongLess(Interval a, Interval b){
		return (a.getRight() <= b.getLeft());
	}
	
	public static double jaccard(Interval a, Interval b){
		
		if (iStrongLess(a, b) || iStrongLess(b, a)){
			return 0.0;
		} else if (iWeakLess(a,b)){
			return ((a.getRight()-b.getLeft())/(b.getRight() - a.getLeft()));
		} else if (iContains(a, b)){
			return (((double) a.width())/b.width());
		} else {
			return jaccard(b,a);
		}
	}
	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj){
		if (obj == null)
			return false;
		
		if (!(obj instanceof Interval))
            return false;
        if (obj == this)
            return true;

        Interval rhs = (Interval) obj;
        
		if (left == rhs.getLeft() && right == rhs.getRight()){
			return true;
		} else {
			return false;
		}
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString(){
		return "["+left+", "+right+"]";
	}
	
	// just omitted null checks
	@Override
	public int hashCode() {
		int hash = 3;
		hash = (int) (7 * hash + this.left);
		hash = (int) (7 * hash + this.right);
		return hash;
	}

}
