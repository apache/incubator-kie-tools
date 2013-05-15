/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.projecteditor.client.forms;

import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.kie.workbench.projecteditor.client.resources.i18n.ProjectEditorConstants;
import org.kie.workbench.projecteditor.client.widgets.ListFormComboPanelViewImpl;
import org.uberfire.client.common.BusyPopup;
import org.uberfire.client.workbench.widgets.events.NotificationEvent;

public class KModuleEditorPanelViewImpl
        extends ListFormComboPanelViewImpl
        implements KModuleEditorPanelView {

    private final Event<NotificationEvent> notificationEvent;

    @Inject
    public KModuleEditorPanelViewImpl( Event<NotificationEvent> notificationEvent ) {
        super();
        setListTitle( ProjectEditorConstants.INSTANCE.KBases() );
        this.notificationEvent = notificationEvent;
    }

    @Override
    public void showSaveSuccessful( String fileName ) {
        notificationEvent.fire( new NotificationEvent( ProjectEditorConstants.INSTANCE.SaveSuccessful( fileName ) ) );
    }

    @Override
    public void showBusyIndicator( final String message ) {
        BusyPopup.showMessage( message );
    }

    @Override
    public void hideBusyIndicator() {
        BusyPopup.close();
    }

}
