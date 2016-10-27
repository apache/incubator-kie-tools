/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.client.widgets.notification;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.user.cellview.client.ColumnSortEvent;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;
import org.gwtbootstrap3.extras.notify.client.constants.NotifyType;
import org.gwtbootstrap3.extras.notify.client.ui.Notify;
import org.gwtbootstrap3.extras.notify.client.ui.NotifySettings;
import org.kie.workbench.common.stunner.client.widgets.notification.canvas.CanvasCommandNotification;
import org.kie.workbench.common.stunner.client.widgets.notification.canvas.CanvasNotificationContext;
import org.kie.workbench.common.stunner.client.widgets.notification.canvas.CanvasValidationFailNotification;
import org.kie.workbench.common.stunner.client.widgets.notification.canvas.CanvasValidationSuccessNotification;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.event.command.AbstractCanvasCommandEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.command.CanvasCommandExecutedEvent;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.client.validation.canvas.CanvasValidationFailEvent;
import org.kie.workbench.common.stunner.core.client.validation.canvas.CanvasValidationSuccessEvent;
import org.kie.workbench.common.stunner.core.client.validation.canvas.CanvasValidationViolation;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.util.UUID;
import org.kie.workbench.common.stunner.core.validation.event.AbstractValidationEvent;
import org.kie.workbench.common.stunner.core.validation.event.AbstractValidationFailEvent;
import org.uberfire.client.mvp.UberView;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.util.Collection;
import java.util.List;

@Dependent
public class Notifications implements IsWidget {

    public interface View extends UberView<Notifications> {

        View setColumnSortHandler( ColumnSortEvent.ListHandler<Notification> sortHandler );

        View addColumn( com.google.gwt.user.cellview.client.Column<Notification, String> column,
                        String name );

        View removeColumn( int index );

        int getColumnCount();

        View redraw();

        View clear();

    }

    View view;
    final ListDataProvider<Notification> logsProvider = new ListDataProvider<Notification>();
    private boolean notifyErrors = true;

    @Inject
    public Notifications( final View view ) {
        this.view = view;
    }

    @PostConstruct
    public void init() {
        view.init( this );
        buildViewColumns();
    }

    public void setNotifyErrors( boolean notifyErrors ) {
        this.notifyErrors = notifyErrors;
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    public void add( final Notification notification ) {
        if ( null != notification ) {
            showNotificationPopup( notification );
            addLogEntry( notification );
            view.redraw();

        }

    }

    private void showNotificationPopup( final Notification notification ) {
        if ( notification instanceof CanvasValidationSuccessNotification ) {
            final String text = getNotificationSourceMessage( notification );
            doShowNotify( NotifyType.SUCCESS, text );

        }
        if ( notifyErrors && Notification.Type.ERROR.equals( notification.getType() ) ) {
            final String text = getNotificationSourceMessage( notification );
            doShowNotify( NotifyType.DANGER, text );

        }

    }

    private void doShowNotify( final NotifyType type,
                               final String message ) {
        NotifySettings settings = NotifySettings.newSettings();
        settings.makeDefault();
        settings.setType( type );
        settings.setDelay( 5000 );
        settings.setTimer( 100 );
        settings.setAllowDismiss( true );
        Notify.notify( message, settings );

    }

    public void clear() {
        logsProvider.getList().clear();
        view.clear();

    }
    
    /*  ******************************************************************************************************
                                 VIEW CALLBACKS 
     ****************************************************************************************************** */

    final ProvidesKey<Notification> KEY_PROVIDER = new ProvidesKey<Notification>() {
        @Override
        public Object getKey( final Notification item ) {
            return item == null ? null : item.getNotificationUUID();
        }
    };

    /**
     * View callback for getting the list.
     */
    void addDataDisplay( final HasData<Notification> display ) {
        logsProvider.addDataDisplay( display );
    }

    protected void buildViewColumns() {
        int columnCount = view.getColumnCount();
        while ( columnCount > 0 ) {
            view.removeColumn( 0 );
            columnCount = view.getColumnCount();
        }
        // Attach a column sort handler to the ListDataProvider to sort the list.
        ColumnSortEvent.ListHandler<Notification> sortHandler = new ColumnSortEvent.ListHandler<Notification>( logsProvider.getList() );
        view.setColumnSortHandler( sortHandler );
        // Log's type.
        final com.google.gwt.user.cellview.client.Column<Notification, String> typeColumn = createTypeColumn( sortHandler );
        if ( typeColumn != null ) {
            view.addColumn( typeColumn, "Type" );
        }
        // Log element's UUID.
        final com.google.gwt.user.cellview.client.Column<Notification, String> contextColumn = createContextColumn( sortHandler );
        if ( contextColumn != null ) {
            view.addColumn( contextColumn, "Context" );
        }
        // Log's message.
        final com.google.gwt.user.cellview.client.Column<Notification, String> messageColumn = createMessageColumn( sortHandler );
        if ( messageColumn != null ) {
            view.addColumn( messageColumn, "Message" );
        }

    }

    private void addLogEntry( final Notification entry ) {
        List<Notification> logs = logsProvider.getList();
        logs.remove( entry );
        logs.add( entry );
    }

    private com.google.gwt.user.cellview.client.Column<Notification, String> createTypeColumn( ColumnSortEvent.ListHandler<Notification> sortHandler ) {
        // Log type..
        final Cell<String> typeCell = new TextCell();
        final com.google.gwt.user.cellview.client.Column<Notification, String> typeColumn = new com.google.gwt.user.cellview.client.Column<Notification, String>(
                typeCell ) {
            @Override
            public String getValue( final Notification object ) {
                return getNotificationTypeMessage( object );
            }
        };
        typeColumn.setSortable( true );
        sortHandler.setComparator( typeColumn, ( o1, o2 ) -> o1.getType().compareTo( o2.getType() ) );
        return typeColumn;
    }

    private com.google.gwt.user.cellview.client.Column<Notification, String> createContextColumn( ColumnSortEvent.ListHandler<Notification> sortHandler ) {
        // Log element's context.
        final Cell<String> contextCell = new TextCell();
        final com.google.gwt.user.cellview.client.Column<Notification, String> contextColumn = new com.google.gwt.user.cellview.client.Column<Notification, String>(
                contextCell ) {
            @Override
            public String getValue( final Notification object ) {
                return getNotificationContextMessage( object );
            }
        };
        contextColumn.setSortable( false );
        return contextColumn;
    }

    private com.google.gwt.user.cellview.client.Column<Notification, String> createMessageColumn( ColumnSortEvent.ListHandler<Notification> sortHandler ) {
        // Log message.
        final Cell<String> messageCell = new TextCell();
        final com.google.gwt.user.cellview.client.Column<Notification, String> messageColumn = new com.google.gwt.user.cellview.client.Column<Notification, String>(
                messageCell ) {
            @Override
            public String getValue( final Notification object ) {
                return getNotificationSourceMessage( object );
            }
        };
        messageColumn.setSortable( false );
        return messageColumn;
    }

    @SuppressWarnings( "unchecked" )
    private Notification translate( final AbstractCanvasCommandEvent<? extends CanvasHandler> commandExecutedEvent ) {
        if ( null != commandExecutedEvent ) {
            final CanvasHandler canvasHandler = commandExecutedEvent.getCanvasHandler();
            final Command<CanvasHandler, CanvasViolation> command =
                    ( Command<CanvasHandler, CanvasViolation> ) commandExecutedEvent.getCommand();
            final Collection<? extends Command<? extends CanvasHandler, CanvasViolation>> commands = commandExecutedEvent.getCommands();
            final CommandResult<CanvasViolation> result = commandExecutedEvent.getResult();
            return new CanvasCommandNotification.CanvasCommandNotificationBuilder<CanvasHandler>()
                    .canvasHander( canvasHandler )
                    .commands( ( Collection<Command<CanvasHandler, CanvasViolation>> ) commands )
                    .command( command )
                    .result( result )
                    .build();

        }
        return null;
    }

    @SuppressWarnings( "unchecked" )
    private Notification translate( final AbstractValidationEvent<? extends CanvasHandler> validationEvent ) {
        if ( null != validationEvent ) {
            final CanvasHandler canvasHandler = validationEvent.getEntity();
            final Diagram diagram = canvasHandler.getDiagram();
            final String diagramUUID = diagram.getName();
            final String title = diagram.getMetadata().getTitle();
            final CanvasNotificationContext context =
                    new CanvasNotificationContext( canvasHandler.toString(), diagramUUID, title );
            if ( validationEvent instanceof AbstractValidationFailEvent ) {
                final AbstractValidationFailEvent failEvent = ( AbstractValidationFailEvent ) validationEvent;
                final Iterable<CanvasValidationViolation> violations = failEvent.getViolations();
                return new CanvasValidationFailNotification( UUID.uuid(), violations, context );

            }
            return new CanvasValidationSuccessNotification( UUID.uuid(), context );

        }
        return null;
    }

    @SuppressWarnings( "unchecked" )
    private String getNotificationSourceMessage( final Notification notification ) {
        return notification.getSource() != null ?
                notification.getSource().toString() :
                "-- No source --";
    }

    @SuppressWarnings( "unchecked" )
    private String getNotificationContextMessage( final Notification notification ) {
        return notification.getContext() != null ?
                notification.getContext().toString() :
                "-- No context --";
    }

    @SuppressWarnings( "unchecked" )
    private String getNotificationTypeMessage( final Notification notification ) {
        return notification.getType() != null ?
                notification.getType().name() :
                "-- No type --";
    }

    void onGraphCommandExecuted( @Observes CanvasCommandExecutedEvent<? extends CanvasHandler> commandExecutedEvent ) {
        Notification notification = translate( commandExecutedEvent );
        add( notification );
    }

    void onCanvasValidationSuccessEvent( @Observes CanvasValidationSuccessEvent validationSuccessEvent ) {
        Notification notification = translate( validationSuccessEvent );
        add( notification );
    }

    void onCanvasValidationFailEvent( @Observes CanvasValidationFailEvent validationFailEvent ) {
        Notification notification = translate( validationFailEvent );
        add( notification );
    }

}
