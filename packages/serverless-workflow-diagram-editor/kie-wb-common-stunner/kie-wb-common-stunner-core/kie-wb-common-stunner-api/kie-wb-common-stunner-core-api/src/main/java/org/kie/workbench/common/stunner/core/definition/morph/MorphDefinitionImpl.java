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


package org.kie.workbench.common.stunner.core.definition.morph;

import java.util.Collections;
import java.util.List;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.stunner.core.definition.clone.ClonePolicy;

@Portable
public class MorphDefinitionImpl implements MorphDefinition {

    private final String definitionId;
    private final String base;
    private final String defaultDefinitionId;
    private final List<String> targets;
    private final ClonePolicy policy;

    public MorphDefinitionImpl(final @MapsTo("definitionId") String definitionId,
                               final @MapsTo("base") String base,
                               final @MapsTo("defaultDefinitionId") String defaultDefinitionId,
                               final @MapsTo("targets") List<String> targets,
                               final @MapsTo("policy") ClonePolicy policy) {
        this.definitionId = definitionId;
        this.base = base;
        this.defaultDefinitionId = defaultDefinitionId;
        this.targets = targets;
        this.policy = policy;
    }

    @Override
    public boolean accepts(final String definitionId) {
        return this.definitionId.equals(definitionId);
    }

    @Override
    public String getBase() {
        return base;
    }

    @Override
    public String getDefault() {
        return defaultDefinitionId;
    }

    @Override
    public Iterable<String> getTargets(final String definitionId) {
        if (this.definitionId.equals(definitionId)) {
            return targets;
        }
        return Collections.emptyList();
    }

    @Override
    public ClonePolicy getPolicy() {
        return policy;
    }
}
