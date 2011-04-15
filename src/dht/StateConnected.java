package dht;

import java.util.concurrent.BlockingQueue;

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
				} else
					System.err.println("Kernel panic dans "
							+ this.getClass().getName() + " pr msg : '" + msg
							+ "' node : [" + node + "]");
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	void process(MessageAskConnection msg) {
		
		System.out.println("Ducon ; " + node.getId() + " recoit " + msg);
		
		if (range.inRange(msg.getOriginalSource())) {
			node.setState(new StateInsertingNext(inetwork, queue, node, range,
					msg));
			quit = true;
		} else {
			inetwork.sendInChannel(node.getNext(), msg);
		}
	}

	void process(MessageData msg) {
		if (range.inRange(msg.getOriginalSource())) {
			node.setState(new StatePreviousDisconnecting(inetwork, queue, node,
					range, msg));
		} else {
			inetwork.sendInChannel(node.getNext(), msg);
		}
	}
}
