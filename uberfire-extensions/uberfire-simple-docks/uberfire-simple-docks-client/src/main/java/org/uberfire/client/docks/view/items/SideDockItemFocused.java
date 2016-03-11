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
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.constants.ButtonSize;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.gwtbootstrap3.client.ui.constants.IconPosition;
import org.uberfire.client.workbench.docks.UberfireDockPosition;

public class SideDockItemFocused
        extends PopupPanel {

    interface ViewBinder
            extends
            UiBinder<Widget, SideDockItemFocused> {

    }

    private ViewBinder uiBinder = GWT.create( ViewBinder.class );

    @UiField
    Button itemButton;

    private SideDockItem parent;

    public SideDockItemFocused( final SideDockItem parent ) {
        super( true );
        this.parent = parent;
        add( uiBinder.createAndBindUi( this ) );
        removeStyleName( "gwt-PopupPanel" );
        createButton( parent );
    }

    void createButton( final SideDockItem parent ) {
        itemButton.setSize( ButtonSize.SMALL );
        itemButton.setType( ButtonType.INFO );
        itemButton.setText( parent.getLabel() );
        parent.configureIcon( itemButton, parent.getDock().getImageIconFocused() );
        onMouseOutHidePopup();
        itemButton.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                if ( !parent.isSelected() ) {
                    select();
                    parent.selectAndExecuteExpandCommand();
                } else {
                    deselect();
                    parent.deselectAndExecuteCommand();
                }
            }
        } );
    }

    public void deselect() {
        itemButton.setActive( false );
    }

    public void select() {
        itemButton.setActive( true );
    }

    public void open() {
        setupPositionAndShow();
        if( parent != null ) {
            parent.getElement().getStyle().setVisibility( Style.Visibility.HIDDEN );
        }
    }

    private void onMouseOutHidePopup() {
        this.addDomHandler( new MouseOutHandler() {
            public void onMouseOut( MouseOutEvent event ) {
                hide();
            }
        }, MouseOutEvent.getType() );
        setAutoHideEnabled( true );
    }

    @Override
    public void hide() {
        super.hide();
        if( parent != null ) {
            parent.getElement().getStyle().setVisibility( Style.Visibility.VISIBLE );
        }
    }

    private void setupPositionAndShow() {
        this.setPopupPositionAndShow( new PositionCallback() {
            public void setPosition( int offsetWidth,
                                     int offsetHeight ) {
                int left = 0;
                if ( parent.getDock().getDockPosition() == UberfireDockPosition.EAST ) {
                    left = Window.getClientWidth() - getOffsetWidth();
                }
                int top = parent.getAbsoluteTop();
                setPopupPosition( left, top );
            }
        } );
    }

}
