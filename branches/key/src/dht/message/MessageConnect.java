package dht.message;

import dht.UInt;

/**
 * Message chargé d'indiquer la connection d'une <code>INode</code> à une autre.
 * 
 * Lors de la réception d'un message {@link MessageConnectTo} indiquant le
 * successeur de l'<code>INode</code> récepteur, le récepteur envoie un
 * <code>MessageConnect</code> au sucesseur lors de l'ouverture de la
 * connection.
 */
public class MessageConnect extends AMessage {

	private static final long serialVersionUID = 1L;

	public boolean gruick;
	
	/**
	 * Crée et initialise un message de connection.
	 * 
	 * @param originalSource
	 *            Identifiant de l'<code>INode</code> ayant créé et envoyé le
	 *            message.
	 */
	public MessageConnect(UInt originalSource) {
		super(originalSource);
		gruick = false;
	}
	
	
	public MessageConnect(UInt originalSource, boolean gruick) {
		super(originalSource);
		this.gruick = gruick;
	}
}
