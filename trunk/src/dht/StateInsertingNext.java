package dht;

import java.util.Queue;

import dht.Range.Data;
import dht.message.AMessage;
import dht.message.MessageAskConnection;
import dht.message.MessageConnectTo;
import dht.message.MessageDataRange;
import dht.message.MessageDisconnect;
import dht.message.MessageEndRange;
import dht.message.MessageGet;
import dht.message.MessagePing;
import dht.message.MessagePut;
import dht.message.MessageReturnGet;

public class StateInsertingNext extends ANodeState {

	private final MessageAskConnection msg;
	private UInt oldEndRange;

	StateInsertingNext(INetwork network, Queue<AMessage> queue, Node node,
			Range range, MessageAskConnection msg) {
		super(network, queue, node, range);
		this.msg = msg;
	}

	@Override
	boolean isAcceptable(AMessage msg) {
		/*
		 * OPTIM On pourrait envisager de faire transiter les messages
		 * AskConnection qui ne nous concernent pas. Cela posse le problème de
		 * la duplication de code.
		 */
		return msg instanceof MessageGet || msg instanceof MessagePut
				|| msg instanceof MessagePing
				|| msg instanceof MessageDisconnect
				|| msg instanceof MessageReturnGet;
	}

	@Override
	void init() {

		// Déconnexion du suivant
		network.sendInChannel(node.getNext(),
				new MessageDisconnect(node.getId()));
		network.closeChannel(node.getNext());

		// Etablissement de la connexion vers le nouveau suivant
		network.openChannel(msg.getOriginalSource());

		// Envoi de l'ancien suivant à mon nouveau suivant pour reconnexion
		network.sendInChannel(msg.getOriginalSource(), new MessageConnectTo(
				node.getId(), node.getNext()));

		node.setNext(msg.getOriginalSource());

		oldEndRange = range.getEnd();

		// Commence le transfert des données
		dataTransfer();
	}

	/**
	 * Transfert au moins une donnée vers le nouveau suivant tant qu'un message
	 * traitable n'est pas reçu.
	 */
	private void dataTransfer() {

		// Première donnée à transférer
		Data data = range.shrinkToLast(node.getNext().getNumericID());

		while (data != null) {
			network.sendInChannel(
					node.getNext(),
					new MessageDataRange(node.getId(), data.getKey(), data
							.getData(), oldEndRange));

			// Si j'ai reçu dans ma file un message que je peux traiter
			if (pendingMessages()) {
				// Je vais le traiter via un process
				return;
			}
			data = range.shrinkToLast(node.getNext().getNumericID());
		}

		// MAJ de la nouvelle plage
		range.shrinkEnd(node.getNext().getNumericID());

		// Envoi du reste de la plage au suivant
		network.sendInChannel(node.getNext(), new MessageEndRange(node.getId(),
				oldEndRange));

		node.setState(new StateConnected(network, queue, node, range));
	}

	@Override
	void process(MessageGet msg) {
		super.process(msg);
		dataTransfer();
	}

	@Override
	void process(MessagePut msg) {
		super.process(msg);
		dataTransfer();
	}

	@Override
	void process(MessagePing msg) {
		super.process(msg);
		dataTransfer();
	}

	@Override
	void process(MessageDisconnect msg) {
		super.process(msg);
		dataTransfer();
	}

	@Override
	void process(MessageReturnGet msg) {
		super.process(msg);
		dataTransfer();
	}
}
