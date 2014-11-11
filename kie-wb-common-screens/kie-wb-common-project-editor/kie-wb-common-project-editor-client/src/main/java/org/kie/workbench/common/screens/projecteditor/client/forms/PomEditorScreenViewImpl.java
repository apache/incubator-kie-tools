package org.kie.workbench.common.screens.projecteditor.client.forms;

import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.kie.workbench.common.screens.projecteditor.client.resources.ProjectEditorResources;
import org.uberfire.ext.widgets.common.client.common.BusyPopup;
import org.uberfire.workbench.events.NotificationEvent;

public class PomEditorScreenViewImpl
        implements PomEditorScreenView {

    private final Event<NotificationEvent> notificationEvent;

    @Inject
    public PomEditorScreenViewImpl( Event<NotificationEvent> notificationEvent ) {
        this.notificationEvent = notificationEvent;
    }

    @Override
    public void showBusyIndicator( final String message ) {
        BusyPopup.showMessage( message );
    }

    @Override
    public void hideBusyIndicator() {
        BusyPopup.close();
    }

    @Override
    public void showSaveSuccessful( String fileName ) {
        notificationEvent.fire( new NotificationEvent( ProjectEditorResources.CONSTANTS.SaveSuccessful( fileName ) ) );
    }
}
