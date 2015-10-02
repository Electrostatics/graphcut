package test;

import java.util.Arrays;
import java.util.Set;

import filetools.WriteFile;
import generaltools.ErdosRenyiGenerator;

import org.jgrapht.VertexFactory;
import org.jgrapht.generate.ScaleFreeGraphGenerator;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

public class GRAD_Graphs {

	public static WeightedMultigraph<Integer, DefaultWeightedEdge> G;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String type = "ER";
		int n = 100;
		int path = 10;
		double p = 0.9;
		int num = 2;
		String filename = null;
		if (type.equals("SF")){
			filename = "C:\\Users\\hoga886\\Documents\\_Projects\\GRADIENT\\GRAD_workspace\\graphs\\SF_"+n+"v_"+path+"path.mtx";
		}else if (type.equals("ER")){
			filename = "C:\\Users\\hoga886\\Documents\\_Projects\\GRADIENT\\GRAD_workspace\\graphs\\ER_"+n+"v_"+p+"p"+path+"path.mtx";
		}
		
		ErdosRenyiGenerator<Integer, DefaultWeightedEdge> erGen = null;
		ScaleFreeGraphGenerator<Integer, DefaultWeightedEdge> sfGen = null;
		if (type.equals("ER")){
			erGen = new ErdosRenyiGenerator<Integer, DefaultWeightedEdge>(n, p);
		}else if (type.equals("SF")){
			sfGen = new ScaleFreeGraphGenerator<Integer, DefaultWeightedEdge>(n);
		}
		
		VertexFactory<Integer> vertexFactory =
				new VertexFactory<Integer>() {
			private int i;

			public Integer createVertex()
			{
				return new Integer(++i);
			}
		};
		
		G = new WeightedMultigraph<Integer, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		if (type.equals("ER")){
			erGen.generateGraph(G,vertexFactory,null);
		}else if (type.equals("SF")){
			sfGen.generateGraph(G,vertexFactory, null);
		}
		
		Set<DefaultWeightedEdge> E = G.edgeSet();
		for (DefaultWeightedEdge e: E){
			G.setEdgeWeight(e, Math.rint(1000*Math.random()));
			//System.out.println(e.toString()+" "+G.getEdgeWeight(e));
		}
		
		//TODO: put in path of length 5 from a random vertex to the 
		//largest number vertex that makes temporal sense
		
		randomPath(path,n);
		
//		Integer v1 = (int) Math.floor(Math.random()*100);
//		Integer v2 = (int) Math.floor(Math.random()*100);
//		Integer v3 = (int) Math.floor(Math.random()*100);
//		Integer v4 = (int) Math.floor(Math.random()*100);
//		Integer v5 = (int) Math.floor(Math.random()*100);
//		Integer end = n;
//		
//		DefaultWeightedEdge e1 = G.addEdge(v1, v2); G.setEdgeWeight(e1, 100);
//		DefaultWeightedEdge e2 = G.addEdge(v2, v3); G.setEdgeWeight(e2, 200);
//		DefaultWeightedEdge e3 = G.addEdge(v3, v4); G.setEdgeWeight(e3, 300);
//		DefaultWeightedEdge e4 = G.addEdge(v4, v5); G.setEdgeWeight(e4, 400);
//		DefaultWeightedEdge e5 = G.addEdge(v5, end); G.setEdgeWeight(e5, 500);
//		
//		System.out.println(v1+", "+v2+", "+v3+", "+v4+", "+v5+", "+end);
		
		
		writeToFile(filename);
	}
	
	private static void randomPath(int length, int size){
		int[] verts = new int[length];
		for (int i=0; i<length-1; i++){
			int v = (int) Math.floor(Math.random()*size);
			verts[i] = v;
		}
		verts[length-1] = size;
		
		for (int j=0; j<length-1; j++){
			Integer vi = new Integer(verts[j]);
			Integer vj = new Integer(verts[j+1]);
			DefaultWeightedEdge e = G.addEdge(vi, vj); G.setEdgeWeight(e,(j+1)*100); 
		}
		
		System.out.println(Arrays.toString(verts));
	}

	private static void writeToFile(String filename) {
		// TODO Auto-generated method stub
		WriteFile wr = new WriteFile(filename);
		writeGraphToFileMTX(wr);
		//writePartitionToFile(wr);
		wr.close();
	}
	
	private static void writeGraphToFileMTX(WriteFile wr){
		Set<DefaultWeightedEdge> E = G.edgeSet();
		wr.writeln("%%MatrixMarket matrix coordinate real general");
		wr.writeln(G.vertexSet().size()+" "+G.vertexSet().size()+" "+E.size());
		for (DefaultWeightedEdge e: E){
			wr.writeln(G.getEdgeSource(e)+" " +G.getEdgeTarget(e)+" "+G.getEdgeWeight(e));
		}
	}

}
