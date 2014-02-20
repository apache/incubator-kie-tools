package org.drools.workbench.screens.guided.rule.client.widget;

import com.google.gwt.event.shared.GwtEvent;
import org.drools.workbench.screens.guided.rule.client.editor.ExpressionChangeHandler;

public class FactTypeKnownValueChangeEvent
        extends GwtEvent<FactTypeKnownValueChangeHandler> {

    private static final GwtEvent.Type<FactTypeKnownValueChangeHandler> TYPE = new GwtEvent.Type<FactTypeKnownValueChangeHandler>();

    @Override
    protected void dispatch( FactTypeKnownValueChangeHandler handler ) {
        handler.onValueChanged( this );
    }

    @Override
    public GwtEvent.Type<FactTypeKnownValueChangeHandler> getAssociatedType() {
        return getType();
    }

    public static final Type<FactTypeKnownValueChangeHandler> getType() {
        return TYPE;
    }
}
