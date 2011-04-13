package dht.message;

/**
 * Message routé à travers l'ensemble des <code>INode</code> de l'anneau.
 */
public class MessagePing extends AMessage {

	private static final long serialVersionUID = 1L;

	/**
	 * Crée et initialise un message Ping.
	 * 
	 * @param originalSource
	 *            L'identifiant de l'<code>INode</code> ayant créé et envoyé le
	 *            message.
	 */
	public MessagePing(long originalSource) {
		super(originalSource);
	}
}
