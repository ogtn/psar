package psar;

/**
 * Message de demande d'insertion envoyé par un <code>INode</code> à un membre
 * quelconque de l'anneau pour routage et insertion dudit <code>INode</code>.
 */
public class MessageAskConnection extends AMessage {

	private static final long serialVersionUID = 1L;

	/**
	 * Crée et initialise un message de demande d'insertion.
	 * 
	 * @param originalSource
	 *            L'identifiant du noeud ayant créé et envoyé le message.
	 */
	public MessageAskConnection(long originalSource) {
		super(originalSource);
	}
}