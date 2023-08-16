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


package org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.datePicker.definition;

import java.util.Date;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormDefinition;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormField;
import org.kie.workbench.common.forms.adf.definitions.annotations.i18n.I18nSettings;
import org.kie.workbench.common.forms.fields.shared.AbstractFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.datePicker.type.DatePickerFieldType;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.kie.workbench.common.forms.model.HasPlaceHolder;

@Portable
@Bindable
@FormDefinition(
        i18n = @I18nSettings(keyPreffix = "FieldProperties"),
        startElement = "label"
)
public class DatePickerFieldDefinition extends AbstractFieldDefinition implements HasPlaceHolder {

    public static final DatePickerFieldType FIELD_TYPE = new DatePickerFieldType();

    @FormField(
            labelKey = "placeHolder",
            afterElement = "label"
    )
    protected String placeHolder = "";

    @FormField(
            labelKey = "showTime",
            afterElement = "placeHolder"
    )
    protected Boolean showTime = Boolean.TRUE;

    public DatePickerFieldDefinition() {
        super(Date.class.getName());
    }

    @Override
    public DatePickerFieldType getFieldType() {
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

    public Boolean getShowTime() {
        return showTime;
    }

    public void setShowTime(Boolean showTime) {
        this.showTime = showTime;
    }

    @Override
    protected void doCopyFrom(FieldDefinition other) {
        if (other instanceof DatePickerFieldDefinition) {
            DatePickerFieldDefinition otherDate = (DatePickerFieldDefinition) other;
            setPlaceHolder(otherDate.getPlaceHolder());
            setShowTime(otherDate.getShowTime());
        } else if (other instanceof HasPlaceHolder) {
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

        DatePickerFieldDefinition that = (DatePickerFieldDefinition) o;

        if (placeHolder != null ? !placeHolder.equals(that.placeHolder) : that.placeHolder != null) {
            return false;
        }
        return showTime != null ? showTime.equals(that.showTime) : that.showTime == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (placeHolder != null ? placeHolder.hashCode() : 0);
        result = ~~result;
        result = 31 * result + (showTime != null ? showTime.hashCode() : 0);
        result = ~~result;
        return result;
    }
}
