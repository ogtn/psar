package dht;

import java.util.concurrent.BlockingQueue;

import dht.message.AMessage;
import dht.message.MessageData;

public class StatePreviousDisconnecting extends ANodeState {

	StatePreviousDisconnecting(INetwork inetwork,
			BlockingQueue<AMessage> queue, Node node, Range range, MessageData msg) {
		super(inetwork, queue, node, range);
		// TODO Auto-generated constructor stub
	}

	@Override
	void run() {
		// TODO Auto-generated method stub

	}

}
