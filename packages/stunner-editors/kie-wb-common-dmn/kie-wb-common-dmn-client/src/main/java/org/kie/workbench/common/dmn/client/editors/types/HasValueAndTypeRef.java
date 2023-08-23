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

package org.kie.workbench.common.dmn.client.editors.types;

import org.kie.workbench.common.dmn.api.definition.HasTypeRef;
import org.kie.workbench.common.dmn.api.definition.HasValue;

/**
 * Definition of a domain model object that can be edited with a {@link ValueAndDataTypePopoverView}.
 */
public interface HasValueAndTypeRef<V> extends HasValue<V>,
                                               HasTypeRef {

    /**
     * Returns the {@link String} for the {@link ValueAndDataTypePopoverView} title used to to edit properties.
     * @return null if no title is to be shown.
     */
    default String getPopoverTitle() {
        return null;
    }

    /**
     * Converts the value in the UI to the domain model value.
     * @param componentValue
     * @return
     */
    V toModelValue(final String componentValue);

    /**
     * Converts the value in the domain model to the UI value.
     * @param modelValue
     * @return
     */
    String toWidgetValue(final V modelValue);

    /**
     * Returns the {@link String} for the _value_ label in the {@link ValueAndDataTypePopoverView}
     * @return
     */
    String getValueLabel();

    /**
     * Returns a _normalised_ value that can be used to populate the domain model from the value entered by Users.
     * @param componentValue
     * @return
     */
    String normaliseValue(final String componentValue);
}
