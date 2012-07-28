package org.uberfire.client.common;

import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.HasCloseHandlers;
import com.google.gwt.event.logical.shared.HasOpenHandlers;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.SimplePanel;

public abstract class AbstractLazyStackPanelHeader extends SimplePanel
    implements
    HasCloseHandlers<AbstractLazyStackPanelHeader>,
    HasOpenHandlers<AbstractLazyStackPanelHeader> {

    protected boolean expanded = false;

    public HandlerRegistration addOpenHandler(OpenHandler<AbstractLazyStackPanelHeader> handler) {
        return addHandler( handler,
                           OpenEvent.getType() );
    }

    public HandlerRegistration addCloseHandler(CloseHandler<AbstractLazyStackPanelHeader> handler) {
        return addHandler( handler,
                           CloseEvent.getType() );
    }
    
    public abstract void expand();
    
    public abstract void collapse();

}
