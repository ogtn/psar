package dht;

import dht.message.MessageAskConnection;
import dht.message.MessageConnect;
import dht.message.MessageConnectTo;
import dht.message.MessageDisconnect;
import dht.message.MessagePing;

public interface INodeState {

	void recv(MessageAskConnection msg);
	
	void recv(MessageConnectTo msg);
	
	void recv(MessageConnect msg);
	
	void recv(MessageDisconnect msg);
	
	void recv(MessagePing msg);
}
