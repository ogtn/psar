package dht;

import java.util.concurrent.BlockingQueue;

import dht.message.AMessage;
import dht.message.MessageDataRange;
import dht.message.MessageEndRange;

public class StateConnectedWaitRange extends ANodeState {

	StateConnectedWaitRange(INetwork inetwork, BlockingQueue<AMessage> queue,
			Node node, Range range) {
		super(inetwork, queue, node, range);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void process(MessageDataRange msg) {
		range.setEnd(msg.getEndRange());
		range.insertExtend(msg.getKey(), msg.getData());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void process(MessageEndRange msg) {
		
		range.setEnd(msg.getEnd());
		// On ne peut pas faire le setBegin à la reception de la 1ere donnée
		// sinon on a un range du début jusqu'a la donnée le plus élevée.
		// Alorq que les données sont potentiellement dans le noeud précédent
		// qui ne nous les a pas encore envoyé.
		range.setBegin(node.getId().getNumericID());
		node.setState(new StateConnected(inetwork, queue, node, range));
	}
}