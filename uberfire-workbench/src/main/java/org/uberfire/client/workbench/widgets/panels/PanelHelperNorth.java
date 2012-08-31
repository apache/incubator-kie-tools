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
package org.uberfire.client.workbench.widgets.panels;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.uberfire.client.workbench.BeanFactory;
import org.uberfire.client.workbench.Position;
import org.uberfire.client.workbench.WorkbenchPanel;
import org.uberfire.client.workbench.annotations.WorkbenchPosition;
import org.uberfire.client.workbench.widgets.dnd.WorkbenchDragAndDropManager;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Helper to add or remove WorkbenchPanels from the North of a
 * VerticalSplitterPanel.
 */
@ApplicationScoped
@WorkbenchPosition(position = Position.NORTH)
public class PanelHelperNorth
    implements
    PanelHelper {

    @Inject
    private WorkbenchDragAndDropManager dndManager;

    @Inject
    private BeanFactory                 factory;

    public void add(final WorkbenchPanel.View newPanel,
                    final WorkbenchPanel.View targetPanel) {

        //TODO {manstis}
        //final Widget parent = targetPanel.getParent();

        //if ( parent instanceof SimplePanel ) {

        //    final SimplePanel sp = (SimplePanel) parent;
        //    dndManager.unregisterDropController( sp );

        //    final VerticalSplitterPanel vsp = factory.newVerticalSplitterPanel( newPanel,
        //                                                                        targetPanel,
        //                                                                        Position.NORTH );

        //    sp.clear();
        //    sp.setWidget( vsp );

            //Adding an additional embedded ScrollPanel can cause scroll-bars to disappear
            //so ensure we set the sizes of the new Panel and it's children after the 
            //browser has added the new DIVs to the HTML tree. This does occasionally
            //add slight flicker when adding a new Panel.
        //    scheduleResize( vsp );
        //}
    }

    @Override
    public void remove(WorkbenchPanel.View panel) {
        //TODO {manstis}
        //final VerticalSplitterPanel vsp = (VerticalSplitterPanel) panel.getParent().getParent().getParent();
        //final Widget parent = vsp.getParent();
        //final Widget northWidget = vsp.getWidget( Position.NORTH );
        //final Widget southWidget = vsp.getWidget( Position.SOUTH );

        //vsp.clear();

        //dndManager.unregisterDropController( (SimplePanel) northWidget.getParent() );
        //dndManager.unregisterDropController( (SimplePanel) southWidget.getParent() );

        //Set parent's content to the SOUTH widget
        //if ( parent instanceof SimplePanel ) {
        //    ((SimplePanel) parent).setWidget( southWidget );
        //    if ( southWidget instanceof WorkbenchPanel ) {
        //        final WorkbenchPanel wbp = (WorkbenchPanel) southWidget;
        //        dndManager.registerDropController( (SimplePanel) parent,
        //                                           factory.newDropController( wbp ) );
        //    }
        //}

        //if ( southWidget instanceof RequiresResize ) {
        //    scheduleResize( (RequiresResize) southWidget );
        //}
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
