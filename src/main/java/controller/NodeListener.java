package controller;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

/**
 * The NodeListener listens for isAlive waits for isAlive messages from the nodes. See the {@link #run() run} method for more details.
 */
public class NodeListener implements Runnable{

	private CloudController cloudController;
	private boolean running = true;
	private DatagramSocket socket;

	public NodeListener(int udpPort, CloudController cloudController) {
		this.cloudController = cloudController;
		try {
			this.socket = new DatagramSocket(udpPort);
		} 
		catch (SocketException e) {}
	}
	
	/**
	 * Stops the NodeListener waiting for isAlive messages.
	 */
	public void stopRunning(){
		running = false;
		if(socket != null) socket.close();
	}
	
	/**
	 * Waits for isAlive messages from nodes and registers or updates these nodes to the cloud controller. See also {@link CloudController#updateNodes() updateNodes} how the check is performed.
	 */
	@Override
	public void run() {
		while(running){
			byte[] buf = new byte[18];
			DatagramPacket packet = new DatagramPacket(buf, buf.length);
			try {
				socket.receive(packet); //waits forever until it receives a packet
			}
			catch (SocketException e) {}
			catch (IOException e) {}
			InetAddress address = packet.getAddress();
            String alivePacket = new String( packet.getData()).trim();
            String[] parts = alivePacket.split("\\s+");
            String tcpPort = parts[1];
            String operators = parts[2];
            cloudController.updateNodes(address, new Integer(tcpPort), operators, System.currentTimeMillis());
		}

	}

}
