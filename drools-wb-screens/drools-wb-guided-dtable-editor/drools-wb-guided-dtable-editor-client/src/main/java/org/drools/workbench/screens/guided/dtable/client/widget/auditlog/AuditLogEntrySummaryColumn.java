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

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.cellview.client.Column;
import org.drools.workbench.models.datamodel.auditlog.AuditLogEntry;
import org.guvnor.common.services.shared.config.ApplicationPreferences;

/**
 * A column for the Audit Log summary
 */
public class AuditLogEntrySummaryColumn extends Column<AuditLogEntry, AuditLogEntry> {

    private static final String DATE_TIME_FORMAT = ApplicationPreferences.getDroolsDateTimeFormat();

    private static final DateTimeFormat format = DateTimeFormat.getFormat( DATE_TIME_FORMAT );

    public AuditLogEntrySummaryColumn(String labelClass, String valueClass) {
        super( new AuditLogEntryCell( format, labelClass, valueClass ) );
    }

    public AuditLogEntrySummaryColumn() {
        super( new AuditLogEntryCell( format ) );
    }



    @Override
    public AuditLogEntry getValue( AuditLogEntry object ) {
        return object;
    }

}
