package psar;

/**
 * Message envoyé pour attribuer à un noeud sa nouvelle Plage.
 */
public class MessageEndRange extends AMessage {

	private static final long serialVersionUID = 1L;
	private long end;

	/**
	 * Crée et initialise un message transmettant la fin de plage pour un noeud.
	 * 
	 * @param originalSource
	 *            L'identifiant du noeud ayant créé et envoyé le message.
	 * @param end
	 *            La fin de plage du noeud destinataire.
	 */
	public MessageEndRange(long originalSource, long end) {
		super(originalSource);
		this.end = end;
	}

	/**
	 * Retourne la fin de la plage
	 * 
	 * @return La fin de la plage
	 */
	public long getEnd() {
		return end;
	}
}
