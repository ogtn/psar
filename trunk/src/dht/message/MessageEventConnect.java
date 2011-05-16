package dht.message;

import dht.ANodeId;

public class MessageEventConnect extends AMessage {

	private static final long serialVersionUID = 1L;
	private ANodeId previous;
	private ANodeId next;
	private ANodeId shortcut;
	private int ttl;

	public MessageEventConnect(ANodeId previous, ANodeId originalSource,
			ANodeId next) {
		super(originalSource);
		this.next = next;
		this.previous = previous;
		ttl = 0;
	}

	public ANodeId getPrevious() {
		return previous;
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