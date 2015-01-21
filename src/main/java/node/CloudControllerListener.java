package node;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

/**
 * The CloudControllerListener listens for computation requests. If the calculation is successful, the resulting number is sent back to the cloud controller. 
 * Otherwise, the cloud controller is informed about the reason of the failure. Each time, a log file is created. See the {@link #run() run} method for more details.
 */
public class CloudControllerListener implements Runnable {

	private boolean running = true;
	private ServerSocket serverSocket;
	private int tcpPort;
	private Node node;
	private PrintWriter out;
	private BufferedReader in;

	public CloudControllerListener(int tcpPort, Node node){
		this.node = node;
		this.tcpPort = tcpPort;
	}
	
	/**
	 * Stops listening for computation requests.
	 */
	public void stopRunning(){
		running = false;
		try {
			if(serverSocket != null) serverSocket.close();
		} 
		catch (IOException e) {}
	}
	
	/**
	 * Listens for computation requests from the cloud controller and performs the requested operation.
	 * After it has completely processed a request and sent the response back to the cloud controller, the respective Socket is closed. 
	 * For any new request, a new Socket is created.
	 */
	@Override
	public void run() {
		try {
			serverSocket = new ServerSocket(tcpPort);
			while(running){
				Socket clientSocket = serverSocket.accept();
				out = new PrintWriter(clientSocket.getOutputStream(), true);
				in = new BufferedReader( new InputStreamReader(clientSocket.getInputStream()));
				String request = in.readLine();
				String splittedExp[] = request.split("\\s+");
				String result = "";
				if(splittedExp[1].equals("+")) result = ""+(Integer.valueOf(splittedExp[0]) + Integer.valueOf(splittedExp[2]));
				if(splittedExp[1].equals("-")) result = ""+(Integer.valueOf(splittedExp[0]) - Integer.valueOf(splittedExp[2]));
				if(splittedExp[1].equals("*")) result = ""+(Integer.valueOf(splittedExp[0]) * Integer.valueOf(splittedExp[2]));
				if(splittedExp[1].equals("/")) {
					if(Integer.valueOf(splittedExp[2]) != 0){
						double tmp = (Double.valueOf(splittedExp[0]) / Double.valueOf(splittedExp[2]));
						if(tmp<0){
							result = ""+(-Math.round(-tmp));
						}
						else{
							result = ""+Math.round(tmp);
						}
					}
					else{
						result = "Error: division by 0";
					}
				}

				node.createLogFile(splittedExp[0] + " " +splittedExp[1] + " "+splittedExp[2], result);	
				out.println(result);
				clientSocket.close();
			}
		}
		catch(SocketException e){}
		catch (IOException e) {}
	}

}
