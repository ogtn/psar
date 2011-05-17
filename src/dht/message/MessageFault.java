package dht.message;

import dht.ANodeId;
import dht.UInt;

public class MessageFault extends AMessage {

	private static final long serialVersionUID = 1L;

	private UInt newBeginRange;
	
	public MessageFault(ANodeId originalSource, UInt newBeginRange) {
		super(originalSource);
		this.newBeginRange = newBeginRange;
	}
	
	public UInt getNewBeginRange() {
		return newBeginRange;
	}
}
