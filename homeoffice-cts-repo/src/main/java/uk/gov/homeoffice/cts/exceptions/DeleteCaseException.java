package uk.gov.homeoffice.cts.exceptions;

/**
 * Created by davidt on 17/09/2014.
 */
public class DeleteCaseException extends RuntimeException {

    public DeleteCaseException(String message) {
        super(message);
    }

    public DeleteCaseException(String message, Throwable cause) {
        super(message, cause);
    }
}
