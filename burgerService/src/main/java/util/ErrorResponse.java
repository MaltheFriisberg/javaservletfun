package util;

@SuppressWarnings("serial")
public class ErrorResponse implements java.io.Serializable {
	
	/**
	 * @param status A HTTP status code, used for classifying responses from the server. Part of the HTTP/1.1 standard
	 * @param errorMessage The error message associated with the status code 
	 */
	public ErrorResponse(int status, String errorMessage) {
		this.status = status;
		this.errorMessage = errorMessage;
	}

	public int status;
	public String errorMessage;
	
}
