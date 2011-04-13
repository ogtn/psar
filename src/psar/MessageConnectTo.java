package psar;

/**
 * Suite à une demande d'insertion envoyé via un message
 * {@link MessageAskConnection}, l'<code>INode</code> qui accepte l'insertion,
 * se connecte au demandeur et lui transmet alors son successeur via le présent
 * message.
 */
public class MessageConnectTo extends AMessage {

	private static final long serialVersionUID = 1L;
	private long connectNodeId;

	/**
	 * Crée et initialise un message indiquant le sucesseur auquel se connecter.
	 * 
	 * @param originalSource
	 *            L'identifiant du noeud ayant créé et envoyé le message.
	 * @param connectNodeId
	 *            L'identifiant du noeud auquel le noeud destinataire doit se
	 *            connecter.
	 */
	public MessageConnectTo(long originalSource, long connectNodeId) {
		super(originalSource);
		this.connectNodeId = connectNodeId;
	}

	/**
	 * Retourne L'identifiant du noeud auquel le noeud destinataire doit se
	 * connecter.
	 * 
	 * @return L'identifiant du noeud auquel le noeud destinataire doit se
	 *         connecter.
	 */
	public long getConnectNodeId() {
		return connectNodeId;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuilder strBuild = new StringBuilder();
		strBuild.append("\n===== MessageConnectTo =====\n");
		strBuild.append("source : " + getSource() + "\n");
		strBuild.append("original source : " + getOriginalSource() + "\n");
		strBuild.append("Identifiant du noeud auquel se connecter : "
				+ connectNodeId + "\n");
		strBuild.append("\n============================\n");
		return strBuild.toString();
	}
}
