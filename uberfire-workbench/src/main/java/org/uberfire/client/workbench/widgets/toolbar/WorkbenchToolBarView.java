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

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.ButtonGroup;
import com.github.gwtbootstrap.client.ui.ButtonToolbar;
import com.github.gwtbootstrap.client.ui.Tooltip;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.github.gwtbootstrap.client.ui.constants.Placement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import org.uberfire.client.resources.WorkbenchResources;

/**
 * The Tool Bar widget
 */
public class WorkbenchToolBarView extends Composite
        implements
        WorkbenchToolBarPresenter.View {

    private final ButtonToolbar toolBar = new ButtonToolbar();

    //Map of ToolBar to GWT Widgets used to represent them
    private final Map<String, ButtonGroup> toolBarItemsMap = new HashMap<String, ButtonGroup>();

    public WorkbenchToolBarView() {
        toolBar.addStyleName( WorkbenchResources.INSTANCE.CSS().toolbar() );
        initWidget( toolBar );
    }

    /**
     * Add a Tool Bar item to the view. Filtering of menu items for permissions
     * is conducted by the Presenter.
     */
    @Override
    public void addToolBar( final ToolBar _toolBar ) {

        final ButtonGroup bgroup = new ButtonGroup();

        for ( final ToolBarItem item : _toolBar.getItems() ) {
            bgroup.add( new Tooltip( item.getTooltip() ) {{
                setPlacement( Placement.BOTTOM );
                add( new Button() {{
                    setIcon( IconType.valueOf( ( (ToolBarTypeIcon) item.getIcon() ).getType().toString() ) );
                    setEnabled( item.isEnabled() );
                    addClickHandler( new ClickHandler() {
                        @Override
                        public void onClick( final ClickEvent event ) {
                            item.getCommand().execute();
                        }
                    } );
                }} );
            }} );
        }

        toolBarItemsMap.put( _toolBar.getId(), bgroup );

        toolBar.add( bgroup );
    }

    /**
     * Remove a Tool Bar item from the view.
     */
    @Override
    public void removeToolBar( final ToolBar _toolBar ) {
        toolBar.remove( toolBarItemsMap.remove( _toolBar.getId() ) );
    }

}
