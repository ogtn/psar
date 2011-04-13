package psar;

import java.io.Serializable;

/**
 * Classe implémentant la notion de message envoyé entre noeuds.
 */
public abstract class AMessage implements Serializable {

	private static final long serialVersionUID = 1L;

	private final long originalSource;

	private long source;

	/**
	 * Crée et initialise un message.
	 * 
	 * @param originalSource
	 *            L'identifiant du noeud ayant créé et envoyé le message.
	 */
	AMessage(long originalSource) {
		source = this.originalSource = originalSource;
	}

	/**
	 * Retourne l'identifiant du noeud ayant originellement envoyé le message.
	 * 
	 * @return L'identifiant du noeud ayant originellement envoyé le message.
	 */
	// TODO public et pr tt les methodes
	public long getOriginalSource() {
		return originalSource;
	}

	/**
	 * Retourne l'identifiant du dernier noeud ayant routé le message.
	 * 
	 * @return L'identifiant du noeud ayant originellement envoyé le message.
	 */
	public long getSource() {
		return source;
	}

	/**
	 * Modifie l'identifiant du dernier noeud ayant routé le message.
	 * 
	 * @param source
	 *            Le nouvel identifiant du dernier noeud.
	 */
	public void setSource(long source) {
		this.source = source;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuilder strBuild = new StringBuilder();
		String className = getClass().getName();
		strBuild.append("\n=====");
		strBuild.append(getClass().getName());
		strBuild.append("=====\n");
		strBuild.append("source : " + getSource() + "\n");
		strBuild.append("original source : " + getOriginalSource() + "\n");
		strBuild.append("\n=====");
		for (int cpt = 0; cpt < className.length(); cpt++)
			strBuild.append("=");
		strBuild.append("=====\n");
		return strBuild.toString();
	}
}