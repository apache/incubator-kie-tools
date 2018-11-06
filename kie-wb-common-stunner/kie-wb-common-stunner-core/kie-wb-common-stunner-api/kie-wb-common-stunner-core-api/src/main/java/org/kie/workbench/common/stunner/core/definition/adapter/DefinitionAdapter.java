/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

import java.util.Optional;
import java.util.Set;

import org.kie.workbench.common.stunner.core.definition.property.PropertyMetaTypes;
import org.kie.workbench.common.stunner.core.factory.graph.ElementFactory;

/**
 * A Definition pojo adapter..
 */
public interface DefinitionAdapter<T> extends PriorityAdapter {

    /**
     * Returns the definition's identifier for a given pojo.
     */
    String getId(final T pojo);

    /**
     * Returns the definition's category for a given pojo.
     */
    String getCategory(final T pojo);

    /**
     * Returns the definition's title for a given pojo.
     */
    String getTitle(final T pojo);

    /**
     * Returns the definition's description for a given pojo.
     */
    String getDescription(final T pojo);

    /**
     * Returns the definition's labels for a given pojo.
     */
    Set<String> getLabels(final T pojo);

    /**
     * Returns the definition's property sets for a given pojo.
     */
    Set<?> getPropertySets(final T pojo);

    /**
     * Returns all the definition's properties for a given pojo.
     * Must return the properties from the different
     * definition's property sets as well.
     */
    Set<?> getProperties(final T pojo);

    /**
     * Returns the property instance with the given name.
     * @param pojo definition
     * @param propertyName property field name on the class
     * @return
     */
    Optional<?> getProperty(final T pojo, final String propertyName);

    /**
     * Returns the property bean instance for the given meta-property type..
     * Stunner provides some built-in features that could require model updates,
     * so this meta-properties are used for binding these features with the property beans.
     */
    Object getMetaProperty(final PropertyMetaTypes metaType,
                           final T pojo);

    /**
     * Returns the definition's graph element factory class for a given pojo.
     */
    Class<? extends ElementFactory> getGraphFactoryType(final T pojo);

    /**
     * Respective name field with namespace (i.e. attribue1.attribue2.name)
     * @param pojo definition
     * @return the field with namespace if applied
     */
    Optional<String> getNameField(final T pojo);
}
