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


package org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.decimalBox.definition;

import javax.validation.constraints.Min;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormDefinition;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormField;
import org.kie.workbench.common.forms.adf.definitions.annotations.i18n.I18nSettings;
import org.kie.workbench.common.forms.fields.shared.AbstractFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.HasMaxLength;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.decimalBox.type.DecimalBoxFieldType;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.kie.workbench.common.forms.model.HasPlaceHolder;

@Portable
@Bindable
@FormDefinition(
        i18n = @I18nSettings(keyPreffix = "FieldProperties"),
        startElement = "label"
)
public class DecimalBoxFieldDefinition extends AbstractFieldDefinition implements HasMaxLength,
                                                                                  HasPlaceHolder {

    public static DecimalBoxFieldType FIELD_TYPE = new DecimalBoxFieldType();

    @FormField(
            labelKey = "placeHolder",
            afterElement = "label"
    )
    protected String placeHolder = "";

    @FormField(
            labelKey = "maxLength",
            afterElement = "placeHolder"
    )
    @Min(1)
    protected Integer maxLength = 100;

    public DecimalBoxFieldDefinition() {
        super(Double.class.getName());
    }

    @Override
    public DecimalBoxFieldType getFieldType() {
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
    public Integer getMaxLength() {
        return maxLength;
    }

    @Override
    public void setMaxLength(Integer maxLength) {
        this.maxLength = maxLength;
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

        DecimalBoxFieldDefinition that = (DecimalBoxFieldDefinition) o;

        if (placeHolder != null ? !placeHolder.equals(that.placeHolder) : that.placeHolder != null) {
            return false;
        }
        return maxLength != null ? maxLength.equals(that.maxLength) : that.maxLength == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (placeHolder != null ? placeHolder.hashCode() : 0);
        result = ~~result;
        result = 31 * result + (maxLength != null ? maxLength.hashCode() : 0);
        result = ~~result;
        return result;
    }
}
