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

package org.kie.workbench.common.stunner.bpmn.forms.model;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.forms.metaModel.FieldDef;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.kie.workbench.common.stunner.bpmn.forms.meta.definition.AssigneeEditor;

import static org.kie.workbench.common.stunner.bpmn.util.FieldLabelConstants.FIELDDEF_DEFAULT_VALUE;
import static org.kie.workbench.common.stunner.bpmn.util.FieldLabelConstants.FIELDDEF_TYPE;

@Portable
@Bindable
public class AssigneeEditorFieldDefinition extends FieldDefinition {

    public static final String CODE = "AssigneeEditor";

    @FieldDef( label = FIELDDEF_DEFAULT_VALUE )
    @AssigneeEditor
    private String defaultValue;

    @FieldDef( label = FIELDDEF_TYPE )
    @AssigneeEditor
    private AssigneeType type;

    public AssigneeEditorFieldDefinition() {
        super( CODE );
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue( String defaultValue ) {
        this.defaultValue = defaultValue;
    }

    public AssigneeType getType() {
        return type;
    }

    public void setType( AssigneeType type ) {
        this.type = type;
    }

    @Override
    protected void doCopyFrom( FieldDefinition other ) {
        if ( other instanceof AssigneeEditorFieldDefinition ) {
            this.setDefaultValue( ( ( AssigneeEditorFieldDefinition ) other ).getDefaultValue() );
            this.setType( ( ( AssigneeEditorFieldDefinition ) other ).getType() );
        }
    }
}
