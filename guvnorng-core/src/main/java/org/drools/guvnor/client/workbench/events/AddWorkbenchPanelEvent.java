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

import org.drools.guvnor.client.workbench.PositionSelectorPopup.Position;
import org.drools.guvnor.client.workbench.WorkbenchPanel;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 */
public class AddWorkbenchPanelEvent extends GwtEvent<AddWorkbenchPanelEvent.AddWorkbenchPanelEventHandler> {

    public static final Type<AddWorkbenchPanelEventHandler> TYPE = new Type<AddWorkbenchPanelEventHandler>();

    public interface AddWorkbenchPanelEventHandler
        extends
        EventHandler {

        public void onAddWorkbenchPanel(final AddWorkbenchPanelEvent event);

    }

    private final String         title;

    private final WorkbenchPanel target;

    private final Position       position;

    private final Widget         widget;

    public AddWorkbenchPanelEvent(final String title,
                                  final WorkbenchPanel target,
                                  final Position position,
                                  final Widget widget) {
        this.title = title;
        this.target = target;
        this.position = position;
        this.widget = widget;
    }

    public String getTitle() {
        return this.title;
    }

    public WorkbenchPanel getTarget() {
        return this.target;
    }

    public Position getPosition() {
        return this.position;
    }

    public Widget getWidget() {
        return this.widget;
    }

    @Override
    public Type<AddWorkbenchPanelEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(AddWorkbenchPanelEventHandler handler) {
        handler.onAddWorkbenchPanel( this );
    }

}
