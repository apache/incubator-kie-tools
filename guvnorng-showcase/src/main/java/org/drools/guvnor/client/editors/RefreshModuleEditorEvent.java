package org.drools.guvnor.client.editors;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public class RefreshModuleEditorEvent extends GwtEvent<RefreshModuleEditorEvent.Handler> {

    public interface Handler
        extends
        EventHandler {

        void onRefreshModule(RefreshModuleEditorEvent refreshModuleEditorEvent);
    }

    public static final Type<RefreshModuleEditorEvent.Handler> TYPE = new Type<RefreshModuleEditorEvent.Handler>();

    private final String                                       uuid;

    public RefreshModuleEditorEvent(String uuid) {
        this.uuid = uuid;
    }

    public String getUuid() {
        return uuid;
    }

    @Override
    public Type<RefreshModuleEditorEvent.Handler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(RefreshModuleEditorEvent.Handler handler) {
        handler.onRefreshModule( this );
    }
}
