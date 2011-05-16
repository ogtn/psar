package dht;

import java.util.EventListener;

import dht.message.AMessage;

public interface INetworkListener extends EventListener {
	void sendMessage(AMessage message, INode node, ANodeId id);
	void recvMessage(AMessage message, INode node);
}
