package psar;

public class MessageGet extends AMessage {
	private long key;
	
	public MessageGet(long id, long key) {
		super(id);
		this.key = key;
	}
	
	public long getKey() {
		return key;
	}
}
