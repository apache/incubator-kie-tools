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

import java.util.HashSet;
import java.util.Set;

import com.google.gwt.cell.client.ImageResourceCell;
import com.google.gwt.cell.client.SafeHtmlCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.Window;
import org.drools.workbench.models.datamodel.auditlog.AuditLogEntry;
import org.drools.workbench.screens.guided.dtable.client.resources.GuidedDecisionTableResources;
import org.drools.workbench.screens.guided.dtable.client.resources.i18n.GuidedDecisionTableConstants;

/**
 * A column showing an icon to delete an AuditLogEntry.
 *
 * NOTE: BZ-996942: Replaced old icon for a bootstrap trash icon.
 */
public class AuditLogEntryDeleteCommentColumn extends Column<AuditLogEntry, SafeHtml> {

    /** BZ-996942: Replaced old icon for the icon trash (using bootstrap style icon-trash). */
    private static final SafeHtml safeHtml = SafeHtmlUtils.fromTrustedString("<div style=\"cursor: pointer\" class=\"icon-trash\"></div>");

    private static final SafeHtmlCell cell = new SafeHtmlCell() {

        public Set<String> getConsumedEvents() {
            HashSet<String> events = new HashSet<String>();
            events.add( "click" );
            return events;
        }

        @Override
        public void onBrowserEvent(Context context, Element parent, SafeHtml value,
                                   NativeEvent event, ValueUpdater<SafeHtml> valueUpdater ) {

            super.onBrowserEvent( context, parent, value,
                    event, valueUpdater );

            if ( "click".equals( event.getType() ) ) {
                onEnterKeyDown( context,
                                parent,
                                value,
                                event,
                                valueUpdater );
            }

        }

        @Override
        protected void onEnterKeyDown( Context context,
                                       Element parent,
                                       SafeHtml value,
                                       NativeEvent event,
                                       ValueUpdater<SafeHtml> valueUpdater ) {
            if ( valueUpdater != null ) {
                if ( Window.confirm( GuidedDecisionTableConstants.INSTANCE.AreYouSureYouWantToRemoveThisItem() ) ) {
                    valueUpdater.update( safeHtml );
                }
            }
        }

    };

    public AuditLogEntryDeleteCommentColumn() {
        super( cell );
    }

    @Override
    public SafeHtml getValue( AuditLogEntry object ) {
        return safeHtml;
    }

}
