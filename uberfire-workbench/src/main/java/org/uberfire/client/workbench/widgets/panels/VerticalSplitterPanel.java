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

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.Position;

/**
 * A split panel to contain WorkbenchPanels split vertically.
 */
@Dependent
public class VerticalSplitterPanel extends ResizeComposite
        implements
        SplitPanel {

    private final WorkbenchSplitLayoutPanel slp = new WorkbenchSplitLayoutPanel();
    private final SimpleLayoutPanel northWidgetContainer = new SimpleLayoutPanel();
    private final SimpleLayoutPanel southWidgetContainer = new SimpleLayoutPanel();

    public VerticalSplitterPanel() {
        initWidget( slp );
    }

    @Override
    public void setup( final WorkbenchPanelView northWidget,
                       final WorkbenchPanelView southWidget,
                       final Position position,
                       final Integer preferredSize,
                       final Integer preferredMinSize ) {

        final int size = assertSize( preferredSize );
        final int minSize = assertMinimumSize( preferredMinSize );

        switch ( position ) {
            case NORTH:
                int northChildSize = getChildSize( northWidget.getPresenter().getDefinition() );
                slp.addNorth( northWidgetContainer,
                              size + northChildSize );
                slp.add( southWidgetContainer );
                break;
            case SOUTH:
                int southChildSize = getChildSize( southWidget.getPresenter().getDefinition() );
                slp.addSouth( southWidgetContainer,
                              size + southChildSize );
                slp.add( northWidgetContainer );
                break;
            default:
                throw new IllegalArgumentException( "position must be either NORTH or SOUTH" );
        }
        slp.setWidgetMinSize( northWidgetContainer,
                              minSize );
        slp.setWidgetMinSize( southWidgetContainer,
                              minSize );

        northWidgetContainer.setWidget( northWidget );
        southWidgetContainer.setWidget( southWidget );
        scheduleResize( slp );
    }

    @Override
    public void clear() {
        this.slp.clear();
    }

    @Override
    public Widget getWidget( Position position ) {
        switch ( position ) {
            case NORTH:
                return this.northWidgetContainer.getWidget();
            case SOUTH:
                return this.southWidgetContainer.getWidget();
            default:
                throw new IllegalArgumentException( "position must be either NORTH or SOUTH" );
        }
    }

    @Override
    public void onResize() {
        //It is possible that the SplitterPanel is removed from the DOM before the resize is called
        if ( isAttached() ) {
            final Widget parent = getParent();
            setPixelSize( parent.getOffsetWidth(),
                          parent.getOffsetHeight() );
            super.onResize();
        }
    }

    private int assertSize( final Integer size ) {
        return ( size == null ? DEFAULT_SIZE : size );
    }

    private int assertMinimumSize( final Integer minSize ) {
        return ( minSize == null ? DEFAULT_MIN_SIZE : minSize );
    }

    private int getChildSize( final PanelDefinition panel ) {
        int childSize = 0;
        final PanelDefinition northPanel = panel.getChild( Position.NORTH );
        final PanelDefinition southPanel = panel.getChild( Position.SOUTH );
        if ( northPanel != null ) {
            childSize = childSize + assertSize( northPanel.getHeight() ) + getChildSize( northPanel );
        }
        if ( southPanel != null ) {
            childSize = childSize + assertSize( southPanel.getHeight() ) + getChildSize( southPanel );
        }
        return childSize;
    }

    private void scheduleResize( final Widget widget ) {
        if ( widget instanceof RequiresResize ) {
            final RequiresResize requiresResize = (RequiresResize) widget;
            Scheduler.get().scheduleDeferred( new ScheduledCommand() {

                @Override
                public void execute() {
                    requiresResize.onResize();
                }

            } );
        }
    }

}
