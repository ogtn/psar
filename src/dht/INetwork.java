package dht;

import dht.message.AMessage;
import dht.message.MessageAskConnection;
import dht.message.MessageConnect;
import dht.message.MessageConnectTo;
import dht.message.MessageDisconnect;

/**
 * Interface implémentant la notion de couche réseau fournissant un ensemble de
 * primitives de base à un noeud donné pour envoyer et recevoir des messages.
 */
public interface INetwork {

	/**
	 * Exception lancée lors d'une erreur interne à la couche réseau inférieure.
	 */
	public static class NetworkException extends RuntimeException {

		private static final long serialVersionUID = 1L;

		/**
		 * Crée et initialise une exception lancée lors d'une erreur de la
		 * couche réseau.
		 * 
		 * @param cause
		 *            L'exception de la couche réseau source de l'erreur.
		 */
		public NetworkException(Throwable cause) {
			super(cause);
		}
	}

	/**
	 * Exception lancée lorsqu'un noeud du réseau ne peut communiquer avec un
	 * autre car il ne le connait pas.
	 */
	public static class NodeNotFoundException extends RuntimeException {

		private static final long serialVersionUID = 1L;
		private INode sourceNode;
		private UInt unknowNodeId;

		/**
		 * Crée et initialise une exception indiquant qu'un noeud ne peut
		 * communiquer avec un autre noeud.
		 * 
		 * @param sourceNode
		 *            Le noeud source qui ne peut trouver le noeud.
		 * @param unknowNodeId
		 *            Identifiant du noeud inconnu.
		 */
		public NodeNotFoundException(INode sourceNode, UInt unknowNodeId) {
			this.sourceNode = sourceNode;
			this.unknowNodeId = unknowNodeId;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return super.toString() + "Le noeud " + sourceNode.getId()
					+ " ne connait pas l'id : " + unknowNodeId;
		}
	}

	/**
	 * Crée et initialise la couche réseau, doit être appellé avant tout envoi
	 * ou réception de message.
	 * 
	 * @param node
	 *            Le noeud qui va intéragir avec la couche réseau.
	 * @throws NetworkException
	 *             Une exception est lancée si la couche réseau ne peut
	 *             initialiser ses objets internes.
	 */
	void init(INode node) throws NetworkException;

	// TODO
	void sendTo(UInt id, AMessage message)
			throws NodeNotFoundException, NetworkException;

	/**
	 * Envoie un message {@link MessageAskConnection} à un noeud.
	 * 
	 * @param id
	 *            Identifiant du noeud auquel on envoie le message.
	 * @param message
	 *            Le message envoyé.
	 * @throws NodeNotFoundException
	 *             Une exception est lancée si la couche réseau ne peut trouver
	 *             le noeud à qui on envoie le message.
	 * @throws NetworkException
	 *             Une exception est lancée si une erreur interne à la couché
	 *             réseau se produit.
	 */
	void sendTo(UInt id, MessageAskConnection message)
			throws NodeNotFoundException, NetworkException;

	/**
	 * Envoie un message {@link MessageConnect} à un noeud.
	 * 
	 * @param id
	 *            Identifiant du noeud auquel on envoie le message.
	 * @param message
	 *            Le message envoyé.
	 * @throws NodeNotFoundException
	 *             Une exception est lancée si la couche réseau ne peut trouver
	 *             le noeud à qui on envoie le message.
	 * @throws NetworkException
	 *             Une exception est lancée si une erreur interne à la couché
	 *             réseau se produit.
	 */
	void sendTo(UInt id, MessageConnect message) throws NodeNotFoundException,
			NetworkException;

	/**
	 * Envoie un message {@link MessageDisconnect} à un noeud.
	 * 
	 * @param id
	 *            Identifiant du noeud auquel on envoie le message.
	 * @param message
	 *            Le message envoyé.
	 * @throws NodeNotFoundException
	 *             Une exception est lancée si la couche réseau ne peut trouver
	 *             le noeud à qui on envoie le message.
	 * @throws NetworkException
	 *             Une exception est lancée si une erreur interne à la couché
	 *             réseau se produit.
	 */
	void sendTo(UInt id, MessageDisconnect message)
			throws NodeNotFoundException, NetworkException;

	/**
	 * Envoie un message {@link MessageConnectTo} à un noeud.
	 * 
	 * @param id
	 *            Identifiant du noeud auquel on envoie le message.
	 * @param message
	 *            Le message envoyé.
	 * @throws NodeNotFoundException
	 *             Une exception est lancée si la couche réseau ne peut trouver
	 *             le noeud à qui on envoie le message.
	 * @throws NetworkException
	 *             Une exception est lancée si une erreur interne à la couché
	 *             réseau se produit.
	 */
	void sendTo(UInt id, MessageConnectTo message) throws NodeNotFoundException,
			NetworkException;

	/**
	 * Envoie un message {@link MessagePing} à un noeud.
	 * 
	 * @param id
	 *            Identifiant du noeud auquel on envoie le message.
	 * @param message
	 *            Le message envoyé.
	 * @throws NodeNotFoundException
	 *             Une exception est lancée si la couche réseau ne peut trouver
	 *             le noeud à qui on envoie le message.
	 * @throws NetworkException
	 *             Une exception est lancée si une erreur interne à la couché
	 *             réseau se produit.
	 */
	/*void sendTo(int id, MessagePing message) throws NodeNotFoundException,
			NetworkException;*/

	/**
	 * Méthode bloquante permettant la récupération d'un message depuis la
	 * couche réseau.
	 * 
	 * @return Le message reçu.
	 * @throws NetworkException
	 *             Une exception est lancée si une erreur interne à la couché
	 *             réseau se produit.
	 */
	AMessage receive() throws NetworkException;
}
