package dht;

import java.util.Iterator;
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
import dht.message.MessageReturnGet;

public abstract class ANodeState {

	protected INetwork network;
	protected BlockingQueue<AMessage> queue;
	protected Node node;
	protected Range range;

	ANodeState(INetwork network, BlockingQueue<AMessage> queue, Node node,
			Range range) {
		this.network = network;
		this.queue = queue;
		this.node = node;
		this.range = range;
	}

	final AMessage filter() {

		Iterator<AMessage> iter = queue.iterator();

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
				synchronized (queue) {
					msg = queue.take();

					if (isAcceptable(msg)) {
						return msg;
					} else
						queue.wait();
				}
			} catch (InterruptedException e) {
				throw new IllegalStateException(e);
			}
		}
	}

	/**
	 * ret true si on a un lmsg que l'on peut traietr, false sinon
	 * 
	 * @return
	 */
	boolean pendingMessages() {

		Iterator<AMessage> iter = queue.iterator();

		while (iter.hasNext()) {
			AMessage msg = iter.next();

			if (isAcceptable(msg))
				return true;
		}

		AMessage msg = network.receive(false);
		if (msg != null) {
			queue.add(msg);
			return true;
		} else
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

	void process(MessageDisconnect msg) {
	}

	void process(MessageDataRange msg) {
	}

	void process(MessageEndRange msg) {
	}

	void process(MessageGet msg) {
		if (range.inRange(msg.getKey())) {
			network.sendTo(msg.getOriginalSource(),
					new MessageReturnGet(node.getId(), range.get(msg.getKey())));
		} else
			network.sendInChannel(node.getNext(), msg);
	}

	void process(MessageReturnGet msg) {
		node.setReturnGet(msg.getData());
	}

	void process(MessagePing msg) {

		if (msg.getOriginalSource().equals(node.getId())
				&& msg.getSource().equals(node.getId()) == false) {
		} else {
			network.sendInChannel(node.getNext(), msg);
		}
	}

	void process(MessagePut msg) {
		if (range.inRange(msg.getKey()) == false) {
			network.sendInChannel(node.getNext(), msg);
		} else {
			System.out.println(node.getId() + "route put vers "
					+ node.getNext());
			range.add(msg.getKey(), msg.getData());
		}
	}

	void process(MessageLeave msg) {
	}
}
