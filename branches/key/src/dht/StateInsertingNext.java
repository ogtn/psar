package dht;

import java.util.concurrent.BlockingQueue;

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

public class StateInsertingNext extends ANodeState {

	private final MessageAskConnection msg;
	private UInt oldEndRange;

	StateInsertingNext(INetwork inetwork, BlockingQueue<AMessage> queue,
			Node node, Range range, MessageAskConnection msg) {
		super(inetwork, queue, node, range);
		this.msg = msg;
	}

	@Override
	void init() {

		// Déconnexion du suivant
		inetwork.sendInChannel(node.getNext(), new MessageDisconnect(node
				.getId()));
		inetwork.closeChannel(node.getNext());

		// Etablissement de la connection vers le nouveau suivant
		inetwork.openChannel(msg.getOriginalSource());

		// Envoi de l'ancien suivant à mon nouveau suivant pour reconnexion
		inetwork.sendInChannel(msg.getOriginalSource(), new MessageConnectTo(
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
		Data data = range.shrinkToLast(node.getNext());

		while (data != null) {
			inetwork.sendInChannel(node.getNext(), new MessageDataRange(node
					.getId(), data.getKey(), data.getData(), oldEndRange));

			// TODO filter is empty non bloquant
			// Si j'ai reçu dans ma file un message que je peux traiter
			if (queue.isEmpty() == false) {

				System.out.println("Inserting next queue.isEmpty() "
						+ queue.isEmpty());
				System.out.println("Inserting next queue.size() "
						+ queue.size());
				System.out.println("Msg : ");
				for (AMessage msg : queue) {
					System.out.print(msg + " ");
				}
				System.out.println("");

				// Je vais le traiter via un process
				return;
			}
			data = range.shrinkToLast(node.getNext());
		}

		// MAJ de la nouvelle plage
		range.shrinkEnd(node.getNext());

		// Envoi du reste de la plage au suivant
		inetwork.sendInChannel(node.getNext(), new MessageEndRange(
				node.getId(), oldEndRange));

		System.out.println("Inserting next change d'état");

		node.setState(new StateConnected(inetwork, queue, node, range));
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

	// TODO vérifier que l'on appelle dataTransfer(); sur toutes les methodes
	// non filtrées
	@Override
	void process(MessageDisconnect msg) {
		super.process(msg);
		dataTransfer();
	}
}
