/* ==========================================
 * JGraphT : a free Java graph-theory library
 * ==========================================
 *
 * Project Info:  http://jgrapht.sourceforge.net/
 * Project Creator:  Barak Naveh (http://sourceforge.net/users/barak_naveh)
 *
 * (C) Copyright 2003-2008, by Barak Naveh and Contributors.
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation,
 * Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307, USA.
 */
/* -----------------
 * EdmondsKarpMaximumFlow.java
 * -----------------
 * (C) Copyright 2008-2008, by Ilya Razenshteyn and Contributors.
 *
 * Original Author:  Ilya Razenshteyn
 * Contributor(s):   -
 *
 * $Id: EdmondsKarpMaximumFlow.java 628 2008-08-17 12:00:59Z ilyaraz $
 *
 * Changes
 * -------
 */
package graphtools;

import java.util.*;

import org.jgrapht.*;


/**
 * A <a href = "http://en.wikipedia.org/wiki/Flow_network">flow network</a> is a
 * directed graph where each edge has a capacity and each edge receives a flow.
 * The amount of flow on an edge can not exceed the capacity of the edge (note,
 * that all capacities must be non-negative). A flow must satisfy the
 * restriction that the amount of flow into a vertex equals the amount of flow
 * out of it, except when it is a source, which "produces" flow, or sink, which
 * "consumes" flow.
 *
 * <p>This class computes maximum flow in a network using <a href =
 * "http://en.wikipedia.org/wiki/Edmonds-Karp_algorithm">Edmonds-Karp
 * algorithm</a>. Be careful: for large networks this algorithm may consume
 * significant amount of time (its upper-bound complexity is O(VE^2), where V -
 * amount of vertices, E - amount of edges in the network).
 *
 * <p>For more details see Andrew V. Goldberg's <i>Combinatorial Optimization
 * (Lecture Notes)</i>.
 */
public final class EdmondsKarpMaxFlowMinCut<V, E>
{
    //~ Static fields/initializers ---------------------------------------------

    /**
     * Default tolerance.
     */
    public static final double DEFAULT_EPSILON = 0.000000001;

    //~ Instance fields --------------------------------------------------------

    private DirectedGraph<V, E> network; // our network
    private double epsilon; // tolerance (DEFAULT_EPSILON or user-defined)
    private int currentSource; // current source vertex
    private int currentSink; // current sink vertex
    private Map<E, Double> maximumFlow; // current maximum flow
    private Double maximumFlowValue; // current maximum flow value
    private Map<V, Integer> minimumCut; // current minimum cut
    private Double minimumCutValue; //current minimum cut value
    private List<Node> sourceSet;
    private List<Node> targetSet;
    private int numNodes; // number of nodes in the network
    private Map<V, Integer> indexer; // mapping from vertices to their indexes
                                     // in the internal representation
    private List<Node> nodes; // internal representation of the network

    //~ Constructors -----------------------------------------------------------

    /**
     * Constructs <tt>MaximumFlow</tt> instance to work with <i>a copy of</i>
     * <tt>network</tt>. Current source and sink are set to <tt>null</tt>. If
     * <tt>network</tt> is weighted, then capacities are weights, otherwise all
     * capacities are equal to one. Doubles are compared using <tt>
     * DEFAULT_EPSILON</tt> tolerance.
     *
     * @param network network, where maximum flow will be calculated
     */
    public EdmondsKarpMaxFlowMinCut(DirectedGraph<V, E> network)
    {
        this(network, DEFAULT_EPSILON);
    }

    /**
     * Constructs <tt>MaximumFlow</tt> instance to work with <i>a copy of</i>
     * <tt>network</tt>. Current source and sink are set to <tt>null</tt>. If
     * <tt>network</tt> is weighted, then capacities are weights, otherwise all
     * capacities are equal to one.
     *
     * @param network network, where maximum flow will be calculated
     * @param epsilon tolerance for comparing doubles
     */
    public EdmondsKarpMaxFlowMinCut(DirectedGraph<V, E> network,
        double epsilon)
    {
        if (network == null) {
            throw new NullPointerException("network is null");
        }
        if (epsilon <= 0) {
            throw new IllegalArgumentException(
                "invalid epsilon (must be positive)");
        }
        for (E e : network.edgeSet()) {
            if (network.getEdgeWeight(e) < -epsilon) {
                throw new IllegalArgumentException(
                    "invalid capacity (must be non-negative)");
            }
        }

        this.network = network;
        this.epsilon = epsilon;

        currentSource = -1;
        currentSink = -1;
        maximumFlow = null;
        maximumFlowValue = null;
        minimumCut = null;
        minimumCutValue = null;

        buildInternalNetwork();
    }

    //~ Methods ----------------------------------------------------------------

    // converting the original network into internal more convenient format
    private void buildInternalNetwork()
    {
        numNodes = network.vertexSet().size();
        nodes = new ArrayList<Node>();
        Iterator<V> it = network.vertexSet().iterator();
        indexer = new HashMap<V, Integer>();
        for (int i = 0; i < numNodes; i++) {
            V currentNode = it.next();
            nodes.add(new Node(currentNode));
            indexer.put(currentNode, i);
        }
        for (int i = 0; i < numNodes; i++) {
            V we = nodes.get(i).prototype;
            for (E e : network.outgoingEdgesOf(we)) {
                V he = network.getEdgeTarget(e);
                int j = indexer.get(he);
                Arc e1 = new Arc(i, j, network.getEdgeWeight(e), e);
                Arc e2 = new Arc(j, i, 0.0, null);
                e1.reversed = e2;
                e2.reversed = e1;
                nodes.get(i).outgoingArcs.add(e1);
                nodes.get(j).outgoingArcs.add(e2);
            }
        }
    }

    /**
     * Sets current source to <tt>source</tt>, current sink to <tt>sink</tt>,
     * then calculates maximum flow from <tt>source</tt> to <tt>sink</tt>. Note,
     * that <tt>source</tt> and <tt>sink</tt> must be vertices of the <tt>
     * network</tt> passed to the constructor, and they must be different.
     *
     * @param source source vertex
     * @param sink sink vertex
     */
    public void calculateMaximumFlow(
        V source,
        V sink)
    {
        if (!network.containsVertex(source)) {
            throw new IllegalArgumentException(
                "invalid source (null or not from this network)");
        }
        if (!network.containsVertex(sink)) {
            throw new IllegalArgumentException(
                "invalid sink (null or not from this network)");
        }

        if (source.equals(sink)) {
            throw new IllegalArgumentException("source is equal to sink");
        }

        currentSource = indexer.get(source);
        currentSink = indexer.get(sink);

        for (int i = 0; i < numNodes; i++) {
            for (Arc currentArc : nodes.get(i).outgoingArcs) {
                currentArc.flow = 0.0;
            }
        }
        maximumFlowValue = 0.0;
        for (;;) {
            breadthFirstSearch();
            if (!nodes.get(currentSink).visited) {
                maximumFlow = new HashMap<E, Double>();
                for (int i = 0; i < numNodes; i++) {
                    for (Arc currentArc : nodes.get(i).outgoingArcs) {
                        if (currentArc.prototype != null) {
                            maximumFlow.put(
                                currentArc.prototype,
                                currentArc.flow);
                        }
                    }
                }
                return;
            }
            augmentFlow();
        }
    }
    
    private void breadthFirstSearch()
    {
        for (int i = 0; i < numNodes; i++) {
            nodes.get(i).visited = false;
        }
        Queue<Integer> queue = new LinkedList<Integer>();
        queue.offer(currentSource);
        nodes.get(currentSource).visited = true;
        nodes.get(currentSource).flowAmount = Double.POSITIVE_INFINITY;
        while (queue.size() != 0) {
            int currentNode = queue.poll();
            for (Arc currentArc : nodes.get(currentNode).outgoingArcs) {
                if ((currentArc.flow + epsilon) < currentArc.capacity) {
                    if (!nodes.get(currentArc.head).visited) {
                        nodes.get(currentArc.head).visited = true;
                        nodes.get(currentArc.head).flowAmount =
                            Math.min(
                                nodes.get(currentNode).flowAmount,
                                currentArc.capacity - currentArc.flow);
                        nodes.get(currentArc.head).lastArc = currentArc;
                        queue.add(currentArc.head);
                    }
                }
            }
        }
    }
    
    /**
     * Sets current source to <tt>source</tt>, current sink to <tt>sink</tt>,
     * then calculates minimum cut from <tt>source</tt> to <tt>sink</tt>. Note,
     * that <tt>source</tt> and <tt>sink</tt> must be vertices of the <tt>
     * network</tt> passed to the constructor, and they must be different.
     *
     * @param source source vertex
     * @param sink sink vertex
     */
    public void calculateMinimumCut(
    		V source,
    		V sink)
    {
    	calculateMaximumFlow(source,sink);
    	depthFirstSearch();
    	Iterator<Node> it = nodes.iterator();
    	minimumCut = new HashMap<V,Integer>();
    	sourceSet = new ArrayList<Node>();
    	targetSet = new ArrayList<Node>();
    	while (it.hasNext()){
    		Node currentNode = it.next();
    		if (currentNode.DFvisited){
    			minimumCut.put(currentNode.prototype, 0);
    			sourceSet.add(currentNode);
    		} else{
    			minimumCut.put(currentNode.prototype, 1);
    			targetSet.add(currentNode);
    		}
    	}
    }
    
    public void calculateMinimumCutValue()
    {
    	if (sourceSet==null || targetSet==null){
    		System.out.println("Cut hasn't been calculated yet");
    		return;
    	}
    	minimumCutValue = (double) 0;
    	int n = sourceSet.size();
    	int m = targetSet.size();
    	for(int i=0; i<n; i++){
    		for(int j=0; j<m; j++){
    			List<Arc> arcs = sourceSet.get(i).outgoingArcs;
    			for (Arc a : arcs){
    				if (nodes.get(a.head).prototype == targetSet.get(j).prototype){
    					minimumCutValue += a.flow;
    				}
    			}
    		}
    	}
    	
    }
    
    /**
     * Does a depth first search on the residual network (after the 
     * maximum flow has been calculated) in order to calculate the
     * minimum cut
     */
    private void depthFirstSearch()
    {
        for (int i = 0; i < numNodes; i++) {
            nodes.get(i).DFvisited = false;
        }
        Stack<Integer> stack = new Stack<Integer>();
        stack.push(currentSource);
        nodes.get(currentSource).DFvisited = true;
        nodes.get(currentSource).flowAmount = Double.POSITIVE_INFINITY;
        //System.out.println("Starting while loop in DFS");
        while (!stack.empty()){
        	int currentNode = stack.pop();
        	//System.out.println(nodes.get(currentNode).prototype.toString());
        	nodes.get(currentNode).DFvisited = true;
        	for (Arc currentArc: nodes.get(currentNode).outgoingArcs){
        		//System.out.println(currentArc.prototype);
        		if(currentArc.capacity-currentArc.flow != 0){
        			if (!nodes.get(currentArc.head).DFvisited) {
        				stack.push(currentArc.head);
        			}
        		}
        	}
        }
    }

    private void augmentFlow()
    {
        double deltaFlow = nodes.get(currentSink).flowAmount;
        maximumFlowValue += deltaFlow;
        int currentNode = currentSink;
        while (currentNode != currentSource) {
            nodes.get(currentNode).lastArc.flow += deltaFlow;
            nodes.get(currentNode).lastArc.reversed.flow -= deltaFlow;
            currentNode = nodes.get(currentNode).lastArc.tail;
        }
    }

    /**
     * Returns maximum flow value, that was calculated during last <tt>
     * calculateMaximumFlow</tt> call, or <tt>null</tt>, if there was no <tt>
     * calculateMaximumFlow</tt> calls.
     *
     * @return maximum flow value
     */
    public Double getMaximumFlowValue()
    {
        return maximumFlowValue;
    }

    /**
     * Returns maximum flow, that was calculated during last <tt>
     * calculateMaximumFlow</tt> call, or <tt>null</tt>, if there was no <tt>
     * calculateMaximumFlow</tt> calls.
     *
     * @return <i>read-only</i> mapping from edges to doubles - flow values
     */
    public Map<E, Double> getMaximumFlow()
    {
        if (maximumFlow == null) {
            return null;
        }
        return Collections.unmodifiableMap(maximumFlow);
    }
    
    /**
     * Returns minimum cut value, that was calculated during last <tt>
     * calculateMinimumCut</tt> call, or <tt>null</tt>, if there was no <tt>
     * calculateMinimumCut</tt> calls.
     *
     * @return minimum cut value
     */
    public Double getMinimumCutValue()
    {
    	return minimumCutValue;
    }

    /**
     * Returns minimum cut, that was calculated during last <tt>
     * calculateMinimumCut</tt> call, or <tt>null</tt>, if there was no <tt>
     * calculateMinimumCut</tt> calls.
     *
     * @return <i>read-only</i> mapping from vertices to {0,1}: Map(v)=0 if v is in source set,
     * Map(v)=1 if v is in target set
     */
    public Map<V, Integer> getMinimumCut()
    {
    	if (minimumCut == null) {
    		return null;
    	}
    	return Collections.unmodifiableMap(minimumCut);
    }
    
    /**
     * Returns current source vertex, or <tt>null</tt> if there was no <tt>
     * calculateMaximumFlow</tt> calls.
     *
     * @return current source
     */
    public V getCurrentSource()
    {
        if (currentSource == -1) {
            return null;
        }
        return nodes.get(currentSource).prototype;
    }

    /**
     * Returns current sink vertex, or <tt>null</tt> if there was no <tt>
     * calculateMaximumFlow</tt> calls.
     *
     * @return current sink
     */
    public V getCurrentSink()
    {
        if (currentSink == -1) {
            return null;
        }
        return nodes.get(currentSink).prototype;
    }

    //~ Inner Classes ----------------------------------------------------------

    // class used for internal representation of network
    class Node
    {
        V prototype; // corresponding node in the original network
        List<Arc> outgoingArcs = new ArrayList<Arc>(); // list of outgoing arcs
                                                       // in the residual
                                                       // network
        boolean visited; // this mark is used during BFS to mark visited nodes
        boolean DFvisited;
        Arc lastArc; // last arc in the shortest path
        double flowAmount; // amount of flow, we are able to push here

        Node(
            V prototype)
        {
            this.prototype = prototype;
        }
    }

    // class used for internal representation of network
    class Arc
    {
        int tail; // "from"
        int head; // "to"
        double capacity; // capacity (can be zero)
        double flow; // current flow (can be negative)
        Arc reversed; // for each arc in the original network we are to create
                      // reversed arc
        E prototype; // corresponding edge in the original network, can be null,
                     // if it is reversed arc

        Arc(
            int tail,
            int head,
            double capacity,
            E prototype)
        {
            this.tail = tail;
            this.head = head;
            this.capacity = capacity;
            this.prototype = prototype;
        }
    }
}

// End EdmondsKarpMaximumFlow.java
