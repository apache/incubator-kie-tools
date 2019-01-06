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

package org.kie.workbench.common.stunner.core.factory.impl;

import java.util.Set;

import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionId;
import org.kie.workbench.common.stunner.core.factory.graph.ElementFactory;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;

public abstract class AbstractElementFactory<C, D extends Definition<C>, T extends Element<D>>
        implements ElementFactory<C, D, T> {

    protected AbstractElementFactory() {
    }

    protected abstract DefinitionManager getDefinitionManager();

    protected void appendLabels(final Set<String> target,
                                final Object definition) {
        final DefinitionId id = getDefinitionManager().adapters().forDefinition().getId(definition);
        target.add(id.value());
        if (id.isDynamic()) {
            target.add(id.type());
        }
        target.addAll(getDefinitionManager().adapters().forDefinition().getLabels(definition));
    }
}
