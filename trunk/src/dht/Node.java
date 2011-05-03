package dht;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

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
import dht.message.MessageReturnGet;

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
	private final Queue<AMessage> queue;
	// Valeur de retour du get
	private Object returnGet;
	private List<INodeListener> listeners;

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

		queue = new LinkedList<AMessage>();
		this.inetwork = inetwork;
		this.id = id;
		next = id;
		previous = null;
		range = new Range(id.getNumericID());
		state = new StateDisconnected(inetwork, queue, this, range);
		listeners = new ArrayList<INodeListener>();
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
		queue = new LinkedList<AMessage>();
		this.inetwork = inetwork;
		this.id = id;
		next = firstNode;
		previous = null;
		range = new Range();
		state = new StateDisconnected(inetwork, queue, this, range);
		listeners = new ArrayList<INodeListener>();
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
			} else if (msg instanceof MessageReturnGet) {
				state.process((MessageReturnGet) msg);
			} else
				System.err.println("Kernel panic dans "
						+ this.getClass().getName() + " pr msg : '" + msg
						+ "' node : [" + this + "]");
		}
	}

	@Override
	public void ping() {
		inetwork.sendTo(id, new MessagePing(id));
	}

	@Override
	public void put(UInt key, Object data) {
		inetwork.sendTo(id, new MessagePut(id, data, key));
	}

	@Override
	public void leave() {
		inetwork.sendTo(id, new MessageLeave(id));
	}

	@Override
	public Object get(UInt key) {
		synchronized (this) {
			inetwork.sendTo(id, new MessageGet(id, key));
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		return returnGet;
	}

	void setReturnGet(Object data) {
		synchronized (this) {
			returnGet = data;
			notifyAll();
		}
	}

	Range getRange() {
		return range;
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
		fireChangeSate(this.state, state);
		this.state = state;
		this.state.init();
	}

	@Override
	public void addINodeListener(INodeListener listener) {
		if (listener == null)
			throw new NullPointerException();

		listeners.add(listener);
	}

	@Override
	public void removeINodeListener(INodeListener listener) {
		if (listener == null)
			throw new NullPointerException();

		listeners.remove(listener);
	}

	/**
	 * 
	 * @param message
	 */
	private void fireChangeSate(ANodeState oldState, ANodeState newState) {
		for (INodeListener listener : listeners) {
			try {
				listener.changeState(this, oldState, newState);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public String toString() {
		return "id : '" + id + "' next '" + next + "' previous '" + previous
				+ "' status: '" + state.getClass().getName() + "' " + range;
	}
}
