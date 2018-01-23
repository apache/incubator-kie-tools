/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.core.definition.adapter;

import java.util.Map;

import org.kie.workbench.common.stunner.core.definition.property.PropertyType;

/**
 * A Property pojo adapter..
 */
public interface PropertyAdapter<T, V> extends PriorityAdapter {

    /**
     * Returns the property's identifier for a given pojo.
     */
    String getId(final T pojo);

    /**
     * Returns the property's type for a given pojo.
     */
    PropertyType getType(final T pojo);

    /**
     * Returns the property's caption for a given pojo.
     */
    String getCaption(final T pojo);

    /**
     * Returns the property's description for a given pojo.
     */
    String getDescription(final T pojo);

    /**
     * Specifies if the property is read only.
     */
    boolean isReadOnly(final T pojo);

    /**
     * Specifies if the property is optional.
     */
    boolean isOptional(final T pojo);

    /**
     * Returns the property's value for a given pojo.
     */
    V getValue(final T pojo);

    /**
     * Returns allowed values for this property, if multiple.
     * Otherwise returns null,.
     */
    Map<V, String> getAllowedValues(final T pojo);

    /**
     * Update's the property value for a given pojo..
     */
    void setValue(final T pojo,
                  final V value);
}
