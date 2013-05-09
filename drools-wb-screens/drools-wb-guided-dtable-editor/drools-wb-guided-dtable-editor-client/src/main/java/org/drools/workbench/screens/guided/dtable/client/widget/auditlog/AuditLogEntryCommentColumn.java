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

import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextInputCell;
import com.google.gwt.user.cellview.client.Column;
import org.drools.guvnor.models.commons.shared.auditlog.AuditLogEntry;

/**
 * A column for Audit Log User comments
 */
public class AuditLogEntryCommentColumn extends Column<AuditLogEntry, String> {

    private static final TextInputCell cell = new TextInputCell();

    public AuditLogEntryCommentColumn() {
        super( cell );
        setFieldUpdater( new FieldUpdater<AuditLogEntry, String>() {

            @Override
            public void update( int index,
                                AuditLogEntry object,
                                String value ) {
                object.setUserComment( value );
            }

        } );
    }

    @Override
    public String getValue( AuditLogEntry object ) {
        return object.getUserComment();
    }

}
