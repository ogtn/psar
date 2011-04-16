package dht;

import java.util.Map;
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
import dht.message.MessageError;
import dht.message.MessageGet;
import dht.message.MessagePing;
import dht.message.MessagePut;

abstract class ANodeState {

	protected INetwork inetwork;
	protected BlockingQueue<AMessage> queue;
	protected Node node;
	protected Range range;

	ANodeState(INetwork inetwork, BlockingQueue<AMessage> queue, Node node,
			Range range) {
		this.node = node;
		this.queue = queue;
		this.inetwork = inetwork;
		this.range = range;
	}

	abstract void run();
	
	

	void process(MessageAskConnection msg) {
	}

	void process(MessageBeginRange msg) {
	}

	void process(MessageConnect msg) {
	}

	void process(MessageConnectTo msg) {
	}

	void process(MessageData msg) {
	}

	void process(MessageDisconnect msg) {
	}

	void process(MessageDataRange msg) {
	}

	void process(MessageEndRange msg) {
	}

	void process(MessageError msg) {
	}

	void process(MessageGet msg) {
		if (range.inRange(msg.getKey())) {

			Object tmpData = range.get(msg.getKey());

			if (tmpData == null)
				System.out.println("Fail : " + msg.getKey());
			else
				System.out.println("Ok : " + tmpData + " id: " + node.getId());
		} else {
			System.out.println(node.getId() + "route get vers "
					+ node.getNext());
			inetwork.sendInChannel(node.getNext(), msg);
		}
	}

	void process(MessagePing msg) {
		if (msg.getOriginalSource().equals(node.getId()) == false) {
			Map<UInt, Object> data = range.getData();
			/*
			 * Iterator<Entry<UInt, Object>> iter = data.entrySet().iterator();
			 * String out = "";
			 * 
			 * while (iter.hasNext()) { Entry<UInt, Object> entry = iter.next();
			 * out += "{" + entry.getKey() + " : " + entry.getValue() + "}"; }
			 */

			System.out.println("PING : " + node);
			inetwork.sendInChannel(node.getNext(), msg);
		}
	}

	void process(MessagePut msg) {
		if (range.inRange(msg.getKey()) == false) {
			inetwork.sendInChannel(node.getNext(), msg);
		} else {
			System.out.println(node.getId() + "route put vers "
					+ node.getNext());
			range.add(msg.getKey(), msg.getData());
		}
	}
}
