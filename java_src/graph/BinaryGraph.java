package graph;

import generaltools.ArrayTools;
import graphtools.EdmondsKarpMaxFlowMinCut;
import hashtools.TwoKeyHash;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Vector;

import mathtools.MapleTools;
import mathtools.Subsets;

import org.jgrapht.WeightedGraph;
import org.jgrapht.alg.EdmondsKarpMaximumFlow;
import org.jgrapht.alg.MinSourceSinkCut;
import org.jgrapht.alg.StoerWagnerMinimumCut;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.DirectedWeightedMultigraph;

import filetools.WriteFile;
import returntools.Tuple2;
import syst.Instance;
import syst.Systm;
import syst.Variable;

public class BinaryGraph {

	private Systm sys;
	private DirectedWeightedMultigraph<Variable, DefaultWeightedEdge> graph;

	private final Variable source = new Variable("S");
	private final Variable sink = new Variable("T");

	private final String[] biLabels;
	private boolean isAE; //is from an alpha expansion

	/* * * * * * * * * *
	 *  CONSTRUCTORS   *
	 * * * * * * * * * */

	/**
	 * @param s A binary Systm
	 */
	public BinaryGraph(Systm s, boolean ae){
		// Instantiate the Systm and make the hats
		sys = s;
		sys.makeHats();
		if (ae){
			biLabels = Systm.binaryLabels;

			if (!sys.getIsBinary()){
				System.out.println("Must have a binary Systm to make a BinaryGraph!");
				System.exit(0);
			}else{
				// Make the flow network from the normal form energy
				createNonSubmodularFlowNetworkAE();
				//printGraphCompact();
			}
		}else{
			biLabels = null;
			if (!sys.getIsBinary()){
				System.out.println("Must have a binary Systm to make a BinaryGraph!");
				System.exit(0);
			}else{
				// Make the flow network from the normal form energy
				createNonSubmodularFlowNetworkNonAE();
				printGraphCompact();
			}
		}

	}

	public BinaryGraph(Systm s, boolean ae, WriteFile wr){
		// Instantiate the Systm and make the hats
		sys = s;
		sys.makeHats();
		if (ae){
			biLabels = Systm.binaryLabels;

			if (!sys.getIsBinary()){
				System.out.println("Must have a binary Systm to make a BinaryGraph!");
				System.exit(0);
			}else{
				// Make the flow network from the normal form energy
				createNonSubmodularFlowNetworkAE();
				//printGraphCompact();
			}
		}else{
			biLabels = null;
			if (!sys.getIsBinary()){
				System.out.println("Must have a binary Systm to make a BinaryGraph!");
				System.exit(0);
			}else{
				// Make the flow network from the normal form energy
				createNonSubmodularFlowNetworkNonAE();
				printGraphCompact(wr);
			}
		}

	}

	/* * * * * * *
	 *  THE CUT  *
	 * * * * * * */

	/**
	 * Does the graph cut on the flow network to find the minimum
	 * energy configuration. If some Variables are not assigned we 
	 * do the brute force minimization on just those unassigned
	 * Variables.
	 * @return The array of {0,1} indicating the output of
	 * the cut. There may be some -1 in the array if the cut
	 * algorithm failed to label everything and what it didn't
	 * label was too large for brute force (currently >25 elements)
	 */
	public HashMap<Variable,Integer> doCut(){	
		System.out.println("Doing the cut!");

		EdmondsKarpMaxFlowMinCut<Variable, DefaultWeightedEdge> F = 
				new EdmondsKarpMaxFlowMinCut<Variable, DefaultWeightedEdge>(graph);

		Vector<Variable> vars = sys.getVars();
		Double constantNF = sys.getConstantNF();

		F.calculateMinimumCut(source, sink);
		F.calculateMinimumCutValue();

		System.out.println();

		Map <Variable,Integer> minCut = F.getMinimumCut();
		Double minCutValue = F.getMinimumCutValue();
		Vector<Variable> S = new Vector<Variable>();
		Vector<Variable> T = new Vector<Variable>();
		Vector<Variable> U = new Vector<Variable>();

		Iterator<Variable> it = vars.iterator();
		int[] varSeq = new int[sys.getVars().size()]; int ind = 0;
		HashMap<Variable,Integer> varHM = new HashMap<Variable,Integer>();
		while (it.hasNext()){
			Variable gr = it.next();
			if (minCut.get(gr) == 0 && minCut.get(gr.getHat()) == 1){
				S.add(gr);
				varSeq[ind] = 0;
				varHM.put(gr, 0);
			}else if (minCut.get(gr) == 1 && minCut.get(gr.getHat())==0){
				T.add(gr);
				varSeq[ind] = 1;
				varHM.put(gr, 1);
			}else{
				U.add(gr);
				varSeq[ind] = -1;
				varHM.put(gr, -1);
			}
			ind++;
		}

		//System.out.println("flow: "+F.getMaximumFlow().toString());
		System.out.println("flow value: "+F.getMaximumFlowValue().toString());
		System.out.println("cut: "+minCut.toString());
		System.out.println("cut value: "+minCutValue);

		//System.out.println("cut constant: "+constantNF);
		System.out.println("cut + constant: " + (minCutValue+constantNF));

		System.out.println();
		printST(S,T);
		System.out.println("UNKNOWN groups: "+Arrays.deepToString(U.toArray()));
		System.out.println("-----------------------------------------------------");

		Tuple2<Double,int[]> better = bruteLeftOver(varSeq);

		if (better != null){
			Double minValue = better.getFirst();
			int[] varSeq2 = better.getSecond();
			HashMap<Variable,Integer> varHM2 = new HashMap<Variable,Integer>();

			Vector<Variable> S2 = new Vector<Variable>();
			Vector<Variable> T2 = new Vector<Variable>();
			Vector<Variable> U2 = new Vector<Variable>();
			//TODO: Here I'm just picking the first of the minimizers
			// since they are all the same minimum value. Should probably
			// pick more carefully
			//for (int i=0; i<varSeq2.length; i++){
			for (int i=0; i<vars.size(); i++){
				Variable gr = vars.get(i);
				if (varSeq2[i] == 0){
					S2.add(gr); //System.out.println("S"+gr.toString());
					//protSeq[ind] = 0;
					varHM2.put(gr, 0);
				}else if (varSeq2[i] == 1){
					T2.add(gr); //System.out.println("T"+gr.toString());
					//protSeq[ind] = 1;
					varHM2.put(gr, 1);
				}else{
					U2.add(gr); //System.out.println("U"+gr.toString());
					//protSeq[ind] = -1;
					varHM2.put(gr, -1);
				}
				ind++;
			}

			System.out.println();
			System.out.println("After brute force on the leftover we got...");
			System.out.println("minimum energy: "+minValue);
			printST(S2,T2);
			System.out.println("UNKNOWN groups: "+Arrays.deepToString(U2.toArray()));
			System.out.println("=====================================================");
			System.out.println();

			return varHM2;
		}else{
			System.out.println("=====================================================");
			return varHM;
		}

	}

	public HashMap<Variable,Instance> doCutNonAE(){	
		System.out.println("Doing the cut!");

		EdmondsKarpMaxFlowMinCut<Variable, DefaultWeightedEdge> F = 
				new EdmondsKarpMaxFlowMinCut<Variable, DefaultWeightedEdge>(graph);

		Vector<Variable> vars = sys.getVars();
		Double constantNF = sys.getConstantNF();

		F.calculateMinimumCut(source, sink);
		F.calculateMinimumCutValue();

		System.out.println();

		Map <Variable,Integer> minCut = F.getMinimumCut();
		Double minCutValue = F.getMinimumCutValue();
		Vector<Variable> S = new Vector<Variable>();
		Vector<Variable> T = new Vector<Variable>();
		Vector<Variable> U = new Vector<Variable>();

		Iterator<Variable> it = vars.iterator();
		int[] varSeq = new int[sys.getVars().size()]; int ind = 0;
		HashMap<Variable,Instance> varHM = new HashMap<Variable,Instance>();
		while (it.hasNext()){
			Variable gr = it.next();
			if (minCut.get(gr) == 0 && minCut.get(gr.getHat()) == 1){
				S.add(gr);
				varSeq[ind] = 0;
				varHM.put(gr, gr.getInstances().get(0));
			}else if (minCut.get(gr) == 1 && minCut.get(gr.getHat())==0){
				T.add(gr);
				varSeq[ind] = 1;
				varHM.put(gr, gr.getInstances().get(1));
			}else{
				U.add(gr);
				varSeq[ind] = -1;
				varHM.put(gr, null);
			}
			ind++;
		}

		System.out.println("flow: "+F.getMaximumFlow().toString());
		System.out.println("flow value: "+F.getMaximumFlowValue().toString());
		System.out.println("cut: "+minCut.toString());
		System.out.println("cut value: "+minCutValue);

		//System.out.println("cut constant: "+constantNF);
		System.out.println("cut + constant: " + (minCutValue+constantNF));

		System.out.println();
		printST(S,T);
		System.out.println("UNKNOWN groups: "+Arrays.deepToString(U.toArray()));
		System.out.println("-----------------------------------------------------");

		Tuple2<Double,int[]> better = bruteLeftOver(varSeq);

		if (better != null){
			Double minValue = better.getFirst();
			int[] varSeq2 = better.getSecond();
			HashMap<Variable,Instance> varHM2 = new HashMap<Variable,Instance>();

			Vector<Variable> S2 = new Vector<Variable>();
			Vector<Variable> T2 = new Vector<Variable>();
			Vector<Variable> U2 = new Vector<Variable>();
			//TODO: Here I'm just picking the first of the minimizers
			// since they are all the same minimum value. Should probably
			// pick more carefully
			//for (int i=0; i<varSeq2.length; i++){
			for (int i=0; i<vars.size(); i++){
				Variable gr = vars.get(i);
				if (varSeq2[i] == 0){
					S2.add(gr); //System.out.println("S"+gr.toString());
					//protSeq[ind] = 0;
					varHM2.put(gr, gr.getInstances().get(0));
				}else if (varSeq2[i] == 1){
					T2.add(gr); //System.out.println("T"+gr.toString());
					//protSeq[ind] = 1;
					varHM2.put(gr, gr.getInstances().get(1));
				}else{
					U2.add(gr); //System.out.println("U"+gr.toString());
					//protSeq[ind] = -1;
					varHM2.put(gr, null);
				}
				ind++;
			}

			System.out.println();
			System.out.println("After brute force on the leftover we got...");
			System.out.println("minimum energy: "+minValue);
			printST(S2,T2);
			System.out.println("UNKNOWN groups: "+Arrays.deepToString(U2.toArray()));
			System.out.println("=====================================================");
			System.out.println();

			return varHM2;
		}else{
			System.out.println("=====================================================");
			return varHM;
		}

	}

	public HashMap<Variable,Instance> doCutNonAE(WriteFile wr){	
		System.out.println("Doing the cut!");

		EdmondsKarpMaxFlowMinCut<Variable, DefaultWeightedEdge> F = 
				new EdmondsKarpMaxFlowMinCut<Variable, DefaultWeightedEdge>(graph);

		Vector<Variable> vars = sys.getVars();
		Double constantNF = sys.getConstantNF();

		F.calculateMinimumCut(source, sink);
		F.calculateMinimumCutValue();

		System.out.println();

		Map <Variable,Integer> minCut = F.getMinimumCut();
		Double minCutValue = F.getMinimumCutValue();
		Vector<Variable> S = new Vector<Variable>();
		Vector<Variable> T = new Vector<Variable>();
		Vector<Variable> U = new Vector<Variable>();

		Iterator<Variable> it = vars.iterator();
		int[] varSeq = new int[sys.getVars().size()]; int ind = 0;
		HashMap<Variable,Instance> varHM = new HashMap<Variable,Instance>();
		while (it.hasNext()){
			Variable gr = it.next();
			if (minCut.get(gr) == 0 && minCut.get(gr.getHat()) == 1){
				S.add(gr);
				varSeq[ind] = 0;
				varHM.put(gr, gr.getInstances().get(0));
			}else if (minCut.get(gr) == 1 && minCut.get(gr.getHat())==0){
				T.add(gr);
				varSeq[ind] = 1;
				varHM.put(gr, gr.getInstances().get(1));
			}else{
				U.add(gr);
				varSeq[ind] = -1;
				varHM.put(gr, null);
			}
			ind++;
		}

		wr.writeln("RESULTS");
		wr.writeln("flow: "+F.getMaximumFlow().toString());
		wr.writeln("flow value: "+F.getMaximumFlowValue().toString());
		wr.writeln("cut: "+minCut.toString());
		wr.writeln("cut value: "+minCutValue);

		//System.out.println("cut constant: "+constantNF);
		wr.writeln("cut + constant: " + (minCutValue+constantNF));

		wr.writeln();
		printST(S,T,wr);
		wr.writeln("UNKNOWN groups: "+Arrays.deepToString(U.toArray()));
		wr.writeln("-----------------------------------------------------");

		Tuple2<Double,int[]> better = bruteLeftOver(varSeq, wr);

		if (better != null){
			Double minValue = better.getFirst();
			int[] varSeq2 = better.getSecond();
			HashMap<Variable,Instance> varHM2 = new HashMap<Variable,Instance>();

			Vector<Variable> S2 = new Vector<Variable>();
			Vector<Variable> T2 = new Vector<Variable>();
			Vector<Variable> U2 = new Vector<Variable>();
			//TODO: Here I'm just picking the first of the minimizers
			// since they are all the same minimum value. Should probably
			// pick more carefully
			//for (int i=0; i<varSeq2.length; i++){
			for (int i=0; i<vars.size(); i++){
				Variable gr = vars.get(i);
				if (varSeq2[i] == 0){
					S2.add(gr); //System.out.println("S"+gr.toString());
					//protSeq[ind] = 0;
					varHM2.put(gr, gr.getInstances().get(0));
				}else if (varSeq2[i] == 1){
					T2.add(gr); //System.out.println("T"+gr.toString());
					//protSeq[ind] = 1;
					varHM2.put(gr, gr.getInstances().get(1));
				}else{
					U2.add(gr); //System.out.println("U"+gr.toString());
					//protSeq[ind] = -1;
					varHM2.put(gr, null);
				}
				ind++;
			}

			wr.writeln();
			wr.writeln("After brute force on the leftover we got...");
			wr.writeln("minimum energy: "+minValue);
			printST(S2,T2,wr);
			wr.writeln("UNKNOWN groups: "+Arrays.deepToString(U2.toArray()));
			wr.writeln("=====================================================");
			wr.writeln();

			return varHM2;
		}else{
			wr.writeln("=====================================================");
			return varHM;
		}

	}
	
	// THE ONE THAT YOU WANT
	public HashMap<Variable,Instance> doCutNonAE(WriteFile main, WriteFile sec){
		System.out.println("Doing the cut!");

		MinSourceSinkCut<Variable, DefaultWeightedEdge> MSSC = 
				new MinSourceSinkCut<Variable, DefaultWeightedEdge>(graph);
		
		EdmondsKarpMaximumFlow<Variable, DefaultWeightedEdge> F = 
				new EdmondsKarpMaximumFlow<Variable, DefaultWeightedEdge>(graph);
		
		Vector<Variable> vars = sys.getVars();
		Double constantNF = sys.getConstantNF();

//		F.calculateMinimumCut(source, sink);
//		F.calculateMinimumCutValue();
		F.calculateMaximumFlow(source, sink);
		MSSC.computeMinCut(source, sink);
		
		System.out.println();

//		Map <Variable,Integer> minCut = F.getMinimumCut();
		Set<Variable> SRC = MSSC.getSourcePartition();
		Set<Variable> SNK = MSSC.getSinkPartition();
		
//		Double minCutValue = F.getMinimumCutValue();
		Double minCutValue = MSSC.getCutWeight();

		if (minCutValue - F.getMaximumFlowValue() > 0.00001){
			throw new IllegalArgumentException(
					"maxflow != mincut");
		}
		
		Vector<Variable> S = new Vector<Variable>();
		Vector<Variable> T = new Vector<Variable>();
		Vector<Variable> U = new Vector<Variable>();

		Iterator<Variable> it = vars.iterator();
		int[] varSeq = new int[sys.getVars().size()]; int ind = 0;
		HashMap<Variable,Instance> varHM = new HashMap<Variable,Instance>();
		while (it.hasNext()){
			Variable gr = it.next();
//			if (minCut.get(gr) == 0 && minCut.get(gr.getHat()) == 1){
			if (SRC.contains(gr) && SNK.contains(gr.getHat())){
				S.add(gr);
				varSeq[ind] = 0;
				varHM.put(gr, gr.getInstances().get(0));
//			}else if (minCut.get(gr) == 1 && minCut.get(gr.getHat())==0){
			}else if (SNK.contains(gr) && SRC.contains(gr.getHat())){
				T.add(gr);
				varSeq[ind] = 1;
				varHM.put(gr, gr.getInstances().get(1));
			}else{
				U.add(gr);
				varSeq[ind] = -1;
				varHM.put(gr, null);
			}
			ind++;
		}

		main.writeln("RESULTS");
//		main.writeln("flow: "+F.getMaximumFlow().toString());
		main.writeln("flow: "+F.getMaximumFlow().toString());
		main.writeln("flow value: "+F.getMaximumFlowValue().toString());
//		main.writeln("cut: "+minCut.toString());
		main.writeln("cut:");
		main.writeln("  SRC = "+SRC.toString());
		main.writeln("  SNK = "+SNK.toString());
		main.writeln("cut value: "+minCutValue);

		//System.out.println("cut constant: "+constantNF);
		main.writeln("cut + constant: " + (minCutValue+constantNF));

		main.writeln();
		printST(S,T,main);
		main.writeln("UNKNOWN groups: "+Arrays.deepToString(U.toArray()));
		main.writeln("-----------------------------------------------------");

		Tuple2<Double,int[]> better = bruteLeftOver(varSeq, main, sec);

		if (better != null){
			Double minValue = better.getFirst();
			int[] varSeq2 = better.getSecond();
			HashMap<Variable,Instance> varHM2 = new HashMap<Variable,Instance>();

			Vector<Variable> S2 = new Vector<Variable>();
			Vector<Variable> T2 = new Vector<Variable>();
			Vector<Variable> U2 = new Vector<Variable>();
			//TODO: Here I'm just picking the first of the minimizers
			// since they are all the same minimum value. Should probably
			// pick more carefully
			//for (int i=0; i<varSeq2.length; i++){
			for (int i=0; i<vars.size(); i++){
				Variable gr = vars.get(i);
				if (varSeq2[i] == 0){
					S2.add(gr); //System.out.println("S"+gr.toString());
					//protSeq[ind] = 0;
					varHM2.put(gr, gr.getInstances().get(0));
				}else if (varSeq2[i] == 1){
					T2.add(gr); //System.out.println("T"+gr.toString());
					//protSeq[ind] = 1;
					varHM2.put(gr, gr.getInstances().get(1));
				}else{
					U2.add(gr); //System.out.println("U"+gr.toString());
					//protSeq[ind] = -1;
					varHM2.put(gr, null);
				}
				ind++;
			}

			main.writeln();
			main.writeln("After brute force on the leftover we got...");
			main.writeln("minimum energy: "+minValue);
			printST(S2,T2,main);
			main.writeln("UNKNOWN groups: "+Arrays.deepToString(U2.toArray()));
			main.writeln("=====================================================");
			main.writeln();

			return varHM2;
		}else{
			main.writeln("=====================================================");
			return varHM;
		}

	}

	/**
	 * @param varSeq Array of {-1,0,1} indicating the output of the cut algorithm
	 * @return The result of doing a brute force minimization on the 
	 * unassigned Variables (indicated by "-1" in varSeq). Outputs the new
	 * minimum energy as well as the configuration that realizes this minimum.
	 */
	private Tuple2<Double, int[]> bruteLeftOver(int[] varSeq) {

		int[] unassigned = MapleTools.select(-1, varSeq);
		int n = unassigned.length;
		System.out.println("There are "+n+" unassigned residues.");

		if (n==0){
			//System.out.println("There are no unassigned residues.");
			//			long endBrute = System.currentTimeMillis();
			//			System.out.println("Took "+(endBrute - startBrute)+" ms to do the brute force minimization on the unassigned residues.");
			//			//time.write("brute "+(endBrute-startBrute)+" ");
			//			time.write("NO ");
			return null;
		}
		if (n>25){
			System.out.println("Can't do brute that high! There are "+n+" unassigned residues.");
			//			long endBrute = System.currentTimeMillis();
			//			System.out.println("Took "+(endBrute - startBrute)+" ms to do the brute force minimization on the unassigned residues.");
			//			//time.write("brute "+(endBrute-startBrute)+" ");
			//			time.write("XX ");
			//			wr.write(n+" ");
			return null;
		}

		Subsets S = new Subsets(n);

		double currentMin = Double.POSITIVE_INFINITY;
		int[] currentMinimizer = new int[n];
		while (S.hasNext()){
			int[] subset = S.next();
			int[] fullSub = varSeq.clone();
			for (int j=0; j<n; j++){
				fullSub[unassigned[j]] = subset[j];
			}
			double energy;
			if (isAE){
				energy = sys.evaluateBinaryEnergy(fullSub,true); //System.out.println(Arrays.toString(subsetVectors[i])+" "+energy);
			}else{
				energy = sys.evaluateEnergy(fullSub, true);
			}
			//if (verb){
			//	System.out.println(energy + " " + Arrays.toString(subsetVectors[i]));
			//}

			if (energy<currentMin){
				currentMin = energy;
				currentMinimizer = fullSub.clone();
			}else if (energy == currentMin){
				currentMinimizer = ArrayTools.concat(currentMinimizer,fullSub);
			}
		}

		Tuple2<Double,int[]> Ret = new Tuple2<Double,int[]>(currentMin, currentMinimizer);
		return Ret;

	}

	private Tuple2<Double, int[]> bruteLeftOver(int[] varSeq, WriteFile wr) {

		int[] unassigned = MapleTools.select(-1, varSeq);
		int n = unassigned.length;
		wr.writeln("There are "+n+" unassigned residues.");

		if (n==0){
			//System.out.println("There are no unassigned residues.");
			//			long endBrute = System.currentTimeMillis();
			//			System.out.println("Took "+(endBrute - startBrute)+" ms to do the brute force minimization on the unassigned residues.");
			//			//time.write("brute "+(endBrute-startBrute)+" ");
			//			time.write("NO ");
			return null;
		}
		if (n>25){
			wr.writeln("Can't do brute that high! There are "+n+" unassigned residues.");
			//			long endBrute = System.currentTimeMillis();
			//			System.out.println("Took "+(endBrute - startBrute)+" ms to do the brute force minimization on the unassigned residues.");
			//			//time.write("brute "+(endBrute-startBrute)+" ");
			//			time.write("XX ");
			//			wr.write(n+" ");
			return null;
		}

		Subsets S = new Subsets(n);

		double currentMin = Double.POSITIVE_INFINITY;
		int[] currentMinimizer = new int[n];
		while (S.hasNext()){
			int[] subset = S.next();
			int[] fullSub = varSeq.clone();
			for (int j=0; j<n; j++){
				fullSub[unassigned[j]] = subset[j];
			}
			double energy;
			if (isAE){
				energy = sys.evaluateBinaryEnergy(fullSub,true); //System.out.println(Arrays.toString(subsetVectors[i])+" "+energy);
			}else{
				energy = sys.evaluateEnergy(fullSub, true);
			}
			//if (verb){
			//	System.out.println(energy + " " + Arrays.toString(subsetVectors[i]));
			//}

			if (energy<currentMin){
				currentMin = energy;
				currentMinimizer = fullSub.clone();
			}else if (energy == currentMin){
				currentMinimizer = ArrayTools.concat(currentMinimizer,fullSub);
			}
		}

		Tuple2<Double,int[]> Ret = new Tuple2<Double,int[]>(currentMin, currentMinimizer);
		return Ret;

	}
	
	private Tuple2<Double, int[]> bruteLeftOver(int[] varSeq, WriteFile wr, WriteFile sec) {

		int[] unassigned = MapleTools.select(-1, varSeq);
		int n = unassigned.length;
		wr.writeln("There are "+n+" unassigned residues.");
		sec.writeln(sys.getVars().size()+" "+n);

		if (n==0){
			//System.out.println("There are no unassigned residues.");
			//			long endBrute = System.currentTimeMillis();
			//			System.out.println("Took "+(endBrute - startBrute)+" ms to do the brute force minimization on the unassigned residues.");
			//			//time.write("brute "+(endBrute-startBrute)+" ");
			//			time.write("NO ");
			return null;
		}
		if (n>25){
			wr.writeln("Can't do brute that high! There are "+n+" unassigned residues.");
			//			long endBrute = System.currentTimeMillis();
			//			System.out.println("Took "+(endBrute - startBrute)+" ms to do the brute force minimization on the unassigned residues.");
			//			//time.write("brute "+(endBrute-startBrute)+" ");
			//			time.write("XX ");
			//			wr.write(n+" ");
			return null;
		}

		Subsets S = new Subsets(n);

		double currentMin = Double.POSITIVE_INFINITY;
		int[] currentMinimizer = new int[n];
		while (S.hasNext()){
			int[] subset = S.next();
			int[] fullSub = varSeq.clone();
			for (int j=0; j<n; j++){
				fullSub[unassigned[j]] = subset[j];
			}
			double energy;
			if (isAE){
				energy = sys.evaluateBinaryEnergy(fullSub,true); //System.out.println(Arrays.toString(subsetVectors[i])+" "+energy);
			}else{
				energy = sys.evaluateEnergy(fullSub, true);
			}
			//if (verb){
			//	System.out.println(energy + " " + Arrays.toString(subsetVectors[i]));
			//}

			if (energy<currentMin){
				currentMin = energy;
				currentMinimizer = fullSub.clone();
			}else if (energy == currentMin){
				currentMinimizer = ArrayTools.concat(currentMinimizer,fullSub);
			}
		}

		Tuple2<Double,int[]> Ret = new Tuple2<Double,int[]>(currentMin, currentMinimizer);
		return Ret;

	}

	/* * * * * * * *
	 *  MAKE GRAPH *
	 * * * * * * * */

	/**
	 *  Creates the flow network for the general binary Systm 
	 *  of this BinaryGraph object.
	 */
	private void createNonSubmodularFlowNetworkNonAE(){
		System.out.println("Making the binary energy flow network for the binary Systm.");

		// Get the variables and normal form binary energy
		Vector<Variable> vars = sys.getVars();
		TwoKeyHash<Instance,Double> matrixNF = sys.getBinaryNF();

		// This will be the flow network
		DirectedWeightedMultigraph<Variable, DefaultWeightedEdge> g = 
				new DirectedWeightedMultigraph<Variable, DefaultWeightedEdge>(DefaultWeightedEdge.class);

		int n = vars.size();
		g.addVertex(source);
		g.addVertex(sink);

		// Weight the edges as indicated in the Minimizing Non-Submodular Energy paper.
		for(int i = 0; i<=n-1; i++){
			Variable vi = vars.get(i); //System.out.println(vi);
			Variable viHat = vi.getHat();   //System.out.println(viHat);
			Vector<Instance> viInsts = vi.getInstances();
			Instance vi0 = viInsts.get(0);
			Instance vi1 = viInsts.get(1);

			g.addVertex(vi);
			g.addVertex(viHat);

			Double E0 = vi0.getEnergyNF();
			Double E1 = vi1.getEnergyNF();

			g.addEdge(vi,sink);
			g.setEdgeWeight(g.getEdge(vi, sink),0.5*E0);
			g.addEdge(source,viHat);
			g.setEdgeWeight(g.getEdge(source,viHat), 0.5*E0);

			g.addEdge(source,vi);
			g.setEdgeWeight(g.getEdge(source,vi),0.5*E1); 
			g.addEdge(viHat, sink);
			g.setEdgeWeight(g.getEdge(viHat,sink),0.5*E1); 
		}

		for(int p=0; p<=n-1; p++){
			for(int q=p+1; q<=n-1 && p!=q; q++){
				Variable vp  = vars.get(p); 
				Vector<Instance> vpInsts = vp.getInstances();
				Instance vp0 = vpInsts.get(0); Instance vp1 = vpInsts.get(1); 
				Variable vpHat = vp.getHat();

				Variable vq  = vars.get(q); 
				Vector<Instance> vqInsts = vq.getInstances();
				Instance vq0 = vqInsts.get(0); Instance vq1 = vqInsts.get(1); 
				Variable vqHat = vq.getHat();

				//System.out.println(vp.toString()+" ["+vp0.toString()+", "+vp1.toString()+"] "+vq.toString()+" ["+vq0.toString()+", "+vq1.toString()+"]");

				Double E01 = matrixNF.get(vp0, vq1);
				Double E10 = matrixNF.get(vp1, vq0);
				Double E00 = matrixNF.get(vp0, vq0);
				Double E11 = matrixNF.get(vp1, vq1);

				g.addEdge(vp,vq);
				g.addEdge(vqHat,vpHat);
				g.setEdgeWeight(g.getEdge(vp,vq), 0.5*E01);
				g.setEdgeWeight(g.getEdge(vqHat,vpHat), 0.5*E01);

				g.addEdge(vq,vp);
				g.addEdge(vpHat,vqHat);
				g.setEdgeWeight(g.getEdge(vq,vp),0.5*E10);
				g.setEdgeWeight(g.getEdge(vpHat,vqHat), 0.5*E10);

				g.addEdge(vp,vqHat);
				g.addEdge(vq,vpHat);
				g.setEdgeWeight(g.getEdge(vp,vqHat),0.5*E00);
				g.setEdgeWeight(g.getEdge(vq,vpHat), 0.5*E00);

				g.addEdge(vqHat,vp);
				g.addEdge(vpHat,vq);
				g.setEdgeWeight(g.getEdge(vqHat,vp),0.5*E11);
				g.setEdgeWeight(g.getEdge(vpHat,vq),0.5*E11);


			}
		}

		graph = addWeights(g);

	}


	/**
	 *  Creates the flow network for the binary Systm (created for 
	 *  the alpha-expansion) of this BinaryGraph object.
	 */
	private void createNonSubmodularFlowNetworkAE() {
		System.out.println("Making the binary energy flow network for the alpha-expansion.");

		// Get the variables and normal form binary energy
		Vector<Variable> vars = sys.getVars();
		TwoKeyHash<Instance,Double> matrixNF = sys.getBinaryNF();

		// This will be the flow network
		DirectedWeightedMultigraph<Variable, DefaultWeightedEdge> g = 
				new DirectedWeightedMultigraph<Variable, DefaultWeightedEdge>(DefaultWeightedEdge.class);

		int n = vars.size();
		g.addVertex(source);
		g.addVertex(sink);

		// Weight the edges as indicated in the Minimizing Non-Submodular Energy paper.
		for(int i = 0; i<=n-1; i++){
			Variable vi = vars.get(i); //System.out.println(vi);
			Variable viHat = vi.getHat();   //System.out.println(viHat);
			Instance vi0 = vi.getInstance(biLabels[0]);
			Instance vi1 = vi.getInstance(biLabels[1]);

			g.addVertex(vi);
			g.addVertex(viHat);

			Double E0 = vi0.getEnergyNF();
			Double E1 = vi1.getEnergyNF();

			g.addEdge(vi,sink);
			g.setEdgeWeight(g.getEdge(vi, sink),0.5*E0);
			g.addEdge(source,viHat);
			g.setEdgeWeight(g.getEdge(source,viHat), 0.5*E0);

			g.addEdge(source,vi);
			g.setEdgeWeight(g.getEdge(source,vi),0.5*E1); 
			g.addEdge(viHat, sink);
			g.setEdgeWeight(g.getEdge(viHat,sink),0.5*E1); 
		}

		for(int p=0; p<=n-1; p++){
			for(int q=p+1; q<=n-1 && p!=q; q++){
				Variable vp  = vars.get(p); 
				Instance vp0 = vp.getInstance(biLabels[0]); Instance vp1 = vp.getInstance(biLabels[1]); 
				Variable vpHat = vp.getHat();

				Variable vq  = vars.get(q); 
				Instance vq0 = vq.getInstance(biLabels[0]); Instance vq1 = vq.getInstance(biLabels[1]);
				Variable vqHat = vq.getHat();

				Double E01 = matrixNF.get(vp0, vq1);
				Double E10 = matrixNF.get(vp1, vq0);
				Double E00 = matrixNF.get(vp0, vq0);
				Double E11 = matrixNF.get(vp1, vq1);

				g.addEdge(vp,vq);
				g.addEdge(vqHat,vpHat);
				g.setEdgeWeight(g.getEdge(vp,vq), 0.5*E01);
				g.setEdgeWeight(g.getEdge(vqHat,vpHat), 0.5*E01);

				g.addEdge(vq,vp);
				g.addEdge(vpHat,vqHat);
				g.setEdgeWeight(g.getEdge(vq,vp),0.5*E10);
				g.setEdgeWeight(g.getEdge(vpHat,vqHat), 0.5*E10);

				g.addEdge(vp,vqHat);
				g.addEdge(vq,vpHat);
				g.setEdgeWeight(g.getEdge(vp,vqHat),0.5*E00);
				g.setEdgeWeight(g.getEdge(vq,vpHat), 0.5*E00);

				g.addEdge(vqHat,vp);
				g.addEdge(vpHat,vq);
				g.setEdgeWeight(g.getEdge(vqHat,vp),0.5*E11);
				g.setEdgeWeight(g.getEdge(vpHat,vq),0.5*E11);


			}
		}

		graph = addWeights(g);

	}

	/**
	 * @param g A flow network with possible multi-edges
	 * @return The same flow network with all multi-edges
	 * collapsed into a single edge having weight equal
	 * to the sum of the original weights.
	 */
	private DirectedWeightedMultigraph<Variable, DefaultWeightedEdge> addWeights(
			DirectedWeightedMultigraph<Variable, DefaultWeightedEdge> g) {

		DirectedWeightedMultigraph<Variable, DefaultWeightedEdge> g1 =
				new DirectedWeightedMultigraph<Variable, DefaultWeightedEdge>(DefaultWeightedEdge.class);

		Set<Variable> vertices = g.vertexSet();
		for(Variable v: vertices){
			for(Variable w: vertices){
				g1.addVertex(v);
				g1.addVertex(w);

				Set<DefaultWeightedEdge> edges = g.getAllEdges(v, w);
				Double newWt = new Double(0);
				for(DefaultWeightedEdge e: edges){
					newWt += g.getEdgeWeight(e);
				}
				if(newWt != 0){
					g1.addEdge(v,w);
					g1.setEdgeWeight(g1.getEdge(v,w), newWt);
				}
			}
		}
		return g1;
	}

	/* * * * * * * *
	 *  PRINTING   *
	 * * * * * * * */

	/**
	 * Prints out the number of vertices as well as the edges with weights
	 * Number of vertices first followed by each edge and weight
	 * on subsequent lines (one line per edge)
	 */
	public void printGraph() {
		Set<Variable> vertices = graph.vertexSet(); 
		System.out.println("There are "+vertices.size() +" vertices.");
		System.out.println("The edges with weights are:");
		for(Variable v: vertices){
			for(Variable w: vertices){
				Set<DefaultWeightedEdge> edges = graph.getAllEdges(v, w);
				for(DefaultWeightedEdge e: edges){
					double wt = graph.getEdgeWeight(e);
					System.out.println(v.toString()+", "+ w.toString() +", "+ wt);
				}
			}
		}		
	}

	/**
	 * Prints out the vertex set and edge set with weights on one line.
	 * [vertex set], {(v1,v2)=wt...}
	 * where (v1,v2) is an edge with weight wt
	 */
	public void printGraphCompact(){
		Set<Variable> vertices = graph.vertexSet(); 

		String toPrint = " ";
		toPrint = toPrint.concat(vertices.toString()+", {");
		for(Variable v: vertices){
			for(Variable w: vertices){
				Set<DefaultWeightedEdge> edges = graph.getAllEdges(v, w);
				for(DefaultWeightedEdge e: edges){
					double wt = graph.getEdgeWeight(e);
					toPrint = toPrint.concat("("+v.toString()+", "+ w.toString() +")= "+ wt+", ");
				}
			}
		}
		toPrint = toPrint.substring(0,toPrint.length()-2);
		toPrint = toPrint.concat("}");

		System.out.println(toPrint);
	}

	public void printGraphCompact(WriteFile wr){
		Set<Variable> vertices = graph.vertexSet(); 
		
		// sort the vertices alphabetically
		Comparator<Variable> comparator = new Comparator<Variable>() {
		  public int compare(Variable o1, Variable o2) {
		    return o1.getName().toString().compareTo(o2.getName().toString());
		  }
		};
		SortedSet<Variable> sorted_keys = new TreeSet<Variable>(comparator);
		sorted_keys.addAll(vertices);
		
		// run through the vertices to print
		String toPrintVerts = "Vertices:\n";
		String toPrintEdges = "Edges:\n";
		for(Variable vtx1: sorted_keys){
			String vname = vtx1.toString();
			String vnameTranslate = "";
			if (vname.equals("S"))
				vnameTranslate = "S";
			else if (vname.equals("T"))
				vnameTranslate = "T";
			else if (vname.substring(vname.length()-2).equals("_H")){
				vnameTranslate = vname.substring(0, vname.length()-2)+"_PROTONATED";
			}else{
				vnameTranslate = vname+"_DEPROTONATED";
			}
			toPrintVerts += (vnameTranslate+"\n");
			for (Variable vtx2 : sorted_keys){
				if (vtx1.equals(vtx2))
					continue;
				
				String wname = vtx2.toString();
				String wnameTranslate = "";
				if (wname.equals("S"))
					wnameTranslate = "S";
				else if (wname.equals("T"))
					wnameTranslate = "T";
				else if (wname.substring(wname.length()-2).equals("_H")){
					wnameTranslate = wname.substring(0, wname.length()-2)+"_DEPROTONATED";
				}else{
					wnameTranslate = wname+"_PROTONATED";
				}
				Set<DefaultWeightedEdge> edges = graph.getAllEdges(vtx1, vtx2);
				for(DefaultWeightedEdge e: edges){
					double wt = graph.getEdgeWeight(e);
					toPrintEdges += ("("+vnameTranslate+", "+ wnameTranslate +")= "+ Math.round(wt*10000.0)/10000.0+"\n");
				}
			}
		}

//		String toPrint = " ";
//		toPrint = toPrint.concat(vertices.toString()+", {");
//		for(Variable v: vertices){
//			for(Variable w: vertices){
//				Set<DefaultWeightedEdge> edges = graph.getAllEdges(v, w);
//				for(DefaultWeightedEdge e: edges){
//					double wt = graph.getEdgeWeight(e);
//					toPrint = toPrint.concat("("+v.toString()+", "+ w.toString() +")= "+ wt+", ");
//				}
//			}
//		}
//		toPrint = toPrint.substring(0,toPrint.length()-2);
//		toPrint = toPrint.concat("}");
		wr.writeln("Flow network:");
		wr.writeln(toPrintVerts);
		wr.writeln(toPrintEdges);
	}

	/**
	 * Prints out the configuration given by the S and T sets
	 * @param S Vector of Variables assigned to "0"
	 * @param T Vector of Variables assigned to "1"
	 */
	private void printST(Vector<Variable> S, Vector<Variable> T) {
		System.out.print("0 instances: ");
		for (int i=0; i<S.size(); i++){
			Variable iGrp = S.get(i);
			Instance iGrp0;
			if (isAE){
				iGrp0 = iGrp.getInstance(biLabels[0]);
			}else{
				iGrp0 = iGrp.getInstances().get(0);
			}
			System.out.print(iGrp0.toString()+", ");
		} System.out.println();

		System.out.print("1 instances: ");
		for (int i=0; i<T.size(); i++){
			Variable iGrp = T.get(i);
			Instance iGrp1;
			if (isAE){
				iGrp1 = iGrp.getInstance(biLabels[1]);
			}else{
				iGrp1 = iGrp.getInstances().get(1);
			}
			System.out.print(iGrp1.toString()+", ");
		} System.out.println();
	}

	private void printST(Vector<Variable> S, Vector<Variable> T, WriteFile wr) {
		wr.write("0 instances: ");
		for (int i=0; i<S.size(); i++){
			Variable iGrp = S.get(i);
			Instance iGrp0;
			if (isAE){
				iGrp0 = iGrp.getInstance(biLabels[0]);
			}else{
				iGrp0 = iGrp.getInstances().get(0);
			}
			wr.write(iGrp0.toString()+", ");
		} wr.writeln();

		wr.write("1 instances: ");
		for (int i=0; i<T.size(); i++){
			Variable iGrp = T.get(i);
			Instance iGrp1;
			if (isAE){
				iGrp1 = iGrp.getInstance(biLabels[1]);
			}else{
				iGrp1 = iGrp.getInstances().get(1);
			}
			wr.write(iGrp1.toString()+", ");
		} wr.writeln();
	}

}
