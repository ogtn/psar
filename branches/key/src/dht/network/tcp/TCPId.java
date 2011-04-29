package dht.network.tcp;

import java.net.InetSocketAddress;

import dht.ANodeId;
import dht.UInt;

/**
 * Identifiant de noeud alliant un couple adresse port permettant une connexion
 * en TCP au noeud.
 */
class TCPId extends ANodeId {

	private static final long serialVersionUID = 1L;
	private final UInt id;
	private final InetSocketAddress address;

	/**
	 * Crée et initialise un identifiant de noeud TCP.
	 * 
	 * @param id
	 *            Identifiant du noeud.
	 * @param address
	 *            Adresse et port vers lesquels on peut ouvrir une connection
	 *            TCP.
	 */
	public TCPId(final UInt id, final InetSocketAddress address) {
		super(id);
		this.id = id;
		this.address = address;
	}

	/**
	 * Retourne l'identifiant du noeud.
	 * 
	 * @return L'identifiant du noeud.
	 */
	@Override
	public UInt getUid() {
		return id;
	}

	/**
	 * Retourne l'adresse IP et le port du noeud.
	 * 
	 * @return L'adresse IP et le port sur lesquels on peut contacter le noeud.
	 */
	public InetSocketAddress getAddress() {
		return address;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {

		if (obj instanceof TCPId) {
			return ((TCPId) obj).getUid().equals(id);
		}

		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "id: " + id + " adress: " + address;
	}
}
