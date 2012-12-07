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

import org.uberfire.client.workbench.Position;
import org.uberfire.client.workbench.model.PanelDefinition;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * A split panel to contain WorkbenchPanels split horizontally.
 */
@Dependent
public class HorizontalSplitterPanel extends ResizeComposite
        implements
        SplitPanel {

    private final WorkbenchSplitLayoutPanel slp = new WorkbenchSplitLayoutPanel();
    private final SimpleLayoutPanel eastWidgetContainer = new SimpleLayoutPanel();
    private final SimpleLayoutPanel westWidgetContainer = new SimpleLayoutPanel();

    public HorizontalSplitterPanel() {
        initWidget( slp );
    }

    @Override
    public void setup( final WorkbenchPanelView eastWidget,
                       final WorkbenchPanelView westWidget,
                       final Position position,
                       final Integer preferredSize,
                       final Integer preferredMinSize ) {

        final int size = assertSize( preferredSize );
        final int minSize = assertMinimumSize( preferredMinSize );

        switch ( position ) {
            case EAST:
                final int eastChildSize = getChildSize( eastWidget.getPresenter().getDefinition() );
                slp.addEast( eastWidgetContainer,
                             size + eastChildSize );
                slp.add( westWidgetContainer );
                break;
            case WEST:
                final int westChildSize = getChildSize( westWidget.getPresenter().getDefinition() );
                slp.addWest( westWidgetContainer,
                             size + westChildSize );
                slp.add( eastWidgetContainer );
                break;
            default:
                throw new IllegalArgumentException( "position must be either EAST or WEST" );
        }
        slp.setWidgetMinSize( eastWidgetContainer,
                              minSize );
        slp.setWidgetMinSize( westWidgetContainer,
                              minSize );

        westWidgetContainer.setWidget( westWidget );
        eastWidgetContainer.setWidget( eastWidget );
        scheduleResize( slp );
    }

    @Override
    public void clear() {
        this.slp.clear();
    }

    @Override
    public Widget getWidget( Position position ) {
        switch ( position ) {
            case EAST:
                return this.westWidgetContainer.getWidget();
            case WEST:
                return this.eastWidgetContainer.getWidget();
            default:
                throw new IllegalArgumentException( "position must be either EAST or WEST" );
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
        final PanelDefinition eastPanel = panel.getChild( Position.EAST );
        final PanelDefinition westPanel = panel.getChild( Position.WEST );
        if ( eastPanel != null ) {
            childSize = childSize + assertSize( eastPanel.getWidth() ) + getChildSize( eastPanel );
        }
        if ( westPanel != null ) {
            childSize = childSize + assertSize( westPanel.getWidth() ) + getChildSize( westPanel );
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
