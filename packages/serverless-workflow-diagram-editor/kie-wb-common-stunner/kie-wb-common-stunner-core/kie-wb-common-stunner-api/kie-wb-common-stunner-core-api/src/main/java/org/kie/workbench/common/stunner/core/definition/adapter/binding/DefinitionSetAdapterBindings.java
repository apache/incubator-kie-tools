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


package org.kie.workbench.common.stunner.core.definition.adapter.binding;

import java.lang.annotation.Annotation;
import java.util.Set;

public class DefinitionSetAdapterBindings {

    private Class<?> graphFactory;
    private Annotation qualifier;
    private Set<String> definitionIds;

    public Class<?> getGraphFactory() {
        return graphFactory;
    }

    public DefinitionSetAdapterBindings setGraphFactory(Class<?> graphFactory) {
        this.graphFactory = graphFactory;
        return this;
    }

    public Annotation getQualifier() {
        return qualifier;
    }

    public DefinitionSetAdapterBindings setQualifier(Annotation qualifier) {
        this.qualifier = qualifier;
        return this;
    }

    public Set<String> getDefinitionIds() {
        return definitionIds;
    }

    public DefinitionSetAdapterBindings setDefinitionIds(Set<String> definitionIds) {
        this.definitionIds = definitionIds;
        return this;
    }
}
