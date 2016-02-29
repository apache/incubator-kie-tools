/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.screens.examples.client.wizard.widgets;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import org.gwtbootstrap3.client.ui.AnchorListItem;
import org.gwtbootstrap3.client.ui.DropDown;
import org.gwtbootstrap3.client.ui.DropDownMenu;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.base.HasDataToggle;
import org.gwtbootstrap3.client.ui.base.mixin.DataToggleMixin;
import org.gwtbootstrap3.client.ui.constants.Toggle;

public class ComboBox extends Composite implements HasValueChangeHandlers<String> {

    private static class ToggleTextBox extends TextBox implements HasDataToggle {

        private final DataToggleMixin<ToggleTextBox> toggleMixin = new DataToggleMixin<ToggleTextBox>( this );

        @Override
        public void setDataToggle( final Toggle toggle ) {
            toggleMixin.setDataToggle( toggle );
        }

        @Override
        public Toggle getDataToggle() {
            return toggleMixin.getDataToggle();
        }
    }

    private static class ValueChangeEvent extends com.google.gwt.event.logical.shared.ValueChangeEvent<String> {

        ValueChangeEvent( final String value ) {
            super( value );
        }
    }

    private final DropDown dropDown = new DropDown();
    private final DropDownMenu content = new DropDownMenu();

    private final ToggleTextBox textBox = new ToggleTextBox() {{
        setDataToggle( Toggle.DROPDOWN );
        addValueChangeHandler( new ValueChangeHandler<String>() {
            @Override
            public void onValueChange( com.google.gwt.event.logical.shared.ValueChangeEvent<String> event ) {
                ComboBox.this.fireEvent( new ValueChangeEvent( getText() ) );
            }
        } );
    }};

    public ComboBox() {
        initWidget( dropDown );
        dropDown.add( textBox );
        dropDown.add( content );
    }

    public void clear() {
        content.clear();
    }

    public void addItem( final String url ) {
        content.add( makeListItem( url ) );
    }

    public void setText( final String text ) {
        textBox.setText( text );
    }

    public void setPlaceholder( final String placeHolder ) {
        textBox.setPlaceholder( placeHolder );
    }

    private AnchorListItem makeListItem( final String text ) {
        final AnchorListItem item = new AnchorListItem( text );
        item.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( final ClickEvent event ) {
                textBox.setText( text );
                ComboBox.this.fireEvent( new ValueChangeEvent( text ) );
            }
        } );
        return item;
    }

    @Override
    public HandlerRegistration addValueChangeHandler( final ValueChangeHandler<String> handler ) {
        return addHandler( handler,
                           ValueChangeEvent.getType() );
    }

}
