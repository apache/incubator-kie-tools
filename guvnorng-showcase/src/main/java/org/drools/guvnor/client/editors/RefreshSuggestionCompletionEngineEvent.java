package org.drools.guvnor.client.editors;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public class RefreshSuggestionCompletionEngineEvent extends GwtEvent<RefreshSuggestionCompletionEngineEvent.Handler> {

    public interface Handler
        extends
        EventHandler {
        void onRefreshModule(RefreshSuggestionCompletionEngineEvent refreshSuggestionCompletionEngineEvent);
    }

    public static final Type<RefreshSuggestionCompletionEngineEvent.Handler> TYPE = new Type<RefreshSuggestionCompletionEngineEvent.Handler>();

    private final String                                                     moduleName;

    public RefreshSuggestionCompletionEngineEvent(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getModuleName() {
        return moduleName;
    }

    @Override
    public Type<RefreshSuggestionCompletionEngineEvent.Handler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(RefreshSuggestionCompletionEngineEvent.Handler handler) {
        handler.onRefreshModule( this );
    }
}
