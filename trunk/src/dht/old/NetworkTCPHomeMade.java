package dht.old;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

import dht.Filter;
import dht.INetwork;
import dht.INode;
import dht.message.AMessage;
import dht.message.MessageAskConnection;
import dht.message.MessageConnect;
import dht.message.MessageConnectTo;
import dht.message.MessageDisconnect;
import dht.message.MessagePing;
import dht.network.tcp.Couple;
import dht.network.tcp.NetworkMessage;
import dht.tools.Selector;
import dht.tools.Tools;

//import psar1.old.Select;

/**
 * Classe de gestion des primitives réseaux.
 */
class NetworkTCPHomeMade implements INetwork {

    private INode node;
    final private Selector select;
    final private Couple me;
    private ObjectOutputStream next;

    /**
     * Map associant à chaque identifiant de noeud une adresse de socket serveur
     * ou l'on peut le contacter.
     */
    final private Map<Integer, InetSocketAddress> directory;

    /**
     * Crée et initialise un objet de gestion du réseau pour un noeud donné.
     * 
     * @param me
     *            Un couple associant l'identifiant du noeud à son adresse et
     *            port d'écoute de son serveur.
     */
    NetworkTCPHomeMade(Couple me) {
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
    NetworkTCPHomeMade(Couple me, Couple firstNode) {
	this.me = me;
	directory = new HashMap<Integer, InetSocketAddress>();
	directory.put(firstNode.getId(), firstNode.getAddr());
	directory.put(me.getId(), me.getAddr());
	next = null;
	select = new Selector();
	node = null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void init(INode node) {

	if (this.node != null)
	    throw new IllegalStateException("init a déja été appellée");

	this.node = node;

	try {
	    ServerSocket ss = null;

	    try {
		ss = new ServerSocket(me.getAddr().getPort());
		select.add(ss);
	    } catch (IOException e) {
		Tools.close(ss);
		throw e;
	    }
	} catch (IOException e) {
	    throw new NetworkException(e);
	}
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendTo(int id, MessageAskConnection message) {

	System.out.println("[" + node.getId()
		+ "] envoie un MessageAskConnection à [" + id + "]");

	Socket s = null;
	ObjectOutputStream oos = null;
	try {
	    InetSocketAddress addr = directory.get(id);
	    if (addr == null)
		throw new NodeNotFoundException(node, id);

	    s = new Socket(addr.getAddress(), addr.getPort());
	    oos = new ObjectOutputStream(s.getOutputStream());
	    oos.reset();
	    oos.writeObject(new NetworkMessage(message, me));
	    oos.flush();
	} catch (IOException e) {
	    throw new NetworkException(e);
	} finally {
	    // TODO qui gere le lien
	    //Tools.close(s);
	    //Tools.close(oos);
	}
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendTo(int id, MessageConnect message) {

	System.out.println("[" + node.getId()
		+ "] envoie un MessageConnect à [" + id + "]");

	try {
	    InetSocketAddress addr = directory.get(id);
	    if (addr == null)
		throw new NodeNotFoundException(node, id);
	    next = new ObjectOutputStream(new Socket(addr.getAddress(), addr
		    .getPort()).getOutputStream());
	    next.writeObject(new NetworkMessage(message));
	    next.flush();
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
		+ "] envoie un MessageConnectTo à [" + id + "]");

	try {
	    InetSocketAddress addr = directory.get(id);
	    if (addr == null)
		throw new NodeNotFoundException(node, id);

	    Tools.close(next);
	    next = new ObjectOutputStream(new Socket(addr.getAddress(), addr
		    .getPort()).getOutputStream());

	    NetworkMessage nm = new NetworkMessage(message, me, new Couple(
		    message.getConnectNodeId(), directory.get(message
			    .getConnectNodeId())));

	    next.writeObject(nm);
	    next.flush();
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
		+ "] envoie un MessagePing à [" + id + "]");

	try {
	    InetSocketAddress addr = directory.get(id);
	    if (addr == null)
		throw new NodeNotFoundException(node, id);
	    // TODO Fr la recherche

	    // TODO : fr comparasions
	    // if (addr.getPort() == next.socket().getPort() &&
	    // addr.getAddress().equals(next.socket().getInetAddress())) {
	    next.writeObject(new NetworkMessage(message));
	    next.flush();
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
    public AMessage receive(Filter... filter) {
	ObjectInputStream ois = null;
	AMessage msg = null;
	NetworkMessage netMsg = null;

	try {

	    select.select();

	    if (select.isReadObject()) {
		netMsg = select.getObject();
		msg = netMsg.getContent();

		System.out.println("[" + node.getId() + "] reçoit un message "
			+ msg.getClass().getName() + " de ["
			+ msg.getOriginalSource() + "]");

	    } else {
		Socket s = select.getSocket();

		// Socket s = ss.accept();
		ois = new ObjectInputStream(s.getInputStream());
		netMsg = (NetworkMessage) ois.readObject();
		msg = netMsg.getContent();

		System.out.println("[" + node.getId() + "] reçoit un message "
			+ msg.getClass().getName() + " de ["
			+ msg.getOriginalSource() + "]");

		if (msg instanceof MessageAskConnection) {

		} else if (msg instanceof MessageConnect) {

		    System.out.println("[" + node.getId()
			    + "] ajoute un stream dans son select ");

		    /*
		     * Object object = ois.readObject();
		     * System.out.println("oooooooooo0 " + object);
		     */
		    select.add(ois, node.getId());
		} else if (msg instanceof MessageConnectTo) {

		    System.out.println("[" + node.getId()
			    + "] ajoute un stream dans son select ");

		    /*
		     * Object object = ois.readObject();
		     * System.out.println("oooooooooooooooooooooooooooooooo1 " +
		     * object + " [" + node.getId() + "]");
		     */
		    select.add(ois, node.getId());
		}
		
	    }
	    
	    for (Couple c : netMsg.getCouples()) {
		directory.put(c.getId(), c.getAddr());
	    }
	} catch (IOException e) {

	    System.out.println("EXCEPTION [" + node.getId() + "]");

	    // TODO close(sc); close(ois); bouger try cach
	    // Tools.close(sc);
	    throw new NetworkException(e);
	} catch (ClassNotFoundException e) {
	    throw new NetworkException(e);
	} finally {

	}

	return msg;
    }

    @Override
    public String toString() {
	StringBuilder strBuild = new StringBuilder();

	strBuild.append("next : " + next + "\n");

	return strBuild.toString();
    }
}
