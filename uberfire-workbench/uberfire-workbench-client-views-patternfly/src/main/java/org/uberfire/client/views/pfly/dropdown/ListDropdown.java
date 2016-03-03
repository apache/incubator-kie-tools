/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.client.views.pfly.dropdown;

import java.util.Iterator;

import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.DropDown;
import org.gwtbootstrap3.client.ui.DropDownMenu;
import org.gwtbootstrap3.client.ui.ListItem;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.gwtbootstrap3.client.ui.constants.Toggle;

/**
 * Utility component for creating dropdown menus.
 * It also allows to determine if dropdown should shown when there is a single element in the list.
 */
public class ListDropdown extends DropDown {

    protected Button button = new Button();
    protected DropDownMenu dropDownMenu = new DropDownMenu();
    protected boolean hideOnSingleElement = true;

    public ListDropdown() {
        super.add( button );
        super.add( dropDownMenu );
        button.setType( ButtonType.LINK );
        this.addStyleName( "uf-list-dropdown" );
    }

    @Override
    public void add( final Widget child ) {
        if ( child instanceof ListItem ) {
            dropDownMenu.add( child );
            addCaretToText();
        }
    }

    public void setText( final Widget text ) {
        removeChildWidgets( button );
        button.add( text );
        addCaretToText();
    }

    @Override
    public boolean remove( final Widget w ) {
        final boolean remove = dropDownMenu.remove( w );
        addCaretToText();
        return remove;
    }

    public void setHideOnSingleElement( boolean hide ) {
        this.hideOnSingleElement = hide;
    }

    @Override
    public void clear() {
        removeChildWidgets( button );
        removeChildWidgets( dropDownMenu );
    }

    private void removeChildWidgets( final ComplexPanel panel ) {
        final Iterator<Widget> iterator = panel.iterator();
        while ( iterator.hasNext() ) {
            iterator.next();
            iterator.remove();
        }
    }

    /**
     * Checks whether or not caret should be added/removed
     */
    protected void addCaretToText() {
        if( hideOnSingleElement && dropDownMenu.getWidgetCount() == 1 ){
            button.setToggleCaret( false );
            button.setDataToggle( null );
            this.removeStyleName( "open" );
            toggleStyles( true );
        } else if ( ( dropDownMenu.getWidgetCount() > 1 || hideOnSingleElement == false && dropDownMenu.getWidgetCount() == 1 ) ) {
            button.setToggleCaret( true );
            button.setDataToggle( Toggle.DROPDOWN );
            button.setDataTargetWidget( this );
            toggleStyles( false );
        }
    }

    private void toggleStyles( boolean single ) {
        this.removeStyleName( "uf-list-dropdown-single" );
        this.removeStyleName( "uf-list-dropdown-multi" );
        this.addStyleName( single ? "uf-list-dropdown-single" : "uf-list-dropdown-multi" );
    }

}
