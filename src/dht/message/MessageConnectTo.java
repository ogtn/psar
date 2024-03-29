package dht.message;

import dht.ANodeId;

/**
 * Message envoyé à une <code>INode</code> pour lui demander de se connecter à
 * une nouvelle <code>INode</code> suivante.
 */
public class MessageConnectTo extends AMessage {

	private static final long serialVersionUID = 1L;
	private ANodeId connectNodeId;

	/**
	 * Crée et initialise un message indiquant le sucesseur auquel se connecter.
	 * 
	 * @param originalSource
	 *            L'identifiant de l'<code>INode</code> ayant créé et envoyé le
	 *            message.
	 * @param connectNodeId
	 *            L'identifiant de l'<code>INode</code> à laquelle le noeud
	 *            récepteur du message doit se connecter.
	 */
	public MessageConnectTo(ANodeId originalSource, ANodeId connectNodeId) {
		super(originalSource);
		this.connectNodeId = connectNodeId;
	}

	/**
	 * Retourne L'identifiant de l'<code>INode</code> à laquelle l'
	 * <code>INode</code> destinataire du message doit se connecter.
	 * 
	 * @return L'identifiant de l'<code>INode</code> à laquelle l'
	 *         <code>INode</code> destinataire du message doit se connecter.
	 */
	public ANodeId getConnectNodeId() {
		return connectNodeId;
	}
}
