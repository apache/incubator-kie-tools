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
package org.uberfire.client.workbench.panels.impl;

import javax.enterprise.context.Dependent;
import javax.inject.Named;

import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import org.uberfire.client.workbench.panels.MultiPartWidget;
import org.uberfire.client.workbench.widgets.tab.UberTabPanel;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.model.PartDefinition;

/**
 * A Workbench panel that can contain WorkbenchParts.
 */
@Dependent
@Named("MultiTabWorkbenchPanelView")
public class MultiTabWorkbenchPanelView
        extends BaseMultiPartWorkbenchPanelView<MultiTabWorkbenchPanelPresenter> {

    @Override
    protected MultiPartWidget setupWidget() {
        final UberTabPanel tabPanel = new UberTabPanel();

//        //Selecting a tab causes the previously selected tab to receive a Lost Focus event
//        widget.addBeforeSelectionHandler( new BeforeSelectionHandler<PartDefinition>() {
//            @Override
//            public void onBeforeSelection( final BeforeSelectionEvent<PartDefinition> event ) {
//
//            }
//        } );

        //When a tab is selected ensure content is resized and set focus
        tabPanel.addSelectionHandler( new SelectionHandler<PartDefinition>() {
            @Override
            public void onSelection( SelectionEvent<PartDefinition> event ) {
                presenter.onPartLostFocus();
                presenter.onPartFocus( event.getSelectedItem() );
            }
        } );

        tabPanel.addOnFocusHandler( new Command() {
            @Override
            public void execute() {
                panelManager.onPanelFocus( presenter.getDefinition() );
            }
        } );

        return tabPanel;
    }
}
