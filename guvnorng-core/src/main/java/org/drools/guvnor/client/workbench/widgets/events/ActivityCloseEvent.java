package org.drools.guvnor.client.workbench.widgets.events;

import com.google.gwt.event.shared.GwtEvent;

public class ActivityCloseEvent extends GwtEvent<ActivityCloseHandler> {

    private static final Type<ActivityCloseHandler> TYPE = new Type<ActivityCloseHandler>();

    @Override
    public Type<ActivityCloseHandler> getAssociatedType() {
        return TYPE;
    }

    public static Type<ActivityCloseHandler> getType() {
        return TYPE;
    }

    public static void fire(HasCloseActivityHandlers source) {
        ActivityCloseEvent event = new ActivityCloseEvent();
        source.fireEvent( event );
    }

    @Override
    protected void dispatch(ActivityCloseHandler handler) {
        handler.onCloseActivity( this );
    }
}
