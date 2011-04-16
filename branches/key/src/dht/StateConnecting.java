package dht;

import java.util.concurrent.BlockingQueue;

import dht.message.AMessage;
import dht.message.MessageConnect;
import dht.message.MessageConnectTo;

/**
 * Etat du noeud qui est entrain de rejoindre l'anneau.
 */
public class StateConnecting extends ANodeState {

	private boolean quit = false;
	
	StateConnecting(INetwork inetwork, BlockingQueue<AMessage> queue,
			Node node, Range range) {
		super(inetwork, queue, node, range);
	}

	@Override
	void run() {
		try {
			while (!quit) {
				AMessage msg;
				msg = queue.take();
				if (msg instanceof MessageConnectTo) {
					process((MessageConnectTo) msg);
				} /*else
					System.err.println("Kernel panic dans "
							+ this.getClass().getName() + " pr msg : '" + msg
							+ "' node : [" + node + "]");*/
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	void process(MessageConnectTo msg) {

		// TODO reflechir pourquoi on traite le message ici que suivant
		// choisir entre les deux cas

		/* Je suis en attente de connexion à l'anneau */
		node.setNext(msg.getConnectNodeId());
		inetwork.openChannel(node.getNext());
		inetwork.sendInChannel(node.getNext(), new MessageConnect(node.getId()));
		node.setPrevious(msg.getSource());
		
		node.setState(new StateConnectedWaitRange(inetwork, queue, node, range));
		quit = true;
	}
}
