/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
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

    void setParentWidget( ActivityDataIOEditorWidget parentWidget );

    void setDataTypes( ListBoxValues dataTypeListBoxValues );

    void setProcessVariables( ListBoxValues processVarListBoxValues );

    void setShowConstants( boolean showConstants );

    void setDisallowedNames( Set<String> disallowedNames, String disallowedNameErrorMessage );

    void setAllowDuplicateNames( boolean allowDuplicateNames, String duplicateNameErrorMessage );

    boolean isDuplicateName( String name );

    VariableType getVariableType();

    String getDataType();

    void setDataType( String dataType );

    String getProcessVar();

    void setProcessVar( String processVar );

    String getCustomDataType();

    void setCustomDataType( String customDataType );

    String getConstant();

    void setConstant( String constant );
}
