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

import org.drools.guvnor.client.workbench.widgets.panels.PanelManager;
import org.drools.guvnor.client.workbench.widgets.panels.tabpanel.WorkbenchTabPanel;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 */
public class WorkbenchPanel extends ResizeComposite {

    //TODO Move the tab panel out of the WorkbenchPanel
    private final WorkbenchTabPanel tabPanel;

    public WorkbenchPanel() {
        this.tabPanel = makeTabPanel();
        initWidget( tabPanel );
    }

    public WorkbenchPanel(final Widget widget,
                          final String title) {
        this();
        addTab( widget,
                title );
    }

    public void setFocus(boolean hasFocus) {
        this.tabPanel.setFocus( hasFocus );
    }

    private WorkbenchTabPanel makeTabPanel() {
        final WorkbenchTabPanel tabPanel = new WorkbenchTabPanel();

        //Clicking on the TabPanel takes focus
        tabPanel.addDomHandler( new ClickHandler() {

                                    @Override
                                    public void onClick(ClickEvent event) {
                                        PanelManager.getInstance().setFocus( WorkbenchPanel.this );
                                    }

                                },
                                 ClickEvent.getType() );
        return tabPanel;
    }

    public void addTab(final Widget content,
                       final String title) {
        tabPanel.add( content,
                        title );
        tabPanel.selectTab( tabPanel.getTabBar().getTabCount() - 1 );
    }

}
