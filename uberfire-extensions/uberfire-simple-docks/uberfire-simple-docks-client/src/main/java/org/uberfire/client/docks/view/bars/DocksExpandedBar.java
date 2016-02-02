/*
 * Copyright 2015 JBoss, by Red Hat, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.client.docks.view.bars;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.ButtonGroup;
import org.gwtbootstrap3.client.ui.Heading;
import org.gwtbootstrap3.client.ui.constants.ButtonSize;
import org.gwtbootstrap3.client.ui.constants.HeadingSize;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.uberfire.client.docks.view.menu.MenuBuilder;
import org.uberfire.client.resources.WebAppResource;
import org.uberfire.client.workbench.docks.UberfireDockPosition;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.Menus;

public class DocksExpandedBar
        extends Composite implements ProvidesResize,
                                     RequiresResize {

    private UberfireDockPosition position;

    @UiField
    FlowPanel titlePanel;

    @UiField
    FlowPanel targetPanel;

    Button collapse;

    Heading title;

    @Override
    public void onResize() {
        resizeTargetPanel();
    }

    interface ViewBinder
            extends
            UiBinder<Widget, DocksExpandedBar> {

    }

    private ViewBinder uiBinder = GWT.create( ViewBinder.class );

    private static WebAppResource CSS = GWT.create( WebAppResource.class );

    public DocksExpandedBar( UberfireDockPosition position ) {
        initWidget( uiBinder.createAndBindUi( this ) );
        this.position = position;
    }

    public void setup( String titleString,
                       ParameterizedCommand<String> deselectCommand ) {
        clear();
        createTitle( titleString );
        createButtons( titleString, deselectCommand );
        setupComponents();
        setupCSS();
    }

    private void setupComponents() {
        if ( position == UberfireDockPosition.SOUTH ) {
            titlePanel.add( collapse );
            titlePanel.add( title );
        } else if ( position == UberfireDockPosition.WEST ) {
            titlePanel.add( title );
            titlePanel.add( collapse );
        } else if ( position == UberfireDockPosition.EAST ) {
            titlePanel.add( collapse );
            titlePanel.add( title );
        }
    }

    public void addMenus( Menus menus,
                          MenuBuilder menuBuilder ) {
        for ( MenuItem menuItem : menus.getItems() ) {
            final Widget result = menuBuilder.makeItem( menuItem, true );
            if ( result != null ) {
                final ButtonGroup bg = new ButtonGroup();
                bg.addStyleName( CSS.CSS().dockExpandedContentButton() );
                bg.add( result );
                titlePanel.add( bg );
            }
        }
    }

    private void createTitle( String titleString ) {
        title = new Heading( HeadingSize.H3, titleString );
    }

    private void createButtons( final String identifier,
                                final ParameterizedCommand<String> deselectCommand ) {

        collapse = GWT.create( Button.class );
        collapse.setSize( ButtonSize.SMALL );
        collapse.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                deselectCommand.execute( identifier );
            }
        } );
    }

    private void setupCSS() {
        if ( position == UberfireDockPosition.SOUTH ) {
            titlePanel.addStyleName( CSS.CSS().dockExpandedContentPanelSouth() );
            title.addStyleName( CSS.CSS().dockExpandedLabelSouth() );
            collapse.addStyleName( CSS.CSS().dockExpandedButtonSouth() );
            collapse.setIcon( IconType.CHEVRON_DOWN );
        } else if ( position == UberfireDockPosition.WEST ) {
            title.addStyleName( CSS.CSS().dockExpandedLabelWest() );
            collapse.addStyleName( CSS.CSS().dockExpandedButtonWest() );
            collapse.setIcon( IconType.CHEVRON_LEFT );
        } else if ( position == UberfireDockPosition.EAST ) {
            title.addStyleName( CSS.CSS().dockExpandedLabelEast() );
            collapse.addStyleName( CSS.CSS().dockExpandedButtonEast() );
            collapse.setIcon( IconType.CHEVRON_RIGHT );
        }
        setupDockContentSize();
    }

    public void setupDockContentSize() {
        //  goTo( PlaceRequest place, HasWidgets addTo ) lost widget size
        Scheduler.get().scheduleDeferred( new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                resizeTargetPanel();
            }
        } );
    }

    protected void resizeTargetPanel() {
        int height = getOffsetHeight() - titlePanel.getOffsetHeight();
        int width = getOffsetWidth();

        targetPanel.setSize( width + "px", height + "px" );
    }

    public void setPanelSize( int width,
                              int height ) {
        targetPanel.setPixelSize( width, height );
    }

    public FlowPanel targetPanel() {
        return targetPanel;
    }

    public void clear() {
        targetPanel.clear();
        titlePanel.clear();
    }

    public UberfireDockPosition getPosition() {
        return position;
    }
}
