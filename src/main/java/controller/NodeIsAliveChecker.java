package controller;

/**
 * Checks whether the nodes are still alive or not. See the {@link #run() run} method for more details.
 */
public class NodeIsAliveChecker implements Runnable {

	private int nodeCheckPeriod;
	private CloudController cloudController;
	private boolean running = true;

	public NodeIsAliveChecker(int nodeCheckPeriod, CloudController cloudController) {
		this.nodeCheckPeriod = nodeCheckPeriod;
		this.cloudController = cloudController;
	}
	
	/**
	 * Stop checking if the nodes are still alive.
	 */
	public void stopRunning(){
		running = false;
	}
	
	/**
	 * Checks every nodeCheckPeriod milliseconds if the nodes are still alive. See also {@link CloudController#checkIfNodesAreAlive() checkIfNodesAreAlive} how the check is performed.
	 */
	@Override
	public void run() {
		while(running){
			cloudController.checkIfNodesAreAlive();
			try {
				Thread.sleep(nodeCheckPeriod);
			} 
			catch (InterruptedException e) {}
		}
	}
}