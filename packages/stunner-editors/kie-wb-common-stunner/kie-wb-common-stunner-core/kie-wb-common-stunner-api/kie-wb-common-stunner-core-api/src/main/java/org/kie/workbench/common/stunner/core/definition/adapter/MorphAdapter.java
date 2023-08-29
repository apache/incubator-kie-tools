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

import org.kie.workbench.common.stunner.core.definition.morph.MorphDefinition;
import org.kie.workbench.common.stunner.core.definition.morph.MorphProperty;

/**
 * The morphing adapter.
 */
public interface MorphAdapter<S> extends Adapter {

    /**
     * Returns the morphing definitions for the given Definition instance, if any.
     */
    <T> Iterable<MorphDefinition> getMorphDefinitions(final T definition);

    /**
     * Returns the morphing definitions for the given DefinitionId and BaseId
     */
    Iterable<MorphDefinition> getMorphDefinitions(final String id, final String baseId);

    /**
     * Returns the morphing properties for the given Definition instance, if any.
     */
    <T> Iterable<MorphProperty> getMorphProperties(final T definition);

    /**
     * Returns the morphing targets for the given Morphing Definition.
     */
    <T> Iterable<String> getTargets(final T definition,
                                    final MorphDefinition morphDefinition);

    /**
     * Performs the morph operation for a given morph definition and a given target.
     */
    <T> T morph(final S source,
                final MorphDefinition definition,
                final String target);
}
