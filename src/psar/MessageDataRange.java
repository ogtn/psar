package psar;

public class MessageDataRange extends AMessage {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private long key;
	private Object data;
	private long endRange;
	
	MessageDataRange(long originalSource, long key, Object data, long endRange) {
		super(originalSource);
		this.key = key;
		this.data = data;
		this.endRange = endRange;
	}
	
	public long getKey() {
		return key;
	}
	
	public Object getData() {
		return data;
	}
	
	public long getEndRange() {
		return endRange;
	}
}
