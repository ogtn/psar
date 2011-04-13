package psar;

/**
 * Message envoyé par le noeud précédent avant la fermeture du lien
 * inter-noeuds.
 */
public class MessageDisconnect extends AMessage {

	private static final long serialVersionUID = 1L;

	/**
	 * Crée et initialise un message indiquant le sucesseur auquel se connecter.
	 * 
	 * @param originalSource
	 *            L'identifiant du noeud ayant créé et envoyé le message.
	 */
	MessageDisconnect(long originalSource) {
		super(originalSource);
	}
}