package dht;

import java.io.Serializable;

/**
 * Classe implémentant la notion d'identifiant de noeud pouvant être
 * sous-classée pour contenir les informations nécessaires à la couche réseau.
 */
public abstract class ANodeId implements Serializable {

	private static final long serialVersionUID = 1L;

	private final UInt id;

	/**
	 * Crée et initialise un identifiant de noeud.
	 * 
	 * @param id
	 *            L'identifiant numérique du noeud.
	 */
	public ANodeId(final UInt id) {
		this.id = id;
	}

	/**
	 * Retourne l'identifiant numérique du noeud.
	 * 
	 * @return L'identifiant numérique du noeud.
	 */
	public UInt getUid() {
		return id;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {

		if (obj instanceof ANodeId) {
			return ((ANodeId) obj).getUid().equals(id);
		}
		return false;
	}
}
