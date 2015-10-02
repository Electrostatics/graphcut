package test;

import java.util.Set;
import java.util.Vector;

import org.jgrapht.VertexFactory;
import org.jgrapht.generate.ScaleFreeGraphGenerator;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import mathtools.MapleTools;


import filetools.WriteFile;
import generaltools.ErdosRenyiGenerator;

public class ARC_Graphs {
	
	public static WeightedMultigraph<Integer, DefaultWeightedEdge> G;
	public static Vector<Vector<Integer>> partition1;
	public static Vector<Vector<Integer>> partition2;
	
	public static void main(String[] args){
		String type = "SF";
		int n = 1000000;
		double p = 0.5;
		int numParts1 = (int) Math.floor(Math.sqrt(n));
		int numParts2 = (int) Math.floor(Math.sqrt(numParts1));
		String filename = " ";
		if (type.equals("ER")){
			filename = "C:\\Users\\hoga886\\Documents\\_Projects\\MnMs4graphs\\graphs\\ER_"+n+"v_"+p+"p_"+numParts1+"part1_"+numParts2+"part2.txt";
		}else if (type.equals("SF")){
			filename = "C:\\Users\\hoga886\\Documents\\_Projects\\MnMs4graphs\\graphs\\SF_"+n+"v_"+numParts1+"part1_"+numParts2+"part2.txt";
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
			G.setEdgeWeight(e, Math.rint(10*Math.random()));
			//System.out.println(e.toString()+" "+G.getEdgeWeight(e));
		}
		
		partition1 = MapleTools.randomSetPart(n, numParts1);
		partition2 = MapleTools.randomSetPart(numParts1, numParts2);

		writeToFile(filename);
		
	}
	
	private static void writeToFile(String filename){
		WriteFile wr = new WriteFile(filename);
		writeGraphToFile(wr);
		writePartitionToFile(wr);
		wr.close();
	}
	
	private static void writeGraphToFile(WriteFile wr){
		Set<DefaultWeightedEdge> E = G.edgeSet();
		for (DefaultWeightedEdge e: E){
			wr.writeln((G.getEdgeSource(e)-1)+" " +(G.getEdgeTarget(e)-1)+" "+G.getEdgeWeight(e));
		}
	}

	private static void writePartitionToFile(WriteFile wr){
		wr.writeln(partition1.toString());
		wr.writeln(partition2.toString());
	}
	
}
