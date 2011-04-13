package psar;

public interface INodeState {

	void recv(MessageAskConnection msg);
	
	void recv(MessageConnectTo msg);
	
	void recv(MessageConnect msg);
	
	void recv(MessageDisconnect msg);
	
	void recv(MessagePing msg);
}
