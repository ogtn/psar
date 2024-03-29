package dht.message;

import dht.ANodeId;

/**
 * Message envoyé par l'<code>INode</code> précédente avant la fermeture d'un
 * lien inter-<code>INode</code>.
 */
public class MessageDisconnectOld extends AMessage {

	private static final long serialVersionUID = 1L;

	/**
	 * Crée et initialise un message de déconnexion.
	 * 
	 * @param originalSource
	 *            L'identifiant de l'<code>INode</code> ayant créé et envoyé le
	 *            message.
	 */
	public MessageDisconnectOld(ANodeId originalSource) {
		super(originalSource);
	}
}