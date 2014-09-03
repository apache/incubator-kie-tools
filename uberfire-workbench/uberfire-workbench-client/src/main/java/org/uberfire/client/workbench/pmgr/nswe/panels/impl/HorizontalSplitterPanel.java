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
package org.uberfire.client.workbench.pmgr.nswe.panels.impl;

import javax.enterprise.context.Dependent;

import org.uberfire.client.workbench.panels.SplitPanel;
import org.uberfire.client.workbench.panels.WorkbenchPanelView;
import org.uberfire.client.workbench.widgets.split.WorkbenchSplitLayoutPanel;
import org.uberfire.workbench.model.CompassPosition;
import org.uberfire.workbench.model.PanelDefinition;

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

    WorkbenchSplitLayoutPanel slp = new WorkbenchSplitLayoutPanel();
    SimpleLayoutPanel eastWidgetContainer = new SimpleLayoutPanel();
    SimpleLayoutPanel westWidgetContainer = new SimpleLayoutPanel();

    public HorizontalSplitterPanel() {
        initWidget( getSlp() );
    }

    @Override
    public void setup( final WorkbenchPanelView eastWidget,
                       final WorkbenchPanelView westWidget,
                       final CompassPosition position,
                       final Integer preferredSize,
                       final Integer preferredMinSize ) {

        final int size = assertSize( preferredSize );
        final int minSize = assertMinimumSize( preferredMinSize );

        switch ( position ) {
            case EAST:
                final int eastChildSize = getChildSize( eastWidget.getPresenter().getDefinition() );
                getSlp().addEast( eastWidgetContainer,
                                  size + eastChildSize );
                getSlp().add( westWidgetContainer );
                break;
            case WEST:
                final int westChildSize = getChildSize( westWidget.getPresenter().getDefinition() );
                getSlp().addWest( westWidgetContainer,
                                  size + westChildSize );
                getSlp().add( eastWidgetContainer );
                break;
            default:
                throw new IllegalArgumentException( "position must be either EAST or WEST" );
        }
        getSlp().setWidgetMinSize( eastWidgetContainer,
                                   minSize );
        getSlp().setWidgetMinSize( westWidgetContainer,
                                   minSize );

        westWidgetContainer.setWidget( westWidget );
        eastWidgetContainer.setWidget( eastWidget );
    }

    WorkbenchSplitLayoutPanel getSlp() {
        return slp;
    }

    @Override
    public void clear() {
        getSlp().clear();
    }

    @Override
    public Widget getWidget( CompassPosition position ) {
        switch ( position ) {
            case EAST:
                return this.westWidgetContainer.getWidget();
            case WEST:
                return this.eastWidgetContainer.getWidget();
            default:
                return null;
        }
    }

    private int assertSize( final Integer size ) {
        return ( size == null ? DEFAULT_SIZE : size );
    }

    private int assertMinimumSize( final Integer minSize ) {
        return ( minSize == null ? DEFAULT_MIN_SIZE : minSize );
    }

    int getChildSize( final PanelDefinition panel ) {
        int childSize = 0;
        final PanelDefinition eastPanel = panel.getChild( CompassPosition.EAST );
        final PanelDefinition westPanel = panel.getChild( CompassPosition.WEST );
        if ( eastPanel != null ) {
            childSize = childSize + assertSize( eastPanel.getWidth() ) + getChildSize( eastPanel );
        }
        if ( westPanel != null ) {
            childSize = childSize + assertSize( westPanel.getWidth() ) + getChildSize( westPanel );
        }
        return childSize;
    }

}
