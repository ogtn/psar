package dht.message;

import dht.ANodeId;
import dht.UInt;

/**
 * Message envoyé à une <code>INode</code> pour lui indiquer la fin de sa
 * nouvelle plage de données. Ce message est utilisé lorsqu'une
 * <code>INode</code> s'insère dans l'anneau (<code>insert</code>) pour
 * transmettre la plage de données de l'<code>INode</code> gérant l'insertion à
 * l' <code>INode</code> entrante.
 */
public class MessageEndRange extends AMessage {

	private static final long serialVersionUID = 1L;
	private UInt end;

	/**
	 * Crée et initialise un message transmettant la fin de plage pour une
	 * <code>INode</code>.
	 * 
	 * @param originalSource
	 *            L'identifiant de l'<code>INode</code> ayant créé et envoyé le
	 *            message.
	 * @param end
	 *            La fin de plage de l'<code>INode</code> destinataire.
	 */
	public MessageEndRange(ANodeId originalSource, UInt end) {
		super(originalSource);
		this.end = end;
	}

	/**
	 * Retourne la fin de la nouvelle plage.
	 * 
	 * @return La fin de la nouvelle plage.
	 */
	public UInt getEnd() {
		return end;
	}
}
