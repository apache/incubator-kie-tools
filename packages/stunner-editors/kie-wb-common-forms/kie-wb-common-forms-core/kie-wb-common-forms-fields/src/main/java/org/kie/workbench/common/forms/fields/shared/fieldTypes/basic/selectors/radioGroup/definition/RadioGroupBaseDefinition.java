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


package org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.radioGroup.definition;

import org.kie.workbench.common.forms.adf.definitions.annotations.FormField;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.SelectorFieldBaseDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.SelectorOption;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.radioGroup.type.RadioGroupFieldType;
import org.kie.workbench.common.forms.model.FieldDefinition;

public abstract class RadioGroupBaseDefinition<OPTIONS extends SelectorOption<TYPE>, TYPE> extends SelectorFieldBaseDefinition<OPTIONS, TYPE> {

    public static final RadioGroupFieldType FIELD_TYPE = new RadioGroupFieldType();

    @FormField(
            labelKey = "radios.inline",
            afterElement = "options"
    )
    protected Boolean inline = Boolean.FALSE;

    public RadioGroupBaseDefinition(String className) {
        super(className);
    }

    @Override
    public RadioGroupFieldType getFieldType() {
        return FIELD_TYPE;
    }

    public Boolean getInline() {
        return inline;
    }

    public void setInline(Boolean inline) {
        this.inline = inline;
    }

    @Override
    protected void doCopyFrom(FieldDefinition other) {
        super.doCopyFrom(other);
        if (other instanceof RadioGroupBaseDefinition) {
            RadioGroupBaseDefinition otherRadio = (RadioGroupBaseDefinition) other;
            setInline(otherRadio.getInline());
        }
    }
}
