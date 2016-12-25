package com.ccnode.codegenerator.dialog.exception;

import org.jetbrains.annotations.NonNls;

/**
 * Created by bruce.ge on 2016/12/25.
 */
public class TypeNotFoundException extends RuntimeException {
    public TypeNotFoundException() {
        super();
    }

    public TypeNotFoundException(@NonNls String message) {
        super(message);
    }
}
