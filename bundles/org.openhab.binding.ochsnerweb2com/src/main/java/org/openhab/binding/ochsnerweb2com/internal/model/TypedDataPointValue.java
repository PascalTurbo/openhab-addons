package org.openhab.binding.ochsnerweb2com.internal.model;

public class TypedDataPointValue<T> {
    private T value;

    public void GenericValue(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }
}
