/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.kie.workbench.common.stunner.bpmn.client.forms.fields.multipleInstanceVariableEditor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import elemental2.promise.IThenable;
import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.ui.client.widget.HasModel;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.model.Variable;
import org.kie.workbench.common.stunner.bpmn.client.forms.util.FieldEditorPresenter;
import org.kie.workbench.common.stunner.bpmn.client.forms.util.ListBoxValues;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.mvp.UberElement;

import static java.util.stream.Collectors.toMap;
import static org.kie.workbench.common.stunner.bpmn.client.forms.util.StringUtils.createDataTypeDisplayName;

public class MultipleInstanceVariableEditorPresenter extends FieldEditorPresenter<String> {

    public interface View extends UberElement<MultipleInstanceVariableEditorPresenter>,
                                  HasModel<Variable> {

        String CUSTOM_PROMPT = "Custom" + ListBoxValues.EDIT_SUFFIX;
        String ENTER_TYPE_PROMPT = "Enter type" + ListBoxValues.EDIT_SUFFIX;

        void setVariableName(String variableName);

        void setVariableType(String variableType);

        String getVariableName();

        String getVariableType();

        String getCustomDataType();

        String getDataTypeDisplayName();

        void setReadOnly(boolean readOnly);
    }

    private final View view;

    private final SessionManager sessionManager;

    private static final Map<String, String> mapDataTypeNamesToDisplayNames = createMapForSimpleDataTypes();

    static final List<String> simpleDataTypes = Arrays.asList("Boolean", "Float", "Integer", "Object", "String");

    @Inject
    public MultipleInstanceVariableEditorPresenter(final View view,
                                                   final SessionManager sessionManager) {
        this.view = view;
        this.sessionManager = sessionManager;
    }

    @PostConstruct
    public void init() {
        view.init(this);
    }

    @Override
    protected IsElement getView() {
        return view;
    }

    @Override
    public void setValue(String value) {
        super.value = value;
        Variable variable = Variable.deserialize(value, Variable.VariableType.INPUT, simpleDataTypes);
        view.setModel(variable);
    }

    public List<String> getSimpleDataTypes() {
        return simpleDataTypes;
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        view.setReadOnly(readOnly);
    }

    public void onVariableChange() {
        String oldValue = value;
        value = view.getVariableName() + ":" + view.getVariableType();
        notifyChange(oldValue, value);
    }

    public Path getDiagramPath() {
        final Diagram diagram = sessionManager.getCurrentSession().getCanvasHandler().getDiagram();
        return diagram.getMetadata().getPath();
    }

    public static Map<String, String> getMapDataTypeNamesToDisplayNames() {
        return mapDataTypeNamesToDisplayNames;
    }

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
                if (type.contains("Asset-")) {
                    type = type.substring(6);
                }
                String displayType = createDataTypeDisplayName(type);
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

    static String getDisplayName(String realType) {
        if (getMapDataTypeNamesToDisplayNames().containsKey(realType)) {
            return getMapDataTypeNamesToDisplayNames().get(realType);
        }

        return realType;
    }

    static String getRealType(String value) {
        if (isNullOrEmpty(value)) {
            return "";
        }

        for (Map.Entry<String, String> entry : getMapDataTypeNamesToDisplayNames().entrySet()) {
            if (Objects.equals(value, entry.getValue())) {
                return entry.getKey();
            }
        }
        return value;
    }

    static String getDataType(Variable variable) {
        if (!isNullOrEmpty(variable.getCustomDataType())) {
            return variable.getCustomDataType();
        }

        return isNullOrEmpty(variable.getDataType()) ?
                "Object" :
                variable.getDataType();
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
