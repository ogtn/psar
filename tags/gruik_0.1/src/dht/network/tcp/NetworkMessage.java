package dht.network.tcp;

import java.io.Serializable;

import dht.message.AMessage;

/**
 * Classe chargée d'encapsuler les messages envoyés par le noeud dans des
 * messages réseaux afin d'assurer la diffusion des adresses IP et ports
 * nécessaires aux connections inter-noeuds.
 */
class NetworkMessage implements Serializable {

	/**
	 * Type de message
	 * */
	enum Type {
		/* Lorsque l'on ouvre un canal */
		OPEN_CHANNEL,
		/* Lorsque l'on ferme un canal */
		CLOSE_CHANNEL,
		/* Lorsque l'on envoie un messsage via un canal */
		MESSAGE_IN_CHANNEL,
		/* Lorsque l'on envoie un messsage via un canal ouvert temporairement */
		MESSAGE_OUT_CHANNEL
	}

	private static final long serialVersionUID = 1L;
	private final AMessage content;
	private final Type type;

	/**
	 * Couples devant être transmis entre les noeuds.
	 */
	private Couple[] couples;

	/**
	 * Crée et initialise un message réseau.
	 * 
	 * @param content
	 *            Le message du noeud à encapsuler.
	 * @param couples
	 *            Les couples d'identifiant de noeuds/et d'identifiants réseaux
	 */
	NetworkMessage(Type type, AMessage content, Couple... couples) {
		this.type = type;
		this.couples = couples;
		this.content = content;
	}

	/**
	 * Retourne le message envoyé par le noeud et transporté par le message
	 * réseau
	 * 
	 * @return Le message envoyé par le noeud source.
	 */
	AMessage getContent() {
		return content;
	}

	// TODO
	/**
	 * Retourne le type de message
	 * 
	 * @return
	 */
	public Type getType() {
		return type;
	}

	/**
	 * Retourne les couples d'identifiant de noeuds/et d'identifiants réseaux
	 * transportés par le noeud.
	 * 
	 * @return Les couples d'identifiants.
	 */
	Couple[] getCouples() {
		return couples;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {

		StringBuilder strBuild = new StringBuilder();

		strBuild.append("\n==== NetworkMessage ===\n");
		strBuild.append("Couples: \n");
		for (Couple c : couples) {
			strBuild.append("\t").append(c.toString()).append("\n");
		}
		strBuild.append("Content: ");
		strBuild.append(content.toString());
		strBuild.append("======================\n");

		return strBuild.toString();
	}
}

// OPEN_CONNECTION CONNECTED
// CLOSE_CONNECTION CONNECTED
// MESSAGE_IN_CONNECTION CONNECTED
// MESSAGE_OUT_CONNECTION DISCONNECTED

