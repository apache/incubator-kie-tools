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


package org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textBox.definition;

import org.kie.workbench.common.forms.adf.definitions.annotations.FormField;
import org.kie.workbench.common.forms.fields.shared.AbstractFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.HasMaxLength;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textBox.type.TextBoxFieldType;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.kie.workbench.common.forms.model.HasPlaceHolder;

public abstract class TextBoxBaseDefinition extends AbstractFieldDefinition implements HasMaxLength,
                                                                                       HasPlaceHolder {

    public static TextBoxFieldType FIELD_TYPE = new TextBoxFieldType();

    @FormField(
            labelKey = "placeHolder",
            afterElement = "label"
    )
    protected String placeHolder = "";

    public TextBoxBaseDefinition(String className) {
        super(className);
    }

    @Override
    public TextBoxFieldType getFieldType() {
        return FIELD_TYPE;
    }

    @Override
    public String getPlaceHolder() {
        return placeHolder;
    }

    @Override
    public void setPlaceHolder(String placeHolder) {
        this.placeHolder = placeHolder;
    }

    @Override
    protected void doCopyFrom(FieldDefinition other) {
        if (other instanceof HasMaxLength) {
            setMaxLength(((HasMaxLength) other).getMaxLength());
        }
        if (other instanceof HasPlaceHolder) {
            setPlaceHolder(((HasPlaceHolder) other).getPlaceHolder());
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }

        TextBoxBaseDefinition that = (TextBoxBaseDefinition) o;

        return placeHolder != null ? placeHolder.equals(that.placeHolder) : that.placeHolder == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (placeHolder != null ? placeHolder.hashCode() : 0);
        result = ~~result;
        return result;
    }
}
