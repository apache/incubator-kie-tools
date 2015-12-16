/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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

import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.HasKeyboardPagingPolicy.KeyboardPagingPolicy;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.RangeChangeEvent;
import org.drools.workbench.models.datamodel.auditlog.AuditLog;
import org.drools.workbench.models.datamodel.auditlog.AuditLogEntry;
import org.drools.workbench.screens.guided.dtable.client.resources.i18n.GuidedDecisionTableConstants;
import org.guvnor.common.services.shared.security.AppRoles;
import org.gwtbootstrap3.client.ui.CheckBox;
import org.gwtbootstrap3.client.ui.Column;
import org.gwtbootstrap3.client.ui.Label;
import org.gwtbootstrap3.client.ui.Pagination;
import org.gwtbootstrap3.client.ui.Row;
import org.gwtbootstrap3.client.ui.constants.ColumnSize;
import org.gwtbootstrap3.client.ui.constants.ModalBackdrop;
import org.gwtbootstrap3.client.ui.constants.Styles;
import org.gwtbootstrap3.client.ui.gwt.CellTable;
import org.jboss.errai.security.shared.api.RoleImpl;
import org.jboss.errai.security.shared.api.identity.User;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;
import org.uberfire.ext.widgets.common.client.common.popups.footers.ModalFooterOKButton;

/**
 * The AuditLog View implementation
 */
public class AuditLogViewImpl extends BaseModal
        implements
        AuditLogView {

    private final AuditLog auditLog;

    /* The 500px width constant. */
    private static final String P500 = "500px";

    /**
     * The page size constant value.
     */
    private static final int PAGE_SIZE = 4;

    @UiField
    Row eventTypes;

    @UiField
    Row eventsContainer;

    @UiField
    Pagination cellTablePagination;

    private SimplePager pager = new SimplePager();

    private CellTable<AuditLogEntry> events;

    /**
     * Custom styles for audit log cell table.
     * <p/>
     * NOTE: BZ-996942
     */
    public interface AuditLogStyle extends CssResource {

        String eventTypesTitle();

        String eventsContainerInline();

        String auditLogDetailLabel();

        String auditLogDetailValue();
    }

    @UiField
    AuditLogStyle style;

    //The current user's security context (admins can see all records)
    private final User identity;

    interface AuditLogViewImplBinder
            extends
            UiBinder<Widget, AuditLogViewImpl> {

    }

    private static AuditLogViewImplBinder uiBinder = GWT.create( AuditLogViewImplBinder.class );

    public AuditLogViewImpl( final AuditLog auditLog,
                             final User identity ) {
        this.auditLog = auditLog;
        this.identity = identity;

        setTitle( GuidedDecisionTableConstants.INSTANCE.DecisionTableAuditLog() );
        setDataBackdrop( ModalBackdrop.STATIC );
        setDataKeyboard( true );
        setFade( true );
        setRemoveOnHide( true );
        //setMaxHeigth( P500 );
        setWidth( 1000 + "px" );

        setBody( uiBinder.createAndBindUi( AuditLogViewImpl.this ) );
        add( new ModalFooterOKButton( new Command() {
            @Override
            public void execute() {
                hide();
            }
        } ) );

        setup();
    }

    public void setup() {
        // BZ-996917: Use a the gwtboostrap style "row-fluid" to allow display some events in the same row.
        eventTypes.setStyleName( Styles.ROW );

        // Fill panel with available events.
        for ( Map.Entry<String, Boolean> e : auditLog.getAuditLogFilter().getAcceptedTypes().entrySet() ) {
            eventTypes.add( makeEventTypeCheckBox( e.getKey(),
                                                   e.getValue() ) );
        }

        // Create the GWT Cell Table as events container.
        // BZ-996942: Set custom width and table css style.
        events = new CellTable<AuditLogEntry>();
        events.setWidth( "100%" );
        events.addStyleName( Styles.TABLE );

        final ListDataProvider<AuditLogEntry> dlp = new ListDataProvider<AuditLogEntry>( filterDeletedEntries( auditLog ) );
        dlp.addDataDisplay( events );

        AuditLogEntrySummaryColumn summaryColumn = new AuditLogEntrySummaryColumn( style.auditLogDetailLabel(), style.auditLogDetailValue() );
        AuditLogEntryCommentColumn commentColumn = new AuditLogEntryCommentColumn();

        events.addColumn( summaryColumn );
        events.addColumn( commentColumn );

        events.setColumnWidth( summaryColumn,
                               60.0,
                               Unit.PCT );
        events.setColumnWidth( commentColumn,
                               40.0,
                               Unit.PCT );

        //If the current user is not an Administrator include the delete comment column
        if ( identity.getRoles().contains( new RoleImpl( AppRoles.ADMIN.getName() ) ) ) {

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
                                   35.0,
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

        events.addRangeChangeHandler( new RangeChangeEvent.Handler() {
            @Override
            public void onRangeChange( final RangeChangeEvent event ) {
                cellTablePagination.rebuild( pager );
            }
        } );

        cellTablePagination.rebuild( pager );

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
        chkEventType.setWordWrap( false );

        return new Column( ColumnSize.MD_2 ) {{
            add( chkEventType );
        }};
    }

    private List<AuditLogEntry> filterDeletedEntries( final List<AuditLogEntry> entries ) {
        if ( !identity.getRoles().contains( new RoleImpl( AppRoles.ADMIN.getName() ) ) ) {
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
