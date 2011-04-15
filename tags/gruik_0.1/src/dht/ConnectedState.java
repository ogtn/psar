package dht;

import dht.message.MessageAskConnection;
import dht.message.MessageConnect;
import dht.message.MessageConnectTo;
import dht.message.MessageDisconnect;
import dht.message.MessagePing;

public class ConnectedState implements INodeState {

	private INode node;

	public ConnectedState(INode node, int insertingNodeId) {
		this.node = node;
	}
	
	@Override
	public void recv(MessageAskConnection msg) {
		if (node.getRange().inRange(msg.getOriginalSource())) {
			node.setState(new InsertingNextState());
		} else {
			node.getNetwork().sendInChannel(node.getNext(), msg);
		}
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
		node.getNetwork().sendInChannel(node.getNext(), msg);
	}
}
