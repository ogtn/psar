package dht;

import dht.message.AMessage;

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
	 * Exception lancée lorsque l'identifiant d'un noeud ne peut être converti
	 * en identifiant de la couche réseau utilisée.
	 */
	public static class BadNodeIdException extends RuntimeException {

		private static final long serialVersionUID = 1L;
		private ANodeId nodeId;

		/**
		 * Crée et initialise une exception lancée suite à un problème de
		 * conversion.
		 * 
		 * @param nodeId
		 *            Le noeud que l'on a tenté de convertir.
		 */
		public BadNodeIdException(ANodeId nodeId) {
			this.nodeId = nodeId;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return super.toString()
					+ "L'identifiant de la classe "
					+ nodeId.getClass()
					+ " n'est pas conforme à celui attendu par la classe réseau.";
		}
	}

	/**
	 * Exception lancée lorsqu'un noeud du réseau ne peut communiquer avec un
	 * autre car aucun canal les reliants n'est ouvert.
	 */
	public static class ChannelNotFoundException extends RuntimeException {

		private static final long serialVersionUID = 1L;
		private INode sourceNode;
		private ANodeId unknowNodeId;

		/**
		 * Crée et initialise une exception indiquant qu'un canal n'est pas
		 * ouvert.
		 * 
		 * @param sourceNode
		 *            Le noeud source qui ne peut trouver le canal.
		 * @param unknowNodeId
		 *            Identifiant du noeud lié au canal inconnu.
		 */
		public ChannelNotFoundException(INode sourceNode, ANodeId unknowNodeId) {
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

	// TODO
	public static class ChannelCloseException extends RuntimeException {

		private static final long serialVersionUID = 1L;
		private ANodeId channelId;

		public ChannelCloseException(ANodeId channelId) {
			this.channelId = channelId;
		}

		public ChannelCloseException(ANodeId channelId, Throwable t) {
			super(t);
			this.channelId = channelId;
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
	 * Envoie un message à un noeud sans passer par un canal ouvert.
	 * 
	 * @param id
	 *            L'identifiant du noeud vers lequel on transmet le message.
	 * @param message
	 *            Le message transmis.
	 * @throws NetworkException
	 *             Une exception est lancée si une erreur interne à la couché
	 *             réseau se produit.
	 */
	void sendTo(ANodeId id, AMessage message) throws NetworkException;

	/**
	 * Ouvre un canal vers un noeud du réseau.
	 * 
	 * @param id
	 *            L'identifiant du noeud vers lequel on ouvre le canal.
	 * @throws NetworkException
	 *             Une exception est lancée si une erreur interne à la couché
	 *             réseau se produit.
	 * @throws BadNodeIdException
	 *             Une exception est lancée si l'implémentation de l'identifiant
	 *             réseau n'est pas adapté à la couche réseau utilisée.
	 */
	void openChannel(ANodeId id) throws NetworkException, BadNodeIdException;

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
	void closeChannel(ANodeId id) throws ChannelNotFoundException,
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
	 *             TODO
	 */
	void sendInChannel(ANodeId id, AMessage message)
			throws ChannelNotFoundException, NetworkException,ChannelCloseException;

	/**
	 * Méthode bloquante permettant la récupération d'un message depuis la
	 * couche réseau.
	 * 
	 * @return Le message reçu.
	 * @throws NetworkException
	 *             Une exception est lancée si une erreur interne à la couché
	 *             réseau se produit.
	 *             TODO
	 */
	AMessage receive() throws NetworkException, ChannelCloseException;

	/**
	 * 
	 * @param listener
	 */
	void addNetworkListener(INetworkListener listener);

	/**
	 * 
	 * @param listener
	 */
	void removeNetworkListener(INetworkListener listener);
}