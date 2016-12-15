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

package org.kie.workbench.common.stunner.core.definition.adapter;

import org.kie.workbench.common.stunner.core.factory.graph.ElementFactory;

import java.util.Set;

/**
 * A Definition pojo adapter..
 */
public interface DefinitionAdapter<T> extends PriorityAdapter {

    /**
     * Returns the definition's identifier for a given pojo.
     */
    String getId( T pojo );

    /**
     * Returns the definition's name property for a given pojo.
     * Name is a build-in property supported by different components and widgets, it's a good idea returning your
     * pojo name property instance here so you can use all the different features for it.
     * If no name for the definition, can return null.
     */
    Object getNameProperty( T pojo );

    /**
     * Returns the definition's category for a given pojo.
     */
    String getCategory( T pojo );

    /**
     * Returns the definition's title for a given pojo.
     */
    String getTitle( T pojo );

    /**
     * Returns the definition's description for a given pojo.
     */
    String getDescription( T pojo );

    /**
     * Returns the definition's labels for a given pojo.
     */
    Set<String> getLabels( T pojo );

    /**
     * Returns the definition's property sets for a given pojo.
     */
    Set<?> getPropertySets( T pojo );

    /**
     * Returns all the definition's properties for a given pojo.
     * Must return the properties from the different
     * definition's property sets as well.
     */
    Set<?> getProperties( T pojo );

    /**
     * Returns the definition's graph element factory class for a given pojo.
     */
    Class<? extends ElementFactory> getGraphFactoryType( T pojo );

}
