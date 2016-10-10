/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.forms.client.fields.colorPicker;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.UIObject;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.TextBox;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.ext.widgets.common.client.colorpicker.ColorPickerDialog;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

/**
 * @author Pere Fernandez <pefernan@redhat.com>
 */
@Dependent
@Templated
public class ColorPickerWidget extends Composite implements HasValue<String> {

    @Inject
    @DataField
    private Button colorButton;

    @Inject
    @DataField
    private TextBox colorTextBox;

    private String color;

    @EventHandler( "colorButton" )
    public void onClickColorButton( ClickEvent clickEvent ) {
        showColorDialog( colorButton );
    }

    @EventHandler( "colorTextBox" )
    public void onClickColorTextBox( ClickEvent clickEvent ) {
        showColorDialog( colorTextBox );
    }

    protected void showColorDialog( final UIObject owner ) {
        final ColorPickerDialog dlg = new ColorPickerDialog();
        dlg.getElement().getStyle().setZIndex( 9999 );
        dlg.addDialogClosedHandler( event -> {
            if ( !event.isCanceled() ) {
                setValue( "#" + dlg.getColor(), true );
            }
        } );
        String color = getValue();
        if ( color.startsWith( "#" ) ) {
            color = color.substring( 1, color.length() );
        }
        dlg.setColor( color );
        dlg.showRelativeTo( owner );
    }

    @Override
    public String getValue() {
        return color;
    }

    @Override
    public void setValue( String value ) {
        setValue( value, false );
    }

    @Override
    public void setValue( String value, boolean fireEvents ) {
        String oldValue = color;
        color = value;
        initTextBox();
        if ( fireEvents ) {
            ValueChangeEvent.fireIfNotEqual( this, oldValue, color );
        }
    }

    protected void initTextBox() {
        colorTextBox.getElement().getStyle().setBackgroundColor( color );
    }

    @Override
    public HandlerRegistration addValueChangeHandler( ValueChangeHandler<String> handler ) {
        return addHandler( handler, ValueChangeEvent.getType() );
    }
}
