package dht.network.tcp;

import java.io.Serializable;
import java.net.InetSocketAddress;

/**
 * Couple associant à un identifiant de noeud une adresse ip et un port.
 */
class Couple implements Serializable {

	private static final long serialVersionUID = 1L;
	private long id;
	private InetSocketAddress addr;

	/**
	 * Crée et initialise un couple identifiant de noeud adresse ip et port.
	 * 
	 * @param id
	 *            Identifiant du noeud.
	 * @param addr
	 *            Adresse et port associé au noeud.
	 */
	Couple(long id, InetSocketAddress addr) {
		this.id = id;
		this.addr = addr;
	}

	/**
	 * Retourne l'identifiant du couple.
	 * 
	 * @return L'identifiant du couple.
	 */
	long getId() {
		return id;
	}

	/**
	 * Retourne l'adresse ip/port du couple.
	 * 
	 * @return L'adresse ip/port du couple.
	 */
	InetSocketAddress getAddr() {
		return addr;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuilder strBuild = new StringBuilder();

		strBuild.append("{");
		strBuild.append("id: " + id + ", addr: " + addr);
		strBuild.append("}");
		return strBuild.toString();
	}
}