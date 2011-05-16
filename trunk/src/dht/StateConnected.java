package dht;

import java.util.Queue;
import java.util.concurrent.BlockingQueue;

import dht.message.AMessage;
import dht.message.MessageAskConnection;
import dht.message.MessageBeginRange;
import dht.message.MessageConnect;
import dht.message.MessageConnectTo;
import dht.message.MessageData;
import dht.message.MessageDataRange;
import dht.message.MessageDisconnect;
import dht.message.MessageEndRange;
import dht.message.MessageEventConnect;
import dht.message.MessageEventDisconnect;
import dht.message.MessageLeave;

public class StateConnected extends ANodeState {

	StateConnected(INetwork network, BlockingQueue<AMessage> queue, Node node,
			Range range, Queue<AMessage> buffer) {
		super(network, queue, node, range, buffer);
	}

	@Override
	boolean isAcceptable(AMessage msg) {
		// TODO change
		return !(msg instanceof MessageConnect)
				&& !(msg instanceof MessageDataRange)
				&& !(msg instanceof MessageDisconnect)
				&& !(msg instanceof MessageEndRange);
	}

	@Override
	void process(MessageAskConnection msg) {
		if (range.inRange(msg.getOriginalSource().getNumericID())) {
			node.setState(new StateInsertingNext(network, queue, node, range,
					msg, buffer));
		} else {
			network.sendInChannel(node.getNext(), msg);
		}
	}

	/**
	 * On reçoit un message data qui nous indique que le noeud précédent se
	 * déconnecte et nous envoie sa première donnée.
	 */
	@Override
	void process(MessageData msg) {
		node.setState(new StatePreviousDisconnecting(network, queue, node,
				range, msg, buffer));
	}

	/**
	 * Notre précédent se déconnecte et n'a pas de donnée à nous envoyer. On
	 * reçoit un message MessageBeginRange pour passer en déconnexion de noeud.
	 */
	@Override
	void process(MessageBeginRange msg) {
		node.setState(new StatePreviousDisconnecting(network, queue, node,
				range, msg, buffer));
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
		network.sendInChannel(node.getNext(),
				new MessageDisconnect(node.getId()));
		network.closeChannel(node.getNext());
		node.setNext(msg.getConnectNodeId());
		network.openChannel(node.getNext());
		network.sendInChannel(node.getNext(), new MessageConnect(node.getId()));
	}

	@Override
	public void process(MessageLeave msg) {
		node.setState(new StateDisconnecting(network, queue, node, range, buffer));
	}

	// TODO utiliser ailleurs ??? Ne pas filitrer ailleurs?
	@Override
	void process(MessageEventConnect msg) {

		if (msg.getOriginalSource().equals(node.getId())) {
			node.setNextShortcut(msg.getShortcut());
		} else {
			msg.incTtl();

			// Je suis suivant du suivant du noeud qui s'insere
			if (msg.getTtl() == 2) {
				// Je suis alors son raccourci
				msg.setShortcut(node.getId());
			}

			// Je suis le précédent du noeud qui s'insere
			if (node.getNext().equals(msg.getOriginalSource())) {
				// Je récupère mon nouveau raccourci
				node.setNextShortcut(msg.getNext());
			}

			// Mon suivant est égal au précédent du noeud se connectant
			if (node.getNext().equals(msg.getPrevious())) {
				// Je pointe sur le nouveau noeud
				node.setNextShortcut(msg.getOriginalSource());
			}

			// TODO detecter ac le ttl qu'on ne peut pas remplir les shortcuts quand l'anneau est trop petit
			network.sendInChannel(node.getNext(), msg);
		}
	}
}