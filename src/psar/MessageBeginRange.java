package psar;

public class MessageBeginRange extends AMessage {

	private static final long serialVersionUID = 1L;
	private long beginRange;
	
	public MessageBeginRange(long id, long beginRange) {
		super(id);
		this.beginRange = beginRange;
	}
	
	public long getBegin() {
		return beginRange;
	}
}
