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


package org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.image.definition;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormDefinition;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormField;
import org.kie.workbench.common.forms.adf.definitions.annotations.i18n.I18nSettings;
import org.kie.workbench.common.forms.fields.shared.AbstractFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.image.type.PictureFieldType;
import org.kie.workbench.common.forms.model.FieldDefinition;

@Portable
@Bindable
@FormDefinition(
        i18n = @I18nSettings(keyPreffix = "FieldProperties"),
        startElement = "label"
)
public class PictureFieldDefinition extends AbstractFieldDefinition {

    public static final PictureFieldType FIELD_TYPE = new PictureFieldType();

    @FormField(
            labelKey = "picture.size",
            afterElement = "label"
    )
    private PictureSize size = PictureSize.SMALL;

    public PictureFieldDefinition() {
        super(String.class.getName());
    }

    @Override
    public PictureFieldType getFieldType() {
        return FIELD_TYPE;
    }

    public PictureSize getSize() {
        return size;
    }

    public void setSize(PictureSize size) {
        this.size = size;
    }

    @Override
    protected void doCopyFrom(FieldDefinition other) {
        if (other instanceof PictureFieldDefinition) {
            PictureFieldDefinition otherPicture = (PictureFieldDefinition) other;
            setSize(otherPicture.getSize());
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

        PictureFieldDefinition that = (PictureFieldDefinition) o;

        return size == that.size;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (size != null ? size.hashCode() : 0);
        result = ~~result;
        return result;
    }
}
