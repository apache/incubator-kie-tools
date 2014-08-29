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
import org.uberfire.client.workbench.panels.WorkbenchPanelView;
import org.uberfire.client.workbench.pmgr.nswe.panels.impl.HorizontalSplitterPanel;
import org.uberfire.workbench.model.CompassPosition;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Helper to add or remove WorkbenchPanels from the West of a
 * HorizontalSplitterPanel.
 */
public class PanelHelperWest extends AbstractPanelHelper {

    protected PanelHelperWest( BeanFactory factory ) {
        super( factory );
    }

    @Override
    public void remove( final WorkbenchPanelView panel ) {
        final HorizontalSplitterPanel vsp = (HorizontalSplitterPanel) panel.asWidget().getParent().getParent().getParent();
        final Widget parent = vsp.getParent();
        final Widget eastWidget = vsp.getWidget( CompassPosition.EAST );

        vsp.clear();

        //Set parent's content to the EAST widget
        if ( parent instanceof SimplePanel ) {
            ( (SimplePanel) parent ).setWidget( eastWidget );
        }

        if ( eastWidget instanceof RequiresResize ) {
            scheduleResize( (RequiresResize) eastWidget );
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
