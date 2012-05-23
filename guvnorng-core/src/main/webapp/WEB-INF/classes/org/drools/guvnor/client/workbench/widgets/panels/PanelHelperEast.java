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
import org.drools.guvnor.client.workbench.WorkbenchPart;
import org.drools.guvnor.client.workbench.widgets.dnd.CompassDropController;
import org.drools.guvnor.client.workbench.widgets.dnd.WorkbenchDragAndDropManager;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 */
public class PanelHelperEast
    implements
    PanelHelper {

    public void add(final WorkbenchPart part,
                    final WorkbenchPanel panel) {

        final Widget parent = panel.getParent();

        if ( parent instanceof SimplePanel ) {

            final SimplePanel sp = (SimplePanel) parent;
            WorkbenchDragAndDropManager.getInstance().unregisterDropController( sp );

            final HorizontalSplitterPanel hsp = new HorizontalSplitterPanel( panel,
                                                                             part,
                                                                             Position.EAST );
            sp.clear();
            sp.setWidget( hsp );

            //Adding an additional embedded ScrollPanel can cause scroll-bars to disappear
            //so ensure we set the sizes of the new Panel and it's children after the 
            //browser has added the new DIVs to the HTML tree. This does occasionally
            //add slight flicker when adding a new Panel.
            scheduleResize( hsp );
        }
    }

    @Override
    public void remove(WorkbenchPanel panel) {
        final HorizontalSplitterPanel vsp = (HorizontalSplitterPanel) panel.getParent().getParent().getParent();
        final Widget parent = vsp.getParent();
        final Widget eastWidget = vsp.getWidget( Position.EAST );
        final Widget westWidget = vsp.getWidget( Position.WEST );

        vsp.clear();

        WorkbenchDragAndDropManager.getInstance().unregisterDropController( (SimplePanel) eastWidget.getParent() );
        WorkbenchDragAndDropManager.getInstance().unregisterDropController( (SimplePanel) westWidget.getParent() );

        //Set parent's content to the WEST widget
        if ( parent instanceof SimplePanel ) {
            ((SimplePanel) parent).setWidget( westWidget );
            if ( westWidget instanceof WorkbenchPanel ) {
                final WorkbenchPanel wbp = (WorkbenchPanel) westWidget;
                WorkbenchDragAndDropManager.getInstance().registerDropController( (SimplePanel) parent,
                                                                                  new CompassDropController( wbp ) );
            }
        }

        if ( westWidget instanceof RequiresResize ) {
            scheduleResize( (RequiresResize) westWidget );
        }
    }

    private void scheduleResize(final RequiresResize widget) {
        Scheduler.get().scheduleDeferred( new ScheduledCommand() {

            @Override
            public void execute() {
                widget.onResize();
            }

        } );
    }

}
