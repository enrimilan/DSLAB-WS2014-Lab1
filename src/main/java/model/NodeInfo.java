package model;

import java.net.InetAddress;

/**
 * Represents all the information of a node.
 */
public class NodeInfo {
	
	private InetAddress address;
	private int tcpPort;
	private String operators;
	private long usage;
	private boolean online;
	private long lastSeen;
	
	public NodeInfo(InetAddress address, int tcpPort, String operators, long usage, boolean online, long lastSeen) {
		this.address = address;
		this.tcpPort = tcpPort;
		this.operators = operators;
		this.usage = usage;
		this.online = online;
		this.lastSeen = lastSeen;
	}

	public InetAddress getAddress() {
		return address;
	}

	public int getTcpPort() {
		return tcpPort;
	}
	
	public String getOperators(){
		return operators;
	}
	
	public synchronized long getUsage() {
		return usage;
	}

	public synchronized void increaseUsage(long usage){
		this.usage = this.usage + usage;
	}

	public synchronized boolean isOnline() {
		return online;
	}
	
	public synchronized void setStatus(boolean status){
		online = status;
	}
	
	public synchronized long getLastSeen(){
		return lastSeen;
	}
	
	public synchronized void setLastSeen(long time){
		lastSeen = time;
	}
	
	@Override
	public synchronized String toString(){
		if(online){
			return "IP: "+getAddress().getHostAddress()+" Port: "+getTcpPort()+ " online Usage: "+usage;
		}
		return "IP: "+getAddress().getHostAddress()+" Port: "+getTcpPort()+ " offline Usage: "+usage;
	}
}
