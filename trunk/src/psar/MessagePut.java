package psar;

public class MessagePut extends AMessage {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Object data;
	private long key;
	
	MessagePut(long originalSource, Object data, long key) {
		super(originalSource);
		this.data = data;
		this.key = key;
	}
	
	public Object getData() {
		return data;
	}
	
	public long getKey() {
		return key;
	}
}
