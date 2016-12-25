package com.ccnode.codegenerator.dialog.exception;

import org.jetbrains.annotations.NonNls;

/**
 * Created by bruce.ge on 2016/12/26.
 */
public class NotStringException extends RuntimeException {
    public NotStringException() {
        super();
    }

    public NotStringException(@NonNls String message) {
        super(message);
    }
}
