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
import javax.inject.Inject;
import javax.inject.Named;

import org.uberfire.client.util.Layouts;
import org.uberfire.client.workbench.panels.MultiPartWidget;
import org.uberfire.client.workbench.widgets.panel.MaximizeToggleButton;
import org.uberfire.client.workbench.widgets.tab.UberTabPanel;
import org.uberfire.mvp.Command;

import com.github.gwtbootstrap.client.ui.ButtonGroup;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;

/**
 * A Workbench panel that can contain WorkbenchParts.
 */
@Dependent
@Named("MultiTabWorkbenchPanelView")
public class MultiTabWorkbenchPanelView
extends AbstractMultiPartWorkbenchPanelView<MultiTabWorkbenchPanelPresenter> {

    @Inject
    MaximizeToggleButton maximizeButton;

    @Inject
    UberTabPanel tabPanel;

    @Override
    protected MultiPartWidget setupWidget() {
        maximizeButton.setMaximizeCommand( new Command() {
            @Override
            public void execute() {
                maximize();
            }
        } );
        maximizeButton.setUnmaximizeCommand( new Command() {
            @Override
            public void execute() {
                unmaximize();
            }
        } );

        Layouts.setToFillParent( tabPanel );
        addOnFocusHandler( tabPanel );
        addSelectionHandler( tabPanel );
        return tabPanel;
    }

    @Override
    protected void populatePartViewContainer() {

        ButtonGroup headerButtonGroup = new ButtonGroup( maximizeButton );

        // MAINTENANCE WARNING: these magic numbers should agree with the separately defined styles of ListBarWidget.ui.xml
        Style buttonGroupStyle = headerButtonGroup.getElement().getStyle();
        buttonGroupStyle.setMarginRight( 10, Unit.PX );
        buttonGroupStyle.setMarginTop( 5, Unit.PX );
        buttonGroupStyle.setZIndex( 2 ); // otherwise, clicks don't make it through the tab area

        // line up against right-hand side of the tab area
        buttonGroupStyle.setPosition( Position.ABSOLUTE );
        buttonGroupStyle.setRight( 0, Unit.PX );

        getPartViewContainer().add( headerButtonGroup );

        super.populatePartViewContainer();
    }

    @Override
    public void maximize() {
        super.maximize();
        maximizeButton.setMaximized( true );
    }

    @Override
    public void unmaximize() {
        super.unmaximize();
        maximizeButton.setMaximized( false );
    }

    @Override
    public void setElementId( String elementId ) {
        super.setElementId( elementId );
        maximizeButton.ensureDebugId( elementId + "-maximizeButton" );
    }
}
