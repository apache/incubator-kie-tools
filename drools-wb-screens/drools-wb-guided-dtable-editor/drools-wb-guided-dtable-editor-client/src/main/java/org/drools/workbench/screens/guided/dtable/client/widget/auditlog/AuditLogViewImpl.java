/*
 * Copyright 2012 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.drools.workbench.screens.guided.dtable.client.widget.auditlog;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.github.gwtbootstrap.client.ui.CheckBox;
import com.github.gwtbootstrap.client.ui.DropdownButton;
import com.github.gwtbootstrap.client.ui.Modal;
import com.github.gwtbootstrap.client.ui.constants.BackdropType;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.HasKeyboardPagingPolicy.KeyboardPagingPolicy;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import org.drools.workbench.models.commons.shared.auditlog.AuditLog;
import org.drools.workbench.models.commons.shared.auditlog.AuditLogEntry;
import org.drools.workbench.screens.guided.dtable.client.resources.i18n.GuidedDecisionTableConstants;
import org.kie.workbench.common.services.security.AppRoles;
import org.uberfire.client.common.popups.footers.ModalFooterOKButton;
import org.uberfire.client.tables.UberfireSimplePager;
import org.uberfire.security.Identity;

/**
 * The AuditLog View implementation
 */
public class AuditLogViewImpl extends Modal
        implements
        AuditLogView {

    private final AuditLog auditLog;

    @UiField
    DropdownButton eventTypes;

    @UiField
    SimplePanel eventsContainer;

    private CellTable<AuditLogEntry> events;

    //The current user's security context (admins can see all records)
    private final Identity identity;

    interface AuditLogViewImplBinder
            extends
            UiBinder<Widget, AuditLogViewImpl> {

    }

    private static AuditLogViewImplBinder uiBinder = GWT.create( AuditLogViewImplBinder.class );

    public AuditLogViewImpl( final AuditLog auditLog,
                             final Identity identity ) {
        this.auditLog = auditLog;
        this.identity = identity;

        setTitle( GuidedDecisionTableConstants.INSTANCE.DecisionTableAuditLog() );
        setBackdrop( BackdropType.STATIC );
        setKeyboard( true );
        setAnimation( true );
        setDynamicSafe( true );
        setWidth( "900px" );

        add( uiBinder.createAndBindUi( this ) );
        add( new ModalFooterOKButton( new Command() {
            @Override
            public void execute() {
                hide();
            }
        } ) );

        setup();
    }

    public void setup() {
        for ( Map.Entry<String, Boolean> e : auditLog.getAuditLogFilter().getAcceptedTypes().entrySet() ) {
            eventTypes.add( makeEventTypeCheckBox( e.getKey(),
                                                   e.getValue() ) );
        }

        events = new CellTable<AuditLogEntry>();

        final ListDataProvider<AuditLogEntry> dlp = new ListDataProvider<AuditLogEntry>( filterDeletedEntries( auditLog ) );
        dlp.addDataDisplay( events );

        AuditLogEntrySummaryColumn summaryColumn = new AuditLogEntrySummaryColumn();
        AuditLogEntryCommentColumn commentColumn = new AuditLogEntryCommentColumn();

        events.addColumn( summaryColumn );
        events.addColumn( commentColumn );

        events.setColumnWidth( summaryColumn,
                               50.0,
                               Unit.PCT );
        events.setColumnWidth( commentColumn,
                               50.0,
                               Unit.PCT );

        //If the current user is not an Administrator include the delete comment column
        if ( !identity.hasRole( AppRoles.ADMIN ) ) {

            AuditLogEntryDeleteCommentColumn deleteCommentColumn = new AuditLogEntryDeleteCommentColumn();
            deleteCommentColumn.setFieldUpdater( new FieldUpdater<AuditLogEntry, ImageResource>() {

                public void update( int index,
                                    AuditLogEntry row,
                                    ImageResource value ) {
                    row.setDeleted( true );
                    dlp.setList( filterDeletedEntries( auditLog ) );
                    dlp.refresh();
                }

            } );
            events.addColumn( deleteCommentColumn );
            events.setColumnWidth( commentColumn,
                                   45.0,
                                   Unit.PCT );
            events.setColumnWidth( deleteCommentColumn,
                                   5.0,
                                   Unit.PCT );
        }

        events.setEmptyTableWidget( new Label( GuidedDecisionTableConstants.INSTANCE.DecisionTableAuditLogNoEntries() ) );
        events.setKeyboardPagingPolicy( KeyboardPagingPolicy.CHANGE_PAGE );
        events.setKeyboardSelectionPolicy( KeyboardSelectionPolicy.DISABLED );

        UberfireSimplePager gsp = new UberfireSimplePager();
        gsp.setDisplay( events );

        events.setPageSize( 4 );
        gsp.setPageSize( 4 );

        VerticalPanel vp = new VerticalPanel();
        vp.add( gsp );
        vp.add( events );

        eventsContainer.add( vp );
    }

    private Widget makeEventTypeCheckBox( final String eventType,
                                          final Boolean isEnabled ) {
        final CheckBox chkEventType = new CheckBox( AuditLogEntryCellHelper.getEventTypeDisplayText( eventType ) );
        chkEventType.setValue( Boolean.TRUE.equals( isEnabled ) );
        chkEventType.addValueChangeHandler( new ValueChangeHandler<Boolean>() {

            @Override
            public void onValueChange( ValueChangeEvent<Boolean> event ) {
                auditLog.getAuditLogFilter().getAcceptedTypes().put( eventType,
                                                                     event.getValue() );
            }

        } );

        return chkEventType;
    }

    private List<AuditLogEntry> filterDeletedEntries( final List<AuditLogEntry> entries ) {
        if ( identity.hasRole( AppRoles.ADMIN ) ) {
            return entries;
        }
        final List<AuditLogEntry> filteredEntries = new ArrayList<AuditLogEntry>();
        final Iterator<AuditLogEntry> i = entries.iterator();
        while ( i.hasNext() ) {
            final AuditLogEntry entry = i.next();
            if ( !entry.isDeleted() ) {
                filteredEntries.add( entry );
            }
        }
        return filteredEntries;
    }

}
