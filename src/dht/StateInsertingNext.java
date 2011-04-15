package dht;

import java.util.concurrent.BlockingQueue;

import dht.Range.Data;
import dht.message.AMessage;
import dht.message.MessageAskConnection;
import dht.message.MessageConnect;
import dht.message.MessageConnectTo;
import dht.message.MessageDataRange;
import dht.message.MessageDisconnect;
import dht.message.MessageEndRange;

public class StateInsertingNext extends ANodeState {

	private final MessageAskConnection msg;

	StateInsertingNext(INetwork inetwork, BlockingQueue<AMessage> queue,
			Node node, Range range, MessageAskConnection msg) {
		super(inetwork, queue, node, range);
		this.msg = msg;
	}

	@Override
	void run() {

		inetwork.sendInChannel(node.getNext(),
				new MessageDisconnect(node.getId()));
		inetwork.closeChannel(node.getNext());

		// Etablissement de la connection
		inetwork.openChannel(msg.getOriginalSource());
		inetwork.sendInChannel(msg.getOriginalSource(),
				new MessageConnect(node.getId()));

		// Envoi du suivant de la connection
		inetwork.sendInChannel(msg.getOriginalSource(), new MessageConnectTo(
				node.getId(), node.getNext()));

		UInt endOfRange = range.getEnd();

		// TODO alternatif Transfert DATA
		Data data = range.shrinkToLast(msg.getOriginalSource());
		while (data != null) {
			System.out.println(node.getId() + " envoi data " + data.getKey()
					+ " à " + msg.getOriginalSource());
			inetwork.sendInChannel(
					msg.getOriginalSource(),
					new MessageDataRange(node.getId(), data.getKey(), data
							.getData(), endOfRange));
			data = range.shrinkToLast(msg.getOriginalSource());
		}

		// Calcul de la nouvelle plage
		range.shrinkEnd(msg.getOriginalSource());

		// Envoi de la plage
		inetwork.sendInChannel(msg.getOriginalSource(), new MessageEndRange(
				node.getId(), endOfRange));
		System.out.println(node.getId() + " envoi fin plage" + endOfRange
				+ " à " + msg.getOriginalSource());

		node.setNext(msg.getOriginalSource());

		node.setState(new StateConnected(inetwork, queue, node, range));
	}
}
