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

import com.google.gwt.cell.client.ImageResourceCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.Window;
import org.drools.workbench.models.commons.shared.auditlog.AuditLogEntry;
import org.drools.workbench.screens.guided.dtable.client.resources.Resources;
import org.drools.workbench.screens.guided.dtable.client.resources.i18n.Constants;

import java.util.HashSet;
import java.util.Set;

/**
 * A column showing an icon to delete an AuditLogEntry
 */
public class AuditLogEntryDeleteCommentColumn extends Column<AuditLogEntry, ImageResource> {

    private static final ImageResourceCell cell = new ImageResourceCell() {

        public Set<String> getConsumedEvents() {
            HashSet<String> events = new HashSet<String>();
            events.add( "click" );
            return events;
        }

        @Override
        public void onBrowserEvent( Context context,
                                    Element parent,
                                    ImageResource value,
                                    NativeEvent event,
                                    ValueUpdater<ImageResource> valueUpdater ) {
            super.onBrowserEvent( context,
                                  parent,
                                  value,
                                  event,
                                  valueUpdater );
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
                                       ImageResource value,
                                       NativeEvent event,
                                       ValueUpdater<ImageResource> valueUpdater ) {
            if ( valueUpdater != null ) {
                if ( Window.confirm( Constants.INSTANCE.AreYouSureYouWantToRemoveThisItem() ) ) {
                    valueUpdater.update( image );
                }
            }
        }

    };

    private static final ImageResource image = Resources.INSTANCE.images().deleteItemSmall();

    public AuditLogEntryDeleteCommentColumn() {
        super( cell );
    }

    @Override
    public ImageResource getValue( AuditLogEntry object ) {
        return image;
    }

}
