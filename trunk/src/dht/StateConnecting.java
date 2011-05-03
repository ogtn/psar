package dht;

import java.util.Queue;

import dht.message.AMessage;
import dht.message.MessageAskConnection;
import dht.message.MessageConnectTo;

/**
 * Etat du noeud qui est entrain de rejoindre l'anneau.
 */
public class StateConnecting extends ANodeState {

	StateConnecting(INetwork inetwork, Queue<AMessage> queue,
			Node node, Range range) {
		super(inetwork, queue, node, range);
	}

	@Override
	boolean isAcceptable(AMessage msg) {
		return msg instanceof MessageConnectTo;
	};

	/**
	 * {@inheritDoc}
	 */
	@Override
	void init() {
		// Je suis le premier noeud
		if (node.getNext().equals(node.getId())) {
			// Etablissement d'un connexion bouclante vers moi même
			network.openChannel(node.getNext());
			node.setPrevious(node.getId());
			node.setState(new StateConnected(network, queue, node, range));
		} else {
			// Envoi d'un message de demande de connexion
			network.sendTo(node.getNext(),
					new MessageAskConnection(node.getId()));
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void process(MessageConnectTo msg) {
		/*
		 * Je suis en attente de connexion à l'anneau et viens de recevoir un
		 * message me disant à qui me connecter
		 */

		node.setState(new StateConnectedWaitRange(network, queue, node, range, msg));
	}
}
