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
package org.uberfire.client.workbench.pmgr.nswe.panels.support;

import org.uberfire.client.workbench.BeanFactory;
import org.uberfire.client.workbench.panels.SplitPanel;
import org.uberfire.client.workbench.panels.WorkbenchPanelView;
import org.uberfire.workbench.model.CompassPosition;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Helper to add or remove WorkbenchPanels from the East of a
 * HorizontalSplitterPanel.
 */
public class PanelHelperEast extends AbstractPanelHelper {

    protected PanelHelperEast( BeanFactory factory ) {
        super( factory );
    }

    @Override
    public void remove( final WorkbenchPanelView panel ) {
        final SplitPanel vsp = (SplitPanel) panel.asWidget().getParent().getParent().getParent();
        final Widget parent = vsp.getParent();
        final Widget westWidget = vsp.getWidget( CompassPosition.WEST );

        vsp.clear();

        //Set parent's content to the WEST widget
        if ( parent instanceof SimplePanel ) {
            ( (SimplePanel) parent ).setWidget( westWidget );
        }

        if ( westWidget instanceof RequiresResize ) {
            scheduleResize( (RequiresResize) westWidget );
        }
    }

    private void scheduleResize( final RequiresResize widget ) {
        Scheduler.get().scheduleDeferred( new ScheduledCommand() {

            @Override
            public void execute() {
                widget.onResize();
            }

        } );
    }

}
