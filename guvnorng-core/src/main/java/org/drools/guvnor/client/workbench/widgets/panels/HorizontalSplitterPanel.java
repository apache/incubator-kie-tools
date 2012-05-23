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
package org.drools.guvnor.client.workbench.widgets.panels;

import org.drools.guvnor.client.workbench.PositionSelectorPopup.Position;
import org.drools.guvnor.client.workbench.WorkbenchPanel;
import org.drools.guvnor.client.workbench.WorkbenchPart;
import org.drools.guvnor.client.workbench.widgets.dnd.CompassDropController;
import org.drools.guvnor.client.workbench.widgets.dnd.WorkbenchDragAndDropManager;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 */
public class HorizontalSplitterPanel extends ResizeComposite
    implements
    SplitPanel {

    private final ResizableSplitLayoutPanel slp                 = new ResizableSplitLayoutPanel();
    private final ScrollPanel               eastWidgetContainer = new ScrollPanel();
    private final ScrollPanel               westWidgetContainer = new ScrollPanel();

    public HorizontalSplitterPanel(final WorkbenchPart eastWidget,
                                   final WorkbenchPanel westWidget,
                                   final Position position) {
        switch ( position ) {
            case EAST :
                slp.addEast( westWidgetContainer,
                             INITIAL_SIZE );
                slp.add( eastWidgetContainer );
                slp.setWidgetMinSize( westWidgetContainer,
                                      MIN_SIZE );
                break;
            case WEST :
                slp.addWest( eastWidgetContainer,
                             INITIAL_SIZE );
                slp.add( westWidgetContainer );
                slp.setWidgetMinSize( eastWidgetContainer,
                                      MIN_SIZE );
                break;
            default :
                throw new IllegalArgumentException( "position must be either EAST or WEST" );
        }
        final WorkbenchPanel eastPanel = new WorkbenchPanel( eastWidget );
        westWidgetContainer.setWidget( westWidget );
        eastWidgetContainer.setWidget( eastPanel );

        initWidget( slp );

        //Wire-up DnD controllers
        WorkbenchDragAndDropManager.getInstance().registerDropController( eastWidgetContainer,
                                                                          new CompassDropController( eastPanel ) );
        WorkbenchDragAndDropManager.getInstance().registerDropController( westWidgetContainer,
                                                                          new CompassDropController( westWidget ) );
    }

    public HorizontalSplitterPanel(final WorkbenchPanel eastWidget,
                                   final WorkbenchPart westWidget,
                                   final Position position) {
        switch ( position ) {
            case EAST :
                slp.addEast( westWidgetContainer,
                             INITIAL_SIZE );
                slp.add( eastWidgetContainer );
                slp.setWidgetMinSize( westWidgetContainer,
                                      MIN_SIZE );
                break;
            case WEST :
                slp.addWest( eastWidgetContainer,
                             INITIAL_SIZE );
                slp.add( westWidgetContainer );
                slp.setWidgetMinSize( eastWidgetContainer,
                                      MIN_SIZE );
                break;
            default :
                throw new IllegalArgumentException( "position must be either EAST or WEST" );
        }
        final WorkbenchPanel westPanel = new WorkbenchPanel( westWidget );
        westWidgetContainer.setWidget( westPanel );
        eastWidgetContainer.setWidget( eastWidget );

        initWidget( slp );

        //Wire-up DnD controllers
        WorkbenchDragAndDropManager.getInstance().registerDropController( eastWidgetContainer,
                                                                          new CompassDropController( eastWidget ) );
        WorkbenchDragAndDropManager.getInstance().registerDropController( westWidgetContainer,
                                                                          new CompassDropController( westPanel ) );
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
        Widget parent = getParent();
        int width = parent.getElement().getOffsetWidth();
        int height = parent.getElement().getOffsetHeight();
        this.getElement().getStyle().setWidth( width,
                                               Unit.PX );
        this.getElement().getStyle().setHeight( height,
                                                Unit.PX );
        super.onResize();
    }

}
