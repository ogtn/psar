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

	StateInsertingNext(INetwork inetwork, BlockingQueue<AMessage> queue,
			Node node, Range range, MessageAskConnection msg) {
		super(inetwork, queue, node, range);
		this.msg = msg;
	}

	@Override
	void run() {

		try {
			inetwork.sendInChannel(node.getNext(),
					new MessageDisconnect(node.getId()));
			inetwork.closeChannel(node.getNext());

			// Etablissement de la connection
			inetwork.openChannel(msg.getOriginalSource());

			// Envoi du suivant de la connection
			inetwork.sendInChannel(msg.getOriginalSource(),
					new MessageConnectTo(node.getId(), node.getNext()));

			UInt endOfRange = range.getEnd();

			// TODO alternatif Transfert DATA
			Data data = range.shrinkToLast(msg.getOriginalSource());
			while (data != null) {
				System.out.println(node.getId() + " envoi data "
						+ data.getKey() + " à " + msg.getOriginalSource());
				inetwork.sendInChannel(
						msg.getOriginalSource(),
						new MessageDataRange(node.getId(), data.getKey(), data
								.getData(), endOfRange));
				data = range.shrinkToLast(msg.getOriginalSource());

				if (queue.isEmpty() == false) {
					AMessage msg;
					msg = queue.take();
					
					if (msg instanceof MessagePing) {
						process((MessagePing) msg);
					} else if (msg instanceof MessagePut) {
						process((MessagePut) msg);
					} else if (msg instanceof MessageGet) {
						process((MessageGet) msg);
					} else
						System.err.println("Kernel panic dans "
								+ this.getClass().getName() + " pr msg : '"
								+ msg + "' node : [" + node + "]");
				}
			}

			// Calcul de la nouvelle plage
			range.shrinkEnd(msg.getOriginalSource());

			// Envoi de la plage
			inetwork.sendInChannel(msg.getOriginalSource(),
					new MessageEndRange(node.getId(), endOfRange));

			node.setNext(msg.getOriginalSource());

			node.setState(new StateConnected(inetwork, queue, node, range));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	void process(MessageGet msg) {
		if (range.inRange(msg.getKey())) {
			Object tmpData = range.get(msg.getKey());

			if (tmpData == null)
				System.out.println("Fail : " + msg.getKey());
			else
				System.out.println("Ok : " + tmpData + " id: " + node.getId());
		} else
			inetwork.sendInChannel(node.getNext(), msg);
	}

	@Override
	void process(MessagePut msg) {
		if (range.inRange(msg.getKey()) == false) {
			inetwork.sendInChannel(node.getNext(), msg);
		} else
			range.add(msg.getKey(), msg.getData());
	}
}
