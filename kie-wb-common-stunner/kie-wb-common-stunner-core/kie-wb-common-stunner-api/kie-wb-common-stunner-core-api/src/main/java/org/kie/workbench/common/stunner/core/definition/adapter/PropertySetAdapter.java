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

import java.util.Set;

/**
 * A Property Set pojo adapter..
 */
public interface PropertySetAdapter<T> extends PriorityAdapter {

    /**
     * Returns the property set's identifier for a given pojo.
     */
    String getId(final T pojo);

    /**
     * Returns the property set's name for a given pojo.
     */
    String getName(final T pojo);

    /**
     * Returns the property set's properties for a given pojo.
     */
    Set<?> getProperties(final T pojo);

    /**
     * Returns property of a given pojo and the property name.
     */
    //todo:tiago return optional
    <P> P getProperty(final T pojo, final String propertyName);
}
