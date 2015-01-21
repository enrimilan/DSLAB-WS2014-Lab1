package node;

import util.Config;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cli.Command;
import cli.Shell;

public class Node implements INodeCli, Runnable {

	private String componentName;
	private Config config;
	private String logDir;
	private int tcpPort;
	private String controllerHost;
	private int controllerUdpPort;
	private int nodeAlive;
	private String nodeOperators;
	private Shell shell;
	private AlivePacketSender alivePacketSender;
	private CloudControllerListener cloudControllerListener;
	private ExecutorService executor;
	private final static ThreadLocal<SimpleDateFormat> threadLocal = new ThreadLocal<SimpleDateFormat>() {
		protected SimpleDateFormat initialValue() {
			return new SimpleDateFormat("yyyyMMdd_HHmmss.SSS");
		};
	};

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
	public Node(String componentName, Config config, InputStream userRequestStream, PrintStream userResponseStream) {
		this.componentName = componentName;
		this.config = config;
		this.shell = new Shell(componentName, userRequestStream, userResponseStream);
		executor = Executors.newCachedThreadPool();
	}
	
	/**
	 * Reads all the parameters from the node's properties file.
	 */
	private void readNodeProperties(){
		logDir = config.getString("log.dir");
		tcpPort = config.getInt("tcp.port");
		controllerHost = config.getString("controller.host");
		controllerUdpPort = config.getInt("controller.udp.port");
		nodeAlive = config.getInt("node.alive");
		nodeOperators = config.getString("node.operators");
	}
	
	/**
	 * Registers to the shell the interactive commands that the node can perform and then starts the shell.
	 */
	private void startShell(){
		shell.register(this);
		executor.submit(shell);
	}
	
	/**
	 * Tells the cloud controller if this node is still alive. See {@link AlivePacketSender} for more details.
	 */
	private void startAlivePacketSender(){
		alivePacketSender = new AlivePacketSender(tcpPort, controllerHost, controllerUdpPort, nodeAlive, nodeOperators);
		executor.submit(alivePacketSender);
	}
	
	/**
	 * Listens for computation requests from the cloud controller. See {@link CloudControllerListener} for more details.
	 */
	private void startCloudControllerListener(){
		cloudControllerListener = new CloudControllerListener(tcpPort,this);
		executor.submit(cloudControllerListener);
	}
	/**
	 * Creates a log file containing the request and the result of an operation
	 * @param request
	 * 		The computation request.
	 * @param result
	 * 		The result of the computation request.
	 */
	public void createLogFile(String request, String result){
		BufferedWriter writer = null;
		SimpleDateFormat sdf = threadLocal.get();
		File file = new File(logDir+"/"+sdf.format(new Date())+componentName+".log");
		file.getParentFile().mkdirs();
		try {
			writer = new BufferedWriter(new PrintWriter(file));
			writer.write(request);
			writer.newLine();
			writer.write(result);
			writer.close();
		} 
		catch (FileNotFoundException e) {} 
		catch (UnsupportedEncodingException e) {} 
		catch (IOException e) {}
	}
	
	/**
	 * Starts the node.
	 */
	@Override
	public void run() {
		readNodeProperties();
		startShell();
		startAlivePacketSender();
		startCloudControllerListener();
	}

	@Command(value="exit")
	@Override
	public String exit() throws IOException {
		shell.close();
		alivePacketSender.stopRunning();
		cloudControllerListener.stopRunning();
		executor.shutdown();
		return "Shuting down "+ componentName+" now.";
	}

	/**
	 * @param args
	 *            the first argument is the name of the {@link Node} component,
	 *            which also represents the name of the configuration
	 */
	public static void main(String[] args) {
		Node node = new Node(args[0], new Config(args[0]), System.in, System.out);
		node.run();
	}

	// --- Commands needed for Lab 2. Please note that you do not have to
	// implement them for the first submission. ---

	@Override
	public String history(int numberOfRequests) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String resources() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

}
