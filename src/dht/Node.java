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
import dht.message.MessageData;
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

	private final UInt id;
	private INetwork inetwork;
	private UInt next;
	private UInt previous;
	private Range range;
	private Status status;

	/**
	 * Etat interne du noeud.
	 */
	private enum Status {
		CONNECTING, OTHER
	};

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
	public Node(INetwork inetwork, UInt id, UInt firstNode) {

		assert id != null : "id is null";

		status = Status.OTHER;

		this.inetwork = inetwork;
		this.id = id;
		next = firstNode;
		previous = null;
		range = new Range();
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

		// Je suis le premier noeud
		if (next == id) {
			// Etablissement d'un connection bouclante vers moi même
			inetwork.openChannel(next);
			inetwork.sendInChannel(next, new MessageConnect(id));
		} else {
			// Envoi d'un message de demande de connection
			inetwork.sendTo(next, new MessageAskConnection(id));
			status = status.CONNECTING;
		}

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
			} else if (msg instanceof MessageData) {
				process((MessageData) msg);
			} else
				System.err.println("[" + id + "] Kernel panic! ");
		}
	}

	private void process(MessageData msg) {
		range.insertExtend(msg.getKey(), msg.getData());
		System.out.println(id + ": ajout de la donnée " + msg.getData() + " "
				+ range);
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
			inetwork.sendInChannel(next, msg);
	}

	private void process(MessageDataRange msg) {
		System.out.println("DataRange" + id + ": " + range);
		range.setEnd(msg.getEndRange());
		range.insertExtend(msg.getKey(), msg.getData());
		System.out.println("#DataRange" + id + ": " + range);
	}

	private void process(MessageConnectTo msg) {

		/* Je suis en attente de connection a l'anneau */
		if (status == Status.CONNECTING) {
			status = Status.OTHER;
			next = msg.getConnectNodeId();
			inetwork.openChannel(next);
			inetwork.sendInChannel(next, new MessageConnect(id));
		}
		/* Mon précédent se déconnecte */
		else {
			inetwork.sendInChannel(next, new MessageDisconnect(id));
			inetwork.closeChannel(next);
			next = msg.getConnectNodeId();
			inetwork.openChannel(next);
			inetwork.sendInChannel(next, new MessageConnect(id));
		}

	}

	private void process(MessageConnect msg) {

		if (msg.getSource() != msg.getOriginalSource())
			throw new IllegalStateException("Oh shit §§§§§§§§§ FUUUUUUUUUUUUUU");

		previous = msg.getSource();
	}

	private void process(MessageEndRange msg) {
		range.setEnd(msg.getEnd());
		range.setBegin(id);
	}

	private void process(MessageAskConnection msg) {
		
		if (range.inRange(msg.getOriginalSource())) {

			inetwork.sendInChannel(next, new MessageDisconnect(id));
			inetwork.closeChannel(next);
			
			// Etablissement de la connection
			inetwork.openChannel(msg.getOriginalSource());
			inetwork.sendInChannel(msg.getOriginalSource(), new MessageConnect(id));

			// Envoi du suivant de la connection
			inetwork.sendInChannel(msg.getOriginalSource(),
					new MessageConnectTo(id, next));

			UInt endOfRange = range.getEnd();

			// Transfert DATA
			Data data = range.shrinkToLast(msg.getOriginalSource());
			while (data != null) {
				System.out.println(id + " envoi data " + data.getKey() + " à "
						+ msg.getOriginalSource());
				inetwork.sendInChannel(msg.getOriginalSource(),
						new MessageDataRange(id, data.getKey(), data.getData(),
								endOfRange));
				data = range.shrinkToLast(msg.getOriginalSource());
			}

			// Calcul de la nouvelle plage
			range.shrinkEnd(msg.getOriginalSource());

			// Envoi de la plage
			inetwork.sendInChannel(msg.getOriginalSource(),
					new MessageEndRange(id, endOfRange));
			System.out.println(id + " envoi fin plage" + endOfRange + " à "
					+ msg.getOriginalSource());

			next = msg.getOriginalSource();
		} else {
			inetwork.sendInChannel(next, msg);
		}
	}

	private void process(MessagePut msg) {
		if (range.inRange(msg.getKey()) == false) {
			System.out.println("Route MessagePut Data : " + msg.getKey());
			inetwork.sendInChannel(next, msg);
		} else
			range.add(msg.getKey(), msg.getData());
	}

	private void route(MessagePing msg) {

		if (msg.getOriginalSource().equals(id) == false) {
			Map<UInt, Object> data = range.getData();
			Iterator<Entry<UInt, Object>> iter = data.entrySet().iterator();
			String out = "";

			while (iter.hasNext()) {
				Entry<UInt, Object> entry = iter.next();
				out += "{" + entry.getKey() + " : " + entry.getValue() + "}";
			}

			System.out.println("Ping Id: " + id + " " + range + "  " + out
					+ " previous : " + previous);
			inetwork.sendInChannel(next, msg);
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

		System.out.println("Ping Id: " + id + " " + range + "  " + out
				+ " previous : " + previous);

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
			inetwork.sendInChannel(next, new MessageGet(id, key));
	}

	public Range getRange() {
		return range;
	}

	@Override
	public String toString() {
		return "id : " + id + "\nnext " + next + "\n" + inetwork.toString();
	}
}
