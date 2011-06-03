package dht.network.tcp;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import dht.ANodeId;
import dht.INetwork;
import dht.INetworkListener;
import dht.INode;
import dht.message.AMessage;
import dht.message.MessageFault;
import dht.network.tcp.NetworkMessage.Type;
import dht.tools.Tools;

/**
 * Interface implémentant les primitives de base de la couche réseau via le
 * protocole TCP .
 */
class NetworkTCP implements INetwork {

	private static final int NETWORK_TIMEOUT = 200;

	private INode node;
	private Selector selector;
	private TCPId nextId;
	private SocketChannel nextChannel;
	private InetAddress localhost;

	/**
	 * Connexions en attente
	 */
	private final Queue<SocketChannel> pendingConnections;

	private final List<INetworkListener> listeners;

	/**
	 * Crée et initialise un objet de gestion du réseau pour un noeud donné.
	 */
	public NetworkTCP() {
		node = null;
		selector = null;
		nextId = null;
		nextChannel = null;
		pendingConnections = new LinkedList<SocketChannel>();
		listeners = new ArrayList<INetworkListener>();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init(INode node) throws NetworkException {

		ServerSocketChannel ssChan = null;

		try {
			localhost = InetAddress.getLocalHost();

			selector = Selector.open();

			if (node == null)
				throw new IllegalStateException("node is null");

			if (this.node != null)
				throw new IllegalStateException("init method was already called");

			// Création de notre socket serveur
			ssChan = ServerSocketChannel.open();
			// Non bloquante
			ssChan.configureBlocking(false);
			// Réutilisation de l'adresse
			ssChan.socket().setReuseAddress(true);
			// Acquisition de l'adresse socket
			ssChan.socket().bind(
					new InetSocketAddress("0.0.0.0", narrowToNetworkId(node.getId()).getAddress().getPort()));
			/*
			 * ssChan.socket().bind( new InetSocketAddress("0.0.0.0",
			 * narrowToNetworkId( node.getId()).getAddress().getPort()));
			 */
			// Enregistre notre serveur
			ssChan.register(selector, SelectionKey.OP_ACCEPT);
			this.node = node;
		} catch (IOException e) {
			Tools.close(ssChan);
			throw new NetworkException(e);
		}
	}

	/**
	 * Méthode utilitaire pour écrire un objet dans une
	 * <code>SocketChannel</code>.
	 * 
	 * @param socketChannel
	 *            La <code>SocketChannel</code> dans laquelle on envoie l'objet.
	 * @param object
	 *            L'objet à envoyer dans la <code>SocketChannel</code>.
	 * @throws IOException
	 *             Une exception est lancée si l'écriture échoue.
	 */
	private void writeObject(SocketChannel socketChannel, Serializable object) throws IOException {

		ObjectOutputStream oos = null;
		ByteArrayOutputStream bos = null;
		ByteBuffer bufferObject = null, bufferSize = null;

		try {
			// Ecriture de l'objet dans un flux de byte
			bos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(bos);
			oos.writeObject(object);
			oos.flush();

			// Conversion du flux de byte en tableau puis en buffer de byte
			bufferObject = ByteBuffer.wrap(bos.toByteArray());

			// Récupération de la taille de l'objet bufferisé
			bufferSize = ByteBuffer.allocateDirect(4);
			bufferSize.putInt(bufferObject.capacity());
			bufferSize.flip();

			// Ecriture de la taille de l'objet
			socketChannel.write(bufferSize);
			// Ecriture de l'objet
			socketChannel.write(bufferObject);
		} finally {
			Tools.close(bos);
			Tools.close(oos);
		}
	}

	/**
	 * Méthode utilitaire pour lire un objet dans une <code>SocketChannel</code>
	 * .
	 * 
	 * @param <T>
	 *            Le type de retour de l'objet attendu.
	 * @param socketChannel
	 *            La <code>SocketChannel</code> dans laquelle on lit l'objet.
	 * @return L'objet lu.
	 * 
	 * @throws IOException
	 *             Une exception est lancée si la lecture échoue.
	 * @throws ClassNotFoundException
	 *             Une exception est lancée si le type récupéré ne peut être
	 *             casté.
	 */
	@SuppressWarnings("unchecked")
	private <T extends Serializable> T readObject(SocketChannel socketChannel) throws IOException,
			ClassNotFoundException {

		ByteBuffer bufferSize = null, bufferObject = null;
		int objectSize = 0;
		byte[] data = null;
		int nbRead = 0, cptRead = 0;

		// Récupération de la taille de l'objet
		bufferSize = ByteBuffer.allocateDirect(4);
		do {
			nbRead = socketChannel.read(bufferSize);

			// Si la connexion a été coupée
			if (nbRead == -1)
				throw new EOFException();

			cptRead += nbRead;

		} while (cptRead != 4);

		bufferSize.flip();
		objectSize = bufferSize.getInt();

		// Récupération de l'objet dans un buffer de byte puis dans un tableau
		bufferObject = ByteBuffer.allocateDirect(objectSize);
		cptRead = 0;
		do {
			nbRead = socketChannel.read(bufferObject);

			// Si la connexion a été coupée
			if (nbRead == -1)
				throw new EOFException();

			cptRead += nbRead;

		} while (cptRead != objectSize);

		bufferObject.flip();

		// Récupération dans un tableau de byte
		data = new byte[bufferObject.capacity()];
		bufferObject.get(data, 0, data.length);

		// Lecture de l'objet depuis le tableau de byte
		ByteArrayInputStream bis = new ByteArrayInputStream(data);
		ObjectInputStream ois = new ObjectInputStream(bis);

		return (T) ois.readObject();
	}

	// TODO
	private <T extends Serializable> T nonBlockingReadObject(SocketChannel socketChannel) throws IOException,
			ClassNotFoundException {

		ByteBuffer bufferSize = null, bufferObject = null;
		int objectSize = 0;
		byte[] data = null;
		int nbRead = 0, cptRead = 0;

		// Récupération de la taille de l'objet
		bufferSize = ByteBuffer.allocateDirect(4);
		do {
			nbRead = socketChannel.read(bufferSize);

			// Si la connexion a été coupée
			if (nbRead == -1)
				throw new EOFException();
			else if (nbRead == 0)
				return null;

			cptRead += nbRead;

		} while (cptRead != 4);

		bufferSize.flip();
		objectSize = bufferSize.getInt();

		// Récupération de l'objet dans un buffer de byte puis dans un tableau
		bufferObject = ByteBuffer.allocateDirect(objectSize);
		cptRead = 0;
		do {
			nbRead = socketChannel.read(bufferObject);

			// Si la connexion a été coupée
			if (nbRead == -1)
				throw new EOFException();
			else if (nbRead == 0)
				return null;

			cptRead += nbRead;

		} while (cptRead != objectSize);

		bufferObject.flip();

		// Récupération dans un tableau de byte
		data = new byte[bufferObject.capacity()];
		bufferObject.get(data, 0, data.length);

		// Lecture de l'objet depuis le tableau de byte
		ByteArrayInputStream bis = new ByteArrayInputStream(data);
		ObjectInputStream ois = new ObjectInputStream(bis);

		return (T) ois.readObject();
	}

	/**
	 * Convertit un identifiant de noeud en identifiant de réseau TCP.
	 * 
	 * @param id
	 *            L'identifiant de noeud à convertir.
	 * @return L'identifiant TCP.
	 * @throws BadNodeIdException
	 *             Une exception est lancée si la convertion est impossible car
	 *             l'identifiant passé en paramètre est du mauvais type.
	 */
	private static TCPId narrowToNetworkId(ANodeId id) throws BadNodeIdException {
		if (id instanceof TCPId)
			return (TCPId) id;
		else
			throw new BadNodeIdException(id);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * thread safe.
	 */
	@Override
	public void sendTo(ANodeId id, AMessage message) throws NetworkException {

		SocketChannel next = null;
		TCPId tcpId = narrowToNetworkId(id);

		message.setSource(node.getId());

		try {
			// Adresse de destination = adresse de mon interface
			if (localhost.getHostAddress().equals(tcpId.getAddress().getAddress().getHostAddress()))
				next = SocketChannel.open(new InetSocketAddress("127.0.0.1", tcpId.getAddress().getPort()));
			else
				next = SocketChannel.open(tcpId.getAddress());

			writeObject(next, new NetworkMessage(Type.MESSAGE_OUT_CHANNEL, message));
			fireSendMessage(message, id, false);
		} catch (IOException e) {
			throw new NetworkException(e);
		} finally {
			Tools.close(next);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void openChannel(ANodeId id) throws NetworkException, BadNodeIdException {		
		try {
			TCPId tcpId = narrowToNetworkId(id);

			if (nextChannel != null)
				throw new IllegalStateException("Channel is already open.");
			nextChannel = SocketChannel.open();

			nextChannel.socket().setReuseAddress(true);

			// TODO ss veriables
			
			// Adresse de destination = adresse de mon interface
			if (localhost.getHostAddress().equals(tcpId.getAddress().getAddress().getHostAddress()))
				nextChannel.socket().connect(new InetSocketAddress("127.0.0.1", tcpId.getAddress().getPort()),
						NETWORK_TIMEOUT);
			else
				nextChannel.socket().connect(tcpId.getAddress(), NETWORK_TIMEOUT);

			nextChannel.configureBlocking(false);

			nextId = tcpId;
			writeObject(nextChannel, new NetworkMessage(Type.OPEN_CHANNEL, null));
		} catch (IOException e) {
			Tools.close(nextChannel);
			nextChannel = null;
			nextId = null;
			throw new NetworkException(e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void closeChannel(ANodeId id) throws ChannelNotFoundException, NetworkException {

		if (nextId == null || nextId.equals(id) == false)
			throw new ChannelNotFoundException(node, id);

		try {
			writeObject(nextChannel, new NetworkMessage(Type.CLOSE_CHANNEL, null));
		} catch (IOException e) {
			throw new NetworkException(e);
		} finally {
			Tools.close(nextChannel);
			nextChannel = null;
			nextId = null;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void sendInChannel(ANodeId id, AMessage message) throws ChannelNotFoundException, NetworkException,
			ChannelCloseException {		
		if (nextId == null || nextId.equals(id) == false)
			throw new ChannelNotFoundException(node, id);

		message.setSource(node.getId());

		try {
			writeObject(nextChannel, new NetworkMessage(Type.MESSAGE_IN_CHANNEL, message));

			try {
				Thread.sleep(NETWORK_TIMEOUT);

				NetworkMessage netMsg = this.<NetworkMessage> nonBlockingReadObject(nextChannel);

				if (netMsg == null)
					throw new ChannelCloseException(nextId);

			} catch (ClassNotFoundException e) {
				throw new NetworkException(e);
			} catch (InterruptedException e) {
				throw new NetworkException(e);
			}
			
		} catch (IOException e) {

			if (node.getNextShortcut() != null) {
				nextChannel = null;
				nextId = null;
				openChannel(node.getNextShortcut());
				node.errorNext();
				sendInChannel(nextId, new MessageFault(node.getId(), node.getNextRange()));
				sendInChannel(nextId, message);
			} else
				throw new ChannelCloseException(nextId);

		} catch (ChannelCloseException e) {
			nextChannel = null;
			if (node.getNextShortcut() != null) {
				System.out.println("ERREUR: ouverture du canal vers " + node.getNextShortcut().getNumericID());
				openChannel(node.getNextShortcut());
				node.errorNext();
				sendInChannel(nextId, new MessageFault(node.getId(), node.getNextRange()));
				sendInChannel(nextId, message);
			} else
				throw e;
		}
		
		fireSendMessage(message, id, true);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AMessage receive() throws NetworkException {

		AMessage msg = null;
		NetworkMessage netMsg = null;
		SocketChannel sc = null;
		ServerSocketChannel server = null;

		// try {
		Iterator<SelectionKey> keys = null;
		SelectionKey key = null;

		// On ne sort pas la boucle tant que l'on a pas reçu un message
		while (msg == null) {

			try {
				// Attente d'un évènement sur nos sockets
				selector.select();
			} catch (IOException e) {
				Tools.close(selector);
				throw new NetworkException(e);
			}
			// Clés des sockets ayant reçu un évènement
			keys = selector.selectedKeys().iterator();

			// On traite la première clé activée
			key = keys.next();
			keys.remove();
			try {
				// Nouvelle connexion sur notre serveur
				if (key.isValid() && key.isAcceptable()) {

					server = (ServerSocketChannel) key.channel();
					sc = server.accept();

					netMsg = this.<NetworkMessage> readObject(sc);

					if (netMsg.getType() == Type.OPEN_CHANNEL) {
						sc.configureBlocking(false);

						/*
						 * On ne peut avoir dans notre sélecteur que la socket
						 * serveur courante et une seule connecxion entrante.
						 */
						if (selector.keys().size() == 2) {
							
							System.out.println("IOUUUUUUUUUUUUUUUUUUUUUUUUUUUU\nIOUUUUUUUUUUUUUUUUUUUUUUUUUUUU\nIOUUUUUUUUUUUUUUUUUUUUUUUUUUUU\nIOUUUUUUUUUUUUUUUUUUUUUUUUUUUU\nIOUUUUUUUUUUUUUUUUUUUUUUUUUUUU\nIOUUUUUUUUUUUUUUUUUUUUUUUUUUUU\nIOUUUUUUUUUUUUUUUUUUUUUUUUUUUU\nIOUUUUUUUUUUUUUUUUUUUUUUUUUUUU\nIOUUUUUUUUUUUUUUUUUUUUUUUUUUUU\n");
							// Quand une connexion arrive, avant d'avoir recu le
							// message de deconnexion de la précédente on la
							// met en attente
							pendingConnections.add(sc);
						} else {
							sc.register(selector, SelectionKey.OP_READ);
						}
					} else if (netMsg.getType() == Type.MESSAGE_OUT_CHANNEL) {
						msg = netMsg.getContent();
						sc.close();
					} else {
						throw new IllegalStateException("Unknown type");
					}

					// Envoi de message par un de nos predecesseur
				} else if (key.isValid() && key.isReadable()) {

					sc = (SocketChannel) key.channel();
					netMsg = this.<NetworkMessage> readObject(sc);

					if (netMsg.getType() == Type.CLOSE_CHANNEL) {
						sc.close();
						key.cancel();

						// On a une connexion en attente
						if (pendingConnections.size() > 0) {
							
							System.out.println("YAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\nYAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\nYAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\nYAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\nYAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\nYAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\nYAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\nYAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\nYAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n");
							pendingConnections.remove().register(selector, SelectionKey.OP_READ);
						}
					} else if (netMsg.getType() == Type.MESSAGE_IN_CHANNEL) {
						msg = netMsg.getContent();
						writeObject(sc, new NetworkMessage(Type.MESSAGE_ACK, null));
					} else {
						throw new IllegalStateException("Unknown type");
					}
				} else {
					throw new IllegalStateException("Invalid key");
				}
			} catch (IOException e) {
				pendingConnections.remove(sc);
				// Désenregistre la connexion si elle l'était
				Tools.close(sc);
				key.cancel();
			} catch (ClassNotFoundException e) {
				Tools.close(server);
				Tools.close(sc);
				throw new NetworkException(e);
			}
		}

		/*
		 * } catch (IOException e) { Tools.close(server); Tools.close(sc); throw
		 * new NetworkException(e); } catch (ClassNotFoundException e) {
		 * Tools.close(server); Tools.close(sc); throw new NetworkException(e);
		 * }
		 */

		fireRecvMessage(msg);

		return msg;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addNetworkListener(INetworkListener listener) {
		if (listener == null)
			throw new NullPointerException();

		listeners.add(listener);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removeNetworkListener(INetworkListener listener) {
		if (listener == null)
			throw new NullPointerException();

		listeners.remove(listener);
	}

	/**
	 * Notifie aux listners un évènement envoi de messages.
	 * 
	 * @param message
	 *            Les messages envoyés.
	 */
	private void fireSendMessage(AMessage message, ANodeId id, boolean isInChannel) {
		for (INetworkListener listener : listeners) {
			try {
				listener.eventSendMessage(message, node, id, isInChannel);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Notifie aux listners un évènement réception de messages.
	 * 
	 * @param message
	 *            Les messages envoyés.
	 */
	private void fireRecvMessage(AMessage message) {
		for (INetworkListener listener : listeners) {
			try {
				listener.eventRecvMessage(message, node);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
