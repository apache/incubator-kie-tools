package org.drools.guvnor.client.widgets;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

/**
 * An Event to show a message in the Notification area
 */
public class ShowMessageEvent extends GwtEvent<ShowMessageEvent.Handler> {

    public interface Handler
        extends
        EventHandler {

        void onShowMessage(ShowMessageEvent event);
    }

    public static Type<Handler> TYPE = new Type<Handler>();

    private final String        message;

    private final MessageType   messageType;

    public ShowMessageEvent(String message,
                            MessageType messageType) {
        this.message = message;
        this.messageType = messageType;
    }

    public String getMessage() {
        return this.message;
    }

    public MessageType getMessageType() {
        return this.messageType;
    }

    @Override
    public Type<ShowMessageEvent.Handler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(ShowMessageEvent.Handler handler) {
        handler.onShowMessage( this );
    }

    public static enum MessageType {
        INFO,
        WARNING,
        ERROR
    }
}
