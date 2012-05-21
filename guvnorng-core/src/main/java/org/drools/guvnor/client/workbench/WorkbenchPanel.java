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
package org.drools.guvnor.client.workbench;

import org.drools.guvnor.client.workbench.events.FocusReceivedEvent;
import org.drools.guvnor.client.workbench.widgets.panels.ResizableSplitLayoutPanel;
import org.drools.guvnor.client.workbench.widgets.panels.SplitPanel;
import org.drools.guvnor.client.workbench.widgets.panels.tabpanel.WorkbenchTabPanel;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 */
public class WorkbenchPanel extends ResizeComposite {

    private final WorkbenchTabPanel tabPanel;

    private final EventBus          eventBus;

    public WorkbenchPanel(final EventBus eventBus,
                          final Widget content,
                          final String title) {
        this.eventBus = eventBus;
        this.tabPanel = makeTabPanel( content,
                                      title );
        initWidget( tabPanel );
    }

    private WorkbenchTabPanel makeTabPanel(final Widget content,
                                           final String title) {
        final WorkbenchTabPanel tabPanel = new WorkbenchTabPanel( eventBus,
                                                                  this );
        tabPanel.add( content,
                      title );

        //Clicking on the TabPanel takes focus
        tabPanel.addDomHandler( new ClickHandler() {

                                    @Override
                                    public void onClick(ClickEvent event) {
                                        setFocus( true );
                                    }

                                },
                                 ClickEvent.getType() );

        tabPanel.selectTab( 0 );

        return tabPanel;
    }

    public void remove() {
        //TODO {manstis} This is a little brittle, should the DOM hierarchy change...
        //- we just call parent.remove() and that calls up, and calls up.. etc until something handles it?
        //- we pass references to the actual "containing" class in the constructors?
        //
        //Parent should be a SimplePanel (or subclass, e.g. ScrollPanel). This SimplePanel
        //could be embedded in a SplitPanel. In which case removal is a little more complex.
        Widget parent0 = getParent();
        if ( parent0 instanceof SimplePanel ) {
            Widget parent1 = parent0.getParent();
            if ( parent1 instanceof ResizableSplitLayoutPanel ) {
                Widget parent2 = parent1.getParent();
                if ( parent2 instanceof SplitPanel ) {
                    ((SplitPanel) parent2).remove( this );
                }
            } else {
                ((SimplePanel) parent0).remove( this );
            }
        }
    }

    public void setFocus(final boolean isFocused) {
        eventBus.fireEvent( new FocusReceivedEvent( this ) );
    }

    public void addTab(final Widget content,
                       final String title) {
        tabPanel.add( content,
                        title );
        tabPanel.selectTab( tabPanel.getTabBar().getTabCount() - 1 );
    }

}
