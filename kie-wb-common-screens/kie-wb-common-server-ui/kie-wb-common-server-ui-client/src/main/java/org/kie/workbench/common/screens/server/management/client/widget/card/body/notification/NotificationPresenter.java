package org.kie.workbench.common.screens.server.management.client.widget.card.body.notification;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.kie.server.api.model.Message;
import org.kie.server.api.model.Severity;

import static org.uberfire.commons.validation.PortablePreconditions.*;

@Dependent
public class NotificationPresenter {

    public interface View extends IsWidget {

        void setupOk();

        void setup( final NotificationType type,
                    final String size );

        void setup( final NotificationType type,
                    final String size,
                    final String popOverContent );
    }

    private final View view;

    @Inject
    public NotificationPresenter( final View view ) {
        this.view = view;
    }

    public View getView() {
        return view;
    }

    public void setupOk() {
        view.setupOk();
    }

    public void setup( final Message message ) {
        checkNotNull( "message", message );

        final NotificationType notificationType = toNotificationType( message.getSeverity() );
        if ( notificationType.equals( NotificationType.OK ) ) {
            view.setupOk();
        } else {
            if ( message.getMessages().isEmpty() ) {
                view.setup( notificationType, "0" );
            } else {
                final StringBuilder sb = new StringBuilder();
                int i = 0;
                for ( final String msg : message.getMessages() ) {
                    if ( !msg.trim().isEmpty() ) {
                        i++;
                        sb.append( i ).append( ": " ).append( msg ).append( '\n' );
                    }
                }
                view.setup( notificationType, String.valueOf( i ), clean( sb ) );
            }
        }
    }

    private String clean( StringBuilder str ) {
        final String result;
        if ( str.length() > 0 ) {
            result = str.substring( 0, str.length() - 1 );
        } else {
            result = "";
        }
        return result;
    }

    private NotificationType toNotificationType( final Severity severity ) {
        checkNotNull( "severity", severity );

        switch ( severity ) {
            case WARN:
                return NotificationType.WARNING;

            case ERROR:
                return NotificationType.ERROR;

            case INFO:
                return NotificationType.OK;

            default:
                return NotificationType.OK;
        }
    }

}
