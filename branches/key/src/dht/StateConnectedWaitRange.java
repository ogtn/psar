package dht;

import java.util.concurrent.BlockingQueue;

import dht.message.AMessage;
import dht.message.MessageDataRange;
import dht.message.MessageEndRange;
import dht.message.MessageGet;
import dht.message.MessagePing;
import dht.message.MessagePut;

public class StateConnectedWaitRange extends ANodeState {

	private boolean quit = false;

	StateConnectedWaitRange(INetwork inetwork, BlockingQueue<AMessage> queue,
			Node node, Range range) {
		super(inetwork, queue, node, range);
	}

	@Override
	void run() {
		try {
			while (!quit) {
				AMessage msg;
				msg = queue.take();

				if (msg instanceof MessageDataRange) {
					process((MessageDataRange) msg);
				} else if (msg instanceof MessageEndRange) {
					process((MessageEndRange) msg);
				} else if (msg instanceof MessagePing) {
					process((MessagePing) msg);
				} else if (msg instanceof MessagePut) {
					process((MessagePut) msg);
				} else if (msg instanceof MessageGet) {
					process((MessageGet) msg);
				} /*else
					System.err.println("[" + node + "] Kernel panic! "
							+ this.getClass().getName());*/
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	void process(MessageDataRange msg) {
		range.setEnd(msg.getEndRange());
		range.insertExtend(msg.getKey(), msg.getData());
	}

	void process(MessageEndRange msg) {
		range.setEnd(msg.getEnd());
		range.setBegin(node.getId());
		node.setState(new StateConnected(inetwork, queue, node, range));
		quit = true;
	}

	@Override
	void process(MessagePut msg) {
		if (range.inRange(msg.getKey()) == false) {
			inetwork.sendInChannel(node.getNext(), msg);
		} else
			range.add(msg.getKey(), msg.getData());
	}

	@Override
	void process(MessageGet msg) {
		if (range.inRange(msg.getKey())) {
			Object tmpData = range.get(msg.getKey());

			/*if (tmpData == null)
				System.out.println("Fail : " + msg.getKey());
			else
				System.out.println("Ok : " + tmpData + " id: " + node.getId());*/
		} else
			inetwork.sendInChannel(node.getNext(), msg);
	}
}
