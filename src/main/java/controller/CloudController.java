package controller;

import util.Config;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import model.NodeInfo;
import model.UserInfo;
import cli.Command;
import cli.Shell;

public class CloudController implements ICloudControllerCli, Runnable {

	private String componentName;
	private Config config;
	private int tcpPort;
	private int udpPort;
	private int nodeTimeout;
	private int nodeCheckPeriod;
	private ArrayList<UserInfo> users;
	private CopyOnWriteArrayList<NodeInfo> nodes;
	private Shell shell;
	private ClientListener clientListener;
	private NodeListener nodeListener;
	private NodeIsAliveChecker nodeIsAliveChecker;
	private ExecutorService executor;

	/**
	 * @param componentName
	 *            the name of the component - represented in the prompt
	 * @param config
	 *            the configuration to use
	 * @param userRequestStream
	 *            the input stream to read user input from
	 * @param userResponseStream
	 *            the output stream to write the console output to
	 */
	public CloudController(String componentName, Config config, InputStream userRequestStream, PrintStream userResponseStream) {
		this.componentName = componentName;
		this.config = config;
		this.shell = new Shell(componentName, userRequestStream, userResponseStream);
		this.users = new ArrayList<UserInfo>();
		this.nodes = new CopyOnWriteArrayList<NodeInfo>();
		this.executor = Executors.newCachedThreadPool();
	}

	/**
	 * Reads all the parameters from the cloud controller's properties file.
	 */
	private void readCloudControllerProperties(){
		tcpPort = config.getInt("tcp.port");
		udpPort = config.getInt("udp.port");
		nodeTimeout = config.getInt("node.timeout");
		nodeCheckPeriod = config.getInt("node.checkPeriod");
	}

	/**
	 * Reads for each user the username, password and credits from the user.properties file.
	 */
	private void readUserProperties(){
		ArrayList<String> usernames = new ArrayList<String>();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader("src/main/resources/user.properties"));
			String line = br.readLine();
			while(line != null){
				if(line.indexOf(".credits")!= -1){
					usernames.add(line.substring(0, line.indexOf(".credits")));
				}
				line = br.readLine();
			}
		} 
		catch (IOException e) {}
		finally {
			try {
				br.close();
			} 
			catch (IOException e) {}
		}
		Config userConfig = new Config("user");
		for(String username : usernames){
			users.add(new UserInfo(username, userConfig.getString(username+".password"),userConfig.getInt(username+".credits"), false));
		}
	}

	/**
	 * Registers to the shell the interactive commands that the cloud controller can perform and then starts the shell.
	 */
	private void startShell(){
		shell.register(this);
		executor.submit(shell);
	}

	/**
	 * Concurrently listens for new connections from the clients. See {@link ClientListener} for more details.
	 */
	private void startClientListener(){
		clientListener = new ClientListener(tcpPort,this);
		executor.submit(clientListener);
	}

	/**
	 * Waits for incoming isAlive packets from the nodes. See {@link NodeListener} for more details.
	 */
	private void startNodeListener(){
		nodeListener = new NodeListener(udpPort, this);
		executor.submit(nodeListener);
	}

	/**
	 * Checks every nodeCheckPeriod milliseconds if a node has sent an isAlive message. See {@link NodeIsAliveChecker} for more details.
	 */
	private void startNodeIsAliveChecker(){
		nodeIsAliveChecker = new NodeIsAliveChecker(nodeCheckPeriod, this);
		executor.submit(nodeIsAliveChecker);
	}

	/**
	 * Sets the status of a user to online if this user is not online
	 * @param username
	 * @param password
	 * @return the position on the list of a user, if this user wasn't online, -2 if he was already online or -1 if this user doesn't exist(username or password wrong).
	 */
	public synchronized int setUserOnline(String username, String password){
		for(UserInfo u : users){
			if(u.getUsername().equals(username)&&u.getPassword().equals(password)){
				if(!u.isOnline()){
					u.setStatus(true);
					return users.indexOf(u);
				}
				else{
					return -2;
				}
			}
		}
		return -1;
	}
	
	/**
	 * Sets the users status to offline.
	 * @param position
	 * 			the position on the list of the user
	 */
	public void setUserOffline(int position){
		users.get(position).setStatus(false);
	}
	
	/**
	 * Gives the amount of credits that a user currently has.
	 * @param position
	 * 			the position on the list of the user
	 * @return the amount of credits the user currently has
	 */
	public long getCredits(int position){
		return users.get(position).getCredits();
	}
	
	/**
	 * Increases or decreases the number of credits of a user
	 * @param position
	 * 			the position on the list of the user
	 * @param credits
	 * 			amount of credits a user wants to add or substract
	 * @return	the new amount of credits the user now has
	 */
	public long modifyCredits(int position, long credits){
		users.get(position).setCredits(users.get(position).getCredits()+credits);
		return users.get(position).getCredits();
	}
	
	/**
	 * @return the available operations
	 */
	public String getAvailableOperations(){
		String tmp = "";
		for(NodeInfo node : nodes){
			if(node.isOnline()){
				tmp = tmp + node.getOperators();
			}
		}
		if(tmp.length()==0) return "No operations available";
		String operations = "";
		if(tmp.contains("+")) operations = operations + "+";
		if(tmp.contains("-")) operations = operations + "-";
		if(tmp.contains("*")) operations = operations + "*";
		if(tmp.contains("/")) operations = operations + "/";
		return operations;
	}
	
	/**
	 * Updates the latest time a node sent an isAlive message or registers a new node to the cloud controller. 
	 * @param address
	 * 			the host address of the node.
	 * @param tcpPort
	 * 			the tcp port the node is listening for requests.
	 * @param operators
	 * 			the supported operators by this node.
	 * @param time
	 * 			the last time the node sent a message.
	 */
	public void updateNodes(InetAddress address, int tcpPort, String operators, long time){
		for(NodeInfo node : nodes){
			if(node.getAddress().getHostAddress().equals(address.getHostAddress())&&node.getTcpPort()==tcpPort){
				node.setLastSeen(time);
				node.setStatus(true);
				return;
			}
		}
		nodes.add(new NodeInfo(address, tcpPort, operators, 0, true, time));
	}
	
	/**
	 * If no isAlive packet is received within nodeTimeout milliseconds until the actual time, a node's status is set to offline.
	 */
	public void checkIfNodesAreAlive(){
		for(NodeInfo node : nodes){
			if(System.currentTimeMillis()-node.getLastSeen()>nodeTimeout){
				node.setStatus(false);
			}
		}
	}
	
	/**
	 * Finds the node with the lowest usage for a given operator.
	 * @param operator 
	 * 			the given operator
	 * @return the node with the lowest usage or null if no such node exists
	 */
	public NodeInfo getNodeWithLowestUsage(String operator){
		NodeInfo nodeWithLowestUsage = null;
		for(NodeInfo node : nodes){
			if(nodeWithLowestUsage == null && node.isOnline()){
				if(node.getOperators().contains(operator)){
					nodeWithLowestUsage = node;
				}

			}
			else if(node.isOnline() && node.getUsage()<nodeWithLowestUsage.getUsage()){
				if(node.getOperators().contains(operator)){
					nodeWithLowestUsage = node;
				}
			}
		}
		return nodeWithLowestUsage;
	}

	/**
	 * Starts the cloud controller.
	 */
	@Override
	public void run() {
		readCloudControllerProperties();
		readUserProperties();
		startShell();
		startClientListener();
		startNodeListener();
		startNodeIsAliveChecker();
	}

	@Command(value="nodes")
	@Override
	public String nodes() throws IOException {
		if(nodes.size()==0){
			return "No nodes found";
		}
		String list = "";
		for(int i = 0; i<nodes.size(); i++){
			list = list + (i+1) + ". " + nodes.get(i)+"\n";
		}
		return list.trim();
	}

	@Command(value="users")
	@Override
	public String users() throws IOException {
		if(users.size()==0){
			return "No users found";
		}
		String list = "";
		for(int i=0; i<users.size(); i++){
			list = list + (i+1) + ". " + users.get(i)+"\n";
		}
		return list.trim();
	}

	@Command(value="exit")
	@Override
	public String exit() throws IOException {
		shell.close();
		clientListener.stopRunning();
		nodeListener.stopRunning();
		nodeIsAliveChecker.stopRunning();
		executor.shutdown();
		return "Shutting down "+componentName+" now.";
	}

	/**
	 * @param args
	 *            the first argument is the name of the {@link CloudController}
	 *            component
	 */
	public static void main(String[] args) {
		CloudController cloudController = new CloudController(args[0], new Config("controller"), System.in, System.out);
		cloudController.run();
	}
	
}