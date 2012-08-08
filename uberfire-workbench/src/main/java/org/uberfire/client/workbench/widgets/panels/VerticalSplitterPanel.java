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
package org.uberfire.client.workbench.widgets.panels;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.uberfire.client.workbench.BeanFactory;
import org.uberfire.client.workbench.Position;
import org.uberfire.client.workbench.WorkbenchPanel;
import org.uberfire.client.workbench.widgets.dnd.WorkbenchDragAndDropManager;

import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * A split panel to contain WorkbenchPanels split vertically.
 */
@Dependent
public class VerticalSplitterPanel extends ResizeComposite
    implements
    SplitPanel {

    @Inject
    private WorkbenchDragAndDropManager     dndManager;

    @Inject
    private BeanFactory                     factory;

    private final WorkbenchSplitLayoutPanel slp                  = new WorkbenchSplitLayoutPanel();
    private final SimpleLayoutPanel         northWidgetContainer = new SimpleLayoutPanel();
    private final SimpleLayoutPanel         southWidgetContainer = new SimpleLayoutPanel();

    public VerticalSplitterPanel() {
        initWidget( slp );
    }

    public void setup(final WorkbenchPanel northWidget,
                      final WorkbenchPanel southWidget,
                      final Position position) {
        switch ( position ) {
            case NORTH :
                slp.addNorth( northWidgetContainer,
                              INITIAL_SIZE );
                slp.add( southWidgetContainer );
                break;
            case SOUTH :
                slp.addSouth( southWidgetContainer,
                              INITIAL_SIZE );
                slp.add( northWidgetContainer );
                break;
            default :
                throw new IllegalArgumentException( "position must be either NORTH or SOUTH" );
        }
        slp.setWidgetMinSize( northWidgetContainer,
                              MIN_SIZE );
        slp.setWidgetMinSize( southWidgetContainer,
                              MIN_SIZE );

        northWidgetContainer.setWidget( northWidget );
        southWidgetContainer.setWidget( southWidget );

        //Wire-up DnD controllers
        dndManager.registerDropController( northWidgetContainer,
                                           factory.newDropController( northWidget ) );
        dndManager.registerDropController( southWidgetContainer,
                                           factory.newDropController( southWidget ) );
    }

    @Override
    public void clear() {
        this.slp.clear();
    }

    @Override
    public Widget getWidget(Position position) {
        switch ( position ) {
            case NORTH :
                return this.northWidgetContainer.getWidget();
            case SOUTH :
                return this.southWidgetContainer.getWidget();
            default :
                throw new IllegalArgumentException( "position must be either NORTH or SOUTH" );
        }
    }

    @Override
    public void onResize() {
        final Widget parent = getParent();
        setPixelSize( parent.getOffsetWidth(),
                      parent.getOffsetHeight() );
        super.onResize();
    }

}
