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
package org.uberfire.client.workbench.widgets.toolbar;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.PushButton;

/**
 * The Tool Bar widget
 */
public class WorkbenchToolBarView extends Composite
    implements
    WorkbenchToolBarPresenter.View {

    private final HorizontalPanel            toolBar           = new HorizontalPanel();
    private final HorizontalPanel            toolBarContainer  = new HorizontalPanel();

    //Map of ToolBarItems to GWT Widgets used to represent them
    private final Map<ToolBarItem, IsWidget> toolBarItemsMap   = new HashMap<ToolBarItem, IsWidget>();

    private static final String              STYLENAME_DEFAULT = "gwt-MenuBar";

    public WorkbenchToolBarView() {
        initWidget( toolBarContainer );
        toolBarContainer.setWidth( "100%" );
        toolBarContainer.setHeight( "32px" );
        toolBarContainer.setStyleName( STYLENAME_DEFAULT );
        toolBarContainer.addStyleDependentName( "horizontal" );
        toolBarContainer.add( toolBar );
    }

    /**
     * Add a Tool Bar item to the view. Filtering of menu items for permissions
     * is conducted by the Presenter.
     */
    @Override
    public void addToolBarItem(final ToolBarItem item) {
        //TODO {manstis} Image, enabled etc
        final PushButton icon = new PushButton( item.getTooltip() );
        if ( item.getCommand() != null ) {
            icon.addClickHandler( new ClickHandler() {

                @Override
                public void onClick(ClickEvent event) {
                    item.getCommand().execute();
                }

            } );
        }
        icon.setEnabled( item.isEnabled() );
        toolBarItemsMap.put( item,
                             icon );
        toolBar.add( icon );
    }

    /**
     * Remove a Tool Bar item from the view.
     */
    @Override
    public void removeToolBarItem(final ToolBarItem item) {
        final IsWidget icon = toolBarItemsMap.remove( item );
        if ( icon != null ) {
            toolBar.remove( icon );
        }
    }

}
