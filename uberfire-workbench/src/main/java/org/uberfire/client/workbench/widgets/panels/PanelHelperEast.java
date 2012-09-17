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
import org.uberfire.client.workbench.WorkbenchPanelPresenter;
import org.uberfire.client.workbench.annotations.WorkbenchPosition;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Helper to add or remove WorkbenchPanels from the East of a
 * HorizontalSplitterPanel.
 */
@ApplicationScoped
@WorkbenchPosition(position = Position.EAST)
public class PanelHelperEast
    implements
    PanelHelper {

    @Inject
    private BeanFactory factory;

    @Override
    public void add(final WorkbenchPanelPresenter.View newPanel,
                    final WorkbenchPanelPresenter.View targetPanel,
                    final Integer preferredSize,
                    final Integer preferredMinSize) {

        final Widget parent = targetPanel.asWidget().getParent();

        if ( parent instanceof SimplePanel ) {

            final SimplePanel sp = (SimplePanel) parent;
            final HorizontalSplitterPanel hsp = factory.newHorizontalSplitterPanel( newPanel,
                                                                                    targetPanel,
                                                                                    Position.EAST,
                                                                                    preferredSize,
                                                                                    preferredMinSize );

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
    public void remove(WorkbenchPanelPresenter.View panel) {
        final HorizontalSplitterPanel vsp = (HorizontalSplitterPanel) panel.asWidget().getParent().getParent().getParent();
        final Widget parent = vsp.getParent();
        final Widget westWidget = vsp.getWidget( Position.WEST );

        vsp.clear();

        //Set parent's content to the WEST widget
        if ( parent instanceof SimplePanel ) {
            ((SimplePanel) parent).setWidget( westWidget );
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
