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

package org.kie.workbench.common.stunner.core.factory.impl;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.factory.graph.ElementFactory;
import org.kie.workbench.common.stunner.core.factory.graph.GraphFactory;

@ApplicationScoped
public class GraphFactoryImpl extends AbstractGraphFactory {

    private final DefinitionManager definitionManager;

    protected GraphFactoryImpl() {
        this(null);
    }

    @Inject
    public GraphFactoryImpl(final DefinitionManager definitionManager) {
        this.definitionManager = definitionManager;
    }

    @Override
    public Class<? extends ElementFactory> getFactoryType() {
        return GraphFactory.class;
    }

    @Override
    public boolean accepts(final String source) {
        return true;
    }

    @Override
    protected DefinitionManager getDefinitionManager() {
        return definitionManager;
    }
}
