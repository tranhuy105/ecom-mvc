package com.tranhuy105.site.exception;

public class StockUnavailableException extends RuntimeException{
    public StockUnavailableException(String message)
    {
        super(message);
    }
}
