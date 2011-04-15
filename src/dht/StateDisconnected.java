package dht;

import java.util.concurrent.BlockingQueue;

import dht.message.AMessage;
import dht.message.MessageAskConnection;

public class StateDisconnected extends ANodeState {

	StateDisconnected(INetwork inetwork, BlockingQueue<AMessage> queue,
			Node node, Range range) {
		super(inetwork, queue, node, range);
	}

	@Override
	void run() {
		// Je suis le premier noeud
		if (node.getNext().equals(node.getId())) {
			// Etablissement d'un connexion bouclante vers moi même
			inetwork.openChannel(node.getNext());
			node.setPrevious(node.getId());
			node.setState(new StateConnected(inetwork, queue, node, range));
		} else {
			// Envoi d'un message de demande de connexion
			inetwork.sendTo(node.getNext(),
					new MessageAskConnection(node.getId()));
			node.setState(new StateConnecting(inetwork, queue, node, range));
		}
	}

}
