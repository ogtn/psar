package dht;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import dht.Range.Data;
import dht.message.AMessage;
import dht.message.MessageBeginRange;
import dht.message.MessageConnectTo;
import dht.message.MessageData;
import dht.message.MessageDisconnect;
import dht.message.MessageGet;
import dht.message.MessagePing;
import dht.message.MessagePut;

// TODO com
/**
 * Implémentation concrète d'un noeud de l'application.
 */
public class Node implements INode, Runnable {

	private final UInt id;
	private final INetwork inetwork;
	private UInt next;
	private UInt previous;
	private final Range range;
	private ANodeState state;
	private final BlockingQueue<AMessage> queue;

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
	public Node(INetwork inetwork, UInt id) {
		// Lorsqu'il n'a pas de voisins un noeud boucle sur lui même

		queue = new ArrayBlockingQueue<AMessage>(42);
		this.inetwork = inetwork;
		this.id = id;
		next = id;
		previous = null;
		range = new Range(id);
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
	public Node(INetwork inetwork, UInt id, UInt firstNode) {
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
	public UInt getId() {
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

		while (true)
			state.run();
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
	public void put(Object data, UInt key) {
		if (range.inRange(key))
			range.add(key, data);
		else
			inetwork.sendInChannel(next, new MessagePut(id, data, key));
	}

	// TODO synchronized
	@Override
	public void leave() {

		UInt beginRange = range.getBegin();

		// Transfert DATA
		Data data = range.shrinkToLast(id);
		while (data != null) {
			System.out.println(id + " envoi data " + data.getKey() + " à "
					+ next);
			inetwork.sendInChannel(next, new MessageData(id, data.getKey(),
					data.getData()));
			data = range.shrinkToLast(id);
		}

		// Envoi du reste de la plage
		inetwork.sendInChannel(next, new MessageBeginRange(id, beginRange));
		System.out.println(id + " envoi debut plage" + beginRange + " à "
				+ next);

		inetwork.sendTo(previous, new MessageConnectTo(id, next));

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		inetwork.sendInChannel(next, new MessageDisconnect(id));
		inetwork.closeChannel(next);

	}

	// TODO synchronized
	@Override
	public void get(UInt key) {
		if (range.inRange(key)) {
			Object tmpData = range.get(key);

			if (tmpData == null)
				System.out.println("Fail : " + key);
			else
				System.out.println("Ok : " + tmpData + " id: " + id);
		} else
		{
			//System.out.println("Envoie de " + id + " GET a " + next);
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

	UInt getNext() {
		return next;
	}

	void setNext(UInt next) {
		this.next = next;
	}

	void setPrevious(UInt prev) {
		previous = prev;
	}

	UInt getPrevious() {
		return previous;
	}

	void setState(ANodeState state) {

		/*System.out.println("	Le noeud " + id + " passe de l'etat '"
				+ this.state.getClass().getName() + "' à '"
				+ state.getClass().getName() + "'");*/

		this.state = state;
	}
}
