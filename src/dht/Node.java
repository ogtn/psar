package dht;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import dht.message.AMessage;
import dht.message.MessageAskConnection;
import dht.message.MessageBeginRange;
import dht.message.MessageConnect;
import dht.message.MessageConnectTo;
import dht.message.MessageData;
import dht.message.MessageDataRange;
import dht.message.MessageDisconnect;
import dht.message.MessageEndRange;
import dht.message.MessageGet;
import dht.message.MessageLeave;
import dht.message.MessagePing;
import dht.message.MessagePut;

/**
 * Implémentation concrète d'un noeud de l'application.
 */
public class Node implements INode, Runnable {

	private final ANodeId id;
	private final INetwork inetwork;
	private ANodeId next;
	private ANodeId previous;
	private final Range range;
	private ANodeState state;
	private final BlockingQueue<AMessage> queue;

	/**
	 * Crée et intialise un noeud déconnecté de tout voisin.
	 * 
	 * @param inetwork
	 *            Objet chargé de fournir les primitives réseaux utilisées par
	 *            le noeud.
	 * @param id
	 *            Identifiant du noeud.
	 */
	public Node(INetwork inetwork, ANodeId id) {
		// Lorsqu'il n'a pas de voisins un noeud boucle sur lui même

		queue = new ArrayBlockingQueue<AMessage>(42);
		this.inetwork = inetwork;
		this.id = id;
		next = id;
		previous = null;
		range = new Range(id.getNumericID());
		state = new StateDisconnected(inetwork, queue, this, range);
	}

	/**
	 * Crée et intialise un noeud.
	 * 
	 * @param inetwork
	 *            Objet chargé de fournir les primitives réseaux utilisées par
	 *            le noeud.
	 * @param id
	 *            Identifiant du noeud.
	 * @param firstNode
	 *            Identifiant du premier noeud auquel le noeud devra se
	 *            connecter.
	 */
	public Node(INetwork inetwork, ANodeId id, ANodeId firstNode) {
		queue = new ArrayBlockingQueue<AMessage>(42);
		this.inetwork = inetwork;
		this.id = id;
		next = firstNode;
		previous = null;
		range = new Range();
		state = new StateDisconnected(inetwork, queue, this, range);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ANodeId getId() {
		return id;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void run() {
		// Initialisation de la couche réseau
		inetwork.init(this);
		new Thread() {
			@Override
			public void run() {
				while (true) {
					try {
						queue.put(inetwork.receive());
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}.start();

		// Je me connecte
		setState(new StateConnecting(inetwork, queue, this, range));

		// On sort quand on arrive dans l'état déconnecté
		while (!(state instanceof StateDisconnected)) {
			AMessage msg;

			// Selon l'état on filtre les messages que l'on peut traiter
			// les autres sont laissés dans la file
			msg = state.filter();

			if (msg instanceof MessageAskConnection) {
				state.process((MessageAskConnection) msg);
			} else if (msg instanceof MessageData) {
				state.process((MessageData) msg);
			} else if (msg instanceof MessagePing) {
				state.process((MessagePing) msg);
			} else if (msg instanceof MessageConnect) {
				state.process((MessageConnect) msg);
			} else if (msg instanceof MessagePut) {
				state.process((MessagePut) msg);
			} else if (msg instanceof MessageGet) {
				state.process((MessageGet) msg);
			} else if (msg instanceof MessageBeginRange) {
				state.process((MessageBeginRange) msg);
			} else if (msg instanceof MessageConnectTo) {
				state.process((MessageConnectTo) msg);
			} else if (msg instanceof MessageDisconnect) {
				state.process((MessageDisconnect) msg);
			} else if (msg instanceof MessageDataRange) {
				state.process((MessageDataRange) msg);
			} else if (msg instanceof MessageEndRange) {
				state.process((MessageEndRange) msg);
			} else if (msg instanceof MessageLeave) {
				state.process((MessageLeave) msg);
			} else
				System.err.println("Kernel panic dans "
						+ this.getClass().getName() + " pr msg : '" + msg
						+ "' node : [" + this + "]");
		}
	}

	// TODO synchronized
	@Override
	public void ping() {

		Map<UInt, Object> data = range.getData();
		Iterator<Entry<UInt, Object>> iter = data.entrySet().iterator();
		String out = "";

		while (iter.hasNext()) {
			Entry<UInt, Object> entry = iter.next();
			out += "{" + entry.getKey() + " : " + entry.getValue() + "}";
		}

		System.out.println("PING : " + this);

		inetwork.sendInChannel(next, new MessagePing(id));
	}

	// TODO synchronized
	@Override
	public void put(UInt key, Object data) {
		if (range.inRange(key))
			range.add(key, data);
		else
			inetwork.sendInChannel(next, new MessagePut(id, data, key));
	}

	// TODO synchronized
	@Override
	public void leave() {
		try {
			queue.put(new MessageLeave(id));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// TODO synchronized
	@Override
	public void get(UInt key) {
		
		// TODO dans a fr ds state
		
		if (range.inRange(key)) {
			Object tmpData = range.get(key);

			if (tmpData == null)
				System.out.println("Fail : " + key);
			else
				System.out.println("Ok : " + tmpData + " id: " + id);
		} else {
			// System.out.println("Envoie de " + id + " GET a " + next);
			inetwork.sendInChannel(next, new MessageGet(id, key));
		}
	}

	public Range getRange() {
		return range;
	}

	@Override
	public String toString() {
		return "id : '" + id + "' next '" + next + "' previous '" + previous
				+ "' status: '" + state.getClass().getName() + "' " + range;
	}

	ANodeId getNext() {
		return next;
	}

	void setNext(ANodeId next) {
		this.next = next;
	}

	void setPrevious(ANodeId prev) {
		previous = prev;
	}

	ANodeId getPrevious() {
		return previous;
	}

	void setState(ANodeState state) {

		/*
		 * System.out.println("	Le noeud " + id + " passe de l'etat '" +
		 * this.state.getClass().getName() + "' à '" +
		 * state.getClass().getName() + "'");
		 */

		this.state = state;
		this.state.init();
	}
	
	// TODO delete
	public String getState() {
		return state.getClass().getName();
	}
}
