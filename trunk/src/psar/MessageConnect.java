package psar;

/**
 * Message chargé d'indiquer la connection d'un <code>INode</code> à un autre.
 * 
 * Lors de la réception d'un message {@link MessageConnectTo} indiquant le
 * successeur de l'<code>INode</code> récepteur, le récepteur envoie un
 * <code>MessageConnect</code> au sucesseur lors de l'ouverture de la
 * connection.
 */
public class MessageConnect extends AMessage {

	private static final long serialVersionUID = 1L;

	/**
	 * Crée et initialise un message de connection.
	 * 
	 * @param originalSource
	 *            L'identifiant du noeud ayant créé et envoyé le message.
	 */
	public MessageConnect(long originalSource) {
		super(originalSource);
	}
}
