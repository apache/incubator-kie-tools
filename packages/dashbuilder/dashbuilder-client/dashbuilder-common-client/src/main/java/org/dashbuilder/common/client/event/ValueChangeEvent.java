package org.dashbuilder.common.client.event;

/**
 * <p>CDI event for components that have a value and it can be changed.</p>
 * 
 * @since 0.4.0
 */
public class ValueChangeEvent<T> extends ContextualEvent {

    private final T oldValue;
    private final T value;

    public ValueChangeEvent(final Object context, final T oldValue, final T value) {
        super(context);
        this.oldValue = oldValue;
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    public T getOldValue() {
        return oldValue;
    }

    @Override
    public String toString() {
        return "ValueChangeEvent [value=" + value.toString() + "]";
    }

}
