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


package org.kie.workbench.common.stunner.bpmn.client.forms.fields.variablesEditor;

import java.util.List;

import org.jboss.errai.ui.client.widget.HasModel;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.model.Variable.VariableType;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.model.VariableRow;
import org.kie.workbench.common.stunner.bpmn.client.forms.util.ListBoxValues;

public interface VariableListItemWidgetView extends HasModel<VariableRow> {

    String CUSTOM_PROMPT = "Custom" + ListBoxValues.EDIT_SUFFIX;
    String ENTER_TYPE_PROMPT = "Enter type" + ListBoxValues.EDIT_SUFFIX;
    String ENTER_TAG_PROMPT = "Enter tag" + ListBoxValues.EDIT_SUFFIX;

    void init();

    void setParentWidget(final VariablesEditorWidgetView.Presenter parentWidget);

    void notifyModelChanged();

    void setDataTypes(final ListBoxValues dataTypeListBoxValues);

    void setTagTypes(List<String> tagTypes);

    VariableType getVariableType();

    String getDataTypeDisplayName();

    void setDataTypeDisplayName(final String dataTypeDisplayName);

    String getCustomDataType();

    void setCustomDataType(final String customDataType);

    void setCustomTags(final List<String> tags);

    List<String> getCustomTags();

    void setReadOnly(final boolean readOnly);

    void setTagsNotEnabled();
}
