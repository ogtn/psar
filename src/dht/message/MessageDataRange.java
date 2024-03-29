package dht.message;

import java.io.Serializable;

import dht.ANodeId;
import dht.UInt;

/**
 * Message servant à transmettre une donnée ainsi qu'une fin de plage. Ce
 * message est utilObjectObjectisé lors de l'insertion d'une <code>INode</code> pour la
 * transmission des données entre <code>INode</code>.
 */
public class MessageDataRange extends AMessage {

	private static final long serialVersionUID = 1L;
	private UInt key;
	private Serializable data;
	private UInt endRange;

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
	 * @param endRange
	 *            La nouvelle fin de plage de l'<code>INode</code> récepteur.
	 */
	public MessageDataRange(ANodeId originalSource, UInt key, Serializable data,
			UInt endRange) {
		super(originalSource);
		this.key = key;
		this.data = data;
		this.endRange = endRange;
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
	public Serializable getData() {
		return data;
	}

	/**
	 * Retourne la nouvelle fin de plage de l'<code>INode</code> récepteur.
	 * 
	 * @return La nouvelle fin de plage de l'<code>INode</code> récepteur.
	 */
	public UInt getEndRange() {
		return endRange;
	}
}
