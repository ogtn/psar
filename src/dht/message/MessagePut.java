package dht.message;

import dht.ANodeId;
import dht.UInt;

/**
 * Message envoyé à une <code>Inode</code> pour stocker une donnée dans la DHT.
 */
public class MessagePut extends AMessage {

	private static final long serialVersionUID = 1L;
	private Object data;
	private UInt key;

	/**
	 * Crée et initialise un message de stockage de donnée.
	 * 
	 * @param originalSource
	 *            L'identifiant de l'<code>INode</code> ayant créé et envoyé le
	 *            message.
	 * @param data
	 *            La donnée à ajouter dans la DHT.
	 * @param key
	 *            La clé de la donnée à ajouter dans la DHT.
	 */
	public MessagePut(ANodeId originalSource, Object data, UInt key) {
		super(originalSource);
		this.data = data;
		this.key = key;
	}

	/**
	 * Retourne la donnée à ajouter dans la DHT.
	 * 
	 * @return La donnée à ajouter dans la DHT.
	 */
	public Object getData() {
		return data;
	}

	/**
	 * Retourne la clé ajoutée dans la DHT.
	 * 
	 * @return La clé ajoutée dans la DHT.
	 */
	public UInt getKey() {
		return key;
	}
}
