package dht.network.tcp;

import java.io.Serializable;

import dht.message.AMessage;

/**
 * Classe chargée d'encapsuler les messages envoyés par le noeud dans des
 * messages réseaux pour tagger les différents messages.
 */
class NetworkMessage implements Serializable {

	/**
	 * Type de message
	 */
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
	 * Crée et initialise un message réseau.
	 * 
	 * @param content
	 *            Le message du noeud à encapsuler.
	 */
	NetworkMessage(Type type, AMessage content) {
		this.type = type;
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

	/**
	 * Retourne le type de message
	 * 
	 * @return
	 */
	public Type getType() {
		return type;
	}
}