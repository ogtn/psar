package dht;

import java.util.concurrent.BlockingQueue;

import dht.message.AMessage;
import dht.message.MessageAskConnection;
import dht.message.MessageBeginRange;
import dht.message.MessageConnect;
import dht.message.MessageConnectTo;
import dht.message.MessageData;
import dht.message.MessageDisconnect;
import dht.message.MessageLeave;

public class StateConnected extends ANodeState {

	StateConnected(INetwork inetwork, BlockingQueue<AMessage> queue, Node node,
			Range range) {
		super(inetwork, queue, node, range);
	}

	@Override
	void process(MessageAskConnection msg) {
		if (range.inRange(msg.getOriginalSource())) {
			node.setState(new StateInsertingNext(inetwork, queue, node, range,
					msg));
		} else {
			inetwork.sendInChannel(node.getNext(), msg);
		}
	}

	/**
	 * On reçoit un message data qui nous indique que le noeud précédent se
	 * déconnecte et nous envoie sa première donnée.
	 */
	@Override
	void process(MessageData msg) {
		node.setState(new StatePreviousDisconnecting(inetwork, queue, node,
				range, msg));
	}

	/**
	 * Notre précédent se déconnecte et n'a pas de donnée à nous envoyer. On
	 * reçoit un message MessageBeginRange pour passer en déconnexion de noeud.
	 */
	@Override
	void process(MessageBeginRange msg) {
		node.setState(new StatePreviousDisconnecting(inetwork, queue, node,
				range, msg));
	}

	/**
	 * Notre précédent s'est déconnecté et le nouveau se connecte.
	 */
	@Override
	void process(MessageConnect msg) {
		node.setPrevious(msg.getSource());
	}

	/**
	 * Mon successeur se déconnecte et me demande de me reconnecter à son
	 * suivant.
	 */
	@Override
	void process(MessageConnectTo msg) {

		// TODO : Créer un état DiconnectingNext ???

		inetwork.sendInChannel(node.getNext(),
				new MessageDisconnect(node.getId()));
		inetwork.closeChannel(node.getNext());
		node.setNext(msg.getConnectNodeId());
		inetwork.openChannel(node.getNext());
		inetwork.sendInChannel(node.getNext(), new MessageConnect(node.getId()));
	}
	
	
	@Override
	public void process(MessageLeave msg) {
		node.setState(new StateDisconnecting(inetwork, queue, node, range));
	}
}