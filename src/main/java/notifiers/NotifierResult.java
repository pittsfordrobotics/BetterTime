package notifiers;

public class NotifierResult {
    private String message;

    public NotifierResult() {
        this.message = "";
    }

    public NotifierResult(String message) {
        this.message = message;
    }

    public boolean hasMessage() {
        return (this.message != null && !this.message.equals(""));
    }

    public String getMessage() {
        return this.message == null ? "" : this.message;
    }
}
