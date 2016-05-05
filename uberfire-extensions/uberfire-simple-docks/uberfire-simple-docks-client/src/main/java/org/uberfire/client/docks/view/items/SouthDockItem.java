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
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.constants.ButtonSize;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.gwtbootstrap3.client.ui.constants.IconPosition;
import org.uberfire.client.resources.WebAppResource;
import org.uberfire.client.workbench.docks.UberfireDock;
import org.uberfire.mvp.ParameterizedCommand;

public class SouthDockItem
        extends AbstractDockItem {

    private final ParameterizedCommand<String> selectCommand;

    private final ParameterizedCommand<String> deselectCommand;

    private boolean selected;

    private static WebAppResource CSS = GWT.create( WebAppResource.class );

    interface ViewBinder
            extends
            UiBinder<Widget, SouthDockItem> {

    }

    @UiField
    Button itemButton;

    private ViewBinder uiBinder = GWT.create( ViewBinder.class );

    SouthDockItem( final UberfireDock dock,
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
        itemButton.setText( getDock().getLabel() );
        configureIcon( itemButton, getDock().getImageIcon() );
        itemButton.getElement().addClassName( CSS.CSS().southDockItem() );
        itemButton.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                if ( !selected ) {
                    select();
                    selectCommand.execute( getIdentifier() );
                } else {
                    deselect();
                    deselectCommand.execute( getIdentifier() );
                }
            }
        } );
    }

    @Override
    public void selectAndExecuteExpandCommand() {
        select();
        selectCommand.execute( getIdentifier() );
    }

    @Override
    public void select() {
        selected = true;
        itemButton.setType( ButtonType.INFO );
        if ( getDock().getImageIconFocused() != null ) {
            itemButton.remove( 0 );
            configureImageIcon( itemButton, getDock().getImageIconFocused() );
        }
    }

    @Override
    public void deselect() {
        selected = false;
        itemButton.setType( ButtonType.LINK );
        if ( getDock().getImageIcon() != null ) {
            itemButton.remove( 0 );
            configureImageIcon( itemButton, getDock().getImageIcon() );
        }
    }

}
