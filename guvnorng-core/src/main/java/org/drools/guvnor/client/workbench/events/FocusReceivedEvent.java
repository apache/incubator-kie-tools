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
package org.drools.guvnor.client.workbench.events;

import org.drools.guvnor.client.workbench.WorkbenchPanel;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

/**
 * 
 */
public class FocusReceivedEvent extends GwtEvent<FocusReceivedEvent.FocusReceivedEventHandler> {

    public static final Type<FocusReceivedEventHandler> TYPE = new Type<FocusReceivedEventHandler>();

    public interface FocusReceivedEventHandler
        extends
        EventHandler {

        public void onFocusReceived(final FocusReceivedEvent event);

    }

    private final WorkbenchPanel workbenchPanel;

    public FocusReceivedEvent(final WorkbenchPanel workbenchPanel) {
        this.workbenchPanel = workbenchPanel;
    }

    public WorkbenchPanel getWorkbenchPanel() {
        return workbenchPanel;
    }

    @Override
    public Type<FocusReceivedEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(FocusReceivedEventHandler handler) {
        handler.onFocusReceived( this );
    }

}
