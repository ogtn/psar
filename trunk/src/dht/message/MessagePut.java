package dht.message;

import java.io.Serializable;

import dht.ANodeId;
import dht.UInt;

/**
 * Message envoyé à une <code>Inode</code> pour stocker une donnée dans la DHT.
 */
public class MessagePut extends AMessage {

	private static final long serialVersionUID = 1L;
	private Serializable data;
	private UInt key;
	boolean force;

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
	public MessagePut(ANodeId originalSource, Serializable data, UInt key) {
		super(originalSource);
		this.data = data;
		this.key = key;
		force = false;
	}

	/**
	 * Retourne la donnée à ajouter dans la DHT.
	 * 
	 * @return La donnée à ajouter dans la DHT.
	 */
	public Serializable getData() {
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

	public void setForce(boolean force) {
		this.force = force;
	}

	public boolean isForce() {
		return force;
	}
}
