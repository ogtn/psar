package dht.message;

import dht.UInt;

/**
 * Message de demande d'insertion envoyé par une <code>INode</code> à un membre
 * quelconque de l'anneau pour routage et insertion de ladite <code>INode</code>
 * .
 */
public class MessageAskConnection extends AMessage {

	private static final long serialVersionUID = 1L;

	/**
	 * Crée et initialise un message de demande d'insertion.
	 * 
	 * @param originalSource
	 *            L'identifiant de l'<code>INode</code> ayant créé et envoyé le
	 *            message.
	 */
	public MessageAskConnection(UInt originalSource) {
		super(originalSource);
	}
}