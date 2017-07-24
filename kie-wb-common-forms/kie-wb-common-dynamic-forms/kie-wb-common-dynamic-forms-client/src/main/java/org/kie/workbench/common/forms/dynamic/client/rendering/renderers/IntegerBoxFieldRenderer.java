/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.forms.dynamic.client.rendering.renderers;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.databinding.client.api.Converter;
import org.kie.workbench.common.forms.common.rendering.client.util.valueConverters.ValueConvertersFactory;
import org.kie.workbench.common.forms.common.rendering.client.widgets.integerBox.IntegerBox;
import org.kie.workbench.common.forms.dynamic.client.rendering.FieldRenderer;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.integerBox.definition.IntegerBoxFieldDefinition;

@Dependent
public class IntegerBoxFieldRenderer extends FieldRenderer<IntegerBoxFieldDefinition>
        implements RequiresValueConverter {

    private IntegerBox integerBox;

    @Inject
    public IntegerBoxFieldRenderer(IntegerBox integerBox) {
        this.integerBox = integerBox;
    }

    @Override
    public String getName() {
        return "IntegerBox";
    }

    @Override
    public void initInputWidget() {
        integerBox.setId(field.getId());
        integerBox.setPlaceholder(field.getPlaceHolder());
        integerBox.setMaxLength(field.getMaxLength());
        integerBox.setEnabled(!field.getReadOnly());
    }

    @Override
    public IsWidget getInputWidget() {
        return integerBox;
    }

    @Override
    public IsWidget getPrettyViewWidget() {
        return new HTML();
    }

    @Override
    public String getSupportedCode() {
        return IntegerBoxFieldDefinition.FIELD_TYPE.getTypeName();
    }

    @Override
    protected void setReadOnly(boolean readOnly) {
        integerBox.setEnabled(!readOnly);
    }

    @Override
    public Converter getConverter() {
        return ValueConvertersFactory.getConverterForType(field.getStandaloneClassName());
    }
}
