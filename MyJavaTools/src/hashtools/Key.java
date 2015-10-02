package hashtools;

public final class Key<K1, K2> {
    private final K1 part1;
    private final K2 part2;

    public Key(K1 part1, K2 part2) {
        this.part1 = part1;
        this.part2 = part2;
    }

    public K1 getFirst(){
    	return part1;
    }
    
    public K2 getSecond(){
    	return part2;
    }
    
    public Key<K2, K1> swap(){
    	return new Key<K2, K1>(part2, part1);
    }
    
    @Override public boolean equals(Object other) {
        if (!(other instanceof Key)) {
            return false;
        }
        // Can't find out the type arguments, unfortunately
        Key rawOther = (Key) other;
        // TODO: Handle nullity
        return part1.equals(rawOther.part1) &&
            part2.equals(rawOther.part2);
    }

    @Override public int hashCode() {
        // TODO: Handle nullity
        int hash = 23;
        hash = hash * 31 + part1.hashCode();
        hash = hash * 31 + part2.hashCode();
        return hash;
    }
    
    @Override public String toString(){
    	return "<"+part1.toString()+", "+part2.toString()+">";
    }

}