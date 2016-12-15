/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.basicset.definition.property;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.stunner.core.definition.annotation.Description;
import org.kie.workbench.common.stunner.core.definition.annotation.Property;
import org.kie.workbench.common.stunner.core.definition.annotation.property.*;
import org.kie.workbench.common.stunner.core.definition.property.PropertyType;
import org.kie.workbench.common.stunner.core.definition.property.type.EnumType;
import org.kie.workbench.common.stunner.shapes.def.icon.dynamics.Icons;

import java.util.ArrayList;

@Portable
@Bindable
@Property
public class IconType {

    @Caption
    public static final transient String caption = "Icon type";

    @Description
    public static final transient String description = "The icon type";

    @ReadOnly
    public static final Boolean readOnly = false;

    @Optional
    public static final Boolean optional = false;

    @Type
    public static final PropertyType type = new EnumType();

    @DefaultValue
    public static final transient Icons defaultValue = Icons.PLUS;

    @Value
    private Icons value = defaultValue;

    @AllowedValues
    public static final Iterable<Icons> allowedValues = new ArrayList<Icons>( 3 ) {{
        add( Icons.PLUS );
        add( Icons.MINUS );
        add( Icons.XOR );
    }};

    public IconType() {
    }

    public IconType( final Icons icon ) {
        this.value = icon;
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

    public Icons getDefaultValue() {
        return defaultValue;
    }

    public Icons getValue() {
        return value;
    }

    public void setValue( Icons value ) {
        this.value = value;
    }

    public Iterable<Icons> getAllowedValues() {
        return allowedValues;
    }

}
