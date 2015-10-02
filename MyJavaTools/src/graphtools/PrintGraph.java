package graphtools;

import org.jgrapht.Graph;

public class PrintGraph {

	public PrintGraph(){
		
	}
	
	public static <V, E> void printGraph(Graph<V, E> G, boolean wt){
		String Vs = vertexString(G);
		String Es = edgeString(G, wt);
		System.out.println(Vs+"\n"+Es+"\n");
	}
	
	private static <V, E> String vertexString(Graph<V, E> G){
		String Vs = "V: ";
		for (V v : G.vertexSet()){
			Vs += v.toString()+", ";
		}
		return Vs;
	}
	
	private static <V, E> String edgeString(Graph<V, E> G, boolean wt){
		String Es = "E: ";
		for (E e : G.edgeSet()){
			Es += "("+G.getEdgeSource(e).toString()+", "+G.getEdgeTarget(e).toString();
			if (wt)
				Es += ": "+G.getEdgeWeight(e)+"), ";
			else
				Es += "), ";
		}
		return Es;
	}
}
