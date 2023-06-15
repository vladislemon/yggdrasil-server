package ru.vladislemon.yggdrasilserver.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ExceptionResponse {
    private final String error;
    private final String errorMessage;
    private final String cause;
}
