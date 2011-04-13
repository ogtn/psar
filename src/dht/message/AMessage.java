package dht.message;

import java.io.Serializable;

/**
 * Classe implémentant la notion de messages envoyés entre <code>INode</code>.
 */
public abstract class AMessage implements Serializable {

	private static final long serialVersionUID = 1L;
	private final long originalSource;
	private long source;

	/**
	 * Crée et initialise un message.
	 * 
	 * @param originalSource
	 *            L'identifiant de l'<code>INode</code> ayant créé et envoyé le
	 *            message.
	 */
	AMessage(long originalSource) {
		source = this.originalSource = originalSource;
	}

	/**
	 * Retourne l'identifiant de l'<code>INode</code> ayant originellement
	 * envoyé le message.
	 * 
	 * @return L'identifiant de l'<code>INode</code> ayant originellement envoyé
	 *         le message.
	 */
	public long getOriginalSource() {
		return originalSource;
	}

	/**
	 * Retourne l'identifiant de la dernière <code>INode</code> ayant routé le
	 * message.
	 * 
	 * @return L'identifiant de l'<code>INode</code> ayant originellement envoyé
	 *         le message.
	 */
	public long getSource() {
		return source;
	}

	/**
	 * Modifie l'identifiant de la dernière <code>INode</code> ayant routé le
	 * message.
	 * 
	 * @param source
	 *            Le nouvel identifiant de la dernière <code>INode</code>.
	 */
	public void setSource(long source) {
		this.source = source;
	}
}