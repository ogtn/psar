package dht;

import java.util.concurrent.BlockingQueue;

import dht.message.AMessage;
import dht.message.MessageConnect;
import dht.message.MessageConnectTo;
import dht.message.MessageDataRange;
import dht.message.MessageEndRange;
import dht.message.MessageGet;
import dht.message.MessagePing;
import dht.message.MessagePut;
import dht.message.MessageReturnGet;

public class StateConnectedWaitRange extends ANodeState {

	private MessageConnectTo msg;
	
	StateConnectedWaitRange(INetwork network, BlockingQueue<AMessage> queue, Node node,
			Range range, MessageConnectTo msg) {
		super(network, queue, node, range);
		this.msg = msg;
	}

	@Override
	boolean isAcceptable(AMessage msg) {
		return msg instanceof MessageGet || msg instanceof MessagePut
				|| msg instanceof MessagePing
				|| msg instanceof MessageDataRange
				|| msg instanceof MessageEndRange
				|| msg instanceof MessageReturnGet;
	}

	@Override
	void init() {
		process(msg);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	void process(MessageConnectTo msg) {
		node.setNext(msg.getConnectNodeId());
		network.openChannel(node.getNext());
		network.sendInChannel(node.getNext(), new MessageConnect(node.getId()));
		node.setPrevious(msg.getSource());
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
		node.setState(new StateConnected(network, queue, node, range));
	}
}