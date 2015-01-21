package client;

import java.io.IOException;

public interface IClientCli {

	// --- Commands needed for Lab 1 ---

	/**
	 * Authenticates the client with the provided username and password.
	 * <p/>
	 * <b>Request</b>:<br/>
	 * {@code !login &lt;username&gt; &lt;password&gt;}<br/>
	 * <b>Response:</b><br/>
	 * {@code !login success}<br/>
	 * or<br/>
	 * {@code !login wrong_credentials}
	 *
	 * @param username
	 *            the name of the user
	 * @param password
	 *            the password
	 * @return status whether the authentication was successful or not
	 * @throws IOException
	 *             if an I/O error occurs
	 */
	String login(String username, String password) throws IOException;

	/**
	 * Performs a logout if necessary and closes open connections between client
	 * and cloud controller.
	 * <p/>
	 * <b>Request</b>:<br/>
	 * {@code !logout}<br/>
	 * <b>Response:</b><br/>
	 * {@code !logout &lt;message&gt;}<br/>
	 *
	 * @return message stating whether the logout was successful
	 * @throws IOException
	 *             if an I/O error occurs
	 */
	String logout() throws IOException;

	/**
	 * Retrieves the current amount of credits of the authenticated user.
	 * <p/>
	 * <b>Request</b>:<br/>
	 * {@code !credits}<br/>
	 * <b>Response:</b><br/>
	 * {@code !credits &lt;total_credits&gt;}<br/>
	 *
	 * @return the current amount of credits
	 * @throws IOException
	 *             if an I/O error occurs
	 */
	String credits() throws IOException;

	/**
	 * Buys additional credits for the authenticated user.
	 * <p/>
	 * <b>Request</b>:<br/>
	 * {@code !buy &lt;amount&gt;}<br/>
	 * <b>Response:</b><br/>
	 * {@code !credits &lt;total_credits&gt;}<br/>
	 *
	 * @param credits
	 *            the amount of credits to buy
	 * @return the total amount of credits
	 * @throws IOException
	 *             if an I/O error occurs
	 */
	String buy(long credits) throws IOException;

	/**
	 * Lists available arithmetic operations of the cloud.
	 * <p/>
	 * <b>Request</b>:<br/>
	 * {@code !list}<br/>
	 * <b>Response:</b><br/>
	 * {@code +-}<br/>
	 *
	 * @return a string containing all the operators
	 * @throws IOException
	 *             if an I/O error occurs
	 */
	String list() throws IOException;

	/**
	 * Evaluates the given mathematical term and returns the result.
	 * <p/>
	 * <b>Request</b>:<br/>
	 * {@code !compute 5 + 5}<br/>
	 * <b>Response:</b><br/>
	 * {@code 10}<br/>
	 *
	 * @param term
	 *            the expression to evaluate
	 * @return the result of the evaluation
	 * @throws IOException
	 *             if an I/O error occurs
	 */
	String compute(String term) throws IOException;

	/**
	 * Performs a shutdown of the client and release all resources.<br/>
	 * Shutting down an already terminated client has no effect.
	 * <p/>
	 * Logout the user if necessary and be sure to release all resources, stop
	 * all threads and close any open sockets.
	 * <p/>
	 * E.g.:
	 * 
	 * <pre>
	 * &gt; !exit
	 * Shutting down client now
	 * </pre>
	 *
	 * @return exit message
	 * @throws IOException
	 *             if an I/O error occurs
	 */
	String exit() throws IOException;

	// --- Commands needed for Lab 2. Please note that you do not have to
	// implement them for the first submission. ---

	/**
	 * Authenticates the client with the provided username and key.
	 * <p/>
	 * <b>Request</b>:<br/>
	 * {@code !login &lt;username&gt; &lt;client-challenge&gt;}<br/>
	 * <b>Response:</b><br/>
	 * {@code !ok &lt;client-challenge&gt; &lt;controller-challenge&gt; &lt; secret-key&gt; &lt;iv-parameter&gt;}
	 * <br/>
	 * <b>Request</b>:<br/>
	 * {@code &lt;controller-challenge&gt;}
	 *
	 * @param username
	 *            the name of the user
	 * @return status whether the authentication was successful or not
	 * @throws IOException
	 *             if an I/O error occurs
	 */
	String authenticate(String username) throws IOException;

}
