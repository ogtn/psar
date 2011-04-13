package dht.old;

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
import java.util.List;
import java.util.Map;

import psar1.AMessage;
import psar1.Filter;
import psar1.INetwork;
import psar1.INode;
import psar1.MessageAskConnection;
import psar1.MessageConnect;
import psar1.MessageConnectTo;
import psar1.MessagePing;
import psar1.tools.Tools;

public class NetworkTCP implements INetwork {

    /**
     * Classe ayant pour objectif de stocker un message ne pouvant 
     */
    private static class PendingMessage {
	
	public SocketChannel sc;
	public NetworkMessage msg;
	public SelectionKey key;

	public PendingMessage(SocketChannel sc, NetworkMessage msg,
		SelectionKey key) {
	    this.sc = sc;
	    this.msg = msg;
	    this.key = key;
	}
    }

    private INode node;
    public int id;
    private Selector selector;
    final private Couple me;
    private SocketChannel next;
    final private List<PendingMessage> pendingMessages = new LinkedList<PendingMessage>();
    final private Map<Integer, InetSocketAddress> directory = new HashMap<Integer, InetSocketAddress>();

    NetworkTCP(Couple me, Couple firstNode) {
	this.me = me;
	directory.put(firstNode.getId(), firstNode.getAddr());
	directory.put(me.getId(), me.getAddr());
	next = null;
	try {
	    selector = Selector.open();
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

    NetworkTCP(Couple me) {
	this.me = me;
	directory.put(me.getId(), me.getAddr());
	next = null;
	try {
	    selector = Selector.open();
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

    @Override
    public void init(INode node) {
	try {
	    this.node = node;
	    // Création de notre socket serveur

	    ServerSocketChannel ssChan = null;
	    selector = Selector.open();
	    try {
		// Socket du serveur
		ssChan = ServerSocketChannel.open();
		// Non bloquante
		ssChan.configureBlocking(false);
		// Acquisition de l'adresse socket
		ssChan.socket().bind(me.getAddr());
		// Enregistre notre serveur
		ssChan.register(selector, SelectionKey.OP_ACCEPT);
	    } catch (IOException e) {
		Tools.close(ssChan);
		throw e;
	    }
	} catch (IOException e) {
	    throw new NetworkException(e);
	}
    }

    /**
     * Méthode utilitaire pour envoyer un objet dans un
     * <code>SocketChannel</code>.
     * 
     * @param socketChannel
     *            Le socket channel dans laquelle on envoie l'objet.
     * @param object
     *            L'objet à envoyer dans le socket channel.
     * @throws IOException
     *             Une exception est lancée si l'écriture échoue.
     */
    private void writeObject(SocketChannel socketChannel, Serializable object)
	    throws IOException {
	ObjectOutputStream oos = null;
	ByteArrayOutputStream bos = null;
	try {
	    // Ecriture de l'objet dans un flux de byte
	    bos = new ByteArrayOutputStream();
	    oos = new ObjectOutputStream(bos);
	    oos.writeObject(object);
	    oos.flush();

	    // Conversion du flux de byte en tableau puis en buffer de byte
	    ByteBuffer bufferObject = ByteBuffer.wrap(bos.toByteArray());

	    // Récupération de la taille de l'objet bufferisé
	    ByteBuffer bufferSize = ByteBuffer.allocateDirect(4);
	    bufferSize.putInt(bufferObject.capacity());
	    System.out.println("send " + bufferSize.flip().capacity());

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
     * Méthode utilitaire pour récupérer un objet dans un
     * <code>SocketChannel</code>.
     * 
     * @param <T>
     *            Le type de retour de l'objet attendu.
     * @param socketChannel
     *            Le socket channel dans laquelle on lit l'objet.
     * @return L'objet lu.
     * 
     * @throws IOException
     *             Une exception est lancée si la lecture échoue.
     * @throws ClassNotFoundException
     *             Une exception est lancée si le type récupéré ne peut être
     *             casté.
     */
    private <T extends Serializable> T readObject(SocketChannel socketChannel)
	    throws IOException, ClassNotFoundException {

	System.out.println(id + "socketChannel.isConnected() "
		+ socketChannel.isConnected());
	System.out.println(id + "socketChannel.isOpen()"
		+ socketChannel.isOpen());
	System.out.println(id + "socketChannel.isConnectionPending()"
		+ socketChannel.isConnectionPending());

	System.out.println(id + "socketChannel.isBlocking()"
		+ socketChannel.isBlocking());

	System.out.println(id + "socketChannel" + socketChannel);

	ByteBuffer bufferSize = null, bufferObject = null;
	int objectSize = 0;
	byte[] data = null;
	int nbRead = 0, cptRead = 0;

	// Récupération de la taille de l'objet
	bufferSize = ByteBuffer.allocateDirect(4);
	do {
	    nbRead = socketChannel.read(bufferSize);
	    System.out.println("§§§§ readObject " + id + " §§§§ nbRead: "
		    + nbRead);

	    System.out.println(id + "socketChannel.isConnected() "
		    + socketChannel.isConnected());
	    System.out.println(id + "socketChannel.isOpen()"
		    + socketChannel.isOpen());
	    System.out.println(id + "socketChannel.isConnectionPending()"
		    + socketChannel.isConnectionPending());
	    System.out.println(id + "socketChannel" + socketChannel);
	    System.out.println(id + "socketChannel.isBlocking()"
		    + socketChannel.isBlocking());
	    
	    if(nbRead == -1)
		throw new EOFException();	// TODO
	    
	    cptRead += nbRead != -1 ? nbRead : 0;

	    try {
		Thread.sleep(1000);
	    } catch (InterruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }

	} while (cptRead != 4);
	bufferSize.flip();
	objectSize = bufferSize.getInt();

	// Récupération de l'objet dans un buffer de byte puis dans un tableau
	bufferObject = ByteBuffer.allocateDirect(objectSize);
	cptRead = 0;
	do {
	    System.out.println("§§§§ readObject 2" + id + " §§§§");
	    nbRead = socketChannel.read(bufferObject);
	    cptRead += nbRead != -1 ? nbRead : 0;
	} while (cptRead != objectSize);
	bufferObject.flip();

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
    public void sendTo(int id, MessageAskConnection message) {

	System.out.println("[" + node.getId()
		+ "] envoie un MessageAskConnection à [" + id + "]");

	SocketChannel s = null;
	ObjectOutputStream oos = null;
	try {
	    InetSocketAddress addr = directory.get(id);
	    if (addr == null)
		throw new NodeNotFoundException(node, id);
	    s = SocketChannel.open(addr);
	    writeObject(s, new NetworkMessage(message, me));
	} catch (IOException e) {
	    throw new NetworkException(e);
	} finally {
	    Tools.close(s);
	    Tools.close(oos);
	}
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendTo(int id, MessageConnect message) {

	System.out.println("[" + node.getId()
		+ "] envoie un MessageAskConnection à [" + id + "]");

	try {
	    InetSocketAddress addr = directory.get(id);
	    if (addr == null)
		throw new NodeNotFoundException(node, id);
	    System.out.println("next avant : " + next);
	    next = SocketChannel.open(addr);
	    System.out.println(id + " envoie MessageConnect dans " + next);
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
    public void sendTo(int id, MessageConnectTo message) {

	System.out.println("[" + node.getId()
		+ "] envoie un MessageAskConnection à [" + id + "]");

	try {
	    InetSocketAddress addr = directory.get(id);
	    if (addr == null)
		throw new NodeNotFoundException(node, id);
	    //Tools.close(next);
	    // TODO
	    next = SocketChannel.open(addr);
	    writeObject(next, new NetworkMessage(message, me, new Couple(
		    message.getConnectNodeId(), directory.get(message
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
    public void sendTo(int id, MessagePing message) {

	System.out.println("[" + node.getId()
		+ "] envoie un MessageAskConnection à [" + id + "]");

	try {
	    // TODO : fr comparasions
	    // if (addr.getPort() == next.socket().getPort() &&
	    // addr.getAddress().equals(next.socket().getInetAddress())) {
	    writeObject(next, new NetworkMessage(message));
	    // } else {
	    // throw new UnknownError("UnknownError !!!"); // TODO fr exceptio
	    // netowrk :
	    // unklnon node
	    // }
	} catch (IOException e) {
	    Tools.close(next);
	    throw new NetworkException(e);
	}
    }

    // TODO : vrai filtre id emetteur via interface contenant methode
    // boolean accept(Msg m)
    // + penser état
    @Override
    public AMessage receive(Filter... filter) throws NetworkException {

	System.out.println(id + " \t\t Entre dans receive");

	PendingMessage pmsg = null;
	/*
	 * AMessage msg = null; NetworkMessage netMsg = null;
	 */

	try {

	    while (true) {

		if (pendingMessages.size() == 0) {
		    SocketChannel sc = null;

		    selector.select();
		    
		    Iterator<SelectionKey> keys = selector.selectedKeys()
			    .iterator();
		    SelectionKey key = null;

		    while (keys.hasNext()) {
			System.out.println("                                    XHILE */*/*/*/*/*/ */*/* /");
			key = keys.next();
			keys.remove();

			// Nouvelle connexion sur notre serveur
			if (key.isValid() && key.isAcceptable()) {
			    ServerSocketChannel server = (ServerSocketChannel) key
				    .channel();
			    sc = server.accept();
			    break;
			}
			// Envoi de message par un de nos predecesseur
			else if (key.isValid() && key.isReadable()) {
			    sc = (SocketChannel) key.channel();
			    break;
			}
		    }

		    pmsg = new PendingMessage(sc, this
			    .<NetworkMessage> readObject(sc), key);
		} else
		    pmsg = pendingMessages.remove(0);

		/*
		 * if (filter.length == 0 || ArrayUtils.exist(filter,
		 * pmsg.msg.getClass())) {
		 */
		if (pmsg.msg.getContent() instanceof MessageAskConnection) {

		} else if (pmsg.msg.getContent() instanceof MessageConnect) {
		    // pmsg.key.cancel();//TODO supprimer l'ancienne entrée
		    // dans notre selector
		    
		    // TODO
		    //pmsg.sc.isRegistered()
		    
		    pmsg.sc.configureBlocking(false);
		    pmsg.sc.register(selector, SelectionKey.OP_READ);
		} else if (pmsg.msg.getContent() instanceof MessageConnectTo) {
		    pmsg.sc.configureBlocking(false);
		    pmsg.sc.register(selector, SelectionKey.OP_READ);
		}
		break;
		/*
		 * } else { pendingMessages.add(pmsg); }
		 */
	    }

	    // netMsg = pmsg.msg;

	    System.out.println("[" + node.getId() + "] reçoit un message "
		    + pmsg.msg.getContent().getClass().getName() + " de ["
		    + pmsg.msg.getContent().getOriginalSource() + "]");

	} catch (IOException e) {
	    // TODO close(sc); close(ois); bouger try cach
	    // Tools.close(sc);
	    throw new NetworkException(e);
	} catch (ClassNotFoundException e) {
	    throw new NetworkException(e);
	}

	System.out.println(id + " \t\t Sort de receive");

	for (Couple c : pmsg.msg.getCouples()) {
	    directory.put(c.getId(), c.getAddr());
	}

	return pmsg.msg.getContent();
    }

    @Override
    public String toString() {
	StringBuilder strBuild = new StringBuilder();

	strBuild.append("next : " + next + "\n");

	if (selector != null) {
	    Iterator<SelectionKey> keys = selector.keys().iterator();

	    while (keys.hasNext()) {

		SelectionKey key = keys.next();

		if (key.isAcceptable()) {
		    ServerSocketChannel server = (ServerSocketChannel) key
			    .channel();

		    strBuild.append("Mon serveur : "
			    + server.socket().getLocalPort() + "\n");
		}

		// l'évenement correspond à une lecture possible
		// on est donc dans un SocketChannel
		else if (key.isValid() && key.isReadable()) {
		    SocketChannel sc = (SocketChannel) key.channel();

		    strBuild.append("previous : mon port"
			    + sc.socket().getLocalPort() + "\n");
		    strBuild.append("port previous : " + sc.socket().getPort()
			    + "\n");
		}
	    }
	} else
	    strBuild.append("selector : null\n");

	return strBuild.toString();
    }
}
