package dht.message;

import dht.ANodeId;

/**
 * Message envoyé à une <code>Inode</code> pour transmettre la donnée issue d'un
 * get au demandeur.
 */
public class MessageReturnGet extends AMessage {

	private static final long serialVersionUID = 1L;
	private Object data;

	/**
	 * Crée et initialise un message de stockage de donnée.
	 * 
	 * @param originalSource
	 *            L'identifiant de l'<code>INode</code> ayant créé et envoyé le
	 *            message.
	 * @param data
	 *            La donnée à retourner ou <code>null</code> si la donnée
	 *            n'existe pas.
	 */
	public MessageReturnGet(ANodeId originalSource, Object data) {
		super(originalSource);
		this.data = data;
	}

	/**
	 * Retourne la donnée issue du get.
	 * 
	 * @return La donnée issue du get ouy <code>null</code> si la donnée n'est
	 *         pas dans la dht.
	 */
	public Object getData() {
		return data;
	}
}
