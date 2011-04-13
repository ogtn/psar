package dht;

import dht.message.AMessage;

/**
 * Interface permettant de filtrer les messages renvoyées par la couche réseau.
 */
public interface Filter {

	/**
	 * Méthode appellée pour savoir si un message doit être remonté depuis la
	 * couche réseau vers le {@link INode}.
	 * 
	 * @param message
	 *            Le message a filtrer.
	 * @return <code>true</code> si le message peut être accepté,
	 *         <code>false</code> sinon.
	 */
	boolean accept(AMessage message);
}