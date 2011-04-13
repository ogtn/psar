package dht.message;

/**
 * Message envoyé à une <code>INode</code> pour lui indiquer le début de sa
 * nouvelle plage de données. Ce message est utilisé lorsqu'une
 * <code>INode</code> sort de l'anneau (<code>leave</code>) pour transmettre la
 * plage de données de l'<code>INode</code> partante à l'<code>INode</code>
 * suivante.
 */
public class MessageBeginRange extends AMessage {

	private static final long serialVersionUID = 1L;
	private long beginRange;

	/**
	 * Crée et initialise un message indiquant le début de la nouvelle plage de
	 * données à l'<code>INode</code> suivante.
	 * 
	 * @param originalSource
	 *            L'identifiant de l'<code>INode</code> ayant créé et envoyé le
	 *            message.
	 * @param beginRange
	 *            La clé du nouveau début de la plage de données.
	 */
	public MessageBeginRange(long originalSource, long beginRange) {
		super(originalSource);
		this.beginRange = beginRange;
	}

	/**
	 * Retourne le début de la nouvelle plage de données.
	 * 
	 * @return Le début de la nouvelle plage de données.
	 */
	public long getBegin() {
		return beginRange;
	}
}
