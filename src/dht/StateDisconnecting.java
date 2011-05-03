package dht;

import java.util.Queue;

import dht.Range.Data;
import dht.message.AMessage;
import dht.message.MessageBeginRange;
import dht.message.MessageConnectTo;
import dht.message.MessageData;
import dht.message.MessageDisconnect;
import dht.message.MessageGet;
import dht.message.MessagePing;
import dht.message.MessagePut;

public class StateDisconnecting extends ANodeState {

	private UInt oldBeginRange;
	// true si on a transféré toutes nos données, si on a fermé notre connexion
	// et que l'on vide les messages pendants dans la file, false sinon.
	private boolean dataTransfered;

	StateDisconnecting(INetwork network, Queue<AMessage> queue, Node node,
			Range range) {
		super(network, queue, node, range);
	}

	@Override
	boolean isAcceptable(AMessage msg) {
		return msg instanceof MessageDisconnect
				|| msg instanceof MessagePut
				|| msg instanceof MessageGet
				|| msg instanceof MessagePing;
	}

	@Override
	void init() {
		// Sauvegarde de l'ancien début de plage car lorsque l'on réduit notre
		// plage via des "shrinkToLast" si on a une donnée au début de notre
		// plage on perd notre ainsi que le début
		oldBeginRange = range.getBegin();

		dataTransfered = false;
		dataTransfer();
	}

	private void dataTransfer() {

		if (dataTransfered)
			return;

		Data data = range.shrinkToLast(node.getId().getNumericID());

		while (data != null) {
			network.sendInChannel(node.getNext(), new MessageData(node.getId(),
					data.getKey(), data.getData()));
			
			// Si j'ai reçu dans ma file un message que je peux traiter
			if (pendingMessages()) {
				// Je vais le traiter via un process
				return;
			}
		}

		// Envoi du reste de la plage
		network.sendInChannel(node.getNext(),
				new MessageBeginRange(node.getId(), oldBeginRange));

		// Demande à notre précédent de se connecter à notre suivant
		network.sendTo(node.getPrevious(), new MessageConnectTo(node.getId(),
				node.getNext()));

		// Fermeture vers le suivant
		network.sendInChannel(node.getNext(),
				new MessageDisconnect(node.getId()));
		network.closeChannel(node.getNext());

		dataTransfered = true;
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
		// TODO vider la file des messages que l'on ne peut traiter
		node.setState(new StateDisconnected(network, queue, node, range));
	}
}
