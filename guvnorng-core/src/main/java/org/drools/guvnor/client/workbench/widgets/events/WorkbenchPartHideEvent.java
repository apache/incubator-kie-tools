package org.drools.guvnor.client.workbench.widgets.events;

import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.shared.GwtEvent;
import org.drools.guvnor.client.workbench.WorkbenchPart;

public class WorkbenchPartHideEvent extends GwtEvent<WorkbenchPartHideHandler> {

    private final WorkbenchPart                         deselectedWorkbenchPart;

    private static final Type<WorkbenchPartHideHandler> TYPE = new Type<WorkbenchPartHideHandler>();

    @Override
    public Type<WorkbenchPartHideHandler> getAssociatedType() {
        return TYPE;
    }

    public static Type<WorkbenchPartHideHandler> getType() {
        return TYPE;
    }

    @Override
    protected void dispatch(WorkbenchPartHideHandler handler) {
        handler.onHide( this );
    }

    public WorkbenchPartHideEvent(WorkbenchPart deselectedWorkbenchPart) {
        this.deselectedWorkbenchPart = deselectedWorkbenchPart;
    }

    public WorkbenchPart getDeselectedWorkbenchPart() {
        return deselectedWorkbenchPart;
    }

    public static <T> void fire(HasSelectionHandlers<T> source,
                                WorkbenchPart deselectedItem) {
        if ( TYPE != null ) {
            WorkbenchPartHideEvent event = new WorkbenchPartHideEvent( deselectedItem );
            source.fireEvent( event );
        }
    }
}
