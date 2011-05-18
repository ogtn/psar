package dht;

import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;

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

public abstract class ANodeState {

	protected INetwork network;
	protected BlockingQueue<AMessage> queue;
	protected Node node;
	protected Range range;
	Queue<AMessage> buffer;

	ANodeState(INetwork network, BlockingQueue<AMessage> queue, Node node, Range range, Queue<AMessage> buffer) {
		this.network = network;
		this.queue = queue;
		this.node = node;
		this.range = range;
		this.buffer = buffer;
	}

	final AMessage filter() {

		Iterator<AMessage> iter = buffer.iterator();

		/* On trouve, dans la file, un message que l'on peut traiter */
		while (iter.hasNext()) {
			AMessage msg = iter.next();

			if (isAcceptable(msg)) {
				iter.remove();
				return msg;
			}
		}

		/* Sinon */
		while (true) {
			AMessage msg;
			try {
				msg = queue.take();
				if (isAcceptable(msg)) {
					return msg;
				} else
					buffer.add(msg);
			} catch (InterruptedException e) {
				throw new IllegalStateException(e);
			}
		}

		// TODO listener sur les msgs delivres

	}

	/**
	 * ret true si on a un lmsg que l'on peut traietr, false sinon
	 * 
	 * @return
	 */
	boolean pendingMessages() {

		Iterator<AMessage> iter = buffer.iterator();

		while (iter.hasNext()) {
			if (isAcceptable(iter.next()))
				return true;
		}

		while (queue.isEmpty() == false) {
			AMessage msg;
			try {
				msg = queue.take();
			} catch (InterruptedException e) {
				throw new IllegalStateException(e);
			}
			buffer.add(msg);
			if (isAcceptable(msg))
				return true;
		}

		return false;
	}

	abstract boolean isAcceptable(AMessage msg);

	void init() {
	}

	// abstract void run();

	void process(MessageAskConnection msg) {
	}

	void process(MessageBeginRange msg) {
	}

	void process(MessageConnect msg) {
	}

	void process(MessageConnectTo msg) {
	}

	void process(MessageData msg) {
	}

	void process(MessageDataRange msg) {
	}

	void process(MessageEndRange msg) {
	}

	void process(MessageGet msg) {
		if (range.inRange(msg.getKey())) {
			network.sendTo(msg.getOriginalSource(), new MessageReturnGet(node.getId(), range.get(msg.getKey())));
		} else
			network.sendInChannel(node.getNext(), msg);
	}

	void process(MessageReturnGet msg) {
		node.setReturnGet(msg.getData());
	}

	void process(MessagePing msg) {

		if (!((msg.getOriginalSource().equals(node.getId()) && msg.getSource().equals(node.getId()) == false) || node
				.getId().equals(node.getNext()))) {
			network.sendInChannel(node.getNext(), msg);
		}
	}

	void process(MessagePut msg) {
		if (range.inRange(msg.getKey()) == false) {
			network.sendInChannel(node.getNext(), msg);
		} else {
			range.add(msg.getKey(), msg.getData());
		}
	}

	void process(MessageLeave msg) {
	}

	void process(MessageEventConnect msg) {
	}

	void process(MessageEventDisconnect msg) {

		msg.incTtl();

		if (node.getNextShortcut() != null && node.getNextShortcut().equals(msg.getOriginalSource()))
			node.setNextShortcut(msg.getNext());

		// Je suis suivant du suivant du noeud qui se déconnecte
		if (msg.getTtl() == 2)
			msg.setShortcut(node.getId());

		// Je suis le précédent du noeud qui se déconnecte
		if (node.getNext().equals(msg.getOriginalSource())) {
			// Je récupère mon nouveau raccourci
			node.setNextShortcut(msg.getShortcut());
		}

		network.sendInChannel(node.getNext(), msg);
	}

	void process(MessageFault msg) {

	}
}
