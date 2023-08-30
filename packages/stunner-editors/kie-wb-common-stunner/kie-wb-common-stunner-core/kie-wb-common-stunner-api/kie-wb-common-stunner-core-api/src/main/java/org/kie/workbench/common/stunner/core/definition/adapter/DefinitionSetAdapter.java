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

import java.lang.annotation.Annotation;
import java.util.Optional;
import java.util.Set;

import org.kie.workbench.common.stunner.core.factory.graph.ElementFactory;

/**
 * A Definition Set pojo adapter..
 */
public interface DefinitionSetAdapter<T> extends PriorityAdapter {

    /**
     * Returns the definition set's identifier for a given pojo.
     */
    String getId(T pojo);

    /**
     * Returns the definition set's domain for a given pojo.
     */
    String getDomain(T pojo);

    /**
     * Returns the definition set's description for a given pojo.
     */
    String getDescription(T pojo);

    /**
     * Returns the definition set's definitions for a given pojo.
     */
    Set<String> getDefinitions(T pojo);

    /**
     * Returns the definition set's graph class for a given pojo.
     */
    Class<? extends ElementFactory> getGraphFactoryType(T pojo);

    /**
     * Returns the qualifier used for this Definition Set component's implementations, if any.
     * It must return at least <code>javax.enterprise.inject.Default</code>
     * or <code>javax.enterprise.inject.Any</code>.
     */
    Annotation getQualifier(T pojo);

    /**
     * Returns the definition set's node id for SVG generation.
     */
    Optional<String> getSvgNodeId(T pojo);
}
