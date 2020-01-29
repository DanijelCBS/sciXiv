package xml.web.services.team2.sciXiv.exception;

public class NotOnTheReviewerListException extends RuntimeException {

	private static final long serialVersionUID = -1773640420717069970L;

	public NotOnTheReviewerListException(String message) {
		super(message);
	}

	public NotOnTheReviewerListException(String message, Throwable cause) {
		super(message, cause);
	}
}
