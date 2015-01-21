package node;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * From time to time, the node needs to send isAlive packets to the cloud controller to demonstrate it is still online and is ready to handle client requests.
 * This is done by the AlivePacketSender. 
 * The very first packet that arrives works as a registration in the cloud. See the {@link #run() run} method for more details.
 */
public class AlivePacketSender implements Runnable {

	private int tcpPort;
	private String controllerHost;
	private int controllerUdpPort;
	private int nodeAlive;
	private String nodeOperators;
	private DatagramSocket socket;
	private boolean running = true;

	public AlivePacketSender(int tcpPort, String controllerHost, int controllerUdpPort, int nodeAlive, String nodeOperators) {
		this.tcpPort = tcpPort;
		this.controllerHost = controllerHost;
		this.controllerUdpPort = controllerUdpPort;
		this.nodeAlive = nodeAlive;
		this.nodeOperators = nodeOperators;
	}
	
	/**
	 * Stops sending isALive messages to the cloud controller.
	 */
	public void stopRunning(){
		running = false;
		if(socket != null) socket.close();
	}
	
	/**
	 * Sends to the cloud controller every <nodeAlive> milliseconds isAlive messages, containing the tcp port of this node and its supported operations.
	 */
	@Override
	public void run() {
		try {
			socket = new DatagramSocket();
			while(running){
				String alivePacket = "!alive "+tcpPort+" "+nodeOperators;
				byte[] buf = alivePacket.getBytes();
				DatagramPacket request = new DatagramPacket(buf, buf.length, InetAddress.getByName(controllerHost), controllerUdpPort);
				socket.send(request);
				Thread.sleep(nodeAlive);
			}
		} 
		catch (IOException e) {} 
		catch (InterruptedException e) {}
	}
}