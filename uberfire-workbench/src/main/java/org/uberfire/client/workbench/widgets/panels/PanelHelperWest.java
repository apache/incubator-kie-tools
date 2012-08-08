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
 * Helper to add or remove WorkbenchPanels from the West of a
 * HorizontalSplitterPanel.
 */
@ApplicationScoped
@WorkbenchPosition(position = Position.WEST)
public class PanelHelperWest
    implements
    PanelHelper {

    @Inject
    private WorkbenchDragAndDropManager dndManager;

    @Inject
    private BeanFactory                 factory;

    public void add(final WorkbenchPanel newPanel,
                    final WorkbenchPanel targetPanel) {

        final Widget parent = targetPanel.getParent();

        if ( parent instanceof SimplePanel ) {

            final SimplePanel sp = (SimplePanel) parent;
            dndManager.unregisterDropController( sp );

            final HorizontalSplitterPanel hsp = factory.newHorizontalSplitterPanel( newPanel,
                                                                                    targetPanel,
                                                                                    Position.WEST );

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

        dndManager.unregisterDropController( (SimplePanel) eastWidget.getParent() );
        dndManager.unregisterDropController( (SimplePanel) westWidget.getParent() );

        //Set parent's content to the EAST widget
        if ( parent instanceof SimplePanel ) {
            ((SimplePanel) parent).setWidget( eastWidget );
            if ( eastWidget instanceof WorkbenchPanel ) {
                final WorkbenchPanel wbp = (WorkbenchPanel) eastWidget;
                dndManager.registerDropController( (SimplePanel) parent,
                                                   factory.newDropController( wbp ) );
            }
        }

        if ( eastWidget instanceof RequiresResize ) {
            scheduleResize( (RequiresResize) eastWidget );
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
