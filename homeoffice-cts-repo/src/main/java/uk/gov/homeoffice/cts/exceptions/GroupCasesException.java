package uk.gov.homeoffice.cts.exceptions;

import java.util.ArrayList;
import java.util.List;

public class GroupCasesException extends Exception {

    private List<String> errorMessages;

    public GroupCasesException(String message) {
        super(message);
        this.errorMessages = new ArrayList<>();
    }

    public GroupCasesException(String message, List<String> errorMessages) {
        super(message);
        this.errorMessages = errorMessages;
    }

    public GroupCasesException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public List<String> getErrorMessages() {
        return errorMessages;
    }

    public void setErrorMessages(List<String> errorMessages) {
        this.errorMessages = errorMessages;
    }
}
