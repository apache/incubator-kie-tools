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

import com.google.gwt.user.client.ui.IsWidget;
import org.kie.workbench.common.forms.dynamic.client.rendering.FieldRenderer;
import org.kie.workbench.common.stunner.forms.model.ColorPickerFieldDefinition;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

@Dependent
public class ColorPickerFieldRenderer extends FieldRenderer<ColorPickerFieldDefinition> {

    private ColorPickerWidget colorPicker;

    @Inject
    public ColorPickerFieldRenderer( ColorPickerWidget colorPicker ) {
        this.colorPicker = colorPicker;
    }

    @Override
    public String getName() {
        return ColorPickerFieldDefinition.CODE;
    }

    @Override
    public void initInputWidget() {
    }

    @Override
    public IsWidget getInputWidget() {
        return colorPicker;
    }

    @Override
    public String getSupportedCode() {
        return ColorPickerFieldDefinition.CODE;
    }

    @Override
    public IsWidget getPrettyViewWidget() {
        initInputWidget();
        return getInputWidget();
    }

    @Override
    protected void setReadOnly( boolean readOnly ) {

    }
}
