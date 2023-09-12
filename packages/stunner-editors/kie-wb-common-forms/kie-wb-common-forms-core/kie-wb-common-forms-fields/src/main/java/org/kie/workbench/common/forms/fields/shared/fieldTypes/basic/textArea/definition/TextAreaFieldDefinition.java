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


package org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textArea.definition;

import javax.validation.constraints.Min;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormDefinition;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormField;
import org.kie.workbench.common.forms.adf.definitions.annotations.i18n.I18nSettings;
import org.kie.workbench.common.forms.fields.shared.AbstractFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.HasRows;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textArea.type.TextAreaFieldType;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.kie.workbench.common.forms.model.HasPlaceHolder;

@Portable
@Bindable
@FormDefinition(
        i18n = @I18nSettings(keyPreffix = "FieldProperties"),
        startElement = "label"
)
public class TextAreaFieldDefinition extends AbstractFieldDefinition implements HasRows,
                                                                                HasPlaceHolder {

    public static final TextAreaFieldType FIELD_TYPE = new TextAreaFieldType();

    @FormField(
            labelKey = "placeHolder",
            afterElement = "label"
    )
    protected String placeHolder = "";

    @FormField(
            labelKey = "rows",
            afterElement = "placeHolder"
    )
    @Min(1)
    protected Integer rows = 4;

    public TextAreaFieldDefinition() {
        super(String.class.getName());
    }

    @Override
    public TextAreaFieldType getFieldType() {
        return FIELD_TYPE;
    }

    @Override
    public Integer getRows() {
        return rows;
    }

    @Override
    public void setRows(Integer rows) {
        this.rows = rows;
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
        if (other instanceof TextAreaFieldDefinition) {
            TextAreaFieldDefinition otherTextArea = (TextAreaFieldDefinition) other;
            this.setRows(otherTextArea.getRows());
            this.setPlaceHolder(otherTextArea.getPlaceHolder());
        } else {
            if (other instanceof HasRows) {
                setRows(((HasRows) other).getRows());
            }
            if (other instanceof HasPlaceHolder) {
                setPlaceHolder(((HasPlaceHolder) other).getPlaceHolder());
            }
        }
    }
}
