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

import org.uberfire.client.workbench.panels.MultiPartWidget;
import org.uberfire.client.workbench.widgets.tab.UberTabPanel;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.model.PartDefinition;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;

/**
 * A Workbench panel that can contain WorkbenchParts.
 */
@Dependent
@Named("MultiTabWorkbenchPanelView")
public class MultiTabWorkbenchPanelView
extends BaseMultiPartWorkbenchPanelView<MultiTabWorkbenchPanelPresenter> {

    @Override
    protected MultiPartWidget setupWidget() {
        final UberTabPanel tabPanel = getUberTabPanel();

        Style tabPanelStyle = tabPanel.getElement().getStyle();
        tabPanelStyle.setPosition( com.google.gwt.dom.client.Style.Position.ABSOLUTE );
        tabPanelStyle.setTop( 0, Unit.PX );
        tabPanelStyle.setBottom( 0, Unit.PX );
        tabPanelStyle.setLeft( 0, Unit.PX );
        tabPanelStyle.setRight( 0, Unit.PX );

        //When a tab is selected ensure content is resized and set focus
        tabPanel.addSelectionHandler( new SelectionHandler<PartDefinition>() {
            @Override
            public void onSelection( SelectionEvent<PartDefinition> event ) {
                panelManager.onPartLostFocus();
                panelManager.onPartFocus( event.getSelectedItem() );
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
    UberTabPanel getUberTabPanel() {
        return new UberTabPanel( panelManager );
    }
}
