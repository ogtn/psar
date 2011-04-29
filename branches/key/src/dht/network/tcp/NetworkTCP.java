package dht.network.tcp;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import dht.INetwork;
import dht.INode;
import dht.UInt;
import dht.message.AMessage;
import dht.message.MessageAskConnection;
import dht.message.MessageConnectTo;
import dht.message.MessagePing;
import dht.network.tcp.NetworkMessage.Type;
import dht.tools.Tools;

/**
 * Interface implémentant les primitives de base de la couche réseau via le
 * protocole TCP .
 */
public class NetworkTCP implements INetwork {

	private static boolean gruickPrint = false;

	private INode node;
	private Selector selector;
	final private Couple me;
	private UInt nextId;
	private SocketChannel nextChannel;

	/**
	 * Connections en attente
	 */
	private final Queue<SocketChannel> pendingConnections;

	/**
	 * Map associant à chaque identifiant de noeud une adresse de socket serveur
	 * ou l'on peut le contacter.
	 */
	final private Map<UInt, InetSocketAddress> directory;

	/**
	 * Crée et initialise un objet de gestion du réseau pour un noeud donné.
	 * 
	 * @param me
	 *            Un couple associant l'identifiant du noeud à son adresse et
	 *            port d'écoute de son serveur.
	 */
	NetworkTCP(Couple me) {
		this(me, me);
	}

	/**
	 * Crée et initialise un objet de gestion du réseau pour un noeud donné.
	 * 
	 * @param me
	 *            Un couple associant l'identifiant du noeud à son adresse et
	 *            port d'écoute de son serveur.
	 * @param firstNode
	 *            Le triplet adresse/port/identifiant du premier noeud auquel le
	 *            noeud courant va se connecter.
	 */
	NetworkTCP(Couple me, Couple firstNode) {
		this.me = me;
		nextId = null;
		nextChannel = null;
		// queue = new ArrayList<E>();
		directory = new HashMap<UInt, InetSocketAddress>();
		directory.put(firstNode.getId(), firstNode.getAddr());
		directory.put(me.getId(), me.getAddr());
		pendingConnections = new LinkedList<SocketChannel>();
		node = null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init(INode node) throws NetworkException {

		ServerSocketChannel ssChan = null;

		try {
			selector = Selector.open();

			if (this.node != null)
				throw new IllegalStateException(
						"init method was already called");

			// Création de notre socket serveur
			ssChan = ServerSocketChannel.open();
			// Non bloquante
			ssChan.configureBlocking(false);
			// Acquisition de l'adresse socket
			ssChan.socket().bind(me.getAddr());
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
	private void writeObject(SocketChannel socketChannel, Serializable object)
			throws IOException {

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
	private <T extends Serializable> T readObject(SocketChannel socketChannel)
			throws IOException, ClassNotFoundException {

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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void openChannel(UInt id) throws NodeNotFoundException,
			NetworkException {

		InetSocketAddress addr = directory.get(id);
		if (addr == null)
			throw new NodeNotFoundException(node, id);

		try {
			if (nextChannel != null)
				throw new IllegalStateException("Channel is already open.");
			nextChannel = SocketChannel.open(addr);
			nextId = id;
			writeObject(nextChannel, new NetworkMessage(Type.OPEN_CHANNEL,
					null, me));
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
	public void closeChannel(UInt id) throws ChannelNotFoundException,
			NetworkException {

		if (nextId == null || nextId != id)
			throw new ChannelNotFoundException(node, id);

		try {
			writeObject(nextChannel, new NetworkMessage(Type.CLOSE_CHANNEL,
					null));
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
	public void sendTo(UInt id, AMessage message) throws NodeNotFoundException,
			NetworkException {

		if (gruickPrint)
			System.out.println("Envoi de message hors bande : " + message
					+ " de " + node.getId() + " à " + id);

		SocketChannel next = null;
		InetSocketAddress addr = directory.get(id);
		if (addr == null)
			throw new NodeNotFoundException(node, id);

		message.setSource(node.getId());

		try {
			next = SocketChannel.open(addr);
			writeObject(next, new NetworkMessage(Type.MESSAGE_OUT_CHANNEL,
					message, me));
			Tools.close(next);
		} catch (IOException e) {
			Tools.close(next);
			throw new NetworkException(e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void sendInChannel(UInt id, AMessage message)
			throws ChannelNotFoundException, NetworkException {

		if (gruickPrint)
			System.out.println("nextId. "  + nextId + "Envoi de message in bande : " + message
					+ " de " + node.getId() + " à " + id);

		if (nextId == null || nextId.equals(id) == false)
			throw new ChannelNotFoundException(node, id);

		message.setSource(node.getId());

		try {
			writeObject(nextChannel, new NetworkMessage(
					Type.MESSAGE_IN_CHANNEL, message));
		} catch (IOException e) {
			throw new NetworkException(e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void sendInChannel(UInt id, MessageAskConnection message)
			throws NodeNotFoundException, ChannelNotFoundException,
			NetworkException {

		if (gruickPrint)
			System.out.println("Envoi de message in bande : " + message
					+ " de " + node.getId() + " à " + id);

		if (nextId == null || nextId != id)
			throw new ChannelNotFoundException(node, id);

		message.setSource(node.getId());

		try {
			// Recherche de l'adresse réseau du noeud émetteur de la demande
			InetSocketAddress addr = directory.get(message.getOriginalSource());
			if (addr == null)
				throw new NodeNotFoundException(node,
						message.getOriginalSource());

			// On transmet l'adresse réseau du noeud émetteur auquel le neoud
			// récepteur pourra éventuellement répondre plus tard
			Couple couple = new Couple(message.getOriginalSource(), addr);
			writeObject(nextChannel, new NetworkMessage(
					Type.MESSAGE_IN_CHANNEL, message, me, couple));

		} catch (IOException e) {
			throw new NetworkException(e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void sendInChannel(UInt id, MessageConnectTo message)
			throws NodeNotFoundException, ChannelNotFoundException,
			NetworkException {

		if (gruickPrint)
			System.out.println("Envoi de message in bande : " + message
					+ " de " + node.getId() + " à " + id);

		if (nextId == null || nextId != id)
			throw new ChannelNotFoundException(node, id);

		message.setSource(node.getId());

		try {
			// Recherche de l'adresse réseau du noeud à qui le récepteur du
			// message doit se connecter
			InetSocketAddress addr = directory.get(message.getConnectNodeId());
			if (addr == null)
				throw new NodeNotFoundException(node,
						message.getOriginalSource());

			// On transmet l'adresse réseau du noeud auquel le neoud
			// récepteur devra se connecter
			Couple couple = new Couple(message.getConnectNodeId(), addr);
			writeObject(nextChannel, new NetworkMessage(
					Type.MESSAGE_IN_CHANNEL, message, me, couple));

		} catch (IOException e) {
			throw new NetworkException(e);
		}
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

		try {
			Iterator<SelectionKey> keys = null;
			SelectionKey key = null;

			// On ne sort pas la boucle tant que l'on a pas reçu un message
			while (msg == null) {

				// Attente d'un évènement sur nos sockets
				selector.select();

				// Clés des sockets ayant reçu un évènement
				keys = selector.selectedKeys().iterator();

				// On traite la première clé activée
				key = keys.next();
				keys.remove();

				// Nouvelle connexion sur notre serveur
				if (key.isValid() && key.isAcceptable()) {

					server = (ServerSocketChannel) key.channel();
					sc = server.accept();
					netMsg = this.<NetworkMessage> readObject(sc);

					if (netMsg.getType() == Type.OPEN_CHANNEL) {
						sc.configureBlocking(false);

						/*
						 * On ne peut avoir dans notre sélecteur que la socket
						 * serveur courante et une seule connection entrante.
						 */
						if (selector.keys().size() == 2) {
							// Quand une connexion arrive, avant d'avoir recu le
							// message de deconnexion de la précédente on la
							// met en attente
							pendingConnections.add(sc);
						} else {
							sc.register(selector, SelectionKey.OP_READ);
						}
					} else if (netMsg.getType() == Type.MESSAGE_OUT_CHANNEL) {
						msg = netMsg.getContent();
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
							pendingConnections.remove().register(selector,
									SelectionKey.OP_READ);
						}
					} else if (netMsg.getType() == Type.MESSAGE_IN_CHANNEL) {
						msg = netMsg.getContent();
					} else {
						throw new IllegalStateException("Unknown type");
					}
				} else {
					throw new IllegalStateException("Invalid key");
				}

				// Ajout des identifiants supplémentaires dans notre
				// dictionnaire
				for (Couple c : netMsg.getCouples()) {
					directory.put(c.getId(), c.getAddr());
				}
			}
			/*
			 * try { Thread.sleep(250); } catch (InterruptedException e) { //
			 * TODO Auto-generated catch block e.printStackTrace(); }
			 */

			if (true || msg instanceof MessagePing) {

				String str = "\n";
				str += "id: " + node.getId() + " ";
				str += "etat: " + node.getState() + " ";
				// str += "directory : " + directory + "\n";
				// str += "nextId : " + nextId + "\n";
				// str += "nextChannel : " + nextChannel + "\n";
				// str += "nb selectors " + selector.keys().size() + "\n";
				// str += "OriginalSource " + msg.getOriginalSource() + "\n";
				// str += "Source " + msg.getSource() + "\n";
				str += " recoit un MSG " + msg.toString().split("@")[0] + " ";// msg.getClass().getName());
				str += " de " + msg.getSource() + "\n";

				if (gruickPrint)
					System.out
							.println(str);
			}

		} catch (IOException e) {
			Tools.close(server);
			Tools.close(sc);
			throw new NetworkException(e);
		} catch (ClassNotFoundException e) {
			Tools.close(server);
			Tools.close(sc);
			throw new NetworkException(e);
		}

		return msg;
	}
}
