package node;

import java.io.IOException;

public interface INodeCli {

	// --- Commands needed for Lab 1 ---

	/**
	 * Returns the given number of executed operations (requests).
	 * 
	 * @param numberOfRequests
	 * @return the evaluated expressions and their results
	 * @throws IOException
	 *             if an I/O error occurs
	 */
	String history(int numberOfRequests) throws IOException;

	/**
	 * Performs a shutdown of the node and release all resources.<br/>
	 * Shutting down an already terminated node has no effect.
	 * <p/>
	 * E.g.:
	 * 
	 * <pre>
	 * &gt; !exit
	 * Shutting down node now
	 * </pre>
	 *
	 * @return any message indicating that the node is going to terminate
	 * @throws IOException
	 *             if an I/O error occurs
	 */
	String exit() throws IOException;

	// --- Commands needed for Lab 2. Please note that you do not have to
	// implement them for the first submission. ---
	
	/**
	 * Prints out information about the current resource level of the node (the
	 * amount of resources of the last negotiation).
	 * 
	 * @return the current resource level of the node
	 * @throws IOException
	 *             if an I/O error occurs
	 */
	String resources() throws IOException;
}
