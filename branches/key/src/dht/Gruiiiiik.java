package dht;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.BlockingQueue;

import dht.Range.Data;
import dht.message.AMessage;
import dht.message.MessageAskConnection;
import dht.message.MessageBeginRange;
import dht.message.MessageConnect;
import dht.message.MessageConnectTo;
import dht.message.MessageData;
import dht.message.MessageDataRange;
import dht.message.MessageDisconnect;
import dht.message.MessageEndRange;
import dht.message.MessageGet;
import dht.message.MessagePing;
import dht.message.MessagePut;

public class Gruiiiiik extends ANodeState {
	boolean connecting;
	
	/*
	Gruiiiiik(INetwork inetwork, BlockingQueue<AMessage> queue,
			Node node, Range range) {
		super(inetwork, queue, node, range);
		connecting = false;
	}*/

	@Override
	void run() {
		
		try {
			while (true) {
				
				// /!\ GAFFE A CE TRUC DE MERDE
				if (node.getNext().equals(node.getId())) {

				} else {
					connecting = true;
				}
				
				AMessage msg = queue.take();

				if (msg instanceof MessageAskConnection) {
					process((MessageAskConnection) msg);
				} else if (msg instanceof MessageConnect) {
					process((MessageConnect) msg);
				} else if (msg instanceof MessageConnectTo) {
					process((MessageConnectTo) msg);
				} else if (msg instanceof MessagePing) {
					process((MessagePing) msg);
				} else if (msg instanceof MessageEndRange) {
					process((MessageEndRange) msg);
				} else if (msg instanceof MessagePut) {
					process((MessagePut) msg);
				} else if (msg instanceof MessageDataRange) {
					process((MessageDataRange) msg);
				} else if (msg instanceof MessageGet) {
					process((MessageGet) msg);
				} else if (msg instanceof MessageBeginRange) {
					process((MessageBeginRange) msg);
				} else if (msg instanceof MessageData) {
					process((MessageData) msg);
				} else
					System.err.println("[" + node.getId() + "] Kernel panic! ");
			}

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	void process(MessageData msg) {
		range.insertExtend(msg.getKey(), msg.getData());
		System.out.println(node.getId() + ": ajout de la donnée "
				+ msg.getData() + " " + range);
	}

	@Override
	void process(MessageBeginRange msg) {
		range.setBegin(msg.getBegin());
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
	void process(MessageDataRange msg) {
		System.out.println("DataRange" + node.getId() + ": " + range);
		range.setEnd(msg.getEndRange());
		range.insertExtend(msg.getKey(), msg.getData());
		System.out.println("#DataRange" + node.getId() + ": " + range);
	}

	@Override
	void process(MessageConnectTo msg) {

		/* Je suis en attente de connection a l'anneau */
		if (connecting) {
			connecting = false;
			node.setNext(msg.getConnectNodeId());
			inetwork.openChannel(node.getNext());
			inetwork.sendInChannel(node.getNext(),
					new MessageConnect(node.getId()));
		}
		/* Mon précédent se déconnecte */
		else {
			inetwork.sendInChannel(node.getNext(),
					new MessageDisconnect(node.getId()));
			inetwork.closeChannel(node.getNext());
			node.setNext(msg.getConnectNodeId());
			inetwork.openChannel(node.getNext());
			inetwork.sendInChannel(node.getNext(),
					new MessageConnect(node.getId()));
		}
	}

	@Override
	void process(MessageConnect msg) {

		if (msg.getSource() != msg.getOriginalSource())
			throw new IllegalStateException("Oh shit §§§§§§§§§ FUUUUUUUUUUUUUU");

		node.setPrevious(msg.getSource());
	}

	@Override
	void process(MessageEndRange msg) {
		range.setEnd(msg.getEnd());
		range.setBegin(node.getId());
	}

	@Override
	void process(MessageAskConnection msg) {

		if (range.inRange(msg.getOriginalSource())) {

			inetwork.sendInChannel(node.getNext(),
					new MessageDisconnect(node.getId()));
			inetwork.closeChannel(node.getNext());

			// Etablissement de la connection
			inetwork.openChannel(msg.getOriginalSource());
			inetwork.sendInChannel(msg.getOriginalSource(), new MessageConnect(
					node.getId()));

			// Envoi du suivant de la connection
			inetwork.sendInChannel(msg.getOriginalSource(),
					new MessageConnectTo(node.getId(), node.getNext()));

			UInt endOfRange = range.getEnd();

			// Transfert DATA
			Data data = range.shrinkToLast(msg.getOriginalSource());
			while (data != null) {
				System.out.println(node.getId() + " envoi data "
						+ data.getKey() + " à " + msg.getOriginalSource());
				inetwork.sendInChannel(
						msg.getOriginalSource(),
						new MessageDataRange(node.getId(), data.getKey(), data
								.getData(), endOfRange));
				data = range.shrinkToLast(msg.getOriginalSource());
			}

			// Calcul de la nouvelle plage
			range.shrinkEnd(msg.getOriginalSource());

			// Envoi de la plage
			inetwork.sendInChannel(msg.getOriginalSource(),
					new MessageEndRange(node.getId(), endOfRange));
			System.out.println(node.getId() + " envoi fin plage" + endOfRange
					+ " à " + msg.getOriginalSource());

			node.setNext(msg.getOriginalSource());
		} else {
			inetwork.sendInChannel(node.getNext(), msg);
		}
	}

	@Override
	void process(MessagePut msg) {
		if (range.inRange(msg.getKey()) == false) {
			System.out.println("Route MessagePut Data : " + msg.getKey());
			inetwork.sendInChannel(node.getNext(), msg);
		} else
			range.add(msg.getKey(), msg.getData());
	}

	@Override
	void process(MessagePing msg) {

		if (msg.getOriginalSource().equals(node.getId()) == false) {
			Map<UInt, Object> data = range.getData();
			Iterator<Entry<UInt, Object>> iter = data.entrySet().iterator();
			String out = "";

			while (iter.hasNext()) {
				Entry<UInt, Object> entry = iter.next();
				out += "{" + entry.getKey() + " : " + entry.getValue() + "}";
			}

			System.out.println("PING : " + node);
			inetwork.sendInChannel(node.getNext(), msg);
		}
	}
}
