package dht;

import java.util.Queue;

import dht.message.AMessage;

public class StateDisconnected extends ANodeState {

	StateDisconnected(INetwork network, Queue<AMessage> queue,
			Node node, Range range) {
		super(network, queue, node, range);
	}

	@Override
	boolean isAcceptable(AMessage msg) {
		return false;
	}
}
