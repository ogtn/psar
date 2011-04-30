package dht;

import java.util.concurrent.BlockingQueue;

import dht.message.AMessage;

public class StateDisconnected extends ANodeState {

	StateDisconnected(INetwork inetwork, BlockingQueue<AMessage> queue,
			Node node, Range range) {
		super(inetwork, queue, node, range);
	}
}
