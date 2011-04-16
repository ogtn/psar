package dht;

import java.util.concurrent.BlockingQueue;

import dht.message.AMessage;
import dht.message.MessageAskConnection;
import dht.message.MessageBeginRange;
import dht.message.MessageConnect;
import dht.message.MessageConnectTo;
import dht.message.MessageData;
import dht.message.MessageDisconnect;
import dht.message.MessageGet;
import dht.message.MessagePing;
import dht.message.MessagePut;

public class StateConnected extends ANodeState {

	private boolean quit = false;

	StateConnected(INetwork inetwork, BlockingQueue<AMessage> queue, Node node,
			Range range) {
		super(inetwork, queue, node, range);
	}

	@Override
	void run() {
		try {
			while (!quit) {
				AMessage msg;
				msg = queue.take();

				if (msg instanceof MessageAskConnection) {
					process((MessageAskConnection) msg);
				} else if (msg instanceof MessageData) {
					process((MessageData) msg);
				} else if (msg instanceof MessagePing) {
					process((MessagePing) msg);
				} else if (msg instanceof MessageConnect) {
					process((MessageConnect) msg);
				} else if (msg instanceof MessagePut) {
					process((MessagePut) msg);
				} else if (msg instanceof MessageGet) {
					process((MessageGet) msg);
				} else if (msg instanceof MessageBeginRange) {
					process((MessageBeginRange) msg);
				} else if (msg instanceof MessageConnectTo) {
					process((MessageConnectTo) msg);
				} /*else
					System.err.println("Kernel panic dans "
							+ this.getClass().getName() + " pr msg : '" + msg
							+ "' node : [" + node + "]");*/
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	void process(MessageAskConnection msg) {
		if (range.inRange(msg.getOriginalSource())) {
			node.setState(new StateInsertingNext(inetwork, queue, node, range,
					msg));
			quit = true;
		} else {
			inetwork.sendInChannel(node.getNext(), msg);
		}
	}

	void process(MessageData msg) {
		node.setState(new StatePreviousDisconnecting(inetwork, queue, node,
				range, msg));
		quit = true;
	}

	@Override
	void process(MessageBeginRange msg) {
		node.setState(new StatePreviousDisconnecting(inetwork, queue, node,
				range, msg));
		quit = true;
	}

	@Override
	void process(MessageConnect msg) {
		node.setPrevious(msg.getSource());
	}

	@Override
	void process(MessageConnectTo msg) {
		/* Mon précédent se déconnecte */

		inetwork.sendInChannel(node.getNext(),
				new MessageDisconnect(node.getId()));
		inetwork.closeChannel(node.getNext());
		node.setNext(msg.getConnectNodeId());
		inetwork.openChannel(node.getNext());
		inetwork.sendInChannel(node.getNext(), new MessageConnect(node.getId()));
	}
}