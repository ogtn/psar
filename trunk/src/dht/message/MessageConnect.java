package dht.message;

import dht.ANodeId;

/**
 * Message chargé d'indiquer la connexion d'une <code>INode</code> à une autre.
 * 
 * Lors de la réception d'un message {@link MessageConnectTo} indiquant le
 * successeur de l'<code>INode</code> récepteur, le récepteur envoie un
 * <code>MessageConnect</code> au successeur lors de l'ouverture de la
 * connexion.
 */
public class MessageConnect extends AMessage {

	private static final long serialVersionUID = 1L;
	
	/**
	 * Crée et initialise un message de connexion.
	 * 
	 * @param originalSource
	 *            Identifiant de l'<code>INode</code> ayant créé et envoyé le
	 *            message.
	 */
	public MessageConnect(ANodeId originalSource) {
		super(originalSource);
	}
}
