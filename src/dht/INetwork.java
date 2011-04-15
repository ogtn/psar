package dht;

import dht.message.AMessage;
import dht.message.MessageAskConnection;
import dht.message.MessageConnectTo;

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
	 * Exception lancée lorsqu'un noeud du réseau ne peut ouvrir une connection
	 * avec un autre car il ne le connait pas.
	 */
	public static class NodeNotFoundException extends RuntimeException {

		private static final long serialVersionUID = 1L;
		private INode sourceNode;
		private UInt unknowNodeId;

		/**
		 * Crée et initialise une exception indiquant qu'un noeud ne peut ouvrir
		 * une connection avec un autre noeud.
		 * 
		 * @param sourceNode
		 *            Le noeud source qui ne peut ouvrir la connection.
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
	 * Exception lancée lorsqu'un noeud du réseau ne peut communiquer avec un
	 * autre car aucun canal les reliants n'est ouvert.
	 */
	public static class ChannelNotFoundException extends RuntimeException {

		private static final long serialVersionUID = 1L;
		private INode sourceNode;
		private UInt unknowNodeId;

		/**
		 * Crée et initialise une exception indiquant qu'un canal n'est pas
		 * ouvert.
		 * 
		 * @param sourceNode
		 *            Le noeud source qui ne peut trouver le canal.
		 * @param unknowNodeId
		 *            Identifiant du noeud lié au canal inconnu.
		 */
		public ChannelNotFoundException(INode sourceNode, UInt unknowNodeId) {
			this.sourceNode = sourceNode;
			this.unknowNodeId = unknowNodeId;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return super.toString() + "Le noeud " + sourceNode.getId()
					+ " n'a pas de cannal ouvert vers l'id : " + unknowNodeId;
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

	/**
	 * Ouvre un canal vers un noeud du réseau.
	 * 
	 * @param id
	 *            L'identifiant du noeud vers lequel on ouvre le canal.
	 * @throws NodeNotFoundException
	 *             Une exception est lancée si la couche réseau ne peut trouver
	 *             le noeud vers qui on ouvre le canal.
	 * @throws NetworkException
	 *             Une exception est lancée si une erreur interne à la couché
	 *             réseau se produit.
	 */
	void openChannel(UInt id) throws NodeNotFoundException,
			NetworkException;

	/**
	 * Ferme un canal ouvert vers un noeud du réseau.
	 * 
	 * @param id
	 *            L'identifiant du noeud vers lequel on ouvre le canal.
	 * @throws ChannelNotFoundException
	 *             Une exception est lancée si la couche réseau ne peut trouver
	 *             le canal à fermer.
	 * @throws NetworkException
	 *             Une exception est lancée si une erreur interne à la couché
	 *             réseau se produit.
	 */
	void closeChannel(UInt id)
			throws ChannelNotFoundException, NetworkException;

	/**
	 * Ouvre transmet un message vers un noeud.
	 * 
	 * @param id
	 *            L'identifiant du noeud vers lequel on transmet le message.
	 * @param message
	 *            Le message transmis.
	 * @throws NodeNotFoundException
	 *             Une exception est lancée si la couche réseau ne peut trouver
	 *             le noeud à qui transmettre le message.
	 * @throws NetworkException
	 *             Une exception est lancée si une erreur interne à la couché
	 *             réseau se produit.
	 */
	void sendTo(UInt id, AMessage message) throws NodeNotFoundException,
			NetworkException;

	/**
	 * Envoi un {@link AMessage} à un noeud sur un canal.
	 * 
	 * @param id
	 *            L'identifiant du noeud vers lequel on transmet le message.
	 * @param message
	 *            Le message envoyé.
	 * @throws ChannelNotFoundException
	 *             Une exception est lancée si l'on ne peut trouver le canal
	 *             associé au noeud destinataire.
	 * @throws NetworkException
	 *             Une exception est lancée si une erreur interne à la couché
	 *             réseau se produit.
	 */
	void sendInChannel(UInt id, AMessage message)
			throws ChannelNotFoundException, NetworkException;

	/**
	 * Envoie un message {@link MessageAskConnection} à un noeud sur un canal.
	 * 
	 * @param id
	 *            Identifiant du noeud auquel on envoie le message.
	 * @param message
	 *            Le message envoyé.
	 * @throws NodeNotFoundException
	 *             Une exception est lancée si la couche réseau ne peut trouver
	 *             le noeud émetteur du {@link MessageAskConnection}.
	 * @throws ChannelNotFoundException
	 *             Une exception est lancée si l'on ne peut trouver le canal
	 *             associé au noeud destinataire.
	 * @throws NetworkException
	 *             Une exception est lancée si une erreur interne à la couché
	 *             réseau se produit.
	 */
	void sendInChannel(UInt id, MessageAskConnection message)
			throws NodeNotFoundException, ChannelNotFoundException,
			NetworkException;

	/**
	 * Envoie un message {@link MessageConnectTo} à un noeud.
	 * 
	 * @param id
	 *            Identifiant du noeud auquel on envoie le message.
	 * @param message
	 *            Le message envoyé.
	 * @throws NodeNotFoundException
	 *             Une exception est lancée si la couche réseau ne peut trouver
	 *             le noeud auquel le récepteur du message
	 *             {@link MessageConnectTo} doit se connecter.
	 * @throws ChannelNotFoundException
	 *             Une exception est lancée si l'on ne peut trouver le canal
	 *             associé au noeud destinataire.
	 * @throws NetworkException
	 *             Une exception est lancée si une erreur interne à la couché
	 *             réseau se produit.
	 */
	void sendInChannel(UInt id, MessageConnectTo message)
			throws NodeNotFoundException, ChannelNotFoundException,
			NetworkException;

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
