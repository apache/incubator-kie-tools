/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.dmn.api.property.dimensions;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.dmn.api.property.DMNProperty;
import org.kie.workbench.common.forms.adf.definitions.annotations.metaModel.FieldDefinition;
import org.kie.workbench.common.forms.adf.definitions.annotations.metaModel.FieldLabel;
import org.kie.workbench.common.forms.adf.definitions.annotations.metaModel.FieldReadOnly;
import org.kie.workbench.common.forms.adf.definitions.annotations.metaModel.FieldValue;
import org.kie.workbench.common.forms.adf.definitions.annotations.metaModel.LabelMode;
import org.kie.workbench.common.stunner.core.definition.annotation.Description;
import org.kie.workbench.common.stunner.core.definition.annotation.Property;
import org.kie.workbench.common.stunner.core.definition.annotation.property.Caption;
import org.kie.workbench.common.stunner.core.definition.annotation.property.DefaultValue;
import org.kie.workbench.common.stunner.core.definition.annotation.property.Optional;
import org.kie.workbench.common.stunner.core.definition.annotation.property.ReadOnly;
import org.kie.workbench.common.stunner.core.definition.annotation.property.Type;
import org.kie.workbench.common.stunner.core.definition.annotation.property.Value;
import org.kie.workbench.common.stunner.core.definition.property.PropertyMetaTypes;
import org.kie.workbench.common.stunner.core.definition.property.PropertyType;
import org.kie.workbench.common.stunner.core.definition.property.type.DoubleType;

@Portable
@Bindable
@Property(meta = PropertyMetaTypes.WIDTH)
@FieldDefinition(labelMode = LabelMode.OVERRIDE_I18N_KEY)
public class Width implements DMNProperty {

    @Caption
    @FieldLabel
    public static final transient String caption = "width";

    @Description
    public static final transient String description = "The width";

    @Type
    public static final PropertyType type = new DoubleType();

    @ReadOnly
    @FieldReadOnly
    private Boolean readOnly = false;

    @Optional
    public static final Boolean optional = false;

    @DefaultValue
    public static final Double defaultValue = 100d;

    @Value
    @Min(50)
    @Max(200)
    @FieldValue
    private Double value = defaultValue;

    public Width() {
    }

    public Width(final Double value) {
        this.value = value;
    }

    public String getCaption() {
        return caption;
    }

    public String getDescription() {
        return description;
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public boolean isOptional() {
        return optional;
    }

    public PropertyType getType() {
        return type;
    }

    public Double getDefaultValue() {
        return defaultValue;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(final Double value) {
        this.value = value;
    }
}