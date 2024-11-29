package br.com.Challenger.LiterAlura.exception;

public class DataProcessingException extends RuntimeException {

    private final String message;

    public DataProcessingException(String message, Exception e) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}

