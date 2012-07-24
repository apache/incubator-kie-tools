package org.drools.guvnor.client.editors;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.user.client.Command;

public class RefreshModuleDataModelEvent extends GwtEvent<RefreshModuleDataModelEvent.Handler> {

    public interface Handler
        extends
        EventHandler {

        void onRefreshModuleDataModel(RefreshModuleDataModelEvent refreshModuleDataModelEvent);
    }

    public static final Type<RefreshModuleDataModelEvent.Handler> TYPE            = new Type<RefreshModuleDataModelEvent.Handler>();

    private final String                                          moduleName;
    private Command                                               callbackCommand = null;

    public RefreshModuleDataModelEvent(String moduleName,
                                       Command callbackCommand) {
        this.moduleName = moduleName;
        this.callbackCommand = callbackCommand;
    }

    public String getModuleName() {
        return moduleName;
    }

    public Command getCallbackCommand() {
        return callbackCommand;
    }

    @Override
    public Type<RefreshModuleDataModelEvent.Handler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(RefreshModuleDataModelEvent.Handler handler) {
        handler.onRefreshModuleDataModel( this );
    }
}
