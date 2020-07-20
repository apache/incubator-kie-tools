/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.client.forms.fields.artifacts;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.text.shared.Renderer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasValue;
import elemental2.promise.IThenable;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.ValueListBox;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.stunner.bpmn.client.forms.DataTypeNamesService;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.i18n.StunnerFormsClientFieldsConstants;
import org.kie.workbench.common.stunner.bpmn.client.forms.util.ListBoxValues;
import org.kie.workbench.common.stunner.bpmn.client.forms.util.StringUtils;
import org.kie.workbench.common.stunner.bpmn.client.forms.widgets.ComboBox;
import org.kie.workbench.common.stunner.bpmn.client.forms.widgets.ComboBoxView;
import org.kie.workbench.common.stunner.bpmn.client.forms.widgets.CustomDataTypeTextBox;
import org.kie.workbench.common.stunner.bpmn.definition.property.artifacts.DataObjectTypeValue;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.uberfire.backend.vfs.Path;

import static java.util.stream.Collectors.toMap;

@Dependent
@Templated
public class DataObjectTypeWidget extends Composite implements HasValue<DataObjectTypeValue>,
                                                               ComboBoxView.ModelPresenter {

    String CUSTOM_PROMPT = "Custom" + ListBoxValues.EDIT_SUFFIX;
    String ENTER_TYPE_PROMPT = "Enter type" + ListBoxValues.EDIT_SUFFIX;

    static final List<String> simpleDataTypes = Arrays.asList("Boolean", "Float", "Integer", "Object", "String");

    private final SessionManager sessionManager;

    @Inject
    protected ComboBox dataTypeComboBox;

    @Inject
    protected DataTypeNamesService clientDataTypesService;

    @Inject
    @DataField
    protected CustomDataTypeTextBox customDataType;

    @Inject
    public DataObjectTypeWidget(final SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    protected DataObjectTypeValue current = new DataObjectTypeValue();

    @PostConstruct
    public void init() {

        dataTypeComboBox.init(this,
                              true,
                              dataType,
                              customDataType,
                              false,
                              true,
                              CUSTOM_PROMPT,
                              ENTER_TYPE_PROMPT);

        customDataType.setRegExp(StringUtils.ALPHA_NUM_UNDERSCORE_DOT_REGEXP,
                                 StunnerFormsClientFieldsConstants.INSTANCE.Removed_invalid_characters_from_name(),
                                 StunnerFormsClientFieldsConstants.INSTANCE.Invalid_character_in_name());

        ListBoxValues dataTypeListBoxValues = new ListBoxValues(CUSTOM_PROMPT, "Edit ", null);

        clientDataTypesService
                .call(getDiagramPath())
                .then(getListObjectThenOnFulfilledCallbackFn(simpleDataTypes, dataTypeListBoxValues))
                .catch_(exception -> {
                    dataTypeListBoxValues.addValues(simpleDataTypes);
                    return null;
                });

        dataTypeComboBox.setCurrentTextValue("");
        dataTypeComboBox.setListBoxValues(dataTypeListBoxValues);
        dataTypeComboBox.setShowCustomValues(true);
    }

    public static Map<String, String> getMapDataTypeNamesToDisplayNames() {
        return mapDataTypeNamesToDisplayNames;
    }

    private static final Map<String, String> mapDataTypeNamesToDisplayNames = createMapForSimpleDataTypes();

    static Map<String, String> createMapForSimpleDataTypes() {
        if (simpleDataTypes == null) {
            return new HashMap<>();
        }
        return simpleDataTypes.stream().collect(toMap(x -> x, x -> x));
    }

    static IThenable.ThenOnFulfilledCallbackFn<List<String>, Object> getListObjectThenOnFulfilledCallbackFn(List<String> simpleDataTypes, ListBoxValues dataTypeListBoxValues) {
        return serverDataTypes -> {
            List<String> mergedList = new ArrayList<>(simpleDataTypes);

            for (String type : serverDataTypes) {
                String displayType = StringUtils.createDataTypeDisplayName(type);
                getMapDataTypeNamesToDisplayNames().put(
                        displayType,
                        type
                );
                mergedList.add(displayType);
            }

            dataTypeListBoxValues.addValues(mergedList);
            return null;
        };
    }

    public Path getDiagramPath() {
        final Diagram diagram = sessionManager.getCurrentSession().getCanvasHandler().getDiagram();
        return diagram.getMetadata().getPath();
    }

    @Override
    public DataObjectTypeValue getValue() {
        current.setType(getDisplayName(getFirstIfExistsOrSecond(customDataType.getValue(), dataType.getValue())));
        return current;
    }

    @Override
    public void setValue(DataObjectTypeValue value) {
        setValue(value, false);
    }

    @Override
    public void setValue(DataObjectTypeValue value, boolean fireEvents) {
        if (value != null) {
            DataObjectTypeValue oldValue = current;
            current = value;

            if (fireEvents) {
                ValueChangeEvent.fireIfNotEqual(this,
                                                oldValue,
                                                current);
            } else {
                final String type = value.getType();
                dataType.setValue(type);
            }
        }
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<DataObjectTypeValue> handler) {

        return addHandler(handler,
                          ValueChangeEvent.getType());
    }

    public void setReadOnly(boolean readOnly) {
        dataType.setEnabled(!readOnly);
        dataTypeComboBox.setReadOnly(readOnly);
        customDataType.setEnabled(!readOnly);
    }

    @DataField
    protected ValueListBox<String> dataType = new ValueListBox<>(new Renderer<String>() {
        public String render(final String value) {
            return getNonNullName(value);
        }

        public void render(final String value,
                           final Appendable appendable) throws IOException {
            String s = render(value);
            appendable.append(s);
        }
    });

    @Override
    public void setTextBoxModelValue(TextBox textBox, String value) {
    }

    @Override
    public void setListBoxModelValue(ValueListBox<String> listBox, String value) {
    }

    @Override
    public String getModelValue(ValueListBox<String> listBox) {
        return getFirstIfExistsOrSecond(customDataType.getValue(), dataTypeComboBox.getValue());
    }

    protected String oldValue = "";

    @Override
    public void notifyModelChanged() {
        String currentValue = dataType.getValue();

        if (currentValue != null && !currentValue.equals(oldValue)) {
            setValue(new DataObjectTypeValue(currentValue), true);
        }
        oldValue = currentValue;
    }

    static String getDisplayName(String realType) {
        if (getMapDataTypeNamesToDisplayNames().containsKey(realType)) {
            return getMapDataTypeNamesToDisplayNames().get(realType);
        }

        return realType;
    }

    private static boolean isNullOrEmpty(String value) {
        return value == null || value.isEmpty();
    }

    static String getNonNullName(String name) {
        return name == null ? "" : name;
    }

    static String getFirstIfExistsOrSecond(String first, String second) {
        return isNullOrEmpty(first) ? second : first;
    }
}
