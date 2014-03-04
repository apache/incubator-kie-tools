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

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import org.drools.workbench.models.datamodel.auditlog.AuditLogEntry;
import org.drools.workbench.screens.guided.dtable.client.resources.i18n.GuidedDecisionTableConstants;

/**
 * A cell to render AuditLogEntry's
 */
@SuppressWarnings("unused")
public class AuditLogEntryCell extends AbstractCell<AuditLogEntry> {

    interface Template
            extends
            SafeHtmlTemplates {

        @Template("<div class=\"auditLogSummary\"><b>{0}</b></div><div class=\"auditLogDetailValue\">{1}</div>")
        SafeHtml entrySummary( String eventTypeDisplayText,
                               String whoWhenDisplayText );
    }

    private static final Template TEMPLATE = GWT.create( Template.class );

    private final DateTimeFormat format;

    private final AuditLogEntryCellHelper helper;

    public AuditLogEntryCell( final DateTimeFormat format ) {
        this.helper = new AuditLogEntryCellHelper( format );
        this.format = format;
    }

    public AuditLogEntryCell( final DateTimeFormat format, String labelClass, String valueClass ) {
        this.helper = new AuditLogEntryCellHelper( format, labelClass, valueClass );
        this.format = format;
    }

    @Override
    public void render( Context context,
                        AuditLogEntry value,
                        SafeHtmlBuilder sb ) {
        if ( value == null ) {
            return;
        }

        //Audit Log entry type and date
        final String eventTypeDisplayText = AuditLogEntryCellHelper.getEventTypeDisplayText( value.getGenericType() );
        final String whenWhoDisplayText = GuidedDecisionTableConstants.INSTANCE.AuditLogEntryOn1( format.format( value.getDateOfEntry() ),
                                                                               value.getUserName() );
        sb.append( TEMPLATE.entrySummary( eventTypeDisplayText,
                                          whenWhoDisplayText ) );

        //Audit Log entry detail
        sb.append( helper.getSafeHtml( value ) );
    }

}
