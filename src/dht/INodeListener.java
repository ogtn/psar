package dht;



public interface INodeListener {
	void changeState(INode node, ANodeState oldState, ANodeState newState);
}
