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
import org.kie.workbench.common.forms.common.rendering.client.widgets.decimalBox.DecimalBox;
import org.kie.workbench.common.forms.dynamic.client.rendering.FieldRenderer;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.decimalBox.definition.DecimalBoxFieldDefinition;

@Dependent
public class DecimalBoxFieldRenderer extends FieldRenderer<DecimalBoxFieldDefinition>
        implements RequiresValueConverter {

    private DecimalBox decimalBox;

    @Inject
    public DecimalBoxFieldRenderer(DecimalBox decimalBox) {
        this.decimalBox = decimalBox;
    }

    @Override
    public String getName() {
        return "DecimalBox";
    }

    @Override
    public void initInputWidget() {

        decimalBox.setId(field.getId());
        decimalBox.setPlaceholder(field.getPlaceHolder());
        decimalBox.setMaxLength(field.getMaxLength());
        decimalBox.setEnabled(!field.getReadOnly());
    }

    @Override
    public IsWidget getInputWidget() {
        return decimalBox;
    }

    @Override
    public IsWidget getPrettyViewWidget() {
        return new HTML();
    }

    @Override
    public String getSupportedCode() {
        return DecimalBoxFieldDefinition.FIELD_TYPE.getTypeName();
    }

    @Override
    protected void setReadOnly(boolean readOnly) {
        decimalBox.setEnabled(!readOnly);
    }

    @Override
    public Converter getConverter() {
        return ValueConvertersFactory.getConverterForType(field.getStandaloneClassName());
    }
}
