package psar.network.tcp;

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
import java.util.Map;

import psar.AMessage;
import psar.INetwork;
import psar.INode;
import psar.MessageAskConnection;
import psar.MessageConnect;
import psar.MessageConnectTo;
import psar.MessageDisconnect;
import psar.MessagePing;
import psar.tools.Tools;

/**
 * Interface implémentant les primitives de base de la couche réseau via le
 * protocole TCP .
 */
public class NetworkTCP implements INetwork {

	private INode node;
	private Selector selector;
	final private Couple me;
	private SocketChannel next;

	/**
	 * Map associant à chaque identifiant de noeud une adresse de socket serveur
	 * ou l'on peut le contacter.
	 */
	final private Map<Long, InetSocketAddress> directory = new HashMap<Long, InetSocketAddress>();

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
		directory.put(firstNode.getId(), firstNode.getAddr());
		directory.put(me.getId(), me.getAddr());
		next = null;
		node = null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init(INode node) {

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

	@Override
	public void sendTo(long id, AMessage message) throws NodeNotFoundException,
			NetworkException {

		// System.out.println("[" + node.getId() + "] envoie un "
		// + message.getClass().getName() + " à [" + id + "]");

		message.setSource(node.getId());

		try {
			writeObject(next, new NetworkMessage(message));
		} catch (IOException e) {
			Tools.close(next);
			throw new NetworkException(e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void sendTo(long id, MessageAskConnection message) {

		// System.out.println("[" + node.getId()
		// + "] envoie un MessageAskConnection à [" + id + "]");

		InetSocketAddress addr = directory.get(id);
		if (addr == null)
			throw new NodeNotFoundException(node, id);

		message.setSource(node.getId());

		SocketChannel socketChannel = null;
		try {
			socketChannel = SocketChannel.open(addr);

			// TODO AHhhhhhhhhhhhhhhhhhhhhhhh
			if (message.getOriginalSource() != message.getSource()) {
				writeObject(
						socketChannel,
						new NetworkMessage(message, me, new Couple(message
								.getOriginalSource(), directory.get(message
								.getOriginalSource()))));
			} else {
				writeObject(socketChannel, new NetworkMessage(message, me));
			}
		} catch (IOException e) {
			throw new NetworkException(e);
		} finally {
			Tools.close(socketChannel);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void sendTo(long id, MessageConnect message) {

		// System.out.println("[" + node.getId()
		// + "] envoie un MessageConnect à [" + id + "]");

		InetSocketAddress addr = directory.get(id);
		if (addr == null)
			throw new NodeNotFoundException(node, id);

		message.setSource(node.getId());

		try {
			next = SocketChannel.open(addr);
			writeObject(next, new NetworkMessage(message));
		} catch (IOException e) {
			Tools.close(next);
			throw new NetworkException(e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void sendTo(long id, MessageDisconnect message)
			throws NodeNotFoundException, NetworkException {

		// System.out.println("[" + node.getId()
		// + "] envoie un MessageDisconnect à [" + id + "]");

		InetSocketAddress addr = directory.get(id);
		if (addr == null)
			throw new NodeNotFoundException(node, id);

		message.setSource(node.getId());

		try {
			writeObject(next, new NetworkMessage(message, me));
		} catch (IOException e) {
			Tools.close(next);
			throw new NetworkException(e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void sendTo(long id, MessageConnectTo message) {

		// System.out.println("[" + node.getId()
		// + "] envoie un MessageConnectTo à [" + id + "]");

		InetSocketAddress addr = directory.get(id);
		if (addr == null)
			throw new NodeNotFoundException(node, id);
		Tools.close(next);

		message.setSource(node.getId());

		// TODO AHhhhhhhhhhhhhhhhhhhhhhhh
		try {
			next = SocketChannel.open(addr);
			writeObject(
					next,
					new NetworkMessage(message, me, new Couple(message
							.getConnectNodeId(), directory.get(message
							.getConnectNodeId()))));
		} catch (IOException e) {
			Tools.close(next);
			throw new NetworkException(e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AMessage receive() throws NetworkException {

		// TODO

		NetworkMessage netMsg = null;
		SocketChannel sc = null;
		ServerSocketChannel server = null;

		try {
			Iterator<SelectionKey> keys = null;
			SelectionKey key = null;

			// Attente d'un évènement sur nos sockets
			selector.select();

			// Clés des sockets ayant reçu un évènement
			keys = selector.selectedKeys().iterator();

			//System.out.println(node.getId() + "etat du selecteur : "
			//		+ selector.keys().size());

			// On traite la première clé activée
			key = keys.next();
			keys.remove();

			if (key.isValid() && key.isAcceptable()) {
				// Nouvelle connexion sur notre serveur
				server = (ServerSocketChannel) key.channel();
				sc = server.accept();
				netMsg = this.<NetworkMessage> readObject(sc);

				if (netMsg.getContent() instanceof MessageConnectTo) {
					sc.configureBlocking(false);
					sc.register(selector, SelectionKey.OP_READ);
				} else if (netMsg.getContent() instanceof MessageConnect) {
					sc.configureBlocking(false);
					sc.register(selector, SelectionKey.OP_READ);
				}
			} else if (key.isValid() && key.isReadable()) {
				// Envoi de message par un de nos predecesseur
				sc = (SocketChannel) key.channel();
				netMsg = this.<NetworkMessage> readObject(sc);

				if (netMsg.getContent() instanceof MessageDisconnect) {
					System.out.println("MessageDisconnect " + node.getId());
					sc.close();
					key.cancel();
				}
			} else {
				throw new IllegalStateException("Invalid key");
			}

			//if (netMsg.getContent() instanceof MessagePing) {
			//	System.out.println("Ping sur " + node.getId()
			//			+ "etat du selecteur : " + selector.keys().size() + " "
			//			+ selector.selectedKeys().size());
			//}

			// System.out.println("[" + node.getId() + " " + node.getRange()
			// + "] reçoit un message "
			// + netMsg.getContent().getClass().getName() + " de ["
			// + netMsg.getContent().getOriginalSource() + "]");

		} catch (IOException e) {
			Tools.close(server);
			Tools.close(sc);
			throw new NetworkException(e);
		} catch (ClassNotFoundException e) {
			Tools.close(server);
			Tools.close(sc);
			throw new NetworkException(e);
		}

		for (Couple c : netMsg.getCouples()) {
			directory.put(c.getId(), c.getAddr());
		}

		return netMsg.getContent();
	}
}
