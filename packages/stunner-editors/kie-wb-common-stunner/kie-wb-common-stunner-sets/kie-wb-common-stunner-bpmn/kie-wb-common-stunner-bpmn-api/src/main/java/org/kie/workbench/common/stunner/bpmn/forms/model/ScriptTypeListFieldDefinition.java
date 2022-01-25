/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.forms.model;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.forms.fields.shared.AbstractFieldDefinition;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.ScriptTypeListValue;

import static org.kie.workbench.common.stunner.bpmn.forms.model.ScriptTypeMode.ACTION_SCRIPT;

@Portable
@Bindable
public class ScriptTypeListFieldDefinition extends AbstractFieldDefinition {

    public static final ScriptTypeListFieldType FIELD_TYPE = new ScriptTypeListFieldType();

    private ScriptTypeMode mode = ACTION_SCRIPT;

    public ScriptTypeListFieldDefinition() {
        super(ScriptTypeListValue.class.getName());
    }

    @Override
    public ScriptTypeListFieldType getFieldType() {
        return FIELD_TYPE;
    }

    public ScriptTypeMode getMode() {
        return mode;
    }

    public void setMode(ScriptTypeMode mode) {
        this.mode = mode;
    }

    @Override
    protected void doCopyFrom(FieldDefinition other) {
        //no processing is needed for this field type
    }
}