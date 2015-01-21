package test;

import admin.AdminConsole;
import client.Client;
import client.IClientCli;
import controller.CloudController;
import controller.IAdminConsole;
import controller.ICloudControllerCli;
import node.INodeCli;
import node.Node;
import util.Config;
import util.TestInputStream;
import util.TestOutputStream;

/**
 * Provides methods for starting an arbitrary amount of various components.
 */
public class ComponentFactory {
	/**
	 * Creates and starts a new client instance using the provided
	 * {@link Config} and I/O streams.
	 *
	 * @param componentName
	 *            the name of the component to create
	 * @return the created component after starting it successfully
	 * @throws Exception
	 *             if an exception occurs
	 */
	public IClientCli createClient(String componentName, TestInputStream in,
			TestOutputStream out) throws Exception {
		/*
		 * TODO: Here you can do anything in order to construct a node instance.
		 * Depending on your code you might want to modify the following lines
		 * but you do not have to.
		 */
		Config config = new Config("client");
		return new Client(componentName, config, in, out);
	}

	/**
	 * Creates and starts a new cloud controller instance using the provided
	 * {@link Config} and I/O streams.
	 *
	 * @param componentName
	 *            the name of the component to create
	 * @return the created component after starting it successfully
	 * @throws Exception
	 *             if an exception occurs
	 */
	public ICloudControllerCli createCloudController(String componentName,
			TestInputStream in, TestOutputStream out) throws Exception {
		/*
		 * TODO: Here you can do anything in order to construct a node instance.
		 * Depending on your code you might want to modify the following lines
		 * but you do not have to.
		 */
		Config config = new Config("controller");
		return new CloudController(componentName, config, in, out);
	}

	/**
	 * Creates and starts a new node instance using the provided {@link Config}
	 * and I/O streams.
	 *
	 * @param componentName
	 *            the name of the component to create
	 * @return the created component after starting it successfully
	 * @throws Exception
	 *             if an exception occurs
	 */
	public INodeCli createNode(String componentName, TestInputStream in,
			TestOutputStream out) throws Exception {
		/*
		 * TODO: Here you can do anything in order to construct a node instance.
		 * Depending on your code you might want to modify the following lines
		 * but you do not have to.
		 */
		Config config = new Config(componentName);
		return new Node(componentName, config, in, out);
	}

	// --- Methods needed for Lab 2. Please note that you do not have to
	// use them for the first submission. ---

	/**
	 * Creates and starts a new admin console instance using the provided
	 * {@link Config} and I/O streams.
	 *
	 * @param componentName
	 *            the name of the component to create
	 * @return the created component after starting it successfully
	 * @throws Exception
	 *             if an exception occurs
	 */
	public IAdminConsole createAdminConsole(String componentName,
			TestInputStream in, TestOutputStream out) throws Exception {
		/*
		 * TODO: Here you can do anything in order to construct a node instance.
		 * Depending on your code you might want to modify the following lines
		 * but you do not have to.
		 */
		Config config = new Config("admin");
		return new AdminConsole(componentName, config, in, out);
	}
}
