package dht;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import dht.message.AMessage;
import dht.message.MessageAskConnection;
import dht.message.MessageBeginRange;
import dht.message.MessageConnect;
import dht.message.MessageConnectTo;
import dht.message.MessageData;
import dht.message.MessageDataRange;
import dht.message.MessageEndRange;
import dht.message.MessageEventConnect;
import dht.message.MessageEventDisconnect;
import dht.message.MessageFault;
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
	private ANodeId nextShortcut;
	private ANodeId previous;
	private final Range range;
	private ANodeState state;
	private final BlockingQueue<AMessage> queue;
	private final Queue<AMessage> buffer;
	// Valeur de retour du get
	private Object returnGet;
	private List<INodeListener> listeners;
	private HashMap<UInt, Serializable> backup;

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
		queue = new LinkedBlockingQueue<AMessage>();
		this.inetwork = inetwork;
		this.id = id;
		next = id;
		nextShortcut = null;
		previous = null;
		range = new Range(id.getNumericID());
		buffer = new LinkedList<AMessage>();
		state = new StateDisconnected(inetwork, queue, this, range, buffer);
		listeners = new ArrayList<INodeListener>();
		backup = new HashMap<UInt, Serializable>();
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
		queue = new LinkedBlockingQueue<AMessage>();
		this.inetwork = inetwork;
		this.id = id;
		next = firstNode;
		nextShortcut = null;
		previous = null;
		range = new Range();
		buffer = new LinkedList<AMessage>();
		state = new StateDisconnected(inetwork, queue, this, range, buffer);
		listeners = new ArrayList<INodeListener>();
		backup = new HashMap<UInt, Serializable>();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ANodeId getId() {
		return id;
	}

	@Override
	public ANodeId getPrev() {
		return previous;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void run() {
		// Initialisation de la couche réseau
		inetwork.init(this);

		new Thread() {
			public void run() {
				while (true) {
					synchronized (queue) {
						queue.add(inetwork.receive());
					}
				}
			};
		}.start();

		// Je me connecte
		setState(new StateConnecting(inetwork, queue, this, range, buffer));

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
			} else if (msg instanceof MessageDataRange) {
				state.process((MessageDataRange) msg);
			} else if (msg instanceof MessageEndRange) {
				state.process((MessageEndRange) msg);
			} else if (msg instanceof MessageLeave) {
				state.process((MessageLeave) msg);
			} else if (msg instanceof MessageReturnGet) {
				state.process((MessageReturnGet) msg);
			} else if (msg instanceof MessageEventConnect) {
				state.process((MessageEventConnect) msg);
			} else if (msg instanceof MessageEventDisconnect) {
				state.process((MessageEventDisconnect) msg);
			} else if (msg instanceof MessageFault) {
				state.process((MessageFault) msg);
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
	public void put(UInt key, Serializable data) {
		// TODO on pourrait le mettre ds la queue il faudrait alors verifier le
		// code du routage
		// same pr les autres methodes
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

	public Range getRange() {
		return range;
	}

	// TODO virer la visibilité
	@Override
	public ANodeId getNext() {
		return next;
	}

	void setNext(ANodeId next) {
		this.next = next;
	}

	public ANodeId getNextShortcut() {
		return nextShortcut;
	}

	void setNextShortcut(ANodeId nextShortcut) {
		this.nextShortcut = nextShortcut;
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
				listener.eventChangeState(this, oldState, newState);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	// TODO virer inutile
	@Override
	public ANodeState getState() {
		return state;
	}

	@Override
	public UInt getNextRange() {
		return new UInt((range.getEnd().toLong() + 1) % UInt.MAX_KEY);
	}

	@Override
	public String toString() {

		String buff = "";
		String shortcutStr = nextShortcut != null ? String.valueOf(nextShortcut
				.getNumericID()) : null;
		String prevStr = previous != null ? String.valueOf(previous
				.getNumericID()) : null;
		String nextStr = next != null ? String.valueOf(next.getNumericID())
				: null;
		String statusStr = state.getClass().getName();
		statusStr = statusStr.substring("dht.State".length(), statusStr
				.length());

		buff += "id: " + id.getNumericID() + " next: " + nextStr + " prev: "
				+ prevStr;
		buff += " short " + shortcutStr + " status: " + statusStr;
		buff += "\n" + range;

		// TODO builder

		return buff;
	}

	@Override
	public void errorNext() {
		next = nextShortcut;
		nextShortcut = null;
	}

	void addBackup(UInt key, Serializable data) {
		backup.put(key, data);
	}

	Map<UInt, Serializable> getBackup()
	{
		return backup;
	}
}
