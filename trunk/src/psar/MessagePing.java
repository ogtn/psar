package psar;

/**
 * Mesasge routré à travers l'ensemble des noeuds de l'anneau.
 */
public class MessagePing extends AMessage {

	private static final long serialVersionUID = 1L;

	/**
	 * Crée et initlise un message Ping.
	 * 
	 * @param originalSource
	 *            L'identifiant du noeud ayant créé et envoyé le message.
	 */
	MessagePing(long originalSource) {
		super(originalSource);
	}
}
