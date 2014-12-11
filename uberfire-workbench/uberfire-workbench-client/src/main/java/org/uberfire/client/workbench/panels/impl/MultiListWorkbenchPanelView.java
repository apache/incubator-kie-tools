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

import org.uberfire.client.workbench.panels.MultiPartWidget;
import org.uberfire.client.workbench.widgets.listbar.ListBarWidget;
import org.uberfire.client.workbench.widgets.panel.MaximizeToggleButton;
import org.uberfire.mvp.Command;

/**
 * A Workbench panel that can contain WorkbenchParts.
 */
@Dependent
@Named("MultiListWorkbenchPanelView")
public class MultiListWorkbenchPanelView
extends AbstractMultiPartWorkbenchPanelView<MultiListWorkbenchPanelPresenter> {

    @Inject
    protected ListBarWidget listBar;

    @Override
    protected MultiPartWidget setupWidget() {
        if ( contextWidget != null ) {
            listBar.setExpanderCommand( new Command() {
                @Override
                public void execute() {
                    contextWidget.toogleDisplay();
                }
            } );
        }
        addOnFocusHandler( listBar );
        addSelectionHandler( listBar );

        final MaximizeToggleButton maximizeButton = listBar.getMaximizeButton();
        maximizeButton.setVisible( true );
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

        return listBar;
    }

    @Override
    public void maximize() {
        super.maximize();
        listBar.getMaximizeButton().setMaximized( true );
    }

    @Override
    public void unmaximize() {
        super.unmaximize();
        listBar.getMaximizeButton().setMaximized( false );
    }
    
    @Override
    public void setElementId( String elementId ) {
        super.setElementId( elementId );
        listBar.getMaximizeButton().ensureDebugId( elementId + "-maximizeButton" );
    }
}
