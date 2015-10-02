package graphtools;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import org.jgrapht.Graph;
import org.jgrapht.WeightedGraph;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.jgrapht.graph.WeightedMultigraph;
import org.jgrapht.graph.WeightedPseudograph;

public class ReadGraphInteger {
	
	public static final int EDGE_TYPE = 1;
	public static final int PROBLEM_TYPE = 2;
	public static final int COMMENT_TYPE = 3;
	
	public static final String RMAT_GRAPH = "rmat";
	public static final String DIMACS_GRAPH = "dimacs";
	public static final String PAJEK_GRAPH = "pajek";

	public ReadGraphInteger(){
		
	}
	
	public static WeightedGraph<Integer, DefaultWeightedEdge> readCSVFile(String filename, boolean dir, boolean multi){
		BufferedReader br = null;
		WeightedGraph<Integer, DefaultWeightedEdge> G;
		if (dir && multi){
			G = new DefaultDirectedWeightedGraph<Integer, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		} else if (dir && !multi){
			G = new SimpleDirectedWeightedGraph<Integer, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		} else if (!dir && multi){
			G = new WeightedMultigraph<Integer, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		} else { // !dir && !multi
			G = new SimpleWeightedGraph<Integer, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		}
		
		try{
			String line;
			br = new BufferedReader(new FileReader(filename));
			int vtxNum = 1;
			HashMap<Integer, Integer> vtxDict = new HashMap<Integer, Integer>();
			while ((line = br.readLine()) != null){
				line = line.trim();
				String[] lineParts = line.split("[,+]");
				int src = Integer.parseInt(lineParts[0]);
				int dst = Integer.parseInt(lineParts[1]);
				DefaultWeightedEdge e;
				if (vtxDict.containsKey(src) && vtxDict.containsKey(dst)){
					if (!multi){
						if (G.containsEdge(vtxDict.get(src), vtxDict.get(dst))){
							e = G.getEdge(vtxDict.get(src), vtxDict.get(dst));
							G.setEdgeWeight(e, G.getEdgeWeight(e)+1);
						} else{
							e = G.addEdge(vtxDict.get(src), vtxDict.get(dst));
							G.setEdgeWeight(e, 1);
						}
					} else {
						e = G.addEdge(vtxDict.get(src), vtxDict.get(dst));
						G.setEdgeWeight(e, 1);
					}
				}else if (vtxDict.containsKey(src) && !vtxDict.containsKey(dst)){
					vtxDict.put(dst, vtxNum); vtxNum++;
					G.addVertex(vtxDict.get(dst));
					e = G.addEdge(vtxDict.get(src), vtxDict.get(dst));
					G.setEdgeWeight(e, 1);
				}else if (!vtxDict.containsKey(src) && vtxDict.containsKey(dst)){
					vtxDict.put(src, vtxNum); vtxNum++;
					G.addVertex(vtxDict.get(src));
					e = G.addEdge(vtxDict.get(src), vtxDict.get(dst));
					G.setEdgeWeight(e, 1);
				}else{
					vtxDict.put(src, vtxNum); vtxNum++;
					G.addVertex(vtxDict.get(src));
					vtxDict.put(dst, vtxNum); vtxNum++;
					G.addVertex(vtxDict.get(dst));
					e = G.addEdge(vtxDict.get(src), vtxDict.get(dst));
					G.setEdgeWeight(e, 1);
				}
			}
			br.close();
		}catch(IOException exe){
			System.err.println("IO Exception!");
			exe.printStackTrace();
			System.exit(0);
		}
		
		return G;
	}
	
	public static WeightedPseudograph<Integer, DefaultWeightedEdge> readCSVFile(String filename){
		BufferedReader br = null;
		WeightedPseudograph<Integer, DefaultWeightedEdge> G = 
				new WeightedPseudograph<Integer, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		
		try{
			String line;
			br = new BufferedReader(new FileReader(filename));
			int vtxNum = 1;
			HashMap<Integer, Integer> vtxDict = new HashMap<Integer, Integer>();
			while ((line = br.readLine()) != null){
				line = line.trim();
				String[] lineParts = line.split("[,+]");
				int src = Integer.parseInt(lineParts[0]);
				int dst = Integer.parseInt(lineParts[1]);
				DefaultWeightedEdge e;
				if (vtxDict.containsKey(src) && vtxDict.containsKey(dst)){
					if (G.containsEdge(vtxDict.get(src), vtxDict.get(dst))){
						e = G.getEdge(vtxDict.get(src), vtxDict.get(dst));
						G.setEdgeWeight(e, G.getEdgeWeight(e)+1);
					} else{
						e = G.addEdge(vtxDict.get(src), vtxDict.get(dst));
						G.setEdgeWeight(e, 1);
					}
				}else if (vtxDict.containsKey(src) && !vtxDict.containsKey(dst)){
					vtxDict.put(dst, vtxNum); vtxNum++;
					G.addVertex(vtxDict.get(dst));
					e = G.addEdge(vtxDict.get(src), vtxDict.get(dst));
					G.setEdgeWeight(e, 1);
				}else if (!vtxDict.containsKey(src) && vtxDict.containsKey(dst)){
					vtxDict.put(src, vtxNum); vtxNum++;
					G.addVertex(vtxDict.get(src));
					e = G.addEdge(vtxDict.get(src), vtxDict.get(dst));
					G.setEdgeWeight(e, 1);
				}else{
					vtxDict.put(src, vtxNum); vtxNum++;
					G.addVertex(vtxDict.get(src));
					vtxDict.put(dst, vtxNum); vtxNum++;
					G.addVertex(vtxDict.get(dst));
					e = G.addEdge(vtxDict.get(src), vtxDict.get(dst));
					G.setEdgeWeight(e, 1);
				}
			}
			br.close();
		}catch(IOException exe){
			System.err.println("IO Exception!");
			exe.printStackTrace();
			System.exit(0);
		}
		
		return G;
	}
	
	public static WeightedGraph<Integer, DefaultWeightedEdge> readPajekFile(String filename, boolean dir, boolean multi, boolean ignoreWeights){
		BufferedReader br = null;
		WeightedGraph<Integer, DefaultWeightedEdge> G;
		
		if (dir && multi){
			G = new DefaultDirectedWeightedGraph<Integer, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		} else if (dir && !multi){
			G = new SimpleDirectedWeightedGraph<Integer, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		} else if (!dir && multi){
			G = new WeightedMultigraph<Integer, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		} else { // !dir && !multi
			G = new SimpleWeightedGraph<Integer, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		}
		
		int numVertices = -1;
		
		try{
			String line;
			br = new BufferedReader(new FileReader(filename));
			boolean ignoreNext = false;
			while ((line = br.readLine()) != null){
				line = line.trim();
				String[] lineParts = line.split("[\\s+]");
				if (lineParts[0].equals("*Vertices")){
					numVertices = Integer.parseInt(lineParts[1]); 
					for (int i=1; i<numVertices+1; i++){
						G.addVertex(i);
					}
					ignoreNext = true;
				}
				else if (lineParts[0].equals("*Arcs") || lineParts[0].equals("*Edges")){
					ignoreNext = false;
				} else {
					if (ignoreNext)
						continue;
					Integer src = Integer.parseInt(lineParts[0]);
					Integer tgt = Integer.parseInt(lineParts[1]);
					if (src.equals(tgt))
						continue;
					if (G.containsEdge(src, tgt) && !multi){
						// don't add the edge
					} else{
						DefaultWeightedEdge e = G.addEdge(src, tgt);
						double wt;
						if (ignoreWeights)
							wt = 1.0;
						else
							wt = Double.parseDouble(lineParts[2]);
						G.setEdgeWeight(e, wt);
					}
				}
			}
			br.close();
		}catch(IOException exe){
			System.err.println("IO Exception!");
			exe.printStackTrace();
			System.exit(0);
		}
		
		
		return G;
	}
	
	public static WeightedPseudograph<Integer, DefaultWeightedEdge> readPajekFile(String filename){
		BufferedReader br = null;
		WeightedPseudograph<Integer, DefaultWeightedEdge> G = 
				new WeightedPseudograph<Integer, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		int numVertices = -1;
		
		try{
			String line;
			br = new BufferedReader(new FileReader(filename));
			boolean ignoreNext = false;
			while ((line = br.readLine()) != null){
				line = line.trim();
				String[] lineParts = line.split("[\\s+]");
				if (lineParts[0].equals("*Vertices")){
					numVertices = Integer.parseInt(lineParts[1]); 
					for (int i=1; i<numVertices+1; i++){
						G.addVertex(i);
					}
					ignoreNext = true;
				}
				else if (lineParts[0].equals("*Arcs")){
					ignoreNext = false;
				} else {
					if (ignoreNext)
						continue;
					DefaultWeightedEdge e = G.addEdge(Integer.parseInt(lineParts[0]), Integer.parseInt(lineParts[1]));
					G.setEdgeWeight(e, Double.parseDouble(lineParts[2]));
				}
			}
			br.close();
		}catch(IOException exe){
			System.err.println("IO Exception!");
			exe.printStackTrace();
			System.exit(0);
		}
		
		
		return G;
	}
	
	public static WeightedGraph<Integer, DefaultWeightedEdge> readGraphFile(String filename, String graphType, boolean dir, boolean ignoreWeights){
		BufferedReader br = null;
		WeightedGraph<Integer, DefaultWeightedEdge> G;
		if (dir){
			G = new SimpleDirectedWeightedGraph<Integer, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		}else{
			G = new SimpleWeightedGraph<Integer, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		}
		
		try{
			String line;
			br = new BufferedReader(new FileReader(filename));
			int numVertices = -1;
			while ((line = br.readLine()) != null){
				double[] lineInfo = {-1,-1,-1,-1};
				if (graphType.equals(DIMACS_GRAPH))
					lineInfo = parseDimacsGraphLine(line);
				else if (graphType.equals(RMAT_GRAPH))
					lineInfo = parseRmatGraphLine(line, numVertices);
								
				if (lineInfo[0] == COMMENT_TYPE){
					// do nothing
				}else if (lineInfo[0] == EDGE_TYPE){
					int src = (int) Math.floor(lineInfo[1]);
					int tgt = (int) Math.floor(lineInfo[2]);
					double wt;
					if (ignoreWeights)
						wt = 1.0;
					else
						wt = lineInfo[3];
					
					G.addVertex(src); G.addVertex(tgt);
					DefaultWeightedEdge e = G.addEdge(src, tgt);
					G.setEdgeWeight(e, wt);
					
				}else if (lineInfo[0] == PROBLEM_TYPE){
					// do nothing for now
					numVertices = (int) lineInfo[1];
				}else{
					//shouldn't happen
				}
			}
			
		}catch(IOException exe){
			exe.printStackTrace();
		}finally{
			try{
				if (br != null)
					br.close();
			}catch(IOException exe){
				exe.printStackTrace();
			}
		}
		
		return G;
	}
	
	public static WeightedPseudograph<Integer, DefaultWeightedEdge> readGraphFile(String filename, String graphType){
		BufferedReader br = null;
		WeightedPseudograph<Integer, DefaultWeightedEdge> G = 
				new WeightedPseudograph<Integer, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		
		try{
			String line;
			br = new BufferedReader(new FileReader(filename));
			int numVertices = -1;
			while ((line = br.readLine()) != null){
				double[] lineInfo = {-1,-1,-1,-1};
				if (graphType.equals(DIMACS_GRAPH))
					lineInfo = parseDimacsGraphLine(line);
				else if (graphType.equals(RMAT_GRAPH))
					lineInfo = parseRmatGraphLine(line, numVertices);
								
				if (lineInfo[0] == COMMENT_TYPE){
					// do nothing
				}else if (lineInfo[0] == EDGE_TYPE){
					int src = (int) Math.floor(lineInfo[1]);
					int tgt = (int) Math.floor(lineInfo[2]);
					double wt = lineInfo[3];
					
					G.addVertex(src); G.addVertex(tgt);
					DefaultWeightedEdge e = G.addEdge(src, tgt);
					G.setEdgeWeight(e, wt);
					
				}else if (lineInfo[0] == PROBLEM_TYPE){
					// do nothing for now
					numVertices = (int) lineInfo[1];
				}else{
					//shouldn't happen
				}
			}
			
		}catch(IOException exe){
			exe.printStackTrace();
		}finally{
			try{
				if (br != null)
					br.close();
			}catch(IOException exe){
				exe.printStackTrace();
			}
		}
		
		return G;
	}
	
	private static double[] parseRmatGraphLine(String line, int numVertices){
		line = line.trim();
		String[] lineSplit = line.split("[\\s]");
		String lineType = lineSplit[0];
		double type = -1;
		double src = -1;
		double tgt = -1;
		double wt = 0;
		
		if (lineType.equals("a")){
			// line has the form "a <src vertex> <tgt vertex> <edge weight>"
			type = EDGE_TYPE;
			src = Double.parseDouble(lineSplit[1]);
			tgt = Double.parseDouble(lineSplit[2])-numVertices;
			wt = Double.valueOf(lineSplit[3]);
		} else if (lineType.equals("c")){
			// line has the form "c <arbitrary text that is a comment>"
			type = COMMENT_TYPE;
		} else if (lineType.equals("p")){
			// line has the form "p sp <number of vertices> <number of edges>"
			type = PROBLEM_TYPE;
			src = Double.parseDouble(lineSplit[2]); // actually the number of vertices
			tgt = Double.parseDouble(lineSplit[3]); // actually the number of edges
		} else{
			// this shouldn't happen
		}
		
		double[] info = {type, src, tgt, wt};
		
		return info;
		
		
	}
	
	/**
	 * Parses a line from a DIMACS style graph file. Each line
	 * begins with a character indicating the line type. 
	 * Possible line types are "c" for comment, "p" for problem, 
	 * and "a" for arc (or edge) lines.
	 * @param line
	 * @return
	 */
	private static double[] parseDimacsGraphLine(String line){
		line = line.trim();
		String[] lineSplit = line.split("[\\s+]");
		String lineType = lineSplit[0];
		double type = -1;
		double src = -1;
		double tgt = -1;
		double wt = 0;
		if (lineType.equals("a")){
			// line has the form "a <src vertex> <tgt vertex> <edge weight>"
			type = EDGE_TYPE;
			src = Double.parseDouble(lineSplit[1]);
			tgt = Double.parseDouble(lineSplit[2]);
			wt = Double.valueOf(lineSplit[3]);
		} else if (lineType.equals("c")){
			// line has the form "c <arbitrary text that is a comment>"
			type = COMMENT_TYPE;
		} else if (lineType.equals("p")){
			// line has the form "p sp <number of vertices> <number of edges>"
			type = PROBLEM_TYPE;
			src = Double.parseDouble(lineSplit[2]); // actually the number of vertices
			tgt = Double.parseDouble(lineSplit[3]); // actually the number of edges
		} else{
			// this shouldn't happen
		}
		
		double[] info = {type, src, tgt, wt};
		
		return info;
		
	}
	
}
