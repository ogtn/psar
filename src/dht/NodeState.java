package dht;

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

abstract class NodeState {
	
	protected INetwork inetwork;
	protected BlockingQueue<AMessage> queue;
	protected Node node;
	protected Range range;

	NodeState(INetwork inetwork, BlockingQueue<AMessage> queue, Node node, Range range) {
		this.node = node;
		this.queue = queue;
		this.inetwork = inetwork;
		this.range =range;
	}
	
	abstract void run();
	
	void process(MessageAskConnection msg) {}
	void process(MessageBeginRange msg) {}
	void process(MessageConnect msg) {}
	void process(MessageConnectTo msg) {}
	void process(MessageData msg) {}
	void process(MessageDataRange msg) {}
	void process(MessageDisconnect msg) {}
	void process(MessageEndRange msg) {}
	void process(MessageError msg) {}
	void process(MessageGet msg) {}
	void process(MessagePing msg) {}
	void process(MessagePut msg) {}
}
