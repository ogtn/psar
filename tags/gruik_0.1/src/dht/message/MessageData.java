package dht.message;

import dht.UInt;

/**
 * Message servant à transmettre une donnée. Ce message est utilisé lors de la
 * suppression d'une <code>INode</code> pour la transmission des données entre
 * <code>INode</code>.
 */
public class MessageData extends AMessage {

	private static final long serialVersionUID = 1L;
	private UInt key;
	private Object data;

	/**
	 * Crée et initialise une message de transferts de données et de fin de
	 * plage.
	 * 
	 * @param originalSource
	 *            L'identifiant de l'<code>INode</code> ayant créé et envoyé le
	 *            message.
	 * @param key
	 *            La clé de la donnée transférée.
	 * @param data
	 *            La donnée transférée.
	 */
	public MessageData(UInt originalSource, UInt key, Object data) {
		super(originalSource);
		this.key = key;
		this.data = data;
	}

	/**
	 * Retourne la clé de la donnée transférée.
	 * 
	 * @return La clé de la donnée transférée.
	 */
	public UInt getKey() {
		return key;
	}

	/**
	 * Retourne la donnée transférée.
	 * 
	 * @return La donnée transférée.
	 */
	public Object getData() {
		return data;
	}
}
