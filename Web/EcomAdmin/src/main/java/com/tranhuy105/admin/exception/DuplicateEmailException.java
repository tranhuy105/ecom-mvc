package com.tranhuy105.admin.exception;

public class DuplicateEmailException extends RuntimeException {
    public DuplicateEmailException() {
        super("This email has already associated with another account.");
    }
}
