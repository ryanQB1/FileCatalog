package server.model;

public class RejectedException extends Exception{
    
    public RejectedException(String reason) {
        super(reason);
    }
    
    public RejectedException(String reason, Throwable rootCause) {
        super(reason, rootCause);
    }
    
}
