package dht;

import java.util.concurrent.BlockingQueue;

import dht.message.AMessage;
import dht.message.MessageBeginRange;
import dht.message.MessageData;

public class StatePreviousDisconnecting extends ANodeState {

	private boolean quit;
	private AMessage msg;

	StatePreviousDisconnecting(INetwork inetwork,
			BlockingQueue<AMessage> queue, Node node, Range range,
			final MessageData msg) {
		super(inetwork, queue, node, range);
		quit = false;
		this.msg = msg;
	}

	StatePreviousDisconnecting(INetwork inetwork,
			BlockingQueue<AMessage> queue, Node node, Range range,
			final MessageBeginRange msg) {
		super(inetwork, queue, node, range);
		quit = false;
		this.msg = msg;
	}

	@Override
	void run() {
		try {
			while (!quit) {
				if (msg instanceof MessageData) {
					process((MessageData) msg);
				} else if (msg instanceof MessageBeginRange) {
					process((MessageBeginRange) msg);
				} else
					/*System.err.println("Kernel panic dans "
							+ this.getClass().getName() + " pr msg : '" + msg
							+ "' node : [" + node + "]");*/
				if(!quit)
					msg = queue.take();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	void process(MessageData msg) {
		range.insertExtend(msg.getKey(), msg.getData());
	}

	@Override
	void process(MessageBeginRange msg) {
		range.setBegin(msg.getBegin());
		node.setState(new StateConnected(inetwork, queue, node, range));
		quit = true;
	}
}
