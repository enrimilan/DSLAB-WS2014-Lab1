package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

import cli.Command;
import cli.Shell;
import util.Config;

public class Client implements IClientCli, Runnable {

	private String componentName;
	private Config config;
	private String controllerHost;
	private int controllerTcpPort;
	private Shell shell;
	private Socket socket;
	private PrintWriter out;
	private BufferedReader in;	

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
	public Client(String componentName, Config config, InputStream userRequestStream, PrintStream userResponseStream) {
		this.componentName = componentName;
		this.config = config;
		this.shell = new Shell(componentName, userRequestStream, userResponseStream);
	}

	/**
	 * Reads the host and tcp port of the controller from the client's properties file.
	 */
	private void readClientProperties(){
		controllerHost = config.getString("controller.host");
		controllerTcpPort = config.getInt("controller.tcp.port");
	}

	/**
	 * Creates a Socket and connects to the cloud controller, also gets the outputstream(for outgoing messages) and the intputstream(for ingoing messages).
	 * If nothing went wrong i.e the cloud controller is not offline, the shell will be started.
	 * If the cloud controller is offline, a usage message is printed and the client exits immediately.
	 */
	private void connectToCloudControllerAndStartShell(){
		try {
			socket = new Socket(controllerHost,controllerTcpPort);
			out = new PrintWriter(socket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			startShell();
		}
		catch(ConnectException e) {
			//cloud controller is not online, therefore this client will not even start.
			System.err.println("Cloud controller is offline");
		}
		catch (UnknownHostException e) {} 
		catch (IOException e) {}
	}

	/**
	 * Registers to the shell the interactive commands that the client can perform and then starts the shell.
	 */
	private void startShell(){
		shell.register(this);
		new Thread(shell).start();
	}

	/**
	 * Sends a request to the cloud controller, first it writes to the outputstream of the socket and then reads from its inputstream.
	 * @param request
	 * 		the request which has to be sent to the cloud controller
	 * @return
	 * 		the response from the cloud controller
	 * @throws IOException
	 */
	private String sendRequest(String request) throws IOException{
		out.println(request);
		String response = "";
		try{
			response = in.readLine();
			if(response == null){
				//this check was necessary when running the code on a linux machine
				close();
				return "Cloud controller suddenly went offline. Shutting down " +componentName + " now";
			}
		}
		catch (SocketException e){
			//cloud controller suddenly went offline. make sure to close all the resources in order to exit this client.
			close();
			return "Cloud controller suddenly went offline. Shutting down " +componentName + " now";
		}
		return response;
	}
	
	/**
	 * Releases all resources, stops all threads and closes the socket.
	 * @throws IOException
	 */
	private void close() throws IOException{
		shell.close();
		if(socket != null) socket.close();
		if(out != null) out.close();
		if(in != null) in.close();
	}
	
	/**
	 * Starts the client.
	 */
	@Override
	public void run() {
		readClientProperties();
		connectToCloudControllerAndStartShell();
	}

	@Command(value="login")
	@Override
	public String login(String username, String password) throws IOException {
		return sendRequest("!login "+ username + " " + password);
	}

	@Command(value="logout")
	@Override
	public String logout() throws IOException {
		return sendRequest("!logout");
	}

	@Command(value="credits")
	@Override
	public String credits() throws IOException {
		return sendRequest("!credits");
	}

	@Command(value="buy")
	@Override
	public String buy(long credits) throws IOException {
		return sendRequest("!buy "+credits);
	}

	@Command(value="list")
	@Override
	public String list() throws IOException {
		return sendRequest("!list");
	}

	@Command(value="compute")
	@Override
	public String compute(String term) throws IOException {
		return sendRequest("!compute "+term);
	}

	@Command(value="exit")
	@Override
	public String exit() throws IOException {
		logout();
		close();
		return "Shutting down "+componentName+" now.";
	}

	/**
	 * @param args
	 *            the first argument is the name of the {@link Client} component
	 */
	public static void main(String[] args) {
		Client client = new Client(args[0], new Config("client"), System.in, System.out);
		client.run();
	}

	// --- Commands needed for Lab 2. Please note that you do not have to
	// implement them for the first submission. ---

	@Override
	public String authenticate(String username) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

}
