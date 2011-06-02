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
	public UInt getNumericID() {
		return id;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {

		if (obj instanceof ANodeId) {
			return ((ANodeId) obj).getNumericID().equals(id);
		}
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		return id.hashCode();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return id.toString();
	}
}