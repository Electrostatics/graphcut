package graphtools;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

import org.jgrapht.WeightedGraph;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.jgrapht.graph.WeightedMultigraph;
import org.jgrapht.graph.WeightedPseudograph;

public class ReadGraphString {
	
	public static final String EDGE_TYPE = "1";
	public static final String PROBLEM_TYPE = "2";
	public static final String COMMENT_TYPE = "3";
	
	public static final String RMAT_GRAPH = "rmat";
	public static final String DIMACS_GRAPH = "dimacs";
	public static final String PAJEK_GRAPH = "pajek";
	public static final String CSV_GRAPH = "csv";

	public static WeightedGraph<String, DefaultWeightedEdge> readCSVFile(String filename, boolean dir, boolean multi){
		BufferedReader br = null;
		WeightedGraph<String, DefaultWeightedEdge> G;
		if (dir && multi){
			G = new DefaultDirectedWeightedGraph<String, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		} else if (dir && !multi){
			G = new SimpleDirectedWeightedGraph<String, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		} else if (!dir && multi){
			G = new WeightedMultigraph<String, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		} else { // !dir && !multi
			G = new SimpleWeightedGraph<String, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		}
		
		try{
			String line;
			br = new BufferedReader(new FileReader(filename));
//			int vtxNum = 1;
//			HashMap<String, String> vtxDict = new HashMap<String, String>();
			HashSet<String> vertices = new HashSet<String>();
			while ((line = br.readLine()) != null){
				line = line.trim();
				String[] lineParts = line.split("[,+]");
				String src = lineParts[0];
				String dst = lineParts[1];
				double wt = Double.parseDouble(lineParts[2]);
				DefaultWeightedEdge e;
				if (vertices.contains(src) && vertices.contains(dst)){
					if (!multi){
						if (G.containsEdge(src, dst)){
							e = G.getEdge(src, dst);
							G.setEdgeWeight(e, G.getEdgeWeight(e)+wt);
						} else{
							e = G.addEdge(src, dst);
							G.setEdgeWeight(e, wt);
						}
					} else {
						e = G.addEdge(src, dst);
						G.setEdgeWeight(e, wt);
					}
				}else if (vertices.contains(src) && !vertices.contains(dst)){
					vertices.add(dst); //vtxNum++;
					G.addVertex(dst);
					e = G.addEdge(src, dst);
					G.setEdgeWeight(e, wt);
				}else if (!vertices.contains(src) && vertices.contains(dst)){
					vertices.add(src); //vtxNum++;
					G.addVertex(src);
					e = G.addEdge(src, dst);
					G.setEdgeWeight(e, wt);
				}else{
					vertices.add(src); //vtxNum++;
					G.addVertex(src);
					vertices.add(dst); //vtxNum++;
					G.addVertex(dst);
					e = G.addEdge(src, dst);
					G.setEdgeWeight(e, wt);
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

	public static WeightedGraph<String, DefaultWeightedEdge> readPajekFile(String filename, boolean dir, boolean multi, boolean ignoreWeights){
		BufferedReader br = null;
		WeightedGraph<String, DefaultWeightedEdge> G;
		
		if (dir && multi){
			G = new DefaultDirectedWeightedGraph<String, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		} else if (dir && !multi){
			G = new SimpleDirectedWeightedGraph<String, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		} else if (!dir && multi){
			G = new WeightedMultigraph<String, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		} else { // !dir && !multi
			G = new SimpleWeightedGraph<String, DefaultWeightedEdge>(DefaultWeightedEdge.class);
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
						G.addVertex(""+i);
					}
					ignoreNext = true;
				}
				else if (lineParts[0].equals("*Arcs") || lineParts[0].equals("*Edges")){
					ignoreNext = false;
				} else {
					if (ignoreNext)
						continue;
					String src = lineParts[0];
					String tgt = lineParts[1];
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
	
	public static WeightedGraph<String, DefaultWeightedEdge> readGraphFile(String filename, String graphType, boolean dir, boolean multi, boolean ignoreWeights){
		BufferedReader br = null;
		WeightedGraph<String, DefaultWeightedEdge> G = 
				new WeightedPseudograph<String, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		
		if (graphType.equals(PAJEK_GRAPH)){
			G = readPajekFile(filename, dir, multi, ignoreWeights);
		} else if (graphType.equals(CSV_GRAPH)){
			G = readCSVFile(filename, dir, multi);
		} else {

			try{
				String line;
				br = new BufferedReader(new FileReader(filename));
				int numVertices = -1;
				while ((line = br.readLine()) != null){
					String[] lineInfo = {"-1", "-1", "-1", "-1"};
					if (graphType.equals(DIMACS_GRAPH))
						lineInfo = parseDimacsGraphLine(line);
					else if (graphType.equals(RMAT_GRAPH))
						lineInfo = parseRmatGraphLine(line, numVertices);

					if (lineInfo[0] == COMMENT_TYPE){
						// do nothing
					}else if (lineInfo[0] == EDGE_TYPE){
						String src = lineInfo[1];
						String tgt = lineInfo[2];
						double wt = Double.parseDouble(lineInfo[3]);

						G.addVertex(src); G.addVertex(tgt);
						DefaultWeightedEdge e = G.addEdge(src, tgt);
						G.setEdgeWeight(e, wt);

					}else if (lineInfo[0] == PROBLEM_TYPE){
						// do nothing for now
						numVertices = Integer.parseInt(lineInfo[1]);
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
		}
		
		return G;
	}
	
	private static String[] parseRmatGraphLine(String line, int numVertices){
		line = line.trim();
		String[] lineSplit = line.split("[\\s]");
		String lineType = lineSplit[0];
		String type = "-1";
		String src = "-1";
		String tgt = "-1";
		String wt = "0";
		
		if (lineType.equals("a")){
			// line has the form "a <src vertex> <tgt vertex> <edge weight>"
			type = EDGE_TYPE;
			src = lineSplit[1];
			double tgtDouble = Double.parseDouble(lineSplit[2])-numVertices; 
			tgt = ""+tgtDouble;
			wt = lineSplit[3];
		} else if (lineType.equals("c")){
			// line has the form "c <arbitrary text that is a comment>"
			type = COMMENT_TYPE;
		} else if (lineType.equals("p")){
			// line has the form "p sp <number of vertices> <number of edges>"
			type = PROBLEM_TYPE;
			src = lineSplit[2]; // actually the number of vertices
			tgt = lineSplit[3]; // actually the number of edges
		} else{
			// this shouldn't happen
		}
		
		String[] info = {type, src, tgt, wt};
		
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
	private static String[] parseDimacsGraphLine(String line){
		line = line.trim();
		String[] lineSplit = line.split("[\\s+]");
		String lineType = lineSplit[0];
		String type = "-1";
		String src = "-1";
		String tgt = "-1";
		String wt = "0";
		if (lineType.equals("a")){
			// line has the form "a <src vertex> <tgt vertex> <edge weight>"
			type = EDGE_TYPE;
			src = lineSplit[1];
			tgt = lineSplit[2];
			wt = lineSplit[3];
		} else if (lineType.equals("c")){
			// line has the form "c <arbitrary text that is a comment>"
			type = COMMENT_TYPE;
		} else if (lineType.equals("p")){
			// line has the form "p sp <number of vertices> <number of edges>"
			type = PROBLEM_TYPE;
			src = lineSplit[2]; // actually the number of vertices
			tgt = lineSplit[3]; // actually the number of edges
		} else{
			// this shouldn't happen
		}
		
		String[] info = {type, src, tgt, wt};
		
		return info;
		
	}
	
	
}
