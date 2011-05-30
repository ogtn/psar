package dht;

import java.util.EventListener;

import dht.message.AMessage;

public interface INetworkListener extends EventListener {
	void eventSendMessage(AMessage message, INode node, ANodeId id, boolean isInChannel);
	void eventRecvMessage(AMessage message, INode node); // TODO modifier l'heritage pour un nom conforme au rapport
}
