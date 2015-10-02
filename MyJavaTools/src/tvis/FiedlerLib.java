package tvis;


// Provides access to the fiedler vector functions
// Pat Mackey, 2010 - 2013

public class FiedlerLib 
{
	// Load the FiedlerLib DLL:
	static 
    {
        //loads dlls/FiedlerLib64.dll on 64-bit
	    //System.loadLibrary("dlls/FiedlerLib" + (System.getProperty("os.arch").contains("64")?"64":""));
		System.loadLibrary("FiedlerLib");
	}
	
	/**
	 * Calculates the exact fiedler vector values for the given graph.
	 * @param numNodes   Num nodes in the graph.
	 * @param numLinks   Num links in the graph.
	 * @param linksArray   List of edges, represented by source, destination nodes for each link.
	 *                     (Should be twice as long as the numLinks).
	 * @param fiedlerValues   Output array.  Should be numNodes long.  Stores fiedler values for each node.
	 */
	public native static void calcFiedlerValues(int numNodes, int numLinks, int[] linksArray,
		double [] fiedlerValues);

	/**
	 * Calculates the exact fiedler vector values for the given weighted graph.
	 * @param numNodes   Num nodes in the graph.
	 * @param numLinks   Num links in the graph.
	 * @param linksArray   List of edges, represented by source, destination nodes for each link.
	 *                     (Should be twice as long as the numLinks).
	 * @param linkWeights  List of weights for each edge (should be be numLinks long).
	 * @param fiedlerValues   Output array.  Should be numNodes long.  Stores fiedler values for each node.
	 */
	public native static void calcWeightedFiedlerValues(int numNodes, int numLinks, int[] linksArray,
		double [] linkWeights, double [] fiedlerValues);
	
	/**
	 * Calculates the approximate fiedler vector values for the given graph, using a multi-scale
	 * heuristic.  Good for large, sparse, near-planar graphs.  Highly connected graphs will
	 * likely produce inaccurate results.
	 * @param numNodes   Num nodes in the graph.
	 * @param numLinks   Num links in the graph.
	 * @param linksArray   List of edges, represented by source, destination nodes for each link.
	 *                     (Should be twice as long as the numLinks).
	 * @param fiedlerValues   Output array.  Should be numNodes long.  Stores fiedler values for each node.
	 */
	public native static void calcFastFiedlerValues(int numNodes, int numLinks, int[] linksArray,
		double [] fiedlerValues);
	
	/**
	 * Calculates laplacian matrix for the graph (non-weighted).
	 * @param numNodes   Num nodes in the graph.
	 * @param numLinks   Num links in the graph.
	 * @param linksArray   List of edges, represented by source, destination nodes for each link.
	 *                     (Should be twice as long as the numLinks).
	 * @param laplacianValues   Output array.  Should be numNodes^2 long.  Stores laplacian matrix.
	 */
	public native static void calcLaplacian(int numNodes, int numLinks, int[] linksArray,
			double [] laplacianValues);
	
	/**
	 * Calculates weighted laplacian matrix for the graph.
	 * @param numNodes   Num nodes in the graph.
	 * @param numLinks   Num links in the graph.
	 * @param linksArray   List of edges, represented by source, destination nodes for each link.
	 *                     (Should be twice as long as the numLinks).
	 * @param linkWeights  List of weights for each edge (should be be numLinks long).
	 * @param laplacianValues   Output array.  Should be numNodes^2 long.  Stores laplacian matrix.
	 */
	public native static void calcWeightedLaplacian(int numNodes, int numLinks, int[] linksArray,
			double [] linkWeights, double [] laplacianValues);
	
	/**
	 * Calculates all the eigenvectors for the laplacian matrix of the graph
	 * @param numNodes   Num nodes in the graph.
	 * @param numLinks   Num links in the graph.
	 * @param linksArray   List of edges, represented by source, destination nodes for each link.
	 *                     (Should be twice as long as the numLinks).
	 * @param eigenvals   Output array.  Should be numNodes long.  Stores eigenvalues.
	 * @param eigenvecs   Output array.  Should be numNodes^2 long.  Stores eigenvectors.
	 */
	public native static void calcAllEigens(int numNodes, int numLinks, int [] linksArray, 
			double [] eigenvals, double [] eigenvecs);
	
	/**
	 * Calculates all the eigenvectors for the weighted laplacian matrix of the graph
	 * @param numNodes   Num nodes in the graph.
	 * @param numLinks   Num links in the graph.
	 * @param linksArray   List of edges, represented by source, destination nodes for each link.
	 *                     (Should be twice as long as the numLinks).
	 * @param linkWeights  List of weights for each edge (should be be numLinks long).
	 * @param eigenvals   Output array.  Should be numNodes long.  Stores eigenvalues.
	 * @param eigenvecs   Output array.  Should be numNodes^2 long.  Stores eigenvectors.
	 */
	public native static void calcAllEigensWeighted(int numNodes, int numLinks, int [] linksArray, 
			double [] linkWeights, double [] eigenvals, double [] eigenvecs);
	
	/**
	 * Calculates all the eigenvectors for the weighted laplacian matrix of the graph
	 * @param numNodes   Num nodes in the graph.
	 * @param numLinks   Num links in the graph.
	 * @param linksArray   List of edges, represented by source, destination nodes for each link.
	 *                     (Should be twice as long as the numLinks).
	 * @param linkWeights  List of weights for each edge (should be be numLinks long).
	 * @param eigenvals   Output array.  Should be numNodes long.  Stores eigenvalues.
	 * @param eigenvecs   Output array.  Should be numNodes^2 long.  Stores eigenvectors.
	 */
	public native static void calcWeightedNormalizedEigens(int numNodes, int numLinks, int [] linksArray, 
			double [] linkWeights, double [] eigenvals, double [] eigenvecs);
	
	/**
	 * Calculates a k-means clustering (with random seeds) for the given data.
	 * NOTE: Data should be in row-major order.  Each data point should have its signature
	 * in contiguous memory.  (e.g., [1,1,1,1,1,2,2,2,2,3,3,3,3,3])
	 * @param n  Number of data points we are clustering on.
	 * @param m  Dimension of data points (e.g., size of vectors).
	 * @param k  Number of clusters desired.
	 * @param data  Matrix of data, stored as flat array in row-major order 
	 * 				(assuming rows represent each data point).
	 * @param membership  Output array.  Size n.  Stores which cluster each data point is a member of (0 - n).
	 */
	public native static void kmeans(int n, int m, int k, double [] data, int [] membership);
}
