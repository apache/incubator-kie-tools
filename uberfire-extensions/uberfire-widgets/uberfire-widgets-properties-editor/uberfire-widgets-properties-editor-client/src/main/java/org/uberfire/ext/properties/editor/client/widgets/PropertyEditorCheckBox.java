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

package org.uberfire.ext.properties.editor.client.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.CheckBox;

public class PropertyEditorCheckBox extends AbstractPropertyEditorWidget {

    @UiField
    CheckBox checkBox;

    public PropertyEditorCheckBox() {
        initWidget( uiBinder.createAndBindUi( this ) );
    }

    public void setValue( Boolean value ) {
        checkBox.setValue( value );
    }

    public Boolean getValue() {
        return checkBox.getValue();
    }

    public void addValueChangeHandler( ValueChangeHandler<Boolean> valueChangeHandler ) {
        checkBox.addValueChangeHandler( valueChangeHandler );
    }

    interface MyUiBinder extends UiBinder<Widget, PropertyEditorCheckBox> {

    }

    private static MyUiBinder uiBinder = GWT.create( MyUiBinder.class );

}