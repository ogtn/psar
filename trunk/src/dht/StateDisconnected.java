package dht;

import java.util.Queue;
import java.util.concurrent.BlockingQueue;

import dht.message.AMessage;

public class StateDisconnected extends ANodeState {

	StateDisconnected(INetwork network, BlockingQueue<AMessage> queue,
			Node node, Range range, Queue<AMessage> buffer) {
		super(network, queue, node, range, buffer);
	}

	@Override
	boolean isAcceptable(AMessage msg) {
		return false;
	}
}
