package dht;

import dht.message.MessageAskConnection;
import dht.message.MessageConnect;
import dht.message.MessageConnectTo;
import dht.message.MessageDisconnect;
import dht.message.MessagePing;

/**
 * Etat du noeud qui est entrain de rejoindre l'anneau.
 */
public class ConnectingState implements INodeState {

	@Override
	public void recv(MessageAskConnection msg) {
		// TODO err
		System.err.println("MessageAskConnection");
	}

	@Override
	public void recv(MessageConnectTo msg) {
		// TODO Auto-generated method stub

	}

	@Override
	public void recv(MessageConnect msg) {
		// TODO Auto-generated method stub

	}

	@Override
	public void recv(MessageDisconnect msg) {
		// TODO Auto-generated method stub

	}

	@Override
	public void recv(MessagePing msg) {
		// TODO Auto-generated method stub

	}

}