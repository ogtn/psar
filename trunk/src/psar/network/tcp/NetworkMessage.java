package psar.network.tcp;

import java.io.Serializable;

import psar.AMessage;

/**
 * Classe chargée d'encapsuler les messages envoyés par le noeud dans des
 * messages réseaux afin d'assurer la diffusion des adresses IP et ports
 * nécessaires aux connections inter-noeuds.
 */
class NetworkMessage implements Serializable {

	private static final long serialVersionUID = 1L;
	private AMessage content;

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
	NetworkMessage(AMessage content, Couple... couples) {
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
