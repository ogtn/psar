package dht.message;

import dht.ANodeId;

/**
 * Message envoyé à une <code>INode</code> pour lui demander de quitter
 * l'anneau.
 */
public class MessageLeave extends AMessage {

	private static final long serialVersionUID = 1L;

	/**
	 * Crée et initialise un message demande à une <code>INode</code> de quitter
	 * l'anneau.
	 * 
	 * @param originalSource
	 *            L'identifiant de l'<code>INode</code> ayant créé et envoyé le
	 *            message.
	 */
	public MessageLeave(ANodeId originalSource) {
		super(originalSource);
	}
}
