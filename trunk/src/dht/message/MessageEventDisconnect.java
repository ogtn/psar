package dht.message;

import dht.ANodeId;

public class MessageEventDisconnect extends AMessage {

	private static final long serialVersionUID = 1L;
	private ANodeId next;
	private int ttl;
	private ANodeId shortcut;

	public MessageEventDisconnect(ANodeId originalSource, ANodeId next) {
		super(originalSource);
		this.next = next;
		ttl = 0;
	}

	public ANodeId getNext() {
		return next;
	}

	public void setShortcut(ANodeId shortcut) {
		this.shortcut = shortcut;
	}

	public ANodeId getShortcut() {
		return shortcut;
	}
	
	public int getTtl() {
		return ttl;
	}

	public void incTtl() {
		ttl++;
	}
}
