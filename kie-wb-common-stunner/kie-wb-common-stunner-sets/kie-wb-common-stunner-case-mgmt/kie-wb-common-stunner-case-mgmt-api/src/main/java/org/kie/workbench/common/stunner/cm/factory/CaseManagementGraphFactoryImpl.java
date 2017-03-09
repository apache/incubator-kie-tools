/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.cm.factory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.bpmn.factory.BPMNGraphFactoryImpl;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.factory.graph.ElementFactory;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandManager;
import org.kie.workbench.common.stunner.core.graph.command.impl.GraphCommandFactory;
import org.kie.workbench.common.stunner.core.graph.processing.index.GraphIndexBuilder;

/**
 * The custom factory for Case Management graphs.
 */
@ApplicationScoped
public class CaseManagementGraphFactoryImpl
        extends BPMNGraphFactoryImpl implements CaseManagementGraphFactory {

    protected CaseManagementGraphFactoryImpl() {
        super();
    }

    @Inject
    public CaseManagementGraphFactoryImpl(final DefinitionManager definitionManager,
                                          final FactoryManager factoryManager,
                                          final GraphCommandManager graphCommandManager,
                                          final GraphCommandFactory graphCommandFactory,
                                          final GraphIndexBuilder<?> indexBuilder) {
        super(definitionManager,
              factoryManager,
              graphCommandManager,
              graphCommandFactory,
              indexBuilder);
    }

    @Override
    public Class<? extends ElementFactory> getFactoryType() {
        return CaseManagementGraphFactory.class;
    }
}