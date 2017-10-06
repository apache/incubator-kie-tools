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

package org.kie.workbench.common.forms.dynamic.client.rendering.renderers.selectors.listBox;

import java.util.Map;
import java.util.Set;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;
import org.gwtbootstrap3.client.ui.ValueListBox;
import org.jboss.errai.databinding.client.api.Converter;
import org.kie.workbench.common.forms.common.rendering.client.util.valueConverters.ValueConvertersFactory;
import org.kie.workbench.common.forms.common.rendering.client.widgets.util.DefaultValueListBoxRenderer;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.RequiresValueConverter;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.selectors.SelectorFieldRenderer;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.SelectorOption;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.listBox.definition.ListBoxBaseDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.listBox.type.ListBoxFieldType;

public abstract class AbstractListBoxFieldRenderer<FIELD extends ListBoxBaseDefinition<OPTION, TYPE>, OPTION extends SelectorOption<TYPE>, TYPE>
        extends SelectorFieldRenderer<FIELD, OPTION, TYPE>
        implements RequiresValueConverter {

    protected DefaultValueListBoxRenderer<TYPE> optionsRenderer = new DefaultValueListBoxRenderer();

    protected ValueListBox<TYPE> widgetList = new ValueListBox<>(optionsRenderer);

    @Override
    public String getName() {
        return ListBoxFieldType.NAME;
    }

    @Override
    protected void refreshInput(Map<TYPE, String> optionsValues,
                                TYPE selectedValue) {
        Set<TYPE> values = optionsValues.keySet();

        boolean hasEmpty = values.contains(null) || values.contains(getEmptyValue());

        if (!hasEmpty) {
            optionsValues.put(null,
                              "");
        }

        if (widgetList.getValue() == null && optionsValues.containsKey(selectedValue)) {
            widgetList.setValue(selectedValue);
        }

        optionsRenderer.setValues(optionsValues);
        widgetList.setAcceptableValues(optionsValues.keySet());
    }

    @Override
    public void initInputWidget() {
        widgetList.setEnabled(!field.getReadOnly());
        refreshSelectorOptions();
    }

    public abstract TYPE getEmptyValue();

    @Override
    public IsWidget getPrettyViewWidget() {
        return new HTML();
    }

    @Override
    public IsWidget getInputWidget() {
        return widgetList;
    }

    @Override
    public String getSupportedCode() {
        return ListBoxBaseDefinition.FIELD_TYPE.getTypeName();
    }

    @Override
    protected void setReadOnly(boolean readOnly) {
        widgetList.setEnabled(!readOnly);
    }

    @Override
    public Converter getConverter() {
        return ValueConvertersFactory.getConverterForType(field.getStandaloneClassName());
    }
}
