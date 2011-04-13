package dht;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import dht.Range.Data;
import dht.message.AMessage;
import dht.message.MessageAskConnection;
import dht.message.MessageBeginRange;
import dht.message.MessageConnect;
import dht.message.MessageConnectTo;
import dht.message.MessageDataRange;
import dht.message.MessageDisconnect;
import dht.message.MessageEndRange;
import dht.message.MessageGet;
import dht.message.MessagePing;
import dht.message.MessagePut;


// TODO com
/**
 * Implémentation concrète d'un noeud de l'application.
 */
public class Node implements INode, Runnable {

	/**
	 * Etat interne du noeud.
	 */

	private enum Status {
		DISCONNECTED, CONNECTING, DISCONNECTING, RUN, CONNECTING_NEXT
	};

	private long id;
	private Status status;
	private INetwork inetwork;
	private long next;
	private long previous;
	private Range range;

	/**
   *
	 * Crée et intialise un noeud déconnecté de tout voisin.
	 * 
	 * @param inetwork
	 *            Objet chargé de fournir les primitives réseaux utilisées par
	 *            le noeud.
	 * @param id
	 *            Identifiant du noeud.
	 */
	public Node(INetwork inetwork, long id) {
		// Lorsqu'il n'a pas de voisins un noeud boucle sur lui même
		this(inetwork, id, id);
		range = new Range(id);
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
	public Node(INetwork inetwork, long id, long firstNode) {
		this.inetwork = inetwork;
		this.id = id;
		next = firstNode;
		previous = -1;
		status = Status.DISCONNECTED;
		range = new Range(id, false);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public long getId() {
		return id;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void run() {

		// Initialisation de la couche réseau
		inetwork.init(this);

		// Je suis le premier noeud
		if (next == id) {
			inetwork.sendTo(next, new MessageConnect(id));
		}

		// Envoi du message de demande de connection
		inetwork.sendTo(next, new MessageAskConnection(id));

		// Status en demande de connection
		status = Status.CONNECTING;

		/*
		 * Filter macFilter = null; Filter mctFilter = null; Filter mcFilter =
		 * null;
		 * 
		 * // Noeud seul if (id == next) { macFilter =
		 * MessageAskConnection.getFilter(id); mctFilter =
		 * MessageConnectTo.getFilter(id); mcFilter =
		 * MessageConnect.getFilter(id); filters.add(macFilter);
		 * filters.add(mctFilter); filters.add(mcFilter); } else {
		 * filters.add(MessageConnectTo.getFilter()); }
		 */

		while (true) {
			AMessage msg = inetwork.receive();

			if (msg instanceof MessageAskConnection) {
				process((MessageAskConnection) msg);
				// filters.remove(macFilter);
			} else if (msg instanceof MessageConnect) {
				process((MessageConnect) msg);
				// filters.remove(mcFilter);
			} else if (msg instanceof MessageConnectTo) {
				process((MessageConnectTo) msg);
				// filters.remove(mctFilter);
			} else if (msg instanceof MessagePing) {
				route((MessagePing) msg);
			} else if (msg instanceof MessageEndRange) {
				process((MessageEndRange) msg);
			} else if (msg instanceof MessagePut) {
				process((MessagePut) msg);
			} else if (msg instanceof MessageDataRange) {
				process((MessageDataRange) msg);
			} else if (msg instanceof MessageGet) {
				process((MessageGet) msg);
			} else if (msg instanceof MessageBeginRange) {
				process((MessageBeginRange) msg);
			} else
				System.err
						.println("[" + id + "] Kernel panic! " + status + msg);
		}
	}

	private void process(MessageBeginRange msg) {
		range.setBegin(msg.getBegin());
	}

	private void process(MessageGet msg) {
		if (range.inRange(msg.getKey())) {
			Object tmpData = range.get(msg.getKey());

			if (tmpData == null)
				System.out.println("Fail : " + msg.getKey());
			else
				System.out.println("Ok : " + tmpData + " id: " + id);
		} else
			inetwork.sendTo(next, msg);
	}

	private void process(MessageDataRange msg) {
		range.setEnd(msg.getEndRange());
		range.addExtend(msg.getKey(), msg.getData());
	}

	private void process(MessageConnectTo msg) {
		next = msg.getConnectNodeId();
		inetwork.sendTo(next, new MessageConnect(id));
		status = Status.RUN;
	}

	private void process(MessageConnect msg) {

		if (msg.getSource() != msg.getOriginalSource())
			throw new IllegalStateException("Oh shit §§§§§§§§§ FUUUUUUUUUUUUUU");

		previous = msg.getSource();
	}

	private void process(MessageEndRange msg) {
		System.out.println("endrange: " + msg.getEnd());
		range.setEnd(msg.getEnd());
		range.setBegin(id);
	}

	private void process(MessageAskConnection msg) {

		if (msg.getOriginalSource() == id) {
			next = msg.getOriginalSource();
			return;
		}

		if (range.inRange(msg.getOriginalSource())) {
			status = Status.CONNECTING_NEXT;

			inetwork.sendTo(next, new MessageDisconnect(id));

			// Etablissement de la connection
			inetwork.sendTo(msg.getOriginalSource(), new MessageConnectTo(id,
					next));

			long endOfRange = range.getEnd();

			// Transfert DATA
			Data data = range.shrinkToLast(msg.getOriginalSource());
			while (data != null) {
				System.out.println(id + " envoi data " + data.getKey() + " à "
						+ msg.getOriginalSource());
				inetwork.sendTo(msg.getOriginalSource(), new MessageDataRange(
						id, data.getKey(), data.getData(), endOfRange));
				data = range.shrinkToLast(msg.getOriginalSource());
			}

			// Calcul de la nouvelle plage
			range.shrinkEnd(msg.getOriginalSource());

			// Envoi de la plage
			inetwork.sendTo(msg.getOriginalSource(), new MessageEndRange(id,
					endOfRange));
			System.out.println(id + " envoi fin plage" + endOfRange + " à "
					+ msg.getOriginalSource());

			next = msg.getOriginalSource();
		} else {
			inetwork.sendTo(next, msg);
		}
	}

	private void process(MessagePut msg) {
		if (range.inRange(msg.getKey()) == false) {
			System.out.println("Route MessagePut Data : " + msg.getKey());
			inetwork.sendTo(next, msg);
		} else
			range.add(msg.getKey(), msg.getData());
	}

	private void route(MessagePing msg) {

		Map<Long, Object> data = range.getData();
		Iterator<Entry<Long, Object>> iter = data.entrySet().iterator();
		String out = "";

		while (iter.hasNext()) {
			Entry<Long, Object> entry = iter.next();
			out += "{" + entry.getKey() + " : " + entry.getValue() + "}";
		}

		System.out.println("Id: " + id + " " + range + "  " + out);

		if (msg.getOriginalSource() != id)
			inetwork.sendTo(next, msg);
	}

	// TODO synchronized
	public void ping() {
		inetwork.sendTo(next, new MessagePing(id));
	}

	// TODO synchronized
	public void put(Object data, long key) {

		if (range.inRange(key))
			range.add(key, data);
		else
			inetwork.sendTo(next, new MessagePut(id, data, key));
	}

	// TODO synchronized
	public void leave() {

		long beginRange = range.getBegin();

		// Envoi du reste de la plage
		inetwork.sendTo(next, new MessageBeginRange(id, beginRange));
		System.out.println(id + " envoi debut plage" + beginRange + " à "
				+ next);

		inetwork.sendTo(next, new MessageDisconnect(id));
		inetwork.sendTo(previous, new MessageConnectTo(id, next));
		inetwork.sendTo(previous, new MessageDisconnect(id));

	}

	// TODO synchronized
	public void get(long key) {
		if (range.inRange(key)) {
			Object tmpData = range.get(key);

			if (tmpData == null)
				System.out.println("Fail : " + key);
			else
				System.out.println("Ok : " + tmpData + " id: " + id);
		} else
			inetwork.sendTo(next, new MessageGet(id, key));
	}

	@Override
	public Range getRange() {
		return range;
	}

	@Override
	public String toString() {
		return "id : " + id + "\nstatus : " + status + "\nnext " + next + "\n"
				+ inetwork.toString();
	}
}
