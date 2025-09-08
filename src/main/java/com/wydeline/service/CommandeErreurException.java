// com.wydeline.service.BadOrderException
package com.wydeline.service;

public class CommandeErreurException extends RuntimeException {
    public CommandeErreurException(String msg) { super(msg); }
}
