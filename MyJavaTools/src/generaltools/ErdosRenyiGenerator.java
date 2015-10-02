package generaltools;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.jgrapht.DirectedGraph;
import org.jgrapht.Graph;
import org.jgrapht.VertexFactory;
//import org.jgrapht.generate.RandomGraphGenerator.DefaultEdgeTopologyFactory;
//import org.jgrapht.generate.RandomGraphGenerator.EdgeTopologyFactory;

public class ErdosRenyiGenerator<V,E> 
	implements org.jgrapht.generate.GraphGenerator<V,E,V>{
	
	private int numVertices;
	private double edgeProb;
	
	public ErdosRenyiGenerator(int numberOfVertices, double prob){
		if ((numberOfVertices < 0) || (prob < 0) || (prob > 1)) {
            throw new IllegalArgumentException("must be non-negative, and probability must be <= 1");
        }
		
		numVertices = numberOfVertices;
		edgeProb = prob;
		
	}

	@Override
	public void generateGraph(Graph<V, E> target,
							  VertexFactory<V> vertexFactory, 
							  Map<String, V> resultMap) {
		// TODO Auto-generated method stub
		
		// key = generation order (1st,2nd,3rd,...) value=vertex Object
        // will be used later
        Map<Integer, V> orderToVertexMap =
            new HashMap<Integer, V>(this.numVertices);
		
		for (int i = 0; i < this.numVertices; i++) {
            V currVertex = vertexFactory.createVertex();
            target.addVertex(currVertex);
            orderToVertexMap.put(Integer.valueOf(i), currVertex);
        }
		
		// use specific type of edge factory, depending of the graph type
        // and edge density
        EdgeTopologyFactory<V, E> edgesFactory =
            edgeTopologyFactoryChooser(target, edgeProb);
        
        edgesFactory.createEdges(
            target,
            orderToVertexMap,
            edgeProb);
		
	}
	
	public void generateDirectedGraph(DirectedGraph<V, E> target,
			VertexFactory<V> vertexFactory, 
			Map<String, V> resultMap) {
		// TODO Auto-generated method stub

		// key = generation order (1st,2nd,3rd,...) value=vertex Object
		// will be used later
		Map<Integer, V> orderToVertexMap =
				new HashMap<Integer, V>(this.numVertices);

		for (int i = 0; i < this.numVertices; i++) {
			V currVertex = vertexFactory.createVertex();
			target.addVertex(currVertex);
			orderToVertexMap.put(Integer.valueOf(i), currVertex);
		}

		// use specific type of edge factory, depending of the graph type
		// and edge density
		EdgeTopologyFactory<V, E> edgesFactory =
				edgeTopologyFactoryChooser(target, edgeProb);

		edgesFactory.createDirectedEdges(
				target,
				orderToVertexMap,
				edgeProb);

	}

	private EdgeTopologyFactory<V, E> edgeTopologyFactoryChooser(
	        Graph<V, E> target,
	        double prob)
	    {
	        return new DefaultEdgeTopologyFactory<V, E>();
	    }
	
	//~ Inner Interfaces -------------------------------------------------------

    /**
     * This class is used to generate the edge topology for a graph.
     *
     * @author Assaf
     * @since Aug 6, 2005
     * 
     * @author hoga886
     * @since Dec 13, 2011
     */
    public interface EdgeTopologyFactory<VV, EE>
    {
        /**
         * Two different calls to the createEdges() with the same parameters
         * must result in the generation of the same. But if the randomizer is
         * different, it should, usually, create different edge topology.
         *
         * @param targetGraph - guranteed to start with zero edges.
         * @param orderToVertexMap - key=Integer of vertex order . between zero
         * to numOfVertexes (exclusive). value = vertex from the graph. unique.
         * @param numberOfEdges - to create in the graph
         * @param randomizer
         */
        public void createEdges(
            Graph<VV, EE> targetGraph,
            Map<Integer, VV> orderToVertexMap,
            double prob);
        
        public void createDirectedEdges(
                DirectedGraph<VV, EE> targetGraph,
                Map<Integer, VV> orderToVertexMap,
                double prob);

    }

  //~ Inner Classes ----------------------------------------------------------

    
    public class DefaultEdgeTopologyFactory<VV, EE>
        implements EdgeTopologyFactory<VV, EE>
    {
        
    	public void createEdges(
            Graph<VV, EE> targetGraph,
            Map<Integer, VV> orderToVertexMap,
            double prob)
        {
            for (int i=0; i<orderToVertexMap.size(); i++){
            	for (int j=i+1; j<orderToVertexMap.size(); j++){
            		double r = Math.random();
            		if (r <= prob){
            			//create the edge between i and j
            			VV x = orderToVertexMap.get(i);
            			VV y = orderToVertexMap.get(j);
            			targetGraph.addEdge(x, y);
            		}else{
            			//do nothing
            		}
            	}
            }//end double for loop
           
        }//end createEdges method
    	
    	public void createDirectedEdges(
                DirectedGraph<VV, EE> targetGraph,
                Map<Integer, VV> orderToVertexMap,
                double prob)
            {
                for (int i=0; i<orderToVertexMap.size(); i++){
                	for (int j=i+1; j<orderToVertexMap.size(); j++){
                		double r = Math.random();
                		if (r <= prob){
                			//create the edge between i and j
                			VV x = orderToVertexMap.get(i);
                			VV y = orderToVertexMap.get(j);
                			targetGraph.addEdge(x, y);
                		}else{
                			//do nothing
                		}
                	}
                }//end double for loop
               
            }//end createEdges method
    	
    }//end DefaultEdgeTopologyFactory inner class
}
