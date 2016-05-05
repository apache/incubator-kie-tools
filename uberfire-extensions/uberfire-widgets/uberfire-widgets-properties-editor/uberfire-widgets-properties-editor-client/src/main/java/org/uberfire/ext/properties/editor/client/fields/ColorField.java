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

package org.uberfire.ext.properties.editor.client.fields;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.ext.properties.editor.client.widgets.PropertyEditorColorPicker;
import org.uberfire.ext.properties.editor.model.PropertyEditorChangeEvent;
import org.uberfire.ext.properties.editor.model.PropertyEditorFieldInfo;

@Dependent
public class ColorField extends AbstractField {

    @Inject
    Event<PropertyEditorChangeEvent> propertyEditorChangeEvent;

    @Override
    public Widget widget( final PropertyEditorFieldInfo property ) {
        final PropertyEditorColorPicker colorPicker = GWT.create( PropertyEditorColorPicker.class );
        colorPicker.setValue(property.getCurrentStringValue());
        colorPicker.addChangeHandler(
            new ValueChangeHandler<String>() {
                public void onValueChange(ValueChangeEvent event) {
                    String color = colorPicker.getValue();
                    if (validate(property, color)) {
                        colorPicker.clearOldValidationErrors();
                        property.setCurrentStringValue(color);
                        propertyEditorChangeEvent.fire(new PropertyEditorChangeEvent(property, color));
                    } else {
                        colorPicker.setValidationError(getValidatorErrorMessage(property, color));
                        colorPicker.setValue(property.getCurrentStringValue());
                    }
                }
            });
        return colorPicker;
    }
}
