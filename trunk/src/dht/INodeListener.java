package dht;



public interface INodeListener {
	void eventChangeState(INode node, ANodeState oldState, ANodeState newState); // TODO modifier l'heritage pour un nom conforme au rapport
} 
