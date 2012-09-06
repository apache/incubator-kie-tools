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
import org.uberfire.client.workbench.WorkbenchPanelPresenter;

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

    private final WorkbenchSplitLayoutPanel slp                 = new WorkbenchSplitLayoutPanel();
    private final SimpleLayoutPanel         eastWidgetContainer = new SimpleLayoutPanel();
    private final SimpleLayoutPanel         westWidgetContainer = new SimpleLayoutPanel();

    public HorizontalSplitterPanel() {
        initWidget( slp );
    }

    @Override
    public void setup(final WorkbenchPanelPresenter.View eastWidget,
                      final WorkbenchPanelPresenter.View westWidget,
                      final Position position,
                      final Integer preferredSize,
                      final Integer preferredMinSize) {

        final int size = (preferredSize == null ? DEFAULT_SIZE : preferredSize);
        final int minSize = (preferredMinSize == null ? DEFAULT_MIN_SIZE : preferredMinSize);

        switch ( position ) {
            case EAST :
                slp.addEast( westWidgetContainer,
                             size );
                slp.add( eastWidgetContainer );
                break;
            case WEST :
                slp.addWest( eastWidgetContainer,
                             size );
                slp.add( westWidgetContainer );
                break;
            default :
                throw new IllegalArgumentException( "position must be either EAST or WEST" );
        }
        slp.setWidgetMinSize( eastWidgetContainer,
                              minSize );
        slp.setWidgetMinSize( westWidgetContainer,
                              minSize );

        westWidgetContainer.setWidget( westWidget );
        eastWidgetContainer.setWidget( eastWidget );
    }

    @Override
    public void clear() {
        this.slp.clear();
    }

    @Override
    public Widget getWidget(Position position) {
        switch ( position ) {
            case EAST :
                return this.westWidgetContainer.getWidget();
            case WEST :
                return this.eastWidgetContainer.getWidget();
            default :
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

}
