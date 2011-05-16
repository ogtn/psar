package dht;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;

import dht.message.AMessage;
import dht.message.MessageBeginRange;
import dht.message.MessageConnect;
import dht.message.MessageData;
import dht.message.MessageEventDisconnect;
import dht.message.MessageGet;
import dht.message.MessagePing;
import dht.message.MessagePut;
import dht.message.MessageReturnGet;

public class StatePreviousDisconnecting extends ANodeState {

	// Constructeur appellé qd le prévious doit de nous tranferer les datas
	StatePreviousDisconnecting(INetwork network, BlockingQueue<AMessage> queue,
			Node node, Range range, final MessageData msg, Queue<AMessage> buffer) {
		super(network, queue, node, range, buffer);
		process(msg);
	}

	// Constructeur appellé qd le prévious doit nous tranferer sa plage mais n'a
	// pas de datas
	StatePreviousDisconnecting(INetwork network, BlockingQueue<AMessage> queue,
			Node node, Range range, final MessageBeginRange msg, Queue<AMessage> buffer) {
		super(network, queue, node, range, buffer);
		process(msg);
	}

	@Override
	boolean isAcceptable(AMessage msg) {
		return msg instanceof MessageGet || msg instanceof MessagePut
				|| msg instanceof MessagePing || msg instanceof MessageData
				|| msg instanceof MessageBeginRange
				|| msg instanceof MessageConnect
				|| msg instanceof MessageReturnGet
				|| msg instanceof MessageEventDisconnect;
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
	}

	@Override
	void process(MessageConnect msg) {
		node.setState(new StateConnected(network, queue, node, range, buffer));
	}
}
