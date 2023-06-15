package ru.vladislemon.yggdrasilserver.validator;

public abstract class ObjectValidator<T> {

    public abstract void validate(T object);

    protected void checkArgument(final boolean condition, final RuntimeException e) {
        if(!condition) {
            throw e;
        }
    }

    protected void checkArgument(final boolean condition) {
        checkArgument(condition, new IllegalArgumentException());
    }
}
