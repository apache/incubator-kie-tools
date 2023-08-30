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
package org.kie.workbench.common.dmn.api.property.dmn;

import java.util.Objects;
import java.util.Optional;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.dmn.api.property.DMNProperty;
import org.kie.workbench.common.forms.adf.definitions.annotations.metaModel.FieldDefinition;
import org.kie.workbench.common.forms.adf.definitions.annotations.metaModel.FieldValue;
import org.kie.workbench.common.forms.adf.definitions.annotations.metaModel.I18nMode;
import org.kie.workbench.common.stunner.core.definition.annotation.Property;
import org.kie.workbench.common.stunner.core.definition.annotation.property.Type;
import org.kie.workbench.common.stunner.core.definition.annotation.property.Value;
import org.kie.workbench.common.stunner.core.definition.property.PropertyMetaTypes;
import org.kie.workbench.common.stunner.core.definition.property.PropertyType;
import org.kie.workbench.common.stunner.core.util.HashUtil;

@Portable
@Bindable
@Property(meta = PropertyMetaTypes.NAME)
@FieldDefinition(i18nMode = I18nMode.DONT_OVERRIDE)
public class NameHolder implements DMNProperty {

    public static final String DEFAULT_NAME = "";

    @Type
    public static final PropertyType type = new NamePropertyType();

    @Value
    @FieldValue
    private Name value;

    public NameHolder() {
        this(new Name(DEFAULT_NAME));
    }

    public NameHolder(final Name value) {
        this.value = value;
    }

    public NameHolder copy() {
        return new NameHolder(Optional.ofNullable(value).map(Name::copy).orElse(null));
    }

    public Name getValue() {
        return value;
    }

    public void setValue(final Name value) {
        this.value = value;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof NameHolder)) {
            return false;
        }

        final NameHolder name = (NameHolder) o;

        return Objects.equals(value, name.value);
    }

    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes(value != null ? value.hashCode() : 0);
    }
}
