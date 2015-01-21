package controller;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
/**
 * Spawns a new thread every time a new client connects. 
 */
public class ClientListener implements Runnable {

	private boolean running = true;
	private ServerSocket serverSocket;
	private int tcpPort;
	private CloudController cloudController;
	private ArrayList<ClientHandler> clientHandlers;
	private ExecutorService executor;

	public ClientListener(int tcpPort, CloudController cloudController){
		this.tcpPort = tcpPort;
		this.cloudController = cloudController;
		clientHandlers = new ArrayList<ClientHandler>();
		executor = Executors.newCachedThreadPool();
	}

	public void stopRunning() throws IOException{
		running = false;
		for(ClientHandler c : clientHandlers){
			c.stopRunning();
		}
		if(serverSocket != null) serverSocket.close();
		executor.shutdown();
	}
	
	@Override
	public void run(){
		try {
			serverSocket = new ServerSocket(tcpPort);
			while(running){
				Socket clientSocket = serverSocket.accept();
				ClientHandler clientHandler = new ClientHandler(clientSocket,cloudController);
				clientHandlers.add(clientHandler);
				executor.submit(clientHandler);
			}
		} catch (IOException e) {}
	}
}
