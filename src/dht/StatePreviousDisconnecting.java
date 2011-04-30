package dht;

import java.util.concurrent.BlockingQueue;

import dht.message.AMessage;
import dht.message.MessageBeginRange;
import dht.message.MessageData;

public class StatePreviousDisconnecting extends ANodeState {

	// Constructeur appellé qd le prévious doit de nous tranferer les datas
	StatePreviousDisconnecting(INetwork inetwork,
			BlockingQueue<AMessage> queue, Node node, Range range,
			final MessageData msg) {
		super(inetwork, queue, node, range);
		process(msg);
	}

	// Constructeur appellé qd le prévious doit nous tranferer sa plage mais n'a
	// pas de datas
	StatePreviousDisconnecting(INetwork inetwork,
			BlockingQueue<AMessage> queue, Node node, Range range,
			final MessageBeginRange msg) {
		super(inetwork, queue, node, range);
		process(msg);
	}

	@Override
	void process(MessageData msg) {
		// Reception d'une nouvelle donnée : on agrandit la plage
		range.insertExtend(msg.getKey(), msg.getData());
	}

	@Override
	void process(MessageBeginRange msg) {
		// Dernier message reçu Reception d'une nouvelle donnée : on agrandit la
		// plage
		range.setBegin(msg.getBegin());
		node.setState(new StateConnected(inetwork, queue, node, range));
	}
}
