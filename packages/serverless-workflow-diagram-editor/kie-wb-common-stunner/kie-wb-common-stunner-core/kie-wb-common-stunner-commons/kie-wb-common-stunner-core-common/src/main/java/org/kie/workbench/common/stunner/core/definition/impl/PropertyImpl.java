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


package org.kie.workbench.common.stunner.core.definition.impl;

import java.util.Objects;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.stunner.core.definition.property.PropertyType;

@Portable
public class PropertyImpl<C> {

    private final String id;
    private String caption;
    private String description;
    private boolean isReadOnly;
    private boolean isOptional;
    protected C defaultValue;
    protected C value;
    private final PropertyType type;

    public PropertyImpl(final @MapsTo("id") String id,
                        final @MapsTo("caption") String caption,
                        final @MapsTo("description") String description,
                        final @MapsTo("isReadOnly") boolean isReadOnly,
                        final @MapsTo("isOptional") boolean isOptional,
                        final @MapsTo("defaultValue") C defaultValue,
                        final @MapsTo("value") C value,
                        final @MapsTo("type") PropertyType type) {
        this.id = checkNotNull("id", id);
        this.caption = checkNotNull("caption", caption);
        this.description = checkNotNull("description", description);
        this.type = checkNotNull("type", type);
        this.isReadOnly = isReadOnly;
        this.isOptional = isOptional;
        this.defaultValue = defaultValue;
        this.value = value;
    }

    private static <T> T checkNotNull(String objName, T obj) {
        return Objects.requireNonNull(obj, "Parameter named '" + objName + "' should be not null!");
    }

    public String getId() {
        return id;
    }

    public String getCaption() {
        return caption;
    }

    public String getDescription() {
        return description;
    }

    public boolean isReadOnly() {
        return isReadOnly;
    }

    public boolean isOptional() {
        return isOptional;
    }

    public PropertyType getType() {
        return type;
    }

    public PropertyImpl setCaption(String caption) {
        this.caption = caption;
        return this;
    }

    public PropertyImpl setDescription(String description) {
        this.description = description;
        return this;
    }

    public PropertyImpl setReadOnly(boolean readOnly) {
        isReadOnly = readOnly;
        return this;
    }

    public PropertyImpl setOptional(boolean optional) {
        isOptional = optional;
        return this;
    }

    public C getValue() {
        return value;
    }

    public void setValue(C value) {
        this.value = value;
    }

    public C getDefaultValue() {
        return defaultValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PropertyImpl)) {
            return false;
        }
        PropertyImpl that = (PropertyImpl) o;
        if (isOptional != that.isOptional) {
            return false;
        }
        if (isReadOnly != that.isReadOnly) {
            return false;
        }
        if (!caption.equals(that.caption)) {
            return false;
        }
        if (!description.equals(that.description)) {
            return false;
        }
        if (!id.equals(that.id)) {
            return false;
        }
        if (!getType().equals(that.getType())) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = ~~result;
        result = 31 * result + getType().hashCode();
        result = ~~result;
        result = 31 * result + caption.hashCode();
        result = ~~result;
        result = 31 * result + description.hashCode();
        result = ~~result;
        result = 31 * result + (isReadOnly ? 1 : 0);
        result = ~~result;
        result = 31 * result + (isOptional ? 1 : 0);
        result = ~~result;
        return result;
    }

    @Override
    public String toString() {
        return "PropertyImpl{" +
                "id='" + id + '\'' +
                ", type=" + getType() +
                ", caption='" + caption + '\'' +
                ", description='" + description + '\'' +
                ", isReadOnly=" + isReadOnly +
                ", isOptional=" + isOptional +
                '}';
    }
}
