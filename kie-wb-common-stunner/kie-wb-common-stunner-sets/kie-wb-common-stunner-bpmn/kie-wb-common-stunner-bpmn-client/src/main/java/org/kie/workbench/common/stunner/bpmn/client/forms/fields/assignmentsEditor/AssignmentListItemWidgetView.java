/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.client.forms.fields.assignmentsEditor;

import java.util.Set;

import org.jboss.errai.ui.client.widget.HasModel;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.i18n.StunnerFormsClientFieldsConstants;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.model.AssignmentRow;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.model.Variable.VariableType;
import org.kie.workbench.common.stunner.bpmn.client.forms.util.ListBoxValues;

public interface AssignmentListItemWidgetView extends HasModel<AssignmentRow> {

    String CUSTOM_PROMPT = StunnerFormsClientFieldsConstants.INSTANCE.Custom() + ListBoxValues.EDIT_SUFFIX;
    String ENTER_TYPE_PROMPT = StunnerFormsClientFieldsConstants.INSTANCE.Enter_type() + ListBoxValues.EDIT_SUFFIX;
    String CONSTANT_PROMPT = StunnerFormsClientFieldsConstants.INSTANCE.Constant() + ListBoxValues.EDIT_SUFFIX;
    String ENTER_CONSTANT_PROMPT = StunnerFormsClientFieldsConstants.INSTANCE.Enter_constant() + ListBoxValues.EDIT_SUFFIX;

    void init();

    void setParentWidget(final ActivityDataIOEditorWidget parentWidget);

    void setDataTypes(final ListBoxValues dataTypeListBoxValues);

    void setProcessVariables(final ListBoxValues processVarListBoxValues);

    void setShowConstants(final boolean showConstants);

    void setDisallowedNames(final Set<String> disallowedNames,
                            final String disallowedNameErrorMessage);

    void setAllowDuplicateNames(final boolean allowDuplicateNames,
                                final String duplicateNameErrorMessage);

    boolean isDuplicateName(final String name);

    VariableType getVariableType();

    String getDataType();

    void setDataType(final String dataType);

    String getProcessVar();

    void setProcessVar(final String processVar);

    String getCustomDataType();

    void setCustomDataType(final String customDataType);

    String getConstant();

    void setConstant(final String constant);

    void setReadOnly(final boolean readOnly);
}
