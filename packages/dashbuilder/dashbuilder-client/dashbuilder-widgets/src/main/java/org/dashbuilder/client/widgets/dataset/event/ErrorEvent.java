package org.dashbuilder.client.widgets.dataset.event;

import org.dashbuilder.common.client.error.ClientRuntimeError;
import org.dashbuilder.common.client.event.ContextualEvent;

/**
 * <p>CDI event when a operation produces an error.</p>
 *
 * @since 0.4.0
 */
public class ErrorEvent extends ContextualEvent {

    private ClientRuntimeError clientRuntimeError;
    private String uuid;
    private String message;

    public ErrorEvent(final Object context, final ClientRuntimeError clientRuntimeError) {
        super(context);
        this.clientRuntimeError = clientRuntimeError;
    }

    public ErrorEvent(final Object context, final ClientRuntimeError clientRuntimeError, final String uuid) {
        this(context, clientRuntimeError);
        this.uuid = uuid;
        
    }

    public ErrorEvent(final Object context, final String message, final String uuid) {
        super(context);
        this.message = message;
        this.uuid = uuid;
    }

    public ErrorEvent(final Object context) {
        super(context);
    }

    public ClientRuntimeError getClientRuntimeError() {
        return clientRuntimeError;
    }

    public String getMessage() {
        return message;
    }

    public String getUUID() {
        return uuid;
    }

    @Override
    public String toString() {
        return "ErrorEvent [Context=" + getContext().toString() + "]";
    }

}
