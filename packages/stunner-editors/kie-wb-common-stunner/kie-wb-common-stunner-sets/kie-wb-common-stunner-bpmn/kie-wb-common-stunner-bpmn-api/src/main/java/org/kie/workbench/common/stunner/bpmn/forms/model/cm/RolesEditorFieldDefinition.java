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


package org.kie.workbench.common.stunner.bpmn.forms.model.cm;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.forms.fields.shared.AbstractFieldDefinition;
import org.kie.workbench.common.forms.model.FieldDefinition;

@Portable
@Bindable
public class RolesEditorFieldDefinition extends AbstractFieldDefinition {

    public static final RolesEditorFieldType FIELD_TYPE = new RolesEditorFieldType();

    private String defaultValue;

    public RolesEditorFieldDefinition() {
        super(String.class.getName());
    }

    @Override
    public RolesEditorFieldType getFieldType() {
        return FIELD_TYPE;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    @Override
    protected void doCopyFrom(FieldDefinition other) {
        if (other instanceof RolesEditorFieldDefinition) {
            this.setDefaultValue(((RolesEditorFieldDefinition) other).getDefaultValue());
        }
    }
}
