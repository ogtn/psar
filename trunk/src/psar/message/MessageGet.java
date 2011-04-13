package psar.message;

/**
 * Message envoyé à une <code>Inode</code> pour récupérer une donnée dans la
 * DHT.
 */
public class MessageGet extends AMessage {

	private static final long serialVersionUID = 1L;
	private long key;

	/**
	 * Crée et initialise un message de demande de donnée.
	 * 
	 * @param originalSource
	 *            L'identifiant de l'<code>INode</code> ayant créé et envoyé le
	 *            message.
	 * @param key
	 *            La clé de la donnée demandée.
	 */
	public MessageGet(long originalSource, long key) {
		super(originalSource);
		this.key = key;
	}

	/**
	 * Retourne la clé de la donnée demandée.
	 * 
	 * @return La clé de la donnée demandée.
	 */
	public long getKey() {
		return key;
	}
}
