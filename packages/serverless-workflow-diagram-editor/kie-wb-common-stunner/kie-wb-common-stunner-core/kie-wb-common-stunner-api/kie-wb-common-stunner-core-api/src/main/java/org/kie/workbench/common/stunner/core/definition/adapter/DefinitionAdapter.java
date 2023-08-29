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


package org.kie.workbench.common.stunner.core.definition.adapter;

import java.util.Optional;

import org.kie.workbench.common.stunner.core.definition.property.PropertyMetaTypes;
import org.kie.workbench.common.stunner.core.factory.graph.ElementFactory;

/**
 * A Definition pojo adapter..
 */
public interface DefinitionAdapter<T> extends PriorityAdapter {

    /**
     * Returns the definition's identifier for a given pojo.
     */
    DefinitionId getId(T pojo);

    /**
     * Returns the definition's category for a given pojo.
     */
    String getCategory(T pojo);

    /**
     * Returns the definition's element factory for a given pojo.
     */
    Class<? extends ElementFactory> getElementFactory(T pojo);

    /**
     * Returns the definition's title for a given pojo.
     */
    String getTitle(T pojo);

    /**
     * Returns the definition's description for a given pojo.
     */
    String getDescription(T pojo);

    /**
     * Returns the definition's labels for a given pojo.
     */
    String[] getLabels(T pojo);

    /**
     * Returns the fields which are declared as properties, for a given pojo.
     */
    String[] getPropertyFields(T pojo);

    /**
     * Returns the property instance with the given name.
     *
     * @param pojo  the "bean" definition instance
     * @param field field path relative to the pojo
     * @return
     */
    Optional<?> getProperty(T pojo, String field);

    /**
     * Returns the bean's field for the given meta-property type..
     * Stunner provides some built-in features that could require model updates,
     * so this meta-properties are used for binding these features with the property beans.
     */
    String getMetaPropertyField(T pojo, PropertyMetaTypes metaType);

    /**
     * Returns the definition's graph element factory class for a given pojo.
     */
    Class<? extends ElementFactory> getGraphFactoryType(T pojo);
}
