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

import com.github.gwtbootstrap.client.ui.*;
import com.github.gwtbootstrap.client.ui.CheckBox;
import com.github.gwtbootstrap.client.ui.constants.BackdropType;
import com.github.gwtbootstrap.client.ui.constants.Constants;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.HasKeyboardPagingPolicy.KeyboardPagingPolicy;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.cellview.client.RowStyles;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.view.client.ListDataProvider;
import org.drools.workbench.models.datamodel.auditlog.AuditLog;
import org.drools.workbench.models.datamodel.auditlog.AuditLogEntry;
import org.drools.workbench.screens.guided.dtable.client.resources.i18n.GuidedDecisionTableConstants;
import org.kie.workbench.common.services.security.AppRoles;
import org.uberfire.client.common.popups.footers.ModalFooterOKButton;
import org.uberfire.security.Identity;

/**
 * The AuditLog View implementation
 */
public class AuditLogViewImpl extends Modal
        implements
        AuditLogView {

    private final AuditLog auditLog;

    /* The 700px constant. */
    private static final String P700 = "700px";

    /* The 500px width constant. */
    private static final String P500 = "500px";

    /**
     * The page size constant value.
     */
    private static final int PAGE_SIZE = 4;

    @UiField
    FlowPanel eventTypes;

    @UiField
    SimplePanel eventsContainer;

    @UiField
    SimplePager pager;

    private CellTable<AuditLogEntry> events;

    /**
     * Custom styles for audit log cell table.
     * <p/>
     * NOTE: BZ-996942
     */
    public interface AuditLogStyle extends CssResource {

        String eventTypesTitle();

        String eventsContainerInline();

        String auitLogModalBody();

        String eventTypesCheckbox();

        String auditLogDetailLabel();

        String auditLogDetailValue();
    }

    @UiField
    AuditLogStyle style;

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
        setMaxHeigth( P500 );

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

        // BZ-996942: Add a custom style for modal panel to set a fixed width.
        addStyleName( style.auitLogModalBody() );

        // BZ-996917: Use a the gwtboostrap style "row-fluid" to allow display some events in the same row.
        eventTypes.setStyleName( Constants.ROW_FLUID );

        // Fill panel with available events.
        for ( Map.Entry<String, Boolean> e : auditLog.getAuditLogFilter().getAcceptedTypes().entrySet() ) {
            eventTypes.add( makeEventTypeCheckBox( e.getKey(),
                                                   e.getValue() ) );
        }

        // Create the GWT Cell Table as events container.
        // BZ-996942: Set custom width and table css style.
        events = new CellTable<AuditLogEntry>();
        events.setWidth( P700 );
        events.addStyleName( Constants.TABLE );

        final ListDataProvider<AuditLogEntry> dlp = new ListDataProvider<AuditLogEntry>( filterDeletedEntries( auditLog ) );
        dlp.addDataDisplay( events );

        AuditLogEntrySummaryColumn summaryColumn = new AuditLogEntrySummaryColumn( style.auditLogDetailLabel(), style.auditLogDetailValue() );
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
        if ( identity.hasRole( AppRoles.ADMIN ) ) {

            AuditLogEntryDeleteCommentColumn deleteCommentColumn = new AuditLogEntryDeleteCommentColumn();
            deleteCommentColumn.setFieldUpdater( new FieldUpdater<AuditLogEntry, SafeHtml>() {

                public void update( int index,
                                    AuditLogEntry row,
                                    SafeHtml value ) {
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
        events.setPageSize( PAGE_SIZE );

        // Configure the simple pager.
        pager.setDisplay( events );
        pager.setPageSize( PAGE_SIZE );

        // Add the table to the container.
        eventsContainer.add( events );
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

        // BZ-996942: Use one column layout.
        chkEventType.addStyleName( "span2" );
        chkEventType.addStyleName( style.eventTypesCheckbox() );
        chkEventType.setWordWrap( false );

        return chkEventType;
    }

    private List<AuditLogEntry> filterDeletedEntries( final List<AuditLogEntry> entries ) {
        if ( !identity.hasRole( AppRoles.ADMIN ) ) {
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
