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

package org.uberfire.client.docks.view.items;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.constants.ButtonSize;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.uberfire.client.resources.WebAppResource;
import org.uberfire.client.workbench.docks.UberfireDock;
import org.uberfire.mvp.ParameterizedCommand;

public class SideDockItem
        extends AbstractDockItem {

    private MouseEventHandler mouseEventHandler;

    interface ViewBinder
            extends
            UiBinder<Widget, SideDockItem> {

    }

    @UiField
    Button itemButton;

    private final ParameterizedCommand<String> selectCommand;

    private final ParameterizedCommand<String> deselectCommand;

    private SideDockItemFocused popup = new SideDockItemFocused( SideDockItem.this );

    private boolean selected;

    private ViewBinder uiBinder = GWT.create( ViewBinder.class );

    private static WebAppResource CSS = GWT.create( WebAppResource.class );

    SideDockItem( UberfireDock dock,
                  final ParameterizedCommand<String> selectCommand,
                  final ParameterizedCommand<String> deselectCommand ) {
        super( dock );
        this.selectCommand = selectCommand;
        this.deselectCommand = deselectCommand;
        initWidget( uiBinder.createAndBindUi( this ) );
        createButton();
    }

    void createButton() {
        itemButton.setSize( ButtonSize.SMALL );
        itemButton.setType( ButtonType.LINK );
        configureIcon( itemButton, getDock().getImageIcon() );
        mouseEventHandler = new MouseEventHandler();
        itemButton.addDomHandler( mouseEventHandler, MouseOverEvent.getType() );
        itemButton.addStyleName( CSS.CSS().sideDockItem() );
        itemButton.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                if ( !isSelected() ) {
                    selectAndExecuteExpandCommand();
                } else {
                    deselectAndExecuteCommand();
                }
            }
        } );
    }

    @Override
    public void select() {
        selected = true;
        itemButton.setActive( true );
        itemButton.setType( ButtonType.INFO );
        if ( getDock().getImageIconFocused() != null ) {
            itemButton.remove( 0 );
            configureImageIcon( itemButton, getDock().getImageIconFocused() );
        }
    }

    @Override
    public void selectAndExecuteExpandCommand() {
        select();
        popup.select();
        selectCommand.execute( getIdentifier() );
    }

    @Override
    public void deselect() {
        selected = false;
        popup.deselect();
        itemButton.setActive( false );
        itemButton.setType( ButtonType.LINK );
        if ( getDock().getImageIcon() != null ) {
            itemButton.remove( 0 );
            configureImageIcon( itemButton, getDock().getImageIcon() );
        }
    }

    public void deselectAndExecuteCommand() {
        deselect();
        deselectCommand.execute( getIdentifier() );
    }

    class MouseEventHandler implements MouseOverHandler {

        public MouseEventHandler() {
        }

        public void onMouseOver( final MouseOverEvent moe ) {
            if ( openPopup() ) {
                popup.open();
            }
        }

    }

    private boolean openPopup() {
        return !isSelected();
    }

    public boolean isSelected() {
        return selected;
    }

    SideDockItemFocused getPopup() {
        return popup;
    }
}
