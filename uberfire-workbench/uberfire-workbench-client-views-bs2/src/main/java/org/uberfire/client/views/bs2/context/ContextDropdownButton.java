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

package org.uberfire.client.views.bs2.context;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.DropdownButton;
import com.github.gwtbootstrap.client.ui.base.IconAnchor;
import com.github.gwtbootstrap.client.ui.constants.BaseIconType;
import com.github.gwtbootstrap.client.ui.constants.ButtonType;
import com.github.gwtbootstrap.client.ui.constants.IconPosition;
import com.github.gwtbootstrap.client.ui.constants.IconSize;
import com.github.gwtbootstrap.client.ui.resources.ButtonSize;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;

public class ContextDropdownButton extends DropdownButton {

    private Button trigger;

    /**
     * Creates a DropdownButton without a caption.
     */
    public ContextDropdownButton() {
        super();
    }

    /**
     * Creates a DropdownButton with the given caption.
     * @param caption the button's caption
     */
    public ContextDropdownButton( String caption ) {
        super( caption );
    }

    public void displayCaret( boolean value ) {
        trigger.setCaret( value );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected IconAnchor createTrigger() {
        trigger = new Button();
        trigger.setCaret( true );
        return trigger;
    }

    /**
     * Sets the button's size.
     * @param size the button's size
     */
    public void setSize( ButtonSize size ) {
        trigger.setSize( size );
    }

    /**
     * Sets the button's type.
     * @param type the button's type
     */
    public void setType( ButtonType type ) {
        trigger.setType( type );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setBaseIcon( BaseIconType type ) {
        trigger.setBaseIcon( type );
    }

    @Override
    public HandlerRegistration addClickHandler( ClickHandler handler ) {
        return trigger.addClickHandler( handler );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setIconSize( IconSize size ) {
        trigger.setIconSize( size );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCustomIconStyle( String customIconStyle ) {
        trigger.setCustomIconStyle( customIconStyle );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setIconPosition( IconPosition position ) {
        trigger.setIconPosition( position );
    }
}
