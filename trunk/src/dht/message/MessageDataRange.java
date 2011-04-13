package dht.message;

/**
 * Message servant à transmettre une donnée ainsi qu'une fin de plage. Ce
 * message est utilisé lors de l'insertion et de la suppression d'une
 * <code>INode</code> pour la transmission des données entre <code>INode</code>.
 */
public class MessageDataRange extends AMessage {

	private static final long serialVersionUID = 1L;
	private long key;
	private Object data;
	private long endRange;

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
	public MessageDataRange(long originalSource, long key, Object data,
			long endRange) {
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
	public long getKey() {
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

	/**
	 * Retourne la nouvelle fin de plage de l'<code>INode</code> récepteur.
	 * 
	 * @return La nouvelle fin de plage de l'<code>INode</code> récepteur.
	 */
	public long getEndRange() {
		return endRange;
	}
}
