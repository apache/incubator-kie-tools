package org.drools.guvnor.client.workbench.widgets.events;

import com.google.gwt.event.shared.GwtEvent;

public class ActivityCloseEvent extends GwtEvent<ActivityCloseHandler> {

    private static Type<ActivityCloseHandler> TYPE;

    @Override
    public Type<ActivityCloseHandler> getAssociatedType() {
        return TYPE;
    }

    public static Type<ActivityCloseHandler> getType() {
        if (TYPE == null) {
            TYPE = new Type<ActivityCloseHandler>();
        }
        return (Type) TYPE;
    }

    public static void fire(HasCloseActivityHandlers source) {
        if (TYPE != null) {
            ActivityCloseEvent event = new ActivityCloseEvent();
            source.fireEvent(event);
        }
    }

    @Override
    protected void dispatch(ActivityCloseHandler handler) {
        handler.onCloseActivity(this);
    }
}
