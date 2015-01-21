package admin;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Please note that this interface is not needed for Lab 1, but will
 * later be used in Lab 2. Hence, you do not have to implement it for the
 * first submission.
 *
 * 
 * Implementations of this interface get notified when a certain credit limit is
 * reached.
 */
public interface INotificationCallback extends Remote {
	/**
	 * Invoked to notify an admin that the credits of a certain user are below
	 * the given threshold.
	 *
	 * @param username
	 *            the name of the user
	 * @param credits
	 *            the threshold
	 * @throws RemoteException
	 *             if a remote error occurs
	 */
	void notify(String username, int credits) throws RemoteException;
}
