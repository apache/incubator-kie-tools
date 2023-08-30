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


package org.kie.workbench.common.stunner.bpmn.client.forms.fields.assignmentsEditor;

import java.util.Set;

import org.jboss.errai.ui.client.widget.HasModel;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.i18n.StunnerFormsClientFieldsConstants;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.model.AssignmentRow;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.model.Variable.VariableType;
import org.kie.workbench.common.stunner.bpmn.client.forms.util.ListBoxValues;

public interface AssignmentListItemWidgetView extends HasModel<AssignmentRow> {

    String CUSTOM_PROMPT = StunnerFormsClientFieldsConstants.CONSTANTS.Custom() + ListBoxValues.EDIT_SUFFIX;
    String ENTER_TYPE_PROMPT = StunnerFormsClientFieldsConstants.CONSTANTS.Enter_type() + ListBoxValues.EDIT_SUFFIX;
    String EXPRESSION_PROMPT = StunnerFormsClientFieldsConstants.CONSTANTS.Expression() + ListBoxValues.EDIT_SUFFIX;
    String ENTER_EXPRESSION_PROMPT = StunnerFormsClientFieldsConstants.CONSTANTS.Enter_expression() + ListBoxValues.EDIT_SUFFIX;

    void init();

    void setParentWidget(final ActivityDataIOEditorWidget parentWidget);

    void setDataTypes(final ListBoxValues dataTypeListBoxValues);

    void setProcessVariables(final ListBoxValues processVarListBoxValues);

    void setShowExpressions(final boolean showExpressions);

    void setDisallowedNames(final Set<String> disallowedNames,
                            final String disallowedNameErrorMessage);

    void setAllowDuplicateNames(final boolean allowDuplicateNames,
                                final String duplicateNameErrorMessage);

    boolean isDuplicateName(final String name);

    boolean isMultipleInstanceVariable(final String name);

    VariableType getVariableType();

    String getDataType();

    void setDataType(final String dataType);

    String getProcessVar();

    void setProcessVar(final String processVar);

    String getCustomDataType();

    void setCustomDataType(final String customDataType);

    String getExpression();

    void setExpression(final String expression);

    void setReadOnly(final boolean readOnly);
}
