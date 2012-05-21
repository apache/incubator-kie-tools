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
package org.drools.guvnor.client.workbench.widgets.panels;

import org.drools.guvnor.client.workbench.PositionSelectorPopup.Position;
import org.drools.guvnor.client.workbench.WorkbenchPanel;
import org.drools.guvnor.client.workbench.widgets.dnd.WorkbenchDragAndDropManager;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 */
public class PanelHelperSouth
    implements
    PanelHelper {

    private final EventBus eventBus;

    public PanelHelperSouth(final EventBus eventBus) {
        this.eventBus = eventBus;
    }

    public void add(final String title,
                    final WorkbenchPanel target,
                    final Widget content) {

        final Widget parent = target.getParent();

        if ( parent instanceof SimplePanel ) {
            final WorkbenchPanel wbp = new WorkbenchPanel( eventBus,
                                                           content,
                                                           title );
            add( (SimplePanel) parent,
                 target,
                 wbp );
        }
    }

    private void add(final SimplePanel parent,
                     final WorkbenchPanel target,
                     final WorkbenchPanel panelToAdd) {
        WorkbenchDragAndDropManager.getInstance().unregisterDropController( parent );

        final VerticalSplitterPanel vsp = new VerticalSplitterPanel( eventBus,
                                                                     target,
                                                                     panelToAdd,
                                                                     Position.SOUTH );

        parent.clear();
        parent.setWidget( vsp );

        //Adding an additional embedded ScrollPanel can cause scroll-bars to disappear
        //so ensure we set the sizes of the new Panel and it's children after the 
        //browser has added the new DIVs to the HTML tree. This does occasionally
        //add slight flicker when adding a new Panel.
        Scheduler.get().scheduleDeferred( new ScheduledCommand() {

            @Override
            public void execute() {
                vsp.onResize();
            }

        } );

    }

}
