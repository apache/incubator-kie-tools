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

import org.uberfire.client.resources.WorkbenchResources;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.layout.client.Layout.Layer;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * A {@link WorkbenchTabLayoutPanel} that shows scroll buttons if necessary
 */
public class WorkbenchScrolledTabLayoutPanel extends WorkbenchTabLayoutPanel {

    private LayoutPanel panel;
    private FlowPanel tabBar;
    private Image scrollLeftImage;
    private Image scrollRightImage;
    private SimplePanel scrollLeftImageContainer;
    private SimplePanel scrollRightImageContainer;

    private int scrollLeftImageWidth;
    private int scrollRightImageWidth;

    private int scrollOffset = 0;

    private static final int SCROLL_STEP = 20;

    private ImageResource leftArrowImage;
    private ImageResource rightArrowImage;

    public WorkbenchScrolledTabLayoutPanel( final int barHeight,
                                            final int focusBarHeight,
                                            final ImageResource leftArrowImage,
                                            final ImageResource rightArrowImage ) {
        super( barHeight,
               focusBarHeight );

        this.leftArrowImage = leftArrowImage;
        this.rightArrowImage = rightArrowImage;

        // The main widget wrapped by this composite, which is a LayoutPanel with the tab bar & the tab content
        panel = (LayoutPanel) getWidget();

        // Find the tab bar, which is the first flow panel in the LayoutPanel
        for ( int i = 0; i < panel.getWidgetCount(); ++i ) {
            Widget widget = panel.getWidget( i );
            if ( widget instanceof FlowPanel ) {
                tabBar = (FlowPanel) widget;
                break;
            }
        }

        initScrollButtons( barHeight );
    }

    @Override
    public void add( final Widget child,
                     final Widget tab ) {
        super.add( child,
                   tab );

        // Defer size calculations until tab has been added to view at end of browser loop
        Scheduler.get().scheduleDeferred( new ScheduledCommand() {

            @Override
            public void execute() {
                checkIfScrollButtonsNecessary();
            }
        } );
    }

    @Override
    public boolean remove( final int index ) {
        boolean b = super.remove( index );
        if ( tabBar.getWidgetCount() < 2 ) {
            resetScrollPosition();
        } else {
            checkIfScrollButtonsNecessary();
        }
        return b;
    }

    private ClickHandler createScrollClickHandler( final int diff ) {
        return new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                Widget lastTab = getLastTab();
                if ( lastTab == null ) {
                    return;
                }
                scrollOffset = scrollOffset + diff;
                scrollTo( scrollOffset );
            }
        };
    }

    //Create and attach the scroll button images with a click handler
    private void initScrollButtons( final int barHeight ) {

        scrollLeftImage = new Image( leftArrowImage );
        scrollLeftImage.addClickHandler( createScrollClickHandler( -SCROLL_STEP ) );
        scrollLeftImage.setVisible( false );

        scrollRightImage = new Image( rightArrowImage );
        scrollRightImage.addClickHandler( createScrollClickHandler( SCROLL_STEP ) );
        scrollRightImage.setStyleName( WorkbenchResources.INSTANCE.CSS().tabLayoutPanelTabsScrollButtons() );
        scrollRightImage.setVisible( false );

        scrollLeftImageWidth = scrollLeftImage.getWidth();
        scrollRightImageWidth = scrollRightImage.getWidth();

        scrollLeftImageContainer = new SimplePanel();
        scrollLeftImageContainer.setStyleName( WorkbenchResources.INSTANCE.CSS().tabLayoutPanelTabsScrollButtons() );
        scrollLeftImageContainer.add( scrollLeftImage );

        scrollRightImageContainer = new SimplePanel();
        scrollRightImageContainer.setStyleName( WorkbenchResources.INSTANCE.CSS().tabLayoutPanelTabsScrollButtons() );
        scrollRightImageContainer.add( scrollRightImage );

        panel.insert( scrollRightImageContainer,
                      0 );
        panel.setWidgetRightWidth( scrollRightImageContainer,
                                   getControlsWidth(),
                                   Unit.PX,
                                   scrollRightImageWidth,
                                   Unit.PX );
        panel.setWidgetTopHeight( scrollRightImageContainer,
                                  0,
                                  Unit.PX,
                                  barHeight,
                                  Unit.PX );

        panel.insert( scrollLeftImageContainer,
                      0 );
        panel.setWidgetLeftWidth( scrollLeftImageContainer,
                                  0,
                                  Unit.PX,
                                  scrollLeftImageWidth,
                                  Unit.PX );
        panel.setWidgetTopHeight( scrollLeftImageContainer,
                                  0,
                                  Unit.PX,
                                  barHeight,
                                  Unit.PX );
    }

    private void checkIfScrollButtonsNecessary() {
        boolean isLeftScrolling = isLeftScrollingNecessary();
        boolean isRightScrolling = isRightScrollingNecessary();
        scrollLeftImage.setVisible( isLeftScrolling );
        scrollRightImage.setVisible( isRightScrolling );
    }

    private void resetScrollPosition() {
        scrollOffset = 0;
        scrollTo( 0 );
    }

    private void scrollTo( final int pos ) {
        final Layer layer = (Layer) tabBar.getLayoutData();
        layer.setLeftRight( pos,
                            Unit.PX,
                            getControlsWidth(),
                            Unit.PX );
        panel.forceLayout();
        checkIfScrollButtonsNecessary();
    }

    private boolean isLeftScrollingNecessary() {
        final Widget lastTab = getLastTab();
        if ( lastTab == null ) {
            return false;
        }
        final int tabBarWidth = getTabBarWidth();
        final int rightOfWidget = getRightOfWidget( lastTab );
        return tabBarWidth < rightOfWidget;
    }

    private boolean isRightScrollingNecessary() {
        return scrollOffset < 0;
    }

    private int getRightOfWidget( final Widget widget ) {
        return widget.getElement().getOffsetLeft() + widget.getElement().getOffsetWidth();
    }

    private int getTabBarWidth() {
        return tabBar.getElement().getParentElement().getClientWidth();
    }

    private Widget getLastTab() {
        if ( tabBar.getWidgetCount() == 0 ) {
            return null;
        }
        return tabBar.getWidget( tabBar.getWidgetCount() - 1 );
    }

    @Override
    public void onResize() {
        super.onResize();
        panel.setWidgetRightWidth( scrollRightImageContainer,
                                   getControlsWidth(),
                                   Unit.PX,
                                   scrollRightImageWidth,
                                   Unit.PX );

        //Drag TabBar with right-hand edge of last tab when resizing
        final Widget lastTab = getLastTab();
        if ( lastTab != null ) {
            final int tabBarWidth = getTabBarWidth();
            final int rightOfWidget = getRightOfWidget( lastTab );
            final int overlap = tabBarWidth - rightOfWidget - SCROLL_STEP;
            if ( overlap > 0 && scrollOffset < 0 ) {
                scrollOffset = scrollOffset + overlap;
                if ( scrollOffset < 0 ) {
                    scrollTo( scrollOffset );
                } else if ( scrollOffset > 0 ) {
                    resetScrollPosition();
                }
            }
        }
        checkIfScrollButtonsNecessary();
    }

}