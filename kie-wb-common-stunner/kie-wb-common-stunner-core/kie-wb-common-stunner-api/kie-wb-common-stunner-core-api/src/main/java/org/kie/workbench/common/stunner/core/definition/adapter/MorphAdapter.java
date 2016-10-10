/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
    <T> Iterable<MorphDefinition> getMorphDefinitions( T definition );

    /**
     * Returns the morphing properties for the given Definition instance, if any.
     */
    <T> Iterable<MorphProperty> getMorphProperties( T definition );

    /**
     * Returns the morphing targets for the given Morphing Definition.
     */
    <T> Iterable<String> getTargets( T definition, MorphDefinition morphDefinition );

    /**
     * Performs the morph operation for a diven morph definition and a given target.
     */
    <T> T morph( S source, MorphDefinition definition, String target );

}
