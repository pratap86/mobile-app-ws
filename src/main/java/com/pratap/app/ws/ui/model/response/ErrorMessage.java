package com.pratap.app.ws.ui.model.response;

import java.util.Date;

/**
 * 
 * @author Pratap Narayan
 * <p>ErrorMessage, use to display the custom error message(time & error message)</p>
 *
 */
public class ErrorMessage {

	private Date timestamp;
	private String message;

	public ErrorMessage() {
	}

	public ErrorMessage(Date timestamp, String message) {
		this.timestamp = timestamp;
		this.message = message;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
