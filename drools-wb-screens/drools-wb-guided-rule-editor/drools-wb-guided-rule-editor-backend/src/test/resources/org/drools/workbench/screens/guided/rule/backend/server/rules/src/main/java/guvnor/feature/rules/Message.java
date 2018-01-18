package guvnor.feature.rules;

import java.math.BigDecimal;

// $HASH(55d93934ddacd8765d257ba53570565e) (added manually)
public class Message {

	public static final int HELLO = 0;
	public static final int GOODBYE = 1;

	private String message;

	private int status;

	private BigDecimal lengthBytes;

	public String getMessage() {
		return this.message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getStatus() {
		return this.status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public BigDecimal getLengthBytes() {
		return lengthBytes;
	}

	public void setLengthBytes(BigDecimal lengthBytes) {
		this.lengthBytes = lengthBytes;
	}
}
