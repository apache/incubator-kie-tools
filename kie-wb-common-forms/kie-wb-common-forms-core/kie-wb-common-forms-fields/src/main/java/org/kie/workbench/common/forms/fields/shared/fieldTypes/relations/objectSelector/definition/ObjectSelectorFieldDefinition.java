/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.objectSelector.definition;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormDefinition;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormField;
import org.kie.workbench.common.forms.adf.definitions.annotations.i18n.I18nSettings;
import org.kie.workbench.common.forms.fields.shared.AbstractFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.EntityRelationField;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.HasMask;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.objectSelector.type.ObjectSelectorFieldType;
import org.kie.workbench.common.forms.model.FieldDefinition;

@Portable
@Bindable
@FormDefinition(
        i18n = @I18nSettings(keyPreffix = "FieldProperties"),
        startElement = "label"
)
public class ObjectSelectorFieldDefinition extends AbstractFieldDefinition implements EntityRelationField,
                                                                                      HasMask {

    public static final ObjectSelectorFieldType FIELD_TYPE = new ObjectSelectorFieldType();

    /*
    Expression to mask the value based on the object properties. The object properties should be surrounded by '{' and
    '}', for example {propertyName}.
    For example, an expression to mask a user object could be like: "{lastName}, {name}", when a given user instance is
    masked it will result on a text like "Shakespeare, William".
     */
    @FormField(
            labelKey = "mask",
            afterElement = "label"
    )
    protected String mask = "";

    public ObjectSelectorFieldDefinition() {
        super(Object.class.getName());
    }

    @Override
    public ObjectSelectorFieldType getFieldType() {
        return FIELD_TYPE;
    }

    @Override
    public String getMask() {
        return mask;
    }

    @Override
    public void setMask(String mask) {
        this.mask = mask;
    }

    @Override
    protected void doCopyFrom(FieldDefinition other) {
        if (other instanceof HasMask) {
            setMask(((HasMask) other).getMask());
        }
    }
}
