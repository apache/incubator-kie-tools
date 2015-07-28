/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.kie.workbench.common.screens.datamodeller.client.widgets.common.properties;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.InputGroupAddon;
import org.gwtbootstrap3.client.ui.TextBox;
import org.uberfire.ext.properties.editor.client.widgets.AbstractPropertyEditorWidget;
import org.uberfire.ext.properties.editor.model.PropertyEditorFieldInfo;

public abstract class BasePopupPropertyEditorWidget extends AbstractPropertyEditorWidget {

    interface BasePopupPropertyEditorWidgetUiBinder extends UiBinder<Widget, BasePopupPropertyEditorWidget> {

    }

    private static BasePopupPropertyEditorWidgetUiBinder uiBinder = GWT.create( BasePopupPropertyEditorWidgetUiBinder.class );

    @UiField
    TextBox propertyTextBox;

    @UiField
    InputGroupAddon propertyAddOn;

    ValueChangeHandler<String> valueChangeHandler;

    PropertyEditorFieldInfo property;

    public BasePopupPropertyEditorWidget() {
        initWidget( uiBinder.createAndBindUi( this ) );

        propertyTextBox.setReadOnly( true );

        propertyAddOn.addDomHandler( new ClickHandler() {
            public void onClick( ClickEvent event ) {
                openEditionPopup();
            }
        }, ClickEvent.getType() );
    }

    public void setValue( String value ) {
        propertyTextBox.setText( value );
    }

    public String getValue() {
        return propertyTextBox.getText();
    }

    public PropertyEditorFieldInfo getProperty() {
        return property;
    }

    public void setProperty( PropertyEditorFieldInfo property ) {
        this.property = property;
    }

    public void addChangeHandler( ValueChangeHandler<String> changeHandler ) {
        this.valueChangeHandler = changeHandler;
    }

    protected void openEditionPopup() {

        final PropertyEditionPopup popup = createEditionPopup( property );

        popup.setStringValue( getValue() );
        popup.setOkCommand( new Command() {
            @Override
            public void execute() {
                valueChangeHandler.onValueChange( new StringValueChangeEvent( popup.getStringValue() ) );
                setValue( popup.getStringValue() );
            }
        } );
        popup.show();
    }

    protected abstract PropertyEditionPopup createEditionPopup( PropertyEditorFieldInfo property );

    public static class StringValueChangeEvent extends ValueChangeEvent<String> {

        public StringValueChangeEvent( String value ) {
            super( value );
        }
    }
}
