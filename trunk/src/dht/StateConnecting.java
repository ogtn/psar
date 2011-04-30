package dht;

import java.util.concurrent.BlockingQueue;

import dht.message.AMessage;
import dht.message.MessageAskConnection;
import dht.message.MessageConnect;
import dht.message.MessageConnectTo;

/**
 * Etat du noeud qui est entrain de rejoindre l'anneau.
 */
public class StateConnecting extends ANodeState {

	StateConnecting(INetwork inetwork, BlockingQueue<AMessage> queue,
			Node node, Range range) {
		super(inetwork, queue, node, range);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void init() {
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
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void process(MessageConnectTo msg) {
		
		/*
		 * Je suis en attente de connexion à l'anneau et viens de recvoir un
		 * message me disant a qui connecter
		 */

		// TODO reflechir pourquoi on traite le message ici que ds l etat
		// suivant choisir entre les deux cas

		node.setNext(msg.getConnectNodeId());
		inetwork.openChannel(node.getNext());
		inetwork.sendInChannel(node.getNext(), new MessageConnect(node.getId()));
		node.setPrevious(msg.getSource());

		node.setState(new StateConnectedWaitRange(inetwork, queue, node, range));
	}
}
